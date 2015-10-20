package com.android.google.valerio.livrogoogleandroid;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class DebugActivity extends AppCompatActivity {
    private static final String TAG = "livro";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, getClass().getName() + "onCreate() chamado: " + savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, getClass().getName() + "onStart() chamado.");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, getClass().getName() + "onRestart() chamado.");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, getClass().getName() + "onResume() chamado.");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, getClass().getName() + "onPause() chamado.");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG, getClass().getName() + "onSaveInstanceState() chamado.");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, getClass().getName() + "onStop() chamado.");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, getClass().getName() + "onDestroy() chamado.");
    }
}
