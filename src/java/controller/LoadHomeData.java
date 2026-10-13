package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Category;
import hibernate.HibernateUtil;
import hibernate.OrderItems;
import hibernate.Platform;
import hibernate.Product;
import hibernate.ProductStatus;
import java.io.IOException;
import java.util.ArrayList;
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
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "LoadHomeData", urlPatterns = {"/LoadHomeData"})
public class LoadHomeData extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.openSession();

        Criteria c1 = s.createCriteria(Platform.class);
        List<Platform> platformList = c1.list();
        c1.setFirstResult(0);
        c1.setMaxResults(5);
        responseObject.add("platformList", gson.toJsonTree(platformList));

        Criteria c2 = s.createCriteria(OrderItems.class, "oi");
        c2.createAlias("oi.product", "p");
        c2.createAlias("p.category", "cat");
        Category consoles = (Category) s.get(Category.class, 1); // 1 = Consoles
        c2.add(Restrictions.eq("cat.id", consoles.getId()));
        ProductStatus status = (ProductStatus) s.get(ProductStatus.class, 1);
        c2.add(Restrictions.eq("p.productStatus", status));
        c2.setProjection(Projections.projectionList()
                .add(Projections.groupProperty("oi.product"))
                .add(Projections.sum("oi.qty"), "totalQty"));
        c2.addOrder(Order.desc("totalQty"));
        c2.setFirstResult(0);
        c2.setMaxResults(4);
        List<Object[]> results = c2.list();
        List<Product> consolesList = new ArrayList<>();
        for (Object[] result : results) {
            Product product = (Product) result[0];
            consolesList.add(product);
        }
        responseObject.add("consolesList", gson.toJsonTree(consolesList));

        Criteria c3 = s.createCriteria(OrderItems.class, "oi");
        c3.createAlias("oi.product", "p");
        c3.createAlias("p.category", "cat");
        Category games = (Category) s.get(Category.class, 2); // 2 = Games
        c3.add(Restrictions.eq("cat.id", games.getId()));
        c3.add(Restrictions.eq("p.productStatus", status));
        c3.setProjection(Projections.projectionList()
                .add(Projections.groupProperty("oi.product"))
                .add(Projections.sum("oi.qty"), "totalQty"));
        c3.addOrder(Order.desc("totalQty"));
        c3.setFirstResult(0);
        c3.setMaxResults(8);
        List<Object[]> gresults = c3.list();
        List<Product> featuredGamesList = new ArrayList<>();
        for (Object[] result : gresults) {
            Product product = (Product) result[0];
            featuredGamesList.add(product);
        }
        responseObject.add("featuredGamesList", gson.toJsonTree(featuredGamesList));

        Criteria c4 = s.createCriteria(Product.class);
        c4.addOrder(Order.desc("id"));
        c4.add(Restrictions.eq("category", games));
        c4.add(Restrictions.eq("productStatus", status));
        c4.setFirstResult(0);
        c4.setMaxResults(8);
        List<Product> newReleasesList = c4.list();
        responseObject.add("newReleasesList", gson.toJsonTree(newReleasesList));

        responseObject.addProperty("status", true);
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));
    }
}
