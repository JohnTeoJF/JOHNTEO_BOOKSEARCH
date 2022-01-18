package ssf.bookSearch.services;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ssf.bookSearch.BookSearchApplication;
import ssf.bookSearch.models.Book;
import ssf.bookSearch.repositories.BookRepository;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

@Service
public class BookCacheService {

    private final Logger logger = Logger.getLogger(BookSearchApplication.class.getName());

    @Autowired
    private BookRepository bookRepo;

    public void save(String bookName, List<Book> book) {
        JsonArrayBuilder arrBuilder = Json.createArrayBuilder();
        book.stream()   
            .forEach(v -> arrBuilder.add(v.toJson()));
        bookRepo.save(bookName, arrBuilder.build().toString());
    }

    public Optional<List<Book>> get(String bookName) {
        Optional<String> opt = bookRepo.get(bookName);
        if (opt.isEmpty())
            return Optional.empty();

        JsonArray jsonArray = parseJsonArray(opt.get());
        List<Book> book = jsonArray.stream()
            .map(v -> (JsonObject)v)
            .map(Book::create)
            .collect(Collectors.toList());
        return Optional.of(book);
    }

    private JsonArray parseJsonArray(String s) {
        try (InputStream is = new ByteArrayInputStream(s.getBytes())) {
            JsonReader reader = Json.createReader(is);
            return reader.readArray();
        } catch (Exception ex) {
            logger.log(Level.WARNING, "Parsing", ex);
        }

        // Need to handle error
        return Json.createArrayBuilder().build();
    }
}
