package xyz.needpainkiller.api.audit.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import xyz.needpainkiller.common.model.HttpMethod;
import xyz.needpainkiller.lib.JpaPaginationDirection;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuditRequests {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema
    public static class SearchAuditLogRequest implements Serializable, JpaPaginationDirection {
        @Serial
        private static final long serialVersionUID = -2942141871675334546L;

        @JsonIgnore
        private Long tenantPk;
        @Schema(description = "HTTP 응답코드", example = "200")
        private Integer httpStatus;
        @Schema(description = "HTTP 요청 메서드", example = "POST")
        private HttpMethod httpMethod;
        @Schema(description = "요청 URI", example = "/api/auth/login")
        private String requestUri;
        @Schema(description = "요청 REMOTE IP (v4)", example = "192.168.0.1")
        private String requestIp;

        @JsonIgnore
        private Long requestIpNum;
        @Schema(description = "요청 BROWSER", example = "Chrome")
        private String agentBrowser;

        @Schema(description = "유저 ID", example = "user01")
        private String userId;
        @Schema(description = "유저 이메일", example = "needpainkiller6512@google.com")
        private String userEmail;
        @Schema(description = "유저 이름", example = "user01")
        private String userName;
        @Schema(description = "부서 이름", example = "회사")
        private String teamName;
        @Schema(description = "메뉴 이름", example = "DASHBOARD")
        private String menuName;
        @Schema(description = "API 이름", example = "로그인")
        private String apiName;

        @Schema(description = "시작 일", example = "1635724800000")
        private Timestamp startDate;
        @Schema(description = "종료 일", example = "1635724800000")
        private Timestamp endDate;

        @Schema(description = "정렬 기준", example = "ID")
        private String orderBy;

        public String getOrderBy() {
            return "id";
        }

        @Schema(description = "정렬 순서", example = "ASC / DESC")
        private String orderDirection;
        @JsonIgnore
        Boolean isPagination = true;
        @Min(0)
        @Schema(description = "Pagination - Index", example = "0")
        private Integer startId;
        @Max(100)
        @Schema(description = "Pagination - limit", example = "5")
        private Integer itemCnt;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SearchAuditLogRequest that = (SearchAuditLogRequest) o;
            return Objects.equals(tenantPk, that.tenantPk) && Objects.equals(httpStatus, that.httpStatus) && httpMethod == that.httpMethod && Objects.equals(requestUri, that.requestUri) && Objects.equals(requestIp, that.requestIp) && Objects.equals(requestIpNum, that.requestIpNum) && Objects.equals(agentBrowser, that.agentBrowser) && Objects.equals(userId, that.userId) && Objects.equals(userEmail, that.userEmail) && Objects.equals(userName, that.userName) && Objects.equals(teamName, that.teamName) && Objects.equals(menuName, that.menuName) && Objects.equals(apiName, that.apiName) && Objects.equals(startDate, that.startDate) && Objects.equals(endDate, that.endDate) && Objects.equals(orderBy, that.orderBy) && Objects.equals(orderDirection, that.orderDirection) && Objects.equals(isPagination, that.isPagination) && Objects.equals(startId, that.startId) && Objects.equals(itemCnt, that.itemCnt);
        }

        @Override
        public int hashCode() {
            return Objects.hash(tenantPk, httpStatus, httpMethod, requestUri, requestIp,
                    requestIpNum, agentBrowser, userId, userEmail, userName, teamName,
                    menuName, apiName, startDate, endDate,
                    orderBy, orderDirection, isPagination, startId, itemCnt);
        }
    }
}
