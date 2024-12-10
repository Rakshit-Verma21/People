package com.example.people.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.people.Fragments.chats;
import com.example.people.Fragments.public_postDisplay;

public class Viewpager_adapter extends FragmentStateAdapter
{


    public Viewpager_adapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public Viewpager_adapter(@NonNull Fragment fragment) {
        super(fragment);
    }
    public Viewpager_adapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle)
    {

        super(fragmentManager, lifecycle);

    }

    @NonNull
    @Override
    public Fragment createFragment(int position)
    {
        switch (position)
        {
            case 1:

                return new chats();


            default:
                return new public_postDisplay();

        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }


}