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
import android.widget.ListView;
import android.widget.Toast;

import com.xw.repo.BubbleSeekBar;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    BubbleSeekBar seekBar2;
    ListView listView;
    Button button, button2;
    TinyDB tinydb;
    String currentDateTimeString;

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

        tinydb = new TinyDB(getApplicationContext());
        listView = (ListView)findViewById(R.id.listview2);
        seekBar2 = (BubbleSeekBar)findViewById(R.id.seekbar2);
        button = (Button)findViewById(R.id.button);
        button2 = (Button)findViewById(R.id.button2);

        customActionBar();
        lv();
//        rv();
    }

    private void customActionBar() {

        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);

        View mCustomView = mInflater.inflate(R.layout.custom_menu, null);
//        TextView mTitleTextView = (TextView) mCustomView.findViewById(R.id.title_text);
//        mTitleTextView.setText("My Own Title");


        final Button cheryl = (Button) mCustomView.findViewById(R.id.cheryl);
//        cheryl.setText("Made By Cheryl");
        cheryl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
                String facebookUrl = getFacebookPageURL(getApplicationContext());
                facebookIntent.setData(Uri.parse(facebookUrl));
                startActivity(facebookIntent);
            }
        });

        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowCustomEnabled(true);

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
    private void lv() {
        if(spfList2!=null)
            spfList2 = tinydb.getListString("Data2");

        listAdapter2 = new ArrayAdapter(MainActivity.this,android.R.layout.simple_list_item_1,spfList2);
        listView.setAdapter(listAdapter2);

        seekBar2.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {
                currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                spfList2.add("\n數值:" + String.valueOf(progressFloat) + "  時間:" + currentDateTimeString + "\n");
                listAdapter2.notifyDataSetChanged();

            }

            @Override
            public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {

            }
        });

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

    @Override
    protected void onPause() {
        super.onPause();
        tinydb.putListString("Data2", spfList2);
//        if(spfList2!=null)
//            spfList2 = tinydb.getListString("Data2");
    }

    protected void sendEmail() {
        String[] TO = {"lee.shinyu@gmail.com"};
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
            finish();
            Log.e("Finished send email", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(MainActivity.this,
                    "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }

    public void getFacebookIntent() {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://profile/100001991750191"));
            startActivity(intent);
        } catch (Exception e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.facebook.com/MY_PAGE_NAME")));
        }
    }

    public String getFacebookPageURL(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) { //newer versions of fb app
                return "fb://profile/100001991750191";
//                return "fb://facewebmodal/f?href=" + FACEBOOK_URL;
            } else { //older versions of fb app
                return "fb://page/" + FACEBOOK_PAGE_ID;
            }
        } catch (PackageManager.NameNotFoundException e) {
            return FACEBOOK_URL; //normal web url
        }
    }

}



