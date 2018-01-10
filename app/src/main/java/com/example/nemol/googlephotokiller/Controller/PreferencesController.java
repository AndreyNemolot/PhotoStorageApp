package com.example.nemol.googlephotokiller.Controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.example.nemol.googlephotokiller.Model.ActiveUser;

/**
 * Created by nemol on 10.01.2018.
 */

public class PreferencesController {

    public void saveUser(Context context) {
        SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString("id", Integer.toString(ActiveUser.getId()));
        ed.putString("login", ActiveUser.getLogin());
        ed.putString("password", ActiveUser.getPassword());
        ed.apply();
        Toast.makeText(context, "User saved", Toast.LENGTH_SHORT).show();
    }

    public boolean loadText(Context context) {
        SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(context);
        if (!(sPref.getString("id", "").equals("-1") || sPref.getString("id", "").equals(""))) {
            ActiveUser.saveUser(Integer.decode(sPref.getString("id", "")),
                    sPref.getString("login", ""), sPref.getString("password", ""));
            Toast.makeText(context, "User loaded", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            Toast.makeText(context, "User NOT loaded", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}
