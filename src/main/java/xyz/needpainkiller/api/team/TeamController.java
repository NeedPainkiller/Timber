package xyz.needpainkiller.api.team;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import xyz.needpainkiller.api.authentication.AuthenticationService;
import xyz.needpainkiller.api.team.dto.TeamRequests;
import xyz.needpainkiller.api.team.model.Team;
import xyz.needpainkiller.api.tenant.domain.error.TenantException;
import xyz.needpainkiller.api.user_hex.domain.model.User;
import xyz.needpainkiller.common.controller.CommonController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.http.ResponseEntity.status;
import static xyz.needpainkiller.api.tenant.domain.error.TenantErrorCode.TENANT_CONFLICT;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TeamController extends CommonController implements TeamApi {
    @Autowired
    protected AuthenticationService authenticationService;
    @Autowired
    private TeamService teamService;

    @Override
    public ResponseEntity<Map<String, Object>> selectTeamList(HttpServletRequest request) {
        Map<String, Object> model = new HashMap<>();
        Long tenantPk = authenticationService.getTenantPkByToken(request);
        List<Team> teamList = teamService.selectTeamList(tenantPk);
        model.put(KEY_LIST, teamList);
        model.put(KEY_TOTAL, teamList.size());
        return ok(model);
    }

    @Override
    public ResponseEntity<Map<String, Object>> selectTeam(Long teamPk, HttpServletRequest request) {
        Map<String, Object> model = new HashMap<>();
        Long tenantPk = authenticationService.getTenantPkByToken(request);
        Team team = teamService.selectTeam(teamPk);
        if (!team.getTenantPk().equals(tenantPk)) {
            throw new TenantException(TENANT_CONFLICT);
        }
        model.put(KEY_TEAM, team);
        return ok(model);
    }

    @Override

    public ResponseEntity<Map<String, Object>> createTeam(TeamRequests.UpsertTeamRequest param, HttpServletRequest request) {
        Map<String, Object> model = new HashMap<>();
        User requester = authenticationService.getUserByToken(request);
        Long tenantPk = authenticationService.getTenantPkByToken(request);
        param.setTenantPk(tenantPk);
        Team team = teamService.createTeam(param, requester);
        model.put(KEY_TEAM, team);
        return ok(model);
    }

    @Override

    public ResponseEntity<Map<String, Object>> updateTeam(Long teamPk, TeamRequests.UpsertTeamRequest param, HttpServletRequest request) {
        Map<String, Object> model = new HashMap<>();
        User requester = authenticationService.getUserByToken(request);
        Long tenantPk = authenticationService.getTenantPkByToken(request);
        param.setTenantPk(tenantPk);
        Team team = teamService.updateTeam(teamPk, param, requester);
        model.put(KEY_TEAM, team);
        return ok(model);
    }

    @Override

    public ResponseEntity<Map<String, Object>> deleteTeam(Long teamPk, HttpServletRequest request) {
        Map<String, Object> model = new HashMap<>();
        User requester = authenticationService.getUserByToken(request);
        Long tenantPk = authenticationService.getTenantPkByToken(request);
        teamService.deleteTeam(teamPk, requester, tenantPk);

        return status(HttpStatus.NO_CONTENT).body(model);
    }

    @Override
    public ResponseEntity<Map<String, Object>> createTeamBulk(TeamRequests.BulkTeamListRequest param, HttpServletRequest request) {
        Map<String, Object> model = new HashMap<>();
        User requester = authenticationService.getUserByToken(request);
        List<Team> teamList = teamService.createTeamBulk(param, requester);
        model.put(KEY_LIST, teamList);
        model.put(KEY_TOTAL, teamList.size());
        return ok(model);
    }
}
