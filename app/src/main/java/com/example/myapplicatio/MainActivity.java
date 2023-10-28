package com.example.myapplicatio;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Firebase;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    Button btnLogOut,btnImage,btnSubmit,uploadImage;
    TextView txtUser;
    TextInputEditText title, description;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseFirestore db;
    FirebaseStorage storage;
    StorageReference storageReference;
    Uri imageUri;
    ImageView imageView;
    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                if (result.getData() != null) {
                    btnImage.setEnabled(true);
                    imageUri = result.getData().getData();
                    Glide.with(getApplicationContext()).load(imageUri).into(imageView);
                }
            } else {
                Toast.makeText(MainActivity.this, "Please select an image", Toast.LENGTH_SHORT).show();
            }
        }
    });
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

FirebaseApp.initializeApp(MainActivity.this);
storageReference = FirebaseStorage.getInstance().getReference();

        title = findViewById(R.id.title);
        description = findViewById(R.id.description);
        btnSubmit = findViewById(R.id.btnSubmit);

        btnImage = findViewById(R.id.btnImage);
        uploadImage = findViewById(R.id.uploadImage);
        imageView = findViewById(R.id.imageView);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

//        StorageReference storageRef = storage.getReference();
//        StorageReference imageRef = storageRef.child("images");
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String titleOf, descriptionOf;

                titleOf = String.valueOf(title.getText());
                descriptionOf = String.valueOf(description.getText());

                HashMap<String,Object> data = new HashMap<>();
                data.put("title", titleOf);
                data.put("description", descriptionOf);

                if(TextUtils.isEmpty(titleOf)){
                    Toast.makeText(MainActivity.this,"Enter title first",Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(descriptionOf)){
                    Toast.makeText(MainActivity.this,"Enter Description first",Toast.LENGTH_SHORT).show();
                }else if (!(titleOf.isEmpty() && descriptionOf.isEmpty())){
                    db.collection("data")
                            .add(data)
                            .addOnSuccessListener(new OnSuccessListener() {
                                @Override
                                public void onSuccess(Object o) {
                                    Toast.makeText(MainActivity.this,"Submitted successfully",Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(MainActivity.this,"Failed to submit",Toast.LENGTH_SHORT).show();
                                }
                            });
                }

            }
        });
        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
                activityResultLauncher.launch(intent);

            }
        });
uploadImage.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        uploadImages(imageUri);
    }
});






//        db.collection("data")
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//
//                            }
//                        } else {
//
//                        }
//                    }
//                });



        firebaseAuth = FirebaseAuth.getInstance();

        btnLogOut = findViewById(R.id.btnLogOut);
        txtUser = findViewById(R.id.txtUser);

        user = firebaseAuth.getCurrentUser();

        if (user == null ) {
            Intent intent = new Intent(getApplicationContext(), login.class);
            startActivity(intent);
            finish();
        }
        else {
            txtUser.setText(user.getEmail());
        }

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), login.class);
                startActivity(intent);
                finish();

            }
        });

    }



    private void uploadImages(Uri file) {

        StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());
        ref.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(MainActivity.this, "Image Uploaded!!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Failed to Upload image!" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
