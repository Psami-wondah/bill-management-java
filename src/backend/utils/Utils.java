package backend.utils;

public class Utils {

    public static String generateCode(int digits) {
        return String.format("%0" + digits + "d", new java.util.Random().nextLong((long) Math.pow(10, digits)));
    }

}
