package controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import hibernate.Address;
import hibernate.HibernateUtil;
import hibernate.OrderItems;
import hibernate.Orders;
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
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "LoadOrderHistory", urlPatterns = {"/LoadOrderHistory"})
public class LoadOrderHistory extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession ses = request.getSession(false);
        if (ses != null && ses.getAttribute("user") != null) {
            User user = (User) ses.getAttribute("user");

            Gson gson = new Gson();
            JsonObject responseObject = new JsonObject();

            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();

            Criteria c1 = s.createCriteria(Orders.class);
            c1.add(Restrictions.eq("user", user));
            c1.addOrder(Order.desc("created_at"));
            List<Orders> ordersList = c1.list();

            JsonArray ordersJsonArray = new JsonArray();

            for (Orders order : ordersList) {
                JsonObject orderJson = gson.toJsonTree(order).getAsJsonObject();

                Criteria c2 = s.createCriteria(OrderItems.class);
                c2.add(Restrictions.eq("orders", order));
                List<OrderItems> items = c2.list();

                JsonArray itemsArray = gson.toJsonTree(items).getAsJsonArray();
                orderJson.add("items", itemsArray);

                ordersJsonArray.add(orderJson);
            }

            responseObject.add("ordersList", ordersJsonArray);

            s.close();

            String toJson = gson.toJson(responseObject);
            response.setContentType("application/json");
            response.getWriter().write(toJson);
        }
    }
}
