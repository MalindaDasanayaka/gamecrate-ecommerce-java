package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Cart;
import hibernate.User;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

@WebServlet(name = "DeleteCartItem", urlPatterns = {"/DeleteCartItem"})
public class DeleteCartItem extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        String id = request.getParameter("id");
        if (id == null || id.isEmpty()) {
            responseObject.addProperty("message", "Invalid cart item ID!");

        } else {
            int cartId = Integer.parseInt(id);
            HttpSession ses = request.getSession();
            User user = (User) ses.getAttribute("user");

            if (user != null) {
                // Delete from database
                SessionFactory sf =hibernate.HibernateUtil.getSessionFactory();
                Session s = sf.openSession();
                Transaction tr = s.beginTransaction();

                Cart cart = (Cart) s.get(Cart.class, cartId);
                if (cart != null && cart.getUser().getId() == user.getId()) {
                    s.delete(cart);
                    tr.commit();
                    responseObject.addProperty("status", true);
                    responseObject.addProperty("message", "Cart Item Deleted!");
                } else {
                    responseObject.addProperty("message", "Cart Item Not Found");
                }
                s.close();
            } else {
                // Delete from session cart
                List<Cart> sessionCart = (List<Cart>) ses.getAttribute("sessionCart");
                if (sessionCart != null) {
                    boolean removed = false;
                    Iterator<Cart> iterator = sessionCart.iterator();
                    while (iterator.hasNext()) {
                        Cart cart = iterator.next();
                        if (cart.getId() == cartId) {
                            iterator.remove();
                            removed = true;
                            break;
                        }
                    }
                    if (removed) {
                        responseObject.addProperty("status", true);
                        responseObject.addProperty("message", "Cart Item Deleted!");
                    } else {
                        responseObject.addProperty("message", "Cart Item Not Found");
                    }
                } else {
                    responseObject.addProperty("message", "Session cart is empty.");
                }
            }
        }

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));
    }
}
