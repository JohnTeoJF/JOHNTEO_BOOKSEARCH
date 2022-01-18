package ibf2021.ssf.weather.day18.controllers;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ibf2021.ssf.weather.day18.Day18Application;
import ibf2021.ssf.weather.day18.models.Weather;
import ibf2021.ssf.weather.day18.services.WeatherService;

import static ibf2021.ssf.weather.day18.Constants.*;

@Controller
@RequestMapping(path = "/weather")
public class WeatherController {

    private final Logger logger = Logger.getLogger(Day18Application.class.getName());

    @Autowired
    @Qualifier(BEAN_CACHING_WEATHER_SERVICE)
    private WeatherService weatherSvc;

    @GetMapping
    public String getWeather(@RequestParam(required = true) String city, Model model) {

        logger.log(Level.INFO, "City: %s".formatted(city));

        List<Weather> weatherList = Collections.emptyList();

        try {
            weatherList = weatherSvc.getWeather(city);
        } catch (Exception ex) {
            logger.log(Level.WARNING, "Warning: %s".formatted(ex.getMessage()));
        }

        if (weatherList.size() > 0)
            city = weatherList.get(0).getCityName();
        
        model.addAttribute("weather", weatherList);
        model.addAttribute("city", city);

        return "weather";
    }
    
}
