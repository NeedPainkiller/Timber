package xyz.needpainkiller.api.user_hex.adapter.out.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import xyz.needpainkiller.api.tenant.domain.model.TenantBase;
import xyz.needpainkiller.api.user_hex.domain.model.UserStatusType;
import xyz.needpainkiller.lib.jpa.BooleanConverter;
import xyz.needpainkiller.lib.jpa.JsonToMapConverter;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Map;
import java.util.Objects;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(NON_NULL)
@Entity

@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicUpdate
@Table(name = "ACCOUNT_USER")
public class UserEntity implements Serializable, TenantBase {

    public final static Long SYSTEM_USER = 1L;

    @Serial
    private static final long serialVersionUID = -8737333234871506911L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "_ID", unique = true, nullable = false, columnDefinition = "bigint")
    private Long id;

    @Column(name = "TENANT_PK", nullable = false, columnDefinition = "bigint default 0")
    @Getter
    private Long tenantPk;

    @Convert(converter = BooleanConverter.class)
    @Column(name = "USE_YN", nullable = false, columnDefinition = "tinyint unsigned default 0")
    @Getter
    private boolean useYn;

    @Column(name = "USER_ID", nullable = false, columnDefinition = "nvarchar(256)")
    @Getter
    private String userId;

    @Column(name = "USER_EMAIL", nullable = true, columnDefinition = "nvarchar(256) default null")
    @Getter
    private String userEmail;

    @Column(name = "USER_NAME", nullable = false, columnDefinition = "nvarchar(256)")
    @Getter
    private String userName;

    @Column(name = "USER_PWD", nullable = false, columnDefinition = "nvarchar(1024)")
    private String userPwd;

    @Convert(converter = UserStatusType.Converter.class)
    @Column(name = "USER_STATUS", nullable = false, columnDefinition = "int unsigned default 0")
    @Getter
    private UserStatusType userStatus;

    @Column(name = "TEAM_PK", nullable = false, columnDefinition = "bigint default 0")
    @Getter
    private Long teamPk;

    @Column(name = "CREATED_BY", nullable = false, columnDefinition = "bigint default 0")
    @Getter
    private Long createdBy;

    @Column(name = "CREATED_DATE", nullable = false, columnDefinition = "datetime2(0) default CURRENT_TIMESTAMP")
    @Getter
    @CreationTimestamp
    private Timestamp createdDate;

    @Column(name = "UPDATED_BY", nullable = false, columnDefinition = "bigint default 0")
    @Getter
    private Long updatedBy;

    @Column(name = "UPDATED_DATE", nullable = false, columnDefinition = "datetime2(0) default CURRENT_TIMESTAMP")
    @Getter
    private Timestamp updatedDate;

    @Column(name = "LOGIN_FAILED_CNT", nullable = false, columnDefinition = "int unsigned default 0")
    @Getter
    private Integer loginFailedCnt;

    @Column(name = "LAST_LOGIN_DATE", nullable = true, columnDefinition = "datetime2(0) default null")
    @Getter
    private Timestamp lastLoginDate;

    @Convert(converter = JsonToMapConverter.class)
    @Column(name = "EXTRA_DATA", nullable = true, columnDefinition = "longtext")
    @Getter
    private Map<String, Serializable> data;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserEntity that = (UserEntity) o;
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
