package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.User;
import hibernate.UserStatus;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

@WebServlet(name = "LoadUserTable", urlPatterns = {"/LoadUserTable"})
public class LoadUserTable extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        int start = Integer.parseInt(request.getParameter("start"));
        int count = Integer.parseInt(request.getParameter("count"));

        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.openSession();

        Criteria c = s.createCriteria(User.class);
        responseObject.addProperty("allUserCount", c.list().size());

        c.setFirstResult(start);
        c.setMaxResults(count);

        List<User> userList = c.list();
        for (User user : userList) {
            user.setPassword(null);
            user.setVerification(null);
        }
        responseObject.add("userList", gson.toJsonTree(userList));
        responseObject.addProperty("status", true);
        
        s.close();
        response.setContentType("application/json");
        String toJson = gson.toJson(responseObject);
        response.getWriter().write(toJson);
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject data = gson.fromJson(request.getReader(), JsonObject.class);
        String userId = data.get("userId").getAsString();
        String status = data.get("status").getAsString();

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        if (status.isEmpty()) {
            responseObject.addProperty("message", "Status is empty!");
        } else if (userId.isEmpty()) {
            responseObject.addProperty("message", "User ID is empty!");
        } else {
            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();
            
            User user = (User) s.get(User.class, Integer.valueOf(userId));
            if (user == null) {
                responseObject.addProperty("message", "User with the Given User ID does not Exists!");
            } else {
                UserStatus activeStatus = (UserStatus) s.get(UserStatus.class, 1);
                UserStatus blockStatus = (UserStatus) s.get(UserStatus.class, 2);
                if (status.equals("Active")) {
                    user.setUserStatus(blockStatus);
                } else {
                    user.setUserStatus(activeStatus);
                }
                user.setUpdated_at(new Date());
                s.update(user);
                s.beginTransaction().commit();
                responseObject.addProperty("status", true);
            }
            s.close();
        }
        response.setContentType("application/json");
        String toJson = gson.toJson(responseObject);
        response.getWriter().write(toJson);
    }
}
