package com.example.people.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.people.Adapter.alerts_Adapter;
import com.example.people.R;
import com.google.android.gms.tasks.OnSuccessListener;
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
 * Use the {@link Alerts#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Alerts extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RecyclerView rv;
    TextView alert_text;

    ArrayList<String>key=new ArrayList<>();
 private  alerts_Adapter adapter;


    public Alerts() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Alerts.
     */
    // TODO: Rename and change types and number of parameters
    public static Alerts newInstance(String param1, String param2) {
        Alerts fragment = new Alerts();
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
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view = inflater.inflate(R.layout.fragment_alerts, container, false);
        rv = view.findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        alert_text = view.findViewById(R.id.text_alert);

        accessrequests();




        return view;

    }

    public void accessrequests()
    {


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        FirebaseUser User = FirebaseAuth.getInstance().getCurrentUser();

        myRef.child("friends_request").child(User.getUid()).child("request_received_from").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
            {
                myRef.child("friends_request").child(User.getUid()).child("request_received_from").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot childSnapshot : snapshot.getChildren())
                        {
                            String childKey = childSnapshot.getKey();
                            while(!key.contains(childKey))
                            {
                                key.add(childKey);
                            }

                        }
                        for (int i = 0; i < key.size(); i++) {
                            myRef.child("friends_request").child(User.getUid()).child("request_received_from").child(key.get(i)).child("status").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                                @Override
                                public void onSuccess(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.getValue().equals("pending")) {
                                        adapter = new alerts_Adapter(getActivity(), key,alert_text);
                                        rv.setAdapter(adapter);
                                        adapter.notifyDataSetChanged();

                                    }
                                    if (dataSnapshot.getValue().equals("friends")) {
                                        key.remove(snapshot.getKey());
                                        adapter = new alerts_Adapter(getActivity(), key,alert_text);
                                        rv.setAdapter(adapter);
                                        adapter.notifyDataSetChanged();

                                    }

                                }
                            });

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error)
                    {

                    }
                });

                    }


            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
            {


            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot)
            {
                key.remove(snapshot.getKey());
                adapter = new alerts_Adapter(getActivity(),  key,alert_text);
                rv.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
            {


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        if(key.isEmpty())
        {
            alert_text.setVisibility(View.VISIBLE);
        }
        else
        {
            alert_text.setVisibility(View.GONE);
        }
    }
}

