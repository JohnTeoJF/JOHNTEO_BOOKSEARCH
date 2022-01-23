package ssf.bookSearch.models;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonReader;

import static ssf.bookSearch.Constants.*;


public class Book {

    private String key;
	private String coverURL = "/default_cover.png";
	private String title;
	private String description = "";
	private String excerpt = "";
	private Boolean cached = false;

	public String getKey() { return key; }
	public void setKey(String key) { this.key = key; }

	public String getCoverURL() { return coverURL; }
	public void setCoverURL(String coverURL) { this.coverURL = coverURL; }

	public String getTitle() { return title; }
	public void setTitle(String title) { this.title = title; }

	public String getDescription() { return description; }
	public void setDescription(String description) { this.description = description; }

	public String getExcerpt() { return excerpt; }
	public void setExcerpt(String excerpt) { this.excerpt = excerpt; }

	public Boolean getCached() { return cached; }
	public void setCached(Boolean cached) { this.cached = cached; }

    public static  Book toBook(String json) throws IOException {
		try (InputStream is = new ByteArrayInputStream(json.getBytes("UTF-8"))) {
			JsonReader reader = Json.createReader(is);
			JsonObject o = reader.readObject();
			Book b = new Book();
            
            b.setKey(o.getString("key").replace("/works/", ""));
			b.setTitle(o.getString("title"));

			//Try for edge case of cover located in a different location
			if(o.get("covers") != null){
				b.setCoverURL(URL_COVER_IMAGE.formatted(o.getJsonArray("covers").getInt(0)));
			}else if(o.get("cover") != null){
				b.setCoverURL(o.getString("cover"));
			}


            //Try for edge case where description is located in a different location
            try{
			    b.setDescription(o.getJsonObject("description").getString("value"));
            }catch (Exception e){
                b.setDescription(o.getString("description",""));
            }

            //Check for edge case where excerpt is located in a different location
            if((o.get("excerpts") != null) && (o.get("excerpts").toString().length() >=0 )){
                b.setExcerpt(
                    o.getJsonArray("excerpts").getJsonObject(0).getString("excerpt"));

            }else if((o.get("excerpt") != null) && (o.get("excerpt").toString().length() >=0 ) ){
                b.setExcerpt(
                    o.getString("excerpt"));
            }

			return b;
		}
	}


    @Override
	public String toString() {
		return toJson().toString();
	}
    
	public JsonObject toJson() {
		final JsonObjectBuilder objBuilder = Json.createObjectBuilder()
			.add("key", key)
			.add("coverURL", coverURL)
			.add("title", title)
			.add("description", description)
			.add("excerpt", excerpt);

		return objBuilder.build();
	}

}
