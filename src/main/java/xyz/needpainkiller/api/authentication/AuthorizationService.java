package xyz.needpainkiller.api.authentication;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.AntPathMatcher;
import xyz.needpainkiller.api.authentication.dao.ApiRepo;
import xyz.needpainkiller.api.authentication.dao.ApiRoleMapRepo;
import xyz.needpainkiller.api.authentication.dao.DivisionRepo;
import xyz.needpainkiller.api.authentication.dao.MenuRepo;
import xyz.needpainkiller.api.authentication.error.ApiException;
import xyz.needpainkiller.api.authentication.model.Api;
import xyz.needpainkiller.api.authentication.model.ApiRoleMap;
import xyz.needpainkiller.api.authentication.model.Division;
import xyz.needpainkiller.api.authentication.model.Menu;
import xyz.needpainkiller.api.user.adapter.out.persistence.repository.RoleRepo;
import xyz.needpainkiller.api.user.domain.error.RoleException;
import xyz.needpainkiller.api.user.domain.model.Role;
import xyz.needpainkiller.common.model.HttpMethod;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static xyz.needpainkiller.lib.exceptions.CommonErrorCode.*;

@Slf4j
@Service
public class AuthorizationService {
    private static final AntPathMatcher apiAntPathMatcher = new AntPathMatcher();
    private static final Comparator<Division> divisionSort = Comparator.comparingInt(Division::getOrder);
    private static final Comparator<Menu> menuSort = Comparator.comparingInt(Menu::getOrder);
    private static final Comparator<Api> apiSort = Comparator.comparingLong(Api::getId);
    @Autowired
    private DivisionRepo divisionRepo;
    @Autowired
    private MenuRepo menuRepo;
    @Autowired
    private ApiRepo apiRepo;
    @Autowired
    private ApiRoleMapRepo apiRoleMapRepo;
    @Autowired
    private RoleRepo roleRepo;


    @Cacheable(value = "Api", key = "'selectApi-' + #p0")
    public Api selectApi(Long apiPk) {
        return apiRepo.findById(apiPk).orElseThrow(() -> new ApiException(API_NOT_FOUND));
    }


    public Api selectApiByRequestURI(String requestURI, HttpMethod httpMethod) {
        return selectAvailableApiList().stream()
                .filter(api -> apiAntPathMatcher.match(api.getUrl(), requestURI))
                .filter(api -> api.getHttpMethod().equals(httpMethod))
                .findFirst().orElseThrow(() -> new ApiException(API_NOT_FOUND));
    }


    @Cacheable(value = "ApiList", key = "'selectAvailableApiList'")
    public List<Api> selectAvailableApiList() {
        return apiRepo.findAll().stream().filter(Api::isAvailableApi).sorted(apiSort).toList();
    }


    @Cacheable(value = "ApiList", key = "'selectVisibleApiList'")
    public List<Api> selectVisibleApiList() {
        return apiRepo.findAll().stream().filter(Api::isVisibleApi).sorted(apiSort).toList();
    }


    @Cacheable(value = "ApiList", key = "'selectPrimaryApiList'")
    public List<Api> selectPrimaryApiList() {
        return apiRepo.findAll().stream().filter(Api::isPrimaryApi).sorted(apiSort).toList();
    }


    @Cacheable(value = "ApiList", key = "'selectPublicApiList'")
    public List<Api> selectPublicApiList() {
        return apiRepo.findAll().stream().filter(Api::isPublicApi).sorted(apiSort).toList();
    }


    @Cacheable(value = "ApiList", key = "'selectNonPublicApiList'")
    public List<Api> selectNonPublicApiList() {
        return apiRepo.findAll().stream().filter(Api::isNonPublicApi).sorted(apiSort).toList();
    }


    @Cacheable(value = "ApiList", key = "'selectApiListByRole-' + #p0.getId()")
    public List<Api> selectApiListByRole(Role role) {
        return selectApiListByRolePk(role.getId());
    }


    @Cacheable(value = "ApiList", key = "'selectApiListByRolePk-' + #p0")
    public List<Api> selectApiListByRolePk(Long rolePk) {
        List<Long> apiPkList = apiRoleMapRepo.findByRolePk(rolePk).stream().map(ApiRoleMap::getApiPk).distinct().toList();
        return apiRepo.findByIdIn(apiPkList).stream().filter(Api::isAvailableApi).sorted(apiSort).toList();
    }


