package com.example.drew.messageonabottle;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by jetters on 5/17/15.
 */


public class ChatMessageAdapter extends ArrayAdapter<ChatMessage> {

    private ArrayList<ChatMessage> items;

    public ChatMessageAdapter(Context context, int textViewResourceId, ArrayList<ChatMessage> items) {
        super(context, textViewResourceId, items);
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            //LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //v = vi.inflate(R.layout.row, null);
        }
        ChatMessage msg = items.get(position);
        if (msg != null) {
            //TextView userNameTextView = (TextView) v.findViewById(R.id.userNameTextView);
            //TextView chatMessageTextView = (TextView) v.findViewById(R.id.chatMessageTextView);
            //TextView locationTextView = (TextView) v.findViewById(R.id.locationTextView);
            //ImageThingy imageView = (ImageView) v.findViewById(R.id.imageView);
            //chatMessageTextView.setText(msg.Message);
            //locationTextView.setText(msg.UserLocation);
            //userNameTextView.setText(msg.UserName);

            if(msg.Image != null){
                //imageView.setImage(msg.Image);
            }
        }
        return v;
    }
}
