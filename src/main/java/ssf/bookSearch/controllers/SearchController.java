package ssf.bookSearch.controllers;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ssf.bookSearch.BookSearchApplication;
import ssf.bookSearch.models.Book;
import ssf.bookSearch.services.BookService;

import static ssf.bookSearch.Constants.*;

@Controller
@RequestMapping(path = "/book")
public class SearchController {

    private final Logger logger = Logger.getLogger(BookSearchApplication.class.getName());

    @Autowired
    @Qualifier(BEAN_CACHING_BOOK_SERVICE)
    private BookService bookSvc;

    @GetMapping
    public String search(@RequestParam(required = true) String bookTitle, Model model) {

        logger.log(Level.INFO, "title: %s".formatted(bookTitle));

        List<Book> bookList = Collections.emptyList();

        try {
            bookList = bookSvc.search(bookTitle);
        } catch (Exception ex) {
            logger.log(Level.WARNING, "Warning: %s".formatted(ex.getMessage()));
        }

        
        model.addAttribute("bookList", bookList);
        model.addAttribute("title", bookTitle);

        return "book";
    }
    
}
