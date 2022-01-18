package ssf.bookSearch.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import static ssf.bookSearch.Constants.*;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Repository
public class BookRepository {

    @Autowired
    @Qualifier(BEAN_BOOK_CACHE)
    private RedisTemplate<String, String> template;

    public void save(String bookTitle, String value) {
        template.opsForValue().set(normalize(bookTitle), value, 10L, TimeUnit.MINUTES);
    }

    public Optional<String> get(String bookTitle) {
        String value = template.opsForValue().get(normalize(bookTitle));
        return Optional.ofNullable(value);
    }

    private String normalize(String k) {
        return k.trim().toLowerCase();
    }
    
}
