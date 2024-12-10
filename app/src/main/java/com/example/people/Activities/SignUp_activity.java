package com.example.people.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.people.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUp_activity extends AppCompatActivity {

    EditText username;
    EditText password;
    EditText confirmPassword;
    EditText email;
    Button signUpButton;
    ImageView profilepicture;

    Boolean imageochoosen=false;


    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference myRef;
    Uri imageUri;

    FirebaseStorage storage;
    StorageReference reference;

    String encodedEmail;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient));
        getSupportActionBar().setTitle("SIGN UP");
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        storage=FirebaseStorage.getInstance();
        reference=storage.getReference();
        myRef=database.getReference();
        username=findViewById(R.id.username);
        profilepicture=findViewById(R.id.profile_photo);
        password=findViewById(R.id.signup_password);
        confirmPassword=findViewById(R.id.confirm_password);
        email=findViewById(R.id.signup_mail);
        signUpButton=findViewById(R.id.button_register);


        profilepicture.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                imageChoice();

            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String email=SignUp_activity.this.email.getText().toString();
                String username=SignUp_activity.this.username.getText().toString();
                String password=SignUp_activity.this.password.getText().toString();
                String confirmPassword=SignUp_activity.this.confirmPassword.getText().toString();
                if(email.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty())
                {
                    Toast.makeText(SignUp_activity.this,"Please fill all the fields",Toast.LENGTH_SHORT).show();
                }
                else if(!password.equals(confirmPassword))
                {
                    Toast.makeText(SignUp_activity.this,"Passwords do not match",Toast.LENGTH_SHORT).show();
                }
                else if(!imageochoosen)
                {
                    Toast.makeText(SignUp_activity.this,"Please choose a profile picture",Toast.LENGTH_SHORT).show();
                }
                else if(password.length()<=4)
                {
                    Toast.makeText(SignUp_activity.this,"Password must be at least 5 characters long",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    signUP(email,username,password);
                    Toast.makeText(SignUp_activity.this,"Account created",Toast.LENGTH_SHORT).show();
                }


            }
        });



    }
    public void signUP(String email,String username,String password)
    {
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>()
        {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                if(task.isSuccessful())
                {
                    encodedEmail=email.replace(".",",");
                    myRef.child("users").child(auth.getUid()).child("username").setValue(username);
                    myRef.child("users").child(auth.getUid()).child("email").setValue(email);

                    if(imageochoosen)
                    {
                        reference.child(encodedEmail).child("image.jpg").putFile(imageUri).
                                addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                            {
                                StorageReference storageRef=FirebaseStorage.getInstance().getReference();
                                StorageReference imagepathref=storageRef.child(encodedEmail).child("image.jpg");
                                imagepathref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {

                                        DatabaseReference database=FirebaseDatabase.getInstance().getReference();
                                        database.child("users").child(auth.getUid()).child("profile_image").setValue(uri.toString());
                                        Toast.makeText(SignUp_activity.this,"Profile Picture Uploaded",Toast.LENGTH_SHORT).show();

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e)
                                    {
                                        Toast.makeText(SignUp_activity.this,"Profile Picture Upload Failed",Toast.LENGTH_SHORT).show();

                                    }
                                });

                            }
                        });
                    }
                    else
                    {
                        myRef.child("users").child(auth.getUid()).child("profile_image").setValue("NULL");
                    }
                    Intent intent=new Intent(SignUp_activity.this,MainActivity.class);
                    intent.putExtra("email",email);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    Toast.makeText(SignUp_activity.this,"Something went wrong",Toast.LENGTH_SHORT).show();

                }

            }
        });

    }

    public void imageChoice()
    {

        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK && data!=null)
        {
            imageUri=data.getData();
            Picasso.get().load(imageUri).into(profilepicture);
            imageochoosen=true;
        }
        else
        {
            imageochoosen=false;
        }
    }
}