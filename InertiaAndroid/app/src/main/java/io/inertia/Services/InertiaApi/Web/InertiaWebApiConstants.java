package io.inertia.Services.InertiaApi.Web;

import android.util.Base64;

import java.nio.charset.Charset;

/**
 * Created by Luke Wilimitis on 1/2/17.
 */
public class InertiaWebApiConstants {

    public static final String BASE_URL = "http://10.0.0.38:8000/";
    public static final String GRANT_TYPE_PASSWORD = "password";
    public static final String GRANT_TYPE_REFRESH_TOKEN = "refresh_token";
    public static final String CLIENT_ID = "JQlEMpctF1mIdskl0r5rFrpc8w0xZjbiZtnwucId\n";
    public static final String CLIENT_SECRET = "Ja9TquWXMLzNaaV78ezOhzHTWaOmKrOwqQDEZPzhf4rh8aCLZgrFAz5SZVzLborGzXL1uHfNB3UNH5EITEmvfwy4HO5vnqUYO4yhDNrAkG6yzyffF9Xkv5toRnnzoN2B";
    public static final String ENCODED_AUTH_HEADER = "Basic " + Base64.encodeToString((CLIENT_ID+":"+ CLIENT_SECRET).getBytes(Charset.forName("UTF-8")), Base64.NO_WRAP);
    public static final String AUTH_HEADER = "Authorization";
    public static final String AUTH_HEADER_BEARER = "Bearer";
    public static final int AUTH_DEFAULT_ATTEMPTS = 2;
    public static final int INERTIA_API_DEFAULT_ATTEMPTS = 2;
}
