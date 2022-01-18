package ibf2021.ssf.weather.day18.services;

import java.util.List;

import ibf2021.ssf.weather.day18.models.Weather;

public interface WeatherService {
    
    public List<Weather> getWeather(String city);
}
