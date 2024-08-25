package xyz.needpainkiller.api.audit;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.scheduling.annotation.Async;
import ua_parser.Parser;
import xyz.needpainkiller.api.user_hex.adapter.out.web.data.UserProfile;
import xyz.needpainkiller.common.model.HttpMethod;

import javax.annotation.Nullable;


public interface AuditService {
    String CONTENT_TYPE_JSON = "application/json";
    Parser uaParser = new Parser();

//    SearchCollectionResult<AuditLog> selectAuditLog(AuditRequests.SearchAuditLogRequest param);

    @Async("asyncJobExecutor")
    public void insertAuditLog(HttpMethod httpMethod, int statusCode,
                               String requestURI, String requestIp, String userAgent,
                               String requestContentType, String requestPayload,
                               String responseContentType, String responsePayload,
                               @Nullable String token);

    @Async("asyncJobExecutor")
    public void insertLoginAuditLog(HttpServletRequest request, UserProfile userProfile);
}

