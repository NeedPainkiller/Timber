package xyz.needpainkiller.api.user_hex.adapter.out.web.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import xyz.needpainkiller.api.team.model.Team;
import xyz.needpainkiller.api.user_hex.domain.model.Role;
import xyz.needpainkiller.api.user_hex.domain.model.User;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonInclude(NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserProfile implements Serializable {
    @Serial
    private static final long serialVersionUID = 3025308819403131315L;
    private Long tenantPk;
    private Long userPk;
    private User user;
    private Team team;
    private List<Role> roleList;

    public UserProfile(User user, Team team, List<Role> roleList) {
        this.user = user;
        this.tenantPk = user.getTenantPk();
        this.userPk = user.getId();
        this.team = team;
        this.roleList = roleList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserProfile that = (UserProfile) o;
        return Objects.equals(tenantPk, that.tenantPk) && Objects.equals(userPk, that.userPk) && Objects.equals(user, that.user) && Objects.equals(team, that.team) && Objects.equals(roleList, that.roleList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tenantPk, userPk, user, team, roleList);
    }
}