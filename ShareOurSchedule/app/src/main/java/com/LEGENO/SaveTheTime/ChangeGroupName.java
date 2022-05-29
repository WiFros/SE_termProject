package com.LEGENO.SaveTheTime;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ChangeGroupName extends AppCompatActivity {

    String gId = "";
    EditText gr_name;
    Intent intent;
    TextView groupName;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_change_group);
        gr_name = (EditText) findViewById(R.id.gr_name);
        groupName = (TextView) findViewById(R.id.groupName);
        intent = getIntent();
        Bundle bundle = intent.getExtras();

        assert bundle != null;
        gId = bundle.getString("GID");


        //확인버튼 이벤트
        Button button_ok = (Button) findViewById(R.id.bt_ok);
        button_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //데이터 전달하고 액티비티 닫기
                changeGroupName(gr_name.getText().toString());
                Bundle bundle1 = new Bundle();
                bundle1.putString("GN",gr_name.getText().toString());
                Intent intent1 = new Intent();
                intent1.putExtras(bundle1);
                setResult(1,intent1);
                //    String number = gr_number.getText().toString(); 그룹 인원 수
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
    //그룹 이름 변경하는 메소드
    public void changeGroupName(String GName){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if (GName != "") {
            db.collection("Group").document(gId).update("GroupName",GName).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "그룹 이름이 변경되었습니다.", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "서버 요류. 잠시후 다시 시도하세요.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "이름을 입력하세요", Toast.LENGTH_SHORT).show();
        }
    }

}
