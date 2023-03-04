package com.signatureapp.app.az;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.signatureapp.app.az.Adapter.SignatureRecycleViewAdapter;
import com.signatureapp.app.az.AddSignature.SignatureActivity;
import com.signatureapp.app.az.utils.RecyclerViewEmptySupport;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class CompaniesActivity extends AppCompatActivity {


    RecyclerView recycleView;
    DatabaseReference databaseReference;

    FirebaseAuth auth;
    FirebaseRecyclerAdapter friendsConvAdapter;
    private RecyclerViewEmptySupport mRecyclerView;
    CardView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_companies);
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
            actionBar.setTitle((Html.fromHtml("<font color=\"#ffffff\">" + getString(R.string.companies) + "</font>")));
        }




        Query conversationQuery = databaseReference.child("Companies");


        FirebaseRecyclerOptions<Companies> options =
            new FirebaseRecyclerOptions.Builder<Companies>()
                .setQuery(conversationQuery, Companies.class)
                .build();

        //THIS GIVES THE CONTEXT TO GET THE LANG AND DO THE LIST OF INDUSTRIES
        Companies.initializeIndustries(getApplicationContext());

        FirebaseRecyclerAdapter friendsConvAdapter = new FirebaseRecyclerAdapter<Companies, Viewholder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull Viewholder viewholder, int i, @NonNull Companies model) {
                viewholder.nameTextview.setText(model.getName());
                viewholder.emailTextView.setText(model.getRepresentative());
                viewholder.industryTextView.setText(Companies.industries[model.getIndustry()]);
                /*viewholder.industryTextView.setText(model.getIndustry());//Companies.industries[Integer.parseInt(model.getIndustry())]);*/
                /*viewholder.sizeItemTimeTextView.setText(model.getRank());*/


                /*Log.e("hhhd", "onBindViewHolder: "+ model.getName());*/
            }

            @Override
            public Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_company, parent, false);

                return new Viewholder(view);
            }
        };
        InitRecycleViewer();

        friendsConvAdapter.startListening();
        recycleView.setAdapter(friendsConvAdapter);
        friendsConvAdapter.startListening();




    }

    private void InitRecycleViewer() {
        /*mRecyclerView = findViewById(R.id.mainRecycleView);
        mRecyclerView.setEmptyView(findViewById(R.id.toDoEmptyView));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(), DividerItemDecoration.VERTICAL));
        CreateDataSource();*/
    }


    static class Viewholder extends RecyclerView.ViewHolder {

        TextView nameTextview,industryTextView,emailTextView;


        public Viewholder(@NonNull View itemView)
        {
            super(itemView);

            nameTextview = itemView.findViewById(R.id.nameTextview);
            emailTextView = itemView.findViewById(R.id.emailTextView);
            industryTextView = itemView.findViewById(R.id.industryTextView);



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