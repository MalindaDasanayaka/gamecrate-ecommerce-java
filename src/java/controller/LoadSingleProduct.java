package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.Product;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Util;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "LoadSingleProduct", urlPatterns = {"/LoadSingleProduct"})
public class LoadSingleProduct extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);
        String productId = request.getParameter("id");
        if (Util.isInteger(productId)) {
            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();
            try {
                Product product = (Product) s.get(Product.class, Integer.valueOf(productId));
                if (product.getProductStatus().getValue().equals("Active")) {
                    //similar product data
                    Criteria c = s.createCriteria(Product.class);
                    c.add(Restrictions.eq("category", product.getCategory()));
                    c.add(Restrictions.eq("platform", product.getPlatform()));
                    c.add(Restrictions.eq("productStatus", product.getProductStatus()));
                    c.add(Restrictions.ne("id", product.getId()));
                    c.setMaxResults(4);
                    List<Product> productList = c.list();
                    //similar product data
                    responseObject.add("product", gson.toJsonTree(product));
                    responseObject.add("productList", gson.toJsonTree(productList));
                    responseObject.addProperty("status", true);
                } else {
                    responseObject.addProperty("message", "Product Not Found!");
                }
            } catch (Exception e) {
                responseObject.addProperty("message", "Product Not Found!");
            }
        }

        response.setContentType("application/json");
        String toJson = gson.toJson(responseObject);
        response.getWriter().write(toJson);
    }
}
