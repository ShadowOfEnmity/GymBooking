package ru.kostrikov.gymbooking.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class UrlPath {
    public static final String LOGIN = "/login";
    public static final String LOGOUT = "/logout";


    public static final String HOME = "/home";
    public static final String USERS = "/users";
    public static final String BOOKINGS = "/bookings";
    public static final String GYMS = "/gyms";
    public static final String GYM = "/gym";
    public static final String NEW_BOOKING = "/new-booking";
    public static final String TRAINING_SESSIONS = "/training-sessions";
    public static final String TRAINING_SESSION = "/training-session";
    public static final String IMAGE = "/image";
    public static final String PHOTOS = "/photos";
    public static final String PHOTO = "/photo";
    public static final String UPLOAD_PHOTO = "/photo/upload-photo";
    public static final String USER = "/user";


    public static String buildUrlPathWithContext(String context, String path) {
        StringBuilder sb = new StringBuilder();
        sb.append(context);
        sb.append(path);
        return sb.toString();
    }
}
