package com.sringa.unload.service;

import org.json.JSONObject;

/**
 * Created by sakula on 21.01.2018.
 */

public interface IRequestManager {

    public static enum Method {

        POST("POST"),
        PUT("PUT"),
        DELETE("DELETE");
        private String value;

        Method(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public static class Response {

        private boolean success = false;
        private String response, error;

        public void setResponse(String response) {
            this.response = response;
        }

        public void setError(String error) {
            this.error = error;
        }

        public void markSuccess() {
            this.success = true;
        }

        public String getResponse() {
            return response;
        }

        public String getError() {
            return error;
        }

        public boolean isSuccess() {
            return success;
        }
    }

    public interface IRequestHandler {
        void onComplete(Response response);
    }

    void sendAsyncRequest(Method method, String urlStr, JSONObject jsonObject, IRequestHandler handler);
}
