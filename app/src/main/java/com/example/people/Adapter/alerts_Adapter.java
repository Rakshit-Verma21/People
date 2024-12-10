package com.example.people.Adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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

public class alerts_Adapter extends RecyclerView.Adapter<alerts_Adapter.alerts_AdapterViewholder>
{
    private Context context;
    private ArrayList<String> list;

    private TextView textView;
    FirebaseDatabase database;
    DatabaseReference reference;

    FirebaseUser user;



    public alerts_Adapter(Context context, ArrayList<String> list, TextView textView)
    {
        this.context = context;
        this.list = list;
        this.textView=textView;
       database=FirebaseDatabase.getInstance();
         reference=database.getReference();
         user= FirebaseAuth.getInstance().getCurrentUser();

    }


    @NonNull
    @Override
    public alerts_AdapterViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.single_request,parent,false);
        return new alerts_AdapterViewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull alerts_AdapterViewholder holder, int position)
    {

        reference.child("users").child(list.get(position)).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                String name=snapshot.child("username").getValue(String.class);
                String image=snapshot.child("profile_image").getValue(String.class);
                holder.textView.setText(name);
                Picasso.get().load(image).into(holder.imageView);
                holder.decline.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        reference.child("friends_request").child(user.getUid()).child("request_received_from").child(list.get(holder.getAdapterPosition())).child("status").setValue("declined");
                        reference.child("friends_request").child(list.get(holder.getAdapterPosition())).child("request_sent_to").child(user.getUid()).child("status").setValue("declined");
                        list.remove(holder.getAdapterPosition());
                    }
                });
                holder.accept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {

                        reference.child("friend_list").child(user.getUid()).child(list.get(holder.getAdapterPosition())).setValue("friends");
                        reference.child("friend_list").child(list.get(holder.getAdapterPosition())).child(user.getUid()).setValue("friends");
                        reference.child("friends_request").child(user.getUid()).child("request_received_from").child(list.get(holder.getAdapterPosition())).removeValue();
                        reference.child("friends_request").child(list.get(holder.getAdapterPosition())).child("request_sent_to").child(user.getUid()).removeValue();
                        list.remove(holder.getAdapterPosition());
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount()
    {
       if(list.isEmpty())
       {
           textView.setVisibility(View.VISIBLE);


       }
       else
       {
           textView.setVisibility(View.GONE);

       }

        return list.size();
    }
    public class alerts_AdapterViewholder extends RecyclerView.ViewHolder
    {
        TextView textView;
        ImageView imageView;
        Button decline;
        Button accept;


        public alerts_AdapterViewholder(@NonNull View itemView)
        {
            super(itemView);
            textView=itemView.findViewById(R.id.textView);
            imageView=itemView.findViewById(R.id.imageView2);
            decline=itemView.findViewById(R.id.button5);
            accept=itemView.findViewById(R.id.button6);
        }
    }
}