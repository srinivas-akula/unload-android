package com.sringa.unload.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AssetsPropertyReader {

    public static Properties getProperties(Context context, String fileName) {
        final Properties properties = new Properties();
        try {
            AssetManager assetManager = context.getAssets();
            InputStream inputStream = assetManager.open(fileName);
            properties.load(inputStream);
        } catch (IOException e) {
            Log.e("AssetsPropertyReader", e.toString());
        }
        return properties;
    }
}
