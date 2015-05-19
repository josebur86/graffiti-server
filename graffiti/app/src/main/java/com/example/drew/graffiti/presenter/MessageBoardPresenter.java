package com.example.drew.graffiti.presenter;

import android.graphics.Bitmap;

import com.example.drew.graffiti.ChatMessage;
import com.example.drew.graffiti.MessageBoardView;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class MessageBoardPresenter {

    private MessageBoardView _view;

    private String _username;
    private String _location;

    private List<ChatMessage> _messages;

    // TODO: still not sure if this is the best place for the socket logic.
    private Socket _socket;
    {
        try {
            _socket = IO.socket("https://thawing-island-7364.herokuapp.com/");
        } catch (URISyntaxException e) {}
    }

    private static String kMessageEvent = "new message";
    private static String kNewUserEvent = "add user";

    public MessageBoardPresenter(MessageBoardView view, String username, String location) {
        _view = view;
        _username = username;
        _location = location;

        _messages = new ArrayList<ChatMessage>();
    }

    public void create() {
        _view.setMessages(_messages);

        _socket.on(kMessageEvent, _view.getMessageListener());
        _socket.connect();
        _socket.emit(kNewUserEvent, _username);
    }

    public void destroy() {
        _socket.disconnect();
    }

    public void sendMessage(String message) {
        _messages.add(new ChatMessage(_username, message, null, _location));
        _view.onAddMessage();
        _socket.emit(kMessageEvent, message);
    }

    public void sendPicture(Bitmap image) {
        _messages.add(new ChatMessage(_username, null, image, _location));
        _view.onAddMessage();
        // TODO: send the picture to the socket.
    }

    public void receiveMessage(JSONObject data) {
        String username;
        String location;
        String message;
        try {
            username = data.getString("username");
            location = "TODO"; // TODO: get location from the other users.
            message = data.getString("message");
        } catch (JSONException e) {
            throw new RuntimeException(e); // TODO: seems harsh
        }

        _messages.add(new ChatMessage(username, message, null, location));
        _view.onAddMessage();
        _view.playNotificationSound();
    }
}
