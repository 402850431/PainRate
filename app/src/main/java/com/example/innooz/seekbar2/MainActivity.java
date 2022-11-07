package com.example.innooz.seekbar2;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.innooz.seekbar2.tools.DatabaseDump;
import com.example.innooz.seekbar2.tools.DatetimeExtKt;
import com.example.innooz.seekbar2.tools.MySQLite;
import com.example.innooz.seekbar2.tools.OnSuccessListener;
import com.example.innooz.seekbar2.tools.TinyDB;
import com.xw.repo.BubbleSeekBar;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements OnSuccessListener {

    BubbleSeekBar seekBar;
    //    ListView listView;
//    Button exportButton, clearDataButton;
    TinyDB tinydb;
    String currentDateTimeString;
    String inputName;
    String fileName;
    String[] TO = {"lee.shinyu@gmail.com"};
    Timer timer = null;
    boolean isTimerRunning = false;
    Button startPauseBtn;

    private ArrayList<String> spfList2 = new ArrayList<String>();
    private ArrayAdapter<String> listAdapter2;

    EditText timeEt, etName;
    TextView minusTv;
    TextView increaseTv;
    TextView tvFileInfo;
    MySQLite mySQLite;
    View dialogView;
    AlertDialog.Builder dialogBuilder;

    DatabaseDump databaseDump;
    SQLiteDatabase sqLiteDatabase;


    private final String[] PERMISSION_LIST = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private final int PERMISSION_REQUEST_CODE = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewByIds();

        customActionBar();
        setUpDialog();
        lv();
//        rv();
    }

    private void setUpDialog() {
        dialogView = View.inflate(this, R.layout.custom_dialog, null);
//        dialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog, R.layout.custom_dialog, false);
        etName = (EditText) dialogView.findViewById(R.id.etName);
        timeEt = (EditText) dialogView.findViewById(R.id.timeEt);
        minusTv = (TextView) dialogView.findViewById(R.id.minus);
        increaseTv = (TextView) dialogView.findViewById(R.id.increase);

        minusTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int timeNum = Integer.parseInt(timeEt.getText().toString());
                if (timeNum > 1) {
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
        /*open dialog*/
        openDialog();

    }

    private void openDialog() {
        dialogBuilder = new AlertDialog.Builder(this);

        dialogBuilder.setTitle("初始設定")
                .setView(dialogView)
                .setCancelable(false)
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        //設定當dialog被點掉時,remove原來放進來的view(dialogItem),否則再次點fab時會丟exception
                        ((ViewGroup)dialogView.getParent()).removeView(dialogView);
                    }
                })
                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //override by 'alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener'
                    }
                });

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etName.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please input your name first.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Start Recording", Toast.LENGTH_SHORT).show();
                    int timeNum = Integer.parseInt(timeEt.getText().toString().trim());
                    inputName = etName.getText().toString();
                    startTimer(timeNum);
                    alertDialog.dismiss();
                }
            }
        });
    }

    private void findViewByIds() {
        timer = new Timer(true);
        tinydb = new TinyDB(getApplicationContext());
//        listView = (ListView)findViewById(R.id.listview2);
        seekBar = (BubbleSeekBar) findViewById(R.id.seekbar2);
        tvFileInfo = (TextView) findViewById(R.id.tvFileInfo);
//        exportButton = (Button)findViewById(R.id.button);
//        clearDataButton = (Button)findViewById(R.id.button2);
        mySQLite = new MySQLite(this);
        sqLiteDatabase = mySQLite.getWritableDatabase();
        databaseDump = new DatabaseDump(this, sqLiteDatabase, "數據文檔");

    }

    private void customActionBar() {

        final ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);

        final View mCustomView = mInflater.inflate(R.layout.custom_menu, null);
        startPauseBtn = (Button) mCustomView.findViewById(R.id.startBtn);
        Button resetBtn = (Button) mCustomView.findViewById(R.id.resetBtn);
//        Button settingBtn = (Button) mCustomView.findViewById(R.id.settingBtn);
        Button exportButton = (Button) mCustomView.findViewById(R.id.exploreBtn);

        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowCustomEnabled(true);

        startPauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int timeNum = Integer.parseInt(timeEt.getText().toString().trim());
                startTimer(timeNum);
            }
        });
        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearAllData();
                resetSeekbar();
                openDialog();
            }
        });
        /*
        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        });
        */
        exportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!hasStorePermission()) {
                    requestPermission();
                } else {
                    pauseRecord();
                    tinydb.putListString("Data2", spfList2);
                    fileName = inputName + "_" + DatetimeExtKt.toTimeFormat(new Date(System.currentTimeMillis()), DatetimeExtKt.YMDHM_FORMAT);
                    databaseDump.writeExcel(fileName, MainActivity.this::onSuccess);
                }
            }
        });

    }

    private void clearAllData() {
        listAdapter2.clear();
        listAdapter2.notifyDataSetChanged();
        mySQLite.deleteAll();
    }

    private void resetSeekbar() {
        seekBar.setProgress(0f);
        seekBar.setVerticalScrollbarPosition(0);
    }

    void startTimer(int timeNum) {

        if (!isTimerRunning && timeNum > 0) { //start
            startRecord(timeNum);
        } else { //pause
            pauseRecord();
        }
    }

    private void startRecord(int timeNum) {
        isTimerRunning = true;
        timeEt.setEnabled(false);
        timer = new Timer(true);
        timer.schedule(new MyTimerTask(MainActivity.this), timeNum, timeNum);
        startPauseBtn.setText("暫停");
    }

    private void pauseRecord() {
        isTimerRunning = false;
        stopTimer();
        timeEt.setEnabled(true);
        startPauseBtn.setText("開始");
    }

    void stopTimer() {

        if (timer != null) {
            timer.cancel();
            timer = null;
        }

    }

    /*
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
    */
    private void lv() {

        if (spfList2 != null)
            spfList2 = tinydb.getListString("Data2");

        listAdapter2 = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, spfList2) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = ((TextView) view.findViewById(android.R.id.text1));
                textView.setTextSize(13);
                return view;
            }
        };

//        listView.setAdapter(listAdapter2);

/*

        exportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!hasStorePermission()) {
                    requestPermission();
                } else {
                    tinydb.putListString("Data2", spfList2);
                    Log.e(">>>export", "export");
                    databaseDump.writeExcel("data_table");
                }
            }
        });
        clearDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                listAdapter2.clear();
                clearAllData();
            }
        });
*/

    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(
                this,
                PERMISSION_LIST,
                PERMISSION_REQUEST_CODE
        );
    }

    private boolean hasStorePermission() {
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onSuccess(String filePath) {
        tvFileInfo.setText("檔案位置：" + filePath + "\n檔案名稱：" + fileName);
    }


    private class MyTimerTask extends TimerTask {
        Context context;
        boolean isTimerRunning;

        MyTimerTask(Context context) {
            this.context = context;
        }

        public void run() {
            currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
            String nowDate = DatetimeExtKt.toTimeFormat(new Date(System.currentTimeMillis()), DatetimeExtKt.YMD_FORMAT);
            String nowTime = DatetimeExtKt.toTimeFormat(new Date(System.currentTimeMillis()), DatetimeExtKt.HM_FORMAT);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mySQLite.insert(nowDate, nowTime, seekBar.getProgressFloat());
                    spfList2.add("\n數值:" + String.valueOf(seekBar.getProgressFloat()) + "   時間:" + currentDateTimeString + "\n");
                    listAdapter2.notifyDataSetChanged();
                }
            });
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        tinydb.putListString("Data2", spfList2);
    }

}



