package com.example.myapplicationui.User_Interface;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.myapplicationui.Conection.ListViewItem;
import com.example.myapplicationui.Conection.whiteVoice;
import com.example.myapplicationui.Function.SDClass;
import com.example.myapplicationui.Function.STT_Activity;
import com.example.myapplicationui.Function.TTSClass;
import com.example.myapplicationui.R;
import com.tsengvn.typekit.TypekitContextWrapper;

import java.util.ArrayList;

public class FavoriteActivity extends AppCompatActivity{

    private final int RESULT_SPEECH = 101;
    public static Context mContext;
    private ListView listview;
    private FavoriteAdapter adapter;
    ArrayList<ListViewItem> items = new ArrayList<ListViewItem>() ;
    ListViewItem item;
    String tempText = "";

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }

    public boolean loadItemsFromDB(ArrayList<ListViewItem> list) {

        if (list == null) {
            list = new ArrayList<ListViewItem>() ;
        }

        // 아이템 생성.
        item = new ListViewItem() ;
        item.setText("우리집") ;
        list.add(item) ;

        item = new ListViewItem() ;
        item.setText("학교") ;
        list.add(item) ;

        item = new ListViewItem() ;
        item.setText("카페") ;
        list.add(item) ;

        return true ;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        //TTSClass.Init(this);

        mContext = this;

        // items 로드.
        loadItemsFromDB(items) ;

        // Adapter 생성
        adapter = new FavoriteAdapter(this, R.layout.favorite_listview, items) ;

        // 리스트뷰 참조 및 Adapter달기
        listview = (ListView) findViewById(R.id.favoriteList);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                ListViewItem item = (ListViewItem)parent.getItemAtPosition(position); //아이템 받아오기
                ((whiteVoice)getApplicationContext()).target = item.getText();
                Intent intent = new Intent(getApplication(), NavigationActivity.class);
                startActivity(intent);
            }
        });

        if(getIntent().getStringExtra("value")!=null && getIntent().getStringExtra("addCode").equals("400")) {
            AddItem(getIntent().getStringExtra("value"));
        }

        /*
        String[] tempArray = new String[10];
        //읽어줄 즐겨찾기 리스트 하나의 스트링으로 만들기
        for(int i = 0; i < items.size(); i++) {
            tempArray[i] = items.get(i).getText();
        }

        ((whiteVoice)getApplicationContext()).sttCode = 2; //음성인식 구분
        TTSClass.Init(this, tempArray);
        Intent intentA = new Intent(this, STT_Activity.class);
        startActivityForResult(intentA, 110);
        */
    }

    public void AddItem(String text){
        item = new ListViewItem();
        item.setText(text);
        items.add(item);
    }

    public void onClickAdd(View view){
        TTSClass.Init(this, "즐겨찾기에 추가하실 목적지를 말하세요");
        doSTT();
    }

    private void doSTT(){
        try {
            Thread.sleep(1500);
        }
        catch (Exception e){

        }
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ko-KR");
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "지금 말하세요");

        try {
            startActivityForResult(intent, RESULT_SPEECH);
        }
        catch (ActivityNotFoundException e){
            Toast.makeText(getApplicationContext(), "오류입니다", Toast.LENGTH_SHORT).show();
            e.getStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode == RESULT_OK && requestCode == RESULT_SPEECH){
            ArrayList<String> sstResult = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String result_stt = sstResult.get(0);

            String replace_sst = "";
            replace_sst = result_stt.replace(" ", "");
            TTSClass.Init(this, replace_sst);
            AddItem(replace_sst);
        }/*
        else if(resultCode == RESULT_OK && requestCode == 110){
            String fData= data.getStringExtra("value");
            for(int i = 0; i < items.size(); i++) {
                tempText = items.get(i).getText();
                int temp = SDClass.distance(tempText,fData);
                if(temp<=1){
                    Toast.makeText(getApplicationContext(), "문자열있음 : "+ temp, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplication(), NavigationActivity.class);
                    intent.putExtra("value", tempText);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(getApplicationContext(), "문자열없음: "+ temp, Toast.LENGTH_SHORT).show();
                }
            }
        }*/
    }

    public void RemoveData(int nPosition){
        this.adapter.remove(this.items.get(nPosition));
    }

}
