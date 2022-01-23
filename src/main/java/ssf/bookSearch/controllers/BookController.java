package ssf.bookSearch.controllers;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import ssf.bookSearch.models.Book;
import ssf.bookSearch.repositories.BookRepository;
import ssf.bookSearch.services.BookService;

import java.util.logging.Logger;

@Controller
@RequestMapping
public class BookController {

	@Autowired
	private BookService bookSvc;

	@Autowired
	private BookRepository bookRepo;

	private final Logger logger = Logger.getLogger(BookController.class.getName());

	
	@GetMapping (path = "/book/{key}")
	public String getBook(@PathVariable String key, Model model) {
		
		logger.info("Entering getbook method");
        logger.info("KEY >>>" + key + "<<<");

		Optional<Book> opt = bookRepo.get(key);
		Book book = null;

		//Check if book data is in cache
		if (opt.isPresent()) {

            logger.info("Reading from cache : %s".formatted(opt.get().toString()));
			book = opt.get();
			book.setCached(true);

		} else
			try {
				//Else retrieve data and save to cache
				
				logger.info("Nothing in cache, retriving from API");
				book = bookSvc.getBook(key);
				bookRepo.save(book);
			} catch (IOException ex) {
				model.addAttribute("error", ex.getMessage());
			}

		model.addAttribute("book", book);

		return "details";
	}
}
