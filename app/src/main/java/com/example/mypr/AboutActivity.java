package com.example.mypr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

//класс Activity для отображения детальной информации об артисте
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
        ImageLoader imageLoader = ImageLoader.getInstance(); // Получили экземпляр
        imageLoader.init(ImageLoaderConfiguration.createDefault(this)); // Проинициализировали конфигом по умолчанию
        imageLoader.displayImage(objArtist.cover.big, img); // Запустили асинхронный показ картинки
        mainLayout.addView(img);

        TextView labelName = new TextView(this);
        labelName.setText(objArtist.name);
        labelName.setTextSize(30);
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