    @Cacheable(value = "ApiList", key = "'selectApiListByRoleList-' + #p0.hashCode()")
    public List<Api> selectApiListByRoleList(List<Role> roleList) {
        List<Long> rolePkList = roleList.stream().map(Role::getId).toList();
        return selectApiListByRolePkList(rolePkList);
    }


    @Cacheable(value = "ApiList", key = "'selectApiListByRolePkList-' + #p0.hashCode()")
    public List<Api> selectApiListByRolePkList(List<Long> rolePkList) {
        rolePkList = rolePkList.stream().distinct().toList();
        List<Long> apiPkList = apiRoleMapRepo.findByRolePkIn(rolePkList).stream().map(ApiRoleMap::getApiPk).distinct().toList();
        return apiRepo.findByIdIn(apiPkList).stream().filter(Api::isAvailableApi).sorted(apiSort).toList();
    }


    @Cacheable(value = "ApiRoleList", key = "'selectRoleListByApiPk-' + #p0")
    public List<Role> selectRoleListByApiPk(Long apiPk) {
        List<Long> rolePkList = apiRoleMapRepo.findByApiPk(apiPk).stream().map(ApiRoleMap::getRolePk).distinct().toList();
        return roleRepo.findByIdIn(rolePkList).stream().filter(Role::isAvailable).toList();
    }


    @Cacheable(value = "ApiRoleList", key = "'selectRoleListByApiPkList-' + #p0.hashCode()")
    public List<Role> selectRoleListByApiPkList(List<Long> apiPkList) {
        apiPkList = apiPkList.stream().distinct().toList();
        List<Long> rolePkList = apiRoleMapRepo.findByApiPkIn(apiPkList).stream().map(ApiRoleMap::getRolePk).distinct().toList();
        return roleRepo.findByIdIn(rolePkList).stream().filter(Role::isAvailable).toList();
    }


    @Cacheable(value = "ApiList", key = "'selectApiListByMenuPk-' + #p0")
    public List<Api> selectApiListByMenuPk(Long menuPk) {
        return apiRepo.findByMenuPk(menuPk).stream().filter(Api::isAvailableApi).toList();
    }


    @Cacheable(value = "ApiList", key = "'selectApiListByMenuPkList-' + #p0.hashCode()")
    public List<Api> selectApiListByMenuPkList(List<Long> menuPkList) {
        menuPkList = menuPkList.stream().distinct().toList();
        return apiRepo.findByMenuPkIn(menuPkList).stream().filter(Api::isAvailableApi).toList();
    }

    //    @Cacheable(value = "Menu", key = "#api.getId()")

    public Menu selectMenuByApi(Api api) {
        return menuRepo.findById(api.getMenuPk()).orElseThrow(() -> new ApiException(MENU_NOT_FOUND));
    }


    @Cacheable(value = "MenuList", key = "'selectAvailableMenuList'")
    public List<Menu> selectAvailableMenuList() {
        return menuRepo.findAll().stream().filter(Menu::isAvailableMenu).sorted(menuSort).toList();
    }


    @Cacheable(value = "MenuList", key = "'selectAvailableMenuListByApiList-' + #p0.hashCode()")
    public List<Menu> selectAvailableMenuListByApiList(List<Api> apiList) {
        List<Long> apiMenuPkList = apiList.stream().filter(Api::isPrimaryApi).map(Api::getMenuPk).distinct().toList();
        return menuRepo.findAllByIdIn(apiMenuPkList).stream().filter(Menu::isAvailableMenu).sorted(menuSort).toList();
    }


