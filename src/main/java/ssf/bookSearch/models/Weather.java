package ibf2021.ssf.weather.day18.models;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

public class Weather {

    private String cityName;
    private String main;
    private String description;
    private String icon;
    private Float temperature;
    private Float latitude;
    private Float longitude;

    public String getCityName() { return this.cityName; }
    public void setCityName(String cityName) { this.cityName = cityName; }

    public String getMain() { return main; }
    public void setMain(String main) { this.main = main; }

    public String getDescription() { return this.description; }
    public void setDescription(String description) { this.description = description; }

    public String getIcon() { 
        return "http://openweathermap.org/img/wn/%s@2x.png".formatted(icon);
    }
    public void setIcon(String icon) { this.icon = icon; }

    public Float getTemperature() { return this.temperature; }
    public void setTemperature(Float temperature) { this.temperature = temperature; }

    public Float getLatitude() { return this.latitude; }
    public void setLatitude(Float latitude) { this.latitude = latitude; }

    public Float getLongitude() { return this.longitude; }
    public void setLongitude(Float longitude) { this.longitude = longitude; }

    public static Weather create(JsonObject o) {
        final Weather w = new Weather();
        w.setMain(o.getString("main"));
        w.setDescription(o.getString("description"));
        w.setIcon(o.getString("icon"));
        if (o.containsKey("cityName"))
            w.setCityName(o.getString("cityName"));
        if (o.containsKey("temperature")) {
            double temp = o.getJsonNumber("temperature").doubleValue();
            w.setTemperature((float)temp);
        }
        return w;
    }
    public static Weather create(String jsonString) {
        try (InputStream is = new ByteArrayInputStream(jsonString.getBytes())) {
            final JsonReader reader = Json.createReader(is);
            return create(reader.readObject());
        } catch (Exception ex) { }

        // Need to handle error
        return new Weather();
    }

    @Override
    public String toString() {
        return "cityName: %s, main: %s, description: %s, icon: %s, temperature: %f"
                .formatted(cityName, main, description, icon, temperature);
    }

    public JsonObject toJson() {
        return Json.createObjectBuilder()
            .add("cityName", cityName)
            .add("main", main)
            .add("description", description)
            .add("icon", icon)
            .add("temperature", temperature)
            .build();
    }
    
}
