package it.polito.wa2.ticket_catalogue_service.repositories


import io.r2dbc.spi.ConnectionFactories
import io.r2dbc.spi.ConnectionFactory
import io.r2dbc.spi.ConnectionFactoryOptions
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator


@Configuration
@EnableR2dbcRepositories
class DbConfig {

    /*@Value("postgres")
    lateinit var user: String

    @Value("postgres")
    lateinit var password: String

    @Value("54321")
    var port: Int = -1

    @Bean
    fun connectionFactory(): ConnectionFactory {
        return ConnectionFactories.get(
                ConnectionFactoryOptions.builder().apply{
                    option(ConnectionFactoryOptions.DRIVER, "pool")
                    option(ConnectionFactoryOptions.PROTOCOL,"mariadb")
                    option(ConnectionFactoryOptions.HOST,"localhost")
                    option(ConnectionFactoryOptions.PORT, port)
                    option(ConnectionFactoryOptions.USER, user)
                    option(ConnectionFactoryOptions.PASSWORD, password)
                    option(ConnectionFactoryOptions.DATABASE, "warehouse")
                }.build()
        )
    }*/

    @Bean
    fun initializer(connectionFactory: ConnectionFactory): ConnectionFactoryInitializer {
        val initializer = ConnectionFactoryInitializer()
        initializer.setConnectionFactory(connectionFactory)
        initializer.setDatabasePopulator(
                ResourceDatabasePopulator(
                        ClassPathResource("schema.sql")
                )
        )
        return initializer
    }


}