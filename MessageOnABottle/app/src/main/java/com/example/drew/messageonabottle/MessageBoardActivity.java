package com.example.drew.messageonabottle;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class MessageBoardActivity extends ActionBarActivity {

    private ListView _mainMessageList;
    private ArrayAdapter _arrayAdapter;
    private List<ChatMessage> _nameList = new ArrayList<ChatMessage>();
    private TextView _messageEdit;
    private Button _sendButton;
    private String _username;

    private Socket _socket;
    {
        try {
            _socket = IO.socket("https://thawing-island-7364.herokuapp.com/");
        } catch (URISyntaxException e) {}
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_board);

        _messageEdit = (TextView) findViewById(R.id.editMessage);
        _sendButton = (Button) findViewById(R.id.buttonSendMessage);
        _sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSend();
            }
        });

        _mainMessageList = (ListView) findViewById(R.id.messageList);
        _arrayAdapter = new ArrayAdapter(this,
                                         R.layout.list_item_style,
                                         _nameList);
        _mainMessageList.setAdapter(_arrayAdapter);

        _nameList = createChatHistory();
        _arrayAdapter.notifyDataSetChanged();

        _socket.on("new message", _messageListener);

        _username = getIntent().getStringExtra("userName");
        _socket.connect();
        _socket.emit("add user", _username);
    }

    private ArrayList<ChatMessage> createChatHistory() {
        ArrayList<ChatMessage> chatHistory = new ArrayList<ChatMessage>();

        chatHistory.add(
                new ChatMessage("BlondieBoo",
                        "OMG",
                        null,
                        "Sub Zero Vodka Bar"));


        chatHistory.add(
                new ChatMessage("BlondieBoo",
                                "SNOOP JUST SHOWED UP! WHAT??",
                                null,
                                "Sub Zero Vodka Bar"));

        chatHistory.add(
                new ChatMessage("Ashley91",
                                "Whats up???",
                                null,
                                "Drunken Fish"));

        chatHistory.add(
                new ChatMessage("STL-Chad",
                                "Snoop?? RIGHT. #DoubtIt",
                                null,
                                "Llywelyn’s Pub"));


        chatHistory.add(
                new ChatMessage("BlondieBoo",
                                null,
                                null,  //TODO TODO TODO add local bitmap of snoop partying at sub zero vodka bar.
                                "Sub Zero Vodka Bar"));

        chatHistory.add(
                new ChatMessage("CryzTrain",
                        "not going to work tomorrow! ready to get crazy! #upforwhatever",
                        null,
                        "International Tap House"));


        return chatHistory;

    }

    private void onSend() {
        String message = _messageEdit.getText().toString().trim();
        addMessage(_username, message, null); //TODO TODO TODO Actually use an image if we have one
        _arrayAdapter.notifyDataSetChanged();
        _socket.emit("new message", message);
        _messageEdit.setText("");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        _socket.disconnect();
    }

    private void addMessage(String user, String message, Bitmap image) {
        ChatMessage newChatMessage = new ChatMessage(user, message, image, "C-I-C, USA");
        _nameList.add(newChatMessage);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private Emitter.Listener _messageListener = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    try {
                        String username = data.getString("username");
                        String message = data.getString("message");
                        addMessage(username, message, null); //TODO TODO TODO Images go here maybe one day?
                        _arrayAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }
    };
}
