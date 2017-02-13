package com.atog.grrrben.share;

/**
 * A place where app-wide settings can be configured.
 */
class AppConfig {
    // time settings
    // Checking for updates when the app is _not_ in the foreground
    static int INTERVAL_UPDATE_INACTIVE = 6000;

    // uri for the API endpoints
    static String URL_LOGIN = "https://talcual.atog.nl/json/user/";
    static String URL_CONTACTS = "https://talcual.atog.nl/json/contacts/";
    static String URL_REGISTER = "https://talcual.atog.nl/json/user/register";
    static String URL_MESSAGE = "https://talcual.atog.nl/json/random/";
}
