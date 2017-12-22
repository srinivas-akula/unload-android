package com.sringa.unload.db;

public interface Constants {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "service.db";
    public static final String SERVER_URL = "http://app.unloadweb.in:3434";
    public static final int INTERVAL = 300;
    public static final String PROVIDER = "gps";
    public static final String REST_END = "api/";
    public static final String REST_SEND_OTP = REST_END + "sendotp";
    public static final String REST_VERIFY_OTP = REST_END + "verifyotp";
    public static final String REST_RESEND_OTP = REST_END + "resendotp";
}
