package com.atog.grrrben.share;

import java.util.regex.Pattern;

/**
 * A place where app-wide settings can be configured.
 */
class AppConfig {

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =  Pattern.compile(
            "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
            Pattern.CASE_INSENSITIVE
    );

    // time settings in ms
    // Checking for updates when the app is _not_ in the foreground
    static int INTERVAL_UPDATE_INACTIVE = 120000;

    // uri for the API endpoints
    static String URL_LOGIN = "https://talcual.atog.nl/json/user/";
    static String URL_CONTACTS = "https://talcual.atog.nl/json/contacts/";
    static String URL_REGISTER = "https://talcual.atog.nl/json/user/register";
    static String URL_MESSAGE = "https://talcual.atog.nl/json/random/";
}
