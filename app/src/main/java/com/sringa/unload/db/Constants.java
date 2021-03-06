package com.sringa.unload.db;

public interface Constants {

    public static final boolean DUMMY = false;
    public static final int DATABASE_VERSION = 1;
    public static final String USER_PASSWORD = "unload";
    public static final String DATABASE_NAME = "unload.db";
    public static final String SERVER = "http://192.168.0.185";
    public static final String SERVER_URL = SERVER + ":8082";
    public static final String POSITION_SERVER_URL = SERVER + ":5055";
    public static final int INTERVAL = 300;
    public static final String PROVIDER = "gps";
    public static final String REST_END = "/api";
    public static final String VEHICLE_RESOURCE = SERVER_URL + REST_END + "/devices";
    public static final String POSITION_RESOURCE = SERVER_URL + REST_END + "/positions";
    public static final String VEHICLE_ID_RESOURCE = SERVER_URL + REST_END + "/devices/%s";
    public static final String APP_USER_RESOURCE = SERVER_URL + REST_END + "/users/app";
    public static final String APP_USER_CHANGE_PWD = SERVER_URL + REST_END + "/users/changepwd";
}
