package hr.fer.oprpp2.servlets.util;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name="powers", urlPatterns={"/powers"})
public class PowersServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        try (HSSFWorkbook hwb = new HSSFWorkbook()) {
            int a = Integer.parseInt(req.getParameter("a"));
            int b = Integer.parseInt(req.getParameter("b"));
            int n = Integer.parseInt(req.getParameter("n"));
            int offset = b - a;

            boolean illegalArgCombination = a < -100 || b < -100 || b > 100 || offset < 0 || n < 1 || n > 5;

            if (illegalArgCombination) {
                throw new IllegalArgumentException();
            }

            for (int pow = 1; pow <= n; pow++) {
                HSSFSheet sheet =  hwb.createSheet("pow=" + pow);

                for (int i = 0; i <= offset; i++) {
                    HSSFRow row = sheet.createRow(i);
                    int num = a + i;

                    row.createCell(0).setCellValue(num);
                    row.createCell(1).setCellValue(Math.pow(num, pow));
                }
            }

            resp.setHeader("Content-Disposition", "attachment; filename=\"tablica.xls\"");
            resp.setStatus(HttpServletResponse.SC_OK);
            hwb.write(resp.getOutputStream());
            resp.getOutputStream().flush();
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
