package com.example.admin.techkid_session2.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;

import com.example.admin.techkid_session2.R;
import com.example.admin.techkid_session2.adapters.ToeicExpandableListViewAdapter;
import com.example.admin.techkid_session2.database.DatabaseManager;
import com.example.admin.techkid_session2.database.model.CategoryModel;
import com.example.admin.techkid_session2.database.model.TopicModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ExpandableListView elvToeic;
    ToeicExpandableListViewAdapter toeicExpandableListViewAdapter;
    List<TopicModel> topicModelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        elvToeic = findViewById(R.id.elv_toeic);
        topicModelList = DatabaseManager.getInstance(this).getListTopic();
        List<CategoryModel> categoryModelList = DatabaseManager.getInstance(this).getListCategory(topicModelList);
        HashMap<String, List<TopicModel>> hashMap = DatabaseManager.getInstance(this).getHashMapTopic(topicModelList, categoryModelList);
        toeicExpandableListViewAdapter = new ToeicExpandableListViewAdapter(categoryModelList, hashMap, this);
        elvToeic.setAdapter(toeicExpandableListViewAdapter);

        //i la vi tri cua group, i1 la vi tri cua con
        elvToeic.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                TopicModel topicModel = topicModelList.get(i*5 + i1);

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                String lastTime = simpleDateFormat.format(Calendar.getInstance().getTime());
                DatabaseManager.getInstance(MainActivity.this).updateLastTime(topicModel, lastTime);

                Intent intent = new Intent(MainActivity.this, StudyActivity.class);
                intent.putExtra("topic", topicModel);
                startActivity(intent);

                return false;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        toeicExpandableListViewAdapter.refreshList(MainActivity.this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(MainActivity.this, SettingActivity.class);
        startActivity(intent);
        return super.onOptionsItemSelected(item);
    }
}
