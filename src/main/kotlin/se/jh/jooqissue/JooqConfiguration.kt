package se.jh.jooqissue

import org.jooq.ExecuteContext
import org.jooq.SQLDialect
import org.jooq.impl.DataSourceConnectionProvider
import org.jooq.impl.DefaultConfiguration
import org.jooq.impl.DefaultDSLContext
import org.jooq.impl.DefaultExecuteListener
import org.jooq.impl.DefaultExecuteListenerProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator
import org.springframework.jdbc.support.SQLExceptionTranslator
import javax.sql.DataSource

@Configuration
class JooqConfiguration(private val dataSource: DataSource) {

    @Bean
    fun dsl(): DefaultDSLContext {
        return DefaultDSLContext(configuration())
    }

    private fun configuration(): DefaultConfiguration {
        val jooqConfiguration = DefaultConfiguration()
        jooqConfiguration.set(SQLDialect.POSTGRES)
        jooqConfiguration.set(connectionProvider())
        jooqConfiguration.set(DefaultExecuteListenerProvider(exceptionTransformer()))
        return jooqConfiguration
    }

    @Bean
    fun connectionProvider(): DataSourceConnectionProvider {
        return DataSourceConnectionProvider(TransactionAwareDataSourceProxy(dataSource))
    }

    @Bean
    fun exceptionTransformer(): ExceptionTranslator {
        return ExceptionTranslator()
    }

    class ExceptionTranslator : DefaultExecuteListener() {
        override fun exception(context: ExecuteContext) {
            val dialect = context.configuration().dialect()
            val translator: SQLExceptionTranslator = SQLErrorCodeSQLExceptionTranslator(dialect.name)
            context.exception(translator.translate("Access database using jOOQ", context.sql(), context.sqlException()!!))
        }
    }
}
