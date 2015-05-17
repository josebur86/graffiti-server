package com.example.drew.messageonabottle;

/**
 * Created by jetters on 5/17/15.
 */
public class ChatMessage {
    public String UserName;
    public String Message;
    public String ImageSource;
    public String UserLocation;

    public ChatMessage(String userName, String message, String imageSource, String userLocation) {
        UserName = userName;
        Message = message;
        ImageSource = imageSource;
        UserLocation = userLocation;
    }
}
