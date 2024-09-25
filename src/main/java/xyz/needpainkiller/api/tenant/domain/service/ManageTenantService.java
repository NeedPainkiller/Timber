package xyz.needpainkiller.api.tenant.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.transaction.annotation.Transactional;
import xyz.needpainkiller.api.tenant.adapter.in.web.data.CreateTenantRequest;
import xyz.needpainkiller.api.tenant.adapter.in.web.data.UpdateTenantRequest;
import xyz.needpainkiller.api.tenant.adapter.in.web.data.UpsertTenantRequest;
import xyz.needpainkiller.api.tenant.application.port.in.ManageTenantUseCase;
import xyz.needpainkiller.api.tenant.application.port.out.TenantEventPublisher;
import xyz.needpainkiller.api.tenant.application.port.out.TenantOutputPort;
import xyz.needpainkiller.api.tenant.domain.error.TenantException;
import xyz.needpainkiller.api.tenant.domain.model.Tenant;
import xyz.needpainkiller.api.user_hex.domain.model.User;

import java.util.List;
import java.util.regex.Pattern;

import static xyz.needpainkiller.api.tenant.domain.error.TenantErrorCode.*;


@Slf4j
@RequiredArgsConstructor
public class ManageTenantService implements ManageTenantUseCase {
    private static final String HTTP_REGEX = "\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
    private static final Pattern HTTP_PATTERN = Pattern.compile(HTTP_REGEX);

    private final TenantOutputPort tenantOutputPort;

    private final TenantEventPublisher tenantEventPublisher;

//    private final UserService userService;
//
//    private final RoleService roleService;
//
//    private final TeamService teamService;


    private static void checkTenantParam(UpsertTenantRequest param) {
        String title = param.getTitle();
        if (title == null || title.isBlank()) {
            throw new TenantException(TENANT_TITLE_BLANK);
        }
        String url = param.getUrl();
        if (url == null || url.isBlank()) {
            throw new TenantException(TENANT_SERVER_URL_BLANK);
        }
        if (!ManageTenantService.HTTP_PATTERN.matcher(url).matches()) {
            throw new TenantException(TENANT_SERVER_URL_PATTERN_NOT_MATCH);
        }
    }

