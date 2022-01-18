package ibf2021.ssf.weather.day18;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import static ibf2021.ssf.weather.day18.Constants.*;

import java.util.logging.Logger;

@Configuration
public class AppConfig {

    private final Logger logger = Logger.getLogger(Day18Application.class.getName());

    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private Integer redisPort;

    @Value("${spring.redis.database}")
    private Integer redisDatabase;

    private final String redisPassword;

    public AppConfig() {
        redisPassword = System.getenv(ENV_REDIS_PASSWORD);
    }

    @Bean(BEAN_WEATHER_CACHE)
    public RedisTemplate<String, String> redisTemplateFactory() {

        final RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();
        redisConfig.setHostName(redisHost);
        redisConfig.setPort(redisPort);
        if (null != redisPassword) {
            redisConfig.setPassword(redisPassword);
            logger.info("Set Redis password");
        }
        redisConfig.setDatabase(redisDatabase);

        final JedisClientConfiguration jedisConfig = JedisClientConfiguration.builder().build();
        final JedisConnectionFactory jedisFac = new JedisConnectionFactory(redisConfig, jedisConfig);
        jedisFac.afterPropertiesSet();

        final RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisFac);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());

        return template;
    }

}
