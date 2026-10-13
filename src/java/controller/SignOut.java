package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Cart;
import hibernate.HibernateUtil;
import hibernate.User;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "SignOut", urlPatterns = {"/SignOut"})
public class SignOut extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        JsonObject responseObject = new JsonObject();
//        responseObject.addProperty("status", false);
//
//        // Get current session without creating new one
//        HttpSession ses = request.getSession(false);
//        User user = (User) request.getSession().getAttribute("user");
//
//        if (user != null) {
//            SessionFactory sf = HibernateUtil.getSessionFactory();
//            Session s = sf.openSession();
//
//            Criteria c = s.createCriteria(Cart.class);
//            c.add(Restrictions.eq("user", user));
//            List<Cart> userCartItems = c.list(); // Temporary store
//
//            s.close();
//
//            if (ses != null) {
//                ses.invalidate(); // Invalidate current session
//
//                // Start new session and set sessionCart
//                HttpSession newSession = request.getSession(true);
//                newSession.setAttribute("sessionCart", userCartItems);
//                responseObject.addProperty("status", true);
//            }
//        }
        
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);
        HttpSession ses = request.getSession(false);
        if (ses != null && ses.getAttribute("user") != null) {
            ses.invalidate();
            responseObject.addProperty("status", true);
        }

        Gson gson = new Gson();
        String toJson = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(toJson);
    }
}
