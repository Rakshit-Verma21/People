package com.example.people.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.people.Fragments.reset_password;
import com.example.people.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login_page extends AppCompatActivity {

    EditText user_email;
    EditText user_password;
    Button login;
    Button signup;
    TextView forgot_password;

    FirebaseAuth authentication;

    @Override
    protected void onStart()
    {
        if(authentication.getCurrentUser()!=null)
        {
            Intent intent=new Intent(Login_page.this, MainActivity.class);
            startActivity(intent);
        }
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient));
        authentication=FirebaseAuth.getInstance();

        user_email=findViewById(R.id.login_mail);
        user_password=findViewById(R.id.login_password);
        login=findViewById(R.id.login_button);
        signup=findViewById(R.id.signup_button);
        forgot_password=findViewById(R.id.forgot_password);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=user_email.getText().toString();
                String password=user_password.getText().toString();
                if(email.isEmpty()||password.isEmpty())
                {
                    Toast.makeText(Login_page.this,"Please fill all the fields",Toast.LENGTH_SHORT).show();

                }
                else
                {
                    signin(email, password);
                }

            }
        });


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Login_page.this, SignUp_activity.class);
                startActivity(intent);
            }
        });
        forgot_password.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String usermail=user_email.getText().toString();
                Bundle bundle=new Bundle();
                bundle.putString("email",usermail);
                Fragment newfragment=new reset_password();
                newfragment.setArguments(bundle);
                FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.main,newfragment);
                transaction.addToBackStack(null);
                transaction.commit();




            }
        });



    }
    public void signin(String email,String password)
    {
        authentication.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                if(task.isSuccessful())
                {
                    Intent intent=new Intent(Login_page.this,MainActivity.class);
                    Toast.makeText(Login_page.this,"Login Successful",Toast.LENGTH_SHORT).show();
                    startActivity(intent);

                }
                else
                {
                    Toast.makeText(Login_page.this,"Wrong Credentials",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}