package controller;

import hibernate.Cart;
import hibernate.HibernateUtil;
import hibernate.Product;
import hibernate.User;
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
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "CheckSessionCart", urlPatterns = {"/CheckSessionCart"})
public class CheckSessionCart extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        if (user != null) {
            List<Cart> sessionCarts = (List<Cart>) request.getSession().getAttribute("sessionCart");
            if (sessionCarts != null) {
                SessionFactory sf = HibernateUtil.getSessionFactory();
                Session s = sf.openSession();
                Transaction tr = s.beginTransaction();

                Criteria c1 = s.createCriteria(Cart.class);
                c1.add(Restrictions.eq("user", user));
                for (Cart sessionCart : sessionCarts) {
                    Product product = (Product) s.get(Product.class, sessionCart.getProduct().getId());
                    
                    c1.add(Restrictions.eq("product", sessionCart.getProduct()));
                    if (c1.list().isEmpty()) { // product not available in same product id
                        sessionCart.setUser(user); // session user
                        s.save(sessionCart); 
                    } else { // product available
                        Cart dbCart = (Cart) c1.uniqueResult();
                        int newQty = sessionCart.getQty() + dbCart.getQty();
                        if (newQty <= product.getQty()) {
                            dbCart.setQty(newQty);
                            dbCart.setUser(user);
                            s.update(dbCart);
                        }
                    }
                }
                tr.commit();
                s.close();
                request.getSession().setAttribute("sessionCart", null);
            }
        }
    }
}
