package xyz.needpainkiller.api.workingday;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.needpainkiller.api.tenant.domain.error.TenantException;
import xyz.needpainkiller.api.user.model.User;
import xyz.needpainkiller.api.workingday.dao.HolidayRepo;
import xyz.needpainkiller.api.workingday.dto.WorkingDay;
import xyz.needpainkiller.api.workingday.dto.WorkingDayRequests;
import xyz.needpainkiller.api.workingday.error.WorkingDayErrorCode;
import xyz.needpainkiller.api.workingday.error.WorkingDayException;
import xyz.needpainkiller.api.workingday.model.Holiday;
import xyz.needpainkiller.helper.DateRange;
import xyz.needpainkiller.helper.TimeHelper;

import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

import static java.time.temporal.TemporalAdjusters.*;
import static xyz.needpainkiller.api.tenant.domain.error.TenantErrorCode.TENANT_CONFLICT;

@Slf4j
@Service
public class WorkingDayService {
    private static final Predicate<LocalDate> IS_WEEKEND = date -> date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY;
    private final HolidayRepo holidayRepo;

    @Autowired
    public WorkingDayService(HolidayRepo holidayRepo) {
        this.holidayRepo = holidayRepo;
    }

    public List<LocalDate> toLocalDateList(Holiday holiday) {
        LocalDate startDate = TimeHelper.fromTimestampToLocalDate(holiday.getStart());
        LocalDate endDate = TimeHelper.fromTimestampToLocalDate(holiday.getEnd());
        return new DateRange(startDate, endDate).toList();
    }

    public List<LocalDate> toLocalDateList(List<Holiday> holidayList) {
        return holidayList.stream()
                .map(this::toLocalDateList)
                .flatMap(Collection::stream)
                .distinct().toList();
    }

    static boolean scope(LocalDate localDate, LocalDate from, LocalDate to) {
        return localDate.isAfter(from) && localDate.isBefore(to);
    }

    public List<LocalDate> monthlyWorkingDate(Long tenantPk, LocalDate date) {
        LocalDate firstDay = date.with(firstDayOfMonth());
        LocalDate lastDay = date.with(lastDayOfMonth()).plusDays(1);

        List<Holiday> holidayList = holidayRepo.findAllByTenantPk(tenantPk);
        List<LocalDate> holidayDateList = toLocalDateList(holidayList);

        return firstDay.datesUntil(lastDay)
                .filter(WorkingDayService.IS_WEEKEND.negate())
                .filter(localDate -> !holidayDateList.contains(localDate))
                .toList();
    }

    
    public LocalDate previousWorkingDate(Long tenantPk, LocalDate date) {
        LocalDate from = date.minusYears(1).with(firstDayOfYear());
        List<Holiday> holidayList = holidayRepo.findAllByTenantPk(tenantPk);
        List<LocalDate> holidayDateList = toLocalDateList(holidayList);


        return from.datesUntil(date)
                .filter(IS_WEEKEND.negate())
                .filter(localDate -> !holidayDateList.contains(localDate))
                .reduce((first, second) -> second).orElse(null);
    }

    
    public LocalDate nextWorkingDate(Long tenantPk, LocalDate date) {
        LocalDate tomorrow = date.plusDays(1);
        LocalDate to = tomorrow.plusYears(1).with(lastDayOfYear());
        List<Holiday> holidayList = holidayRepo.findAllByTenantPk(tenantPk);
        List<LocalDate> holidayDateList = toLocalDateList(holidayList);


        return tomorrow.datesUntil(to)
                .filter(IS_WEEKEND.negate())
                .filter(localDate -> !holidayDateList.contains(localDate))
                .reduce((first, second) -> first).orElse(null);
    }

    
    public LocalDate monthFirstWorkingDate(Long tenantPk, LocalDate date) {
        List<LocalDate> workingLocalDates = monthlyWorkingDate(tenantPk, date);
        Optional<LocalDate> firstWorkLocalDateOpt = workingLocalDates.stream().findFirst();
        return firstWorkLocalDateOpt.orElse(null);
    }

    
    public LocalDate monthLastWorkingDate(Long tenantPk, LocalDate date) {
        List<LocalDate> workingLocalDates = monthlyWorkingDate(tenantPk, date);
        Optional<LocalDate> lastWorkLocalDateOpt = workingLocalDates.stream().reduce((first, second) -> second);
        return lastWorkLocalDateOpt.orElse(null);
    }

    /**
     * 영업일 정보 조회
     *
     * @param tenantPk 테넌트 PK
     * @param date     기준일자
     */
    
    public WorkingDay workingDayOfDate(Long tenantPk, LocalDate date) {
        return workingDayOfDate(tenantPk, date, 0, true);
    }

    /**
     * 영업일 정보 조회
     *
     * @param tenantPk             테넌트 PK
     * @param date                 기준일자
     * @param relativeDayOffset    기준일자 기점 일자 추가 차감 오프셋
     * @param offsetIncludeHoliday 오프셋에 휴일 포함 여부
     */
    
