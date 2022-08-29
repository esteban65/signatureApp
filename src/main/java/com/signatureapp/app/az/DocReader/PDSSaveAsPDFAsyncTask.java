package com.signatureapp.app.az.DocReader;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ktx.Firebase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSignatureAppearance;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.security.BouncyCastleDigest;
import com.itextpdf.text.pdf.security.DigestAlgorithms;
import com.itextpdf.text.pdf.security.ExternalDigest;
import com.itextpdf.text.pdf.security.ExternalSignature;
import com.itextpdf.text.pdf.security.MakeSignature;
import com.itextpdf.text.pdf.security.PrivateKeySignature;
import com.signatureapp.app.az.DigitalSignatureActivity;
import com.signatureapp.app.az.MainActivity;
import com.signatureapp.app.az.PDF.PDSPDFDocument;
import com.signatureapp.app.az.PDF.PDSPDFPage;
import com.signatureapp.app.az.PDSModel.PDSElement;
import com.signatureapp.app.az.utils.ViewUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class PDSSaveAsPDFAsyncTask extends AsyncTask<Void, Void, Boolean> {

    private String mfileName;
    DigitalSignatureActivity mCtx;
    private StorageReference storageReference;
    File file;

    public PDSSaveAsPDFAsyncTask(DigitalSignatureActivity context, String str) {
        this.mCtx = context;
        this.mfileName = str;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mCtx.savingProgress.setVisibility(View.VISIBLE);
    }

    @Override
    public Boolean doInBackground(Void... voidArr) {
        if (Looper.myLooper() == null) {
            Looper.prepare();
        }
        PDSPDFDocument document = mCtx.getDocument();
        File root = mCtx.getFilesDir();

        File myDir = new File(root + "/SignatureApp");
        if (!myDir.exists()) {
            myDir.mkdirs();
        }
        file = new File(myDir.getAbsolutePath(), mfileName);
        if (file.exists())
            file.delete();
        try {
            InputStream stream = document.stream;
            FileOutputStream os = new FileOutputStream(file);
            PdfReader reader = new PdfReader(stream);
            PdfStamper signer = null;
            Bitmap createBitmap = null;
            for (int i = 0; i < document.getNumPages(); i++) {
                Rectangle mediabox = reader.getPageSize(i + 1);
                for (int j = 0; j < document.getPage(i).getNumElements(); j++) {
                    PDSPDFPage page = document.getPage(i);
                    PDSElement element = page.getElement(j);
                    RectF bounds = element.getRect();
                    if (element.getType() == PDSElement.PDSElementType.PDSElementTypeSignature) {
                        PDSElementViewer viewer = element.mElementViewer;
                        View dummy = viewer.getElementView();
                        View view = ViewUtils.createSignatureView(mCtx, element, viewer.mPageViewer.getToViewCoordinatesMatrix());
                        createBitmap = Bitmap.createBitmap(dummy.getWidth(), dummy.getHeight(), Bitmap.Config.ARGB_8888);
                        view.draw(new Canvas(createBitmap));
                    } else {
                        createBitmap = element.getBitmap();
                    }
                    ByteArrayOutputStream saveBitmap = new ByteArrayOutputStream();
                    createBitmap.compress(Bitmap.CompressFormat.PNG, 100, saveBitmap);
                    byte[] byteArray = saveBitmap.toByteArray();
                    createBitmap.recycle();

                    Image sigimage = Image.getInstance(byteArray);
                    if (mCtx.alises != null && mCtx.keyStore != null && mCtx.mdigitalIDPassword != null) {
                        KeyStore ks = mCtx.keyStore;
                        String alias = mCtx.alises;
                        PrivateKey pk = (PrivateKey) ks.getKey(alias, mCtx.mdigitalIDPassword.toCharArray());
                        Certificate[] chain = ks.getCertificateChain(alias);
                        if (signer == null)
                            signer = PdfStamper.createSignature(reader, os, '\0');

                        PdfSignatureAppearance appearance = signer.getSignatureAppearance();

                        float top = mediabox.getHeight() - (bounds.top + bounds.height());
                        appearance.setVisibleSignature(new Rectangle(bounds.left, top, bounds.left + bounds.width(), top + bounds.height()), i + 1, "sig" + j);
                        appearance.setRenderingMode(PdfSignatureAppearance.RenderingMode.GRAPHIC);
                        appearance.setSignatureGraphic(sigimage);
                        ExternalDigest digest = new BouncyCastleDigest();
                        ExternalSignature signature = new PrivateKeySignature(pk, DigestAlgorithms.SHA256, null);
                        MakeSignature.signDetached(appearance, digest, signature, chain, null, null, null, 0, MakeSignature.CryptoStandard.CADES);


                    } else {
                        if (signer == null)
                            signer = new PdfStamper(reader, os, '\0');
                        PdfContentByte contentByte = signer.getOverContent(i + 1);
                        sigimage.setAlignment(Image.ALIGN_UNDEFINED);
                        sigimage.scaleToFit(bounds.width(), bounds.height());
                        sigimage.setAbsolutePosition(bounds.left - (sigimage.getScaledWidth() - bounds.width()) / 2, mediabox.getHeight() - (bounds.top + bounds.height()));
                        contentByte.addImage(sigimage);
                    }
                }
            }


            if (signer != null)
                signer.close();
            if (reader != null)
                reader.close();
            if (os != null)
                os.close();


        } catch (Exception e) {
            e.printStackTrace();
            if (file.exists()) {
                file.delete();
            }
            return false;
        }
        return true;
    }

    @Override
    public void onPostExecute(Boolean result) {

        if (!result) {
            Toast.makeText(mCtx, "Something went wrong while Signing PDF document, Please try again", Toast.LENGTH_LONG).show();
            mCtx.runPostExecution();

        }else {


            uploadPic();
            Toast.makeText(mCtx, "PDF document saved successfully", Toast.LENGTH_LONG).show();

        }



    }
    FirebaseStorage storage;

    private void uploadPic() {


        if(file.exists()){


            Uri fileUri = Uri.fromFile(file);

            Toast.makeText(mCtx,"Uploading to Server",Toast.LENGTH_SHORT).show();

            storage = FirebaseStorage.getInstance();

            storageReference=storage.getReference();

            final String randomkey = UUID.randomUUID().toString();
            final StorageReference ref = storageReference.child("pdf_files/" + mfileName);
            ref.putFile(fileUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri downloadUrl) {
                                    //pd.dismiss();


                                    Toast.makeText(mCtx,"Uploaded",Toast.LENGTH_SHORT).show();
                                    addDataToFirebase(downloadUrl.toString());
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
                            Toast.makeText(mCtx, "Something went wrong !", Toast.LENGTH_SHORT).show();
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
    FirebaseDatabase firebaseDatabase;
    ImageView pickImage;
    DatabaseReference databaseReferenceUsers,databaseReferenceFiles;
    private void addDataToFirebase(String download)
    {

        databaseReferenceUsers  = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getUid());

        String userID= FirebaseAuth.getInstance().getUid();


        String result = mfileName.substring(0, mfileName.indexOf("."));

        databaseReferenceFiles  = FirebaseDatabase.getInstance().getReference().child("Files").child(result);

        databaseReferenceFiles.child("file_name").setValue(mfileName);
        databaseReferenceFiles.child("file_url").setValue(download);
        databaseReferenceFiles.child("status").setValue("approved");

        DatabaseReference dd = databaseReferenceFiles.child("SignedBy").push();

        dd.child("userId").setValue(FirebaseAuth.getInstance().getUid());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
        String currentDateandTime = sdf.format(new Date());

        databaseReferenceFiles.child("date").setValue(currentDateandTime);

        databaseReferenceUsers.child("pdf").setValue(mfileName).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

               // Toast.makeText(mCtx,"Done",Toast.LENGTH_SHORT).show();

                mCtx.runPostExecution();

//                Intent intent=new Intent(UploadFilesActivity.this, MainActivity.class);
//                startActivity(intent);
//                finish();
            }
        });






    }

}

