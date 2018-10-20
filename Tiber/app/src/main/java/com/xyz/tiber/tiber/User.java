package com.xyz.tiber.tiber;

import java.io.Serializable;

public class User implements Serializable
{
    private String userId;
    private String userName;
    private String userTag;
    private String userDescription;
    private String userImageUrl;

    public User(String uIL, String uD, String uN, String uT, String d)
    {
        userImageUrl = uIL;
        userId = uD;
        userName = uN;
        userTag = uT;
        userDescription = d;
    }

    public String getUserId()
    {
        return userId;
    }

    public String getUserName()
    {
        return userName;
    }

    public String getUserTag()
    {
        return userTag;
    }

    public String getUserDescription()
    {
        return userDescription;
    }

    public String getUserImageUrl()
    {
        return userImageUrl;
    }
}
