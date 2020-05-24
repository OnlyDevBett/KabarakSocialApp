package com.kabarakuniversityforumApp.AccountCreation;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.kabarakuniversityforumApp.R;

import java.util.Objects;

public class AccountCreation extends AppCompatActivity {
    private Button createAccountbtn;
    private EditText mEmail, InputPassword1, InputPassword2;
    private ProgressDialog loadingBar;
    private FirebaseAuth auth;
    private ProgressDialog progressDialog;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_creation);

        auth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressBar = (ProgressBar) findViewById(R.id.progressBarMethod);

        createAccountbtn = (Button) findViewById(R.id.register_button);
        mEmail= (EditText) findViewById(R.id.email_address);
        InputPassword1 = (EditText) findViewById(R.id.password_input1);
        InputPassword2 = (EditText) findViewById(R.id.password_input2);
        loadingBar = new ProgressDialog(this);

        createAccountbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAccount();
            }
        });
    }

    private void CreateAccount()

    {
        String email = mEmail.getText().toString().trim();
        String password1 = InputPassword1.getText().toString().trim();
        String password2 = InputPassword2.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            mEmail.setError("Enter email address");
            return;

        }
        if (TextUtils.isEmpty(password1))
        {
            InputPassword1.setError("Enter password");
            return;
        }

         if (password1.length() < 8)
        {
            InputPassword1.setError("Password too short");
            return;
        }

        if (TextUtils.isEmpty(password2))
        {
            InputPassword2.setError("Re-Enter password");
            return;
        }
        if (!password1.equals(password2))
        {
            Toast.makeText(this, "Passwords don't match", Toast.LENGTH_SHORT).show();
            return;
        }

        loadingBar.setTitle("Registering");
        loadingBar.setMessage("Please wait...");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();
            auth.createUserWithEmailAndPassword(email, password1)
                    .addOnCompleteListener(AccountCreation.this, new OnCompleteListener<AuthResult>() {
                        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if(task.isSuccessful())
                            {
                                loadingBar.dismiss();
                                Toast.makeText(AccountCreation.this, "Account Created", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(AccountCreation.this, ProfileCreation.class);
                                startActivity(intent);
                                finish();
                            }
                            else
                            {
                                String errorMessage = Objects.requireNonNull(task.getException()).getMessage();
                                Toast.makeText(getApplicationContext(),errorMessage,Toast.LENGTH_SHORT);
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    });
        }

}
