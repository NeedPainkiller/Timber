package xyz.needpainkiller.api.team;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.needpainkiller.api.team.dao.TeamRepo;
import xyz.needpainkiller.api.team.dto.TeamRequests;
import xyz.needpainkiller.api.team.dto.TeamRequests.BulkTeamListRequest;
import xyz.needpainkiller.api.team.dto.TeamRequests.BulkTeamListRequest.BulkTeamRequest;
import xyz.needpainkiller.api.team.error.TeamException;
import xyz.needpainkiller.api.team.model.Team;
import xyz.needpainkiller.api.team.model.TeamLevel;
import xyz.needpainkiller.api.tenant.domain.error.TenantException;
import xyz.needpainkiller.api.user_hex.domain.model.User;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static xyz.needpainkiller.api.team.error.TeamErrorCode.*;
import static xyz.needpainkiller.api.tenant.domain.error.TenantErrorCode.TENANT_CONFLICT;

@Slf4j
@Service
public class TeamService {
    private static final Predicate<Team> predicateAvailableTeam = Team::isUseYn;
    private static final Comparator<Team> comparator = Comparator.comparingLong(Team::getOrder);
    @Autowired
    private TeamRepo teamRepo;


    //    @Cacheable(value = "TeamList", key = "'selectTeamList-' + #tenantPk")

    public List<Team> selectTeamList(Long tenantPk) {
        return teamRepo.findAll().stream()
                .filter(predicateAvailableTeam)
                .filter(team -> team.filterByTenant(tenantPk))
                .sorted(comparator).toList();

    }

    //    @Cacheable(value = "TeamList", key = "'selectTeamList'")

    public List<Team> selectTeamList() {
        return teamRepo.findAll().stream().filter(predicateAvailableTeam).sorted(comparator).toList();
    }


    public Map<Long, Team> selectTeamMap() {
        return teamRepo.findAll().stream().filter(predicateAvailableTeam).collect(Collectors.toMap(Team::getId, t -> t));
    }

    //    @Cacheable(value = "Team", key = "'selectTeam-' + #teamPk", unless = "#result == null")

    public Team selectTeam(Long teamPk) {
        Optional<Team> team = teamRepo.findById(teamPk);
        if (team.isEmpty()) {
            throw new TeamException(TEAM_NOT_EXIST);
        }
        return team.get();
    }


