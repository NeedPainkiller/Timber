package xyz.needpainkiller.api.audit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ua_parser.Client;
import xyz.needpainkiller.api.audit.model.AuditLogMessage;
import xyz.needpainkiller.api.authentication.AuthenticationService;
import xyz.needpainkiller.api.authentication.AuthorizationService;
import xyz.needpainkiller.api.authentication.error.ApiException;
import xyz.needpainkiller.api.authentication.model.Api;
import xyz.needpainkiller.api.authentication.model.Menu;
import xyz.needpainkiller.api.team.model.Team;
import xyz.needpainkiller.api.user.UserService;
import xyz.needpainkiller.api.user_hex.adapter.out.web.data.UserProfile;
import xyz.needpainkiller.api.user_hex.domain.error.UserException;
import xyz.needpainkiller.api.user_hex.domain.model.User;
import xyz.needpainkiller.common.model.HttpMethod;
import xyz.needpainkiller.helper.HttpHelper;
import xyz.needpainkiller.helper.TimeHelper;
import xyz.needpainkiller.lib.exceptions.ApiErrorResponse;
import xyz.needpainkiller.lib.security.error.TokenValidFailedException;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Profile("kafka")
@Slf4j
@Service
public class KafkaAuditService implements AuditService {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private AuthorizationService authorizationService;
    @Autowired
    private UserService userService;


    @Autowired
    private KafkaTemplate<Object, Object> template;


/*    @Override
    public SearchCollectionResult<AuditLog> selectAuditLog(AuditRequests.SearchAuditLogRequest param) {
        if (!Strings.isBlank(param.getRequestIp())) {
            param.setRequestIpNum(Inets.aton(param.getRequestIp()));
        }

        Specification<AuditLog> specification = Specification.where(AuditLogSpecification.search(param));
        Page<AuditLog> auditLogPage = auditLogRepo.findAll(specification, param.pageOf());
        List<AuditLog> auditLogList = auditLogPage.getContent().stream().map(log -> {
            String requestPayload = log.getRequestPayLoad();
            String responsePayload = log.getResponsePayLoad();
            if (!Strings.isBlank(requestPayload)) {
                log.setRequestPayLoad(CompressHelper.decompressString(requestPayload));
            }
            if (!Strings.isBlank(responsePayload)) {
                log.setResponsePayLoad(CompressHelper.decompressString(responsePayload));
            }
            return log;
        }).toList();
        long total = auditLogPage.getTotalElements();
        return SearchCollectionResult.<AuditLog>builder().collection(auditLogList).foundRows(total).build();
    }*/

    @Override
    @Async("asyncJobExecutor")
    public void insertAuditLog(HttpMethod httpMethod, int statusCode,
                               String requestURI, String requestIp, String userAgent,
                               String requestContentType, String requestPayload,
                               String responseContentType, String responsePayload,
                               @Nullable String token) {

        Map<String, Serializable> errorData = new HashMap<>();
        AuditLogMessage auditLogMessage = new AuditLogMessage();
        auditLogMessage.setVisibleYn(true);

        Client client = uaParser.parse(userAgent);
        auditLogMessage.setAgentOs(client.os.family);
        auditLogMessage.setAgentOsVersion(client.os.major);
        auditLogMessage.setAgentBrowser(client.userAgent.family);
        auditLogMessage.setAgentBrowserVersion(client.userAgent.major);
        auditLogMessage.setAgentDevice(client.device.family);

        HttpStatus.Series series = HttpStatus.Series.resolve(statusCode);
        auditLogMessage.setHttpStatus(statusCode);
        auditLogMessage.setHttpMethod(httpMethod);

        try {
            Api api = authorizationService.selectApiByRequestURI(requestURI, httpMethod);
//            if (!api.isRecordYn()) {
//                return;
//            }

            auditLogMessage.setApiUid(api.getId());
            auditLogMessage.setApiName(api.getDescription());

            Menu menu = authorizationService.selectMenuByApi(api);
            auditLogMessage.setMenuUid(menu.getId());
            auditLogMessage.setMenuName(menu.getName());

        } catch (ApiException e) {
            log.info("ApiException : {}", e.getMessage());
            ApiErrorResponse apiErrorResponse = ApiErrorResponse.of(e.getErrorCode(), e);
            errorData.put("message", apiErrorResponse.getMessage());
            errorData.put("status", apiErrorResponse.getStatus());
            errorData.put("code", apiErrorResponse.getCode());
            errorData.put("cause", apiErrorResponse.getCause());
        } catch (IllegalArgumentException e) {
            log.info("IllegalArgumentException : {}", e.getMessage());
            errorData.put("message", e.getMessage());
            errorData.put("code", e.getClass().getName());
        }

        try {
            auditLogMessage.setRequestIp(requestIp);
            auditLogMessage.setRequestUri(requestURI);
            auditLogMessage.setRequestContentType(requestContentType);
            if (!Strings.isBlank(requestPayload)) {
                auditLogMessage.setRequestPayLoad(requestPayload);
//                auditLog.setRequestPayLoad(CompressHelper.compressString(requestPayload));
            }
            auditLogMessage.setResponseContentType(responseContentType);
            if (!Strings.isBlank(responsePayload)) {
                auditLogMessage.setResponsePayLoad(responsePayload);
//                auditLog.setResponsePayLoad(CompressHelper.compressString(responsePayload));
            }
            auditLogMessage.setCreatedDate(TimeHelper.now());

            if (series != null && (series.equals(HttpStatus.Series.CLIENT_ERROR) || series.equals(HttpStatus.Series.SERVER_ERROR))) {
                errorData = objectMapper.readValue(responsePayload, Map.class);
            }

            if (token != null && !token.isBlank()) {
                Long userPk = authenticationService.getUserPkByToken(token);
                Long tenantPk = authenticationService.getTenantPkByToken(token);
                UserProfile userProfile = userService.selectUserProfile(userPk);
                User user = userProfile.getUser();
                auditLogMessage.setTenantPk(tenantPk);
                auditLogMessage.setUserPk(userPk);
                auditLogMessage.setUserId(user.getUserId());
                auditLogMessage.setUserName(user.getUserName());
                auditLogMessage.setUserEmail(user.getUserEmail());
                Team team = userProfile.getTeam();
                auditLogMessage.setTeamPk(team.getId());
                auditLogMessage.setTeamName(team.getTeamName());
            } else {
                auditLogMessage.setUserPk(null);
                auditLogMessage.setUserId(null);
                auditLogMessage.setUserName(null);
                auditLogMessage.setUserEmail(null);
                auditLogMessage.setTeamPk(null);
                auditLogMessage.setTeamName(null);
            }
        } catch (TokenValidFailedException | UserException e) {
            auditLogMessage.setUserPk(null);
            auditLogMessage.setUserId(null);
            auditLogMessage.setUserName(null);
            auditLogMessage.setUserEmail(null);
            auditLogMessage.setTeamPk(null);
            auditLogMessage.setTeamName(null);
        } catch (JsonProcessingException | IllegalArgumentException e) {
            errorData.put("message", e.getMessage());
            errorData.put("code", e.getClass().getName());
        }
        auditLogMessage.setErrorData(errorData);


        template.send("timber__topic-audit-api", auditLogMessage);
    }

