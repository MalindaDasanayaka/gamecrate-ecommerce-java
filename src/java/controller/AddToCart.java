package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Cart;
import hibernate.HibernateUtil;
import hibernate.Product;
import hibernate.User;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.Util;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "AddToCart", urlPatterns = {"/AddToCart"})
public class AddToCart extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String prId = request.getParameter("prId");
        String qty = request.getParameter("qty");
        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        if (!Util.isInteger(prId)) {
            responseObject.addProperty("message", "Invalid Product Id!");
        } else if (!Util.isInteger(qty)) {
            responseObject.addProperty("message", "Invalid Product Quantity!");
        } else {

            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();
            Transaction tr = s.beginTransaction();

            Product product = (Product) s.get(Product.class, Integer.valueOf(prId));

            if (product == null) {
                responseObject.addProperty("message", "Product Not Found!");
            } else { //product available in database

                User user = (User) request.getSession().getAttribute("user");
                if (user != null) { //add product to database cart -> user available

                    Criteria c1 = s.createCriteria(Cart.class);
                    c1.add(Restrictions.eq("user", user));
                    c1.add(Restrictions.eq("product", product));

                    if (c1.list().isEmpty()) { // product not available in same product id
                        if (Integer.parseInt(qty) <= product.getQty()) { // product quantity available
                            Cart cart = new Cart();
                            cart.setQty(Integer.parseInt(qty));
                            cart.setUser(user);
                            cart.setProduct(product);

                            s.save(cart);
                            tr.commit();
                            responseObject.addProperty("status", true);
                            responseObject.addProperty("message", "Product Added to the Cart");
                        } else {
                            responseObject.addProperty("message", "Insufficient Product Quantity!");
                        }
                    } else { // product available
                        Cart cart = (Cart) c1.uniqueResult();
                        int newQty = cart.getQty() + Integer.parseInt(qty);
                        if (newQty <= product.getQty()) {
                            cart.setQty(newQty);
                            s.update(cart);
                            tr.commit();
                            responseObject.addProperty("status", true);
                            responseObject.addProperty("message", "Product Cart Updated");
                        } else {
                            responseObject.addProperty("message", "Insufficient Product Quantity!");
                        }
                    }
                } else { // add product to session cart -> user not available in session

                    HttpSession ses = request.getSession();
                    if (ses.getAttribute("sessionCart") == null) { // sessionCart not available in the session
                        if (Integer.parseInt(qty) <= product.getQty()) {
                            List<Cart> sessCarts = new ArrayList<>();
                            Cart cart = new Cart();
                            cart.setQty(Integer.parseInt(qty));
                            cart.setUser(null);
                            cart.setProduct(product);
                            sessCarts.add(cart);
                            ses.setAttribute("sessionCart", sessCarts);
                            responseObject.addProperty("status", true);
                            responseObject.addProperty("message", "Product Added to the Cart");
                        } else {
                            responseObject.addProperty("message", "Insufficient Product Quantity!");
                        }
                    } else { // sessionCart available
                        List<Cart> sessionList = (List<Cart>) ses.getAttribute("sessionCart");
                        Cart foundedCart = null;
                        for (Cart cart : sessionList) {
                            if (cart.getProduct().getId() == product.getId()) {
                                foundedCart = cart; // reassigned by using the exists cart
                                break;
                            }
                        }
                        if (foundedCart != null) {
                            int newQty = foundedCart.getQty() + Integer.parseInt(qty);
                            if (newQty <= product.getQty()) {
                                foundedCart.setUser(null);
                                foundedCart.setQty(newQty);
                                responseObject.addProperty("status", true);
                                responseObject.addProperty("message", "Product Cart Updated");
                            } else {
                                responseObject.addProperty("message", "Insufficient Product Quantity!");
                            }
                        } else {
                            if (Integer.parseInt(qty) <= product.getQty()) {
                                foundedCart = new Cart(); // assign a new Cart object
                                foundedCart.setQty(Integer.parseInt(qty));
                                foundedCart.setUser(null);
                                foundedCart.setProduct(product);
                                sessionList.add(foundedCart);
                                responseObject.addProperty("status", true);
                                responseObject.addProperty("message", "Product Added to the Cart");
                            } else {
                                responseObject.addProperty("message", "Insufficient Product Quantity!");
                            }
                        }
                    }
                }
            }
        }
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject reqData = gson.fromJson(request.getReader(), JsonObject.class);

        int productId = reqData.get("productId").getAsInt();
        int qty = reqData.get("qty").getAsInt();

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.openSession();

        Product product = (Product) s.get(Product.class, productId);
        if (product == null) {
            responseObject.addProperty("message", "Product Not Found!");
        } else {
            HttpSession ses = request.getSession();
            User user = (User) ses.getAttribute("user");
            if (user != null) {
                // Update DB cart
                Criteria c1 = s.createCriteria(Cart.class);
                c1.add(Restrictions.eq("user", user));
                c1.add(Restrictions.eq("product", product));
                Cart cart = (Cart) c1.uniqueResult();
                if (cart != null) {
                    if (qty > 0 && qty <= product.getQty()) {
                        cart.setQty(qty);
                        s.beginTransaction();
                        s.update(cart);
                        s.getTransaction().commit();
                        responseObject.addProperty("status", true);
                        responseObject.addProperty("message", "Product Cart Updated");
                    } else {
                        responseObject.addProperty("message", "Insufficient Product Quantity!");
                    }
                } else {
                    responseObject.addProperty("message", "Cart item not found");
                }
            } else {
                // Update session cart
                List<Cart> sessionList = (List<Cart>) ses.getAttribute("sessionCart");
                if (sessionList != null) {
                    Cart found = null;
                    for (Cart c : sessionList) {
                        if (c.getProduct().getId() == productId) {
                            found = c;
                            break;
                        }
                    }
                    if (found != null) {
                        if (qty > 0 && qty <= product.getQty()) {
                            found.setQty(qty);
                            responseObject.addProperty("status", true);
                            responseObject.addProperty("message", "Product Cart Updated");
                        } else {
                            responseObject.addProperty("message", "Insufficient Product Quantity!");
                        }
                    } else {
                        responseObject.addProperty("message", "Cart item not found");
                    }
                } else {
                    responseObject.addProperty("message", "Cart is empty");
                }
            }
        }
        s.close();
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));
    }
}
