package com.signatureapp.app.az;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class AllUsersActivity extends AppCompatActivity {


    RecyclerView recycleView;

    DatabaseReference databaseReference;

    FirebaseAuth auth;
    FirebaseRecyclerAdapter friendsConvAdapter;
    CardView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);


        databaseReference = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        recycleView = findViewById(R.id.RecycleList);
//        back = findViewById(R.id.back);


        recycleView.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setReverseLayout(true);
        llm.setStackFromEnd(true);
        recycleView.setLayoutManager(llm);




        ActionBar actionBar = getSupportActionBar();

        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);

        if (actionBar != null) {
            actionBar.setTitle((Html.fromHtml("<font color=\"#ffffff\">" + "Usuarios" + "</font>")));
        }




        Query conversationQuery = databaseReference.child("Users");


        FirebaseRecyclerOptions<Users> options =
                new FirebaseRecyclerOptions.Builder<Users>()
                        .setQuery(conversationQuery, Users.class)
                        .build();

        FirebaseRecyclerAdapter friendsConvAdapter = new FirebaseRecyclerAdapter<Users, Viewholder>(options) {
            @Override
            public Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_user, parent, false);

                return new Viewholder(view);
            }

            @Override
            protected void onBindViewHolder(Viewholder viewholder, int position, Users model) {

                viewholder.nameTextview.setText(model.getName());
                viewholder.emailTextView.setText(model.getEmail());
                viewholder.sizeItemTimeTextView.setText(model.getRank());


                Log.e("hhhd", "onBindViewHolder: "+ model.getName());











            }
        };


        friendsConvAdapter.startListening();
        recycleView.setAdapter(friendsConvAdapter);
        friendsConvAdapter.startListening();




    }

    static class Viewholder extends RecyclerView.ViewHolder {

        TextView nameTextview,emailTextView,sizeItemTimeTextView;


        public Viewholder(@NonNull View itemView)
        {
            super(itemView);

            nameTextview = itemView.findViewById(R.id.nameTextview);
            emailTextView = itemView.findViewById(R.id.emailTextView);
            sizeItemTimeTextView = itemView.findViewById(R.id.sizeItemTimeTextView);



        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}