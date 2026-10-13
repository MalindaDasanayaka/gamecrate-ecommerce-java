package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Address;
import hibernate.City;
import hibernate.HibernateUtil;
import hibernate.User;
import java.io.IOException;
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
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "SaveAddress", urlPatterns = {"/SaveAddress"})
public class SaveAddress extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject userData = gson.fromJson(request.getReader(), JsonObject.class);

        String lineOne = userData.get("lineOne").getAsString();
        String lineTwo = userData.get("lineTwo").getAsString();
        String postalCode = userData.get("postalCode").getAsString();
        int cityId = userData.get("cityId").getAsInt();

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        if (lineOne.isEmpty()) {
            responseObject.addProperty("message", "Address Line 1 can not be empty!");
        } else if (lineOne.length() > 45) {
            responseObject.addProperty("message", "Address Line 1 can not be greater 45 characters!");
        } else if (lineTwo.isEmpty()) {
            responseObject.addProperty("message", "Address Line 2 can not be empty!");
        } else if (lineTwo.length() > 45) {
            responseObject.addProperty("message", "Address Line 2 can not be greater 45 characters!");
        } else if (cityId == 0) {
            responseObject.addProperty("message", "Please Select a City!");
        } else if (postalCode.isEmpty()) {
            responseObject.addProperty("message", "Postal Code can not be empty!");
        } else if (!Util.isCodeValid(postalCode)) {
            responseObject.addProperty("message", "Please enter a valid postal code!");
        } else {
            HttpSession ses = request.getSession();
            if (ses.getAttribute("user") != null) {
                User u = (User) ses.getAttribute("user"); //get session user

                SessionFactory sf = HibernateUtil.getSessionFactory();
                Session s = sf.openSession();

                City city = (City) s.load(City.class, cityId); //primary key search
                Address address = new Address();
                address.setLine_1(lineOne);
                address.setLine_2(lineTwo);
                address.setPostal_code(postalCode);
                address.setCity(city);
                address.setUser(u);

                s.save(address);

                s.beginTransaction().commit();
                responseObject.addProperty("status", true);
                responseObject.addProperty("message", "Address details successfully added!");
                s.close();
            }
        }

        String responseText = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(responseText);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession ses = request.getSession(false);
        if (ses != null && ses.getAttribute("user") != null) {
            User user = (User) ses.getAttribute("user");
            JsonObject responseObject = new JsonObject();

            Gson gson = new Gson();
            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();
            Criteria c = s.createCriteria(Address.class);
            c.add(Restrictions.eq("user", user));
            if (!c.list().isEmpty()) {
                List<Address> addressList = c.list();
                responseObject.add("addressList", gson.toJsonTree(addressList));
            }

            String toJson = gson.toJson(responseObject);
            response.setContentType("application/json");
            response.getWriter().write(toJson);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject userData = gson.fromJson(request.getReader(), JsonObject.class);

        int addressId = userData.get("addressId").getAsInt();
        String lineOne = userData.get("lineOne").getAsString();
        String lineTwo = userData.get("lineTwo").getAsString();
        String postalCode = userData.get("postalCode").getAsString();
        int cityId = userData.get("cityId").getAsInt();

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        if (lineOne.isEmpty()) {
            responseObject.addProperty("message", "Address Line 1 can not be empty!");
        } else if (lineOne.length() > 45) {
            responseObject.addProperty("message", "Address Line 1 can not be greater 45 characters!");
        } else if (lineTwo.isEmpty()) {
            responseObject.addProperty("message", "Address Line 2 can not be empty!");
        } else if (lineTwo.length() > 45) {
            responseObject.addProperty("message", "Address Line 2 can not be greater 45 characters!");
        } else if (cityId == 0) {
            responseObject.addProperty("message", "Please Select a City!");
        } else if (postalCode.isEmpty()) {
            responseObject.addProperty("message", "Postal Code can not be empty!");
        } else if (!Util.isCodeValid(postalCode)) {
            responseObject.addProperty("message", "Please enter a valid postal code!");
        } else {
            HttpSession ses = request.getSession();
            if (ses.getAttribute("user") != null) {
                User u = (User) ses.getAttribute("user"); //get session user

                SessionFactory sf = HibernateUtil.getSessionFactory();
                Session s = sf.openSession();

                Address address = (Address) s.load(Address.class, addressId);

                if (address.getUser().getId() != u.getId()) {
                    responseObject.addProperty("message", "You can only update your own addresses!");
                } else {
                    City city = (City) s.load(City.class, cityId);

                    address.setLine_1(lineOne);
                    address.setLine_2(lineTwo);
                    address.setPostal_code(postalCode);
                    address.setCity(city);

                    s.update(address);
                    s.beginTransaction().commit();

                    responseObject.addProperty("status", true);
                    responseObject.addProperty("message", "Address updated successfully!");
                }

                s.close();

            } else {
                responseObject.addProperty("message", "User session not found!");
            }
        }

        String responseText = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(responseText);
    }
}
