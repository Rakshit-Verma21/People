package com.example.people.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.people.Activities.Chat_Activity;
import com.example.people.Activities.ProfileActivity;
import com.example.people.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class friend_list_adapter extends RecyclerView.Adapter<friend_list_adapter.friend_list_apdapterViewHolder>
{
    private ArrayList<String>friendlist=new ArrayList<>();
   private  Context context;
   private TextView textView;
    FirebaseDatabase database=FirebaseDatabase.getInstance();
    DatabaseReference reference=database.getReference();
    FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();

    public friend_list_adapter(ArrayList<String> friendlist, Context context,TextView textView)
    {
        this.friendlist = friendlist;
        this.context = context;
        this.textView=textView;
    }



    @NonNull
    @Override
    public friend_list_apdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.single_friend_cardview,parent,false);
        return new friend_list_apdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull friend_list_apdapterViewHolder holder, int position)
    {

        reference.child("users").child(friendlist.get(position)).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {

                    String name = snapshot.child("username").getValue().toString();
                    String profile = snapshot.child("profile_image").getValue().toString();
                    holder.name.setText(name);
                    Picasso.get().load(profile).into(holder.profile);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent=new Intent(context, ProfileActivity.class);
                intent.putExtra("key",friendlist.get(holder.getAdapterPosition()));
                intent.putExtra("username",holder.name.getText().toString());
                context.startActivity(intent);

            }
        });
        holder.chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Bundle bundle=new Bundle();
                bundle.putString("friend_id",friendlist.get(holder.getAdapterPosition()));
                bundle.putString("user_id",user.getUid().toString());
                Intent intent=new Intent(context, Chat_Activity.class);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });




    }

    @Override
    public int getItemCount()
    {
        if(friendlist.isEmpty())
        {
            textView.setVisibility(View.VISIBLE);
        }
        else
        {
            textView.setVisibility(View.GONE);
        }

        return friendlist.size();
    }

    public class friend_list_apdapterViewHolder extends RecyclerView.ViewHolder
    {
        ImageView profile;
        TextView name;
        FloatingActionButton chat;
        public friend_list_apdapterViewHolder(@NonNull View itemView)
        {
            super(itemView);
            profile=itemView.findViewById(R.id.profile_friends_fragment);
            name=itemView.findViewById(R.id.username_friends_fragment);
            chat=itemView.findViewById(R.id.chat);
        }
    }
}
