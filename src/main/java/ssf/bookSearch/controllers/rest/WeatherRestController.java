package ibf2021.ssf.weather.day18.controllers.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ibf2021.ssf.weather.day18.Day18Application;
import ibf2021.ssf.weather.day18.models.Weather;
import ibf2021.ssf.weather.day18.services.WeatherService;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;

import static ibf2021.ssf.weather.day18.Constants.*;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping(path = "/weather", produces = MediaType.APPLICATION_JSON_VALUE)
public class WeatherRestController {

    private final Logger logger = Logger.getLogger(Day18Application.class.getName());

    @Autowired @Qualifier(BEAN_CACHING_WEATHER_SERVICE)
    private WeatherService weatherSvc;
    
    @GetMapping(path = "{city}")
    public ResponseEntity<String> getWeatherAsJson(@PathVariable String city) {

        List<Weather> weatherList = Collections.emptyList();

        try {
            weatherList = weatherSvc.getWeather(city);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "getWeather", ex);
        }

        System.out.println("===========> " + weatherList);

        final JsonArrayBuilder arrBuilder = Json.createArrayBuilder();
        weatherList.stream()
            .forEach(v -> arrBuilder.add(v.toJson()));

        return ResponseEntity.ok(arrBuilder.build().toString());
    }
}
