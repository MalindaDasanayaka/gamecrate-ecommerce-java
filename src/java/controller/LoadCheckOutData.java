package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Address;
import hibernate.Cart;
import hibernate.City;
import hibernate.DeliveryType;
import hibernate.HibernateUtil;
import hibernate.User;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "LoadCheckOutData", urlPatterns = {"/LoadCheckOutData"})
public class LoadCheckOutData extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        User sessionUser = (User) request.getSession().getAttribute("user");

        if (sessionUser == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        } else {
            
            responseObject.addProperty("firstName", sessionUser.getFirst_name());
            responseObject.addProperty("lastName", sessionUser.getLast_name());
            responseObject.addProperty("mobile", sessionUser.getMobile());
            responseObject.addProperty("email", sessionUser.getEmail());

            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();

            Criteria c1 = s.createCriteria(Address.class);
            c1.add(Restrictions.eq("user", sessionUser));

            if (c1.list().isEmpty()) {
                responseObject.addProperty("message", "Your account details are incomplete. Please fill your shipping address");
            } else {
                List<Address> addressList = c1.list();
                for (Address address : addressList) {
                    address.getUser().setId(-1);
                    address.getUser().setPassword(null);
                    address.getUser().setVerification(null);
                    address.getUser().setCreated_at(null);
                }

                responseObject.addProperty("status", true);
                responseObject.add("addressList", gson.toJsonTree(addressList));
            }

            // all city data
            Criteria c2 = s.createCriteria(City.class);
            c2.addOrder(Order.asc("name"));
            List<City> cityList = c2.list();
            responseObject.add("cityList", gson.toJsonTree(cityList));

            // get user cart
            Criteria c3 = s.createCriteria(Cart.class);
            c3.add(Restrictions.eq("user", sessionUser));
            List<Cart> cartList = c3.list();
            if (cartList.isEmpty()) {
                responseObject.addProperty("message", "empty-cart");
            } else {
                for (Cart cart : cartList) {
                    cart.setUser(null);
                }
                Criteria c4 = s.createCriteria(DeliveryType.class);
                List<DeliveryType> deliveryTypes = c4.list();
                responseObject.add("deliveryTypes", gson.toJsonTree(deliveryTypes));
                responseObject.add("cartList", gson.toJsonTree(cartList));
                responseObject.addProperty("status", true);
            }
            s.close();
        }

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));
    }
}
