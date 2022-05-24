package com.LEGENO.SaveTheTime;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ConfirmDeleteAllSchedule extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conform_delete_all_shedule);



        //일정 전체삭제 기능 추가해야합니다ㅠ
        Button confirm=findViewById(R.id.confirm2);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"스케줄이 전부 삭제되었습니다.",Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        //취소버튼 누르면 창 종료
        Button cancel=findViewById(R.id.cancel2);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}