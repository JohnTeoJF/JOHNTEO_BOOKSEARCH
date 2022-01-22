package ssf.bookSearch.controllers;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ssf.bookSearch.models.Book;
import ssf.bookSearch.services.BookService;

import java.util.logging.Level;
import java.util.logging.Logger;

@Controller
@RequestMapping(path = "/search")
public class SearchController {
    
    private final Logger logger = Logger.getLogger(SearchController.class.getName());

	@Autowired
	private BookService bookSvc;

    @GetMapping(produces=MediaType.TEXT_HTML_VALUE)
	public String search(@RequestParam(required=true) String title, Model model) {
    
        logger.log(Level.INFO, "User searched for >> %s".formatted(title));

		List<Book> bookList = Collections.emptyList();

		try {
			bookList = bookSvc.search(title);
		} catch (IOException ex) {
			model.addAttribute("error", ex.getMessage());
		}

		model.addAttribute("title", title);
		model.addAttribute("bookList", bookList);

		return "searchResults";
	}

}
