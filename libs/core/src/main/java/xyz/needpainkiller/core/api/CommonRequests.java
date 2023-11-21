package xyz.needpainkiller.core.api;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommonRequests {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema
    public static class VisibleRequest implements Serializable {
        @Serial
        private static final long serialVersionUID = 4005543320943586886L;
        @Schema(description = "노출 여부", example = "true", required = true)
        private boolean visibleYn;
    }


    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema
    public static class LikeRequest implements Serializable {
        @Serial
        private static final long serialVersionUID = -2190656227779396132L;
        @Schema(description = "추천 여부", example = "true", required = true)
        private boolean likeYn;
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema
    public static class DateRequest implements Serializable {
        @Serial
        private static final long serialVersionUID = 2201978521616330202L;
        @NotEmpty
        @Schema(description = "조회일자", example = "1635724800000", required = true)
        private String date;
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema
    public static class MonthDateRequest implements Serializable {
        @Serial
        private static final long serialVersionUID = -4879576505147162503L;
        @NotNull
        @Min(2000)
        @Max(2100)
        @Schema(description = "조회년도", example = "2022", required = true)
        private Integer year;

        @NotNull
        @Min(1)
        @Max(12)
        @Schema(description = "조회월", example = "11", required = true)
        private Integer month;
    }


    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema
    public static class DateRangeRequest implements Serializable {
        @Serial
        private static final long serialVersionUID = 5186408120976995903L;
//        2021-11-01
        @Schema(description = "시작 일", example = "1635724800000", required = true)
        private Timestamp startDate;
//        2021-12-31
        @Schema(description = "종료 일", example = "1640908800000", required = true)
        private Timestamp endDate;
    }
}
