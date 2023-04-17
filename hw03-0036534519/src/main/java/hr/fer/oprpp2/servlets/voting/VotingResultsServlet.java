package hr.fer.oprpp2.servlets.voting;

import hr.fer.oprpp2.servlets.voting.model.BandEntry;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

@WebServlet(name="voting-results", urlPatterns={"/glasanje-rezultati"})
public class VotingResultsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Path bandsPath = Path.of(req.getServletContext().getRealPath("/WEB-INF/glasanje-definicija.txt"));
        Path votesPath = Path.of(req.getServletContext().getRealPath("/WEB-INF/glasanje-rezultati.txt"));

        List<BandEntry> bands = BandEntry.load(bandsPath, votesPath);
        bands.sort(Comparator.comparing(BandEntry::getVoteCount).reversed());
        req.setAttribute("bands", bands);

        req.getRequestDispatcher("/WEB-INF/pages/glasanjeRez.jsp").forward(req, resp);
    }
}
