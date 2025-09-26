package company.utils;

import java.text.SimpleDateFormat;
import java.text.ParseException;

public class GenericUtils {

    public boolean isDate(String value, SimpleDateFormat sdf) {
        try {
            sdf.parse(value);
            return true; // valid date
        } catch (ParseException e) {
            return false; // not a date
        }
    }
}
