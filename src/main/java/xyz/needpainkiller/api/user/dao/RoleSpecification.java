package xyz.needpainkiller.api.user.dao;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import xyz.needpainkiller.api.user_hex.adapter.in.web.data.RoleRequests;
import xyz.needpainkiller.api.user_hex.domain.model.Role;
import xyz.needpainkiller.common.dto.DateType;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class RoleSpecification {
    public static Specification<Role> search(RoleRequests.SearchRoleRequest params) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicateList = new ArrayList<>();

            predicateList.add(criteriaBuilder.equal(root.get("useYn"), true));

            Long tenantPk = params.getTenantPk();
            if (tenantPk != null) {
                predicateList.add(criteriaBuilder.equal(root.get("tenantPk"), tenantPk));
            }

            DateType dateType = params.getDateType();
            Timestamp startDate = params.getStartDate();
            Timestamp endDate = params.getEndDate();

            if (dateType != null && startDate != null && endDate != null) {

                if (params.getDateType().equals(DateType.CREATE)) {
                    predicateList.add(criteriaBuilder.between(root.get("createdDate"), startDate, endDate));
                } else if (params.getDateType().equals(DateType.UPDATE)) {
                    predicateList.add(criteriaBuilder.between(root.get("updatedDate"), startDate, endDate));
                }
            }

            String searchBy = params.getSearchBy();
            String searchValue = params.getSearchValue();
            if (searchBy != null && !searchBy.isBlank() && searchValue != null && !searchValue.isBlank()) {
                switch (searchBy) {
                    case "ROLE_NAME" -> {
                        predicateList.add(criteriaBuilder.like(root.get("roleName"), "%" + searchValue + "%"));
                    }
                    case "ROLE_DESCRIPTION" -> {
                        predicateList.add(criteriaBuilder.like(root.get("roleDescription"), "%" + searchValue + "%"));
                    }
                }
            }
            return criteriaBuilder.and(predicateList.toArray(new Predicate[0]));
        };
    }
}
