package hr.fer.oprpp2.servlets.util;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

@WebServlet(name="setcolor", urlPatterns={"/setcolor"})
public class SetColorServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pickedBgCol = req.getParameter("pickedBgCol");
        req.getSession().setAttribute("pickedBgCol", Objects.requireNonNullElse(pickedBgCol, "white"));
        resp.sendRedirect(req.getContextPath());
    }
}
