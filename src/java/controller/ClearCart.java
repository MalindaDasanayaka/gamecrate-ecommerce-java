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

@WebServlet(name = "ClearCart", urlPatterns = {"/ClearCart"})
public class ClearCart extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.openSession();

        HttpSession ses = request.getSession();
        User user = (User) ses.getAttribute("user");
        if (user != null) {
            // DB cart
            Criteria c1 = s.createCriteria(Cart.class);
            c1.add(Restrictions.eq("user", user));
            List<Cart> cartList = c1.list();
            if (cartList != null) {
                for (Cart cart : cartList) {
                    s.delete(cart);
                }
                s.beginTransaction().commit();
                responseObject.addProperty("status", true);
                responseObject.addProperty("message", "Cart Cleared!");
            } else {
                responseObject.addProperty("message", "Cart not found!");
            }
        } else {
            // Session cart
            List<Cart> sessionList = (List<Cart>) ses.getAttribute("sessionCart");
            if (sessionList != null) {
                ses.removeAttribute("sessionCart");
                responseObject.addProperty("status", true);
                responseObject.addProperty("message", "Cart Cleared!");
            } else {
                responseObject.addProperty("message", "Cart not found!");
            }
        }
        s.close();

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));
    }
}
