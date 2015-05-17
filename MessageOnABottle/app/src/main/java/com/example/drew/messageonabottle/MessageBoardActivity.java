package com.example.drew.messageonabottle;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;


public class MessageBoardActivity extends ListActivity {

    private ListView _mainMessageList;
    private ChatMessageAdapter _chatAdapter;
    private List<ChatMessage> _nameList = new ArrayList<ChatMessage>();
    private TextView _messageEdit;
    private ImageButton _pictureButton;
    private Bitmap _snoopBitmap;
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
        _messageEdit.setOnKeyListener(new ReturnKeyListener());

        _pictureButton = (ImageButton) findViewById(R.id.sendPicture);
        _pictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPicture();
            }
        });

        _mainMessageList = (ListView) findViewById(android.R.id.list);
        _chatAdapter = new ChatMessageAdapter(this,
                                         R.layout.list_item_style,
                                         _nameList);
        _mainMessageList.setAdapter(_chatAdapter);

        createChatHistory();
        _chatAdapter.notifyDataSetChanged();

        _socket.on("new message", _messageListener);

        _username = getIntent().getStringExtra("userName");
        _socket.connect();
        _socket.emit("add user", _username);
    }

    private void createChatHistory() {

        new DownloadImageTask(_snoopBitmap)
                .execute("");

        _nameList.add(
                new ChatMessage("BlondieBoo",
                        "OMG",
                        null,
                        "Sub Zero Vodka Bar"));


        _nameList.add(
                new ChatMessage("BlondieBoo",
                                "SNOOP JUST SHOWED UP! WHAT??",
                                null,
                                "Sub Zero Vodka Bar"));

        _nameList.add(
                new ChatMessage("Ashley91",
                                "Whats up???",
                                null,
                                "Tom's Bar and Grill"));

        _nameList.add(
                new ChatMessage("STL-Chad",
                                "Snoop?? RIGHT. #DoubtIt",
                                null,
                                "Llywelyn’s Pub"));

        _nameList.add(
                new ChatMessage("CrayCrayTrain",
                        "not going to work tomorrow! ready to get crazy! #upforwhatever",
                        null,
                        "I-Tap"));

        _chatAdapter.notifyDataSetChanged();


    }

    private void onSend() {
        String message = _messageEdit.getText().toString().trim();
        addMessage(_username, message, null, "CIC");
        _chatAdapter.notifyDataSetChanged();
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

    private void addMessage(String user, String message, Bitmap image, String userLocation) {
        ChatMessage newChatMessage = new ChatMessage(user, message, image, userLocation);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            addMessage(_username, "", imageBitmap, "C-I-C, USA");
            _chatAdapter.notifyDataSetChanged();
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
                        addMessage(username, message, null, "C-I-C, USA");
                        _chatAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }
    };

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        Bitmap bmImage;

        public DownloadImageTask(Bitmap bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = "https://dl.dropboxusercontent.com/u/43119507/snoop.jpg";
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            _snoopBitmap = result;

            try {
                Thread.sleep(1500);
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }

            ChatMessage msg = new ChatMessage("BlondieBoo",
                            null,
                            _snoopBitmap,  // add  bitmap of snoop partying at sub zero vodka bar.
                           "Sub Zero Vodka Bar");

            addMessage("BlondieBoo", null, _snoopBitmap, "Sub Zero Vodka Bar");
            _chatAdapter.notifyDataSetChanged();


            try {
                Thread.sleep(1500);
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }

            addMessage("STL-Chad", "OMG Thats crazy I'm on my way!", null, "Llywelyn’s Pub");
            _chatAdapter.notifyDataSetChanged();
        }
    }


    private class ReturnKeyListener implements View.OnKeyListener {

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                onSend();
                return true;
            }
            return false;
        }
    }

    public void onClickUser(View view) {
        //final EditText messageEditText = (EditText) findViewById(R.id.editMessage);

        //String message = messageEditText.getText().toString().trim();
        //if (TextUtils.isEmpty(message))
            //return;

        //_socket.emit("new message", message);

        //_toast.setText(String.format("Sent message: %s", message));
        //_toast.show();

        Intent mapIntent = new Intent();
        //messageBoardIntent.putExtra("userName", userName);
        //messageBoardIntent.putExtra("serverUri", qrCodeUri);
        mapIntent.setClass(this, MapActivity.class);

        startActivity(mapIntent);
    }
}
