package ibf2021.ssf.weather.day18.services;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import ibf2021.ssf.weather.day18.Day18Application;
import ibf2021.ssf.weather.day18.models.Weather;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

import static ibf2021.ssf.weather.day18.Constants.*;

@Service(BEAN_WEATHER_SERVICE)
public class WeatherServiceImpl implements WeatherService {

    private final Logger logger = Logger.getLogger(Day18Application.class.getName());

    private final String appId;

    public WeatherServiceImpl() {
        String k = System.getenv(ENV_OPENWEATHERMAP_KEY);
        if ((null != k) && (k.trim().length() > 0)) {
            appId = k;
            logger.info("OPENWEATHERMAP_KEY set");
        } else {
            appId = "abc123";
            logger.warning("OPENWEATHERMAP_KET not set");
        }
    }

    public List<Weather> getWeather(String city) {

        final String url = UriComponentsBuilder
                .fromUriString(URL_WEATHER)
                .queryParam("q", city.trim().replace(" ", "+"))
                .queryParam("appid", appId)
                .queryParam("units", "metric")
                .toUriString();

        final RequestEntity<Void> req = RequestEntity.get(url).build();
        final RestTemplate template = new RestTemplate();
        final ResponseEntity<String> resp = template.exchange(req, String.class);

        if (resp.getStatusCode() != HttpStatus.OK)
            throw new IllegalArgumentException(
                "Error: status code %s".formatted(resp.getStatusCode().toString())
            );
        final String body = resp.getBody();

        logger.log(Level.INFO, "payload: %s".formatted(body));

        try (InputStream is = new ByteArrayInputStream(body.getBytes())) {
            final JsonReader reader = Json.createReader(is);
            final JsonObject result = reader.readObject();
            final JsonArray readings = result.getJsonArray("weather");
            final String cityName = result.getString("name");
            final float temperature = (float)result.getJsonObject("main").getJsonNumber("temp").doubleValue();
            return readings.stream()
                .map(v -> (JsonObject)v)
                .map(Weather::create)
                .map(w -> {
                    w.setCityName(cityName);
                    w.setTemperature(temperature);
                    return w;
                })
                .collect(Collectors.toList());

        } catch (Exception ex) { 
            ex.printStackTrace();
        }
        
        return Collections.EMPTY_LIST;
    }
    
}
