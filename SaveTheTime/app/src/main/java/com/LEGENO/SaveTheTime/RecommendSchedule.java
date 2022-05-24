package com.LEGENO.SaveTheTime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;

public class RecommendSchedule extends AppCompatActivity {
    DatePicker datePicker;
    Button ok, no;
    LinearLayout datepickerLayout;
    TextView textPicker;
    ListView recommendList;

    ArrayAdapter<String> adapter;

    ArrayList<String> memberId;
    ArrayList<Schedule> schedules = new ArrayList<>();

    LocalDate selectDate;
    LocalTime cursor;
    ArrayList<LocalTime> cursorStack = new ArrayList<>();

    int loaded = 0;
    int year, month, day;

    Legeno runnable;
    Handler mHandler;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_recommend_schedule);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            memberId = bundle.getStringArrayList("MI");
        }


        datepickerLayout = findViewById(R.id.date_picker_layout);
        textPicker = findViewById(R.id.text_picker);
        recommendList = findViewById(R.id.recommend_list);
        ok = findViewById(R.id.ok_btn);
        no = findViewById(R.id.no_btn);


        year = LocalDate.now().getYear();
        month = LocalDate.now().getMonthValue();
        day = LocalDate.now().getDayOfMonth();
        selectDate = LocalDate.now();
        datePicker = findViewById(R.id.date_picker_recommend);
        datePicker.init(year, month - 1, day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year_, int monthOfYear, int dayOfMonth) {
                year = year_;
                month = monthOfYear +1;
                day = dayOfMonth;
                selectDate = LocalDate.of(year, month, day);
            }
        });


        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAllSchedules();

                //DatePicker 지우기
                datePicker.setVisibility(View.GONE);
                textPicker.setText("계산 중.....");
                ok.setVisibility(View.INVISIBLE);

                runnable = new Legeno(memberId.size());
                runnable.start();
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                legeno();

                Log.d("dbtest", "결과: " + cursorStack.toString());

                textPicker.setText("추천 일정 시간입니다.");

                ArrayList<String> list = new ArrayList<>();

                for (int i = 0; i < cursorStack.size(); i += 2) {
                    list.add(cursorStack.get(i).toString() + " ~ " + cursorStack.get(i + 1).toString());
                }

                adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_expandable_list_item_1, list);
                recommendList.setAdapter(adapter);
                recommendList.setVisibility(View.VISIBLE);

            }
        };
    }

    private void getAllSchedules() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        schedules.clear();

        for (int i = 0; i < memberId.size(); i++) {
            db.collection(memberId.get(i)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        QuerySnapshot document = task.getResult();
                        for (QueryDocumentSnapshot doc : document) {
                            Schedule sch = doc.toObject(Schedule.class);
                            if (sch.startTimeByClass().toLocalDate().isEqual(selectDate) &&
                                    sch.endTimeByClass().toLocalDate().isEqual(selectDate)) {
                                schedules.add(sch);
                            }
                        }

                        loaded++;
                        Log.d("dbtest", "loaded :  " + loaded + " /  size :" + memberId.size());

                    } else {

                    }
                }
            });
        }
    }

    private void legeno() {
        cursorStack.clear();
        cursor = LocalTime.of(0, 0);
        schedules.sort(new Asending());

        Log.d("dbtest", "Sorting Schedules : " + schedules.toString());

        for (int i = 0; i < schedules.size(); i++) {
            if (cursor.isBefore(schedules.get(i).startTimeByClass().toLocalTime())) {
                cursorStack.add(cursor);
                cursorStack.add(schedules.get(i).startTimeByClass().toLocalTime());
                cursor = schedules.get(i).endTimeByClass().toLocalTime();
            } else {
                if (cursor.isBefore(schedules.get(i).endTimeByClass().toLocalTime())) {
                    cursor = schedules.get(i).endTimeByClass().toLocalTime();
                }
            }
        }
        if (cursor.isBefore(LocalTime.of(23, 59))) {
            cursorStack.add(cursor);
            cursorStack.add(LocalTime.of(23, 59));
        }
    }

    class Asending implements Comparator<Schedule> {
        public int compare(Schedule a, Schedule b) {
            if (a.startTimeByClass().isEqual(b.startTimeByClass()))
                return 0;
            else if (a.startTimeByClass().isBefore(b.startTimeByClass()))
                return -1;
            else
                return 1;
        }
    }

    class Legeno extends Thread {
        int allloadsize;

        Legeno(int size) {
            super();
            allloadsize = size;
        }

        @Override
        public void run() {
            while (loaded != allloadsize) {
                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            mHandler.sendEmptyMessage(0);
        }
    }
}

