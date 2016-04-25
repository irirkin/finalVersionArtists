package com.example.mypr;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Time;

public class FileOperations
{
    public FileOperations()
    {

    }


    public Boolean write(String fContent, String fPath)
    {
        try {

            File file = new File(fPath);

            // If file does not exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(fContent);
            bw.close();

            Log.d("Succees write","Success");
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }

    public String read(String fPath)
    {

        BufferedReader br = null;
        String response = null;

        try {

            StringBuffer output = new StringBuffer();

            br = new BufferedReader(new FileReader(fPath));
            String line = "";
            while ((line = br.readLine()) != null) {
                output.append(line +"n");
            }
            response = output.toString();

        } catch (IOException e) {
            e.printStackTrace();
            return null;

        }
        return response;

    }

    public String saveImageToSD(Bitmap obj, String path)
    {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        obj.compress(Bitmap.CompressFormat.JPEG, 85, bytes);// сохранять картинку в png-формате с 85% сжатия

        try
        {
            //сохраняю img
           // File file = new File(Environment.getExternalStorageDirectory() + File.separator + "myBitmap.jpg");
            File file = new File(path);
            System.out.println("path saved Image to SD Card "+path);
            file.createNewFile();

            //Запись
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(bytes.toByteArray());
            fileOutputStream.close();
        }
        catch (Exception ex)
        {
            System.out.println("exception: NOT save Image to SD Card "+ex);
            return ex.getMessage();
        }

        return "";
    }


}
