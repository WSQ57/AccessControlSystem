package com.weiqian.demo;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

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
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sqlite);
        final EditText mEditText01 = (EditText)findViewById(R.id.EditText01);
        final EditText mEditText02 = (EditText)findViewById(R.id.EditText02);
        //建立数据库PhoneBookDB和表Table:Users
        helper = new DBConnection(this);
        final SQLiteDatabase db = helper.getWritableDatabase();

        //接收传入参数
        Intent intent = getIntent();
        String input_password = intent.getStringExtra("password");
        Log.d("SQLiteActivity", String.valueOf(input_password));
        String visitor = null;
        if(input_password != null){
            //接收到参数
            //进行查询操作
            String where = UserSchema.PASSWORD + "=" + input_password;
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
        if(visitor == null){
            // 查无此人
            Intent returnValue = new Intent();
            Log.d("SQLiteActivity", "No such person");
            setResult(RESULT_CANCELED, returnValue);
            finish();
        }
        else if(!visitor.equals("admin")) { // 非管理员用户，不可管理数据库

            Intent returnValue = new Intent();
            Log.d("SQLiteActivity", String.valueOf(input_password));
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
        private DBConnection(Context ctx) {
            super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
        }
        public void onCreate(SQLiteDatabase db) {
            //创建门禁成员表
            String sql = "CREATE TABLE " + UserSchema.TABLE_NAME + " ("
                    + UserSchema.ID  + " INTEGER primary key autoincrement, "
                    + UserSchema.USER_NAME + " text not null, "
                    + UserSchema.PASSWORD  + " text not null " + ");";
            //Log.i("haiyang:createDB=", sql);
            db.execSQL(sql);

            //创建登录记录表
            sql = "CREATE TABLE " + LoginTime.TABLE_NAME + " ("
                    + LoginTime.ID  + " INTEGER primary key autoincrement, "
                    + LoginTime.LOGIN_USER + " text not null, "
                    + LoginTime.LOGIN_TIME + "time not null " + ");";

            db.execSQL(sql);
        }
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // TODO Auto-generated method stub
        }
    }

}