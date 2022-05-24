package com.LEGENO.SaveTheTime;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ChangeGroupManager extends AppCompatActivity {
    ListView memberListView;
    ArrayAdapter<String> adapter;

    String gId;
    ArrayList<String> memId;
    ArrayList<String> memName;

    boolean selected = false;
    int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_change_group_manager);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        gId = bundle.getString("GID");
        memId = bundle.getStringArrayList("MI");
        memName = bundle.getStringArrayList("MN");

        memberListView = findViewById(R.id.group_list_view);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, memName);
        memberListView.setAdapter(adapter);

        memberListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selected = true;
                index = position;
            }
        });

        Button yes = findViewById(R.id.bt_cm_ok);
        Button no = findViewById(R.id.bt_cm_no);

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selected) {
                    changeManager(index);
                } else {
                    Toast.makeText(getApplicationContext(), "새로운 관리자를 선택하세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void changeManager(int index) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Group").document(gId).update("Manager", memId.get(index)).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "관리자가 변경되었습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "서버 요류. 잠시후 다시 시도하세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}