package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.User;
import hibernate.UserStatus;
import java.io.IOException;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.Mail;
import model.Util;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "SignUp", urlPatterns = {"/SignUp"})
public class SignUp extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject user = gson.fromJson(request.getReader(), JsonObject.class);

        String firstName = user.get("firstName").getAsString();
        String lastName = user.get("lastName").getAsString();
        String mobile = user.get("mobile").getAsString();
        final String email = user.get("email").getAsString();
        String password = user.get("password").getAsString();

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        if (firstName.isEmpty()) {
            responseObject.addProperty("message", "First Name can not be empty!");
        } else if (firstName.length() > 45) {
            responseObject.addProperty("message", "First Name can not be greater than 45 characters!");
        } else if (lastName.isEmpty()) {
            responseObject.addProperty("message", "Last Name can not be empty!");
        } else if (lastName.length() > 45) {
            responseObject.addProperty("message", "Last Name can not be greater than 45 characters!");
        } else if (mobile.isEmpty()) {
            responseObject.addProperty("message", "Mobile Number can not be empty!");
        } else if (!Util.isMobileValid(mobile)) {
            responseObject.addProperty("message", "Please enter a valid Mobile Number!");
        } else if (email.isEmpty()) {
            responseObject.addProperty("message", "Email Address can not be empty!");
        } else if (email.length() > 100) {
            responseObject.addProperty("message", "Email Address can not be greater than 100 characters!");
        } else if (!Util.isEmailValid(email)) {
            responseObject.addProperty("message", "Please enter a valid Email Address!");
        } else if (password.isEmpty()) {
            responseObject.addProperty("message", "Password can not be empty!");
        } else if (!Util.isPasswordValid(password)) {
            responseObject.addProperty("message", "Password must contain atleast an uppercase, a lowercase,"
                    + "a number, a special character and between 8 to 20 characters!");
        } else {
            //hibernate save
            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();

            Criteria criteria = s.createCriteria(User.class);
            criteria.add(Restrictions.eq("email", email));

            if (!criteria.list().isEmpty()) {
                responseObject.addProperty("message", "User with this Email already exists!");
            } else {
                User u = new User();
                u.setFirst_name(firstName);
                u.setLast_name(lastName);
                u.setMobile(mobile);
                u.setEmail(email);
                u.setPassword(password);

                //generate verification code
                final String verificationCode = Util.generateCode();
                u.setVerification(verificationCode);
                //generate verification code

                u.setCreated_at(new Date());

                UserStatus status = (UserStatus) s.get(UserStatus.class, 1);
                u.setUserStatus(status);

                s.save(u);
                s.beginTransaction().commit();
                //hibernate save

                //send email
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Mail.sendMail(email, "Your GameCrate.lk Verification Code", "<body style=\"font-family: JetBrains Mono, sans-serif; background-color: #0b1120; color: #ffffff; padding: 20px;\">"
                                + "  <div style=\"max-width: 600px; margin: auto; background-color: #1a2238; border-radius: 10px; padding: 30px; border: 1px solid #333;\">"
                                + "    <h2 style=\"text-align: center; color: #1ecb5c;\">GAME<span style=\"color: #ffffff;\">CRATE</span><span style=\"color: #00c3ff;\">.LK</span></h2>"
                                + "    <p style=\"font-size: 16px; color: #ffffff;\">Hi there,</p>"
                                + "    <p style=\"font-size: 16px; color: #ffffff;\">"
                                + "      Thank you for signing up with <strong style=\"color: #ffffff;\">GameCrate.lk</strong>! To complete your registration, please use the verification code below:"
                                + "    </p>"
                                + "    <div style=\"text-align: center; margin: 30px 0;\">"
                                + "      <span style=\"display: inline-block; background-color: #1ecb5c; color: #000; padding: 15px 30px; font-size: 24px; font-weight: bold; border-radius: 8px;\">"
                                + verificationCode
                                + "      </span>"
                                + "    </div>"
                                + "    <p style=\"font-size: 16px; color: #ffffff;\">"
                                + "      If you did not request this, you can safely ignore this email."
                                + "    </p>"
                                + "    <p style=\"font-size: 16px; color: #ffffff;\">Happy gaming!<br><strong style=\"color: #ffffff;\">The GameCrate.lk Team</strong></p>"
                                + "    <hr style=\"margin: 40px 0; border-color: #333;\">"
                                + "    <p style=\"font-size: 12px; color: #888888; text-align: center;\">"
                                + "      Need help? Contact us at <a href=\"mailto:support@gamecrate.lk\" style=\"color: #1ecb5c;\">support@gamecrate.lk</a>"
                                + "    </p>"
                                + "  </div>"
                                + "</body>");
                    }
                }).start();
                //send email

                //session management
                HttpSession ses = request.getSession();
                ses.setAttribute("email", email);
                //session management

                responseObject.addProperty("status", true);
                responseObject.addProperty("message", "Registration success. Please check your email for the verification code");
            }
            s.close();
        }

        String responseText = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(responseText);
    }
}
