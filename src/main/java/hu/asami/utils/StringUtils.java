package hu.asami.utils;

import lombok.experimental.UtilityClass;

import java.util.regex.Pattern;

@UtilityClass
public class StringUtils {

    private static final String EMAIL_REGEX_PATTERN = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";

    public static boolean emailValidation(String email){
        return Pattern.compile(EMAIL_REGEX_PATTERN)
                .matcher(email)
                .matches();
    }
}
