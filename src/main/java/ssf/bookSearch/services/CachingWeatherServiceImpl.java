package ibf2021.ssf.weather.day18.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import ibf2021.ssf.weather.day18.Day18Application;
import ibf2021.ssf.weather.day18.models.Weather;

import static ibf2021.ssf.weather.day18.Constants.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service(BEAN_CACHING_WEATHER_SERVICE)
public class CachingWeatherServiceImpl implements WeatherService {

    private final Logger logger = Logger.getLogger(Day18Application.class.getName());

    @Autowired
    @Qualifier(BEAN_WEATHER_SERVICE)
    private WeatherServiceImpl delegate;

    @Autowired
    private WeatherCacheService cacheSvc;
    
    public List<Weather> getWeather(String city) {

        logger.info(">>>> Using CachingWeatherServiceImpl");

        Optional<List<Weather>> opt = cacheSvc.get(city);

        List<Weather> weatherList = Collections.emptyList();

        if (opt.isPresent()) {
            logger.info("Cache hit for %s".formatted(city));
            weatherList = opt.get();
        } else
            try {
                weatherList = delegate.getWeather(city);
                if (weatherList.size() > 0)
                    cacheSvc.save(city, weatherList);
            } catch (Exception ex) {
                logger.log(Level.WARNING, "Warning: %s".formatted(ex.getMessage()));
            }

        return weatherList;
    }
}
