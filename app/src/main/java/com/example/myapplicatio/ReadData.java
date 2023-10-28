package com.example.myapplicatio;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;

import java.util.HashMap;
import java.util.Map;

public class ReadData extends MainActivity{
    TextInputEditText title, description;
    Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        title = findViewById(R.id.title);
        description = findViewById(R.id.description);
        btnSubmit = findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String titleOf, descriptionOf;

                titleOf = String.valueOf(title.getText());
                descriptionOf = String.valueOf(description.getText());

                Map<String, Object> data = new HashMap<>();
                data.put("title", titleOf);
                data.put("description", descriptionOf);

                if(TextUtils.isEmpty(titleOf)){
                    Toast.makeText(ReadData.this,"Enter title first",Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(descriptionOf)){
                    Toast.makeText(ReadData.this,"Enter Description first",Toast.LENGTH_SHORT).show();
                }else if (!(titleOf.isEmpty() && descriptionOf.isEmpty())){
                    db.collection("data")
                            .add(data)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Toast.makeText(ReadData.this,"Submitted successfully",Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(ReadData.this,"Failed to submit",Toast.LENGTH_SHORT).show();
                                }
                            });
                }

            }
        });
    }
}
