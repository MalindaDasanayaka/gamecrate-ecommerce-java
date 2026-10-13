package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.Product;
import hibernate.ProductStatus;
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

@WebServlet(name = "LoadProductTable", urlPatterns = {"/LoadProductTable"})
public class LoadProductTable extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        int start = Integer.parseInt(request.getParameter("start"));
        int count = Integer.parseInt(request.getParameter("count"));

        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.openSession();

        Criteria c = s.createCriteria(Product.class);
        responseObject.addProperty("allProductCount", c.list().size());

        c.setFirstResult(start);
        c.setMaxResults(count);

        List<Product> productList = c.list();
        responseObject.add("productList", gson.toJsonTree(productList));
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
        String productId = data.get("productId").getAsString();
        String status = data.get("status").getAsString();

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        if (status.isEmpty()) {
            responseObject.addProperty("message", "Status is empty!");
        } else if (productId.isEmpty()) {
            responseObject.addProperty("message", "Product ID is empty!");
        } else {
            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();
            
            Product product = (Product) s.get(Product.class, Integer.valueOf(productId));
            if (product == null) {
                responseObject.addProperty("message", "Product with the Given Product ID does not Exists!");
            } else {
                ProductStatus activeStatus = (ProductStatus) s.get(ProductStatus.class, 1);
                ProductStatus discontinueStatus = (ProductStatus) s.get(ProductStatus.class, 2);
                if (status.equals("Active")) {
                    product.setProductStatus(discontinueStatus);
                } else {
                    product.setProductStatus(activeStatus);
                }
                product.setUpdated_at(new Date());
                s.update(product);
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
