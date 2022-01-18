package ssf.bookSearch.services;

import java.util.List;

import ssf.bookSearch.models.Book;

public interface BookService {
    
    public List<Book> search (String bookQuery);
}
