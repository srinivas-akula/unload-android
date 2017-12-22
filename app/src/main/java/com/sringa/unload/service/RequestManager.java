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

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.sringa.unload.utils.Utils;

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

    public static boolean sendGetRequest(Context context, String request, boolean showMessage) {

        final boolean isOnline = Utils.isOnline(context);
        if (isOnline) {
            if (!TextUtils.isEmpty(request))
                //TODO: Send post request.
                return true;
            return false;
        } else {
            if (showMessage) {
                Toast.makeText(context, "No Internet connection.",
                        Toast.LENGTH_LONG).show();
            }
            return false;
        }
    }
}
