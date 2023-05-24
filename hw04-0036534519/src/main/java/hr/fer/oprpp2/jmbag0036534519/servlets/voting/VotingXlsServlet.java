package hr.fer.oprpp2.jmbag0036534519.servlets.voting;

import hr.fer.oprpp2.jmbag0036534519.dao.DAOProvider;
import hr.fer.oprpp2.jmbag0036534519.model.PollOption;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name="voting-xls", urlPatterns={"/servleti/glasanje-xls"})
public class VotingXlsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        long pollId = Long.parseLong(req.getParameter("pollID"));
        List<PollOption> options = DAOProvider.getDao().getAllPollOptions(pollId);

        HSSFWorkbook hwb = new HSSFWorkbook();
        HSSFSheet sheet = hwb.createSheet();
        int rowIndex = 0;

        HSSFRow headerRow = sheet.createRow(rowIndex++);

        headerRow.createCell(0).setCellValue("ID");
        headerRow.createCell(1).setCellValue("Option Name");
        headerRow.createCell(2).setCellValue("URL");
        headerRow.createCell(3).setCellValue("Vote count");

        for (PollOption option: options) {
            HSSFRow row = sheet.createRow(rowIndex++);

            row.createCell(0).setCellValue(option.getId());
            row.createCell(1).setCellValue(option.getOptionTitle());
            row.createCell(2).setCellValue(option.getOptionLink());
            row.createCell(3).setCellValue(option.getVotesCount());
        }

        resp.setHeader("Content-Disposition", "attachment; filename=\"rezultati.xls\"");
        resp.setStatus(HttpServletResponse.SC_OK);
        hwb.write(resp.getOutputStream());
        resp.getOutputStream().flush();
        hwb.close();
    }
}
