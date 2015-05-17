package com.example.drew.messageonabottle;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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
    private List<String> _nameList = new ArrayList<>();

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

        _mainMessageList = (ListView) findViewById(R.id.messageList);
        _arrayAdapter = new ArrayAdapter(this,
                                         R.layout.list_item_style,
                                         _nameList);
        _mainMessageList.setAdapter(_arrayAdapter);

        _socket.on("login", _historyListener);
        _socket.on("new message", _messageListener);

        // This will need to move to the username activity.
        _socket.connect();
        _socket.emit("add user", "Android");
        // -------------------------------- //
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

    private Emitter.Listener _historyListener = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    try {
                        JSONArray history =  data.getJSONArray("history");
                        for (int i = 0; i < history.length(); i++) {
                            JSONObject historyItem = history.getJSONObject(i);
                            String username = historyItem.getString("username");
                            String message = historyItem.getString("message");
                            addMessage(username, message);
                        }
                        _arrayAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }
    };

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
