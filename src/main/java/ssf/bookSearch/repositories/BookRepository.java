package ssf.bookSearch.repositories;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import jakarta.json.JsonObject;

import java.util.logging.Level;
import java.util.logging.Logger;

import ssf.bookSearch.models.Book;


@Repository 
public class BookRepository {

	private final Logger logger = Logger.getLogger(BookRepository.class.getName());
    
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	public Optional<Book> get(String key) {
		Object book = redisTemplate.opsForValue().get(key);
		try {
			if (null != book)
				return Optional.of(Book.toBook(book.toString()));
		} catch (IOException ex) {
			logger.log(Level.WARNING, ex.getMessage(), ex);
		}
		return Optional.empty();
	}

    //Save data to cache for 10 minutes
	public void save(Book b) {
		JsonObject o = b.toJson();
		redisTemplate.opsForValue().set(b.getKey(), o.toString(), 10L, TimeUnit.MINUTES);
	}

    
}
