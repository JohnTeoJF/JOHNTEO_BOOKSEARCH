package ssf.bookSearch.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import ssf.bookSearch.BookSearchApplication;
import ssf.bookSearch.models.Book;

import static ssf.bookSearch.Constants.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service(BEAN_CACHING_BOOK_SERVICE)
public class CachingBookServiceImpl implements BookService {

    private final Logger logger = Logger.getLogger(BookSearchApplication.class.getName());

    @Autowired
    @Qualifier(BEAN_BOOK_SERVICE)
    private BookServiceImpl delegate;

    @Autowired
    private BookCacheService cacheSvc;
    
    public List<Book> search(String bookTitle) {

        logger.info(">>>> Using CachingBookServiceImpl");

        Optional<List<Book>> opt = cacheSvc.get(bookTitle);

        List<Book> Book = Collections.emptyList();

        if (opt.isPresent()) {
            logger.info("Cache hit for %s".formatted(bookTitle));
            Book = opt.get();
        } else
            try {
                Book = delegate.search(bookTitle);
                if (Book.size() > 0)
                    cacheSvc.save(bookTitle, Book);
            } catch (Exception ex) {
                logger.log(Level.WARNING, "Warning: %s".formatted(ex.getMessage()));
            }

        return Book;
    }
}
