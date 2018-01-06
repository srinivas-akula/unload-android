/*
 * Copyright 2015 Anton Tananaev (anton.tananaev@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sringa.unload.service;

import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import com.sringa.unload.db.AppDataBase;
import com.sringa.unload.db.AppUser;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class RequestManager {

    private static final int TIMEOUT = 15 * 1000;

    public interface RequestHandler {
        void onComplete(boolean success);
    }

    private static class RequestAsyncTask extends AsyncTask<String, Void, Boolean> {

        private RequestHandler handler;

        public RequestAsyncTask(RequestHandler handler) {
            this.handler = handler;
        }

        @Override
        protected Boolean doInBackground(String... request) {
            return sendPostRequest(request[0]);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            handler.onComplete(result);
        }
    }

    public static boolean sendPostRequest(String request) {
        InputStream inputStream = null;
        try {
            URL url = new URL(request);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(TIMEOUT);
            connection.setConnectTimeout(TIMEOUT);
            connection.setRequestMethod("POST");
            connection.connect();
            inputStream = connection.getInputStream();
            while (inputStream.read() != -1) ;
            return true;
        } catch (IOException error) {
            return false;
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException secondError) {
                Log.w(RequestManager.class.getSimpleName(), secondError);
            }
        }
    }

    public static void sendRequestAsync(String request, RequestHandler handler) {
        RequestAsyncTask task = new RequestAsyncTask(handler);
        task.execute(request);
    }

    public static String sendPostRequest(String urlStr, JSONObject jsonObject) {
        return sendRequest(urlStr, "POST", jsonObject);
    }

    private static String sendRequest(String urlStr, String method, JSONObject jsonObject) {
        String serverResponse = null;
        try {
            URL url = new URL(urlStr);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setRequestMethod(method);
            AppUser user = AppDataBase.INSTANCE.getAppUser();
            final String encodedAuth = buildBasicAuthorizationString(user.getPhone(), user.getPassword());
            urlConnection.setRequestProperty("Authorization", encodedAuth);
            urlConnection.setRequestProperty("Content-Type", "application/json");

            if (null != jsonObject) {
                DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
                wr.writeBytes(jsonObject.toString());
                wr.flush();
                wr.close();
            }
            urlConnection.connect();

            int responseCode = urlConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                serverResponse = readStream(urlConnection.getInputStream());
            }
        } catch (MalformedURLException e) {
            Log.e("POST request", e.getLocalizedMessage());
        } catch (IOException e) {
            Log.e("POST request", e.getLocalizedMessage());
        }
        return serverResponse;

    }

    public static String sendPutRequest(String urlStr, JSONObject jsonObject) {
        return sendRequest(urlStr, "PUT", jsonObject);
    }

    public static String sendDeleteRequest(String urlStr) {
        return sendRequest(urlStr, "DELETE", null);
    }

    private static String buildBasicAuthorizationString(String username, String password) {

        final String credentials = username + ":" + password;
        return "Basic " + new String(Base64.encode(credentials.getBytes(), Base64.DEFAULT));
    }

    private static String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuffer response = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            Log.e("Read Response", e.getLocalizedMessage());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e("Read Response", e.getLocalizedMessage());
                }
            }
        }
        return response.toString();
    }
}