    @Override
    @Async("asyncJobExecutor")
    public void insertLoginAuditLog(HttpServletRequest request, UserProfile userProfile) {

        log.info("userProfile : {}", userProfile);
        if (request == null) {
            log.error("insertLoginAuditLog - request is null");
            return;
        }

        Map<String, Serializable> errorData = new HashMap<>();
        AuditLogMessage auditLogMessage = new AuditLogMessage();
        auditLogMessage.setVisibleYn(true);

        HttpMethod httpMethod = HttpMethod.nameOf(request.getMethod());
        String requestContentType = request.getContentType();
        auditLogMessage.setHttpStatus(HttpStatus.OK.value());
        auditLogMessage.setHttpMethod(httpMethod);
        auditLogMessage.setRequestContentType(requestContentType);

        String requestIp = HttpHelper.getClientIP(request);
        String requestURI = request.getRequestURI();
        auditLogMessage.setRequestIp(requestIp);
        auditLogMessage.setRequestUri(requestURI);

        String userAgent = request.getHeader("user-agent");
        Client client = uaParser.parse(userAgent);
        auditLogMessage.setAgentOs(client.os.family);
        auditLogMessage.setAgentOsVersion(client.os.major);
        auditLogMessage.setAgentBrowser(client.userAgent.family);
        auditLogMessage.setAgentBrowserVersion(client.userAgent.major);
        auditLogMessage.setAgentDevice(client.device.family);

        Api api = authorizationService.selectApiByRequestURI(requestURI, httpMethod);
        if (!api.isRecordYn()) {
            return;
        }

        auditLogMessage.setApiUid(api.getId());
        auditLogMessage.setApiName(api.getDescription());

        Menu menu = authorizationService.selectMenuByApi(api);
        auditLogMessage.setMenuUid(menu.getId());
        auditLogMessage.setMenuName(menu.getName());

        String requestPayload = null;
        boolean methodHasPayload = HttpMethod.hasPayload(httpMethod);
        if (!Strings.isBlank(requestContentType)) {
            if (methodHasPayload && requestContentType.startsWith(CONTENT_TYPE_JSON)) {
                requestPayload = HttpHelper.getRequestPayload(request);
            }
        }
        auditLogMessage.setRequestPayLoad(requestPayload);
        auditLogMessage.setResponsePayLoad(null);

        auditLogMessage.setCreatedDate(TimeHelper.now());

        if (userProfile != null) {
            Long userPk = userProfile.getUserPk();
            Long tenantPk = userProfile.getTenantPk();
            User user = userProfile.getUser();
            auditLogMessage.setTenantPk(tenantPk);
            auditLogMessage.setUserPk(userPk);
            auditLogMessage.setUserId(user.getUserId());
            auditLogMessage.setUserName(user.getUserName());
            auditLogMessage.setUserEmail(user.getUserEmail());
            Team team = userProfile.getTeam();
            auditLogMessage.setTeamPk(team.getId());
            auditLogMessage.setTeamName(team.getTeamName());
        } else {
            auditLogMessage.setUserPk(null);
            auditLogMessage.setUserId(null);
            auditLogMessage.setUserName(null);
            auditLogMessage.setUserEmail(null);
            auditLogMessage.setTeamPk(null);
            auditLogMessage.setTeamName(null);
        }
        auditLogMessage.setErrorData(errorData);

        template.send("timber__topic-audit-login", auditLogMessage);
    }
}

