package hr.fer.oprpp2.jmbag0036534519.web.servlets;

import hr.fer.oprpp2.jmbag0036534519.dao.DAOProvider;
import hr.fer.oprpp2.jmbag0036534519.model.BlogUser;
import hr.fer.oprpp2.jmbag0036534519.model.forms.BlogUserRegisterForm;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/servleti/register")
public class RegisterServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/pages/Register.jsp").forward(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        BlogUserRegisterForm registerForm = new BlogUserRegisterForm();

        req.setCharacterEncoding("UTF-8");

        registerForm.fillFromHTTPRequest(req);
        registerForm.validate();

        if (registerForm.hasErrors()) {
            req.setAttribute("registerForm", registerForm);
            doGet(req, resp);
            return;
        }

        BlogUser user = DAOProvider.getDAO().getBlogUserByNickname(registerForm.getNickname());

        if (user != null) {
            registerForm.setError("nickname", "Nickname already in use");
            req.setAttribute("registerForm", registerForm);
            doGet(req, resp);
            return;
        }

        BlogUser newUser = new BlogUser();
        registerForm.fillUser(newUser);

        DAOProvider.getDAO().persistBlogUser(newUser);
        newUser.setId(DAOProvider.getDAO().getBlogUserByNickname(newUser.getNickname()).getId());

        HttpSession session = req.getSession();
        session.setAttribute("userId", newUser.getId());
        session.setAttribute("userFirstName", newUser.getFirstName());
        session.setAttribute("userLastName", newUser.getLastName());
        session.setAttribute("userNickname", newUser.getNickname());
        session.setAttribute("userEmail", newUser.getEmail());

        resp.sendRedirect(req.getContextPath() + "/servleti/main");
    }
}
