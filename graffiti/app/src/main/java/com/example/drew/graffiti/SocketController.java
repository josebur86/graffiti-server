package com.example.drew.graffiti;

import com.example.drew.graffiti.presenter.MessageBoardPresenter;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URI;
import java.net.URISyntaxException;

public class SocketController {
    private Socket _socket;

    private static String kMessageEvent = "new message";
    private static String kNewUserEvent = "add user";

    public SocketController(String server, String username, MessageListener messageListener) {
        try {
            _socket = IO.socket(server);
        } catch (URISyntaxException e) {}

        _socket.on(kMessageEvent, messageListener);
        _socket.connect();
        _socket.emit(kNewUserEvent, username);
    }

    public void destroy() {
        _socket.disconnect();
    }

    public void sendMessage(String message) {
        _socket.emit(kMessageEvent, message);
    }
}
