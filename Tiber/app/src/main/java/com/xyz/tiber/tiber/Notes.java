package com.xyz.tiber.tiber;

import java.io.Serializable;
import java.util.ArrayList;

public class Notes implements Serializable
{
    private String userId;
    private String userName;
    private String userTag;
    private String message;
    private String userImageUrl;
    private String noteId;

    public Notes(String uIL, String uD, String uN, String uT, String m, String nD)
    {
        userImageUrl = uIL;
        userId = uD;
        userName = uN;
        userTag = uT;
        message = m;
        noteId = nD;
    }

    public String getUserName()
    {
        return userName;
    }

    public String getUserTag()
    {
        return userTag;
    }

    public String getMessage()
    {
        return message;
    }

    public String getUserId()
    {
        return userId;
    }

    public String getNoteId()
    {
        return noteId;
    }
    @Override
    public boolean equals(Object b)
    {
        return ((String)b).equals(userTag);
    }

}

