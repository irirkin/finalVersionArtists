package com.example.mypr;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List <LinearLayout> people;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        people = new ArrayList<LinearLayout>();

        LinearLayout mainLayout = (LinearLayout)findViewById(R.id.InnerMainLL);

        for( int i = 0; i < 10; i++ )
        {
            LinearLayout slaveLayout = new LinearLayout(this);
            slaveLayout.setOrientation(LinearLayout.HORIZONTAL);
            slaveLayout.setBackgroundColor(getResources().getColor(R.color.colorSlave));

            slaveLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                    intent.putExtra("name","Sergey");

                    startActivity(intent);
                }
            });

            LinearLayout forTextLayout = new LinearLayout(this);
            forTextLayout.setOrientation(LinearLayout.VERTICAL);
            forTextLayout.setBackgroundColor(getResources().getColor(R.color.colorSongs));
            forTextLayout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

            ImageView pic = new ImageView(this);
            pic.setImageResource(R.drawable.irina);
            pic.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

            slaveLayout.addView(pic);

            TextView labelName = new TextView(this);
            labelName.setText("Irina "+i);
            labelName.setTextSize(20);
            labelName.setGravity(Gravity.CENTER_HORIZONTAL);

            TextView labelInfo = new TextView(this);
            labelInfo.setText("My Songs 10"+ i);
            labelInfo.setTextSize(20);
            labelInfo.setGravity(Gravity.BOTTOM);

            forTextLayout.addView(labelName);
            forTextLayout.addView(labelInfo);
            slaveLayout.addView(forTextLayout);


            people.add(slaveLayout);
            mainLayout.addView(people.get(i));

        }

    }


}
