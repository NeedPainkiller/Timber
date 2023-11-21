package xyz.needpainkiller.core.api;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

@Slf4j
@Controller
public class ResourceController implements ErrorController {

    @Value("${spring.base-url}")
    private String baseUrl;

    @RequestMapping(value = "/error")
    public void handleError(HttpServletRequest request, HttpServletResponse response) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        try {
            if (status != null) {
                log.error("status : {} ", status);
                int statusCode = Integer.parseInt(status.toString());
                if (statusCode == HttpStatus.NOT_FOUND.value()) {
                    response.sendRedirect(baseUrl);
                }
            }
        } catch (IOException e) {
            log.error("CustomErrorController - handleError : {}", e.getMessage());
        }
    }
}

