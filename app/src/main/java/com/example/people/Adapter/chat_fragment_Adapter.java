package com.example.people.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.people.Activities.Chat_Activity;
import com.example.people.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class chat_fragment_Adapter extends RecyclerView.Adapter<chat_fragment_Adapter.ViewHolder_chat_fragment>
{
    Context context;
    List<String> key_list=new ArrayList<>();
    TextView text_zeroChats;
    DatabaseReference reference=FirebaseDatabase.getInstance().getReference();
    FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();

    public chat_fragment_Adapter(Context context, List<String> key_list,TextView textView)
    {
        this.context = context;
        this.key_list = key_list;
        this.text_zeroChats=textView;


    }
    @NonNull
    @Override
    public ViewHolder_chat_fragment onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.single_chat,parent,false);
        return new ViewHolder_chat_fragment(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder_chat_fragment holder, int position) {

           reference.child("users").child(key_list.get(position)).addValueEventListener(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot snapshot)

               {
                   String name=snapshot.child("username").getValue().toString();
                   String image=snapshot.child("profile_image").getValue().toString();
                   holder.name.setText(name);
                   Picasso.get().load(image).into(holder.profile_image);

               }

               @Override
               public void onCancelled(@NonNull DatabaseError error) {

               }
           });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Bundle bundle=new Bundle();
                bundle.putString("friend_id",key_list.get(holder.getAdapterPosition()));
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
        if(key_list.isEmpty())
        {
            text_zeroChats.setVisibility(View.VISIBLE);
        }
        else
        {
            text_zeroChats.setVisibility(View.GONE);
        }
        return key_list.size();
    }

    public class ViewHolder_chat_fragment extends RecyclerView.ViewHolder
    {
        ImageView profile_image;
        TextView name;

        public ViewHolder_chat_fragment(@NonNull View itemView)
        {

            super(itemView);

            profile_image=itemView.findViewById(R.id.profile_chats_fragment);
            name=itemView.findViewById(R.id.username_chats_fragment);
        }
    }
}
