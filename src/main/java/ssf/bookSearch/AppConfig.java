package ssf.bookSearch;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.logging.Logger;

import static ssf.bookSearch.Constants.*;

@Configuration
public class AppConfig {
    
    private final Logger logger = Logger.getLogger(BookSearchApplication.class.getName());

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

    @Bean
    @Scope("singleton")
    public RedisTemplate<String, Object> redisTemplateFactory() {

        final RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();
            redisConfig.setHostName(redisHost);
            redisConfig.setPort(redisPort);
            redisConfig.setDatabase(redisDatabase);

		if ((redisPassword != null) && (redisPassword.length() > 0)){
            redisConfig.setPassword(redisPassword);

            logger.info("*******************************");
            logger.info("Redis password has been set");
            logger.info("*******************************");
        }

        final JedisClientConfiguration jedisConfig = JedisClientConfiguration.builder().build();
        final JedisConnectionFactory jedisFac = new JedisConnectionFactory(redisConfig, jedisConfig);
        jedisFac.afterPropertiesSet();

        final RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisFac);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());

        return template;
    }

}
