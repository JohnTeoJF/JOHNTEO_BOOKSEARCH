package ibf2021.ssf.weather.day18.services;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ibf2021.ssf.weather.day18.Day18Application;
import ibf2021.ssf.weather.day18.models.Weather;
import ibf2021.ssf.weather.day18.repositories.WeatherRepository;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

@Service
public class WeatherCacheService {

    private final Logger logger = Logger.getLogger(Day18Application.class.getName());

    @Autowired
    private WeatherRepository weatherRepo;

    public void save(String cityName, List<Weather> weather) {
        JsonArrayBuilder arrBuilder = Json.createArrayBuilder();
        weather.stream()   
            .forEach(v -> arrBuilder.add(v.toJson()));
        weatherRepo.save(cityName, arrBuilder.build().toString());
    }

    public Optional<List<Weather>> get(String cityName) {
        Optional<String> opt = weatherRepo.get(cityName);
        if (opt.isEmpty())
            return Optional.empty();

        JsonArray jsonArray = parseJsonArray(opt.get());
        List<Weather> weather = jsonArray.stream()
            .map(v -> (JsonObject)v)
            .map(Weather::create)
            .collect(Collectors.toList());
        return Optional.of(weather);
    }

    private JsonArray parseJsonArray(String s) {
        try (InputStream is = new ByteArrayInputStream(s.getBytes())) {
            JsonReader reader = Json.createReader(is);
            return reader.readArray();
        } catch (Exception ex) {
            logger.log(Level.WARNING, "Parsing", ex);
        }

        // Need to handle error
        return Json.createArrayBuilder().build();
    }
}
