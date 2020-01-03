package com.example.pablito.first_one;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class DisplayMessageActivity extends AppCompatActivity {

    public static String URL;
    public static String PHONE_NUMBER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);

        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        URL = intent.getStringExtra(MainActivity.URL);
        PHONE_NUMBER = intent.getStringExtra(MainActivity.PHONE_NUMBER);

        TextView textView = findViewById(R.id.textView);
        textView.setText(message);
    }

}
