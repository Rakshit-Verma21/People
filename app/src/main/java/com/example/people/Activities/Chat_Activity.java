package com.example.people.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.people.Adapter.messageAdapter;
import com.example.people.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Chat_Activity extends AppCompatActivity {
    ImageButton back;
    FloatingActionButton send;
    EditText message;
    TextView name;

    ImageButton options_button;
    RecyclerView recyclerView;
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    String user_id;
    String friend_id;
    messageAdapter messageAdapter;
    List<ModelClass> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        back = findViewById(R.id.button_back);
        name = findViewById(R.id.friend_username);
        send = findViewById(R.id.send);
        message = findViewById(R.id.message);
        options_button = findViewById(R.id.button_chat_options);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            friend_id = bundle.getString("friend_id");
            user_id = bundle.getString("user_id");
        }

        recyclerView = findViewById(R.id.rv_chat);
        reference.child("users").child(friend_id).child("username").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                name.setText(snapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        getmesssages();

        recyclerView.setLayoutManager(new LinearLayoutManager(Chat_Activity.this));

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }

        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message_sent = message.getText().toString();
                try {
                    if (message_sent.isEmpty()) {

                    } else {
                        sendMessage(message_sent);
                        message.setText("");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        options_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                PopupMenu popupMenu=new PopupMenu(Chat_Activity.this,v);
                popupMenu.getMenuInflater().inflate(R.menu.chat_activity_optionsmeny,popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item)
                    {
                        if (item.getItemId()==R.id.profile_otheruser)
                        {
                            Intent intent=new Intent(Chat_Activity.this, ProfileActivity.class);
                            intent.putExtra("username",name.getText().toString());
                            intent.putExtra("key",friend_id);
                            startActivity(intent);

                        }
                        return true;
                    }
                });
                popupMenu.show();


            }
        });
    }
    public void getmesssages()
    {
        reference.child("messages").child(user_id).child(friend_id).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
            {
                ModelClass modelClass=snapshot.getValue(ModelClass.class);
                list.add(modelClass);
                messageAdapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(list.size()-1);


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        messageAdapter=new messageAdapter(list,user_id);
        recyclerView.setAdapter(messageAdapter);

    }

    public void sendMessage(String message_sent) {
        String message_id = reference.child("messages").child(user_id).child(friend_id).push().getKey();
        Map<String, Object> messagemap = new HashMap<>();
        messagemap.put("message", message_sent);
        messagemap.put("from", user_id);
        reference.child("messages").child(user_id).child(friend_id).child(message_id).setValue(messagemap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    reference.child("messages").child(friend_id).child(user_id).child(message_id).setValue(messagemap);
                }

            }
        });

    }
}