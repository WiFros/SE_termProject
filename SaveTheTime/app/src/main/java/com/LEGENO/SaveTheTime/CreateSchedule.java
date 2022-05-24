package com.LEGENO.SaveTheTime;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CreateSchedule extends AppCompatActivity {
    private static final String TAG = "tag";
    private static DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    ArrayAdapter groupNameAdap;
    Button getDatestr;
    Button getDateend;
    EditText title;
    EditText content;
    Spinner groupSpinner;
    CheckBox groupCheck;

    String[] groupID;
    String[] groupName;
    boolean isEdit = false;
    String editID;
    String editGID;
    String editGName;

    //날짜, 시간 선택했는지 확인하기 위한 값
    int flag1 = 0;
    int flag2 = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_schedule);

        getDateend = findViewById(R.id.endDayButton);
        getDatestr = findViewById(R.id.startDayButton);

        title = findViewById(R.id.schduleTitle);
        content = findViewById(R.id.schduleContent);

        groupCheck = findViewById(R.id.group_checkbox);
        groupSpinner = findViewById(R.id.group_spinner);

        getDatestr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimeDialog(getDatestr);
            }
        });
        getDateend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimeDialog(getDateend);
            }
        });
        groupCheck.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {
                    groupSpinner.setVisibility(View.VISIBLE);
                } else {
                    groupSpinner.setVisibility(View.INVISIBLE);
                }
            }
        });

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Schedule sch = (Schedule) bundle.getSerializable("selSchedule");
            if (sch != null) { // 수정시
                isEdit = true;
                editID = sch.getID();

                title.setText(sch.getTitle());
                content.setText(sch.getContent());

                getDatestr.setText(sch.getStartTime().substring(2, 16));
                getDateend.setText(sch.getEndTime().substring(2, 16));

                editGID = bundle.getString("gid");
                editGName = bundle.getString("gName");

                if (editGID != null) { //그룹 일정 수정시
                    String[] gn = {editGName};
                    groupNameAdap = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, gn);
                    groupCheck.setChecked(true);
                    groupCheck.setEnabled(false);
                    groupSpinner.setAdapter(groupNameAdap);
                    groupSpinner.setVisibility(View.VISIBLE);
                    //groupSpinner.setClickable(false);
                    groupSpinner.setEnabled(false);
                } else {
                    groupCheck.setEnabled(false);
                }
            } else { // 그냥
                groupID = bundle.getStringArray("groupID");
                groupName = bundle.getStringArray("groupName");

                groupNameAdap = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, groupName);
                groupSpinner.setAdapter(groupNameAdap);
            }
        }
    }

    //DatePickerDialog
    private void showDateTimeDialog(final Button date_time_in) {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy-MM-dd HH:mm");
                        date_time_in.setText(simpleDateFormat.format(calendar.getTime()));

                    }
                };
                new TimePickerDialog(CreateSchedule.this, R.style.my_dialog_theme, timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();

            }
        };
        new DatePickerDialog(CreateSchedule.this, R.style.my_dialog_theme, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    public void mOnPopupClick(View v) {
        Intent intent = new Intent(this, DateSelection.class);
        intent.putExtra("data", "Date select");
        startActivityForResult(intent, 1);
    }

    public void mOnPopupClick2(View v) {
        Intent intent = new Intent(this, DateSelection.class);
        intent.putExtra("data", "Date select");
        startActivityForResult(intent, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                getDatestr = findViewById(R.id.startDayButton);
                String temp = data.getStringExtra("result");
                flag1 = 1;
                getDatestr.setText(temp);
            }
        } else if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                getDateend = findViewById(R.id.endDayButton);
                String temp = data.getStringExtra("result");
                flag1 = 2;
                getDateend.setText(temp);
            }
        }
    }

    public void registerSchedule(View v) {
        //오류 메세지 출력
        if (getDatestr.getText().toString().charAt(0) == 'Y' || getDateend.getText().toString().charAt(0) == 'Y') {
            Toast.makeText(CreateSchedule.this, "일정 시간이 입력되지 않았습니다.", Toast.LENGTH_SHORT).show();
        } else if (content.getText().toString().equals("")) {
            Toast.makeText(CreateSchedule.this, "일정 내용이 입력되지 않았습니다.", Toast.LENGTH_SHORT).show();
        } else if (title.getText().toString().equals("")) {
            Toast.makeText(CreateSchedule.this, "일정 제목이 입력되지 않았습니다.", Toast.LENGTH_SHORT).show();
        } else {

            LocalDateTime start = LocalDateTime.parse("20" + getDatestr.getText().toString(), dateFormat);
            LocalDateTime end = LocalDateTime.parse("20" + getDateend.getText().toString(), dateFormat);

            if (end.isBefore(start)) {
                Toast.makeText(CreateSchedule.this, "일정 시간이 부적절합니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            Schedule schedule = new Schedule(title.getText().toString(), content.getText().toString(), start, end);

            //////////////////////////////////////////////

            if (!groupCheck.isChecked()) { // 개인 일정 추가 수정
                String id = null;
                FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();
                if (user1 != null) {
                    String uid = user1.getUid();
                    id = uid;
                }

                if (id != null) {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    Map<String, Object> user = new HashMap<>();

                    user.put("schedule", schedule);

                    final String finalId = id;
                    if (isEdit) {//수정일 경우
                        db.collection(finalId).document(editID).set(schedule, SetOptions.merge())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "DocumentSnapshot successfully written!");
                                        Toast.makeText(CreateSchedule.this, "일정이 수정되었습니다.", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error writing document", e);
                                    }
                                });
                    } else {// 그냥 추가일 경우
                        db.collection(finalId).document()
                                .set(schedule, SetOptions.merge())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "DocumentSnapshot successfully written!");
                                        Toast.makeText(CreateSchedule.this, "일정이 등록되었습니다.", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error writing document", e);
                                    }
                                });
                    }
                } else {
                    Toast.makeText(this, "Fail", Toast.LENGTH_SHORT).show();
                }


            } else { // 그룹 일정 추가 수정
                int groupIDIndex = groupSpinner.getSelectedItemPosition();

                String id = null;
                FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();
                if (user1 != null) {
                    String uid = user1.getUid();
                    id = uid;
                }

                if (id != null) {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    Map<String, Object> user = new HashMap<>();

                    user.put("schedule", schedule);

                    final String finalId = id;
                    if (isEdit) {//수정일 경우
                        db.collection("Group").document(editGID)
                                .collection("GroupSchedule").document(editID).set(schedule, SetOptions.merge())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "DocumentSnapshot successfully written!");
                                        Toast.makeText(CreateSchedule.this, "일정이 수정되었습니다.", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error writing document", e);
                                    }
                                });
                    } else {// 그냥 추가일 경우
                        db.collection("Group").document(groupID[groupIDIndex])
                                .collection("GroupSchedule").document()
                                .set(schedule, SetOptions.merge())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "DocumentSnapshot successfully written!");
                                        Toast.makeText(CreateSchedule.this, "일정이 등록되었습니다.", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error writing document", e);
                                    }
                                });
                    }
                } else {
                    Toast.makeText(this, "Fail", Toast.LENGTH_SHORT).show();
                }
            }

            //////////////////////////////////////////////
        }
    }
}
