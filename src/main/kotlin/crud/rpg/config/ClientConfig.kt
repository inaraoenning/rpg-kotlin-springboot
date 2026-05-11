package crud.rpg.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient

@Configuration  // roda em paralelo a aplicação
class ClientConfig {

    @Bean
    fun restClient(): RestClient {
        return RestClient.builder().build()
    }
}