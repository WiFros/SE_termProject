package com.LEGENO.SaveTheTime;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Loading extends Activity {
    TextView LoadingState;
    ProgressBar bar;
    ProgressHandler handler;
    boolean isRunning = false;
    ArrayList<Schedule> schedules = new ArrayList<>();
    Map<String, String> joinedGroups = new HashMap<>();
    ArrayList<GroupSchedule> groupSchedules = new ArrayList<>();

    String userName;
    String userEmail;
    Uri userPhoto;

    int loadmax = 1;
    int loaded = 0;
    boolean isAllLoaded = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        bar = (ProgressBar) findViewById(R.id.progress);
        LoadingState = findViewById(R.id.LoadingText);
        handler = new ProgressHandler();
        groupSchedules.clear();
        getMyDB();
    }

    @Override
    protected void onStart() {
        super.onStart();
        bar.setProgress(0);
        Thread thread1 = new Thread(new Runnable() {
            public void run() {
                try {
                    while (!isAllLoaded) {
                        Thread.sleep(100);
                        Message msg = handler.obtainMessage();
                        msg.arg1 = loaded;
                        msg.arg2 = loadmax;
                        handler.sendMessage(msg);
                    }
                    finish();
                } catch (Exception ex) {
                    Log.e("MainActivity", "Exception in processing message.", ex);
                }
            }
        });
        isRunning = true;
        thread1.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        isRunning = false;

        //로딩에서 미리 받아서 메인으로 스케줄 보냄
        Bundle bundle = new Bundle();
        bundle.putString("Name", userName);
        bundle.putString("Email", userEmail);
        bundle.putParcelable("Photo", userPhoto);
        bundle.putSerializable("schedules", schedules);
        ArrayList<Map<String, String>> tempList = new ArrayList<>();
        tempList.add(joinedGroups);
        bundle.putSerializable("groups", tempList);
        bundle.putSerializable("groupschedules", groupSchedules);

        Intent intent = new Intent(getApplicationContext(), Calendar_main.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public class ProgressHandler extends Handler {
        public void handleMessage(Message msg) {
            bar.setMax(msg.arg2);
            bar.setProgress(msg.arg1);
            if (bar.getProgress() == bar.getMax()) {
                LoadingState.setText("Done");
                isAllLoaded = true;
            } else {
                LoadingState.setText("Loading...");
            }
        }
    }

    public void getMyDB() {
        String id = null;
        FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();
        if (user1 != null) {
            id = user1.getUid();
            for (UserInfo profile : user1.getProviderData()) {
                userName = profile.getDisplayName();
                userEmail = profile.getEmail();
                userPhoto = profile.getPhotoUrl();
                //user 정보 얻음
            }

        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot document = task.getResult();
                    schedules.clear();
                    for (QueryDocumentSnapshot doc : document) {
                        Schedule sch = doc.toObject(Schedule.class);
                        sch.setID(doc.getId());
                        schedules.add(sch);
                    }

                    loaded++;

                } else {
                    Log.d("Loading", "data get failed: ", task.getException());
                }
            }
        });

        // 속한 그룹 찾기
        db.collection("Group").whereArrayContains("Member", id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        //Log.d("dbtest", document.getId() + " => " + document.getData());
                        joinedGroups.put(document.getId(), document.get("GroupName").toString());

                        loadmax++;

                        getGS(document.getId(), document.get("GroupName").toString());
                    }
                } else {
                    Log.d("Loading", "Joined Group get failed: ", task.getException());
                }
            }
        });
    }

    public void getGS(final String groupId, final String groupName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Group").document(groupId).collection("GroupSchedule")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot document = task.getResult();
                    for (QueryDocumentSnapshot doc : document) {
                        Schedule sch = doc.toObject(Schedule.class);
                        sch.setID(doc.getId());
                        GroupSchedule gsch = new GroupSchedule(groupId, groupName, sch);
                        groupSchedules.add(gsch);
                    }

                    loaded++;

                } else {
                    Log.d("Loading", "data get failed: ", task.getException());
                }
            }
        });
    }
}
