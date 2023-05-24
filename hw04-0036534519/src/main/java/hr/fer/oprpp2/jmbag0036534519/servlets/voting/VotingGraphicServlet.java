package hr.fer.oprpp2.jmbag0036534519.servlets.voting;

import hr.fer.oprpp2.jmbag0036534519.dao.DAOProvider;
import hr.fer.oprpp2.jmbag0036534519.model.Poll;
import hr.fer.oprpp2.jmbag0036534519.model.PollOption;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

@WebServlet(name="voting-graphic", urlPatterns={"/servleti/glasanje-grafika"})
public class VotingGraphicServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("image/png");
        resp.setStatus(HttpServletResponse.SC_OK);

        OutputStream outputStream = resp.getOutputStream();

        long pollId = Long.parseLong(req.getParameter("pollID"));
        JFreeChart chart = getChart(pollId);
        int width = 400;
        int height = 400;
        ChartUtilities.writeChartAsPNG(outputStream, chart, width, height);

        outputStream.flush();
    }

    private static JFreeChart getChart(long pollId) {
        DefaultPieDataset dataset = new DefaultPieDataset();

        Poll poll = DAOProvider.getDao().getPollById(pollId);
        List<PollOption> pollOptions = DAOProvider.getDao().getAllPollOptions(pollId);

        for (PollOption pollOption: pollOptions) {
            if (pollOption.getVotesCount() > 0) {
                dataset.setValue(pollOption.getOptionTitle(), pollOption.getVotesCount());
            }
        }

        boolean legend = true;
        boolean tooltips = false;
        boolean urls = false;

        JFreeChart chart = ChartFactory.createPieChart(poll.getTitle(), dataset, legend, tooltips, urls);

        chart.setBorderPaint(Color.GREEN);
        chart.setBorderStroke(new BasicStroke(2.0f));
        chart.setBorderVisible(true);

        return chart;
    }
}
