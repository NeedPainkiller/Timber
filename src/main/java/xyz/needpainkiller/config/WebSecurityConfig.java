package xyz.needpainkiller.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.firewall.DefaultHttpFirewall;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import xyz.needpainkiller.api.authentication.AuthenticationService;
import xyz.needpainkiller.api.authentication.AuthorizationChecker;
import xyz.needpainkiller.lib.api.filter.ApiFilter;
import xyz.needpainkiller.lib.security.JwtAuthenticationFilter;


@Slf4j
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    @Value("${server.ssl.enabled}")
    private String sslEnabled;
    @Value("${spring.base-url}")
    private String baseUrl;
    @Value("${api.path-pattern}")
    private String API_PATTERN;

    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private AuthorizationChecker authorizationChecker;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private ApiFilter apiFilter;
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @PostConstruct
    public void init() {
        jwtAuthenticationFilter = new JwtAuthenticationFilter(authenticationService);
    }


    @Bean
    public ServletListenerRegistrationBean<HttpSessionEventPublisher> httpSessionEventPublisher() {
        return new ServletListenerRegistrationBean<>(new HttpSessionEventPublisher());
    }


/*    @Bean
    public AuthenticationManager authenticationManager(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(authenticationService).passwordEncoder(bCryptPasswordEncoder);
        return auth.build();
    }*/

    @Bean
    public AuthenticationManager authenticationManagerBean(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(authenticationService).passwordEncoder(bCryptPasswordEncoder);
        return authenticationManagerBuilder.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        log.info("corsConfigurationSource");
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.addAllowedOriginPattern(baseUrl);
        configuration.addAllowedOriginPattern("http://localhost:8080");
        configuration.addAllowedOriginPattern("http://localhost:3000");

        configuration.addAllowedHeader("*");

        configuration.addAllowedMethod(HttpMethod.GET);
        configuration.addAllowedMethod(HttpMethod.PUT);
        configuration.addAllowedMethod(HttpMethod.POST);
        configuration.addAllowedMethod(HttpMethod.DELETE);
        configuration.addAllowedMethod(HttpMethod.OPTIONS);

        configuration.setMaxAge(60L);
        configuration.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/api/**", configuration);
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public HttpFirewall defaultHttpFirewall() {
        return new DefaultHttpFirewall();
    }


//    @Autowired
//    private JwtAuthenticationEntryPoint authenticationErrorHandler;
//    @Autowired
//    private JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        if (sslEnabled.equalsIgnoreCase("true")) {
            http.requiresChannel(channelRequestMatcherRegistry -> channelRequestMatcherRegistry.anyRequest().requiresSecure());
        }
        HttpSecurity httpSecurity = http
                /**
                 * 세션기반 인증 비활성화
                 * */
                .sessionManagement((sessionManagement) -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                /**
                 * 기본 제공 Login 폼 비활성화
                 * HTTP Basic Authentication 비활성화 */
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                /**
                 * anonymous 를 비활성화 할 경우 JwtAuthenticationFilter 에서 등록되지 않은 SecurityContext 로 인해
                 * AuthenticationCredentialsNotFoundException 를 발생시켜 authenticationEntryPoint 이 호출됨
                 */
//                        .anonymous(AbstractHttpConfigurer::disable)
                /**
                 * API_PATH 를 통해 request 되는 요청은 CSRF 프로텍션을 적용함
                 * 하지만 JWT 인증을 사용하고 있으므로  CSRF 토큰을 사용하기 어려움
                 * 대신 ApiFilter.java 에서 Request Header 의 Referer 를 통해 요청정보를 검증하도록 함
                 * */
                .csrf(AbstractHttpConfigurer::disable)
                /**
                 * X-FRAME-ORIGIN 외 응답 Header 처리는 ApiFilter.java 같이 참조
                 * */
                .headers(httpHeadersConfigurer -> httpHeadersConfigurer.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                .cors(httpCorsConfigurer -> httpCorsConfigurer.configurationSource(corsConfigurationSource()))
                /**
                 *  JwtAuthenticationFilter.java 에서 SecurityContext 에 등록이 되지 않은 경우 발생
                 *  Anonymous 를 비활성화 하였을때 사용함
                 */
//                        .exceptionHandling(httpExceptionHandlingConfigurer -> httpExceptionHandlingConfigurer.authenticationEntryPoint(authenticationErrorHandler).accessDeniedHandler(jwtAccessDeniedHandler))
                .authorizeHttpRequests(authorize -> {
                    /**
                     * SWAGGER 및 Resource 접근 허용
                     * */
                    authorize.requestMatchers(
                                    new AntPathRequestMatcher("/actuator/**"),
                                    new AntPathRequestMatcher("/swagger-ui/**"),
                                    new AntPathRequestMatcher("/swagger.json"),
                                    new AntPathRequestMatcher("/swagger-resources/**"),
                                    new AntPathRequestMatcher("/swagger"),
                                    new AntPathRequestMatcher("/swagger**/**"),
                                    new AntPathRequestMatcher("/swagger-ui.html"),
                                    new AntPathRequestMatcher("/webjars/**"),
                                    new AntPathRequestMatcher("/v2/**"),
                                    new AntPathRequestMatcher("/v3/**"),
                                    new AntPathRequestMatcher("/WEB-INF/**"),
                                    new AntPathRequestMatcher("/web/**"),
                                    new AntPathRequestMatcher("/css/**"),
                                    new AntPathRequestMatcher("/js/**"),
                                    new AntPathRequestMatcher("/img/**"),
                                    new AntPathRequestMatcher("/view/**"),
                                    new AntPathRequestMatcher("/media/**"),
                                    new AntPathRequestMatcher("/static/**"),
                                    new AntPathRequestMatcher("/resources/**"),
                                    new AntPathRequestMatcher("/favicon.ico"),
                                    new AntPathRequestMatcher("/robots.txt")
                            ).permitAll()
                            /**
                             * authorizationChecker.java 의 check 메서드에서 API 접근 가능 여부 확인
                             * */
                            .requestMatchers(new AntPathRequestMatcher(API_PATTERN)).access(authorizationChecker);
                });
        httpSecurity.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        httpSecurity.addFilterBefore(apiFilter, JwtAuthenticationFilter.class);
        return httpSecurity.build();

    }


    @Bean
    public WebSecurityCustomizer WebSecurityCustomizer() {
        return web -> web.httpFirewall(defaultHttpFirewall())
                .ignoring().requestMatchers(
                        new AntPathRequestMatcher("/actuator/**"),
                        new AntPathRequestMatcher("/swagger-ui/**"),
                        new AntPathRequestMatcher("/swagger.json"),
                        new AntPathRequestMatcher("/swagger-resources/**"),
                        new AntPathRequestMatcher("/swagger"),
                        new AntPathRequestMatcher("/swagger**/**"),
                        new AntPathRequestMatcher("/swagger-ui.html"),
                        new AntPathRequestMatcher("/webjars/**"),
                        new AntPathRequestMatcher("/v2/**"),
                        new AntPathRequestMatcher("/v3/**"),
                        new AntPathRequestMatcher("/WEB-INF/**"),
                        new AntPathRequestMatcher("/web/**"),
                        new AntPathRequestMatcher("/css/**"),
                        new AntPathRequestMatcher("/js/**"),
                        new AntPathRequestMatcher("/img/**"),
                        new AntPathRequestMatcher("/view/**"),
                        new AntPathRequestMatcher("/media/**"),
                        new AntPathRequestMatcher("/static/**"),
                        new AntPathRequestMatcher("/resources/**"),
                        new AntPathRequestMatcher("/favicon.ico"),
                        new AntPathRequestMatcher("/robots.txt")
                );
    }
}
