package xyz.needpainkiller.api.audit;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ua_parser.Parser;
import xyz.needpainkiller.api.audit.dao.AuditLogRepo;
import xyz.needpainkiller.api.authentication.AuthenticationService;
import xyz.needpainkiller.api.authentication.AuthorizationService;
import xyz.needpainkiller.api.user.UserService;
import xyz.needpainkiller.api.user.adapter.out.web.data.UserProfile;
import xyz.needpainkiller.common.model.HttpMethod;

import javax.annotation.Nullable;

@Profile("!kafka")
@Slf4j
@Service
public class FileAuditService {

    private static final String CONTENT_TYPE_JSON = "application/json";
    private static final Parser uaParser = new Parser();
    @Autowired
    private AuditLogRepo auditLogRepo;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private AuthorizationService authorizationService;
    @Autowired
    private UserService userService;

/*    public SearchCollectionResult<AuditLog> selectAuditLog(AuditRequests.SearchAuditLogRequest param) {
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

    @Async("asyncJobExecutor")
    public void insertAuditLog(HttpMethod httpMethod, int statusCode,
                               String requestURI, String requestIp, String userAgent,
                               String requestContentType, String requestPayload,
                               String responseContentType, String responsePayload,
                               @Nullable String token) {


/*        Map<String, Serializable> errorData = new HashMap<>();
        AuditLogEntity auditLog = new AuditLogEntity();
        auditLog.setVisibleYn(true);

        Client client = uaParser.parse(userAgent);
        auditLog.setAgentOs(client.os.family);
        auditLog.setAgentOsVersion(client.os.major);
        auditLog.setAgentBrowser(client.userAgent.family);
        auditLog.setAgentBrowserVersion(client.userAgent.major);
        auditLog.setAgentDevice(client.device.family);

        HttpStatus.Series series = HttpStatus.Series.resolve(statusCode);
        auditLog.setHttpStatus(statusCode);
        auditLog.setHttpMethod(httpMethod);

        try {
            ApiEntity api = authorizationService.selectApiByRequestURI(requestURI, httpMethod);
            if (!api.isRecordYn()) {
                return;
            }

            auditLog.setApiUid(api.getId());
            auditLog.setApiName(api.getDescription());

            MenuEntity menu = authorizationService.selectMenuByApi(api);
            auditLog.setMenuUid(menu.getId());
            auditLog.setMenuName(menu.getName());

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
            auditLog.setRequestIp(requestIp);
            auditLog.setRequestUri(requestURI);
            auditLog.setRequestContentType(requestContentType);
            if (!Strings.isBlank(requestPayload)) {
                auditLog.setRequestPayLoad(requestPayload);
//                auditLog.setRequestPayLoad(CompressHelper.compressString(requestPayload));
            }
            auditLog.setResponseContentType(responseContentType);
            if (!Strings.isBlank(responsePayload)) {
                auditLog.setResponsePayLoad(responsePayload);
//                auditLog.setResponsePayLoad(CompressHelper.compressString(responsePayload));
            }

            if (series != null && (series.equals(HttpStatus.Series.CLIENT_ERROR) || series.equals(HttpStatus.Series.SERVER_ERROR))) {
                errorData = objectMapper.readValue(responsePayload, Map.class);
            }

            if (token != null && !token.isBlank()) {
                Long userPk = authenticationService.getUserPkByToken(token);
                Long tenantPk = authenticationService.getTenantPkByToken(token);
                UserProfile<UserEntity, RoleEntity, TeamEntity> userProfile = userService.selectUserProfile(userPk);
                User user = userProfile.getUser();
                auditLog.setTenantPk(tenantPk);
                auditLog.setUserPk(userPk);
                auditLog.setUserId(user.getUserId());
                auditLog.setUserName(user.getUserName());
                auditLog.setUserEmail(user.getUserEmail());
                Team team = userProfile.getTeam();
                auditLog.setTeamPk(team.getId());
                auditLog.setTeamName(team.getTeamName());
            } else {
                auditLog.setUserPk(null);
                auditLog.setUserId(null);
                auditLog.setUserName(null);
                auditLog.setUserEmail(null);
                auditLog.setTeamPk(null);
                auditLog.setTeamName(null);
            }
        } catch (TokenValidFailedException | UserException e) {
            auditLog.setUserPk(null);
            auditLog.setUserId(null);
            auditLog.setUserName(null);
            auditLog.setUserEmail(null);
            auditLog.setTeamPk(null);
            auditLog.setTeamName(null);
        } catch (JsonProcessingException | IllegalArgumentException e) {
            errorData.put("message", e.getMessage());
            errorData.put("code", e.getClass().getName());
        }
        auditLog.setErrorData(errorData);
        auditLogRepo.save(auditLog);*/
    }

    @Async("asyncJobExecutor")
    public void insertLoginAuditLog(HttpServletRequest request, UserProfile userProfile) {

/*        if (request == null) {
            log.error("insertLoginAuditLog - request is null");
            return;
        }

        Map<String, Serializable> errorData = new HashMap<>();
        AuditLogEntity auditLog = new AuditLogEntity();
        auditLog.setVisibleYn(true);

        HttpMethod httpMethod = HttpMethod.nameOf(request.getMethod());
        String requestContentType = request.getContentType();
        auditLog.setHttpStatus(HttpStatus.OK.value());
        auditLog.setHttpMethod(httpMethod);
        auditLog.setRequestContentType(requestContentType);

        String requestIp = HttpHelper.getClientIP(request);
        String requestURI = request.getRequestURI();
        auditLog.setRequestIp(requestIp);
        auditLog.setRequestUri(requestURI);

        String userAgent = request.getHeader("user-agent");
        Client client = uaParser.parse(userAgent);
        auditLog.setAgentOs(client.os.family);
        auditLog.setAgentOsVersion(client.os.major);
        auditLog.setAgentBrowser(client.userAgent.family);
        auditLog.setAgentBrowserVersion(client.userAgent.major);
        auditLog.setAgentDevice(client.device.family);

        ApiEntity api = authorizationService.selectApiByRequestURI(requestURI, httpMethod);
        if (!api.isRecordYn()) {
            return;
        }

        auditLog.setApiUid(api.getId());
        auditLog.setApiName(api.getDescription());

        MenuEntity menu = authorizationService.selectMenuByApi(api);
        auditLog.setMenuUid(menu.getId());
        auditLog.setMenuName(menu.getName());

        String requestPayload = null;
        boolean methodHasPayload = HttpMethod.hasPayload(httpMethod);
        if (!Strings.isBlank(requestContentType)) {
            if (methodHasPayload && requestContentType.startsWith(CONTENT_TYPE_JSON)) {
                requestPayload = HttpHelper.getRequestPayload(request);
            }
        }
        auditLog.setRequestPayLoad(requestPayload);
        auditLog.setResponsePayLoad(null);

        if (userProfile != null) {
            Long userPk = userProfile.getUserPk();
            Long tenantPk = userProfile.getTenantPk();
            UserEntity user = (UserEntity) userProfile.getUser();
            auditLog.setTenantPk(tenantPk);
            auditLog.setUserPk(userPk);
            auditLog.setUserId(user.getUserId());
            auditLog.setUserName(user.getUserName());
            auditLog.setUserEmail(user.getUserEmail());
            Team team = userProfile.getTeam();
            auditLog.setTeamPk(team.getId());
            auditLog.setTeamName(team.getTeamName());
        } else {
            auditLog.setUserPk(null);
            auditLog.setUserId(null);
            auditLog.setUserName(null);
            auditLog.setUserEmail(null);
            auditLog.setTeamPk(null);
            auditLog.setTeamName(null);
        }
        auditLog.setErrorData(errorData);
        auditLogRepo.save(auditLog);*/
    }
}

