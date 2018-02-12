/*
 * Copyright 2012 - 2016 Anton Tananaev (anton.tananaev@gmail.com)
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

import android.net.Uri;
import android.util.Log;

import com.sringa.unload.db.AppUser;
import com.sringa.unload.db.Position;
import com.sringa.unload.db.VehicleDetail;

import org.json.JSONException;
import org.json.JSONObject;

public class ProtocolFormatter {

    public static String formatRequest(String url, Position position) {
        return formatRequest(url, position, null);
    }

    public static String formatRequest(String url, Position position, String alarm) {
        Uri serverUrl = Uri.parse(url);
        Uri.Builder builder = serverUrl.buildUpon()
                .appendQueryParameter("id", position.getDeviceId())
                .appendQueryParameter("timestamp", String.valueOf(position.getTime().getTime() / 1000))
                .appendQueryParameter("lat", String.valueOf(position.getLatitude()))
                .appendQueryParameter("lon", String.valueOf(position.getLongitude()))
                .appendQueryParameter("speed", String.valueOf(position.getSpeed()))
                .appendQueryParameter("bearing", String.valueOf(position.getCourse()))
                .appendQueryParameter("altitude", String.valueOf(position.getAltitude()))
                .appendQueryParameter("accuracy", String.valueOf(position.getAccuracy()))
                .appendQueryParameter("batt", String.valueOf(position.getBattery()));

        if (alarm != null) {
            builder.appendQueryParameter("alarm", alarm);
        }
        return builder.build().toString();
    }

    public static JSONObject toJson(AppUser user) {

        JSONObject obj = new JSONObject();
        try {
            obj.put("phone", user.getPhone());
            obj.put("mode", user.getMode());
            obj.put("uid", user.getUid());
            obj.put("providerid", user.getProviderid());
            obj.put("password", user.getPassword());
            obj.put("displayname", user.getDisplayname());
        } catch (JSONException e) {
            Log.e("AppUser to JSON", e.getLocalizedMessage());
        }
        return obj;
    }

    public static JSONObject toJson(VehicleDetail vehicle) {

        JSONObject obj = new JSONObject();
        try {
            obj.put("name", vehicle.getNumber());
            obj.put("uniqueId", vehicle.getNumber());
            obj.put("model", vehicle.getModel());
            obj.put("category", "truck");
            JSONObject attributes = new JSONObject();
            attributes.put("tonnage", vehicle.getTonnage());
            attributes.put("load", vehicle.getLoad());
            attributes.put("axle", vehicle.getAxle());
            obj.put("attributes", attributes);

        } catch (JSONException e) {
            Log.e("Vehicle to JSON", e.getLocalizedMessage());
        }
        return obj;
    }

    public static JSONObject toJson(VehicleDetail vehicleDetail, String state) {

        JSONObject obj = new JSONObject();
        try {
            obj.put("id", vehicleDetail.getNumber());
            obj.put("address", state);
            obj.put("load", vehicleDetail.getLoad());
        } catch (JSONException e) {
            Log.e("Position to JSON", e.getLocalizedMessage());
        }
        return obj;
    }
}