    public List<Team> selectFlattenTeam(Long teamPk) {
        List<Team> teamList = selectTeamList();
        Map<Long, Team> teamMap = teamList.stream().collect(Collectors.toMap(Team::getId, t -> t));
        Team team = selectTeam(teamPk);
        if (team == null) {
            throw new TeamException(TEAM_NOT_EXIST);
        }
        List<Team> flattenTeamList = Lists.newArrayList(team);
        Long parentTeamPk = team.getParentTeamPk();
        while (parentTeamPk != null) {
            Team parentTeam = teamMap.get(parentTeamPk);
            flattenTeamList.add(parentTeam);
            parentTeamPk = parentTeam.getParentTeamPk();
        }
        Collections.reverse(flattenTeamList);
        return flattenTeamList;
    }



    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "Team", allEntries = true),
            @CacheEvict(value = "TeamList", allEntries = true)
    })
    public Team createTeam(TeamRequests.UpsertTeamRequest param, User requester) {
        validateParentTeam(param);
        Long requesterPk = requester.getId();

        Team team = new Team();
        team.setTenantPk(param.getTenantPk());
        team.setUseYn(true);
        team.setVisibleYn(true);
        team.setTeamName(param.getTeamName());
        team.setCreatedBy(requesterPk);
        team.setUpdatedBy(requesterPk);
        team.setOrder(param.getOrder());
        team.setTeamLevel(param.getTeamLevel());
        team.setParentTeamPk(param.getParentTeamPk());
        team = teamRepo.save(team);
        updateAllTeamPath();
        return team;
    }


    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "Team", allEntries = true),
            @CacheEvict(value = "TeamList", allEntries = true)
    })
    public Team updateTeam(Long teamPk, TeamRequests.UpsertTeamRequest param, User requester) {
        validateParentTeam(param);
        Long tenantPk = param.getTenantPk();
        Team team = selectTeam(teamPk);
        if (!team.isUseYn()) {
            throw new TeamException(TEAM_DELETED);
        }
        if (!team.getTenantPk().equals(tenantPk)) {
            throw new TenantException(TENANT_CONFLICT);
        }
        if (teamPk.equals(param.getParentTeamPk())) {
            throw new TeamException(TEAM_PARENT_CIRCULAR_REFERENCES);
        }
        if (hasChildrenTeam(teamPk)) {
            throw new TeamException(TEAM_CHILDREN_EXIST);
        }

        team.setUseYn(true);
        team.setVisibleYn(true);
        team.setTeamName(param.getTeamName());
        team.setUpdatedBy(requester.getId());
        team.setOrder(param.getOrder());
        team.setTeamLevel(param.getTeamLevel());
        team.setParentTeamPk(param.getParentTeamPk());
        team = teamRepo.save(team);
        updateAllTeamPath();
        return team;
    }

    public void validateParentTeam(TeamRequests.UpsertTeamRequest param) {
        TeamLevel teamLevel = param.getTeamLevel();
        if (teamLevel.equals(TeamLevel.ROOT)) {
            return;
        }
        Long parentTeamPk = param.getParentTeamPk();
        if (parentTeamPk == null) {
            throw new TeamException(TEAM_PARENT_REQUEST_EMPTY);
        }
        Optional<Team> parentTeamOpt = teamRepo.findById(parentTeamPk).filter(TeamService.predicateAvailableTeam);
        if (parentTeamOpt.isEmpty()) {
            throw new TeamException(TEAM_PARENT_NOT_EXIST);
        }
        Team parentTeam = parentTeamOpt.get();
        if (!parentTeam.isUseYn()) {
            throw new TeamException(TEAM_DELETED);
        }
        Long tenantPk = param.getTenantPk();
        if (!parentTeam.getTenantPk().equals(tenantPk)) {
            throw new TenantException(TENANT_CONFLICT);
        }
    }


    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "Team", allEntries = true),
            @CacheEvict(value = "TeamList", allEntries = true)
    })
    public void deleteTeam(Long teamPk, User requester, Long tenantPk) {
        Long requesterPk = requester.getId();
        Team team = selectTeam(teamPk);
        if (!team.isUseYn()) {
            throw new TeamException(TEAM_DELETED);
        }
        if (!team.getTenantPk().equals(tenantPk)) {
            throw new TenantException(TENANT_CONFLICT);
        }
        if (hasChildrenTeam(teamPk)) {
            throw new TeamException(TEAM_CHILDREN_EXIST);
        }

        team.setTeamName(team.getTeamName() + "-" + UUID.randomUUID());
        team.setUseYn(false);
        team.setUpdatedBy(requesterPk);
        teamRepo.save(team);
        updateAllTeamPath();
    }

    public boolean hasChildrenTeam(Long teamPk) {
        return !teamRepo.findByParentTeamPk(teamPk).stream().filter(Team::isUseYn).toList().isEmpty();
    }


    public void updateAllTeamPath() {
        List<Team> teamList = selectTeamList();
        Map<Long, Team> teamMap = teamList.stream().collect(Collectors.toMap(Team::getId, t -> t));

        for (Team team : teamList) {
            List<Team> breadcrumb = Lists.newArrayList(team);

            Long parentPk = team.getParentTeamPk();
            Team parentTeam = teamMap.get(parentPk);
            while (parentTeam != null) {
                breadcrumb.add(parentTeam);
                parentPk = parentTeam.getParentTeamPk();
                parentTeam = teamMap.get(parentPk);
            }

            List<String> teamNmList = breadcrumb.stream().map(Team::getTeamName).collect(Collectors.toList());
            Collections.reverse(teamNmList);
            String teamPath = String.join(" > ", teamNmList);
            team.setTeamPath(teamPath);
            teamRepo.save(team);
        }
    }



    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "Team", allEntries = true),
            @CacheEvict(value = "TeamList", allEntries = true)
    })
    public List<Team> createTeamBulk(BulkTeamListRequest param, User requester) {
        List<BulkTeamRequest> teamRequestList = param.getTeamRequestList();

        Long requesterPk = requester.getId();
        Long tenantPk = requester.getTenantPk();

        List<Team> teamList = Lists.newArrayList();

        int bulkSize = teamRequestList.size();
        for (int i = 0; i < bulkSize; i++) {
            BulkTeamRequest bulkTeamRequest = teamRequestList.get(i);
            Long teamId = bulkTeamRequest.getId();
            List<BulkTeamRequest> childTeamRequestList = teamRequestList.stream()
                    .filter(t -> t.getParentTeamPk() != null)
                    .filter(t -> t.getParentTeamPk().equals(teamId)).toList();

            Team team = new Team();
            team.setTenantPk(tenantPk);
            team.setUseYn(true);
            team.setVisibleYn(true);
            team.setTeamName(bulkTeamRequest.getTeamName());
            team.setCreatedBy(requesterPk);
            team.setUpdatedBy(requesterPk);
            team.setOrder(i);
            team.setTeamLevel(bulkTeamRequest.getTeamLevel());
            team.setParentTeamPk(bulkTeamRequest.getParentTeamPk());
            team = teamRepo.save(team);
            Long teamPk = team.getId();
            childTeamRequestList.forEach(t -> t.setParentTeamPk(teamPk));
            teamList.add(team);
        }
        updateAllTeamPath();
        return teamList;
    }
}

