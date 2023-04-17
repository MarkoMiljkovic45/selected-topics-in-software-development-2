package hr.fer.oprpp2.servlets.voting;

import hr.fer.oprpp2.servlets.voting.model.BandEntry;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@WebServlet(name="voting-xls", urlPatterns={"/glasanje-xls"})
public class VotingXlsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Path bandsPath = Path.of(req.getServletContext().getRealPath("/WEB-INF/glasanje-definicija.txt"));
        Path votesPath = Path.of(req.getServletContext().getRealPath("/WEB-INF/glasanje-rezultati.txt"));

        List<BandEntry> bands = BandEntry.load(bandsPath, votesPath);

        HSSFWorkbook hwb = new HSSFWorkbook();
        HSSFSheet sheet = hwb.createSheet();
        int rowIndex = 0;

        HSSFRow headerRow = sheet.createRow(rowIndex++);

        headerRow.createCell(0).setCellValue("ID");
        headerRow.createCell(1).setCellValue("Band Name");
        headerRow.createCell(2).setCellValue("URL");
        headerRow.createCell(3).setCellValue("Vote count");

        for (BandEntry band: bands) {
            HSSFRow row = sheet.createRow(rowIndex++);

            row.createCell(0).setCellValue(band.getId());
            row.createCell(1).setCellValue(band.getName());
            row.createCell(2).setCellValue(band.getRepresentativeUrl());
            row.createCell(3).setCellValue(band.getVoteCount());
        }

        resp.setHeader("Content-Disposition", "attachment; filename=\"rezultati.xls\"");
        resp.setStatus(HttpServletResponse.SC_OK);
        hwb.write(resp.getOutputStream());
        resp.getOutputStream().flush();
        hwb.close();
    }
}
