package com.example.admin.techkid_session2.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.admin.techkid_session2.R;
import com.example.admin.techkid_session2.backgrounds.ReminderService;
import com.example.admin.techkid_session2.backgrounds.ScreenOnReceiver;
import com.example.admin.techkid_session2.backgrounds.ScreenOnService;
import com.example.admin.techkid_session2.database.DatabaseManager;
import com.example.admin.techkid_session2.database.model.TopicModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingActivity extends AppCompatActivity {

    private static final String TAG = "SettingActivity";
    public final String TIME_REMINDER = "time_reminder";
    public final String TOPIC_REMINDER = "topic_reminder";

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_topic)
    TextView tvTopic;
    @BindView(R.id.iv_save)
    ImageView ivSave;
    @BindView(R.id.view1)
    View view1;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.sw_reminder)
    Switch swReminder;
    @BindView(R.id.tv_enable_timer)
    TextView tvEnableTimer;
    @BindView(R.id.tv_pick_timer)
    TextView tvPickTimer;
    @BindView(R.id.view2)
    View view2;
    @BindView(R.id.tv_review)
    TextView tvReview;
    @BindView(R.id.sw_review)
    Switch swReview;
    @BindView(R.id.view3)
    View view3;
    @BindView(R.id.lv_topics)
    ListView lvTopics;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String time;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);

        getSupportActionBar().hide();

        sharedPreferences = getSharedPreferences("Setting", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        List<TopicModel> topicModels = DatabaseManager.getInstance(this).getListTopic();
        List<String> topicName = new ArrayList<>();
        for(TopicModel topicModel : topicModels) {
            topicName.add(topicModel.name);
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.item_list_review, topicName);
        lvTopics.setAdapter(arrayAdapter);

        time = sharedPreferences.getString(TIME_REMINDER, null);
        if(time != null) {
            tvPickTimer.setText(time);
            swReminder.setChecked(true);
        } else {
            tvPickTimer.setVisibility(View.GONE);
        }

        Set<String> set = sharedPreferences.getStringSet(TOPIC_REMINDER, null);
        if(set != null) {
            swReview.setChecked(true);
            List<String> listChecked = new ArrayList<>(set);
            for(String position : listChecked) {
                lvTopics.setItemChecked(Integer.parseInt(position), true);
            }
            lvTopics.setVisibility(View.VISIBLE);
        } else {
            lvTopics.setVisibility(View.GONE);
        }

        swReminder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked) {
                    tvPickTimer.setVisibility(View.VISIBLE);
                } else {
                    tvPickTimer.setVisibility(View.GONE);
                }

                if(compoundButton.isShown()) {
                    if(isChecked) {
                        setTimeReminder();
                    }
                }
            }
        });

        swReview.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked) {
                    lvTopics.setVisibility(View.VISIBLE);
                } else {
                    lvTopics.setVisibility(View.GONE);
                }
            }
        });
    }

    private void setTimeReminder() {
        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = calendar.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                time = String.format("%02d:%02d", i, i1);
                tvPickTimer.setText(time);
            }
        }, currentHour, currentMinute, true );

        timePickerDialog.show();
    }

    @OnClick({R.id.iv_back, R.id.iv_save, R.id.tv_pick_timer})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.iv_save:
                saveSetting();
                break;
            case R.id.tv_pick_timer:
                setTimeReminder();
                break;
        }
    }

    private void saveSetting() {
        if(swReminder.isChecked()) {
            //save
            editor.putString(TIME_REMINDER, time);
            //noti
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            Intent intent = new Intent(this, ReminderService.class);
            PendingIntent pendingIntent = PendingIntent.getService(this,
                    0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            Calendar calendar = Calendar.getInstance();
            //hh:mm
            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time.substring(0, 2)));
            calendar.set(Calendar.MINUTE, Integer.parseInt(time.substring(3, 5)));

            alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent
                    );

        } else {
            editor.putString(TIME_REMINDER, null);
        }

        //save review
        if(swReview.isChecked()) {
            //1. get checked list
            Log.d(TAG,"saveSetting : " + lvTopics.getCheckedItemPositions());
            SparseBooleanArray sparseBooleanArray = lvTopics.getCheckedItemPositions();

            //2. add checked position to set
            Set<String> set = new HashSet<>();
            for(int i = 0; i < lvTopics.getAdapter().getCount(); i++) {
                if (sparseBooleanArray.get(i)) {
                    set.add((i + ""));
                }
            }

            //3. save set to SharedPreference
            if(set.size() > 0) {
                editor.putStringSet(TOPIC_REMINDER, set);
                startService(new Intent(this, ScreenOnService.class));
            } else {
                editor.putStringSet(TOPIC_REMINDER, null);
            }

        } else {
            editor.putStringSet(TOPIC_REMINDER, null);
        }

        editor.commit();

        Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
        this.finish();
    }
}
