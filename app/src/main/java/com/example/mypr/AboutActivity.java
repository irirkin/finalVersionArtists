package com.example.mypr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * Created by Ирина on 23.04.2016.
 */
public class AboutActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Intent intent = getIntent();

        String fName = intent.getStringExtra("name");

        ScrollView mainLayout = (ScrollView)findViewById(R.id.about);

        TextView labelInfo = new TextView(this);
        labelInfo.setText(fName);

        labelInfo.setGravity(Gravity.CENTER);
        mainLayout.addView(labelInfo);
    }



}
