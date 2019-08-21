package com.dso30bt.project2019.potapp.utils;

/**
 * Created by Joesta on 2019/05/30.
 */

public class Constants {
    public static final int SPLASH_TIME = 2000;
    public static final String USER_COLLECTION = "users";
    public static final String CONSTRUCTOR_COLLECTION = "constructors";
    public static final String ENGINEER_COLLECTION = "Engineer";
    public static final String UUID_COLLECTION = "uuid";
    public static final String POTHOLE_ID = "pothole_id";
    public static final String NAMES_PATTERN = "^[a-zA-Z]{3,25}$";

    /*intend extras*/
    public static final String EXTRA_EMAIL = "email";
    public static final String EXTRA_FULLNAME = "fullName";

    private Constants() {
    }

    public static class DocumentFields {
        public static final String FIST_NAME = "firstName";
        public static final String LAST_NAME = "lastName";
        public static final String CELL_NUMBER = "cellNumber";


        public static final String ROLE = "role";
        public static final String AUTO_ID = "id";
        public static final String EMAIL = "email";
        public static final String STATUS = "status";
        public static final String GENDER = "gender";
        public static final String USER_ID = "userId";
        public static final String REPORTS = "reports";
        public static final String ASSIGNED = "assigned";
        public static final String PASSWORD = "password";
        public static final String POTHOLES = "potholes";
        public static final String ID_NUMBER = "idNumber";
        public static final String USERS_IMAGE_URL = "imageUrl";
    }
}