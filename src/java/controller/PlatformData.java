package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.Platform;
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

@WebServlet(name = "PlatformData", urlPatterns = {"/PlatformData"})
public class PlatformData extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject platformData = gson.fromJson(request.getReader(), JsonObject.class);

        String platform = platformData.get("platform").getAsString();
        
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);
        
        if (platform.isEmpty()) {
            responseObject.addProperty("message", "Please enter a platform name first!");
        } else if (platform.length() > 20) {
            responseObject.addProperty("message", "Platform name should be less than 20 characters!");
        } else {
            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();

            Criteria c = s.createCriteria(Platform.class);
            c.add(Restrictions.eq("value", platform));

            if (!c.list().isEmpty()) {
                responseObject.addProperty("message", "Platform already exists!");
            } else {
                Platform p = new Platform();
                p.setValue(platform);
                
                s.save(p);
                s.beginTransaction().commit();
                
                responseObject.addProperty("status", true);
                responseObject.addProperty("message", "Platform registration success.");
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
        JsonObject platformData = gson.fromJson(request.getReader(), JsonObject.class);

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        String platform = platformData.get("platform").getAsString();
        int platformId = platformData.get("id").getAsInt();

        if (platform.isEmpty()) {
            responseObject.addProperty("message", "Please enter a platform name first!");
        } else if (platform.length() > 20) {
            responseObject.addProperty("message", "Platform name should be less than 20 characters!");
        } else {
            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();

            // Check if a different platform with the same name already exists
            Criteria c1 = s.createCriteria(Platform.class);
            c1.add(Restrictions.eq("value", platform));
            c1.add(Restrictions.ne("id", platformId));

            if (!c1.list().isEmpty()) {
                responseObject.addProperty("message", "Another platform with the same name already exists!");
            } else {
                Platform existingPlatform = (Platform) s.get(Platform.class, platformId);

                if (existingPlatform != null) {
                    existingPlatform.setValue(platform);

                    s.update(existingPlatform);
                    s.beginTransaction().commit();

                    responseObject.addProperty("status", true);
                    responseObject.addProperty("message", "Platform updated successfully.");
                } else {
                    responseObject.addProperty("message", "Platform not found!");
                }
            }
            s.close();
        }

        String responseText = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(responseText);
    }
}
