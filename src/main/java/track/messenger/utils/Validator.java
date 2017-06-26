package track.messenger.utils;

import java.util.regex.Pattern;

/**
 * Created by artem on 23.06.17.
 */
public class Validator {
    public static final int MIN_USERNAME_SYMBOLS = 3;
    public static final int MIN_PASSWORD_SYMBOLS = 5;

    public static String validateUsername(String name) {
        if (null == name) {
            return "Username cannot be null";
        } else if (MIN_USERNAME_SYMBOLS > name.length()) {
            return String.format("Username should contain at least %d symbols", MIN_USERNAME_SYMBOLS);
        } else {
            Pattern pattern = Pattern.compile("[0-9a-zA-Z_]+");
            if (false == pattern.matcher(name).matches()) {
                return "Only followed symbols are allowed for username: A-Z, a-z, 0-9, _";
            } else {
                return null;
            }
        }
    }

    public static String validatePassword(String password) {
        if (null == password) {
            return "Password cannot be null";
        } else if (MIN_PASSWORD_SYMBOLS > password.length()) {
            return String.format("Password should contain at least %d symbols", MIN_PASSWORD_SYMBOLS);
        } else {
            Pattern alpha = Pattern.compile("[a-zA-Z]");
            if (false == alpha.matcher(password).find()) {
                return "Password should contain at least one character: A-Z, a-z";
            } else {
                Pattern numeric = Pattern.compile("[0-9]");
                if (false == numeric.matcher(password).find()) {
                    return "Password should contain at least one numeric symbol: 0-9";
                } else {
                    return null;
                }
            }
        }
    }

}
