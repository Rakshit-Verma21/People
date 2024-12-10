package com.example.people.Fragments;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.people.Activities.ModelClass;
import com.example.people.Activities.friends;
import com.example.people.Adapter.chat_fragment_Adapter;
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
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link chats#newInstance} factory method to
 * create an instance of this fragment.
 */
public class chats extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    RecyclerView recyclerView;
    TextView textView;

    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseUser user;

    FloatingActionButton new_chat;


   List<String> key_user=new ArrayList<>();

    chat_fragment_Adapter adapter;


    public chats()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment chats.
     */
    // TODO: Rename and change types and number of parameters
    public static chats newInstance(String param1, String param2) {
        chats fragment = new chats();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
      View view= inflater.inflate(R.layout.fragment_chats, container, false);
      recyclerView=view.findViewById(R.id.rv_chats_fragment);
      user= FirebaseAuth.getInstance().getCurrentUser();
      recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
      textView=view.findViewById(R.id.textView7);
      new_chat=view.findViewById(R.id.chat_new);


         new_chat.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v)
          {
              Intent intent=new Intent(getActivity(), friends.class);
              startActivity(intent);
          }
      });
      getkeys();
      changes();
      adapter=new chat_fragment_Adapter(getActivity(),key_user,textView);
      recyclerView.setAdapter(adapter);
      return view;
    }

    public void getkeys()
    {
        database=FirebaseDatabase.getInstance();
        reference=database.getReference();
        user=FirebaseAuth.getInstance().getCurrentUser();
        reference.child("messages").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                for(DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                   String user=dataSnapshot.getKey();
                   while(!key_user.contains(user))
                   {
                       key_user.add(user);
                       adapter.notifyDataSetChanged();
                   }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    public void changes()
    {
        reference=FirebaseDatabase.getInstance().getReference();
        reference.child("messages").child(user.getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
            {
                getkeys();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot)
            {
                String user=snapshot.getKey().toString();
                while(key_user.contains(user))
                {
                    key_user.remove(user);
                    adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}