    @Modifying(flushAutomatically = true)
    @Transactional()
    @Caching(evict = {
            @CacheEvict(value = "Api", allEntries = true),
            @CacheEvict(value = "ApiList", allEntries = true),
            @CacheEvict(value = "ApiRoleList", allEntries = true),
            @CacheEvict(value = "Menu", allEntries = true),
            @CacheEvict(value = "MenuList", allEntries = true),
            @CacheEvict(value = "DivisionList", allEntries = true)
    })
    public void upsertApiRole(Role role, List<Long> apiPkList) {
        if (!role.isEditable()) {
            throw new RoleException(ROLE_CAN_NOT_EDITABLE);
        }
        Long rolePk = role.getId();
        if (apiPkList.isEmpty()) {
            throw new ApiException(API_REQUEST_EMPTY);
        }
        List<Long> filteredApiPkList = cleanUpApiPkList(apiPkList);

        List<ApiRoleMap> deleteApiRoleMapList = apiRoleMapRepo.findByRolePk(rolePk);
        deleteApiRoleMapList.forEach(apiRoleMap -> {
            apiRoleMapRepo.delete(apiRoleMap);
        });

        filteredApiPkList.forEach(apiPk -> {
            ApiRoleMap apiRoleMap = new ApiRoleMap();
            apiRoleMap.setApiPk(apiPk);
            apiRoleMap.setRolePk(rolePk);
            apiRoleMapRepo.save(apiRoleMap);
        });
    }

    public List<Long> cleanUpApiPkList(List<Long> apiPkList) {
        List<Api> apiList = selectAvailableApiList().stream().filter(api -> apiPkList.contains(api.getId())).toList();
        List<Api> primaryApiList = selectPrimaryApiList().stream().filter(api -> apiPkList.contains(api.getId())).toList();
        List<Menu> menuList = selectAvailableMenuListByApiList(primaryApiList);
        List<Long> menuPkList = menuList.stream().map(Menu::getId).toList();
        List<Long> apiPkListByMenu = selectApiListByMenuPkList(menuPkList).stream().map(Api::getId).toList();
        return apiList.stream().map(Api::getId).filter(apiPkListByMenu::contains).toList();
    }



    @Cacheable(value = "DivisionList", key = "'selectAvailableDivision'")
    public List<Division> selectAvailableDivision() {
        List<Api> apiList = selectAvailableApiList();
        return selectDivisionByApiList(apiList);
    }


    @Cacheable(value = "DivisionList", key = "'selectDivisionByRole-' + #p0.getId()")
    public List<Division> selectDivisionByRole(Role role) {
        List<Api> apiList = selectApiListByRolePk(role.getId());
        return selectDivisionByApiList(apiList);
    }


    @Cacheable(value = "DivisionList", key = "'selectDivisionByRolePk-' + #p0")
    public List<Division> selectDivisionByRolePk(Long rolePk) {
        List<Api> apiList = selectApiListByRolePk(rolePk);
        return selectDivisionByApiList(apiList);
    }


    @Cacheable(value = "DivisionList", key = "'selectDivisionByRoleList-' + #p0.hashCode()")
    public List<Division> selectDivisionByRoleList(List<Role> roleList) {
        List<Long> rolePkList = roleList.stream().map(Role::getId).toList();
        List<Api> apiList = selectApiListByRolePkList(rolePkList);
        return selectDivisionByApiList(apiList);
    }


    @Cacheable(value = "DivisionList", key = "'selectDivisionByRolePkList-' + #p0.hashCode()")
    public List<Division> selectDivisionByRolePkList(List<Long> rolePkList) {
        List<Api> apiList = selectApiListByRolePkList(rolePkList);
        return selectDivisionByApiList(apiList);
    }


    @Cacheable(value = "DivisionList", key = "'selectDivisionByApiList-' + #p0.hashCode()")
    public List<Division> selectDivisionByApiList(List<Api> apiList) {
        Map<Long, List<Api>> menuApiGroup = apiList.stream().collect(Collectors.groupingBy(Api::getMenuPk));
        List<Long> menuPkList = apiList.stream().map(Api::getMenuPk).distinct().toList();
        List<Menu> menuList = menuRepo.findAllByIdIn(menuPkList).stream().filter(Menu::isAvailableMenu)
                .map(menu -> menu.setApiList(menuApiGroup.get(menu.getId())))
                .sorted(menuSort).toList();
        Map<Long, List<Menu>> divisionMenuGroup = menuList.stream().collect(Collectors.groupingBy(Menu::getDivisionPk));
        List<Long> divisionPkList = menuList.stream().map(Menu::getDivisionPk).distinct().toList();
        return divisionRepo.findAllByIdIn(divisionPkList).stream().filter(Division::isAvailableDivision)
                .map(division -> division.setMenuList(divisionMenuGroup.get(division.getId())))
                .sorted(divisionSort).toList();
    }

}