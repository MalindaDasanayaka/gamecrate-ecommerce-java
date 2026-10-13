package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.User;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.Util;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "Profile", urlPatterns = {"/Profile"})
public class Profile extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession ses = request.getSession(false);
        if (ses != null && ses.getAttribute("user") != null) {
            User user = (User) ses.getAttribute("user");
            JsonObject responseObject = new JsonObject();
            responseObject.addProperty("firstName", user.getFirst_name());
            responseObject.addProperty("lastName", user.getLast_name());
            responseObject.addProperty("mobile", user.getMobile());
            responseObject.addProperty("email", user.getEmail());

            String since = new SimpleDateFormat("MMM yyyy").format(user.getCreated_at());
            responseObject.addProperty("since", since);

            Gson gson = new Gson();
            String toJson = gson.toJson(responseObject);
            response.setContentType("application/json");
            response.getWriter().write(toJson);
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject userData = gson.fromJson(request.getReader(), JsonObject.class);

        String firstName = userData.get("firstName").getAsString();
        String lastName = userData.get("lastName").getAsString();
        String mobile = userData.get("mobile").getAsString();

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        if (firstName.isEmpty()) {
            responseObject.addProperty("message", "First Name can not be empty!");
        } else if (firstName.length() > 45) {
            responseObject.addProperty("message", "First Name can not be greater than 45 characters!");
        } else if (lastName.isEmpty()) {
            responseObject.addProperty("message", "Last Name can not be empty!");
        } else if (lastName.length() > 45) {
            responseObject.addProperty("message", "Last Name can not be greater than 45 characters!");
        } else if (mobile.isEmpty()) {
            responseObject.addProperty("message", "Mobile Number can not be empty!");
        } else if (!Util.isMobileValid(mobile)) {
            responseObject.addProperty("message", "Please enter a valid Mobile Number!");
        } else {
            HttpSession ses = request.getSession();
            if (ses.getAttribute("user") != null) {
                User u = (User) ses.getAttribute("user"); //get session user

                SessionFactory sf = HibernateUtil.getSessionFactory();
                Session s = sf.openSession();

                Criteria c = s.createCriteria(User.class);
                c.add(Restrictions.eq("email", u.getEmail())); //session user email
                if (!c.list().isEmpty()) {
                    User u1 = (User) c.list().get(0); //db user

                    u1.setFirst_name(firstName);
                    u1.setLast_name(lastName);
                    u1.setMobile(mobile);
                    u1.setUpdated_at(new Date());

                    //session management
                    ses.setAttribute("user", u1);
                    //session management

                    s.merge(u1);

                    s.beginTransaction().commit();
                    responseObject.addProperty("status", true);
                    responseObject.addProperty("message", "User profile details update successfully!");
                    s.close();
                }
            }
        }

        String responseText = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(responseText);
    }
}
