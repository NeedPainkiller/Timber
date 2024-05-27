package xyz.needpainkiller.tenant.adapter.in.web.data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import xyz.needpainkiller.lib.validation.NonSpecialCharacter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema
public class CreateTenantRequest implements UpsertTenantRequest, Serializable {
    @Serial
    private static final long serialVersionUID = 1273927602993910119L;
    @NotNull
    @Schema(description = "노출 여부", example = "true / false", required = true)
    private Boolean visibleYn = true;
    private final Boolean defaultYn = false;
    @NotBlank
    @Schema(description = "테넌트 이름", example = "A회사", required = true)
    private String title;
    @NotBlank
    @Schema(description = "라벨명", example = "A회사 라벨", required = true)
    private String label;
    @NotBlank
    @Schema(description = "회사 URL", example = "http://needpainkiller.co.kr", required = true)
    private String url;

    @NotBlank
    @NonSpecialCharacter
    @Schema(description = "유저 ID", example = "user01", required = true)
    private String userId;

    @NotBlank
    @NonSpecialCharacter
    @Schema(description = "유저 이름", example = "user01", required = true)
    private String userName;

    @Schema(description = "패스워드(10자 이상, 1개 이상의 숫자 혹은 패스워드)", example = "password!", required = true)
    private String userPwd;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CreateTenantRequest that = (CreateTenantRequest) o;

        if (!Objects.equals(visibleYn, that.visibleYn)) return false;
        if (!defaultYn.equals(that.defaultYn)) return false;
        if (!Objects.equals(title, that.title)) return false;
        if (!Objects.equals(label, that.label)) return false;
        if (!Objects.equals(url, that.url)) return false;
        if (!Objects.equals(userId, that.userId)) return false;
        if (!Objects.equals(userName, that.userName)) return false;
        return Objects.equals(userPwd, that.userPwd);
    }

    @Override
    public int hashCode() {
        int result = visibleYn != null ? visibleYn.hashCode() : 0;
        result = 31 * result + defaultYn.hashCode();
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (label != null ? label.hashCode() : 0);
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (userName != null ? userName.hashCode() : 0);
        result = 31 * result + (userPwd != null ? userPwd.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CreateTenantRequest{");
        sb.append("visibleYn=").append(visibleYn);
        sb.append(", defaultYn=").append(defaultYn);
        sb.append(", title='").append(title).append('\'');
        sb.append(", label='").append(label).append('\'');
        sb.append(", url='").append(url).append('\'');
        sb.append(", userId='").append(userId).append('\'');
        sb.append(", userName='").append(userName).append('\'');
        sb.append(", userPwd='").append(userPwd).append('\'');
        sb.append('}');
        return sb.toString();
    }
}