package com.example.people.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.people.Activities.Post_Creator;
import com.example.people.Adapter.Public_post_adapterDisplay;
import com.example.people.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class public_postDisplay extends Fragment
{
     RecyclerView recyclerView;
     ArrayList<String>postId=new ArrayList<>();
     Public_post_adapterDisplay adapterDisplay;


     FloatingActionButton open;
     DatabaseReference reference;

    public public_postDisplay()
    {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.public_post_display,container,false);
        recyclerView=view.findViewById(R.id.rv_publicPost);
        open=view.findViewById(R.id.button_opencreatePost);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        getPostId();
        manageChanges();
        adapterDisplay=new Public_post_adapterDisplay(postId,getActivity());
        recyclerView.setAdapter(adapterDisplay);
        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent=new Intent(getActivity(), Post_Creator.class);
                startActivity(intent);
            }
        });
        return view;

    }
    public void getPostId()
    {
        reference=FirebaseDatabase.getInstance().getReference();
     reference.child("post_data").addValueEventListener(new ValueEventListener() {
         @Override
         public void onDataChange(@NonNull DataSnapshot snapshot)
         {
             postId.clear();
             for(DataSnapshot post:snapshot.getChildren()) {
                 String id = post.getKey();
                 if (!postId.contains(id))
                 {
                     postId.add(id);
                     Collections.shuffle(postId);
                     adapterDisplay.notifyDataSetChanged();
                 }
             }
         }
         @Override
         public void onCancelled(@NonNull DatabaseError error) {

         }
     });

    }
    public void manageChanges()
    {
        try {
            reference.child("post_data").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
                {
                    String id = snapshot.getKey();
                    if (!postId.contains(id))
                    {
                        postId.add(id);
                        Collections.shuffle(postId);
                        adapterDisplay.notifyDataSetChanged();
                    }

                }
                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {}
                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
