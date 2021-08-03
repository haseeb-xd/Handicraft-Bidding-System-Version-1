package com.haseebahmed.handicraftbiddingsystemversion1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

public class ForgetPasswordActivity extends AppCompatActivity implements View.OnClickListener {


    private EditText forgetEmail;
    private CircularProgressButton forgetButton;
    private String email;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        forgetEmail = (EditText) findViewById(R.id.emailForget);
        forgetButton= (CircularProgressButton) findViewById(R.id.buttonForget);
        forgetButton.setOnClickListener(this);

        auth= FirebaseAuth.getInstance();


    }

    @Override
    public void onClick(View view) {


        int source = view.getId();

        if(source==R.id.buttonForget){

            email = forgetEmail.getText().toString();

            if(email.isEmpty())
            {
                forgetEmail.setError("Email is required");
                forgetEmail.requestFocus();
                return;
            }
            
            else
            {
                forgetButton.startAnimation();
                forgetpassword();
            }

        }



    }

    private void forgetpassword() {

        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete( @NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ForgetPasswordActivity.this, "Check your Email", Toast.LENGTH_LONG).show();

                            forgetButton.revertAnimation();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent= new Intent(ForgetPasswordActivity.this,LoginActivity.class);
                                    startActivity(intent);
                                    finish();

                                }
                            }, 500);

                        }
                    }
                });




    }

    public void callBackScreenFromForgetPassword(View view) {
        startActivity(new Intent(this, LoginActivity.class));
        overridePendingTransition(R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

}