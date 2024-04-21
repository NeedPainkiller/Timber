package xyz.needpainkiller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.needpainkiller.common.controller.CommonController;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.ResponseEntity.ok;

@Tag(name = "x1000000. TEST", description = "TEST")
@RequestMapping(value = "/api/v1/test", produces = {MediaType.APPLICATION_JSON_VALUE})
@Slf4j
@RestController
@RequiredArgsConstructor
@RefreshScope
public class TestController extends CommonController {

    @Value("${test.message}")
    private String message;


    @GetMapping(value = "/config")
    @Operation(description = "Spring Config 업데이트 체크")
    public ResponseEntity<Map<String, Object>> checConfig(HttpServletRequest request) {
        Map<String, Object> model = new HashMap<>();
        model.put("message", message);
        return ok(model);
    }

}