package com.example.dell.chatapp.activity_class;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.dell.chatapp.R;
import com.example.dell.chatapp.activity_class.adapter.ChatMessageListAdapter;
import com.example.dell.chatapp.activity_class.model.Message;
import com.example.dell.chatapp.activity_class.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatRoomActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView rvMessage;
    private ImageButton btnSend;
    private EditText etMessage;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference userRef;
    private DatabaseReference messageRef;
    private String currentUserID;
    private ChatMessageListAdapter messageListAdapter;
    private List<Message> messages;

    private User receiver;
    private User sender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        toolbar = findViewById(R.id.toolbar);
        rvMessage = findViewById(R.id.rvMessages);
        btnSend = findViewById(R.id.btnSend);
        etMessage = findViewById(R.id.etMessage);

        receiver = (User) getIntent().getSerializableExtra("reeciver");

        toolbar.setTitle(receiver.getUsername());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = etMessage.getText().toString().trim();
                if (message.isEmpty()) return;
                Long mDate = System.currentTimeMillis();
                Message mMessage = new Message(message, mDate, sender);
                String id = getChatId();
                String messageId = messageRef.push().getKey();
                messageRef.child(id)
                        .child(messageId)
                        .setValue(mMessage)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                etMessage.setText("");
                            }
                        });
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        currentUserID = firebaseAuth.getUid();
        messages = new ArrayList<>();
        messageListAdapter = new ChatMessageListAdapter(messages);
        rvMessage.setAdapter(messageListAdapter);
        rvMessage.setLayoutManager(new LinearLayoutManager(this));
        userRef = FirebaseDatabase.getInstance()
                .getReference("users");
        messageRef = FirebaseDatabase.getInstance()
                .getReference("messages");

        userRef.child(currentUserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        sender = dataSnapshot.getValue(User.class);
                        loadMessage();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


    }

    private void loadMessage() {
        String id = getChatId();
        messageRef.child(id)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        messages.clear();
                        for (DataSnapshot message : dataSnapshot.getChildren()) {
                            Message msg = message.getValue(Message.class);
                            messages.add(msg);
                        }
                        messageListAdapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }


    private String getChatId() {
        int result = sender.getUserID().compareTo(receiver.getUserID());
        if (result <= 0)
            return sender.getUserID() + "_" + receiver.getUserID();
        else
            return receiver.getUserID() + "_" + sender.getUserID();
    }
}
