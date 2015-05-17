package com.example.drew.messageonabottle;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class MessageBoardActivity extends ActionBarActivity {

    ListView mainMessageList;
    ArrayAdapter mArrayAdapter;
    ArrayList mNameList = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_board);

        mainMessageList = (ListView) findViewById(R.id.messageList);

        mArrayAdapter = new ArrayAdapter(this,
                        R.layout.list_item_style,
                        mNameList);

        mainMessageList.setAdapter(mArrayAdapter);

        mNameList.add("Test1");
        mNameList.add("Test2");
        mNameList.add("Test3");
        mNameList.add("Test4");

        mArrayAdapter.notifyDataSetChanged();
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

}
