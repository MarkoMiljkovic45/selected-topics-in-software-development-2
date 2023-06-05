package hr.fer.oprpp2.jmbag0036534519.web.servlets;

import hr.fer.oprpp2.jmbag0036534519.dao.DAO;
import hr.fer.oprpp2.jmbag0036534519.dao.DAOProvider;
import hr.fer.oprpp2.jmbag0036534519.model.BlogComment;
import hr.fer.oprpp2.jmbag0036534519.model.BlogEntry;
import hr.fer.oprpp2.jmbag0036534519.model.BlogUser;
import hr.fer.oprpp2.jmbag0036534519.model.forms.BlogEntryForm;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Date;

@WebServlet("/servleti/author/*")
public class AuthorServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null) {
            resp.sendError(404, "Page not found");
            return;
        }

        String trimmedInfo = pathInfo.substring(1);
        int splitIndex = trimmedInfo.indexOf('/');

        if (splitIndex != -1) {
            String action = trimmedInfo.substring(splitIndex + 1);

            switch (action) {
                case "new"  -> handleGetNewBlogEntry(req, resp);
                case "edit" -> handleGetEditBlogEntry(req, resp);
                default -> handleGetBlogEntry(req, resp, action);
            }
            return;
        }

        handleGetAuthor(req, resp, trimmedInfo);
    }

    protected void handleGetAuthor(HttpServletRequest req, HttpServletResponse resp, String userNickname) throws ServletException, IOException {
        BlogUser author = DAOProvider.getDAO().getBlogUserByNickname(userNickname);
        req.setAttribute("author", author);
        req.getRequestDispatcher("/WEB-INF/pages/Author.jsp").forward(req, resp);
    }

    protected void handleGetBlogEntry(HttpServletRequest req, HttpServletResponse resp, String blogEntryId) throws IOException, ServletException {
        try {
            long id = Long.parseLong(blogEntryId);

            BlogEntry blogEntry = DAOProvider.getDAO().getBlogEntry(id);
            req.setAttribute("blogEntry", blogEntry);
            req.getRequestDispatcher("/WEB-INF/pages/BlogEntry.jsp").forward(req, resp);
        }
        catch (NumberFormatException nfe) {
            resp.sendError(404, "Blog entry not found");
        }
    }

    protected void handleGetNewBlogEntry(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("action", "Create");
        req.getRequestDispatcher("/WEB-INF/pages/BlogEntryForm.jsp").forward(req, resp);
    }

    protected void handleGetEditBlogEntry(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("action", "Edit");

        try {
            long blogEntryId = Long.parseLong(req.getParameter("id"));
            BlogEntry blogEntry = DAOProvider.getDAO().getBlogEntry(blogEntryId);

            if (blogEntry == null) {
                req.setAttribute("error", "Blog entry not found");
                req.getRequestDispatcher("/WEB-INF/pages/BlogEntryForm.jsp").forward(req, resp);
                return;
            }

            HttpSession session = req.getSession();

            if (session.getAttribute("userId") == null) {
                req.setAttribute("error", "You must be logged in to access this site");
                req.getRequestDispatcher("/WEB-INF/pages/BlogEntryForm.jsp").forward(req, resp);
                return;
            }

            long currentUserId = (Long) session.getAttribute("userId");
            long creatorId = blogEntry.getCreator().getId();

            if (creatorId != currentUserId) {
                req.setAttribute("error", "You don't have the permission to edit this blog entry");
                req.getRequestDispatcher("/WEB-INF/pages/BlogEntryForm.jsp").forward(req, resp);
                return;
            }

            BlogEntryForm blogForm = new BlogEntryForm();
            blogForm.setTitle(blogEntry.getTitle());
            blogForm.setText(blogEntry.getText());

            req.setAttribute("blogForm", blogForm);
            req.getRequestDispatcher("/WEB-INF/pages/BlogEntryForm.jsp").forward(req, resp);
        }
        catch (NumberFormatException nfe) {
            req.setAttribute("error", "Invalid Blog Entry ID");
            req.getRequestDispatcher("/WEB-INF/pages/BlogEntryForm.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null) {
            resp.sendError(404, "Page not found");
            return;
        }

        String trimmedInfo = pathInfo.substring(1);
        int splitIndex = trimmedInfo.indexOf('/');

        if (splitIndex != -1) {
            String action = trimmedInfo.substring(splitIndex + 1);

            switch (action) {
                case "new"  -> handlePostNewBlogEntry(req, resp);
                case "edit" -> handlePostEditBlogEntry(req, resp);
                default -> handlePostComment(req, resp, action);
            }
        }
    }

    protected void handlePostComment(HttpServletRequest req, HttpServletResponse resp, String blogEntryId) throws IOException {
        try {
            long id = Long.parseLong(blogEntryId);

            BlogComment comment = new BlogComment();

            comment.setBlogEntry(DAOProvider.getDAO().getBlogEntry(id));
            comment.setUsersEMail(req.getSession().getAttribute("userEmail").toString());
            comment.setMessage(req.getParameter("message"));
            comment.setPostedOn(new Date());

            DAOProvider.getDAO().persistBlogComment(comment);
            resp.sendRedirect(req.getContextPath() + "/servleti/author" + req.getPathInfo());
        }
        catch (NumberFormatException nfe) {
            resp.sendError(404, "Blog entry not found");
        }
    }

    protected void handlePostNewBlogEntry(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        BlogEntryForm blogForm = new BlogEntryForm();

        blogForm.fillFromHTTPRequest(req);
        blogForm.validate();

        if (blogForm.hasErrors()) {
            req.setAttribute("blogForm", blogForm);
            req.setAttribute("action", "Create");
            req.getRequestDispatcher("/WEB-INF/pages/BlogEntryForm.jsp").forward(req, resp);
            return;
        }

        Object currentUserNickname = req.getSession().getAttribute("userNickname");
        if (currentUserNickname == null) {
            req.setAttribute("error", "You must be logged in to access this site");
            req.getRequestDispatcher("/WEB-INF/pages/BlogEntryForm.jsp").forward(req, resp);
            return;
        }

        BlogEntry blogEntry = new BlogEntry();

        blogEntry.setCreatedAt(new Date());
        blogEntry.setLastModifiedAt(new Date());
        blogForm.fillBlogEntry(blogEntry);
        blogEntry.setCreator(DAOProvider.getDAO().getBlogUserByNickname(currentUserNickname.toString()));

        DAOProvider.getDAO().persistBlogEntry(blogEntry);

        resp.sendRedirect(req.getContextPath() + "/servleti/author/" + currentUserNickname);
    }

    protected void handlePostEditBlogEntry(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            long blogEntryId = Long.parseLong(req.getParameter("id"));

            BlogEntryForm blogForm = new BlogEntryForm();

            blogForm.fillFromHTTPRequest(req);
            blogForm.validate();

            if (blogForm.hasErrors()) {
                req.setAttribute("action", "Edit");
                req.setAttribute("blogForm", blogForm);
                req.getRequestDispatcher("/WEB-INF/pages/BlogEntryForm.jsp").forward(req, resp);
                return;
            }

            Object currentUserNickname = req.getSession().getAttribute("userNickname");
            if (currentUserNickname == null) {
                req.setAttribute("error", "You must be logged in to access this site");
                req.getRequestDispatcher("/WEB-INF/pages/BlogEntryForm.jsp").forward(req, resp);
                return;
            }

            BlogEntry blogEntry = DAOProvider.getDAO().getBlogEntry(blogEntryId);
            DAOProvider.getDAO().removeBlogEntry(blogEntry);

            blogEntry.setLastModifiedAt(new Date());
            blogForm.fillBlogEntry(blogEntry);

            DAOProvider.getDAO().persistBlogEntry(blogEntry);

            resp.sendRedirect(req.getContextPath() + "/servleti/author/" + currentUserNickname);
        }
         catch (NumberFormatException nfe) {
             req.setAttribute("error", "Invalid Blog Entry ID");
             req.getRequestDispatcher("/WEB-INF/pages/BlogEntryForm.jsp").forward(req, resp);
         }
    }
}
