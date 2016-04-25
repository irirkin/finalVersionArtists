package com.example.mypr;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileOperations
{
    //метод для записи
    public Boolean write(String fContent, String fPath)
    {
        try {

            File file = new File(fPath);

            if (!file.exists())
            {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(fContent);
            bw.close();

            return true;

        } catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }

    }

    //метод для чтения
    public String read(String fPath)
    {

        BufferedReader br = null;
        String response = null;

        try
        {

            StringBuffer output = new StringBuffer();

            br = new BufferedReader(new FileReader(fPath));
            String line = "";
            while ((line = br.readLine()) != null)
            {
                output.append(line +"n");
            }
            response = output.toString();

        }

        catch (IOException e)
        {
            e.printStackTrace();
            return null;

        }
        return response;

    }




}
