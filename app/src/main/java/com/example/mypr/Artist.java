package com.example.mypr;

import java.io.Serializable;
import java.util.List;

//класс для хранения структуры информации об артисте согласно json
public class Artist implements Serializable
{
    public int id;
    public String name;
    public int tracks;
    public int albums;
    public String link;
    public String description;

    public CoverEntity cover;
    public List<String> genres;


    public static class CoverEntity implements Serializable
    {
        public String small;
        public String big;
    }
}
