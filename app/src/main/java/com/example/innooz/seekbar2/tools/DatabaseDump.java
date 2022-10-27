package com.example.innooz.seekbar2.tools;

import java.io.File;
import java.io.IOException;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Test on 2018/3/14.
 */
public class DatabaseDump {

    private String mDestXmlFilename;
    private SQLiteDatabase mDb;
    private Context mContext;

    public DatabaseDump(Context context, SQLiteDatabase db, String destXml) {
        mContext = context;
        mDb = db;
        mDestXmlFilename = destXml;
    }

    public void exportData() {

        try {

              Log.e("mdb", mDb.getPath());
            // get the tables out of the given sqlite database
            String sql = "SELECT * FROM data_table";
            Log.e(">>>", "export sql : " + sql);

            Cursor cur = mDb.rawQuery(sql, new String[0]);
            cur.moveToFirst();

            String tableName;
            while (cur.getPosition() < cur.getCount()) {
                tableName = cur.getString(cur.getColumnIndex("_id"));
//                tableName = cur.getString(cur.getColumnIndex("value"));

                Log.e(">>>", "tableName : " + tableName);

                // don't process these two tables since they are used
                // for metadata
                /*
                if (!tableName.equals("android_metadata")
                        && !tableName.equals("sqlite_sequence")) {
                    writeExcel(tableName);
                }
*/
                cur.moveToNext();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成一个Excel文件
     *
     *            要生成的Excel文件名
     */
    public void writeExcel(String tableName) {
        WritableWorkbook wwb = null;
        String filePath;
//        fileName = "/sdcard/QuestionData/" + tableName + ".xls";
//        fileName = Environment.getExternalStorageDirectory().toString() + File.separator + tableName + ".xls";
        filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + File.separator + tableName + ".xls";
        int r = 0;

        String sql = "select * from " + tableName;
        Log.e(">>>", "writeExcel filePath : " + filePath);
        Cursor cur = mDb.rawQuery(sql, new String[0]);
        int numcols = cur.getColumnCount();
        int numrows = cur.getCount();
        Log.e("row", numrows + "");
        Log.e("col", numcols + "");

        String records[][] = new String[numrows + 1][numcols];// 存放答案，多一行标题行

        try {
            if (cur.moveToFirst()) {
                while (cur.getPosition() < cur.getCount()) {
                    for (int c = 0; c < numcols; c++) {
                        if (r == 0) {
                            records[r][c] = cur.getColumnName(c);
                            records[r + 1][c] = cur.getString(c);
                        } else {
                            records[r + 1][c] = cur.getString(c);
                        }
                        Log.e("value" + r + " " + c, records[r][c]);
                    }
                    cur.moveToNext();
                    r++;
                }

                cur.close();
            }
            // 首先要使用Workbook类的工厂方法创建一个可写入的工作薄(Workbook)对象
            wwb = Workbook.createWorkbook(new File(filePath));
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(mContext, e.toString(), Toast.LENGTH_LONG).show();
        }
        if (wwb != null) {
            // 创建一个可写入的工作表
            // Workbook的createSheet方法有两个参数，第一个是工作表的名称，第二个是工作表在工作薄中的位置
            WritableSheet ws = wwb.createSheet("sheet1", 0);

            // 下面开始添加单元格
            for (int i = 0; i < numrows + 1; i++) {
                for (int j = 0; j < numcols; j++) {
                    // 这里需要注意的是，在Excel中，第一个参数表示列，第二个表示行
                    Label labelC = new Label(j, i, records[i][j]);
                    //      Log.i("Newvalue" + i + " " + j, records[i][j]);
                    try {
                        // 将生成的单元格添加到工作表中
                        ws.addCell(labelC);
                    } catch (WriteException e) {
                        e.printStackTrace();
                        Toast.makeText(mContext, e.toString(), Toast.LENGTH_LONG).show();
                    }

                }
            }

            try {
                // 从内存中写入文件中
                wwb.write();
                // 关闭资源，释放内存
                wwb.close();
                Toast.makeText(mContext, "export succeed. please check on " + filePath, Toast.LENGTH_LONG).show();
            } catch (IOException | WriteException e) {
                e.printStackTrace();
                Toast.makeText(mContext, e.toString(), Toast.LENGTH_LONG).show();
            }
        }
    }
}