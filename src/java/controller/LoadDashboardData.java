package controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.OrderItems;
import hibernate.Platform;
import hibernate.Product;
import hibernate.ProductStatus;
import hibernate.User;
import hibernate.UserStatus;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "LoadDashboardData", urlPatterns = {"/LoadDashboardData"})
public class LoadDashboardData extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.openSession();
        
        UserStatus userStatus = (UserStatus) s.get(UserStatus.class, 1);
        ProductStatus productStatus = (ProductStatus) s.get(ProductStatus.class, 1);

        Criteria c1 = s.createCriteria(User.class);
        c1.add(Restrictions.eq("userStatus", userStatus));
        responseObject.addProperty("userCount", c1.list().size());

        Criteria c2 = s.createCriteria(Product.class);
        c2.add(Restrictions.eq("productStatus", productStatus));
        responseObject.addProperty("productCount", c2.list().size());

        Criteria c3 = s.createCriteria(OrderItems.class);
        c3.setProjection(Projections.sum("qty"));
        Long itemCount = (Long) c3.uniqueResult();
        responseObject.addProperty("itemCount", itemCount);

        Criteria c4 = s.createCriteria(OrderItems.class);
        List<OrderItems> orderItemsList = c4.list();
        double totalSales = 0;
        for (OrderItems orderItems : orderItemsList) {
            int qty = orderItems.getQty();
            double price = orderItems.getProduct().getPrice();
            totalSales += qty * price;
        }
        responseObject.addProperty("totalSales", totalSales);
        
        // Sales Chart Data - Last 7 days
        JsonArray salesLabels = new JsonArray();
        JsonArray salesData = new JsonArray();
        
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        
        for (int i = 6; i >= 0; i--) {
            cal.setTime(new Date());
            cal.add(Calendar.DAY_OF_MONTH, -i);
            Date dayStart = cal.getTime();
            
            cal.add(Calendar.DAY_OF_MONTH, 1);
            Date dayEnd = cal.getTime();
            
            String dateLabel = sdf.format(dayStart);
            salesLabels.add(dateLabel);
            
            // Get sales for this specific day
            Criteria salesCriteria = s.createCriteria(OrderItems.class);
            salesCriteria.createAlias("orders", "o");
            salesCriteria.add(Restrictions.ge("o.created_at", dayStart));
            salesCriteria.add(Restrictions.lt("o.created_at", dayEnd));
            
            List<OrderItems> dailyOrderItems = salesCriteria.list();
            double dailySales = 0;
            
            for (OrderItems item : dailyOrderItems) {
                dailySales += item.getQty() * item.getProduct().getPrice();
            }
            
            salesData.add(dailySales);
        }
        
        responseObject.add("salesLabels", salesLabels);
        responseObject.add("salesData", salesData);
        
        // Platform Chart Data
        JsonArray platformLabels = new JsonArray();
        JsonArray platformData = new JsonArray();
        JsonArray platformColors = new JsonArray();
        
        // Get all platforms
        Criteria platformCriteria = s.createCriteria(Platform.class);
        List<Platform> platformList = platformCriteria.list();
        
        // Calculate total sales across all platforms for percentage calculation
        double totalPlatformSales = 0;
        Map<String, Double> platformSalesMap = new HashMap<>();
        
        for (Platform platform : platformList) {
            Criteria platformSalesCriteria = s.createCriteria(OrderItems.class);
            platformSalesCriteria.createAlias("product", "p");
            platformSalesCriteria.add(Restrictions.eq("p.platform", platform));
            
            List<OrderItems> platformOrderItems = platformSalesCriteria.list();
            double platformSales = 0;
            
            for (OrderItems item : platformOrderItems) {
                platformSales += item.getQty() * item.getProduct().getPrice();
            }
            
            platformSalesMap.put(platform.getValue(), platformSales);
            totalPlatformSales += platformSales;
        }
        
        // Generate colors and calculate percentages
        String[] colors = {"#00D1FF", "#28a745", "#ffc107", "#dc3545", "#6f42c1", "#fd7e14", "#20c997", "#e83e8c"};
        int colorIndex = 0;
        
        for (Platform platform : platformList) {
            String platformName = platform.getValue();
            double platformSales = platformSalesMap.get(platformName);
            
            platformLabels.add(platformName);
            
            // Calculate percentage
            double percentage = totalPlatformSales > 0 ? (platformSales / totalPlatformSales) * 100 : 0;
            platformData.add(Math.round(percentage * 100.0) / 100.0); // Round to 2 decimal places
            
            // Add color
            String color = colors[colorIndex % colors.length];
            platformColors.add(color);
            colorIndex++;
        }
        
        responseObject.add("platformLabels", platformLabels);
        responseObject.add("platformData", platformData);
        responseObject.add("platformColors", platformColors);
        

        s.close();

        responseObject.addProperty("status", true);
        response.setContentType("application/json");
        String toJson = gson.toJson(responseObject);
        response.getWriter().write(toJson);
    }
}
