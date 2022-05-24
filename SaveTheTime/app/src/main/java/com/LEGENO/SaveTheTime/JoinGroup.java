package com.LEGENO.SaveTheTime;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class JoinGroup extends AppCompatActivity {
    EditText groupID;
    Button createBtn;
    Button joinBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_group);

        groupID = (EditText) findViewById(R.id.joinGroupID);
        createBtn = (Button) findViewById(R.id.createBtn);
        joinBtn = (Button) findViewById(R.id.joinBtn);
        joinBtn.setEnabled(false);

        groupID.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override//20글자 넘어가면 JOIN 버튼 활성화
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                if (text.length() >= 20)
                    joinBtn.setEnabled(true);
                else
                    joinBtn.setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        createBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(JoinGroup.this, CreateGroup.class);
                startActivity(intent);
            }
        });

        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinGroup(groupID.getText().toString());
            }
        });
    }

    /*그룹 조인 메소드*/
    private void joinGroup(final String groupID) {
        final FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();

        if (user1 != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("Group").document(groupID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    // DOCUMENT 받아오기 성공
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();

                        if (document.exists()) {
                            // 문서 받아와서 사용자 추가
                            Map<String, Object> docData = new HashMap<String, Object>();
                            docData.clear();
                            docData.put("GroupName", document.getData().get("GroupName"));
                            docData.put("Manager", document.getData().get("Manager"));

                            ArrayList<String> memList = (ArrayList<String>) document.getData().get("Member");
                            ArrayList<String> memNameList = (ArrayList<String>) document.getData().get("MemberName");

                            if (memList.contains(user1.getUid())) { // 이미 가입된 그룹인지 확인
                                Log.d("JoinGroup", "Already joined this group.");
                                Toast.makeText(getApplicationContext(), "이미 가입된 그룹입니다.", Toast.LENGTH_SHORT).show();

                            } else { // 그룹에 가입
                                memList.add(user1.getUid());
                                memNameList.add(user1.getDisplayName());
                                docData.put("Member", memList);
                                docData.put("MemberName", memNameList);

                                FirebaseFirestore.getInstance().collection("Group").document(groupID).set(docData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("JoinGroup", "DocumentSnapshot successfully written");
                                        Toast.makeText(getApplicationContext(), "그룹 가입 완료!", Toast.LENGTH_SHORT).show();

                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("JoinGroup", "Error writing document", e);
                                        Toast.makeText(getApplicationContext(), "DB 오류", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        } else {// DOCUMENT가 없을 때 (그룹 존재 X)
                            Toast.makeText(getApplicationContext(), "존재하지 않는 그룹입니다.", Toast.LENGTH_SHORT).show();

                        }
                    } else {// DOCUMENT 받아오기 실패
                        Log.d("JoinGroup", "join Group failed.", task.getException());
                        Toast.makeText(getApplicationContext(), "DB 오류", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
