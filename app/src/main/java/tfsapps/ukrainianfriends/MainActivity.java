package tfsapps.ukrainianfriends;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

//広告
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.AdRequest;


public class MainActivity extends AppCompatActivity {

    //  DB関連
    private MyOpenHelper helper;            //DBアクセス
    private int db_isopen = 0;              //DB使用したか
    private int db_friends = 0;             //DB友達数
    final int MAX_FRIENDS = 999999999;
    // 広告
    private AdView mAdview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //  広告
        mAdview = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdview.loadAd(adRequest);
    }

    public void setMainScreen(){
        TextView result1 = (TextView) findViewById(R.id.text_friends);

        if (db_friends >= MAX_FRIENDS){
            result1.setText(""+"7,875,000,000"+"\nfriends\nThe world is all friends");
        }
        else{
            result1.setText(""+db_friends+"\nfriends");
        }
    }


    public void onFriends(View view){
        int temp = db_friends;

        temp *= 2;

        if (temp <= MAX_FRIENDS){
            db_friends = temp;
        }
        else{
            db_friends = MAX_FRIENDS;
        }

        setMainScreen();
    }


    /***************************************************
         各種OS上の動作定義
     ****************************************************/
    @Override
    public void onStart() {
        super.onStart();

        //DBのロード
        /* データベース */
        helper = new MyOpenHelper(this);
        AppDBInitRoad();
        setMainScreen();
    }
    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause(){
        super.onPause();
        //  DB更新
        AppDBUpdated();
    }
    @Override
    public void onStop(){
        super.onStop();
        //  DB更新
        AppDBUpdated();
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        //  DB更新
        AppDBUpdated();
    }


    /***************************************************
         DB初期ロードおよび設定
     ****************************************************/
    public void AppDBInitRoad() {
        SQLiteDatabase db = helper.getReadableDatabase();
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT");
        sql.append(" isopen");
        sql.append(" ,friends");
        sql.append(" FROM appinfo;");
        try {
            Cursor cursor = db.rawQuery(sql.toString(), null);
            //TextViewに表示
            StringBuilder text = new StringBuilder();
            if (cursor.moveToNext()) {
                db_isopen = cursor.getInt(0);
                db_friends = cursor.getInt(1);
            }
        } finally {
            db.close();
        }

        db = helper.getWritableDatabase();
        if (db_isopen == 0) {
            long ret;
            /* 新規レコード追加 */
            ContentValues insertValues = new ContentValues();
            insertValues.put("isopen", 1);
            insertValues.put("friends", 1);
            insertValues.put("data1", 0);
            insertValues.put("data2", 0);
            insertValues.put("data3", 0);
            insertValues.put("data4", 0);
            insertValues.put("data5", 0);
            try {
                ret = db.insert("appinfo", null, insertValues);
            } finally {
                db.close();
            }
            db_isopen = 1;
            db_friends = 1;
            /*
            if (ret == -1) {
                Toast.makeText(this, "DataBase Create.... ERROR", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "DataBase Create.... OK", Toast.LENGTH_SHORT).show();
            }
             */
        } else {
            /*
            Toast.makeText(this, "Data Loading...  friends:" + db_friends, Toast.LENGTH_SHORT).show();
             */
        }
    }

    /***************************************************
         DB更新
     ****************************************************/
    public void AppDBUpdated() {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues insertValues = new ContentValues();
        insertValues.put("isopen", db_isopen);
        insertValues.put("friends", db_friends);
        int ret;
        try {
            ret = db.update("appinfo", insertValues, null, null);
        } finally {
            db.close();
        }
/*
        if (ret == -1) {
            Toast.makeText(this, "Saving.... ERROR ", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Saving.... OK "+ "friends= "+db_friends, Toast.LENGTH_SHORT).show();
        }

 */
    }
}