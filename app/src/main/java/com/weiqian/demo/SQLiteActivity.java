package com.weiqian.demo;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Date;
import java.text.SimpleDateFormat;

public class SQLiteActivity extends Activity {
    View.OnClickListener listener_add = null;
    View.OnClickListener listener_update = null;
    View.OnClickListener listener_delete = null;
    View.OnClickListener listener_clear = null;
    View.OnClickListener listener_query = null;
    Button button_add;
    Button button_update;
    Button button_delete;
    Button button_clear;
    Button button_query;
    DBConnection helper;
    private static boolean importDB = false;
    public int id_this;
    public interface UserSchema {
        String TABLE_NAME = "Users";          //Table Name
        String ID = "_id";
        String USER_NAME = "user_name";       //User Name
        String PASSWORD = "password";
    }
    public interface LoginTime{
        String TABLE_NAME = "Login_Detail";
        String ID = "_id";
        String LOGIN_TIME = "login_time";
        String LOGIN_USER = "login_user";
    }


    private static final String UserSql = "CREATE TABLE " + UserSchema.TABLE_NAME + " ("
            + UserSchema.ID  + " INTEGER primary key autoincrement, "
            + UserSchema.USER_NAME + " text not null, "
            + UserSchema.PASSWORD  + " text not null " + ");";

    private static final String LoginSql = "CREATE TABLE " + LoginTime.TABLE_NAME + " ("
            + LoginTime.ID  + " INTEGER primary key autoincrement, "
            + LoginTime.LOGIN_USER + " text not null, "
            + LoginTime.LOGIN_TIME + " not null " + ");";

    public void addDate(String loginTime, String loginUser){
        Log.d("addDate","1");
        helper = new DBConnection(this);
        final SQLiteDatabase db = helper.getWritableDatabase();

        // 插入新数据
        ContentValues values = new ContentValues();
        values.put(LoginTime.LOGIN_TIME, loginTime);
        values.put(LoginTime.LOGIN_USER, loginUser);
        db.insert(LoginTime.TABLE_NAME, null, values);
        db.close();
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sqlite);
        final EditText mEditText01 = (EditText)findViewById(R.id.EditText01);
        final EditText mEditText02 = (EditText)findViewById(R.id.EditText02);
        //建立数据库PhoneBookDB和表Table:Users
        helper = new DBConnection(this);
        if(!importDB) {
            try {
                Log.d("importDB","Importing");
                helper.copyDataBase();
                importDB = true;
            } catch (IOException ioe) {
                throw new Error("Unable to create database");
            }
        }
        final SQLiteDatabase db = helper.getWritableDatabase();

