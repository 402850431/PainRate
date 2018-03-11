package com.example.innooz.seekbar2;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.xw.repo.BubbleSeekBar;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    BubbleSeekBar seekBar2;
    ListView listView;
    Button button, button2;
    TinyDB tinydb;
    String currentDateTimeString;
    String[] TO = {"lee.shinyu@gmail.com"};
    Timer timer = null;
//    TimerTask timerTask = null;
    boolean isTimerRunning = false;
/*
    List<Map<String, String>> list = new ArrayList<Map<String, String>>();
    Map<String, String> map;
    private ArrayList<Float> numberList = new ArrayList<Float>();
    private ArrayList<String> timeList = new ArrayList<String>();
    SimpleAdapter simpleAdapter;
*/

    private ArrayList<String> spfList2 = new ArrayList<String>();
    private ArrayAdapter<String> listAdapter2;

    MyDataAdapter myDataAdapter;
    ArrayList<MyData> myDataList = new ArrayList<MyData>();
    RecyclerView recyclerView;

    public static String FACEBOOK_URL = "https://www.facebook.com/kao.cheryl.1";
    public static String FACEBOOK_PAGE_ID = "100001991750191";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewByIds();
        customActionBar();
        lv();
//        rv();
    }

    private void findViewByIds() {
        timer = new Timer(true);
        tinydb = new TinyDB(getApplicationContext());
        listView = (ListView)findViewById(R.id.listview2);
        seekBar2 = (BubbleSeekBar)findViewById(R.id.seekbar2);
        button = (Button)findViewById(R.id.button);
        button2 = (Button)findViewById(R.id.button2);
    }

    private void customActionBar() {

        final ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);

        final View mCustomView = mInflater.inflate(R.layout.custom_menu, null);

        final EditText timeEt = (EditText) mCustomView.findViewById(R.id.timeEt);

        TextView minusTv = (TextView) mCustomView.findViewById(R.id.minus);
        TextView increaseTv = (TextView) mCustomView.findViewById(R.id.increase);
        minusTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int timeNum = Integer.parseInt(timeEt.getText().toString());
                if(timeNum>1) {
                    timeEt.setText(String.valueOf(timeNum - 1));
                }
            }
        });

        increaseTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int timeNum = Integer.parseInt(timeEt.getText().toString());
                timeEt.setText(String.valueOf(timeNum + 1));
            }
        });

        final Button startPauseBtn = (Button) mCustomView.findViewById(R.id.startBtn);
        startPauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int timeNum = Integer.parseInt(timeEt.getText().toString().trim()+"000");

                if(!isTimerRunning && timeNum>0) { //start
                    isTimerRunning=true;
                    timeEt.setEnabled(false);
                    timer = new Timer(true);
                    timer.schedule(new MyTimerTask(MainActivity.this), timeNum, timeNum);
                    startPauseBtn.setText("暫停");
                }else { //pause
                    isTimerRunning=false;
                    stopTimer();
                    timeEt.setEnabled(true);
                    startPauseBtn.setText("開始");
                }
            }
        });
        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowCustomEnabled(true);

    }

    void stopTimer(){

        if (timer != null) {
            timer.cancel();
            timer = null;
        }

    }

    private void rv() {

        recyclerView = (RecyclerView)findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        myDataAdapter = new MyDataAdapter(MainActivity.this,myDataList);
        recyclerView.setAdapter(myDataAdapter);

        seekBar2.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {
                currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());

                myDataList.add(new MyData(progressFloat,currentDateTimeString));
                myDataAdapter.notifyDataSetChanged();

                spfList2.add("\n數值:" + String.valueOf(progressFloat) + "  時間:" + currentDateTimeString + "\n");

            }

            @Override
            public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {

            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendEmail();
                myDataAdapter.clear();
                myDataAdapter.notifyDataSetChanged();
            }
        });
    }

    private void lv() {

        if(spfList2!=null)
            spfList2 = tinydb.getListString("Data2");

//        listAdapter2 = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1,spfList2);

        listAdapter2 = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1,spfList2){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = ((TextView) view.findViewById(android.R.id.text1));
                textView.setTextSize(13);
                return view;
            }
        };

        listView.setAdapter(listAdapter2);


/*
        seekBar2.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {
            }

            @Override
            public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {
                currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                spfList2.add("\n數值:" + String.valueOf(progressFloat) + "   時間:" + currentDateTimeString + "\n");
                listAdapter2.notifyDataSetChanged();
            }

            @Override
            public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {

            }
        });
*/

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tinydb.putListString("Data2", spfList2);
                sendEmail();
//                tinydb.clear();
//                listAdapter2.clear();
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listAdapter2.clear();
            }
        });

    }


    private class MyTimerTask extends TimerTask
    {
        Context context;
        boolean isTimerRunning;

        MyTimerTask(Context context) {
            this.context = context;
        }

        public void run() {
                currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        spfList2.add("\n數值:" + String.valueOf(seekBar2.getProgressFloat()) + "   時間:" + currentDateTimeString + "\n");
                        listAdapter2.notifyDataSetChanged();
                    }
                });
        }

    }
    @Override
    protected void onPause() {
        super.onPause();
        tinydb.putListString("Data2", spfList2);
//        if(spfList2!=null)
//            spfList2 = tinydb.getListString("Data2");
    }

    protected void sendEmail() {
//        String[] CC = {"cheryl.gao@inno-orz.com"}; //backup
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mail to:"));
        emailIntent.setType("text/plain");

        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
//        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Test Result");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "紀錄如下 : " + spfList2.toString() + "\n \n \n- \n \n \n");

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
//            finish();
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(MainActivity.this,
                    "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }

/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.cheryl)
        {
//            Toast.makeText(this,"Hello :)",Toast.LENGTH_SHORT).show();
//            getFacebookIntent();

            Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
            String facebookUrl = getFacebookPageURL(this);
            facebookIntent.setData(Uri.parse(facebookUrl));
            startActivity(facebookIntent);
        }
        return super.onOptionsItemSelected(item);
    }
*/
}



