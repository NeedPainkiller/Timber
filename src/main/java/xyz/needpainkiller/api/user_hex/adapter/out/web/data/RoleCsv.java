package xyz.needpainkiller.api.user_hex.adapter.out.web.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import xyz.needpainkiller.api.user_hex.domain.model.Role;
import xyz.needpainkiller.helper.TimeHelper;

import java.io.Serial;
import java.io.Serializable;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RoleCsv implements Serializable {

    @Serial
    private static final long serialVersionUID = 1852715471801193355L;
    @JsonProperty(value = "계정권한", index = 1)
    private String roleNm;
    @JsonProperty(value = "구분", index = 2)
    private String isAdmin;
    @JsonProperty(value = "설명", index = 3)
    private String roleDescription;
    @JsonProperty(value = "최근 수정일시", index = 4)
    private String updatedDate;
    @JsonProperty(value = "최초 등록일시", index = 5)
    private String createdDate;

    public RoleCsv(Role role) {
        this.roleNm = role.getRoleName();

        if (role.isAdmin()) {
            this.isAdmin = "관리자";
        } else {
            this.isAdmin = "사용자";
        }

        this.roleDescription = role.getRoleDescription();
        this.updatedDate = TimeHelper.fromTimestampToString(role.getUpdatedDate());
        this.createdDate = TimeHelper.fromTimestampToString(role.getCreatedDate());
    }
}
