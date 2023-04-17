package hr.fer.oprpp2.servlets.voting;

import hr.fer.oprpp2.servlets.voting.model.BandEntry;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.List;

@WebServlet(name="voting-graphic", urlPatterns={"/glasanje-grafika"})
public class VotingGraphicServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("image/png");
        resp.setStatus(HttpServletResponse.SC_OK);

        OutputStream outputStream = resp.getOutputStream();

        Path bandsPath = Path.of(req.getServletContext().getRealPath("/WEB-INF/glasanje-definicija.txt"));
        Path votesPath = Path.of(req.getServletContext().getRealPath("/WEB-INF/glasanje-rezultati.txt"));

        JFreeChart chart = getChart(bandsPath, votesPath);
        int width = 400;
        int height = 400;
        ChartUtilities.writeChartAsPNG(outputStream, chart, width, height);

        outputStream.flush();
    }

    private static JFreeChart getChart(Path bandsPath, Path votesPath) {
        DefaultPieDataset dataset = new DefaultPieDataset();

        List<BandEntry> bands = BandEntry.load(bandsPath, votesPath);

        for (BandEntry band: bands) {
            if (band.getVoteCount() > 0) {
                dataset.setValue(band.getName(), band.getVoteCount());
            }
        }

        boolean legend = true;
        boolean tooltips = false;
        boolean urls = false;

        JFreeChart chart = ChartFactory.createPieChart("Band votes", dataset, legend, tooltips, urls);

        chart.setBorderPaint(Color.GREEN);
        chart.setBorderStroke(new BasicStroke(2.0f));
        chart.setBorderVisible(true);

        return chart;
    }
}
