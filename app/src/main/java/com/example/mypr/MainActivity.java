package com.example.mypr;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.view.ViewGroup.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;


public class MainActivity extends AppCompatActivity {


    private ProgressDialog progress;
    private String jsonUrl = "http://cache-default05e.cdn.yandex.net/download.cdn.yandex.net/mobilization-2016/artists.json";
    private String jsonPath = "FileWithJson.txt";
    private String appPath = "/sdcard/Artists/";
    Artist[] arrayArtists;
    private Integer currentLoadArtist = 0;
    private Integer step = 10;
    private Integer TotalArtist = 0;

    LinearLayout mainLayout;

    //FileOperations fop;
    ImageAsync downloadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainLayout = (LinearLayout)findViewById(R.id.InnerMainLL);

       final  ScrollView mainScroll = (ScrollView)findViewById(R.id.scrollView);
        mainScroll.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {

                if(mainScroll.getHeight()+mainScroll.getScrollY() == mainLayout.getHeight())
                {
                    RenderLayout();
                    currentLoadArtist += step;
                }
                Log.d("TAG", mainScroll.getScrollY() + "");
                System.out.println("mainScroll.height = "+mainScroll.getHeight());
                System.out.println("mainLayout.getVerticalScrollbarPosition() = " + mainLayout.getVerticalScrollbarPosition());
                System.out.println("mainLayout.getHeight() = " + mainLayout.getHeight());

            }
        });

       File dirApp = new File(appPath);
       try {
           if (!dirApp.exists())
               dirApp.mkdirs();
       }
       catch (Exception e)
       {
           System.out.println("ex = "+e);
       }

        File jsonFile = new File(appPath+jsonPath);
        if( jsonFile.exists())
        {
            RenderLayout();
        }
        else
        {
           // sendGetRequest
            new GetClass(this).execute(jsonUrl, appPath+jsonPath);
        }
    }

    public void RenderLayout()
    {
        Gson gson = new GsonBuilder().create();

        try {
            InputStream fileIn = new BufferedInputStream(new FileInputStream(new File(appPath+jsonPath)));
            JsonReader jsonReader = new JsonReader(new InputStreamReader(fileIn, "UTF-8"));
            jsonReader.setLenient(true);
            arrayArtists = gson.fromJson(jsonReader, Artist[].class);
        }
        catch (Exception e)
        {
            System.out.println("exp = "+e);
        }
        TotalArtist = arrayArtists.length;
        // не забыть проверку на step  с переполнением массива
        if(TotalArtist <= currentLoadArtist+step){
            step =  TotalArtist - currentLoadArtist;
        }
        for(int i = currentLoadArtist; i < currentLoadArtist+step; i++)
       // for(int i = 0; i < 10; i++)
        {
            LinearLayout slaveLayout = new LinearLayout(this);
            slaveLayout.setOrientation(LinearLayout.HORIZONTAL);

            final Artist objArtist = arrayArtists[i];


            File dirImage = new File(appPath+ Integer.toString(objArtist.id));
            try {
                if (!dirImage.exists())
                    dirImage.mkdirs();
            }
            catch (Exception e)
            {
                System.out.println("ex = "+e);
            }

            LinearLayout forTextLayout = new LinearLayout(this);
            forTextLayout.setOrientation(LinearLayout.VERTICAL);
            forTextLayout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

            ImageView pic = new ImageView(this);
            Bitmap bmOriginal = BitmapFactory.decodeResource(getResources(), R.drawable.default300);

            //original size
            int width = bmOriginal.getWidth();
            int height = bmOriginal.getHeight();

            // half from original size
            int halfWidth = width / 2;
            int halfHeight = height / 2;
            Bitmap bmHalf = Bitmap.createScaledBitmap(bmOriginal, halfWidth,halfHeight, false);
            pic.setImageBitmap(bmHalf);

            pic.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
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

            System.out.println("*************!!!! start ImageAsync !!!!!!********** ");
            //проверка на существование добавить до загружки деолтных изображений

            //Запускаем задачу, передавая ей ссылку на картинку и путь для сохранения
            //не берем mime type,жестко присваиваем jpg
            downloadTask = new ImageAsync(this, pic);
            downloadTask.execute(objArtist.cover.small, appPath + Integer.toString(objArtist.id)+"/"+
                objArtist.name.toString()+"_small.jpg");

            slaveLayout.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Intent intent = new Intent(MainActivity.this, AboutActivity.class);

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

        protected void onPreExecute(){
            progress= new ProgressDialog(this.context);
            progress.setMessage("Загрузка json файла");
            progress.show();
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            RenderLayout();
        }
    }


    /*
     * Расширяем класс  AsyncTask<Params, Progress, Result>
     * указываем, какими типами будут его generic-параметры.
     * Params - тип входных данных. В нашем случае будет String, т.к. передается url картинки
     * Progress - тип данных, которые будут переданы для обновления прогресса. В нашем случае Integer.
     * Result - тип результата. В нашем случае Drawble.
     */
    private class ImageAsync extends AsyncTask<String, Integer, Drawable>
    {
        ImageView img;
        String path;
        private final Context context;
      //  Bitmap bitIcon;
        String mimeImg;
        String url;
        ProgressDialog progressDialog;
        long total = 0;
        InputStream inputSt;

        public ImageAsync(Context c, ImageView img)
        {
            this.context = c;
            this.img = img;
            this.mimeImg = "";
        }


        protected void onPreExecute()
        {
            //Отображаем системный диалог загрузки
            progressDialog = new ProgressDialog(this.context);
            progressDialog.setIndeterminate(false);
            progressDialog.setMessage("Загрузка изображений");
            progressDialog.show();
        }


        protected void onProgressUpdate(Integer... progress)
        {
            super.onProgressUpdate(progress);
            progressDialog.setProgress(progress[0]);
        }

        //Скроем диалог и покажем картинку
        @Override
        protected void onPostExecute(Drawable result)
        {
            img.setImageDrawable(result);
            progressDialog.dismiss();
        }


        @Override
        protected Drawable doInBackground(String... params)
        {
            int count;
            //url = null;
            path = null;
           // bitIcon = null;

            try//проверка массива входных параметров на непустоту
            {
                if (params.length > 0)
                {
                  //  url = params[0];//url картинки
                    path = params[1];//путь к папке, куда сохранить нужно
                }
            }
            catch (Exception e)
            {
                Log.d("empty img params", e.getMessage());
                e.printStackTrace();
                return null;
            }

            //пытаемся загрузить изображение в bitmap and ImageView
            try
            {
                /*InputStream in = new java.net.URL(url).openStream();
                bitIcon = BitmapFactory.decodeStream(in);*/
                //***************************************************
                URL url = new URL(params[0]);
                URLConnection connection = url.openConnection();
                connection.connect();
                int lengthOfFile = connection.getContentLength();

                //Constructs a new BufferedInputStream, providing in with a buffer of 8192 bytes.
                inputSt = new BufferedInputStream(url.openStream(), 8192);

                //куда сохранить
                OutputStream output = new FileOutputStream(path);

                byte data[] = new byte[256];
                while ((count = inputSt.read(data)) != -1)
                {
                    //Проверяем, актуальна ли еще задача
                    if (isCancelled())
                    {
                        return null;
                    }
                    total += count;
                    output.write(data, 0, count);

                    //Информирование о закачке.
                    //Передаем число, отражающее процент загрузки файла
                    //После вызова этого метода автоматически будет вызван
                    //onProgressUpdate в главном потоке
                    publishProgress((int)((total*100)/lengthOfFile));
                }
                output.flush();
                output.close();
                inputSt.close();

                //******************************************************
            }
            catch (Exception ex)
            {
                Log.e("Error: ", ex.getMessage());
            }

           return Drawable.createFromPath(path);
        }


    }



}
