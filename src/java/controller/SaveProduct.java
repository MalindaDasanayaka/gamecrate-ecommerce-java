package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Category;
import hibernate.Genre;
import hibernate.HibernateUtil;
import hibernate.Platform;
import hibernate.Product;
import hibernate.ProductStatus;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import model.Util;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

@MultipartConfig
@WebServlet(name = "SaveProduct", urlPatterns = {"/SaveProduct"})
public class SaveProduct extends HttpServlet {
    private static final int ACTIVE_STATUS_ID = 1;
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String title = request.getParameter("title");
        String categoryId = request.getParameter("categoryId");
        String platformId = request.getParameter("platformId");
        String genreId = request.getParameter("genreId");
        String description = request.getParameter("description");
        String price = request.getParameter("price");
        String qty = request.getParameter("qty");

        //image uploading
        Part part1 = request.getPart("image1");
        Part part2 = request.getPart("image2");
        Part part3 = request.getPart("image3");

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.openSession();

        if (request.getSession().getAttribute("admin") == null) {
            responseObject.addProperty("message", "Please Sign In!");

        } else if (title.isEmpty()) {
            responseObject.addProperty("message", "Product Title Can not be Empty!");

        } else if (title.length() > 100) {
            responseObject.addProperty("message", "Product Title Can not be Greater than 100 Characters!");

        } else if (!Util.isInteger(categoryId)) {
            responseObject.addProperty("message", "Invalid Category!");

        } else if (Integer.parseInt(categoryId) == 0) {
            responseObject.addProperty("message", "Please Select a Category!");

        } else if (!Util.isInteger(platformId)) {
            responseObject.addProperty("message", "Invalid Platform!");

        } else if (Integer.parseInt(platformId) == 0) {
            responseObject.addProperty("message", "Please Select a Platform!");

        } else if (!Util.isInteger(genreId)) {
            responseObject.addProperty("message", "Invalid Genre!");

        } else if (Integer.parseInt(genreId) == 0) {
            responseObject.addProperty("message", "Please Select a Genre!");

        } else if (description.isEmpty()) {
            responseObject.addProperty("message", "Product Description Can not be Empty!");

        } else if (!Util.isDouble(price)) {
            responseObject.addProperty("message", "Invalid Price!");

        } else if (Double.parseDouble(price) <= 0) {
            responseObject.addProperty("message", "Price must be greater than 0!");

        } else if (!Util.isInteger(qty)) {
            responseObject.addProperty("message", "Invalid Quantity!");

        } else if (Integer.parseInt(qty) <= 0) {
            responseObject.addProperty("message", "Quantity must be greater than 0!");

        } else if (part1.getSubmittedFileName() == null) {
            responseObject.addProperty("message", "Product Image 1 is Required!");

        } else if (part2.getSubmittedFileName() == null) {
            responseObject.addProperty("message", "Product Image 2 is Required!");

        } else if (part3.getSubmittedFileName() == null) {
            responseObject.addProperty("message", "Product Image 3 is Required!");

        } else {

            Category category = (Category) s.get(Category.class, Integer.valueOf(categoryId));
            if (category == null) {
                responseObject.addProperty("message", "Please Select a valid Category!");

            } else {

                Platform platform = (Platform) s.get(Platform.class, Integer.valueOf(platformId));
                if (platform == null) {
                    responseObject.addProperty("message", "Please Select a valid Platform!");

                } else {

                    Genre genre = (Genre) s.get(Genre.class, Integer.valueOf(genreId));
                    if (genre == null) {
                        responseObject.addProperty("message", "Please Select a valid Genre!");
   
                    } else {

                        Product p = new Product();
                        p.setTitle(title);
                        p.setCategory(category);
                        p.setPlatform(platform);
                        p.setGenre(genre);
                        p.setDescription(description);
                        p.setPrice(Double.parseDouble(price));
                        p.setQty(Integer.parseInt(qty));

                        ProductStatus status = (ProductStatus) s.get(ProductStatus.class, SaveProduct.ACTIVE_STATUS_ID);
                        p.setProductStatus(status);

                        p.setCreated_at(new Date());

                        int id = (int) s.save(p);
                        s.beginTransaction().commit();
                        s.close();

                        String appPath = getServletContext().getRealPath("");
                        String newPath = appPath.replace("build\\web", "web\\product-images");

                        File productFolder = new File(newPath, String.valueOf(id));
                        productFolder.mkdir();

                        File file1 = new File(productFolder, "image1.png");
                        Files.copy(part1.getInputStream(), file1.toPath(), StandardCopyOption.REPLACE_EXISTING);

                        File file2 = new File(productFolder, "image2.png");
                        Files.copy(part2.getInputStream(), file2.toPath(), StandardCopyOption.REPLACE_EXISTING);

                        File file3 = new File(productFolder, "image3.png");
                        Files.copy(part3.getInputStream(), file3.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        //image uploading

                        responseObject.addProperty("status", true);

                    }
                }

            }
        }

        Gson gson = new Gson();
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject productData = gson.fromJson(request.getReader(), JsonObject.class);

        int productId = productData.get("productId").getAsInt();
        String title = productData.get("title").getAsString();
        String categoryId = productData.get("categoryId").getAsString();
        String platformId = productData.get("platformId").getAsString();
        String genreId = productData.get("genreId").getAsString();
        String description = productData.get("description").getAsString();
        String price = productData.get("price").getAsString();
        String qty = productData.get("qty").getAsString();

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.openSession();

        if (request.getSession().getAttribute("admin") == null) {
            responseObject.addProperty("message", "Please Sign In!");

        } else if (title.isEmpty()) {
            responseObject.addProperty("message", "Product Title Can not be Empty!");

        } else if (title.length() > 100) {
            responseObject.addProperty("message", "Product Title Can not be Greater than 100 Characters!");

        } else if (!Util.isInteger(categoryId)) {
            responseObject.addProperty("message", "Invalid Category!");

        } else if (Integer.parseInt(categoryId) == 0) {
            responseObject.addProperty("message", "Please Select a Category!");

        } else if (!Util.isInteger(platformId)) {
            responseObject.addProperty("message", "Invalid Platform!");

        } else if (Integer.parseInt(platformId) == 0) {
            responseObject.addProperty("message", "Please Select a Platform!");

        } else if (!Util.isInteger(genreId)) {
            responseObject.addProperty("message", "Invalid Genre!");

        } else if (Integer.parseInt(genreId) == 0) {
            responseObject.addProperty("message", "Please Select a Genre!");

        } else if (description.isEmpty()) {
            responseObject.addProperty("message", "Product Description Can not be Empty!");

        } else if (!Util.isDouble(price)) {
            responseObject.addProperty("message", "Invalid Price!");

        } else if (Double.parseDouble(price) <= 0) {
            responseObject.addProperty("message", "Price must be greater than 0!");

        } else if (!Util.isInteger(qty)) {
            responseObject.addProperty("message", "Invalid Quantity!");

        } else if (Integer.parseInt(qty) <= 0) {
            responseObject.addProperty("message", "Quantity must be greater than 0!");

        } else {

            Category category = (Category) s.get(Category.class, Integer.valueOf(categoryId));
            if (category == null) {
                responseObject.addProperty("message", "Please Select a valid Category!");

            } else {

                Platform platform = (Platform) s.get(Platform.class, Integer.valueOf(platformId));
                if (platform == null) {
                    responseObject.addProperty("message", "Please Select a valid Platform!");

                } else {

                    Genre genre = (Genre) s.get(Genre.class, Integer.valueOf(genreId));
                    if (genre == null) {
                        responseObject.addProperty("message", "Please Select a valid Genre!");
   
                    } else {
                        
                        Product p = (Product) s.load(Product.class, productId);
                        
                        p.setTitle(title);
                        p.setCategory(category);
                        p.setPlatform(platform);
                        p.setGenre(genre);
                        p.setDescription(description);
                        p.setPrice(Double.parseDouble(price));
                        p.setQty(Integer.parseInt(qty));

                        p.setUpdated_at(new Date());

                        s.update(p);
                        s.beginTransaction().commit();
                        s.close();

                        responseObject.addProperty("status", true);
                    }
                }

            }
        }

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));
    }
}
