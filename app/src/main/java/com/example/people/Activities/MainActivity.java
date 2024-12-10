package com.example.people.Activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.people.Adapter.Viewpager_adapter;
import com.example.people.Fragments.Alerts;
import com.example.people.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class MainActivity extends AppCompatActivity
{
    FirebaseAuth auth;
    FirebaseUser user;
    ViewPager2 pager;
    TabLayout tabLayout;
    Viewpager_adapter adapter;


    FragmentContainerView fragmentContainerView;

    StorageReference storageReference;
    DatabaseReference databaseReference;
    TextView search;
    int fragment_code=0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        user = FirebaseAuth.getInstance().getCurrentUser();
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient));
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("users").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String title = snapshot.child("username").getValue().toString().toUpperCase();
                getSupportActionBar().setTitle("WELCOME " + title);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        setContentView(R.layout.activity_main);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        super.onStart();
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
        {
            // When permission is granted

            // Create method

        } else
        {
            // When permission is not granted
            // request permission
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,}, 1);
        }



        auth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();







        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
        pager = findViewById(R.id.pager);
        search = findViewById(R.id.textViewsearch);
        fragmentContainerView = findViewById(R.id.fragmentContainerView);
        fragmentContainerView.setVisibility(View.GONE);
        adapter = new Viewpager_adapter(getSupportFragmentManager(), getLifecycle());
        pager.setAdapter(adapter);
        tabLayout = findViewById(R.id.tabs);
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, pager, (tab, position) ->
        {
            switch(position)
            {
                case 0:
                    tab.setText("Posts");
                    break;
                    case 1:
                    tab.setText("Chats");
                    break;
            }


        }
        );
        tabLayoutMediator.attach();

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Search_Users.class);
                startActivity(intent);
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0
                && grantResults[0]
                == PackageManager.PERMISSION_GRANTED)
        {

            // When permission is granted
            // Call method

        }
        else
        {
            // When permission is denied
            // Display Toast

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.options_main_activity,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        if(item.getItemId()==R.id.action_profile)
        {
            Intent intent=new Intent(this, ViewProfile.class);
            startActivity(intent);
        }
        if(item.getItemId()==R.id.alert)
        {
            fragmentContainerView.setVisibility(View.VISIBLE);
            search.setVisibility(View.GONE);
            FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragmentContainerView,new Alerts());
            transaction.addToBackStack(null);
            transaction.commit();
            fragment_code=1;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed()
    {
        if(fragment_code==1)
        {
            super.onBackPressed();

            search.setVisibility(View.VISIBLE);
            fragmentContainerView.setVisibility(View.GONE);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.popBackStack();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(new Alerts());
            fragment_code=0;
            fragmentTransaction.commit();
        }
        else
        {
            super.onBackPressed();
            pager.removeAllViews();
            tabLayout.removeAllTabs();
            finishAffinity();
            finish();
        }

    }




}