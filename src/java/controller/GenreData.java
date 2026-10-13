package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Genre;
import hibernate.HibernateUtil;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "GenreData", urlPatterns = {"/GenreData"})
public class GenreData extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject genreData = gson.fromJson(request.getReader(), JsonObject.class);

        String genre = genreData.get("genre").getAsString();
        
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);
        
        if (genre.isEmpty()) {
            responseObject.addProperty("message", "Please enter a genre name first!");
        } else if (genre.length() > 20) {
            responseObject.addProperty("message", "Genre name should be less than 20 characters!");
        } else {
            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();

            Criteria c = s.createCriteria(Genre.class);
            c.add(Restrictions.eq("value", genre));

            if (!c.list().isEmpty()) {
                responseObject.addProperty("message", "Genre already exists!");
            } else {
                Genre g = new Genre();
                g.setValue(genre);
                
                s.save(g);
                s.beginTransaction().commit();
                
                responseObject.addProperty("status", true);
                responseObject.addProperty("message", "Genre registration success.");
            }
            s.close();
        }
        String responseText = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(responseText);
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject genreData = gson.fromJson(request.getReader(), JsonObject.class);

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        String genre = genreData.get("genre").getAsString();
        int genreId = genreData.get("id").getAsInt();

        if (genre.isEmpty()) {
            responseObject.addProperty("message", "Please enter a genre name first!");
        } else if (genre.length() > 20) {
            responseObject.addProperty("message", "Genre name should be less than 20 characters!");
        } else {
            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();

            // Check if a different platform with the same name already exists
            Criteria c1 = s.createCriteria(Genre.class);
            c1.add(Restrictions.eq("value", genre));
            c1.add(Restrictions.ne("id", genreId));

            if (!c1.list().isEmpty()) {
                responseObject.addProperty("message", "Another genre with the same name already exists!");
            } else {
                Genre existingGenre = (Genre) s.get(Genre.class, genreId);

                if (existingGenre != null) {
                    existingGenre.setValue(genre);

                    s.update(existingGenre);
                    s.beginTransaction().commit();

                    responseObject.addProperty("status", true);
                    responseObject.addProperty("message", "Genre updated successfully.");
                } else {
                    responseObject.addProperty("message", "Genre not found!");
                }
            }
            s.close();
        }

        String responseText = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(responseText);
    }
}
