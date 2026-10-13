package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Category;
import hibernate.DeliveryType;
import hibernate.Genre;
import hibernate.HibernateUtil;
import hibernate.Platform;
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

@WebServlet(name = "LoadProductData", urlPatterns = {"/LoadProductData"})
public class LoadProductData extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);
        
        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.openSession();
        
        Criteria c1 = s.createCriteria(Category.class);
        List<Category> categoryList = c1.list();
        
        Criteria c2 = s.createCriteria(Platform.class);
        List<Platform> platformList = c2.list();
        
        Criteria c3 = s.createCriteria(Genre.class);
        List<Genre> genreList = c3.list();
        
        Criteria c4 = s.createCriteria(DeliveryType.class);
        List<DeliveryType> deliveryTypeList = c4.list();
        
        s.close();
        
        Gson gson = new Gson();
        
        responseObject.add("categoryList", gson.toJsonTree(categoryList));
        responseObject.add("platformList", gson.toJsonTree(platformList));
        responseObject.add("genreList", gson.toJsonTree(genreList));
        responseObject.add("deliveryTypeList", gson.toJsonTree(deliveryTypeList));
        
        responseObject.addProperty("status", true);
        
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));
    }
}
