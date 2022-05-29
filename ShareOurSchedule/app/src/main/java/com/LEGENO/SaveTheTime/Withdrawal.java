package com.LEGENO.SaveTheTime;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
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

public class Withdrawal extends AppCompatActivity {
    String gId;
    String manangerId;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        manangerId = bundle.getString("M");
        gId = bundle.getString("GID");

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_withdrawal);

        TextView name = (TextView) findViewById(R.id.name);
        name.setText(bundle.getString("GN"));

        //그룹 이름 불러오기
        name.setText("모바일");

        Button button_ok = (Button) findViewById(R.id.bt_ok);
        button_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                withdrawGroup();
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
            return false;
        }
        return true;
    }

    private void withdrawGroup() {
        final FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();

        if (user1 == null)
            return;

        if (user1.getUid().equals(manangerId)) {
            Toast.makeText(getApplicationContext(), "관리자는 관리 권한을 이전하고 시도하세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Group").document(gId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    Map<String, Object> docData = new HashMap<String, Object>();
                    docData.clear();
                    docData.put("GroupName", document.getData().get("GroupName"));
                    docData.put("Manager", document.getData().get("Manager"));

                    ArrayList<String> memList = (ArrayList<String>) document.getData().get("Member");
                    ArrayList<String> memNameList = (ArrayList<String>) document.getData().get("MemberName");

                    int index = memList.indexOf(user1.getUid());

                    memList.remove(index);
                    memNameList.remove(index);

                    docData.put("Member", memList);
                    docData.put("MemberName", memNameList);

                    FirebaseFirestore.getInstance().collection("Group").document(gId).set(docData).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("dbtest", "DocumentSnapshot successfully written");
                            Toast.makeText(getApplicationContext(), "그룹에서 탈퇴하였습니다.", Toast.LENGTH_SHORT).show();

                            setResult(1);
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("dbtest", "Error writing document", e);
                            Toast.makeText(getApplicationContext(), "DB 오류", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Log.e("dbtest", "Get document error.", task.getException());
                }
            }
        });
    }
}

