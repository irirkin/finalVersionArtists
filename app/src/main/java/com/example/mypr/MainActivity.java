package com.example.mypr;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.view.ViewGroup.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class MainActivity extends AppCompatActivity {


    private ProgressDialog progress;
    private String jsonUrl = "http://cache-default05e.cdn.yandex.net/download.cdn.yandex.net/mobilization-2016/artists.json";
    private String jsonPath = "FileWithJson.txt";
    private String appPath = "/sdcard/Artists/";// путь для хранения временных файлов
    Artist[] arrayArtists;
    private Integer currentLoadArtist = 0;
    private Integer step = 5; // шаг подгрузки новых исполнителей
    private Integer TotalArtist = 0;

    LinearLayout mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainLayout = (LinearLayout)findViewById(R.id.InnerMainLL);

        final  ScrollView mainScroll = (ScrollView)findViewById(R.id.scrollView);

        mainScroll.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged()
            {
                // если прокрутили весь текущий лист загруженных исполнителей, то
                if(mainScroll.getHeight()+mainScroll.getScrollY() == mainLayout.getHeight())
                {
                    currentLoadArtist += step;
                    RenderLayout(); // отрисовка новых блоков исполнителей
                }
            }
        });

       File dirApp = new File(appPath);
       try
       {
           //если папки для хранения временных файлов не существует, то создаем
           if (!dirApp.exists())
               dirApp.mkdirs();
       }
       catch (Exception e)
       {
           System.out.println("ex = "+e);
       }

        File jsonFile = new File(appPath+jsonPath);
        //проверяем, если json с описанием артистов уже загружен, то отрисовываем
        if( jsonFile.exists())
        {
            RenderLayout();
        }
        else
        {
           // если нет, то загружаем
            new GetClass(this).execute(jsonUrl, appPath+jsonPath);
        }
    }

    //отрисовка блоков исполнителей
    public void RenderLayout()
    {
        Gson gson = new GsonBuilder().create();

        try
        {
            InputStream fileIn = new BufferedInputStream(new FileInputStream(new File(appPath+jsonPath)));
            JsonReader jsonReader = new JsonReader(new InputStreamReader(fileIn, "UTF-8"));
            jsonReader.setLenient(true);
            //преобразуем из json формата  в массив объектов  Artist
            arrayArtists = gson.fromJson(jsonReader, Artist[].class);
        }
        catch (Exception e)
        {
            System.out.println("exp = "+e);
        }
        TotalArtist = arrayArtists.length;

        // проверка на то, достигли ли конца массива с шагом step
        if(TotalArtist <= currentLoadArtist+step)
        {
            step =  TotalArtist - currentLoadArtist;
        }

        //отображаем количество блоков с артистами, равное шагу step
        for(int i = currentLoadArtist; i < currentLoadArtist+step; i++)
        {
            LinearLayout slaveLayout = new LinearLayout(this);
            slaveLayout.setOrientation(LinearLayout.HORIZONTAL);

            final Artist objArtist = arrayArtists[i];

            //создаем Layout для отображения информации об артистах
            LinearLayout forTextLayout = new LinearLayout(this);
            forTextLayout.setOrientation(LinearLayout.VERTICAL);
            forTextLayout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

            ImageView pic = new ImageView(this);

            //для загружки изображений используем библиотеку ImageLoader
            ImageLoader imageLoader = ImageLoader.getInstance(); // Получили экземпляр
            imageLoader.init(ImageLoaderConfiguration.createDefault(this)); // Проинициализировали конфигом по умолчанию
            imageLoader.displayImage(objArtist.cover.small, pic); // Запустили асинхронный показ картинки
            slaveLayout.addView(pic);

            TextView labelName = new TextView(this);
            labelName.setText(objArtist.name);
            labelName.setTextSize(20);
            labelName.setGravity(Gravity.CENTER_HORIZONTAL);

            //********
            TextView labelInfo = new TextView(this);//жанры музыки
            String str="";
            for(int j=0;j<objArtist.genres.size();j++)
            {
                str += objArtist.genres.get(j)+";";
            }
            labelInfo.setText(str);
            labelInfo.setGravity(Gravity.CENTER);

            //********
            TextView labelMusic = new TextView(this);//жанры музыки
            labelMusic.setText(Integer.toString(objArtist.tracks)+" песен, "+Integer.toString(objArtist.albums)+" альбомов");
            labelMusic.setGravity(Gravity.CENTER);

            //******
            forTextLayout.addView(labelName);
            forTextLayout.addView(labelInfo);
            forTextLayout.addView(labelMusic);
            slaveLayout.addView(forTextLayout);
            slaveLayout.setPadding(5,0,0,10);

            mainLayout.addView(slaveLayout);

            //слушатель на нажатие по блоку с артистом для перехода на новую  AboutActivity
            slaveLayout.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Intent intent = new Intent(MainActivity.this, AboutActivity.class);

                    //передаем в активити текущий объект исполнителя
                    intent.putExtra("artist",objArtist);
                    try
                    {
                        startActivity(intent);
                    }
                    catch (Exception e)
                    {
                        System.out.println("Exception!!! start activity failed= " + e);
                    }

                }
            });

        }
    }


    //класс асинхронного потока для загрузки json файла
    private class GetClass extends AsyncTask<String, Void, Void>
    {
        String path;
        @Override
        protected Void doInBackground(String... params)
        {
            try {
                URL url = null;
                path = null;

                if( params.length > 0 )
                {
                    url = new URL(params[0]);
                    path = params[1];

                }

                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("ACCEPT-LANGUAGE", "ru-RU,ru;0.5");

                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line = "";
                StringBuilder responseOutput = new StringBuilder();

                while((line = br.readLine()) != null )
                {
                    responseOutput.append(line);
                }
                br.close();


                FileOperations fop = new FileOperations();
                fop.write(responseOutput.toString(),path);

                MainActivity.this.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        progress.dismiss();
                    }
                });


            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        private final Context context;

        public GetClass(Context c){
            this.context = c;
        }

        protected void onPreExecute()
        {
            progress= new ProgressDialog(this.context);
            progress.setMessage("Загрузка приложения");
            progress.show();
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            RenderLayout();
        }
    }

}