        //接收传入参数
        Intent intent = getIntent();
        String input_password = intent.getStringExtra("password");
        Log.d("SQLiteActivity", String.valueOf(input_password));
        String visitor = null;
        if(input_password != null){
            //接收到参数
            //进行查询操作
            String where = UserSchema.PASSWORD + "='" + input_password + "'";
            Cursor cursor = db.query(UserSchema.TABLE_NAME,null,where,null,
                    null,null,null);
            if(cursor.moveToFirst()){
                do {
                    String user_name_this = cursor.getString(1);
                    String password_this = cursor.getString(2);
                    Log.d("SQLiteActivity","password is " + password_this);
                    Log.d("SQLiteActivity","username is " + user_name_this);
                    visitor = user_name_this;
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        Intent returnValue = new Intent();
        if(visitor == null){
            // 查无此人

            Log.d("SQLiteActivity", "No such person");
            setResult(RESULT_CANCELED, returnValue);
            finish();
        }
        else if(!visitor.equals("admin")) { // 非管理员用户，不可管理数据库
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss ");
            Date curDate = new Date(System.currentTimeMillis());
            String str = formatter.format(curDate);
            Log.d("SQLiteActivity",str);


            // 查询时间
            Cursor cursor = db.query(LoginTime.TABLE_NAME,null,null,null,
                    null,null,LoginTime.LOGIN_TIME + " DESC");
            int count = 0;
            if(cursor.moveToFirst()) {
                do{
                    String tmp = cursor.getString(1) + " "+ cursor.getString(2);
                    String reName = "time" + count;
                    returnValue.putExtra(reName,tmp);
                    count++;
                }while (cursor.moveToNext() && count < 5);
            }
            returnValue.putExtra("count",count);
            addDate(str,visitor);
            Log.d("return Value", String.valueOf(input_password));
            returnValue.putExtra("visitor", visitor);
            setResult(RESULT_OK, returnValue);
            finish();
        }

        final String[] FROM =
                {
                        UserSchema.ID,
                        UserSchema.USER_NAME,
                        UserSchema.PASSWORD
                };
        //取得所有数据的USER_NAME，放置在list[]上
        Cursor c = db.query(UserSchema.TABLE_NAME, new String[] {UserSchema.USER_NAME},
                null, null, null, null, null);
        c.moveToFirst();
        CharSequence[] list = new CharSequence[c.getCount()];
        for (int i = 0; i < list.length; i++) {
            list[i] = c.getString(0);
            c.moveToNext();
        }
        c.close();
        //显示USER_NAME在Spinner下拉列表-spinner上
        Spinner spinner = (Spinner)findViewById(R.id.Spinner01);
        spinner.setAdapter(new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, list));
        //在Spinner下拉列表-spinner上选定查询的数据，显示所有数据在画面上
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String user_name = ((Spinner)parent).getSelectedItem().toString();
                Cursor c = db.query("Users", FROM , "user_name='" + user_name + "'",
                        null, null, null, null);
                c.moveToFirst();
                id_this = Integer.parseInt(c.getString(0));
                String user_name_this = c.getString(1);
                String password_this = c.getString(2);
                c.close();
                mEditText01.setText(user_name_this);
                mEditText02.setText(password_this);
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        //按下[Add]按钮时，新增一行数据
        listener_add = new View.OnClickListener() {
            public void onClick(View v) {
                ContentValues values = new ContentValues();
                values.put(UserSchema.USER_NAME, mEditText01.getText().toString());
                values.put(UserSchema.PASSWORD, mEditText02.getText().toString());
                SQLiteDatabase db = helper.getWritableDatabase();
                db.insert(UserSchema.TABLE_NAME, null, values);
                db.close();
                onCreate(savedInstanceState);
            }
        };

        //按下[Update]按钮时，更新一行数据
        listener_update = new View.OnClickListener() {
            public void onClick(View v) {
                ContentValues values = new ContentValues();
                values.put(UserSchema.USER_NAME, mEditText01.getText().toString());
                values.put(UserSchema.PASSWORD, mEditText02.getText().toString());
                String where = UserSchema.ID + " = " + id_this;
                SQLiteDatabase db = helper.getWritableDatabase();
                db.update(UserSchema.TABLE_NAME, values, where ,null);
                db.close();
                onCreate(savedInstanceState);
            }
        };

        //按下[Delete]按钮时，刪除一行数据
        listener_delete = new View.OnClickListener() {
            public void onClick(View v) {
                String where = UserSchema.ID + " = " + id_this;
                SQLiteDatabase db = helper.getWritableDatabase();
                db.delete(UserSchema.TABLE_NAME, where ,null);
                db.close();
                onCreate(savedInstanceState);
            }
        };
        //按下[Clear]按钮时，清空编辑框
        listener_clear = new View.OnClickListener() {
            public void onClick(View v) {
                mEditText01.setText("");
                mEditText02.setText("");
            }
        };

        //设定BUTTON0i,i=1,2,3,4的OnClickListener
        button_add = (Button)findViewById(R.id.Button01);
        button_add.setOnClickListener(listener_add);
        button_update = (Button)findViewById(R.id.Button02);
        button_update.setOnClickListener(listener_update);
        button_delete = (Button)findViewById(R.id.Button03);
        button_delete.setOnClickListener(listener_delete);
        button_clear = (Button)findViewById(R.id.Button04);
        button_clear.setOnClickListener(listener_clear);
        button_query = (Button)findViewById(R.id.Button05);
        button_query.setOnClickListener(listener_query);
    }

    //SQLiteOpenHelper-建立数据库PhoneBookDB和Table:Users
    public static class DBConnection extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = "AccessControlDB";
        private static final int DATABASE_VERSION = 1;
        private final Context myContext;

        private DBConnection(Context ctx) {
            super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
            this.myContext = ctx;
        }

        private void copyDataBase()throws IOException {
            //Open your local db as the input stream
            String DB_NAME = "AccessControlDB";
            InputStream myInput = myContext.getAssets().open(DB_NAME);
            // Path to the just created empty db
            String DB_PATH = "/data/data/com.weiqian.demo/databases/";

            File dir = new File(DB_PATH);
            if (!dir.exists()){
                dir.mkdirs();
            }

            String outFileName = DB_PATH + DB_NAME;
            //Open the empty db as the output stream
            OutputStream myOutput = new FileOutputStream(outFileName);
            //transfer bytes from the inputfile to the outputfile
            byte[]buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }

            //Close the streams
            myOutput.flush();
            myOutput.close();
            myInput.close();
        }

        public void onCreate(SQLiteDatabase db) {
            //创建门禁成员表
            //Log.i("haiyang:createDB=", sql);
            db.execSQL(UserSql);

            //创建登录记录表
            db.execSQL(LoginSql);
        }
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // TODO Auto-generated method stub
            db.execSQL(UserSql);
            db.execSQL(LoginSql);
            onCreate(db);
        }
    }

}