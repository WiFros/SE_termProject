package com.LEGENO.SaveTheTime;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class Calendar_main extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    /*DB 관련*/
    ArrayList<Schedule> schedules = new ArrayList<>();//디비에서 불러온 스케줄들 다 여기 있습니다.
    ArrayList<GroupSchedule> groupSchedules = new ArrayList<>();// 그룹 스케쥴
    Map<String, String> joinedGroups = new HashMap<>();// 가입된 그룹 / key: id, value: name
    Map<String, ListenerRegistration> groupSCHListeners = new HashMap<>();

    /*Activity에서 가져오는 것들*/
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle actionBarDrawerToggle;
    TextView botSheetDate;
    MaterialCalendarView calenderView = null;
    ListView groupList;

    /*내부 변수*/
    CalendarDay selectedDay;
    EventDecorator eventDecorator;
    EventDecorator groupEventDecorator;
    String userName;
    String userEmail;
    Uri userPhoto;

    menuListviewAdapter Gdapter;
    ArrayList<String> gList;
    ArrayList<String> Gid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_main);

        final Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            schedules = (ArrayList<Schedule>) bundle.getSerializable("schedules");
            joinedGroups = ((ArrayList<Map<String, String>>) bundle.getSerializable("groups")).get(0);
            groupSchedules = (ArrayList<GroupSchedule>) bundle.getSerializable("groupschedules");
            userName = bundle.getString("Name");
            userEmail = bundle.getString("Email");
            userPhoto = bundle.getParcelable("Photo");
        }
        // 오늘 설정
        selectedDay = CalendarDay.today();

        //drawble 메뉴
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(" ");
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.navigationView);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        groupList = findViewById(R.id.groupList);

        botSheetDate = findViewById(R.id.botsheetDate);

        //drawer menu listview에 정보 표기
        groupList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Calendar_main.this, ManageGroup.class);
                Bundle bundle1 = new Bundle();
                bundle1.putString("GroupName", gList.get(position));
                bundle1.putString("GroupId", Gid.get(position));
                intent.putExtras(bundle1);
                startActivity(intent);
            }
        });


        //drawer_header에 유저 정보 입력하는 코드 99~107
        View headerView = navigationView.getHeaderView(0);
        TextView Nametxt = headerView.findViewById(R.id.userIDText);
        TextView Emailtxt = headerView.findViewById(R.id.emailText);

        Nametxt.setText(userName);
        Emailtxt.setText(userEmail);
