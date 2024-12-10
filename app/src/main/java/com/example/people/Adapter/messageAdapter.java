package com.example.people.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.people.Activities.ModelClass;
import com.example.people.R;

import java.util.List;

public class messageAdapter extends RecyclerView.Adapter<messageAdapter.messageViewholder> {
    List<ModelClass> list;
    String username;
    Boolean status;
    int send;
    int receive;

    public messageAdapter(List<ModelClass> list, String username) {
        this.list = list;
        this.username = username;
        status = false;
        send = 1;
        receive = 2;
    }

    @NonNull
    @Override
    public messageViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == send) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_sentmessaged, parent, false);

        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_recievedmessages, parent, false);
        }
        return new messageViewholder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull messageViewholder holder, int position) {
        holder.message.setText(list.get(position).getMessage());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class messageViewholder extends RecyclerView.ViewHolder
    {
        TextView message;

        public messageViewholder(@NonNull View itemView) {
            super(itemView);
            {
                if (status) {
                    message = itemView.findViewById(R.id.textsent);
                } else {

                    message = itemView.findViewById(R.id.text_recieve);
                }
            }
        }

    }
    @Override
    public int getItemViewType(int position)
    {
        if (list.get(position).getFrom().equals(username))
        {
            status = true;
            return send;
        } else
        {
            status = false;
            return receive;

        }
    }
}
