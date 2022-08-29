package com.signatureapp.app.az;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.bouncycastle.asn1.dvcs.Data;

public class SignedByActivity extends AppCompatActivity {


    RecyclerView recycleView;

    DatabaseReference databaseReference;

    FirebaseAuth auth;
    FirebaseRecyclerAdapter friendsConvAdapter;
    CardView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signed_by);



        String filename = getIntent().getStringExtra("filename");
        databaseReference = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        recycleView = findViewById(R.id.RecycleList);
//        back = findViewById(R.id.back);


        recycleView.setHasFixedSize(true);
        recycleView.setLayoutManager(new LinearLayoutManager(this));


        ActionBar actionBar = getSupportActionBar();

        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);

        if (actionBar != null) {
            actionBar.setTitle((Html.fromHtml("<font color=\"#ffffff\">" + "Approvals" + "</font>")));
        }

        if(!filename.isEmpty()){

            String result = filename.substring(0, filename.indexOf("."));

            Query conversationQuery = databaseReference.child("Files").child(result).child("SignedBy");


            FirebaseRecyclerOptions<SignedByModel> options =
                    new FirebaseRecyclerOptions.Builder<SignedByModel>()
                            .setQuery(conversationQuery, SignedByModel.class)
                            .build();

            FirebaseRecyclerAdapter friendsConvAdapter = new FirebaseRecyclerAdapter<SignedByModel, Viewholder>(options) {
                @Override
                public Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {

                    View view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.item_sign, parent, false);

                    return new Viewholder(view);
                }

                @Override
                protected void onBindViewHolder(Viewholder viewholder, int position, SignedByModel model) {




                    DatabaseReference dd = FirebaseDatabase.getInstance().getReference().child("Users").child(model.getUserId());
                    dd.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if(snapshot.exists()){

                                String email = snapshot.child("email").getValue().toString();
                                String rank = snapshot.child("rank").getValue().toString();
                                String name = snapshot.child("name").getValue().toString();
                                viewholder.nameTextview.setText(name);
                                viewholder.emailTextView.setText(email);
                                viewholder.sizeItemTimeTextView.setText(rank);


                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });



                }
            };


            friendsConvAdapter.startListening();
            recycleView.setAdapter(friendsConvAdapter);
            friendsConvAdapter.startListening();

        }







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