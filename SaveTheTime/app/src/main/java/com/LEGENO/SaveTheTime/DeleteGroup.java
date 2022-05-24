package com.LEGENO.SaveTheTime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

public class DeleteGroup extends AppCompatActivity {
    String gId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_delete_group);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        gId = bundle.getString("GID");

        Button ok = findViewById(R.id.bt_ok_del);
        Button no = findViewById(R.id.bt_no_del);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DelGroup();
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void DelGroup(){
        FirebaseFirestore db= FirebaseFirestore.getInstance();

        db.collection("Group").document(gId).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(), "그룹이 성공적으로 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                    setResult(1);
                    finish();
                }
            }
        });
    }
}