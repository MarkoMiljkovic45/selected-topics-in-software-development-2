package hr.fer.oprpp2.jmbag0036534519.dao.jpa;

import hr.fer.oprpp2.jmbag0036534519.dao.DAO;
import hr.fer.oprpp2.jmbag0036534519.dao.DAOException;
import hr.fer.oprpp2.jmbag0036534519.model.BlogComment;
import hr.fer.oprpp2.jmbag0036534519.model.BlogEntry;
import hr.fer.oprpp2.jmbag0036534519.model.BlogUser;

import javax.persistence.NoResultException;
import java.util.List;

public class JPADAOImpl implements DAO {

	@Override
	public BlogEntry getBlogEntry(Long id) throws DAOException {
		return JPAEMProvider.getEntityManager().find(BlogEntry.class, id);
	}

	@Override
	public BlogUser getBlogUserByNickname(String nickname) throws DAOException {
		try {
			return (BlogUser) JPAEMProvider.getEntityManager()
					.createNamedQuery("findBlogUserByNickname")
					.setParameter("userNickname", nickname)
					.getSingleResult();
		}
		catch (NoResultException noRes) {
			return null;
		}
		catch (Exception e) {
			throw new DAOException(e.getMessage());
		}
	}

	@Override
	public void persistBlogUser(BlogUser user) throws DAOException {
		try {
			JPAEMProvider.getEntityManager().persist(user);
		}
		catch (Exception e) {
			throw new DAOException(e.getMessage());
		}
	}

	@Override
	public List<BlogUser> getBlogUsers() throws DAOException {
		try {
			return JPAEMProvider.getEntityManager()
					.createNamedQuery("getAllBlogUsers")
					.getResultList();
		}
		catch (Exception e) {
			throw new DAOException(e.getMessage());
		}
	}

	@Override
	public void persistBlogComment(BlogComment comment) throws DAOException {
		try {
			JPAEMProvider.getEntityManager().persist(comment);
		}
		catch (Exception e) {
			throw new DAOException(e.getMessage());
		}
	}

	@Override
	public void persistBlogEntry(BlogEntry entry) throws DAOException {
		try {
			JPAEMProvider.getEntityManager().persist(entry);
		}
		catch (Exception e) {
			throw new DAOException(e.getMessage());
		}
	}

	@Override
	public void removeBlogEntry(BlogEntry entry) throws DAOException {
		try {
			JPAEMProvider.getEntityManager().remove(entry);
		}
		catch (Exception e) {
			throw new DAOException(e.getMessage());
		}
	}
}