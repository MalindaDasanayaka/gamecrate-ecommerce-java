package controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.OrderItems;
import hibernate.OrderStatus;
import hibernate.Orders;
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

@WebServlet(name = "LoadOrderTable", urlPatterns = {"/LoadOrderTable"})
public class LoadOrderTable extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        int start = Integer.parseInt(request.getParameter("start"));
        int count = Integer.parseInt(request.getParameter("count"));

        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.openSession();

        Criteria c1 = s.createCriteria(Orders.class);
        c1.addOrder(Order.desc("created_at"));
        responseObject.addProperty("allOrderCount", c1.list().size());

        c1.setFirstResult(start);
        c1.setMaxResults(count);

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
        String orderId = data.get("orderId").getAsString();
        String status = data.get("status").getAsString();

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        if (status.isEmpty()) {
            responseObject.addProperty("message", "Status is empty!");
        } else if (orderId.isEmpty()) {
            responseObject.addProperty("message", "Order ID is empty!");
        } else {
            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();
            
            Orders orders = (Orders) s.get(Orders.class, orderId);
            if (orders == null) {
                responseObject.addProperty("message", "Order with the Given Order ID does not Exists!");
            } else {
                OrderStatus processStatus = (OrderStatus) s.get(OrderStatus.class, 2);
                OrderStatus shippedStatus = (OrderStatus) s.get(OrderStatus.class, 3);
                OrderStatus deliveredStatus = (OrderStatus) s.get(OrderStatus.class, 4);
                if (status.equals("Paid")) {
                    orders.setOrderStatus(processStatus);
                } else if(status.equals("Processing")) {
                    orders.setOrderStatus(shippedStatus);
                } else if (status.equals("Out for Delivery")) {
                    orders.setOrderStatus(deliveredStatus);
                }
                s.update(orders);
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
