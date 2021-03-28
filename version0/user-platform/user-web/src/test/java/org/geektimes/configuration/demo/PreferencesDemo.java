package org.geektimes.configuration.demo;


import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class PreferencesDemo {

    public static void main(String[] args) throws BackingStoreException {
        Preferences userPreferences = Preferences.userRoot();
        //        Preferences.systemRoot();
        userPreferences.put("mykey", "HelloWorld");
        userPreferences.flush();
        System.out.println(userPreferences.get("mykey",""));
    }
}
