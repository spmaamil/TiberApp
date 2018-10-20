package com.xyz.tiber.tiber;

import java.util.Date;

public class AppProperties
{
    private static AppProperties instance = null;

    public long start;
    public static synchronized AppProperties getInstance()
    {
        if(null == instance) {
            instance = new AppProperties();
            Date date = new Date();
            instance.start = date.getTime();
        }
        return instance;
    }
}
