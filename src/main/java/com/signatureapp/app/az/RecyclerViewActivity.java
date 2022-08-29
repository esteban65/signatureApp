package com.signatureapp.app.az;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class RecyclerViewActivity extends AppCompatActivity {



    RecyclerView recycleView;

    DatabaseReference databaseReference;

    FirebaseAuth auth;
    FirebaseRecyclerAdapter friendsConvAdapter;
    CardView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        recycleView = findViewById(R.id.RecycleList);
//        back = findViewById(R.id.back);


        recycleView.setHasFixedSize(true);
        recycleView.setLayoutManager(new LinearLayoutManager(this));







        Query conversationQuery = databaseReference.child("Files");


        FirebaseRecyclerOptions<FIleModel> options =
                new FirebaseRecyclerOptions.Builder<FIleModel>()
                        .setQuery(conversationQuery, FIleModel.class)
                        .build();

        FirebaseRecyclerAdapter friendsConvAdapter = new FirebaseRecyclerAdapter<FIleModel, Viewholder>(options) {
            @Override
            public Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.mainitemgrid, parent, false);

                return new Viewholder(view);
            }

            @Override
            protected void onBindViewHolder(Viewholder viewholder, int position, FIleModel model) {

                viewholder.fileItemTextview.setText(model.getFile_name());

                viewholder.fileItemTextview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        downloadInLocalFile(model.getFile_name());

                    }
                });











            }
        };


        friendsConvAdapter.startListening();
        recycleView.setAdapter(friendsConvAdapter);
        friendsConvAdapter.startListening();




    }

    static class Viewholder extends RecyclerView.ViewHolder {

        TextView fileItemTextview;


        public Viewholder(@NonNull View itemView)
        {
            super(itemView);

            fileItemTextview = itemView.findViewById(R.id.fileItemTextview);



        }
    }


    private StorageReference storageReference;

    private void downloadInLocalFile(String filename) {

        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference storageRef = storage.getReference();

        storageReference = storageRef.child("pdf_files/"+filename);

        File dir = new File(Environment.getExternalStorageDirectory() + "/SignatureApp");
        final File file = new File(dir, filename);
        try {
            if (!dir.exists()) {
                dir.mkdir();
            }
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        final FileDownloadTask fileDownloadTask = storageReference.getFile(file);

        fileDownloadTask.addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {


                Intent target = new Intent(Intent.ACTION_VIEW);
                Uri contentUri = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", file);
                target.setDataAndType(contentUri, "application/pdf");
                target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                Intent intent = Intent.createChooser(target, "Open File");
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    //Snackbar.make(mCoordLayout, "Install PDF reader application.", Snackbar.LENGTH_LONG).show();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

            }
        }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                int progress = (int) ((100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount());
//                Helper.setProgress(progress);

                Toast.makeText(getApplicationContext(),String.valueOf(progress),Toast.LENGTH_SHORT).show();
            }
        });
    }




    @Override
    protected void onStart() {
        super.onStart();
        // friendsConvAdapter.startListening();

    }

}