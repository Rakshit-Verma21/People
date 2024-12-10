package com.example.people.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.people.Adapter.UserlistAdapter;
import com.example.people.R;
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

public class Search_Users extends AppCompatActivity {
    private FirebaseDatabase database;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference reference;
    private RecyclerView recyclerView;


    List<String> list = new ArrayList<>();
    private UserlistAdapter adapter;
    SearchView searchView;
    List<String> filteredlist = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        getSupportActionBar().setTitle("FIND PEOPLE");
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient));
        setContentView(R.layout.activity_search_users);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();


        recyclerView = findViewById(R.id.rv);
        searchView = findViewById(R.id.searchview);

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        reference.child("users").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String username = snapshot.getValue().toString();
                getusers();
                adapter = new UserlistAdapter(list, username,Search_Users.this);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setQuery("", false);
                setFilteredlist();
                adapter = new UserlistAdapter(filteredlist, "", Search_Users.this);
                recyclerView.setAdapter(adapter);

            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                adapter = new UserlistAdapter(list, "",Search_Users.this);
                recyclerView.setAdapter(adapter);

                return false;

            }
        });
    }

    public void getusers() {
        reference.child("users").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String key = snapshot.getKey();
                if (!key.equals(user.getUid())) {
                    list.add(key);
                    adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                String key = snapshot.getKey();
                if (!key.equals(user.getUid())) {
                    list.set(list.lastIndexOf(key), key);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                String key = snapshot.getKey();
                if (!key.equals(user.getUid())) {
                    list.remove(key);
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

    public void setFilteredlist() {

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    filteredlist.clear();
                    adapter.notifyDataSetChanged();

                } else {
                    for (String key : list)
                        reference.child("users").child(key).child("username").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String username = snapshot.getValue().toString().toLowerCase();
                                if (username.contains(newText.toLowerCase()) || username.startsWith(newText.toLowerCase())
                                        || username.endsWith(newText.toLowerCase()) || username.equals(newText.toLowerCase())) {
                                    if (!filteredlist.contains(key)) {
                                        filteredlist.add(key);
                                        adapter.notifyDataSetChanged();
                                    }

                                } else {
                                    filteredlist.remove(key);
                                    adapter.notifyDataSetChanged();
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                }
                return true;
            }
        });

    }
}