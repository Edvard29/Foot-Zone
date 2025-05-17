package com.example.footzone;

import android.app.Application;
import com.google.firebase.FirebaseApp;

public class FootzoneApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
    }
}