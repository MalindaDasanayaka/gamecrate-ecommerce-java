package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Address;
import hibernate.Cart;
import hibernate.City;
import hibernate.DeliveryType;
import hibernate.HibernateUtil;
import hibernate.OrderItems;
import hibernate.OrderStatus;
import hibernate.Orders;
import hibernate.Product;
import hibernate.User;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Mail;
import model.PayHere;
import model.Util;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "CheckOut", urlPatterns = {"/CheckOut"})
public class CheckOut extends HttpServlet {

    private static final int SELECTOR_DEFAULT_VALUE = 0;
    private static final int ORDER_PAID = 1;
    private static final int WITHIN_KURUNEGALA = 1;
    private static final int OUT_OF_KURUNEGALA = 2;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject requJsonObject = gson.fromJson(request.getReader(), JsonObject.class);
        int addressId = requJsonObject.get("addressId").getAsInt();
        String citySelect = requJsonObject.get("citySelect").getAsString();
        String lineOne = requJsonObject.get("lineOne").getAsString();
        String lineTwo = requJsonObject.get("lineTwo").getAsString();
        String postalCode = requJsonObject.get("postalCode").getAsString();

        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.openSession();
        Transaction tr = s.beginTransaction();

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            responseObject.addProperty("message", "Session expired! Please log in again");
        } else {
            if (addressId != 0) {
                Address address = (Address) s.get(Address.class, addressId);
                if (address == null) {
                    responseObject.addProperty("message", "Your current address is not found. Please add a new address");
                } else {
                    processCheckout(s, tr, user, address, responseObject);
                }
            } else {
                if (lineOne.isEmpty()) {
                    responseObject.addProperty("message", "Address Line 1 can not be empty!");
                } else if (lineOne.length() > 45) {
                    responseObject.addProperty("message", "Address Line 1 can not be greater 45 characters!");
                } else if (lineTwo.isEmpty()) {
                    responseObject.addProperty("message", "Address Line 2 can not be empty!");
                } else if (lineTwo.length() > 45) {
                    responseObject.addProperty("message", "Address Line 2 can not be greater 45 characters!");
                } else if (!Util.isInteger(citySelect)) {
                    responseObject.addProperty("message", "Invalid City!");
                } else if (Integer.parseInt(citySelect) == CheckOut.SELECTOR_DEFAULT_VALUE) {
                    responseObject.addProperty("message", "Please Select a City!");
                } else {
                    City city = (City) s.get(City.class, Integer.valueOf(citySelect));
                    if (city == null) {
                        responseObject.addProperty("message", "Invalid City!");
                    } else {
                        if (postalCode.isEmpty()) {
                            responseObject.addProperty("message", "Postal Code is Required!");
                        } else if (!Util.isCodeValid(postalCode)) {
                            responseObject.addProperty("message", "Invalid Postal Code!");
                        } else {
                            Address address = new Address();
                            address.setLine_1(lineOne);
                            address.setLine_2(lineTwo);
                            address.setCity(city);
                            address.setPostal_code(postalCode);
                            address.setUser(user);
                            s.save(address);

                            processCheckout(s, tr, user, address, responseObject);
                        }
                    }
                }
            }
        }

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));
    }

    private void processCheckout(Session s, Transaction tr, User user, Address address, JsonObject responseObject) {
        try {
            double amount = 0;
            String items = "";

            DeliveryType withInKurunegala = (DeliveryType) s.get(DeliveryType.class, CheckOut.WITHIN_KURUNEGALA);
            DeliveryType outOfKurunegala = (DeliveryType) s.get(DeliveryType.class, CheckOut.OUT_OF_KURUNEGALA);
            OrderStatus orderStatus = (OrderStatus) s.get(OrderStatus.class, CheckOut.ORDER_PAID);

            Orders orders = new Orders();
            String orderId = Util.generateOrderId();
            orders.setId(orderId);
            orders.setUser(user);
            orders.setAddress(address);

            if (address.getCity().getName().equals("Kurunegala")) { // within Kurunegala
                amount += withInKurunegala.getPrice();
                orders.setDeliveryType(withInKurunegala);
            } else { // out of Kurunegala
                amount += outOfKurunegala.getPrice();
                orders.setDeliveryType(outOfKurunegala);
            }

            orders.setOrderStatus(orderStatus);
            Date date = new Date();
            orders.setCreated_at(date);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String purchaseDate = sdf.format(date);
            String order_status = orderStatus.getValue();

            s.save(orders);

            Criteria c1 = s.createCriteria(Cart.class);
            c1.add(Restrictions.eq("user", user));
            List<Cart> cartList = c1.list();

            StringBuilder tableRows = new StringBuilder();

            for (Cart cart : cartList) {
                amount += cart.getQty() * cart.getProduct().getPrice();

                OrderItems orderItems = new OrderItems();

                items += cart.getProduct().getTitle() + " x " + cart.getQty() + ", ";

                Product product = cart.getProduct();
                orderItems.setProduct(product);
                orderItems.setQty(cart.getQty());
                orderItems.setOrders(orders);

                s.save(orderItems);

                double itemTotal = cart.getQty() * cart.getProduct().getPrice();

                tableRows.append("<tr>\n")
                        .append("<td style=\"border: 1px solid #333; padding: 12px; text-align: left;\">")
                        .append(cart.getProduct().getTitle())
                        .append("</td>\n")
                        .append("<td style=\"border: 1px solid #333; padding: 12px; text-align: left;\">")
                        .append(cart.getQty())
                        .append("</td>\n")
                        .append("<td style=\"border: 1px solid #333; padding: 12px; text-align: left;\">Rs.")
                        .append(String.format("%,.2f", cart.getProduct().getPrice()))
                        .append("</td>\n")
                        .append("<td style=\"border: 1px solid #333; padding: 12px; text-align: left;\">Rs.")
                        .append(String.format("%,.2f", itemTotal))
                        .append("</td>\n")
                        .append("</tr>\n");

                // update product qty
                product.setQty(product.getQty() - cart.getQty());
                s.update(product);

                // delete cart items
                s.delete(cart);

            }

            tr.commit();

            // Add delivery charge row
            tableRows.append("<tr>\n")
                    .append("<td style=\"border: 1px solid #333; padding: 12px; text-align: left;\">Delivery</td>\n")
                    .append("<td style=\"border: 1px solid #333; padding: 12px; text-align: left;\">-</td>\n")
                    .append("<td style=\"border: 1px solid #333; padding: 12px; text-align: left;\">-</td>\n")
                    .append("<td style=\"border: 1px solid #333; padding: 12px; text-align: left;\">Rs.")
                    .append(String.format("%,.2f", orders.getDeliveryType().getPrice()))
                    .append("</td>\n")
                    .append("</tr>\n");

            // Add total row
            tableRows.append("<tr style=\"font-weight: bold;\">\n")
                    .append("<td colspan=\"3\" style=\"border: 1px solid #333; padding: 12px; text-align: left; color: #1ecb5c;\">Total</td>\n")
                    .append("<td style=\"border: 1px solid #333; padding: 12px; text-align: left; color: #1ecb5c;\">Rs.")
                    .append(String.format("%,.2f", amount))
                    .append("</td>\n")
                    .append("</tr>\n");

            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, 7);
            Date futureDate = calendar.getTime();
            String deliveryDate = sdf.format(futureDate);

            //send email
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Mail.sendMail(user.getEmail(), "Your Invoice for the Order #" + orderId + " placed at GameCrate.lk", "<body style=\"font-family: JetBrains Mono, sans-serif; background-color: #0b1120; color: #ffffff; padding: 20px;\">\n"
                            + "    <div style=\"max-width: 600px; margin: auto; background-color: #1a2238; border-radius: 10px; padding: 30px; border: 1px solid #333;\">\n"
                            + "        <h2 style=\"text-align: center; color: #1ecb5c;\">GAME<span style=\"color: #ffffff;\">CRATE</span><span style=\"color: #00c3ff;\">.LK</span></h2>\n"
                            + "        <h2 style=\"text-align: center; color: #ffffff;\">Invoice</h2>\n"
                            + "\n"
                            + "        <table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"margin-bottom: 20px;\">\n"
                            + "             <tr>\n"
                            + "                 <td style=\"vertical-align: top; padding-right: 200px;\">\n"
                            + "                     <p>\n"
                            + "                         <strong style=\"color: #1ecb5c;\">Order ID:</strong> #" + orderId + "<br>\n"
                            + "                         <strong style=\"color: #1ecb5c;\">Date:</strong> " + purchaseDate + "<br>\n"
                            + "                         <strong style=\"color: #1ecb5c;\">Status:</strong> " + order_status + "<br>\n"
                            + "                         <strong style=\"color: #1ecb5c;\">Estimated Delivery:</strong> " + deliveryDate + "\n"
                            + "                     </p>\n"
                            + "                 </td>\n"
                            + "                 <td style=\"vertical-align: top; text-align: left;\">\n"
                            + "                     <p>\n"
                            + "                         <strong style=\"color: #1ecb5c;\">Billed To:</strong><br>\n"
                            + "                             " + user.getFirst_name() + " " + user.getLast_name() + "<br>\n"
                            + "                             " + user.getMobile() + "<br>\n"
                            + "                             " + address.getLine_1() + ",<br>\n"
                            + "                             " + address.getLine_2() + ",<br>\n"
                            + "                             " + address.getCity().getName() + ", " + address.getPostal_code() + "\n"
                            + "                     </p>\n"
                            + "                 </td>\n"
                            + "             </tr>\n"
                            + "         </table>\n"
                            + "\n"
                            + "        <table style=\"width: 100%; border-collapse: collapse; margin-top: 20px;\">\n"
                            + "            <thead>\n"
                            + "                <tr>\n"
                            + "                    <th style=\"border: 1px solid #333; padding: 12px; text-align: left; background: #0b1120;\">Product</th>\n"
                            + "                    <th style=\"border: 1px solid #333; padding: 12px; text-align: left; background: #0b1120;\">Quantity</th>\n"
                            + "                    <th style=\"border: 1px solid #333; padding: 12px; text-align: left; background: #0b1120;\">Price</th>\n"
                            + "                    <th style=\"border: 1px solid #333; padding: 12px; text-align: left; background: #0b1120;\">Subtotal</th>\n"
                            + "                </tr>\n"
                            + "            </thead>\n"
                            + "            <tbody>\n"
                            + tableRows.toString()
                            + "            </tbody>\n"
                            + "        </table>\n"
                            + "\n"
                            + "        <p style=\"font-size: 0.9em; margin-top: 30px; color: #888888;\">\n"
                            + "            Thank you for shopping with GameCrate.lk!<br>\n"
                            + "            If you have any questions regarding this invoice, contact us at\n"
                            + "            <a href=\"mailto:support@gamecrate.lk\" style=\"color: #1ecb5c;\">support@gamecrate.lk</a>.\n"
                            + "        </p>\n"
                            + "    </div>\n"
                            + "</body>");
                }
            }).start();
            //send email

            //payHere process
            String merahantID = System.getenv("PAYHERE_MERCHANT_ID") != null && !System.getenv("PAYHERE_MERCHANT_ID").trim().isEmpty()
                    ? System.getenv("PAYHERE_MERCHANT_ID").trim()
                    : "1226818";
            String merchantSecret = System.getenv("PAYHERE_MERCHANT_SECRET") != null && !System.getenv("PAYHERE_MERCHANT_SECRET").trim().isEmpty()
                    ? System.getenv("PAYHERE_MERCHANT_SECRET").trim()
                    : "sandbox_merchant_secret";
            String orderID = "#" + orderId;
            String currency = "LKR";
            String formattedAmount = new DecimalFormat("0.00").format(amount);
            String merchantSecretMD5 = PayHere.generateMD5(merchantSecret);

            String hash = PayHere.generateMD5(merahantID + orderID + formattedAmount + currency + merchantSecretMD5);

            String notifyUrl = System.getenv("PAYHERE_NOTIFY_URL") != null && !System.getenv("PAYHERE_NOTIFY_URL").trim().isEmpty()
                    ? System.getenv("PAYHERE_NOTIFY_URL").trim()
                    : "http://localhost:8080/GameCrate/VerifyPayments";

            JsonObject payHereJson = new JsonObject();
            payHereJson.addProperty("sandbox", true);
            payHereJson.addProperty("merchant_id", merahantID);

            payHereJson.addProperty("return_url", "");
            payHereJson.addProperty("cancel_url", "");
            payHereJson.addProperty("notify_url", notifyUrl);

            payHereJson.addProperty("order_id", orderID);
            payHereJson.addProperty("items", items);
            payHereJson.addProperty("amount", formattedAmount);
            payHereJson.addProperty("currency", currency);
            payHereJson.addProperty("hash", hash);

            payHereJson.addProperty("first_name", user.getFirst_name());
            payHereJson.addProperty("last_name", user.getLast_name());
            payHereJson.addProperty("email", user.getEmail());

            payHereJson.addProperty("phone", user.getMobile());
            payHereJson.addProperty("address", address.getLine_1() + ", " + address.getLine_2());
            payHereJson.addProperty("city", address.getCity().getName());
            payHereJson.addProperty("country", "Sri Lanka");

            responseObject.addProperty("status", true);
            responseObject.addProperty("message", "Checkout completed");
            responseObject.add("payHereJson", new Gson().toJsonTree(payHereJson));

        } catch (Exception e) {
            tr.rollback();
        }
    }
}
