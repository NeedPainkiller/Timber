package xyz.needpainkiller.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.config.BootstrapMode;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Slf4j
@Configuration
@EnableTransactionManagement
@MapperScan(value = {"xyz.needpainkiller"}, annotationClass = org.apache.ibatis.annotations.Mapper.class,
        sqlSessionFactoryRef = "sqlSessionFactory")
@EntityScan(basePackages = {"xyz.needpainkiller.**.model", "xyz.needpainkiller.**.entity"})
//@EntityScan(basePackageClasses = ScannableEntity.class)
@EnableJpaRepositories(
        bootstrapMode = BootstrapMode.DEFERRED,
        basePackages = {"xyz.needpainkiller.**.dao", "xyz.needpainkiller.**.repository"},
//        basePackageClasses = ScannableRepository.class,
        entityManagerFactoryRef = "entityManagerFactory",
        transactionManagerRef = "jpaTransactionManager"
)
public class DatabaseConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public HikariConfig hikariConfig() {
        return new HikariConfig();
    }

    /**
     * Datasource : Connection Pool을 지원하는 인터페이스
     */
    @Primary
    @Bean
    public DataSource dataSource() {
        DataSource dataSource = new HikariDataSource(hikariConfig());
        log.info("datasource :{}", dataSource);
        return dataSource;
    }

    /**
     * LocalContainerEntityManagerFactoryBean
     * EntityManager를 생성하는 팩토리
     * SessionFactoryBean과 동일한 역할, Datasource와 mapper를 스캔할 .xml 경로를 지정하듯이
     * datasource와 엔티티가 저장된 폴더 경로를 매핑해주면 된다.
     *
     * @param dataSource
     * @return
     */
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(dataSource);
        emf.setPackagesToScan("xyz.needpainkiller.**.model", "xyz.needpainkiller.**.entity");
        emf.setPersistenceUnitName("entityManager");
        emf.setJpaVendorAdapter(vendorAdapter);
//        emf.setJpaPropertyMap(properties);
        return emf;
    }

    /**
     * SqlSessionFactory : SqlSession을 찍어내는 역할
     * Datasourc를 참조하여 MyBatis와 Mysql 서버를 연동한다. SqlSession을 사용하기 위해 사용한다.
     *
     * @param dataSource
     * @param applicationContext
     */
    @Profile("mssql")
    @Bean("sqlSessionFactory")
    public SqlSessionFactory mssqlSessionFactory(DataSource dataSource, ApplicationContext applicationContext) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        sqlSessionFactoryBean.setVfs(SpringBootVFS.class);
        sqlSessionFactoryBean.setTypeAliasesPackage("xyz.needpainkiller.**.dao, xyz.needpainkiller.**.repository");
//        sqlSessionFactoryBean.setMapperLocations(applicationContext.getResources("classpath:/mapper-mssql/**/*.xml"));
        return sqlSessionFactoryBean.getObject();
    }

    @Profile({"mariadb", "mysql"})
    @Bean("sqlSessionFactory")
    public SqlSessionFactory mariadbSessionFactory(DataSource dataSource, ApplicationContext applicationContext) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        sqlSessionFactoryBean.setVfs(SpringBootVFS.class);
        sqlSessionFactoryBean.setTypeAliasesPackage("xyz.needpainkiller.**.dao, xyz.needpainkiller.**.repository");
//        sqlSessionFactoryBean.setMapperLocations(applicationContext.getResources("classpath:/mapper-mariadb/**/*.xml"));
        return sqlSessionFactoryBean.getObject();
    }


    /**
     * SqlSessionTemplate : SqlSession을 구현하고 코드에서 SqlSession을 대체하는 역할을 한다. 마이바티스 예외처리나 세션의 생명주기 관리
     *
     * @param sqlSessionFactory
     */
    @Primary
    @Bean
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    /**
     * JPA / MyBatis 간 동일 Transaction Manager 참조
     */

    /*@Bean
    public DataSourceTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource());
    }*/
    @Primary
    @Bean
    public JpaTransactionManager jpaTransactionManager(LocalContainerEntityManagerFactoryBean emf) {
        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
        jpaTransactionManager.setEntityManagerFactory(emf.getObject());
        return jpaTransactionManager;
    }

}
