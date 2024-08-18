package xyz.needpainkiller.api.user_hex.adapter.in.web.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import xyz.needpainkiller.common.dto.DateType;
import xyz.needpainkiller.lib.JpaPaginationDirection;
import xyz.needpainkiller.lib.validation.NonSpecialCharacter;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RoleRequests {

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema
    public static class SearchRoleRequest implements Serializable, JpaPaginationDirection {
        @Serial
        private static final long serialVersionUID = -6925694744733658519L;
        @JsonIgnore
        private Long tenantPk;
        @Schema(description = "검색기간 (등록일시 / 수정일시)", example = "CREATE / UPDATE")
        private DateType dateType;
        @Schema(description = "시작 일", example = "1635724800000")
        private Timestamp startDate;
        @Schema(description = "종료 일", example = "1635724800000")
        private Timestamp endDate;
        @Schema(description = "검색어 구분", example = "ALL  / ROLE_NM / ROLE_DESCRIPTION")
        private String searchBy;
        @Schema(description = "검색어 값", example = "이름")
        private String searchValue;

        @JsonIgnore
        Boolean isPagination = true;
        @Schema(description = "Pagination - Index", example = "0")
        private Integer startId;
        @Schema(description = "Pagination - limit", example = "5")
        private Integer itemCnt;
        @Schema(description = "정렬 기준", example = "ID / ROLE_NAME / ROLE_DESCRIPTION / IS_REGISTRABLE / CREATED_DATE / UPDATED_DATE")
        private String orderBy;
        @Schema(description = "정렬 순서", example = "ASC / DESC")
        private String orderDirection;

        public String getOrderBy() {
            if (this.orderBy == null || this.orderBy.isEmpty()) {
                return "id";
            }
            return switch (this.orderBy) {
                case "ID" -> "id";
                case "ROLE_NM" -> "roleName";
                case "ROLE_DESCRIPTION" -> "roleDescription";
                case "CREATED_DATE" -> "createdDate";
                case "UPDATED_DATE" -> "updatedDate";
                default -> "id";
            };
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema
    public static class UpsertRoleRequest implements Serializable {
        @Serial
        private static final long serialVersionUID = 229468397812619345L;
        @JsonIgnore
        private Long tenantPk;
        @NotBlank
        @NonSpecialCharacter
        @Schema(description = "권한명(Spring Security 의 Authority 구분 값이므로 unique 해야함)", example = "administrator", required = true)
        private String name;
        @NotBlank
        @Schema(description = "권한 설명", example = "user01", required = true)
        private String description;
        @NotNull
        @Schema(description = "관리자 권한 여부", example = "true", required = true)
        private Boolean isAdmin;
        @NotNull
        @NotEmpty
        @Schema(description = "API ID 리스트", example = "[1000,1001]", required = true)
        private List<Long> apiList;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;
            UpsertRoleRequest that = (UpsertRoleRequest) o;
            return Objects.equals(name, that.name) && Objects.equals(description, that.description) && Objects.equals(isAdmin, that.isAdmin) && Objects.equals(apiList, that.apiList);
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), name, description, isAdmin, apiList);
        }
    }
}
