package hr.fer.oprpp2.jmbag0036534519.dao;

import hr.fer.oprpp2.jmbag0036534519.model.BlogEntry;
import hr.fer.oprpp2.jmbag0036534519.model.BlogUser;

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
	 * @param nick BlogUser nickname
	 * @return Blog user if he exists, <code>null</code> otherwise
	 * @throws DAOException if an exception occurred while retrieving the user
	 */
	BlogUser getBlogUserByNick(String nick) throws DAOException;

	/**
	 * Persists the user in the database
	 *
	 * @param user to be persisted
	 * @throws DAOException if an error occurred
	 */
	void persistBlogUser(BlogUser user) throws DAOException;
}