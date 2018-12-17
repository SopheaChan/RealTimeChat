package com.example.dell.chatapp.activity_class;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;

import com.example.dell.chatapp.R;
import com.example.dell.chatapp.activity_class.adapter.ContactListAdapter;
import com.example.dell.chatapp.activity_class.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static java.security.AccessController.getContext;

public class ContactListActivity extends AppCompatActivity implements ContactListAdapter.OnUserClickListener {

    private List<User> userList;

    private RecyclerView recyclerView;
    private SearchView searchView;

    private ContactListAdapter myAdapter;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        recyclerView = findViewById(R.id.contact_list_recycler_view);
        searchView = findViewById(R.id.search_box);
        searchView.getSuggestionsAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userList = new ArrayList<>();
        myAdapter = new ContactListAdapter(userList,this);
        recyclerView.setAdapter(myAdapter);
        usersRef = FirebaseDatabase.getInstance()
                .getReference("users");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                searchUser(s.trim());
                return false;
            }
        });
    }

    void searchUser(final String query){
       usersRef.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               userList.clear();
               for(DataSnapshot user : dataSnapshot.getChildren()){
                   User u = user.getValue(User.class);
                   if(u.getUsername().contains(query)){
                       userList.add(u);
                   }
               }
               myAdapter.notifyDataSetChanged();
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });
   }

    @Override
    public void onUserClicked(User user) {
        Intent intent = new Intent(this,ChatRoomActivity.class);
        intent.putExtra("reeciver",user);
        startActivity(intent);
    }
}
