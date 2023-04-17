package hr.fer.oprpp2.servlets.util;


import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name="trigonometric", urlPatterns={"/trigonometric"})
public class TrigonometricServlet extends HttpServlet {

    public static class TrigFunction {
        private final int arg;
        private final double sin;
        private final double cos;

        public TrigFunction(int arg) {
            this.arg = arg;
            this.sin = Math.sin(arg);
            this.cos = Math.cos(arg);
        }

        public int getArg() {
            return arg;
        }

        public double getSin() {
            return sin;
        }

        public double getCos() {
            return cos;
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int a = 0, b = 360;

        try {
            a = Integer.parseInt(req.getParameter("a"));
        } catch(Exception ignore) {}

        try {
            b = Integer.parseInt(req.getParameter("b"));
        } catch(Exception ignore) {}

        if(a > b) {
            int tmp = a;
            a = b;
            b = tmp;
        }

        if (b > a + 720) {
            b = a + 720;
        }

        List<TrigFunction> results = new ArrayList<>();

        for(int i = a; i <= b; i++) {
            results.add(new TrigFunction(i));
        }

        req.setAttribute("results", results);

        req.getRequestDispatcher("/WEB-INF/pages/trigonometric.jsp").forward(req, resp);
    }
}
