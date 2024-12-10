package com.example.people.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.people.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

public class Post_Creator extends AppCompatActivity
{
    ImageView imageView;
    VideoView videoView;

    ImageButton play;
    ProgressBar progressBar;
    String type;
    String file_name;
    ImageButton select;
    Uri Path;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    FirebaseUser user;
    TextView uploading;
    Uri file_path;
    EditText title;
    ImageButton upload_confirm;
    int count = 0;



    ImageButton CreatePost;
    ImageButton discard;
    ImageView post_background;
    EditText text_post;
    Uri imageUri;
    ImageView original;
    TextView upload_text;
    boolean isuploadcomplete=true;

    int post_type=1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_post_creator);
        getSupportActionBar().setTitle("Create Your Post");
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        user = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
        imageView = findViewById(R.id.imageView3);
        videoView = findViewById(R.id.videoView);
        videoView.setVisibility(View.GONE);
        imageView.setVisibility(View.GONE);
        play = findViewById(R.id.button_videopause);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        uploading = findViewById(R.id.textview_uploading);
        upload_confirm=findViewById(R.id.imageButton);
        uploading.setVisibility(View.GONE);
        play.setVisibility(View.GONE);
        select = findViewById(R.id.select);
        title = findViewById(R.id.upload_title);
        upload_confirm.setVisibility(View.GONE);
        original=findViewById(R.id.imageView4);
        discard=findViewById(R.id.discard);
        discard.setVisibility(View.GONE);
        post_background=findViewById(R.id.post_background);
        text_post=findViewById(R.id.post_edittext);
        CreatePost=findViewById(R.id.create_post);
        upload_text=findViewById(R.id.uplod_text);
        upload_text.setVisibility(View.GONE);
        CreatePost.setVisibility(View.GONE);
        discard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                AlertDialog.Builder discardAlert=new AlertDialog.Builder(Post_Creator.this);
                discardAlert.setTitle("Discard Post").setMessage("Do You Wish To Discard Your Post").setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        discard.setVisibility(View.GONE);
                        text_post.setText("");
                        imageUri=null;
                        post_background.setImageResource(R.drawable.empty_profie);
                        original.setImageResource(R.drawable.empty_profie);
                        CreatePost.setVisibility(View.GONE);
                        upload_text.setVisibility(View.GONE);
                    }
                }).setNegativeButton("Back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                }).show();
            }
        });
        CreatePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                isuploadcomplete=false;
                create_post();

            }
        });
        upload_confirm.setOnClickListener(new View.OnClickListener()
        {

            @Override public void onClick(View v)
            {
                isuploadcomplete=false;
                post();
            }
        });
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
        {
            @Override
            public void onCompletion(MediaPlayer mp) {
                count = 0;
                videoView.seekTo(0);
                play.setVisibility(View.VISIBLE);
            }
        });
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadselector();
                videoView.setVisibility(View.GONE);
                imageView.setVisibility(View.GONE);
                play.setVisibility(View.GONE);
            }
        });
        videoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (count % 2 == 0) {
                    videoView.start();
                    play.setVisibility(View.GONE);
                } else {
                    play.setVisibility(View.VISIBLE);
                    videoView.pause();

                }
                count++;

            }
        });
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (count % 2 == 0) {
                    videoView.start();
                    play.setVisibility(View.GONE);
                } else {
                    videoView.pause();
                }
                count++;
            }
        });
        post_background.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                select_image();

            }
        });
        text_post.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                Bitmap bitmap= ((BitmapDrawable)original.getDrawable()).getBitmap();
                Bitmap mbitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                if(s.length()>0)
                {
                    create_image(s.toString(),new BitmapDrawable(getResources(),mbitmap).getBitmap());
                }
                if(text_post.getText().toString().isEmpty())
                {
                    post_background.setImageBitmap(mbitmap);
                }
            }
            @Override
            public void afterTextChanged(Editable s)
            {

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        if (item.getItemId()==android.R.id.home)
        {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);

    }

    public void create_post()
    {
        if (text_post.getText().toString().isEmpty()||imageUri==null)
        {
            Toast.makeText(getApplicationContext(),"Please Select an Image and Enter Desired text",Toast.LENGTH_SHORT).show();
        }
        else
        {
            AlertDialog.Builder alert2=new AlertDialog.Builder(getApplicationContext());
            alert2.setMessage("Do You Wish To Proceed").setTitle("Confirm Your Post").setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    Bitmap bitmap=((BitmapDrawable)post_background.getDrawable()).getBitmap();
                    save_createdpost(bitmap);
                }
            }).setNegativeButton("Back", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    dialog.dismiss();

                }
            }).show();
        }
    }
    public void create_image(String text,Bitmap bitmap)
    {

        if(!text.isEmpty())
        {
            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            paint.setColor(Color.WHITE);
            paint.setTextSize(20);
            paint.setTextAlign(Paint.Align.LEFT);
            Rect textbounds=new Rect();
            paint.getTextBounds(text,0,text.length(),textbounds);
            int x=(bitmap.getWidth()-textbounds.width())/2;
            int y=(bitmap.getHeight()+textbounds.height())/2;
            canvas.drawBitmap(bitmap, 0, 0, null);
            canvas.drawText(text, x, y, paint);
            post_background.setImageBitmap(bitmap);
        }
        else
        {

        }

    }
    public void save_createdpost(Bitmap bitmap)
    {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String randomFileName = "People_Post" + timeStamp + "_" + new Random().nextInt(1000) + ".png";

        // File localfile=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
        File File=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),randomFileName);
        try
        {
            FileOutputStream fileOutputStream=new FileOutputStream(File);
            bitmap.compress(Bitmap.CompressFormat.PNG,100,fileOutputStream);
            Toast.makeText(getApplicationContext(),"File Saved",Toast.LENGTH_SHORT).show();
            file_name=randomFileName;
            Path=Uri.fromFile(File);
            type="image";
            upload_to_cloud();
            fileOutputStream.flush();
            fileOutputStream.close();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }




    public void select_image()
    {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 2);
    }
    public void uploadselector() {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*"); // Set the MIME type to all files
        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/*", "video/*"}); // Specify image and video MIME types

        startActivityForResult(Intent.createChooser(intent, "Select Video"), 1);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null)
        {
            Toast.makeText(getApplicationContext(), "No File Selected", Toast.LENGTH_SHORT).show();
        }
        if(requestCode==2&&resultCode==RESULT_OK&&data!=null)
        {

            post_type=2;
            imageUri = data.getData();
            try
            {
                Picasso.get().load(imageUri).into(post_background);
                Picasso.get().load(imageUri).into(original);
                CreatePost.setVisibility(View.VISIBLE);
                upload_text.setVisibility(View.VISIBLE);
                discard.setVisibility(View.VISIBLE);

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

        }
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            try {
                post_type=1;
                Path = data.getData();
                file_path = data.getData();
                upload_confirm.setVisibility(View.VISIBLE);
                String mimeType = getApplicationContext().getContentResolver().getType(Path);
                if (mimeType != null) {
                    if (mimeType.startsWith("image/")) {
                        imageView.setVisibility(View.VISIBLE);
                        videoView.setVisibility(View.GONE);
                        Picasso.get().load(Path).into(imageView);
                        type = "image";
                        file_name = Path.getLastPathSegment().replace(":", "_");
                        play.setVisibility(View.GONE);
                        count = 0;
                        // Handle image
                        // Display the selected image
                    } else if (mimeType.startsWith("video/")) {
                        imageView.setVisibility(View.GONE);
                        videoView.setVisibility(View.VISIBLE);
                        videoView.setVideoURI(Path);
                        videoView.seekTo(1);
                        type = "video";
                        file_name = Path.getLastPathSegment().replace(":", "_");
                        play.setVisibility(View.VISIBLE);
                        count = 0;

                        if (videoView.getDuration() == videoView.getCurrentPosition()) {
                            count = 0;
                        }
                    }


                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void post() {
        if (Path == null)
        {
            Toast.makeText(getApplicationContext(), "Please Select File", Toast.LENGTH_SHORT).show();

        }
        else if (title.getText().toString().isEmpty())
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(Post_Creator.this);
            builder.setTitle("Alert");
            builder.setMessage("Your Post Title is Empty! Do you want to continue? ");
            builder.setPositiveButton("POST", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    upload_to_cloud();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();
        }
        else
        {
            upload_to_cloud();
        }
    }
    public void upload_to_cloud()
    {
        if(post_type==1)
        {
            upload_confirm.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            uploading.setVisibility(View.VISIBLE);
            uploading.setText("Uploading Your Post");
        }
        else
        {

        }
        String encoded_email = Objects.requireNonNull(user.getEmail()).replace(".", ",");
        storageReference = FirebaseStorage.getInstance().getReference();
        UploadTask uploadTask;
        if (type.equals("image"))
        {
            uploadTask = storageReference.child(encoded_email).child("uploads").child(file_name).putFile(Path);
        }
        else
        {
            uploadTask = storageReference.child(encoded_email).child("uploads").child(file_name).putFile(file_path);

        }
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                int progress = (int) (100 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                progressBar.setProgress(progress);

            }
        }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Upload Complete", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    uploading.setVisibility(View.GONE);
                    storageReference.child(encoded_email).child("uploads").child(file_name).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String downloadURL = uri.toString();
                            upload_to_database(downloadURL);
                        }
                    });
                }

            }
        }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                Toast.makeText(getApplicationContext(), "Upload Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void upload_to_database(String downloadURL) {
        String upload_title = title.getText().toString();
        {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            String post_id = reference.child("posts").child(user.getUid()).push().getKey();
            if (upload_title.isEmpty())
            {
                reference.child("posts").child(user.getUid()).child(post_id).child("title").setValue("");
                reference.child("post_data").child(post_id).child("title").setValue("");
            } else {
                reference.child("posts").child(user.getUid()).child(post_id).child("title").setValue(upload_title);
                reference.child("post_data").child(post_id).child("title").setValue(upload_title);
            }
            if (type.equals("image")) {
                reference.child("posts").child(user.getUid()).child(post_id).child("type").setValue("image");
                reference.child("post_data").child(post_id).child("type").setValue("image");
            } else {
                reference.child("posts").child(user.getUid()).child(post_id).child("type").setValue("video");
                reference.child("post_data").child(post_id).child("type").setValue("video");
            }
            reference.child("posts").child(user.getUid()).child(post_id).child("storage_name").setValue(file_name);
            reference.child("post_data").child(post_id).child("storage_name").setValue("file_name");
            reference.child("posts").child(user.getUid()).child(post_id).child("user_id").setValue(user.getUid());
            reference.child("post_data").child(post_id).child("user_id").setValue(user.getUid());
            reference.child("post_data").child(post_id).child("file").setValue(downloadURL);
            reference.child("posts").child(user.getUid()).child(post_id).child("file").setValue(downloadURL).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful())
                    {
                        uploading.setText("Upload Complete");
                        Toast.makeText(getApplicationContext(), "Post Successful", Toast.LENGTH_SHORT).show();
                        isuploadcomplete=true;
                    }
                    new Handler().postDelayed(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            progressBar.setVisibility(View.GONE);
                            uploading.setVisibility(View.GONE);
                            title.setText("");
                            imageView.setImageResource(android.R.color.transparent);
                            videoView.setVideoURI(null);
                            imageView.setVisibility(View.GONE);
                            videoView.setVisibility(View.GONE);
                            upload_confirm.setVisibility(View.GONE);
                            play.setVisibility(View.GONE);
                        }
                    }, 2000);
                }


            });

        }

    }

    @Override
    public void onBackPressed()
    {
        if(isuploadcomplete)
        {
            finish();
            super.onBackPressed();
        }
        else
        {
            AlertDialog.Builder alert=new AlertDialog.Builder(Post_Creator.this);
            alert.setTitle("Upload is Not Complete");
            alert.setMessage("Please Wait for the upload to Complete");
            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    dialog.dismiss();
                }
            });
            alert.show();
        }

    }
}


