package pab.ta.signal;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import ru.tinkoff.piapi.core.InvestApi;

@Configuration
@PropertySource("classpath:application.properties")
@PropertySource("classpath:secret.properties")
public class Config {
    @Bean
    @Value("${tinkoff.token.api}")
    public InvestApi getApi(String token){
        return  InvestApi.create(token);
    }

}
