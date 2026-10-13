package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Category;
import hibernate.HibernateUtil;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "CategoryData", urlPatterns = {"/CategoryData"})
public class CategoryData extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject categoryData = gson.fromJson(request.getReader(), JsonObject.class);

        String category = categoryData.get("category").getAsString();

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        if (category.isEmpty()) {
            responseObject.addProperty("message", "Please enter a category name first!");
        } else if (category.length() > 20) {
            responseObject.addProperty("message", "Category name should be less than 20 characters!");
        } else {
            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();

            Criteria c = s.createCriteria(Category.class);
            c.add(Restrictions.eq("value", category));

            if (!c.list().isEmpty()) {
                responseObject.addProperty("message", "Category already exists!");
            } else {
                Category cat = new Category();
                cat.setValue(category);

                s.save(cat);
                s.beginTransaction().commit();

                responseObject.addProperty("status", true);
                responseObject.addProperty("message", "Category registration success.");
            }
            s.close();
        }
        String responseText = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(responseText);
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject categoryData = gson.fromJson(request.getReader(), JsonObject.class);

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        String category = categoryData.get("category").getAsString();
        int categoryId = categoryData.get("id").getAsInt();

        if (category.isEmpty()) {
            responseObject.addProperty("message", "Please enter a category name first!");
        } else if (category.length() > 20) {
            responseObject.addProperty("message", "Category name should be less than 20 characters!");
        } else {
            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();

            // Check if a different category with the same name already exists
            Criteria c1 = s.createCriteria(Category.class);
            c1.add(Restrictions.eq("value", category));
            c1.add(Restrictions.ne("id", categoryId));

            if (!c1.list().isEmpty()) {
                responseObject.addProperty("message", "Another category with the same name already exists!");
            } else {
                Category existingCategory = (Category) s.get(Category.class, categoryId);

                if (existingCategory != null) {
                    existingCategory.setValue(category);

                    s.update(existingCategory);
                    s.beginTransaction().commit();

                    responseObject.addProperty("status", true);
                    responseObject.addProperty("message", "Category updated successfully.");
                } else {
                    responseObject.addProperty("message", "Category not found!");
                }
            }
            s.close();
        }

        String responseText = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(responseText);
    }
}
