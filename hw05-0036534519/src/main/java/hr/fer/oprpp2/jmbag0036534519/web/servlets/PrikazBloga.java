package hr.fer.oprpp2.jmbag0036534519.web.servlets;

import hr.fer.oprpp2.jmbag0036534519.model.BlogEntry;
import hr.fer.oprpp2.jmbag0036534519.dao.DAOProvider;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/servleti/prikazi")
public class PrikazBloga extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String sID = req.getParameter("id");
		Long id = null;
		try {
			id = Long.valueOf(sID);
		} catch(Exception ignorable) {
		}
		if(id!=null) {
			BlogEntry blogEntry = DAOProvider.getDAO().getBlogEntry(id);
			if(blogEntry!=null) {
				req.setAttribute("blogEntry", blogEntry);
			}
		}
		req.getRequestDispatcher("/WEB-INF/pages/Prikaz.jsp").forward(req, resp);
	}
	
}