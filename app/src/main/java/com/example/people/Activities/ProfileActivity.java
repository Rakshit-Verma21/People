package com.example.people.Activities;



import android.content.Intent;
import android.os.Bundle;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.people.Adapter.alerts_Adapter;
import com.example.people.Adapter.user_uploadAdapter_ViewProfile;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private CircleImageView profile;
    private TextView username;
    private TextView request;
    private TextView sayhi;

    TextView post_notfriend;
    private ImageButton sendrequest;
    private ImageButton unfriend;
    int click = 0;
    TextView textview_gone;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private String user_key;
    FirebaseUser User;
    String otherusername;
    TextView postindicator;
    RecyclerView postrecyclerview;
    ArrayList<String> listofrequests = new ArrayList<>();
    ArrayList<String> friendlist = new ArrayList<>();
    ArrayList<String> id_post = new ArrayList<>();
    user_uploadAdapter_ViewProfile adapter_post;
    alerts_Adapter adapter;
    RecyclerView recyclerView;
    ImageButton showrequest;
    private FloatingActionButton chat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        Intent intent = getIntent();
        String title = intent.getStringExtra("username");
        otherusername = title;
        user_key = intent.getStringExtra("key");

        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            getSupportActionBar().setTitle(title);
            getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            return insets;
        });
        User = FirebaseAuth.getInstance().getCurrentUser();
        recyclerView = findViewById(R.id.recyclerview_profileactivity);
        recyclerView.setLayoutManager(new LinearLayoutManager(ProfileActivity.this));
        recyclerView.setVisibility(View.GONE);
        textview_gone = findViewById(R.id.textView_profile);
        post_notfriend = findViewById(R.id.textView_post_notfriend);
        post_notfriend.setVisibility(View.GONE);
        textview_gone.setVisibility(View.GONE);
        profile = findViewById(R.id.proflile_imageview);
        showrequest = findViewById(R.id.showrequest);
        showrequest.setVisibility(View.GONE);
        sayhi=findViewById(R.id.textView2);
        sayhi.setVisibility(View.GONE);
        username = findViewById(R.id.profile_textview);
        request = findViewById(R.id.textview_request);
        sendrequest = findViewById(R.id.imageButton_sendrequest);
        postindicator = findViewById(R.id.textviewpost_indicator);
        postindicator.setVisibility(View.GONE);
        postrecyclerview = findViewById(R.id.rv_post_profile);
        postrecyclerview.setLayoutManager(new GridLayoutManager(ProfileActivity.this, 4));
        unfriend = findViewById(R.id.imageButtonunfriend);
        unfriend.setVisibility(View.GONE);
        chat = findViewById(R.id.floatingActionButton);
        chat.setVisibility(View.GONE);

            sayhi.setVisibility(View.VISIBLE);

            sayhi.setVisibility(View.GONE);

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("friend_id", user_key);
                bundle.putString("user_id", User.getUid().toString());
                Intent intent = new Intent(ProfileActivity.this, Chat_Activity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        checkstatus();
        getfriends();
        sendrequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                request();
            }
        });

        reference.child("users").child(user_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    String username_data = snapshot.child("username").getValue().toString();
                    String profile_data = snapshot.child("profile_image").getValue().toString();
                    if (username_data.isEmpty()) {
                        username.setText("Username");
                        Toast.makeText(ProfileActivity.this, "Unable to Fetch Username", Toast.LENGTH_SHORT).show();
                    } else {
                        username.setText(username_data);
                    }
                    if (profile_data.isEmpty()) {
                        profile.setImageResource(R.drawable.empty_profie);
                        Toast.makeText(ProfileActivity.this, "Unable to Fetch Profile Image", Toast.LENGTH_SHORT).show();
                    } else {
                        Picasso.get().load(profile_data).into(profile);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    public void request() {
        FirebaseUser User = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference();
        reference.child("friends_request").child(User.getUid()).child("request_sent_to").child(user_key).child("status").setValue("pending");
        reference.child("friends_request").child(user_key).child("request_received_from").child(User.getUid()).child("status").setValue("pending");
        Toast.makeText(ProfileActivity.this, "Request Sent", Toast.LENGTH_SHORT).show();
        request.setText("Request Sent");
        sendrequest.setVisibility(View.GONE);
        sendrequest.setClickable(false);
    }

    public void checkstatus() {
        status_request_sent();
        status_request_recieved();
        isdeclined();
        isfriend();
        friendlist_changes();
    }

    public void isdeclined() {
        try {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            reference.child("friends_request").child(User.getUid()).child("request_sent_to").child(user_key).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        String status = snapshot.child("status").getValue().toString();
                        if (status.equals("declined")) {
                            request.setText("Add To Friends");
                            sendrequest.setVisibility(View.VISIBLE);
                            sendrequest.setClickable(true);
                            unfriend.setVisibility(View.GONE);
                            chat.setVisibility(View.GONE);
                            sayhi.setVisibility(View.GONE);
                            showrequest.setVisibility(View.GONE);
                            reference.child("friends_request").child(User.getUid()).child("request_sent_to").child(user_key).removeValue();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
            reference.child("friends_request").child(User.getUid()).child("request_received_from").child(user_key).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        String status = snapshot.child("status").getValue().toString();
                        if (status.equals("declined")) {
                            request.setText("Add To Friends");
                            sendrequest.setVisibility(View.VISIBLE);
                            sendrequest.setClickable(true);
                            unfriend.setVisibility(View.GONE);
                            chat.setVisibility(View.GONE);
                            sayhi.setVisibility(View.GONE);
                            showrequest.setVisibility(View.GONE);
                            reference.child("friends_request").child(User.getUid()).child("request_received_from").child(user_key).removeValue();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void isfriend() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("friend_list").child(User.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    if (dataSnapshot.getKey().equals(user_key))
                    {
                        post_notfriend.setVisibility(View.GONE);
                        request.setText("You are Friends");
                        sendrequest.setVisibility(View.GONE);
                        sendrequest.setClickable(false);
                        chat.setVisibility(View.VISIBLE);
                        sayhi.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                        showrequest.setVisibility(View.GONE);
                        unfriend.setVisibility(View.VISIBLE);
                        unfriend.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                unfriend();
                            }
                        });
                    }
                    else
                    {
                        post_notfriend.setVisibility(View.VISIBLE);
                        post_notfriend.setText("Posts are Hidden Since You are Not Friends");
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void unfriend() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("friend_list").child(User.getUid()).child(user_key).removeValue();
        reference.child("friend_list").child(user_key).child(User.getUid()).removeValue();
        request.setText("Add To Friends");
        friendlist_changes();
        sendrequest.setVisibility(View.VISIBLE);
        sendrequest.setClickable(true);
        unfriend.setVisibility(View.GONE);
        chat.setVisibility(View.GONE);
        sayhi.setVisibility(View.GONE);
        postrecyclerview.setVisibility(View.GONE);

    }

    public void status_request_recieved() {
        reference.child("friends_request").child(User.getUid()).child("request_received_from").child(user_key).child("status").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {


                    String status_user = snapshot.getValue().toString();
                    if (status_user.equals("pending")) {
                        request.setText(otherusername + " Wants to be Your Friend");
                        sendrequest.setVisibility(View.GONE);
                        sendrequest.setClickable(false);
                        chat.setVisibility(View.GONE);
                        sayhi.setVisibility(View.GONE);
                        showrequest();
                    }
                } catch (Exception e) {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void status_request_sent() {
        reference.child("friends_request").child(User.getUid()).child("request_sent_to").child(user_key).child("status").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {


                    String status_user = snapshot.getValue().toString();
                    if (status_user.equals("pending")) {
                        request.setText("Request Sent");
                        sendrequest.setVisibility(View.GONE);
                        sendrequest.setClickable(false);
                        chat.setVisibility(View.GONE);
                        sayhi.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.GONE);
                        showrequest.setVisibility(View.GONE);
                    }
                } catch (Exception e) {

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void showrequest()
    {
        showrequest.setVisibility(View.VISIBLE);
        getsinglerequest();
        showrequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (click % 2== 0)
                {
                    recyclerView.setVisibility(View.VISIBLE);
                } else
                {
                    recyclerView.setVisibility(View.GONE);
                }
                click++;
            }
        });
    }

    public void getsinglerequest()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference user_request = FirebaseDatabase.getInstance().getReference();
        user_request.child("friends_request").child(User.getUid()).child("request_received_from").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    String childKey = childSnapshot.getKey();
                    if (childKey.equals(user_key))
                    {
                        listofrequests.add(childKey);
                    }
                }
                adapter = new alerts_Adapter(ProfileActivity.this, listofrequests, textview_gone);
                recyclerView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
    public void friendlist_changes()
    {
        reference.child("friend_list").child(User.getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
            {
                String childKey = snapshot.getKey();
                if(childKey.equals(user_key))
                {
                    request.setText("You are Friends");
                    sendrequest.setVisibility(View.GONE);
                    sendrequest.setClickable(false);
                    chat.setVisibility(View.VISIBLE);
                    sayhi.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    showrequest.setVisibility(View.GONE);
                    unfriend.setVisibility(View.VISIBLE);
                    unfriend.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v)
                        {
                            unfriend();
                        }
                    });
                    getfriends();
                }
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot)
            {
                String childKey = snapshot.getKey();
                if(childKey.equals(user_key))
                {
                   request.setText("Add To Friends");
                   sendrequest.setVisibility(View.VISIBLE);
                   sendrequest.setClickable(true);
                   unfriend.setVisibility(View.GONE);
                   chat.setVisibility(View.GONE);
                    sayhi.setVisibility(View.GONE);
                   showrequest.setVisibility(View.GONE);
                   postrecyclerview.setVisibility(View.GONE);
                   postindicator.setVisibility(View.GONE);
                }
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}});

    }
    public void getfriends()
    {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference();
        reference.child("friend_list").child(User.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    String user = dataSnapshot.getKey();
                    while (!friendlist.contains(user))
                    {
                        friendlist.add(user);
                    }
                }
                if(friendlist.contains(user_key))
                {
                    post_notfriend.setVisibility(View.GONE);
                    showpost();

                }
                if(!friendlist.contains(user_key))
                {
                    post_notfriend.setVisibility(View.VISIBLE);

                }
                if(friendlist.isEmpty())
                {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });


    }
    public void showpost()
    {
        try
        {
            getpost_id();
            postrecyclerview.setVisibility(View.VISIBLE);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
    public void getpost_id()
    {
        try {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            reference.child("posts").child(user_key).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        String id = snapshot1.getKey();
                        while (!id_post.contains(id))
                        {
                            id_post.add(id);

                        }
                    }
                    adapter_post.notifyDataSetChanged();
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            adapter_post = new user_uploadAdapter_ViewProfile(postindicator, id_post, ProfileActivity.this, user_key);
            postrecyclerview.setAdapter(adapter_post);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        finish();
    }

}









