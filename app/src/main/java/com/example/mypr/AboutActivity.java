package com.example.mypr;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.net.URI;

/**
 * Created by Ирина on 23.04.2016.
 */
public class AboutActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Intent intent = getIntent();

        Artist objArtist = (Artist)intent.getSerializableExtra("artist");
        LinearLayout mainLayout = (LinearLayout)findViewById(R.id.aboutLLV);
        mainLayout.setGravity(Gravity.CENTER);


        //присваиваем картинку
        ImageView img = new ImageView(this);
        Picasso.with(this)
                .load(objArtist.cover.big)
                .placeholder(R.drawable.default300)
                .into(img);
        /*Bitmap bmOriginal = BitmapFactory.decodeResource(getResources(), R.drawable.default300);
        img.setImageBitmap(bmOriginal);
        img.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));*/
        mainLayout.addView(img);

        TextView labelName = new TextView(this);
        labelName.setText(objArtist.name);
        labelName.setGravity(Gravity.CENTER);
        mainLayout.addView(labelName);

        //выводим жанры музыки
        String str="";
        for(int j=0;j<objArtist.genres.size();j++)
        {
            str += objArtist.genres.get(j)+";";
        }
        TextView labelGenres = new TextView(this);
        labelGenres.setPadding(10,10,0,0);
        labelGenres.setTextSize(20);
        labelGenres.setText(str);

        //количество песен и альбомов
        TextView labelSongs = new TextView(this);
        labelSongs.setText(Integer.toString(objArtist.tracks)+" песен, "+Integer.toString(objArtist.albums)+" альбомов");
        labelSongs.setPadding(10,10,0,0);
        labelSongs.setTextSize(20);
        mainLayout.addView(labelSongs);

        TextView labelBiography = new TextView(this);
        labelBiography.setText("Биография");
        labelBiography.setPadding(10,5,10,0);
        labelBiography.setTextSize(20);
        mainLayout.addView(labelBiography);

        //описание биографии
        TextView desc = new TextView(this);
        desc.setText(objArtist.description);
        desc.setTextSize(24);
        desc.setPadding(10,5,10,0);
        mainLayout.addView(desc);

    }



}
