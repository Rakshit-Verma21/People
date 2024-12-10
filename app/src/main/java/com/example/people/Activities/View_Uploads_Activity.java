package com.example.people.Activities;

import android.app.DownloadManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.people.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class View_Uploads_Activity extends AppCompatActivity {
    ImageView imageView;
    VideoView videoView;
    String type;
    String uri;
    String user_id;
    String post_id;
    String encoded_email;
    String storage_name;
    ProgressBar bar;
    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_uploads);
        Intent intent = getIntent();
        uri = intent.getStringExtra("uri");
        type = intent.getStringExtra("type");
        user_id = intent.getStringExtra("user_id");
        post_id = intent.getStringExtra("post_id");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.black_gradient));
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) ->
        {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        imageView = findViewById(R.id.imageView_upload_view);
        videoView = findViewById(R.id.videoView2);
        bar = findViewById(R.id.progressBar2);



        bar.setVisibility(View.GONE);

        set_view();
    }

    public void set_view()
    {
        if (type.isEmpty() || uri.isEmpty()) {
            imageView.setVisibility(View.GONE);
            videoView.setVisibility(View.GONE);
            Toast.makeText(View_Uploads_Activity.this, "Unable to Fetch Data", Toast.LENGTH_SHORT).show();
        } else {
            if (type.equals("image")) {
                imageView.setVisibility(View.VISIBLE);
                videoView.setVisibility(View.GONE);
                Picasso.get().load(uri).into(imageView);
            } else if (type.equals("video")) {
                imageView.setVisibility(View.GONE);
                videoView.setVisibility(View.VISIBLE);
                videoView.setVideoURI(Uri.parse(uri));
                videoView.start();
            }
        }
        get_post();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.view_uploads_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        else if (item.getItemId() == R.id.download)
        {
            download();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void get_post()
    {
        if (!user_id.isEmpty()) {
            try {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                reference.child("users").child(user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String email = snapshot.child("email").getValue(String.class);
                        encoded_email = Objects.requireNonNull(email).replace(".", ",");

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error)
                    {
                    }
                });
                reference.child("posts").child(user_id).child(post_id).addValueEventListener(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot)
                    {
                        storage_name=snapshot.child("storage_name").getValue(String.class);
                        type=snapshot.child("type").getValue(String.class);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }

    }
    public void download()
    {
        String filename=storage_name.replace(":","_")+".mp4";
        if(type.equals("image"))
        {
             filename=storage_name.replace(":","_")+"png";
        }

        bar.setVisibility(View.VISIBLE);
        StorageReference storageReference;
        storageReference=FirebaseStorage.getInstance().getReferenceFromUrl(uri);
        File localfile=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),filename);
        storageReference.getFile(localfile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot)
            {
                Toast.makeText(View_Uploads_Activity.this, "Download Complete", Toast.LENGTH_SHORT).show();
            }
        });

    }


}