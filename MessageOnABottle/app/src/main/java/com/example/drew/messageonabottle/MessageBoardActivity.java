package com.example.drew.messageonabottle;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class MessageBoardActivity extends ActionBarActivity {

    private ListView _mainMessageList;
    private ArrayAdapter _arrayAdapter;
    private List<String> _nameList = new ArrayList<String>();

    private TextView _messageEdit;
    private Button _sendButton;
    private ImageButton _pictureButton;

    private String _username;

    static final int REQ_IMAGE_CAPTURE = 1;

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
        _pictureButton = (ImageButton) findViewById(R.id.sendPicture);
        _pictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPicture();
            }
        });

        _mainMessageList = (ListView) findViewById(R.id.messageList);
        _arrayAdapter = new ArrayAdapter(this,
                                         R.layout.list_item_style,
                                         _nameList);
        _mainMessageList.setAdapter(_arrayAdapter);

        addMessage("lt. baggins", "not going to work tomorrow! ready to get crazy! #upforwhatever");
        addMessage("max1", "[picture]");
        addMessage("charlie", "OMG SNOOP IS HERE");
        addMessage("Hooterstump", "hi");
        addMessage("ordinal", "u dtbf");
        _arrayAdapter.notifyDataSetChanged();

        _socket.on("new message", _messageListener);

        _username = getIntent().getStringExtra("userName");
        _socket.connect();
        _socket.emit("add user", _username);
    }

    private void onSend() {
        String message = _messageEdit.getText().toString().trim();
        addMessage(_username, message);
        _arrayAdapter.notifyDataSetChanged();
        _socket.emit("new message", message);
        _messageEdit.setText("");
    }

    private void onPicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQ_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        _socket.disconnect();
    }

    private void addMessage(String user, String message) {
        _nameList.add(user + ": " + message);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            ChatMessage message = new ChatMessage(_username, "", imageBitmap, "CIC, USA");
            // TODO: add this to view.
        }
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
                        addMessage(username, message);
                        _arrayAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }
    };
}
