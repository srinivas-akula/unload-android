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

import com.sringa.unload.activity.StatusActivity;
import com.sringa.unload.db.AppDataBase;
import com.sringa.unload.db.AppUser;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RequestManagerImpl implements IRequestManager {

    private class RequestAsyncTask extends AsyncTask<String, Void, IRequestManager.Response> {

        private JSONObject jsonObj;
        private IRequestHandler handler;

        public RequestAsyncTask(IRequestHandler handler, JSONObject jsonObj) {
            this.handler = handler;
            this.jsonObj = jsonObj;
        }

        @Override
        protected IRequestManager.Response doInBackground(String... request) {
            return sendRequest(request[0], request[1], jsonObj);
        }

        @Override
        protected void onPostExecute(IRequestManager.Response result) {
            handler.onComplete(result);
        }
    }

    private static IRequestManager.Response sendRequest(String urlStr, String method, JSONObject jsonObject) {

        final IRequestManager.Response response = new IRequestManager.Response();
        if (AppDataBase.INSTANCE.isOnline()) {
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
                StatusActivity.addMessage("URL " + urlStr + ", Method " + method);
                urlConnection.connect();
                int responseCode = urlConnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    response.markSuccess();
                    response.setResponse(readStream(urlConnection.getInputStream()));
                } else {
                    response.setError(readStream(urlConnection.getErrorStream()));
                    StatusActivity.addMessage("Error: " + response.getResponse());
                }
            } catch (IOException e) {
                StatusActivity.addMessage("URL " + urlStr + ", Method " + method + " Exception: " + e.toString());
            }
        }
        return response;
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
            StatusActivity.addMessage("Read Response: " + e.toString());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    StatusActivity.addMessage("Read Response: " + e.toString());
                }
            }
        }
        return response.toString();
    }

    @Override
    public void sendAsyncRequest(Method method, String urlStr, JSONObject jsonObject, IRequestHandler handler) {
        sendAsyncRequest(method.getValue(), urlStr, jsonObject, handler);
    }

    private void sendAsyncRequest(String method, String urlStr, JSONObject jsonObject, IRequestHandler handler) {
        RequestAsyncTask task = new RequestAsyncTask(handler, jsonObject);
        task.execute(urlStr, method);
    }
}
