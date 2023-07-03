package hr.fer.oprpp2.jmbag0036534519.dao;

import hr.fer.oprpp2.jmbag0036534519.model.BlogComment;
import hr.fer.oprpp2.jmbag0036534519.model.BlogEntry;
import hr.fer.oprpp2.jmbag0036534519.model.BlogUser;

import java.util.List;

public interface DAO {

	/**
	 * Dohvaća entry sa zadanim <code>id</code>-em. Ako takav entry ne postoji,
	 * vraća <code>null</code>.
	 * 
	 * @param id ključ zapisa
	 * @return entry ili <code>null</code> ako entry ne postoji
	 * @throws DAOException ako dođe do pogreške pri dohvatu podataka
	 */
	BlogEntry getBlogEntry(Long id) throws DAOException;

	/**
	 * Finds BlogUser by nickname
	 *
	 * @param nickname BlogUser nickname
	 * @return Blog user if he exists, <code>null</code> otherwise
	 * @throws DAOException if an exception occurred while retrieving the user
	 */
	BlogUser getBlogUserByNickname(String nickname) throws DAOException;

	/**
	 * Persists the user in the database
	 *
	 * @param user to be persisted
	 * @throws DAOException if an error occurred
	 */
	void persistBlogUser(BlogUser user) throws DAOException;

	/**
	 * Returns a list of all the Blog Users
	 *
	 * @return list of all blog users
	 * @throws DAOException if an error occurred
	 */
	List<BlogUser> getBlogUsers() throws DAOException;

	/**
	 * Persists a comment in the database
	 *
	 * @param comment to be persisted
	 * @throws DAOException if an error occurred
	 */
	void persistBlogComment(BlogComment comment) throws DAOException;

	/**
	 * Persists a blog entry in the database
	 *
	 * @param entry to be persisted
	 * @throws DAOException if an error occurred
	 */
	void persistBlogEntry(BlogEntry entry) throws DAOException;

	/**
	 * Removes a blog entry from the database
	 *
	 * @param entry to be deleted
	 * @throws DAOException if an error occurred
	 */
	void removeBlogEntry(BlogEntry entry) throws DAOException;
}