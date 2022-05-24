package com.LEGENO.SaveTheTime;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class Setting extends AppCompatActivity {

    String userName;
    String userEmail;
    String uid = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);



        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            userName = bundle.getString("UN");
            userEmail = bundle.getString("UE");
            Log.d("userProfile",userName+userEmail);
        }

        TextView Nametxt = findViewById(R.id.userID);
        TextView Emailtxt = findViewById(R.id.email);
        Nametxt.setText(userName);
        Emailtxt.setText(userEmail);


        //로그아웃 버튼 이벤트
        Button logoutbtn=findViewById(R.id.logout);
        logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Setting.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //회원탈퇴 버튼 이벤트
        Button withdrawal=findViewById(R.id.withdraw);
        withdrawal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Setting.this, ConfirmWithdrawal.class);
                startActivity(intent);
            }
        });

        //일정 전체삭제
        Button deleteSchedule=findViewById(R.id.deleteAll);
        deleteSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAll();
                Intent intent = new Intent(Setting.this, ConfirmDeleteAllSchedule.class);
                startActivity(intent);
            }
        });
    }
    // 일정 전체 삭
    public void deleteAll(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user  = FirebaseAuth.getInstance().getCurrentUser();

        if(user!= null){
            uid = user.getUid();
        }

        db.collection(uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                deleteDocument(document.getId());
                            }
                        } else {
                        }
                    }
                });

    }

    //deleteAll()에서 찾은 document id 로 다 지
    public void deleteDocument(String docName){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(uid).document(docName)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }



}

