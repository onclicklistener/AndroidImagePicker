package com.davesla.librarypicker.bean;

import java.io.Serializable;

/**
 * Created by hwb on 15/7/2.
 */
public class Picture implements Serializable,Comparable<Picture>{
    public String path;
    public Long dateAdded;

    @Override
    public int compareTo(Picture another) {
        return another.dateAdded.compareTo(dateAdded);
    }
}
