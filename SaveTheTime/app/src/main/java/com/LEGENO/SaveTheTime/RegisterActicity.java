package com.LEGENO.SaveTheTime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegisterActicity extends AppCompatActivity {
    private EditText emailJoin;
    private EditText pwdJoin;
    private EditText nameJoin;
    private Button registerBtn;
    private Button cancelBtn;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_acticity);



        emailJoin = (EditText) findViewById(R.id.idText);
        pwdJoin = (EditText) findViewById(R.id.passwordText);
        nameJoin = findViewById(R.id.signUpNameText);
        registerBtn = (Button) findViewById(R.id.registerButton);
        cancelBtn = (Button) findViewById(R.id.cancelButton);

        firebaseAuth = FirebaseAuth.getInstance();

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               signUp();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent canceled = new Intent(RegisterActicity.this, LoginActivity.class);
                startActivity(canceled);
                finish();
            }
        });


    }

    public void signUp(){
        String email = emailJoin.getText().toString();
        String pwd = pwdJoin.getText().toString();

        if (emailJoin.getText().toString().equals("") || pwdJoin.getText().toString().equals("")) {
            Toast.makeText(RegisterActicity.this, "ID 또는 PW가 입력되지 않았습니다.", Toast.LENGTH_SHORT).show();
        }else {
            firebaseAuth.createUserWithEmailAndPassword(email, pwd)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(nameJoin.getText().toString())
                                        .build();
                                user.updateProfile(profileUpdates)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                }
                                            }
                                        });
                                Intent intent = new Intent(RegisterActicity.this, LoginActivity.class);
                                Toast.makeText(RegisterActicity.this, "Success", Toast.LENGTH_SHORT).show();
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(RegisterActicity.this, "등록 에러", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    });
        }
    }
    
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
    }
}
