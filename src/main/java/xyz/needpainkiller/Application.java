package xyz.needpainkiller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.event.EventListener;

import java.time.Duration;
import java.time.Instant;

@Slf4j
@SpringBootApplication
public class Application extends SpringBootServletInitializer {
    private static Instant startTime;

    public static void main(String[] args) {
        startTime = Instant.now();
        SpringApplication springApplication = new SpringApplication(Application.class);
        springApplication.addListeners(new ApplicationPidFileWriter());
        springApplication.run(args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void ready() {
        Instant readyTime = Instant.now();
        log.info("Time between start and ApplicationReadyEvent: {}ms", Duration.between(startTime, readyTime).toMillis());
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }
}
