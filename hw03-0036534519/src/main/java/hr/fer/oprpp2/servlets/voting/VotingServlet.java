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

@WebServlet(name="voting", urlPatterns={"/glasanje"})
public class VotingServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Path bandsPath = Path.of(req.getServletContext().getRealPath("/WEB-INF/glasanje-definicija.txt"));

        List<BandEntry> bands = BandEntry.load(bandsPath);
        bands.sort(Comparator.comparing(BandEntry::getId));
        req.setAttribute("bands", bands);

        req.getRequestDispatcher("/WEB-INF/pages/glasanjeIndex.jsp").forward(req, resp);
    }
}
