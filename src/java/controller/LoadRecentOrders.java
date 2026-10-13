package controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
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
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "LoadRecentOrders", urlPatterns = {"/LoadRecentOrders"})
public class LoadRecentOrders extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.openSession();

        Criteria c = s.createCriteria(Orders.class);
        c.addOrder(Order.desc("created_at"));
        c.setMaxResults(5);

        List<Orders> ordersList = c.list();

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

        responseObject.addProperty("status", true);
        response.setContentType("application/json");
        String toJson = gson.toJson(responseObject);
        response.getWriter().write(toJson);
    }
}
