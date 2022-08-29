package com.signatureapp.app.az;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.HashMap;


public class RegisterActivity extends AppCompatActivity {


    FirebaseAuth auth;
    ProgressDialog progressDialog;
    TextView loginn;

    String str_rank;
    CheckBox checkbox;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait....");

        auth = FirebaseAuth.getInstance();
        Button register=findViewById(R.id. register);

        loginn=findViewById(R.id. loginn);
        checkbox=findViewById(R.id. checkbox);

        final EditText email = findViewById(R.id.email);
        final EditText password = findViewById(R.id.password);
        final EditText name = findViewById(R.id.name);

        MaterialSpinner spinner = (MaterialSpinner) findViewById(R.id.spinner);

        spinner.setItems("Select Rank","Supervisor de trabajo",
                "Cordinador de Area",
                "Coordinador PepsiCo",
                "Calidad",
                "SASS",
                "Prevencionista externo",
                "Seguridad Patrimonial",
                "V°B° Area Técnica",
                "Personal médico");
        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                Snackbar.make(view,  item+" Selected", Snackbar.LENGTH_LONG).show();

                str_rank = item;
            }
        });

        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if(b){
                    spinner.setEnabled(false);

                }else{
                    spinner.setEnabled(true);

                }

            }
        });


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String str_email = email.getText().toString();
                String str_password = password.getText().toString();
                String str_name = name.getText().toString();


                if(!checkbox.isChecked()){
                    //checks
                    if (TextUtils.isEmpty(str_email))
                    {
                        email.setError("Email is Required!");
                    }

                    else if (TextUtils.isEmpty(str_password))
                    {
                        password.setError("Password is Empty!");
                    }else if (TextUtils.isEmpty(str_name))
                    {
                        email.setError("Email is Required!");
                    }
                    else if (TextUtils.isEmpty(str_rank) || str_rank.equals("Select Rank"))
                    {
                        email.setError("Rank is Required!");
                    } else
                    {
                        register( str_email, str_password,str_name,str_rank);
                    }
                }else {



                    if (TextUtils.isEmpty(str_email))
                    {
                        email.setError("Email is Required!");
                    }

                    else if (TextUtils.isEmpty(str_password))
                    {
                        password.setError("Password is Empty!");
                    }
                    else if (TextUtils.isEmpty(str_name))
                    {
                        email.setError("Email is Required!");
                    }
                    else
                    {
                        register( str_email, str_password,str_name,"worker");
                    }

                }




            }
        });


        loginn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });



    }

    //register user
    private void register(final String email, final String password,String name, String rank)
    {

        progressDialog.show();
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            String userid = firebaseUser.getUid();

                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                                    .child("Users")
                                    .child(userid);


                            //add values in database
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("id", userid);
                            hashMap.put("imageurl", "https://www.linkpicture.com/q/user_oiq1.png");
                            hashMap.put("email", email);
                            hashMap.put("name", name);
                            hashMap.put("rank", rank);

                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                    {
                                        progressDialog.dismiss();

                                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }
                                    else
                                    {

                                        progressDialog.dismiss();

                                        Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });

                        }
                        else
                        {

                            progressDialog.dismiss();

                            Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}