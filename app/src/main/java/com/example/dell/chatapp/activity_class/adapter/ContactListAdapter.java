package com.example.dell.chatapp.activity_class.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dell.chatapp.R;
import com.example.dell.chatapp.activity_class.model.User;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.ViewHolder> {
    private List<User> mContact;
    private OnUserClickListener listener;
    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView cName;
        public TextView cEmail;
        public TextView cSentDate;
        public CircleImageView profile;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cName = itemView.findViewById(R.id.contact_name);
            cEmail = itemView.findViewById(R.id.contact_email);
//            cSentDate = itemView.findViewById(R.id.message_sent_date);
            profile = itemView.findViewById(R.id.contact_image);
        }
    }

    public ContactListAdapter(List<User> users,OnUserClickListener listener){
        this.mContact = users;
        this.listener  = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.contact_list_row,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final User user = mContact.get(i);
        viewHolder.cName.setText(user.getUsername());
        viewHolder.cEmail.setText(user.getEmail());
        Picasso.get().load(user.getProfileURL()).into(viewHolder.profile);
        viewHolder.profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onUserClicked(user);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mContact.size();
    }

    public interface OnUserClickListener{
        void onUserClicked(User user);
    }
}
