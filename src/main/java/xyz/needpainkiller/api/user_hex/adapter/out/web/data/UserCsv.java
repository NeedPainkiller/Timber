package xyz.needpainkiller.api.user_hex.adapter.out.web.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import xyz.needpainkiller.api.team.model.Team;
import xyz.needpainkiller.api.user.dto.UserProfile;
import xyz.needpainkiller.api.user_hex.domain.model.Role;
import xyz.needpainkiller.api.user_hex.domain.model.User;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserCsv implements Serializable {

    @Serial
    private static final long serialVersionUID = 3326879598583766314L;
    @JsonProperty(value = "번호", index = 1)
    private Long userPk;

    @JsonProperty(value = "구분", index = 2)
    private String isAdmin;
    @JsonProperty(value = "아이디", index = 3)
    private String userId;

    @JsonProperty(value = "이름", index = 4)
    private String userNm;
    @JsonProperty(value = "부서", index = 6)
    private String teamNm;
    @JsonProperty(value = "권한", index = 7)
    private String roleName;
    @JsonProperty(value = "사용여부", index = 8)
    private String userStatusType;


    public UserCsv(UserProfile userProfile) {
        User user = userProfile.getUser();
        Team team = userProfile.getTeam();
        List<Role> roleList = userProfile.getRoleList();
        this.userPk = user.getId();
        this.isAdmin = roleList.stream().anyMatch(Role::isAdmin) ? "관리자" : "일반";
        this.userId = user.getUserId();
        this.userNm = user.getUserName();
        this.teamNm = team.getTeamName();
        this.roleName = roleList.stream().filter(role -> !role.isAdmin()).map(Role::getRoleName).collect(Collectors.joining(","));
        this.userStatusType = user.getUserStatus().getLabel();

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserCsv userCsv = (UserCsv) o;
        return Objects.equals(userPk, userCsv.userPk) && Objects.equals(isAdmin, userCsv.isAdmin) && Objects.equals(userId, userCsv.userId) && Objects.equals(userNm, userCsv.userNm) && Objects.equals(teamNm, userCsv.teamNm) && Objects.equals(roleName, userCsv.roleName) && Objects.equals(userStatusType, userCsv.userStatusType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userPk, isAdmin, userId, userNm, teamNm, roleName, userStatusType);
    }
}
