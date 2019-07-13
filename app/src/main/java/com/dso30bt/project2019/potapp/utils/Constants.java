package com.dso30bt.project2019.potapp.utils;

/**
 * Created by Joesta on 2019/05/30.
 */

public class Constants {
    public static final int SPLASH_TIME = 2000;
    public static final String USER_COLLECTION = "users";
    public static final String CONSTRUCTOR_COLLECTION = "constructors";
    public static final String ENGINEER_COLLECTION = "engineers";

    private Constants() {
    }

    public static class DocumentFields {
        public static final String NAME = "name";
        public static final String EMAIL = "email";
        public static final String GENDER = "gender";
        public static final String USER_ID = "userId";
        public static final String REPORTS = "reports";
        public static final String SURNAME = "surname";
        public static final String PASSWORD = "password";
        public static final String POTHOLES = "potholes";
        public static final String ID_NUMBER = "idNumber";
        public static final String CELL_NUMBER = "cellNumber";
    }
}