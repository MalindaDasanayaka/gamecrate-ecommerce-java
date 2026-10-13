package model;

public class Util {
    
    public static String generateCode() {
        double r = Math.random();
        int x = (int) (r * 1000000);
        return String.format("%06d", x);
    }
    
    public static String generateOrderId() {
        double r = Math.random();
        int x = (int) (r * 100000000);
        return String.format("%08d", x);
    }
    
    public static boolean isEmailValid(String email) {
        return email.matches("^[a-zA-Z0-9_!#$%&amp;'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$");
    }
    
    public static boolean isPasswordValid(String password) {
        return password.matches("^.*(?=.{8,20})(?=..*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$");
    }
    
    public static boolean isMobileValid(String mobile) {
        return mobile.matches("^(0{1})(7{1})([0|1|2|4|5|6|7|8]{1})([0-9]{7})");
    }
    
    public static boolean isCodeValid(String code) {
        return code.matches("^\\d{4,5}$");
    }
    
    public static boolean isInteger(String value) {
        return value.matches("^\\d+$");
    }
    
    public static boolean isDouble(String value) {
        return value.matches("^\\d+(\\.\\d{2})?$");
    }
}
