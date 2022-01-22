package ssf.bookSearch.services;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

import java.util.logging.Level;
import java.util.logging.Logger;

import ssf.bookSearch.models.Book;

import static ssf.bookSearch.Constants.*;

@Service
public class BookService {

    private final Logger logger = Logger.getLogger(BookService.class.getName());
 
    public List<Book> search (String searchTerm) throws IOException {

        final String url = UriComponentsBuilder
                .fromUriString(URL_API_SEARCH)
				.queryParam("title", searchTerm.trim().replace(" ", "+"))
				.queryParam("fields", "key,title")
                .queryParam("limit", MAX_RESULTS)
                .toUriString();

                logger.info("QUERY URL >>> %s".formatted(url));
        
        final RequestEntity<Void> req = RequestEntity.get(url).build();
        final RestTemplate template = new RestTemplate();
        final ResponseEntity<String> resp = template.exchange(req, String.class);

        logger.log(Level.INFO, resp.getStatusCode().toString());
        logger.log(Level.INFO, resp.getHeaders().toString());

        if (resp.getStatusCode() != HttpStatus.OK)
            throw new IllegalArgumentException(
                "Error: status code %s".formatted(resp.getStatusCode().toString())
            );

        final String body = resp.getBody();

        //logger.log(Level.INFO, "payload: %s".formatted(body));

        //Create list of books
        final List<Book> list = new LinkedList<>();

		try (InputStream is = new ByteArrayInputStream(body.getBytes("UTF-8"))) {

			final JsonReader reader = Json.createReader(is);
            final JsonObject temp = reader.readObject();
			final JsonArray result = temp.getJsonArray("docs");

			for (Integer i = 0; i < result.size(); i++) {
				JsonObject obj = result.getJsonObject(i);
				Book b = new Book();

				b.setKey(obj.getString("key").replace("/works/", ""));
                
                    logger.info("KEY >>> %s".formatted(b.getKey()));

				b.setTitle(obj.getString("title"));

                    logger.info("TITLE >>> %s".formatted(b.getTitle()));

				list.add(b);
                
			}
            return list;

        } catch (IOException e) {
			logger.log(Level.WARNING, e.getMessage(), e);
			throw e;
		}
        
    }

	public Book getBook(String key) throws IOException {

		final String bookUrl = URL_API_WORKS+key+".json";

        logger.info("BOOK URL IS >>> " + bookUrl);

        final RequestEntity<Void> req = RequestEntity.get(bookUrl).build();
		final RestTemplate template = new RestTemplate();
        final ResponseEntity<String> resp = template.exchange(req, String.class);

		try {
			return Book.toBook(resp.getBody());
		} catch (IOException ex) {
			logger.log(Level.WARNING, ex.getMessage(), ex);
			throw ex;
		}
	}


}