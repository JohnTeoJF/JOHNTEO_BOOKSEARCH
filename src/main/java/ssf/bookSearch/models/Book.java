package ssf.bookSearch.models;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;


public class Book {

    private String BookName;
    private String works_id;
    
    public String getKey() { return works_id; }
    public void setKey(String works_id) { this.works_id = works_id; }


    public String getBookName() { return this.BookName; }
    public void setBookName(String BookName) { this.BookName = BookName; }

    public static Book create(JsonObject o) {
        final Book w = new Book();
        w.setKey(o.getString("key"));
        w.setBookName(o.getString("title"));

        return w;
    }


// Misc methods

    public static Book create(String jsonString) {
        try (InputStream is = new ByteArrayInputStream(jsonString.getBytes())) {
            final JsonReader reader = Json.createReader(is);
            return create(reader.readObject());
        } catch (Exception ex) { }

        // Need to handle error
        return new Book();
    }

    @Override
    public String toString() {
        return "key: %s ,title: %s"
                .formatted(BookName, works_id);
    }

    public JsonObject toJson() {
        return Json.createObjectBuilder()
        .add("key", works_id)
        .add("title", BookName)
            .build();
    }
    
    
}
