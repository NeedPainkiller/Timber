package xyz.needpainkiller.core.lib;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

public interface JpaPaginationDirection {
    String DEFAULT_ORDER_BY = "id";

    Boolean getIsPagination();

    String getOrderBy();

    String getOrderDirection();

    Integer getStartId();

    Integer getItemCnt();


    default Pageable pageOf() {
        if (getIsPagination() == null || !getIsPagination()) {
            return Pageable.unpaged();
        }

        Integer startId = getStartId();
        Integer itemCnt = getItemCnt();
        if (startId == null || itemCnt == null) {
            return Pageable.unpaged();
        }

        if (itemCnt < 0) {
            itemCnt = 0;
        }
        if (itemCnt > 100) {
            itemCnt = 100;
        }

        int page = 0;
        if (startId != 0 && itemCnt != 0) {
            page = startId / itemCnt;
        }

        String orderDirection = getOrderDirection();
        Direction direction;
        if (orderDirection == null || orderDirection.equals("DESC")) {
            direction = Direction.DESC;
        } else {
            direction = Direction.ASC;
        }
        String orderBy = getOrderBy();
        if (orderBy == null || orderBy.isEmpty()) {
            return PageRequest.of(page, itemCnt, direction, DEFAULT_ORDER_BY);
        } else {
            return PageRequest.of(page, itemCnt, direction, orderBy);
        }
    }

    default Sort sort() {
        String orderBy = getOrderBy();
        if (orderBy == null || orderBy.isEmpty()) {
            orderBy = DEFAULT_ORDER_BY;
        }
        return Sort.by(orderBy);
    }
}
