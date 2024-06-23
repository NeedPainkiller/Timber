package xyz.needpainkiller.api.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import xyz.needpainkiller.api.user_hex.domain.model.UserStatusType;
import xyz.needpainkiller.common.dto.DateType;
import xyz.needpainkiller.lib.JpaPaginationDirection;
import xyz.needpainkiller.lib.validation.NonSpecialCharacter;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserRequests {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema
    public static class SearchUserRequest implements Serializable, JpaPaginationDirection {
        @Serial
        private static final long serialVersionUID = 3582847744409503905L;
        @JsonIgnore
        private Long tenantPk;
        @Schema(description = "검색기간 (등록 일시 / 수정일시 / 최종로그인 일시)", example = "CREATE / UPDATE / LOGIN")
        private DateType dateType;
        @Schema(description = "시작 일", example = "1635724800000")
        private Timestamp startDate;
        @Schema(description = "종료 일", example = "1635724800000")
        private Timestamp endDate;
        @NotNull
        @Schema(description = "부서 PK", example = "1")
        private Long teamPk;
        @NotNull
        @Schema(description = "권한 PK", example = "1")
        private Long rolePk;
        @Schema(description = "검색어 구분 (닉네임, 아이디, 팀)", example = "USER_NM / USER_ID / TEAM_NM")
        private String searchBy;
        @Schema(description = "검색어 값", example = "타이틀 명 등")
        private String searchValue;
        @JsonIgnore
        Boolean isPagination = true;
        @Schema(description = "Pagination - Index", example = "0")
        private Integer startId;
        @Schema(description = "Pagination - limit", example = "5")
        private Integer itemCnt;
        @Schema(description = "정렬 기준", example = "ID / USER_ID / USER_NM /  TEAM_NM / USE_YN")
        private String orderBy;
        @Schema(description = "정렬 순서", example = "ASC / DESC")
        private String orderDirection;

        public String getOrderBy() {
            if (this.orderBy == null || this.orderBy.isEmpty()) {
                return "id";
            }
            return switch (this.orderBy) {
                case "ID" -> "id";
                case "USER_ID" -> "userId";
                case "USER_NAME" -> "userName";
                case "USER_STATUS" -> "userStatus";
                case "LOGIN" -> "lastLoginDate";
                case "CREATED_DATE" -> "createdDate";
                case "UPDATED_DATE" -> "updatedDate";
                default -> "id";
            };
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SearchUserRequest that = (SearchUserRequest) o;
            return Objects.equals(dateType, that.dateType) && Objects.equals(startDate, that.startDate) && Objects.equals(endDate, that.endDate) && Objects.equals(teamPk, that.teamPk) && Objects.equals(searchBy, that.searchBy) && Objects.equals(searchValue, that.searchValue) && Objects.equals(startId, that.startId) && Objects.equals(itemCnt, that.itemCnt) && Objects.equals(orderBy, that.orderBy) && Objects.equals(orderDirection, that.orderDirection);
        }

        @Override
        public int hashCode() {
            return Objects.hash(dateType, startDate, endDate, teamPk, searchBy, searchValue, startId, itemCnt, orderBy, orderDirection);
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema
    public static class UpsertUserRequest implements Serializable {
        @Serial
        private static final long serialVersionUID = -6003636573743622877L;

        @JsonIgnore
        private Long tenantPk;

        @NotBlank
        @NonSpecialCharacter
        @Schema(description = "유저 ID", example = "user01", required = true)
        private String userId;

        @NotBlank
        @Schema(description = "유저 이메일", example = "user01@test.com")
        private String userEmail;

        @NotBlank
        @NonSpecialCharacter
        @Schema(description = "유저 이름", example = "user01", required = true)
        private String userName;

        @Schema(description = "패스워드(10자 이상, 1개 이상의 숫자 혹은 패스워드)", example = "password!", required = true)
        private String userPwd;

        @NotNull
        @Schema(description = "계정 상태", example = "OK / LOCKED / NOT_USED / WITHDRAWAL / OK_SSO", required = true)
        private UserStatusType userStatusType = UserStatusType.OK;

        @NotNull
        @Schema(description = "부서 PK", example = "1", required = true)
        private Long teamPk;

        @NotEmpty
        @Schema(description = "권한 PK", example = "[1, 3]", required = true)
        private List<Long> roles;

        @Schema(description = "유저 데이터", example = "{\"test\" : \"sample\"}", required = true)
        private Map<String, Serializable> data;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            UpsertUserRequest that = (UpsertUserRequest) o;
            return Objects.equals(userId, that.userId) && Objects.equals(userEmail, that.userEmail) && Objects.equals(userName, that.userName) && Objects.equals(userPwd, that.userPwd) && userStatusType == that.userStatusType && Objects.equals(teamPk, that.teamPk) && Objects.equals(roles, that.roles) && Objects.equals(data, that.data);
        }

        @Override
        public int hashCode() {
            return Objects.hash(userId, userEmail, userName, userPwd, userStatusType, teamPk, roles, data);
        }
    }
}
