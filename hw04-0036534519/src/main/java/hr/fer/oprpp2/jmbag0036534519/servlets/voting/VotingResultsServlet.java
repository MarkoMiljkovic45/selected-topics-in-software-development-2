package hr.fer.oprpp2.jmbag0036534519.servlets.voting;

import hr.fer.oprpp2.jmbag0036534519.dao.DAOProvider;
import hr.fer.oprpp2.jmbag0036534519.model.PollOption;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;

@WebServlet(name="voting-results", urlPatterns={"/servleti/glasanje-rezultati"})
public class VotingResultsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long pollId = Long.parseLong(req.getParameter("pollID"));
        List<PollOption> options = DAOProvider.getDao().getAllPollOptions(pollId);
        options.sort(Comparator.comparing(PollOption::getVotesCount).reversed());

        req.setAttribute("options", options);
        req.setAttribute("pollID", pollId);

        req.getRequestDispatcher("/WEB-INF/pages/glasanjeRez.jsp").forward(req, resp);
    }
}
