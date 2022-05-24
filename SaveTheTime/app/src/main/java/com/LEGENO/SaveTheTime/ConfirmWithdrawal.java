package com.LEGENO.SaveTheTime;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ConfirmWithdrawal extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirm_withdrawal);


        //회원탈퇴 기능 추가해야해요ㅠ
        Button confirm=findViewById(R.id.confirm1);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"회원탈퇴가 성공적으로 됐습니다.",Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        //취소버튼 누르면 창 종료
        Button cancel=findViewById(R.id.cancel1);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
