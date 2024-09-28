package xyz.needpainkiller.api.workingday;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import xyz.needpainkiller.api.authentication.AuthenticationService;
import xyz.needpainkiller.api.user.domain.model.User;
import xyz.needpainkiller.api.workingday.dto.HolidayCsv;
import xyz.needpainkiller.api.workingday.dto.WorkingDay;
import xyz.needpainkiller.api.workingday.dto.WorkingDayRequests;
import xyz.needpainkiller.api.workingday.model.Holiday;
import xyz.needpainkiller.common.controller.CommonController;
import xyz.needpainkiller.lib.sheet.SpreadSheetService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.http.ResponseEntity.status;

@Slf4j
@RestController
@RequiredArgsConstructor
public class WorkingDayController extends CommonController implements WorkingDayApi {
    protected static final String KEY_HOLIDAY = "holiday";
    protected static final String KEY_HOLIDAY_LIST = "holidayList";

    @Autowired
    private WorkingDayService workingDayService;
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private SpreadSheetService sheetService;

    @Override
    public ResponseEntity<WorkingDay> selectThisWorkingDay(HttpServletRequest request) {
        LocalDate now = LocalDate.now();
        Long tenantPk = authenticationService.getTenantPkByToken(request);
        WorkingDay today = workingDayService.workingDayOfDate(tenantPk, now);
        return ResponseEntity.ok(today);
    }

    @Override
    public ResponseEntity<Map<String, Object>> selectHolidayList(HttpServletRequest request) {
        Map<String, Object> model = new HashMap<>();
        Long tenantPk = authenticationService.getTenantPkByToken(request);
        List<Holiday> holidayList = workingDayService.selectHolidayList(tenantPk);
        model.put(KEY_HOLIDAY_LIST, holidayList);
        return ok(model);
    }

    @Override
    public ResponseEntity<Map<String, Object>> createHoliday(WorkingDayRequests.UpsertHoliday param, HttpServletRequest request) {
        Map<String, Object> model = new HashMap<>();
        User requester = authenticationService.getUserByToken(request);
        Long tenantPk = authenticationService.getTenantPkByToken(request);
        Holiday holiday = workingDayService.createHoliday(param, requester, tenantPk);
        model.put(KEY_HOLIDAY, holiday);
        return ok(model);
    }

    @Override
    public ResponseEntity<Map<String, Object>> updateHoliday(Long holidayPk, WorkingDayRequests.UpsertHoliday param, HttpServletRequest request) {
        Map<String, Object> model = new HashMap<>();
        User requester = authenticationService.getUserByToken(request);
        Long tenantPk = authenticationService.getTenantPkByToken(request);
        Holiday holiday = workingDayService.updateHoliday(holidayPk, param, requester);
        model.put(KEY_HOLIDAY, holiday);
        return ok(model);
    }

    @Override
    public ResponseEntity<Map<String, Object>> deleteHoliday(Long holidayPk, HttpServletRequest request) {
        Map<String, Object> model = new HashMap<>();
        User requester = authenticationService.getUserByToken(request);
        Long tenantPk = authenticationService.getTenantPkByToken(request);
        workingDayService.deleteHoliday(holidayPk, requester);
        return status(HttpStatus.NO_CONTENT).body(model);
    }

    @Override
    public void downloadHolidayList(HttpServletRequest request, HttpServletResponse response) {
        Long tenantPk = authenticationService.getTenantPkByToken(request);
        List<Holiday> holidayList = workingDayService.selectHolidayList(tenantPk);
        List<HolidayCsv> holidayCsvList = holidayList.stream().map(HolidayCsv::new).toList();
        sheetService.downloadExcel(HolidayCsv.class, holidayCsvList, response);
    }

    @Override
    public ResponseEntity<Map<String, Object>> uploadHolidayBulk(WorkingDayRequests.BulkHolidayListRequest param, HttpServletRequest request) {
        Map<String, Object> model = new HashMap<>();
        User requester = authenticationService.getUserByToken(request);
        Long tenantPk = authenticationService.getTenantPkByToken(request);

        List<WorkingDayRequests.UpsertHoliday> paramList = param.getHolidayRequestList();
        List<Holiday> holidayList = new ArrayList<>();
        paramList.forEach(p -> {
            Holiday holiday = workingDayService.createHoliday(p, requester, tenantPk);
            holidayList.add(holiday);
        });
        model.put(KEY_HOLIDAY_LIST, holidayList);
        return ok(model);
    }

}
