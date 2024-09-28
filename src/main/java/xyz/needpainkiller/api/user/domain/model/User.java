package xyz.needpainkiller.api.user.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.*;
import xyz.needpainkiller.api.tenant.domain.model.TenantBase;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Map;
import java.util.Objects;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(NON_NULL)
@JsonIgnoreProperties(value = {"hibernate_lazy_initializer", "handler"}, ignoreUnknown = true)
@Cacheable
public class User implements Serializable, TenantBase {

    public final static Long SYSTEM_USER = 1L;

    @Serial
    private static final long serialVersionUID = -5024262939443796113L;

    private Long id;
    private Long tenantPk;
    private boolean useYn;
    private String userId;
    private String userEmail;
    private String userName;
    private String userPwd;
    private UserStatusType userStatus;
    private Long teamPk;
    private Long createdBy;
    private Timestamp createdDate;
    private Long updatedBy;
    private Timestamp updatedDate;
    private Integer loginFailedCnt;
    private Timestamp lastLoginDate;
    private Map<String, Serializable> data;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User that = (User) o;
        return useYn == that.useYn && Objects.equals(id, that.id) && Objects.equals(tenantPk, that.tenantPk) && Objects.equals(userId, that.userId) && Objects.equals(userEmail, that.userEmail) && Objects.equals(userName, that.userName) && Objects.equals(userPwd, that.userPwd) && userStatus == that.userStatus && Objects.equals(teamPk, that.teamPk) && Objects.equals(createdBy, that.createdBy) && Objects.equals(createdDate, that.createdDate) && Objects.equals(updatedBy, that.updatedBy) && Objects.equals(updatedDate, that.updatedDate) && Objects.equals(loginFailedCnt, that.loginFailedCnt) && Objects.equals(lastLoginDate, that.lastLoginDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, tenantPk, useYn, userId, userEmail, userName, userPwd, userStatus, teamPk, createdBy, createdDate, updatedBy, updatedDate, loginFailedCnt, lastLoginDate);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", tenantPk=" + tenantPk +
                ", useYn=" + useYn +
                ", userId='" + userId + '\'' +
                ", userEmail='" + userEmail + '\'' +
                ", userName='" + userName + '\'' +
                ", userStatus=" + userStatus +
                ", teamPk=" + teamPk +
                ", createdBy=" + createdBy +
                ", createdDate=" + createdDate +
                ", updatedBy=" + updatedBy +
                ", updatedDate=" + updatedDate +
                ", loginFailedCnt=" + loginFailedCnt +
                ", lastLoginDate=" + lastLoginDate +
                ", data=" + data +
                '}';
    }


    @JsonIgnore
    public boolean isLoginEnabled() {
        return this.useYn && this.userStatus.isLoginable();
    }

    @JsonIgnore
    public boolean isAvailable() {
        return this.useYn;
    }

    @Transient
    @JsonIgnore
    // password is not exposed to the client
    public String getUserPwd() {
        return null;
    }

    @Transient
    @JsonIgnore
    // password for SecurityUser
    public final String pwd() {
        return this.userPwd;
    }
}
