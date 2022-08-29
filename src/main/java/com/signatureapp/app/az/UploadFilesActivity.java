package com.signatureapp.app.az;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.NavUtils;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.ktx.Firebase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class UploadFilesActivity extends AppCompatActivity {


    boolean imagePicked = false;
    FirebaseStorage storage;
    private StorageReference storageReference;
    String profile_link;
    FirebaseAuth firebaseAuth;
    public Uri ImageUri;
    FirebaseDatabase firebaseDatabase;
    ImageView pickImage;
    Button upload;
    DatabaseReference databaseReference,databaseReferenceUsers;

    ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_files);


        pd = new ProgressDialog(this);
        pd.setMessage("Uploading...");
        pickImage = findViewById(R.id.pickImage);
        upload = findViewById(R.id.upload);

        storage = FirebaseStorage.getInstance();
        storageReference=storage.getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase= FirebaseDatabase.getInstance();


        //Load CV
        databaseReferenceUsers  = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("info");

//        databaseReferenceUsers.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                if(snapshot.hasChild("userCV")){
//
//
//                    Log.e("pppp ", "onDataChange: "+snapshot.child("userCV").getValue().toString() );
//                    Picasso.get().load(snapshot.child("userCV").getValue().toString()).into(pickImage);
//
////                    WebView webView = (WebView) findViewById(R.id.web);
////                    webView.getSettings().setJavaScriptEnabled(true);
////                    webView.loadUrl(snapshot.child("userCV").getValue().toString());
//                    Intent intent = new Intent();
//                    intent.setDataAndType(Uri.parse(snapshot.child("userCV").getValue().toString()), "application/pdf");
//                    startActivity(intent);
//
//                }
//                else{
//                    choosePic();
//
//                }
//
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("Add Document");


        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                choosePic();


            }
        });






    }

    @Override
    protected void onActivityResult(int requestcode, int resultcode, @Nullable Intent data)
    {
        super.onActivityResult(requestcode,resultcode,data);

        if (requestcode==1 && resultcode==RESULT_OK && data!=null && data.getData()!=null)
        {
            ImageUri= data.getData();

            // pickImage.setImageURI(ImageUri);

            uploadPic();

        }
    }



    private void uploadPic() {


        if(ImageUri!=null){

            pd.show();
            String mfileName = getFileName(ImageUri);

            Toast.makeText(getApplicationContext(),"Uploading to Server",Toast.LENGTH_SHORT).show();

            storage = FirebaseStorage.getInstance();

            storageReference=storage.getReference();

            final String randomkey = UUID.randomUUID().toString();
            final StorageReference ref = storageReference.child("pdf_files/" + mfileName);
            ref.putFile(ImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri downloadUrl) {
                                    //pd.dismiss();


                                    Toast.makeText(getApplicationContext(),"Uploaded",Toast.LENGTH_SHORT).show();
                                    addDataToFirebase(downloadUrl.toString(),mfileName);
                                    //addDataToFirebase(pd);
                                    // addDataToFirebase(pd);
                                    //Toast.makeText(UploadFilesActivity.this, "CV uploaded !", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Something went wrong !", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            double progressPercent = (100.00 * snapshot.getBytesTransferred()
                                    / snapshot.getTotalByteCount());

                            //pd.setMessage("Percentage: " + (int) progressPercent + "%");

                        }
                    });
        }

    }


    private void addDataToFirebase(String download,String mfileName)
    {

        databaseReferenceUsers  = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getUid());

        String userID= FirebaseAuth.getInstance().getUid();


        String result = mfileName.substring(0, mfileName.indexOf("."));

        DatabaseReference databaseReferenceFiles  = FirebaseDatabase.getInstance().getReference().child("Files").child(result);

        databaseReferenceFiles.child("file_name").setValue(mfileName);
        databaseReferenceFiles.child("file_url").setValue(download);

        String uid=  FirebaseAuth.getInstance().getCurrentUser().getUid();

        databaseReferenceFiles.child("generatedBy").setValue(uid);
        databaseReferenceFiles.child("status").setValue("Pending");

//        DatabaseReference dd = databaseReferenceFiles.child("SignedBy").push();
//
//        dd.child("userId").setValue(FirebaseAuth.getInstance().getUid());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
        String currentDateandTime = sdf.format(new Date());

        databaseReferenceFiles.child("date").setValue(currentDateandTime);

        databaseReferenceUsers.child("pdf").setValue(mfileName).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

                pd.dismiss();
                 Toast.makeText(getApplicationContext(),"Done",Toast.LENGTH_SHORT).show();


                Intent intent=new Intent(UploadFilesActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });






    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }



    private void choosePic() {
        Intent intent;
        intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        startActivityForResult(intent,1);
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