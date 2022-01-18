package ssf.bookSearch.services;

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

import ssf.bookSearch.BookSearchApplication;
import ssf.bookSearch.models.Book;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

import static ssf.bookSearch.Constants.*;

@Service(BEAN_BOOK_SERVICE)
public class BookServiceImpl implements BookService {

    private final Logger logger = Logger.getLogger(BookSearchApplication.class.getName());

    public List<Book> search (String bookQuery) {

        final String url = UriComponentsBuilder
                .fromUriString(URL_BOOK)
                .queryParam("q", bookQuery.trim().replace(" ", "+"))
                .queryParam("limit","20")
                .toUriString();

                logger.info("QUERY URL IS : >>>>>>>> %s".formatted(url));
        
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
            final JsonObject temp = reader.readObject();
            final JsonArray result = temp.getJsonArray("docs");

            return result.stream()
                .map(v -> (JsonObject)v)
                .map(Book::create)
                .collect(Collectors.toList());
          
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return Collections.EMPTY_LIST;
    }
}
