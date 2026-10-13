package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Category;
import hibernate.Genre;
import hibernate.HibernateUtil;
import hibernate.Platform;
import hibernate.Product;
import hibernate.ProductStatus;
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

@WebServlet(name = "SearchProducts", urlPatterns = {"/SearchProducts"})
public class SearchProducts extends HttpServlet {
    private static final int MAX_RESULT = 6;
    private static final int ACTIVE_ID = 1;
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject responsObject = new JsonObject();
        responsObject.addProperty("status", false);

        JsonObject requestJsonObject = gson.fromJson(request.getReader(), JsonObject.class);

        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.openSession();

        Criteria c1 = s.createCriteria(Product.class);

        if (requestJsonObject.has("categoryName") && !requestJsonObject.get("categoryName").isJsonNull()) {
            String categoryName = requestJsonObject.get("categoryName").getAsString();

            Criteria c2 = s.createCriteria(Category.class);
            c2.add(Restrictions.eq("value", categoryName));
            Category category = (Category) c2.uniqueResult();

            c1.add(Restrictions.eq("category", category));
        }

        if (requestJsonObject.has("platformName") && !requestJsonObject.get("platformName").isJsonNull()) {
            String platformName = requestJsonObject.get("platformName").getAsString();

            Criteria c3 = s.createCriteria(Platform.class);
            c3.add(Restrictions.eq("value", platformName));
            Platform platform = (Platform) c3.uniqueResult();

            c1.add(Restrictions.eq("platform", platform));
        }

        if (requestJsonObject.has("genreName") && !requestJsonObject.get("genreName").isJsonNull()) {
            String genreName = requestJsonObject.get("genreName").getAsString();

            Criteria c4 = s.createCriteria(Genre.class);
            c4.add(Restrictions.eq("value", genreName));
            Genre genre = (Genre) c4.uniqueResult();

            c1.add(Restrictions.eq("genre", genre));
        }

        if (requestJsonObject.has("priceStart") && requestJsonObject.has("priceEnd")) {
            double priceStart = requestJsonObject.get("priceStart").getAsDouble();
            double priceEnd = requestJsonObject.get("priceEnd").getAsDouble();

            c1.add(Restrictions.ge("price", priceStart));
            c1.add(Restrictions.le("price", priceEnd));
        }

        if (requestJsonObject.has("sortValue")) {
            int sortValue = requestJsonObject.get("sortValue").getAsInt();

            if (sortValue == 0) {
                c1.addOrder(Order.desc("id"));
            } else if (sortValue == 1) {
                c1.addOrder(Order.asc("id"));
            } else if (sortValue == 2) {
                c1.addOrder(Order.asc("title"));
            } else if (sortValue == 3) {
                c1.addOrder(Order.desc("title"));
            } else if (sortValue == 4) {
                c1.addOrder(Order.asc("price"));
            } else if (sortValue == 5) {
                c1.addOrder(Order.desc("price"));
            }
        }
        
        responsObject.addProperty("allProductCount", c1.list().size());
        
        if (requestJsonObject.has("firstResult")) {
            int firstResult = requestJsonObject.get("firstResult").getAsInt();
            c1.setFirstResult(firstResult);
            c1.setMaxResults(SearchProducts.MAX_RESULT);
        }

        ProductStatus status = (ProductStatus) s.get(ProductStatus.class, SearchProducts.ACTIVE_ID);
        c1.add(Restrictions.eq("productStatus", status));
        List<Product> productList = c1.list();

        s.close();

        responsObject.add("productList", gson.toJsonTree(productList));
        responsObject.addProperty("status", true);
        response.setContentType("application/json");
        String toJson = gson.toJson(responsObject);
        response.getWriter().write(toJson);
    }
}
