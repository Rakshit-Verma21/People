package com.example.people.Fragments;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.people.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link reset_password#newInstance} factory method to
 * create an instance of this fragment.
 */
public class reset_password extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public reset_password() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment reset_password.
     */
    // TODO: Rename and change types and number of parameters
    public static reset_password newInstance(String param1, String param2) {
        reset_password fragment = new reset_password();
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
        String email;
        View view=inflater.inflate(R.layout.fragment_reset_password, container, false);
        Bundle bundle=getArguments();
        email=bundle.getString("email");
        ImageView image=(ImageView)view.findViewById(R.id.imageView);
        EditText et_email=(EditText)view.findViewById(R.id.reset_email);
        Button reset=(Button)view.findViewById(R.id.buttonreset);


        et_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {


                String mail=et_email.getText().toString();
                if(mail.endsWith("@gmail.com"))
                {
                    loadImage(mail,image);;
                }
                else
                {
                    image.setImageResource(R.drawable.empty_profie);
                }

            }

            @Override
            public void afterTextChanged(Editable s)
            {
                String mail=et_email.getText().toString();
                if(mail.endsWith("@gmail.com"))
                {
                    loadImage(mail,image);;
                }
                else
                {
                    image.setImageResource(R.drawable.empty_profie);
                }

            }
        });
        TextView timer=(TextView) view.findViewById(R.id.timer);
        timer.setVisibility(View.GONE);

        if(email!=null)
        {
            et_email.setText(email);
            loadImage(email,image);
            reset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    timer.setVisibility(View.VISIBLE);

                    send_reset_mail(et_email.getText().toString());
                    if(!email.isEmpty())
                    {
                        start_timer(reset, timer);
                    }



                }
            });

        }

        return view;

    }
    public void send_reset_mail(String email)
    {
        FirebaseAuth mAuth=FirebaseAuth.getInstance();
        if(!email.isEmpty())
        {
            mAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(getContext(), "Reset Email sent", Toast.LENGTH_SHORT).show();

                }
            });
        }
        else
        {
            Toast.makeText(getContext(), "Please Enter Email", Toast.LENGTH_SHORT).show();
        }

    }
    public void loadImage(String email , ImageView image)
    {   String encodedEmail=email.replace(".",",");
        FirebaseAuth auth;FirebaseUser user;
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        String path= encodedEmail+"/image.jpg";
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imagepathref=storageRef.child(path);
        imagepathref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri)
            {

                Picasso.get().load(uri).into(image);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "No Profile Image Found", Toast.LENGTH_SHORT).show();

            }
        });



    }
    public void start_timer(Button reset, TextView timer) {
        new CountDownTimer(120000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                reset.setClickable(false);
                String time_left=String.format(Locale.getDefault(),"%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60,
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60);
                timer.setText(time_left);


            }

            @Override
            public void onFinish() {
                reset.setClickable(true);
                timer.setVisibility(View.GONE);

            }


        }.start();
    }
}