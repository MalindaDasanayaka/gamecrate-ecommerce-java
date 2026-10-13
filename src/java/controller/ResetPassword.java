package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.User;
import java.io.IOException;
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

@WebServlet(name = "ResetPassword", urlPatterns = {"/ResetPassword"})
public class ResetPassword extends HttpServlet {

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject passwordData = gson.fromJson(request.getReader(), JsonObject.class);

        String currentPassword = passwordData.get("currentPassword").getAsString();
        String newPassword = passwordData.get("newPassword").getAsString();
        String confirmPassword = passwordData.get("confirmPassword").getAsString();

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        HttpSession ses = request.getSession();
        if (ses.getAttribute("user") != null) {
            User u = (User) ses.getAttribute("user"); //get session user

            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();

            Criteria c = s.createCriteria(User.class);
            c.add(Restrictions.eq("email", u.getEmail())); //session user email
            if (!c.list().isEmpty()) {
                User u1 = (User) c.list().get(0); //db user
                String password = u1.getPassword();

                if (currentPassword.isEmpty()) {
                    responseObject.addProperty("message", "Current Password can not be empty!");
                } else if (!currentPassword.equals(password)) {
                    responseObject.addProperty("message", "Incorrect Password");
                } else if (newPassword.isEmpty()) {
                    responseObject.addProperty("message", "New Password can not be empty!");
                } else if (!Util.isPasswordValid(newPassword)) {
                    responseObject.addProperty("message", "New Password must contain atleast uppercase, lowercase,"
                            + " number, special character and to be minimum eight characters long!");
                } else if (newPassword.equals(currentPassword)) {
                    responseObject.addProperty("message", "The new password can not be the current password!");
                } else if (confirmPassword.isEmpty()) {
                    responseObject.addProperty("message", "Confirm Password can not be empty!");
                } else if (!Util.isPasswordValid(confirmPassword)) {
                    responseObject.addProperty("message", "Confirm Password must contain atleast uppercase, lowercase,"
                            + " number, special character and to be minimum eight characters long!");
                } else if (!confirmPassword.equals(newPassword)) {
                    responseObject.addProperty("message", "Confirm password does not matching entered new password!");
                } else {
                    u1.setPassword(confirmPassword);
                    u1.setUpdated_at(new Date());

                    //session management
                    ses.setAttribute("user", u1);
                    //session management

                    s.merge(u1);

                    s.beginTransaction().commit();
                    responseObject.addProperty("status", true);
                    responseObject.addProperty("message", "Password reset successful!");
                    s.close();
                }
            }

            String responseText = gson.toJson(responseObject);
            response.setContentType("application/json");
            response.getWriter().write(responseText);
        }
    }
}