    public WorkingDay workingDayOfDate(Long tenantPk, LocalDate date,
                                       Integer relativeDayOffset, boolean offsetIncludeHoliday) {

        boolean isHoliday;
        boolean isWeekend;
        int workDayOfYear;
        int workDayOfMonth;

        List<Holiday> holidayList = holidayRepo.findAllByTenantPk(tenantPk);
        List<LocalDate> holidayDateList = toLocalDateList(holidayList);

        if (relativeDayOffset == null || relativeDayOffset == 0) {
            isHoliday = holidayDateList.contains(date);
            isWeekend = IS_WEEKEND.test(date);
        } else {
            int addition = relativeDayOffset > 0 ? 1 : -1;
            int currentOffset = 0;
            while (currentOffset != relativeDayOffset) {
                date = date.plusDays(addition);
                if (!offsetIncludeHoliday && (IS_WEEKEND.test(date) || holidayDateList.contains(date))) {
                    continue;
                }
                currentOffset = currentOffset + addition;
            }
            isHoliday = holidayDateList.contains(date);
            isWeekend = IS_WEEKEND.test(date);
        }

        LocalDate nextDay = date.plusDays(1);

        LocalDate firstDayOfYear = date.with(firstDayOfYear());
        List<LocalDate> workDayOfYearList = firstDayOfYear.datesUntil(nextDay)
                .filter(IS_WEEKEND.or(holidayDateList::contains).negate())
                .toList();
        workDayOfYear = workDayOfYearList.size();


        LocalDate firstDayOfMonth = date.with(firstDayOfMonth());
        List<LocalDate> workDayOfMonthList = firstDayOfMonth.datesUntil(nextDay)
                .filter(IS_WEEKEND.or(holidayDateList::contains).negate())
                .toList();
        workDayOfMonth = workDayOfMonthList.size();

        return new WorkingDay(date, isHoliday, isWeekend, workDayOfYear, workDayOfMonth);
    }

    
    public List<Holiday> selectHolidayList(Long tenantPk) {
        return holidayRepo.findAllByTenantPk(tenantPk);
    }

    
    public List<LocalDate> selectHolidayLocalDateList(Long tenantPk) {
        List<Holiday> holidayList = selectHolidayList(tenantPk);
        return toLocalDateList(holidayList);
    }

    
    public Holiday createHoliday(WorkingDayRequests.UpsertHoliday param, User requester, Long tenantPk) {
        checkHolidayValidation(param);
        Timestamp start = param.getStart();
        Timestamp end = param.getEnd();

        Holiday holiday = new Holiday();
        holiday.setTenantPk(tenantPk);
        holiday.setUuid(UUID.randomUUID().toString());
        holiday.setTitle(param.getTitle().trim());
        holiday.setStart(start);
        holiday.setEnd(end);
        holiday.setData(param.getData());
        return holidayRepo.save(holiday);
    }

    
    public Holiday updateHoliday(Long holidayPk, WorkingDayRequests.UpsertHoliday param, User requester) {
        checkHolidayValidation(param);
        Timestamp start = param.getStart();
        Timestamp end = param.getEnd();

        Long tenantPk = requester.getTenantPk();

        Optional<Holiday> optionalHoliday = holidayRepo.findById(holidayPk);

        if (optionalHoliday.isEmpty()) {
            throw new WorkingDayException(WorkingDayErrorCode.ORG_WORKING_DAY_NOT_EXIST);
        }
        Holiday holiday = optionalHoliday.get();

        if (!holiday.getTenantPk().equals(tenantPk)) {
            throw new TenantException(TENANT_CONFLICT);
        }

        holiday.setTenantPk(tenantPk);
        holiday.setTitle(param.getTitle().trim());
        holiday.setStart(start);
        holiday.setEnd(end);
        holiday.setData(param.getData());
        return holidayRepo.save(holiday);
    }

    
    public void deleteHoliday(Long holidayPk, User requester) {
        Optional<Holiday> optionalHoliday = holidayRepo.findById(holidayPk);
        if (optionalHoliday.isEmpty()) {
            throw new WorkingDayException(WorkingDayErrorCode.ORG_WORKING_DAY_NOT_EXIST);
        }
        Long tenantPk = requester.getTenantPk();
        Holiday holiday = optionalHoliday.get();
        if (!holiday.getTenantPk().equals(tenantPk)) {
            throw new TenantException(TENANT_CONFLICT);
        }
        holidayRepo.delete(holiday);
    }

    public void checkHolidayValidation(WorkingDayRequests.UpsertHoliday param) {
        Timestamp start = param.getStart();
        Timestamp end = param.getEnd();
        if (end.before(start)) {
            throw new WorkingDayException(WorkingDayErrorCode.ORG_WORKING_DAY_WRONG_DATE_RANGE);
        }
    }
}
