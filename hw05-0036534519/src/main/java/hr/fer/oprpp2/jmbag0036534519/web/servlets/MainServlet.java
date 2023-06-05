package hr.fer.oprpp2.jmbag0036534519.web.servlets;

import hr.fer.oprpp2.jmbag0036534519.dao.DAOProvider;
import hr.fer.oprpp2.jmbag0036534519.model.BlogUser;
import hr.fer.oprpp2.jmbag0036534519.model.forms.BlogUserLoginForm;
import hr.fer.oprpp2.jmbag0036534519.util.Util;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/servleti/main")
public class MainServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<BlogUser> users = DAOProvider.getDAO().getBlogUsers();
        req.setAttribute("users", users);
        req.getRequestDispatcher("/WEB-INF/pages/Main.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        BlogUserLoginForm loginForm = new BlogUserLoginForm();

        loginForm.fillFromHTTPRequest(req);
        loginForm.validate();

        if (loginForm.hasErrors()) {
            loginForm.setPassword("");
            req.setAttribute("loginForm", loginForm);
            doGet(req, resp);
            return;
        }

        BlogUser user = DAOProvider.getDAO().getBlogUserByNickname(loginForm.getNickname());

        if (user == null) {
            loginForm.setPassword("");
            req.setAttribute("loginForm", loginForm);
            req.setAttribute("error", "Invalid username or password");
            doGet(req, resp);
            return;
        }

        String userPasswordHash = user.getPasswordHash();
        String providedPasswordHash = Util.getSHA1Digest(loginForm.getPassword());

        if (!userPasswordHash.equals(providedPasswordHash)) {
            loginForm.setPassword("");
            req.setAttribute("loginForm", loginForm);
            req.setAttribute("error", "Invalid username or password");
            doGet(req, resp);
            return;
        }

        HttpSession session = req.getSession();
        session.setAttribute("userId", user.getId());
        session.setAttribute("userFirstName", user.getFirstName());
        session.setAttribute("userLastName", user.getLastName());
        session.setAttribute("userNickname", user.getNickname());
        session.setAttribute("userEmail", user.getEmail());

        resp.sendRedirect(req.getContextPath() + "/servleti/main");
    }
}
