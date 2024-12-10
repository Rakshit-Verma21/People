package com.example.people.Adapter;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.people.Activities.View_Uploads_Activity;
import com.example.people.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

public class user_uploadAdapter_ViewProfile extends RecyclerView.Adapter<user_uploadAdapter_ViewProfile.Viewholder>
{
    TextView textview;
    ArrayList<String>post_uri;
    Context context;
    DatabaseReference reference= FirebaseDatabase.getInstance().getReference();
    int click = 0;

    String user_id;

    public user_uploadAdapter_ViewProfile(TextView textview, ArrayList<String> post_uri, Context context, String user_id)
    {
        this.textview = textview;
        this.post_uri = post_uri;
        this.context = context;
        this.user_id = user_id;

    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.single_userupload,parent,false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position)
    {
        Intent intent   =new Intent(context, View_Uploads_Activity.class);

        reference.child("posts").child(user_id).child(post_uri.get(position)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                String type=snapshot.child("type").getValue(String.class);
                if(type.equals("image"))
                {
                    String uri=snapshot.child("file").getValue(String.class);
                    Picasso.get().load(uri).into(holder.imageview);
                    holder.videoview.setVisibility(View.GONE);
                    holder.play_button.setVisibility(View.GONE);
                    holder.imageview.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            intent.putExtra("uri", uri);
                            intent.putExtra("type","image");
                            intent.putExtra("user_id",user_id);
                            intent.putExtra("post_id",post_uri.get(position));
                            context.startActivity(intent);
                        }
                    });

                }
                else
                {

                    String uri=snapshot.child("file").getValue(String.class);
                    holder.videoview.setVideoURI(Uri.parse(uri));
                    holder.videoview.seekTo(1);
                    holder.play_button.setVisibility(View.VISIBLE);

                    holder.play_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v)
                        {
                            if(click %2==0)
                            {
                                holder.videoview.start();
                                holder.videoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                    @Override
                                    public void onCompletion(MediaPlayer mp) {
                                        holder.videoview.seekTo(1);
                                        holder.videoview.pause();
                                        holder.play_button.setVisibility(View.VISIBLE);

                                    }
                                });
                            }
                            else
                            {
                                holder.videoview.pause();
                                holder.play_button.setVisibility(View.VISIBLE);
                            }
                         click++;

                        }
                    });


                    holder.imageview.setVisibility(View.GONE);
                    holder.videoview.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            intent.putExtra("uri", uri);
                            intent.putExtra("type","video");
                            intent.putExtra("user_id",user_id);
                            intent.putExtra("post_id",post_uri.get(position));
                            context.startActivity(intent);
                        }
                    });
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    @Override
    public int getItemCount()
    {
        if(post_uri.isEmpty())
    {
        textview.setVisibility(View.VISIBLE);
    }
    else
    {
        textview.setVisibility(View.GONE);
    }

        return post_uri.size();

    }

    public static class Viewholder extends RecyclerView.ViewHolder
    {
        ImageView imageview;
        VideoView videoview;
        ImageButton play_button;

        public Viewholder(@NonNull View itemView)
        {
            super(itemView);
            imageview=itemView.findViewById(R.id.imageViewcv);
            videoview=itemView.findViewById(R.id.videoView2cv);
            play_button=itemView.findViewById(R.id.imageButton5);

        }
    }

}
