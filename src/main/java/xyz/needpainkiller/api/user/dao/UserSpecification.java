package xyz.needpainkiller.api.user.dao;

import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;
import xyz.needpainkiller.api.team.model.Team;
import xyz.needpainkiller.api.user.dto.UserRequests;
import xyz.needpainkiller.api.user_hex.domain.model.User;
import xyz.needpainkiller.api.user_hex.domain.model.UserRoleMap;
import xyz.needpainkiller.common.dto.DateType;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class UserSpecification {
    public static Specification<User> search(UserRequests.SearchUserRequest params) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicateList = new ArrayList<>();

            predicateList.add(criteriaBuilder.equal(root.get("useYn"), true));

            Long tenantPk = params.getTenantPk();
            if (tenantPk != null) {
                predicateList.add(criteriaBuilder.equal(root.get("tenantPk"), tenantPk));
            }
            Long teamPk = params.getTeamPk();
            if (teamPk != null) {
                predicateList.add(criteriaBuilder.equal(root.get("teamPk"), teamPk));
            }

            Long rolePk = params.getRolePk();
            if (rolePk != null) {
                Subquery<UserRoleMap> subUserRoleMapQuery = query.subquery(UserRoleMap.class);
                Root<UserRoleMap> subUserRoleMapQueryRoot = subUserRoleMapQuery.from(UserRoleMap.class);

                Predicate rolePkPredicate = criteriaBuilder.equal(subUserRoleMapQueryRoot.get("rolePk"), rolePk);
                subUserRoleMapQuery.select(subUserRoleMapQueryRoot.get("userPk")).where(rolePkPredicate);
//                predicateList.add(criteriaBuilder.and(root.get("id").in(subUserRoleMapQuery)));
                predicateList.add(root.get("id").in(subUserRoleMapQuery));

            }

            DateType dateType = params.getDateType();
            Timestamp startDate = params.getStartDate();
            Timestamp endDate = params.getEndDate();

            if (dateType != null && startDate != null && endDate != null) {

                if (params.getDateType().equals(DateType.CREATE)) {
                    predicateList.add(criteriaBuilder.between(root.get("createdDate"), startDate, endDate));
                } else if (params.getDateType().equals(DateType.UPDATE)) {
                    predicateList.add(criteriaBuilder.between(root.get("updatedDate"), startDate, endDate));
                } else if (params.getDateType().equals(DateType.LOGIN)) {
                    predicateList.add(criteriaBuilder.between(root.get("lastLoginDate"), startDate, endDate));
                }
            }

            String searchBy = params.getSearchBy();
            String searchValue = params.getSearchValue();
            if (searchBy != null && !searchBy.isBlank() && searchValue != null && !searchValue.isBlank()) {
                switch (searchBy) {
                    case "USER_ID" -> {
                        predicateList.add(criteriaBuilder.like(root.get("userId"), "%" + searchValue + "%"));
                    }
                    case "USER_NAME" -> {
                        predicateList.add(criteriaBuilder.like(root.get("userName"), "%" + searchValue + "%"));
                    }
                    case "TEAM_NAME" -> {
                        Subquery<Team> subTeamQuery = query.subquery(Team.class);
                        Root<Team> subTeamQueryRoot = subTeamQuery.from(Team.class);

                        Predicate teamNamePredicate = criteriaBuilder.like(subTeamQueryRoot.get("teamName"), "%" + searchValue + "%");
                        subTeamQuery.select(subTeamQueryRoot.get("id")).where(teamNamePredicate);
                        predicateList.add(root.get("teamPk").in(subTeamQuery));
                    }
                }
            }
            return criteriaBuilder.and(predicateList.toArray(new Predicate[0]));
        };
    }
}
