package hr.fer.oprpp2.servlets.voting;

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
import java.util.List;

@WebServlet(name="voting-vote", urlPatterns={"/glasanje-glasaj"})
public class VotingVoteServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        try {
            long voteId = Long.parseLong(req.getParameter("id"));
            Path filePath = Path.of(req.getServletContext().getRealPath("/WEB-INF/glasanje-rezultati.txt"));

            if (Files.exists(filePath)) {
                List<String> entries = Files.readAllLines(filePath);
                List<String> out     = new ArrayList<>();
                boolean fileContainsVoteId = false;

                for (String entry: entries) {
                    String[] columns = entry.split("\\t");
                    long entryId   = Long.parseLong(columns[0]);
                    int  voteCount = Integer.parseInt(columns[1]);

                    if (entryId == voteId) {
                        voteCount++;
                        fileContainsVoteId = true;
                    }

                    out.add(entryId + "\t" + voteCount);
                }

                if (!fileContainsVoteId) {
                    out.add(voteId + "\t1");
                }

                out.sort(String::compareTo);

                Files.write(
                        filePath,
                        out,
                        StandardCharsets.UTF_8,
                        StandardOpenOption.TRUNCATE_EXISTING
                );
            } else {
                Files.writeString(
                        filePath,
                        voteId + "\t1\n",
                        StandardCharsets.UTF_8,
                        StandardOpenOption.CREATE_NEW
                );
            }

            resp.sendRedirect(req.getContextPath() + "/glasanje-rezultati");
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
