package se.jh.jooqissue.testutil

import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.context.ContextConfiguration
import org.testcontainers.containers.PostgreSQLContainer

const val DBNAME = "test"
const val DBUSERNAME = "test"
const val DBPASSWORD = "test"

const val CONTAINER_VERSION = "postgres:13.2"

@ContextConfiguration(initializers = [AbstractIntegrationTest.Initializer::class])
abstract class AbstractIntegrationTest {

    companion object {
        val container = PostgreSQLContainer<Nothing>(CONTAINER_VERSION)
            .apply {
                withDatabaseName(DBNAME)
                withUsername(DBUSERNAME)
                withPassword(DBPASSWORD)
                withReuse(true)
                withLabel("reuse.UUID", "5E5773E7-CE40-45FA-A493-4ED60F1EC209")
            }
    }

    internal class Initializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
        override fun initialize(configurableApplicationContext: ConfigurableApplicationContext) {
            container.start()
            TestPropertyValues.of(
                "spring.datasource.url=jdbc:postgresql://${container.containerIpAddress}:${container.firstMappedPort}/$DBNAME",
                "spring.datasource.username=$DBUSERNAME",
                "spring.datasource.password=$DBPASSWORD",
            ).applyTo(configurableApplicationContext.environment)
        }
    }

}
