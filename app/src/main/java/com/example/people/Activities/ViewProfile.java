package com.example.people.Activities;

import static android.widget.Toast.makeText;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.car.ui.AlertDialogBuilder;
import com.example.people.Adapter.user_uploadAdapter_ViewProfile;
import com.example.people.Fragments.reset_password;
import com.example.people.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewProfile extends AppCompatActivity {

    CircleImageView profile_image;
    TextView zerouploads;
    Button save;
    EditText name;
    TextView email,verifiedtextview;
    CheckBox verified;

    private String username;
    private String user_email;
    private String user_profile_image;
    private DatabaseReference reference;
    private FirebaseDatabase database;

    private FirebaseAuth auth;
    private FirebaseUser user;
    RecyclerView rv_useruploads;

    FirebaseStorage storage;
    private Uri imageUri;
    private boolean imagechosen=false;

    user_uploadAdapter_ViewProfile adapter;
    ArrayList<String>id_post=new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient));
        getSupportActionBar().setTitle("Profile");
        setContentView(R.layout.activity_view_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        database=FirebaseDatabase.getInstance();
        reference=database.getReference();
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        storage=FirebaseStorage.getInstance();
        profile_image=findViewById(R.id.profile);
        save=findViewById(R.id.save);
        name=findViewById(R.id.user);
        zerouploads=findViewById(R.id.textview_upload);
        zerouploads.setVisibility(View.GONE);
        rv_useruploads=findViewById(R.id.rv_useruploads);
        rv_useruploads.setLayoutManager(new GridLayoutManager(ViewProfile.this,4));
        email=findViewById(R.id.mail);
        getuserinfo();
        getpost_id();
        managechanges();
        verifiedtextview=findViewById(R.id.verifiedtextview);
        verified=findViewById(R.id.checkBox);
        verified.setClickable(false);

        if(auth.getCurrentUser().isEmailVerified())
        {
            verifiedtextview.setText("Email Verified");
            verified.setChecked(true);
        }
        else
        {
            verifiedtextview.setText("Email Not Verified");
            verified.setChecked(false);
        }
        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                imageChoice();

            }
        });
        save.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                updateprofile();
                finish();

            }
        });

    }
    public void updateprofile()
    {
        update_name();
        updateimage();

    }
    public void update_name()
    {
        if(name.getText().toString().isEmpty())
        {
            Toast.makeText(this,"Enter Name",Toast.LENGTH_SHORT).show();
        }
        else
        {
            reference.child("users").child(user.getUid()).child("username").setValue(name.getText().toString());
        }
    }

    public void updateimage()
    {
        if(imagechosen)
        {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference reference = database.getReference();
            reference.child("users").child(auth.getUid()).child("profile_image").setValue(imageUri.toString());
            updateFirebaseStorage();

        }

    }

    public void updateFirebaseStorage()
    {
        String encodedEmail = email.getText().toString().replace(".", ",");
        StorageReference storageReference = storage.getReference();
        StorageReference imagepath = storageReference.child(encodedEmail).child("image.jpg");
        imagepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {
                Toast.makeText(ViewProfile.this,"Image Uploaded",Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                Toast.makeText(ViewProfile.this,"Upload Failed ",Toast.LENGTH_SHORT).show();

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.options_view_profile,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {

        if(item.getItemId()==R.id.reset_password)
        {
            Bundle bundle=new Bundle();
            bundle.putString("email",user_email);
            Fragment newfragment=new reset_password();
            FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
            newfragment.setArguments(bundle);
            transaction.replace(R.id.main,newfragment);
            transaction.addToBackStack(null);
            transaction.commit();

        }
        if(item.getItemId()==R.id.verify)
        {
            FirebaseAuth auth=FirebaseAuth.getInstance();
            FirebaseUser user=auth.getCurrentUser();

            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>()
            {
                @Override
                public void onComplete(@NonNull Task<Void> task)
                {
                    Toast.makeText(ViewProfile.this,"Verification Email Sent",Toast.LENGTH_SHORT).show();
                }
            });
        }
        if(item.getItemId()==R.id.friendlist)
        {
            Intent intent=new Intent(ViewProfile.this,friends.class);
            startActivity(intent);

        }
        if(item.getItemId()==R.id.signout)
        {
            AlertDialog.Builder alert=new AlertDialog.Builder(this);
            alert.setTitle("SIGN OUT");
            alert.setMessage("Are you sure you want to sign out?");
            alert.setPositiveButton("Sign out", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    auth.signOut();
                    Intent intent=new Intent(ViewProfile.this, Login_page.class);
                    startActivity(intent);
                    finish();
                }
            });

            alert.setNegativeButton("Back", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    dialog.cancel();


                }
            });
            AlertDialog dialog=alert.create();
            dialog.show();

        }
        return super.onOptionsItemSelected(item);
    }




    public void getuserinfo()
    {
        reference.child("users").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {


                username= (String) snapshot.child("username").getValue();
                user_email= (String) snapshot.child("email").getValue();
                user_profile_image= (String) snapshot.child("profile_image").getValue();
                if(username==null)
                {
                    makeText(ViewProfile.this, "Unable to fetch username", Toast.LENGTH_SHORT).show();
                }
                else
                {

                    name.setText(username);
                }
                if(user_email==null)
                {
                    makeText(ViewProfile.this, "Unable to fetch Email", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    email.setText(user_email);
                }
                if(user_profile_image==null)
                {
                    profile_image.setImageResource(R.drawable.empty_profie);
                    makeText(ViewProfile.this, "Unable to fetch profile image", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Picasso.get().load(user_profile_image).into(profile_image);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

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
            Picasso.get().load(imageUri).into(profile_image);
            imagechosen=true;
        }
        else
        {
            imagechosen=false;
        }
    }
    public void getpost_id()
    {
        zerouploads.setVisibility(View.GONE);

        reference.child("posts").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                for(DataSnapshot snapshot1:snapshot.getChildren())
                {
                    String id=snapshot1.getKey();
                    while(!id_post.contains(id))
                    {
                        id_post.add(id);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        adapter=new user_uploadAdapter_ViewProfile(zerouploads,id_post,ViewProfile.this,user.getUid());
        rv_useruploads.setAdapter(adapter);


    }
    public void managechanges()
    {
        reference.child("posts").child(user.getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
            {
                String id=snapshot.getKey();
                if(!id_post.contains(id))
                {
                    id_post.add(id);
                    adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot)
            {
                String id=snapshot.getKey();
                id_post.remove(id);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        finish();
    }
}
