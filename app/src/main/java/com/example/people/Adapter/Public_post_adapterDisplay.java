package com.example.people.Adapter;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.people.Activities.ProfileActivity;
import com.example.people.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;

import de.hdodenhof.circleimageview.CircleImageView;

public class Public_post_adapterDisplay extends RecyclerView.Adapter<Public_post_adapterDisplay.ViewHolder>
{
    ArrayList<String>post_ids=new ArrayList<>();
    Context context;

    public Public_post_adapterDisplay(ArrayList<String> post_ids, Context context)
    {
        this.post_ids = post_ids;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.single_public_post,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        holder.image.setVisibility(View.GONE);
        holder.video.setVisibility(View.GONE);
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference();
        reference.child("post_data").child(post_ids.get(position)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    String title = snapshot.child("title").getValue().toString();
                    String type = snapshot.child("type").getValue().toString();
                    String user_id = snapshot.child("user_id").getValue().toString();
                    String file = snapshot.child("file").getValue().toString();
                    if (title.isEmpty()) {
                        holder.title.setVisibility(View.GONE);
                    } else {
                        holder.title.setText(title);
                    }
                    reference.child("users").child(user_id).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String username = snapshot.child("username").getValue().toString();
                            String profile_pic = snapshot.child("profile_image").getValue().toString();
                            holder.username.setText(username);
                            if (profile_pic.isEmpty()) {
                                holder.profile.setImageResource(R.drawable.empty_profie);
                            } else {
                                Picasso.get().load(profile_pic).into(holder.profile);
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    if (type.equals("image")) {
                        holder.image.setVisibility(View.VISIBLE);
                        holder.video.setVisibility(View.GONE);
                        Picasso.get().load(file).into(holder.image);
                    }
                    else
                    {
                        holder.image.setVisibility(View.GONE);
                        holder.video.setVisibility(View.VISIBLE);
                        holder.video.setVideoURI(Uri.parse(file));
                        holder.video.seekTo(1);
                        holder.video.start();
                        holder.video.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                holder.video.seekTo(1);
                                holder.video.pause();

                            }
                        });

                    }
                    holder.layout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, ProfileActivity.class);
                            intent.putExtra("key", user_id);
                            intent.putExtra("username", holder.username.getText().toString());
                            context.startActivity(intent);

                        }
                    });

                }
                catch ( Exception e)
                {
                    e.printStackTrace();
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
        return post_ids.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView title;
        TextView username;
        CircleImageView profile;
        ImageView image;
        VideoView video;
        LinearLayout layout;
        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            title=itemView.findViewById(R.id.post_title);
            username=itemView.findViewById(R.id.username_post);
            profile=itemView.findViewById(R.id.profile_image_post);
            image=itemView.findViewById(R.id.imageView_post);
            video=itemView.findViewById(R.id.videoView3);
            layout=itemView.findViewById(R.id.layout);
        }
    }


}
