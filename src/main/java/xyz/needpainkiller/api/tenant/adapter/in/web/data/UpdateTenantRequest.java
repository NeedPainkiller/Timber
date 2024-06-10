package xyz.needpainkiller.api.tenant.adapter.in.web.data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema
public class UpdateTenantRequest implements UpsertTenantRequest, Serializable {
    @Serial
    private static final long serialVersionUID = -3449786007747414238L;
    @NotNull
    @Schema(description = "공개 여부", example = "true / false", required = true)
    private Boolean visibleYn = true;
    @NotNull
    @Schema(description = "기본 테넌트 여부", example = "true / false", required = true)
    private Boolean defaultYn = false;
    @NotBlank
    @Schema(description = "회사 이름", example = "A회사", required = true)
    private String title;
    @NotBlank
    @Schema(description = "라벨명", example = "A회사 라벨", required = true)
    private String label;
    @NotBlank
    @Schema(description = "회사 URL", example = "http://needpainkiller.co.kr", required = true)
    private String url;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UpdateTenantRequest that = (UpdateTenantRequest) o;

        if (!Objects.equals(visibleYn, that.visibleYn)) return false;
        if (!Objects.equals(defaultYn, that.defaultYn)) return false;
        if (!Objects.equals(title, that.title)) return false;
        if (!Objects.equals(label, that.label)) return false;
        return Objects.equals(url, that.url);
    }

    @Override
    public int hashCode() {
        int result = visibleYn != null ? visibleYn.hashCode() : 0;
        result = 31 * result + (defaultYn != null ? defaultYn.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (label != null ? label.hashCode() : 0);
        result = 31 * result + (url != null ? url.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("UpdateTenantRequest{");
        sb.append("visibleYn=").append(visibleYn);
        sb.append(", defaultYn=").append(defaultYn);
        sb.append(", title='").append(title).append('\'');
        sb.append(", label='").append(label).append('\'');
        sb.append(", url='").append(url).append('\'');
        sb.append('}');
        return sb.toString();
    }
}