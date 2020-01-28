package com.example.firebasechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RecordActivity extends AppCompatActivity {

    private List<Sentence> sentenceList = new ArrayList<>();
    ArrayAdapter<Sentence> arrayAdapter;

    private DatabaseReference databaseReference;
    private String fecha = ChatActivity.fecha;

    private ListView lvRecords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        initComponents();
        listarDatos();
    }

    private void initComponents() {
        lvRecords = findViewById(R.id.lvRecords);
    }

    private void listarDatos() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        databaseReference = database.getReference(FirebaseAuth.getInstance().getUid()).child(fecha);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                sentenceList.clear();
                for(DataSnapshot objSnapshot: dataSnapshot.getChildren()){
                    Sentence s = objSnapshot.getValue(Sentence.class);
                    sentenceList.add(s);

                    arrayAdapter = new ArrayAdapter<Sentence>(RecordActivity.this, android.R.layout.simple_list_item_1, sentenceList);
                    lvRecords.setAdapter(arrayAdapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
