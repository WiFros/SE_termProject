package com.LEGENO.SaveTheTime;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CreateGroup extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_create_group);

        final EditText gr_name = (EditText) findViewById(R.id.gr_name);
       // final EditText gr_number = (EditText) findViewById(R.id.gr_number); 그룹 인원 수

        //확인버튼 이벤트
        Button button_ok = (Button) findViewById(R.id.bt_ok);
        button_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //데이터 전달하고 액티비티 닫기
                String name = gr_name.getText().toString();
            //    String number = gr_number.getText().toString(); 그룹 인원 수

                if (name != "") {
                    createGroup(name);
                } else {
                    Toast.makeText(getApplicationContext(), "이름을 입력하세요", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button button_no = (Button) findViewById(R.id.bt_no);
        button_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });
    }

    //바깥영역 클릭 방지와 백 버튼 차단
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
            return false;
        }
        return true;
    }

    // 그룹 생성
    private void createGroup(String groupName) {
        FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();

        if (user1 != null) {
            Map<String, Object> docData = new HashMap<>();
            docData.clear();

            ArrayList<String> memberIdList = new ArrayList<>();
            ArrayList<String> memberNameList = new ArrayList<>();
            memberIdList.add(user1.getUid());
            memberNameList.add(user1.getDisplayName());

            docData.put("GroupName", groupName);
            docData.put("Manager", user1.getUid());
            docData.put("Member", memberIdList);
            docData.put("MemberName", memberNameList);

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("Group").add(docData).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Log.d("CreateGroup", "DocumentSnapshot written with ID: " + documentReference.getId());
                    Toast.makeText(getApplicationContext(), "그룹이 성공적으로 생성 되었습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w("CreateGroup", "Error adding document", e);
                    Toast.makeText(getApplicationContext(), "DB 에러, 잠시후 다시 시도하세요.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}

