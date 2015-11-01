package com.pulse.cloudnotify2.data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Vusi on 01 Nov 2015.
 */
public class Utility {

    //this class verifies information submitted by the user...

    private static Pattern pattern;
    private static Matcher matcher;

    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" +
            "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
         /*
          * Validate String EMAIL_PATTERN with regex characters.
          *
         */
    public static boolean vaidateEmail(String email){

        //validating user input (matcher = user email address and pattern = regex characters

        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher  = pattern.matcher(email);

        return matcher.matches();
    }

}
