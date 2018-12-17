package com.example.dell.chatapp.activity_class.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dell.chatapp.R;
import com.example.dell.chatapp.activity_class.model.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class ChatMessageListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int IS_SENDER = 1;
    private final int IS_RECEIVER = 2;
    private String currentUserId;

    private List<Message> messages;

    public ChatMessageListAdapter(List<Message> messages) {
        this.messages = messages;
        currentUserId = FirebaseAuth
                .getInstance()
                .getCurrentUser()
                .getUid();
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        if (message.getSender().getUserID().equals(currentUserId))
            return IS_SENDER;
        else
            return IS_RECEIVER;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        if (i == IS_SENDER) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.message_list_send_item, viewGroup, false);
            return new SenderViewHolder(view);
        } else {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.message_list_receive_item, viewGroup, false);
            return new ReceiverViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        Message message = messages.get(i);
        if (message.getSender().getUserID().equals(currentUserId)){
            SenderViewHolder holder = (SenderViewHolder) viewHolder;
            holder.bindView(message);
        }
        else{
            ReceiverViewHolder holder = (ReceiverViewHolder) viewHolder;
            holder.bindView(message);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    class SenderViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage = itemView.findViewById(R.id.tvMessage);

        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        void bindView(Message message){
            tvMessage.setText(message.getMessage());
        }
    }

    class ReceiverViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage = itemView.findViewById(R.id.tvMessage);

        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
        }
        void bindView(Message message){
            tvMessage.setText(message.getMessage());
        }
    }
}