//        new DownloadFilesTask().execute(userPhoto.toString());

        ImageView settingBtn = headerView.findViewById(R.id.setting_button);
        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("userProfile", "Main\n" + userName + userEmail);
                Bundle bundle2 = new Bundle();
                if (userName != null && userEmail != null) {
                    bundle2.putString("UN", userName);
                    bundle2.putString("UE", userEmail);
                    bundle2.putString("UP", userPhoto.toString());
                }

                Intent intent = new Intent(Calendar_main.this, Setting.class);
                intent.putExtras(bundle2);
                startActivity(intent);

            }
        });


        /*                 캘린더 설정                */
        calenderView = findViewById(R.id.calendarView);
        // calender set up
        calenderView.setTopbarVisible(true);

        Log.d("dbtest", "calendar" + groupSchedules.toString());

        calenderView.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setMinimumDate(CalendarDay.from(2017, 0, 1))
                .setMaximumDate(CalendarDay.from(2030, 11, 31))
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();
        calenderView.setShowOtherDates(MaterialCalendarView.SHOW_OUT_OF_RANGE);
        calenderView.setDynamicHeightEnabled(true);
        calenderView.addDecorators(
                new SundayDecorator(),
                new SaturdayDecorator(),
                groupEventDecorator = new EventDecorator(this, groupSchedules, true),
                eventDecorator = new EventDecorator(this, schedules, false)
        );

        Log.d("dbtest", "calendar end" + groupSchedules.toString());


        calenderView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                updateBotSheet(date);
                selectedDay = date;
            }
        });

        //스케쥴 추가 버튼
        Button btnAdd = findViewById(R.id.btnAddSchedule);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Calendar_main.this, CreateSchedule.class);
                Bundle bundle = new Bundle();

                bundle.putStringArray("groupID", joinedGroups.keySet().toArray(new String[joinedGroups.size()]));
                bundle.putStringArray("groupName", joinedGroups.values().toArray(new String[joinedGroups.size()]));

                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        updateSchedules();
    }


    @Override
    protected void onResume() {
        super.onResume();

        calenderView.removeDecorator(eventDecorator);
        eventDecorator = new EventDecorator(this, schedules, false);
        calenderView.addDecorators(eventDecorator);

        calenderView.removeDecorator(groupEventDecorator);
        groupEventDecorator = new EventDecorator(this, groupSchedules, true);
        calenderView.addDecorators(groupEventDecorator);

        updateBotSheet(selectedDay);
        calenderView.setDateSelected(selectedDay, true);
    }


    //뒤로 가기 버튼 누르면 메뉴 들어감
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // 바텀 시트 업데이트
    public void updateBotSheet(@NonNull CalendarDay date) {
        //날짜 변경
        botSheetDate.setText(String.format("%s월 %s일", String.valueOf(date.getMonth() + 1), String.valueOf(date.getDay())));

        LocalDate d = LocalDate.of(date.getYear(), date.getMonth() + 1, date.getDay());

        final ArrayList<ItemData> selectedSchedule = getSelectedSchedule(d);
        //내용 변경
        ListAdapter oAdapter = new ListAdapter(selectedSchedule);
        ListView list = findViewById(R.id.scheduleList);
        list.setAdapter(oAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (schedules.contains(new Schedule(selectedSchedule.get(position).ID))) {
                    showPopUp(schedules.get(schedules.indexOf(new Schedule(selectedSchedule.get(position).ID))));

                } else if (groupSchedules.contains(new Schedule(selectedSchedule.get(position).ID))) {
                    showPopUp(groupSchedules.get(groupSchedules.indexOf(new Schedule(selectedSchedule.get(position).ID))));
                }
            }
        });
    }

    public ArrayList<ItemData> getSelectedSchedule(LocalDate d) {
        ArrayList<ItemData> list = new ArrayList<>();

        for (Schedule sch : schedules) {
            if (sch.containsDate(d)) {
                ItemData item = new ItemData();
                item.ID = sch.getID();
                item.Title = sch.getTitle();
                item.Time = sch.getStartTime() + " ~ " + sch.getEndTime();
                item.Content = sch.getContent();

                list.add(item);
            }
        }

        for (Schedule sch : groupSchedules) {
            if (sch.containsDate(d)) {
                ItemData item = new ItemData();
                item.ID = sch.getID();
                item.Title = sch.getTitle();
                item.Time = sch.getStartTime() + " ~ " + sch.getEndTime();
                item.Content = sch.getContent();

                list.add(item);
            }
        }

        return list;
    }

    // 디비 값 변동 생길시 자동 업데이트 (근데 너무 자주함...)
    public void updateSchedules() {
        String id = null;
        FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();
        if (user1 != null) {
            id = user1.getUid();
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(id).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.d("Update Schedules", "Listen failed.", e);
                    return;
                }
                if (queryDocumentSnapshots != null) {
                    schedules.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Schedule sch = doc.toObject(Schedule.class);
                        sch.setID(doc.getId());
                        schedules.add(sch);
                        //Log.d("dbtest", doc.getId() + "    Main");
                    }

                }
            }
        });

        db.collection("Group").whereArrayContains("Member", id).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.d("Update Schedules", "Listen failed.", e);
                }
                for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                    switch (dc.getType()) {
                        case ADDED:
                            joinedGroups.put(dc.getDocument().getId(), dc.getDocument().get("GroupName").toString());
                            updateGroupSchedule(dc.getDocument().getId(), dc.getDocument().get("GroupName").toString());
                            Log.d("group", String.valueOf(joinedGroups.values()));
                            setMenuList();
                            break;

                        case MODIFIED:
                            if (!joinedGroups.get(dc.getDocument().getId()).equals(dc.getDocument().get("GroupName").toString())) {
                                joinedGroups.put(dc.getDocument().getId(), dc.getDocument().get("GroupName").toString());
                                groupSCHListeners.get(dc.getDocument().getId()).remove();
                                groupSCHListeners.remove(dc.getDocument().getId());

                                groupSchedules.removeIf(s -> s.groupEquals(new GroupSchedule(dc.getDocument().getId())));

                                updateGroupSchedule(dc.getDocument().getId(), dc.getDocument().get("GroupName").toString());
                                setMenuList();
                            }
                            break;

                        case REMOVED:
                            joinedGroups.remove(dc.getDocument().getId());
                            groupSCHListeners.get(dc.getDocument().getId()).remove();
                            groupSCHListeners.remove(dc.getDocument().getId());

                            groupSchedules.removeIf(s -> s.groupEquals(new GroupSchedule(dc.getDocument().getId())));
                            setMenuList();
                            break;
                    }
                }
            }
        });
    }

    boolean first = true;

    public void updateGroupSchedule(final String Gid, final String GName) {
        if (first) {
            first = false;
            Log.d("dbtest", "first");
            groupSchedules.clear();
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        ListenerRegistration listenerRegistration = db.collection("Group").document(Gid).collection("GroupSchedule")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.d("Update Group Schedules", "Listen failed.", e);
                        }

                        for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:
                                    Schedule Asch = dc.getDocument().toObject(Schedule.class);
                                    Asch.setID(dc.getDocument().getId());
                                    GroupSchedule Agsch = new GroupSchedule(Gid, GName, Asch);
                                    groupSchedules.add(Agsch);
                                    break;
                                case MODIFIED:
                                    Schedule Msch = dc.getDocument().toObject(Schedule.class);
                                    Msch.setID(dc.getDocument().getId());
                                    GroupSchedule Mgsch = new GroupSchedule(Gid, GName, Msch);
                                    groupSchedules.set(groupSchedules.indexOf(Mgsch), Mgsch);
                                    break;
                                case REMOVED:
                                    Schedule Dsch = dc.getDocument().toObject(Schedule.class);
                                    Dsch.setID(dc.getDocument().getId());
                                    groupSchedules.remove(new GroupSchedule(Gid, GName, Dsch));
                                    break;
                            }
                        }
                    }
                });
        groupSCHListeners.put(Gid, listenerRegistration);
    }

    public void showPopUp(Schedule schedule) {
        Intent popUp = new Intent(this, SchedulePopUp.class);

        //GroupSchedule groupSchedule = new GroupSchedule("asdf", "그룹입니당", schedule);
        popUp.putExtra("schedule", schedule);
        startActivity(popUp);
    }

    //drawble 메뉴 리스너
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.group1) {
        } else if (id == R.id.group2) {
            Toast.makeText(getApplicationContext(), "그룹2 화면으로 전환", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.add_group) {
            Intent intent = new Intent(Calendar_main.this, JoinGroup.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // 그룹 추가 버튼
    public void addGroup(View view) {
        Intent intent = new Intent(Calendar_main.this, JoinGroup.class);
        startActivity(intent);
    }

    public void setMenuList() {
        Gid = new ArrayList<String>(joinedGroups.keySet());
        gList = new ArrayList<String>(joinedGroups.values());
        Gdapter = new menuListviewAdapter(getApplicationContext(), gList);
        groupList.setAdapter(Gdapter);

    }

    // 요 아래 전부다 프로필 사진 가져오는 거입니다.
    private class DownloadFilesTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... strings) {
            Bitmap bmp = null;
            try {
                String img_url = strings[0]; //url of the image
                URL url = new URL(img_url);
                bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bmp;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected void onPostExecute(Bitmap result) {
            // doInBackground 에서 받아온 total 값 사용 장소
            ImageView Imageuri = findViewById(R.id.imageView2);
            Imageuri.setImageBitmap(result);
        }
    }
}
