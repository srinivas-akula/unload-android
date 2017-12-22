package com.sringa.unload.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.HashMap;
import java.util.Map;

import com.sringa.unload.service.R;

public abstract class BaseActivity extends AppCompatActivity {

    private static final Map<String, Class> classMap = new HashMap<String, Class>() {
        {
            put("vehicles", VehicleListActivity.class);
            put("status", StatusActivity.class);
            put("about", AboutActivity.class);
        }
    };

    protected abstract void modifyMenu(Menu menu);

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        modifyMenu(menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Class clazz = classMap.get(item.getTitle().toString().toLowerCase());
        if (null != clazz) {
            startActivity(new Intent(this, clazz));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
