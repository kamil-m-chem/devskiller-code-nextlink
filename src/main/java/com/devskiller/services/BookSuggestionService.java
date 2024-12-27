package com.devskiller.services;

import com.devskiller.model.Author;
import com.devskiller.model.Book;
import com.devskiller.model.Reader;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

class BookSuggestionService {

	private static final int MINIMUM_RATING = 4;

	private final Set<Book> books;
	private final Set<Reader> readers;

	public BookSuggestionService(Set<Book> books, Set<Reader> readers) {
		this.books = books;
		this.readers = readers;
	}

	/**
	 * Suggest books meeting specific criteria:
	 * 1. Have a rating of four or higher.
	 * 2. Belong to one of the reader's favorite genres.
	 * 3. Are favorite books of at least one peer reader of the same age.
	 */
	Set<String> suggestBooks(Reader reader) {
		Set<Book> peerFavorites = getPeerFavouriteBooks(reader);
		return filterBooks(reader, peerFavorites, MINIMUM_RATING, null, false).stream()
				.map(Book::title)
				.collect(Collectors.toSet());
	}

	/**
	 * Suggest books meeting specific criteria, including a specified rating.
	 */
	Set<String> suggestBooks(Reader reader, int rating) {
		Set<Book> peerFavorites = getPeerFavouriteBooks(reader);
		return filterBooks(reader, peerFavorites, rating, null, true).stream()
				.map(Book::title)
				.collect(Collectors.toSet());
	}

	/**
	 * Suggest books meeting specific criteria, including a specified author.
	 */
	Set<String> suggestBooks(Reader reader, Author author) {
		Set<Book> peerFavorites = getPeerFavouriteBooks(reader);
		return filterBooks(reader, peerFavorites, MINIMUM_RATING, author, false).stream()
				.map(Book::title)
				.collect(Collectors.toSet());
	}

	private Set<Book> getPeerFavouriteBooks(Reader reader) {
		return readers.stream()
				.filter(r -> r.age() == reader.age())
				.filter(r -> !r.equals(reader))
				.flatMap(peer -> peer.favouriteBooks().stream())
				.collect(Collectors.toSet());
	}

	private Set<Book> filterBooks(Reader reader, Set<Book> peerFavorites, int rating, Author author, boolean exactRating) {
		return books.stream()
				.filter(book -> exactRating ? book.rating() == rating : book.rating() >= rating)
				.filter(book -> reader.favouriteGenres().contains(book.genre()))
				.filter(peerFavorites::contains)
				.filter(book -> author == null || book.author().equals(author))
				.collect(Collectors.toSet());
	}
}

