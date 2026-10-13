package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.DeliveryType;
import hibernate.HibernateUtil;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Util;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

@WebServlet(name = "UpdateDeliveryPrice", urlPatterns = {"/UpdateDeliveryPrice"})
public class UpdateDeliveryPrice extends HttpServlet {

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject data = gson.fromJson(request.getReader(), JsonObject.class);

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        String price = data.get("price").getAsString();
        String deliveryId = data.get("id").getAsString();

        if (price.isEmpty()) {
            responseObject.addProperty("message", "Please enter price first!");
        } else if (!Util.isDouble(price)) {
            responseObject.addProperty("message", "Invalid Price!");
        } else if (Double.parseDouble(price) <= 0) {
            responseObject.addProperty("message", "Price must be greater than 0!");
        } else if (deliveryId.isEmpty()) {
            responseObject.addProperty("message", "ID is Empty!");
        } else {
            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();

            DeliveryType deliveryType = (DeliveryType) s.get(DeliveryType.class, Integer.valueOf(deliveryId));
            if (deliveryType == null) {
                responseObject.addProperty("message", "Delivery Type with the Given ID does not Exists!");
            } else {
                deliveryType.setPrice(Double.parseDouble(price));
                s.update(deliveryType);
                s.beginTransaction().commit();
                responseObject.addProperty("status", true);
            }
            s.close();
        }

        String responseText = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(responseText);
    }
}
