package xyz.needpainkiller.core.lib.mybatis.translator;

import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.MyBatisSystemException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.PersistenceExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;

import javax.sql.DataSource;
import java.sql.SQLException;

public class MyBatisExceptionTranslator implements PersistenceExceptionTranslator {
    private final DataSource dataSource;
    private SQLExceptionTranslator exceptionTranslator;

    public MyBatisExceptionTranslator(SqlSessionFactory sqlSessionFactory) {
        this.dataSource = sqlSessionFactory.getConfiguration().getEnvironment().getDataSource();
        this.initExceptionTranslator();
    }

    /**
     * {@inheritDoc}
     */
    public DataAccessException translateExceptionIfPossible(RuntimeException e) {
        if (e instanceof PersistenceException) {
            if (e.getCause() instanceof PersistenceException) {
                e = (PersistenceException) e.getCause();
            }
            if (e.getCause() instanceof SQLException) {
                this.initExceptionTranslator();
                return this.exceptionTranslator.translate(e.getMessage() + "\n", null, (SQLException) e.getCause());
            }
            return new MyBatisSystemException(e);
        }
        return null;
    }

    /**
     * Initializes the internal translator reference.
     */
    private synchronized void initExceptionTranslator() {
        if (this.exceptionTranslator == null) {
            this.exceptionTranslator = new DacSQLErrorCodeSQLExceptionTranslator(this.dataSource);
        }
    }
}

