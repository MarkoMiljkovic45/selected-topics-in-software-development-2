package hr.fer.oprpp2.jmbag0036534519.servlets.voting;

import hr.fer.oprpp2.jmbag0036534519.dao.DAOProvider;
import hr.fer.oprpp2.jmbag0036534519.model.Poll;
import hr.fer.oprpp2.jmbag0036534519.model.PollOption;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;

@WebServlet(name="voting", urlPatterns={"/servleti/glasanje"})
public class VotingServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long pollId = Long.parseLong(req.getParameter("pollID"));

        Poll poll = DAOProvider.getDao().getPollById(pollId);
        List<PollOption> options = DAOProvider.getDao().getAllPollOptions(pollId);
        options.sort(Comparator.comparing(PollOption::getId));

        req.setAttribute("options", options);
        req.setAttribute("pollTitle", poll.getTitle());
        req.setAttribute("pollMessage", poll.getMessage());
        req.setAttribute("pollID", pollId);

        req.getRequestDispatcher("/WEB-INF/pages/glasanjeIndex.jsp").forward(req, resp);
    }
}
