package xyz.needpainkiller.api.user;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import xyz.needpainkiller.api.user.adapter.out.event.UserEventPublisherAdapter;
import xyz.needpainkiller.api.user.adapter.out.persistence.UserPersistenceAdapter;
import xyz.needpainkiller.api.user.adapter.out.persistence.mapper.UserPersistenceMapper;
import xyz.needpainkiller.api.user.adapter.out.persistence.repository.UserRepository;
import xyz.needpainkiller.api.user.domain.service.FindUserService;
import xyz.needpainkiller.api.user.domain.service.ManageUserService;

@Configuration
public class UserBeanConfiguration {

    @Bean
    public UserPersistenceAdapter userPersistenceAdapter(UserRepository userRepository, UserPersistenceMapper userPersistenceMapper) {
        return new UserPersistenceAdapter(userRepository, userPersistenceMapper);
    }

    @Bean
    public UserEventPublisherAdapter userEventPublisherAdapter(ApplicationEventPublisher applicationEventPublisher) {
        return new UserEventPublisherAdapter(applicationEventPublisher);
    }

    @Bean
    public FindUserService findUserService(UserPersistenceAdapter userPersistenceAdapter) {
        return new FindUserService(userPersistenceAdapter);
    }

    @Bean
    public ManageUserService manageUserService(UserPersistenceAdapter userPersistenceAdapter, UserEventPublisherAdapter userEventPublisherAdapter, BCryptPasswordEncoder bCryptPasswordEncoder) {
        return new ManageUserService(userPersistenceAdapter, userEventPublisherAdapter, bCryptPasswordEncoder);
    }
}
