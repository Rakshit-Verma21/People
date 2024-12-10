package com.example.people.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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

import java.util.List;

public class UserlistAdapter extends RecyclerView.Adapter<UserlistAdapter.UserlistAdapter_fragmentViewholder>
{
    List<String> userlist;
    String username;
    Context context;
    FirebaseDatabase database;
    DatabaseReference reference;

    public UserlistAdapter(List<String> userlist, String username, Context context)
    {
        this.userlist = userlist;
        this.username = username;
        this.context = context;
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
    }

    @NonNull
    @Override
    public UserlistAdapter_fragmentViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.userlist_cardview,parent,false);

        return new UserlistAdapter_fragmentViewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserlistAdapter_fragmentViewholder holder, int position)
    {
        reference.child("users").child(userlist.get(position)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                String other_user;
                String key=snapshot.getKey();
                try
                {
                   other_user=snapshot.child("username").getValue().toString();
                    String profile_pic=snapshot.child("profile_image").getValue().toString();
                    holder.username.setText(other_user);
                    if(profile_pic.isEmpty())
                    {
                        holder.profile.setImageResource(R.drawable.empty_profie);
                    }
                    else
                    {
                        Picasso.get().load(profile_pic).into(holder.profile);
                    }

                }
                catch (NullPointerException e)
                {
                    e.printStackTrace();
                }
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        Intent intent=new Intent(context, ProfileActivity.class);
                        intent.putExtra("key",key);
                        intent.putExtra("username",holder.username.getText().toString());
                        context.startActivity(intent);
                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    @Override
    public int getItemCount() {
        return userlist.size();
    }

    public class UserlistAdapter_fragmentViewholder extends RecyclerView.ViewHolder
    {
        TextView username;
        ImageView profile;

        public UserlistAdapter_fragmentViewholder(@NonNull View itemView)
        {
            super(itemView);
            username = itemView.findViewById(R.id.textView_card);
            profile = itemView.findViewById(R.id.imageView_card);
        }
    }

}
