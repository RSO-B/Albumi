package servlet;


import beans.AlbumBeans;
import entities.Album;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/servlet")
public class JPAServlet extends HttpServlet {

    @Inject
    public AlbumBeans albumBeans;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        List<Album> slikaList = albumBeans.getAlbumList();

        // izpis uporabnikov na spletno stran
        resp.setContentType("text/html");

        // Actual logic goes here.
        PrintWriter out = resp.getWriter();
        out.println("<h1>" + "JPA Servlet" + "</h1>");

        if (slikaList == null || slikaList.isEmpty())
            resp.getWriter().println("No Album found.");
        else {
            for (Album a : slikaList) {
                resp.getWriter().println(a.toString());
                resp.getWriter().println("<br>");
                log(a.toString());
            }
        }
    }



}
