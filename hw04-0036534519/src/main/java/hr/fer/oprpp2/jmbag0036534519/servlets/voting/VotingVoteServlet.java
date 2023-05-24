package hr.fer.oprpp2.jmbag0036534519.servlets.voting;

import hr.fer.oprpp2.jmbag0036534519.dao.DAOProvider;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@WebServlet(name="voting-vote", urlPatterns={"/servleti/glasanje-glasaj"})
public class VotingVoteServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        try {
            long pollOptionId = Long.parseLong(req.getParameter("id"));
            DAOProvider.getDao().addVoteByPollOptionId(pollOptionId);
            resp.sendRedirect(req.getContextPath() + "/servleti/glasanje-rezultati?pollID=" + req.getParameter("pollID"));
        }
        catch (IllegalArgumentException |   NullPointerException ex) {
            req.setAttribute("err", "Unexpected arguments");
            req.getRequestDispatcher("/WEB-INF/pages/error.jsp").forward(req, resp);
        }
        catch (Exception ex) {
            req.setAttribute("err", ex.getMessage());
            req.getRequestDispatcher("/WEB-INF/pages/error.jsp").forward(req, resp);
        }
    }
}