    private void checkDuplicateTenant(Tenant updateTenant, UpsertTenantRequest param) {
        String title = param.getTitle().trim();
        String url = param.getUrl().trim();

        List<Tenant> tenantList = tenantOutputPort.selectTenantList().stream().filter(Tenant.predicateAvailableTenant).toList();
        List<Tenant> sameServerUrlTenantLists = tenantList.stream().filter(t -> t.getUrl().equals(url)).toList();
        if (sameServerUrlTenantLists.isEmpty()) {
            return;
        }
        List<Tenant> sameCompanyTenantLists = tenantList.stream().filter(t -> t.getTitle().equals(title)).toList();

        ManageTenantService.log.info("sameCompanyTenantLists : {}", sameCompanyTenantLists);
        ManageTenantService.log.info("sameServerUrlTenantLists : {}", sameServerUrlTenantLists);

        boolean hasSameCompany = false;
        boolean hasSameServerUrl = false;
        if (updateTenant == null) { // 신규 tenant 등록
            ManageTenantService.log.info("create Tenant");
            hasSameCompany = !sameCompanyTenantLists.isEmpty();
            hasSameServerUrl = !sameServerUrlTenantLists.isEmpty();
        } else { // 기존 tenant 업데이트
            ManageTenantService.log.info("update Tenant");
            hasSameCompany = sameCompanyTenantLists.stream().anyMatch(tenant -> !updateTenant.getId().equals(tenant.getId()));
            hasSameServerUrl = sameServerUrlTenantLists.stream().anyMatch(tenant -> !updateTenant.getId().equals(tenant.getId()));
        }
        if (hasSameCompany) {
            throw new TenantException(TENANT_TITLE_ALREADY_EXIST);
        }
        if (hasSameServerUrl) {
            throw new TenantException(TENANT_SERVER_URL_ALREADY_EXIST);
        }
    }


    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "Tenant", allEntries = true),
            @CacheEvict(value = "TenantList", allEntries = true)
    })
    public Tenant createTenant(CreateTenantRequest param, User requester) throws TenantException {
        checkTenantParam(param);
        checkDuplicateTenant(null, param);

        Long requesterPk = requester.getId();

        Tenant tenant = new Tenant();
        tenant.setDefaultYn(false);
        tenant.setUseYn(true);
        tenant.setVisibleYn(param.getVisibleYn());

        tenant.setTitle(param.getTitle().trim());
        tenant.setLabel(param.getLabel().trim());
        tenant.setUrl(param.getUrl().trim());

        tenant.setCreatedBy(requesterPk);
        tenant.setUpdatedBy(requesterPk);

        tenant = tenantOutputPort.create(tenant);
//        createTenantBootstrapSet(tenant, param, requester);
        tenantEventPublisher.publishCreateEvent(tenant);
        return tenant;
    }


    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "Tenant", allEntries = true),
            @CacheEvict(value = "TenantList", allEntries = true)
    })

    public Tenant updateTenant(Long tenantPk, UpdateTenantRequest param, User requester) {
        Tenant tenant = tenantOutputPort.selectTenantAvailable(tenantPk)
                .orElseThrow(() -> new TenantException(TENANT_NOT_EXIST));

        checkTenantParam(param);
        checkDuplicateTenant(tenant, param);

        if (!tenant.isUseYn()) {
            throw new TenantException(TENANT_DELETED);
        }

        Long requesterPk = requester.getId();
        tenant.setDefaultYn(false);
        tenant.setUseYn(true);
        tenant.setVisibleYn(param.getVisibleYn());

        tenant.setTitle(param.getTitle().trim());
        tenant.setLabel(param.getLabel().trim());
        tenant.setUrl(param.getUrl().trim());

        tenant.setCreatedBy(requesterPk);
        tenant.setUpdatedBy(requesterPk);

        tenant = tenantOutputPort.update(tenant);
        tenantEventPublisher.publishUpdateEvent(tenant);
        return tenant;
    }


    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "Tenant", allEntries = true),
            @CacheEvict(value = "TenantList", allEntries = true)
    })
    public void deleteTenant(Long tenantPk, User requester) {
        Long requesterPk = requester.getId();
        Tenant tenant = tenantOutputPort.selectTenantAvailable(tenantPk)
                .orElseThrow(() -> new TenantException(TENANT_NOT_EXIST));
        tenant.setUseYn(false);
        tenant.setVisibleYn(false);
        tenant.setUpdatedBy(requesterPk);
        tenant = tenantOutputPort.delete(tenant);
        tenantEventPublisher.publishDeleteEvent(tenant);
    }

/*    public void createTenantBootstrapSet(Tenant tenant, CreateTenantRequest param, User requester) {
        Long tenantPk = tenant.getId();
        TeamRequests.UpsertTeamRequest teamParam = new TeamRequests.UpsertTeamRequest();
        teamParam.setTenantPk(tenantPk);
        teamParam.setTeamName("ADMIN TEAM");
        Team team = teamService.createTeam(teamParam, requester);
        Long teamPk = team.getId();

        TenantBootstrapRequests.CreateTenantRoleRequest roleParam = new TenantBootstrapRequests.CreateTenantRoleRequest();
        roleParam.setTenantPk(tenantPk);
        Role role = roleService.createRole(roleParam, requester);
        List<Role> roleList = new ArrayList<>();
        roleList.add(role);

        UserRequests.UpsertUserRequest userParam = new UserRequests.UpsertUserRequest();
        userParam.setTenantPk(tenantPk);
        userParam.setTeamPk(teamPk);
        String label = tenant.getLabel();
        String userId = param.getUserId();
        String email = userId + "@" + label;
        String userName = param.getUserName();
        String password = param.getUserPwd();
        userParam.setUserId(userId);
        userParam.setUserEmail(email);
        userParam.setUserName(userName);
        userParam.setUserPwd(password);
        userService.createUser(userParam, roleList, requester);
    }*/


    @Transactional
    public void bootstrap() {
/*        TenantEntity defaultTenant = selectDefatultTenant();
        if (defaultTenant != null) {
            return;
        }

        TenantEntity tenant = new TenantEntity();
        tenant.setDefaultYn(true);
        tenant.setUseYn(true);
        tenant.setVisibleYn(true);

        tenant.setTitle("default");
        tenant.setLabel("default");
        tenant.setUrl("http://localhost:8080");

        tenant.setCreatedBy(1L);
        tenant.setUpdatedBy(1L);
        tenant = tenantRepo.save(tenant);
        createTenantBootstrapSet(tenant, param, requester);*/
    }
}

