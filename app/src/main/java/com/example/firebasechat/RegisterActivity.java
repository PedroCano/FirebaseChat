package com.example.firebasechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    EditText etUsuario, etContraseña;
    Button btRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initComponents();
    }

    private void initComponents() {
        etUsuario = findViewById(R.id.etUsuarioRegister);
        etContraseña = findViewById(R.id.etContraseñaRegister);
        btRegister = findViewById(R.id.btRegister);

        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initUser();
            }
        });
    }

    private void initUser() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if(!etUsuario.getText().toString().equalsIgnoreCase("")) {
            if(!etContraseña.getText().toString().equalsIgnoreCase("")) {
                firebaseAuth.createUserWithEmailAndPassword(etUsuario.getText().toString(), etContraseña.getText().toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.v("usuario", "usuario creado correctamente");
                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            Log.v("usuario", task.getException().toString());
                            Toast.makeText(RegisterActivity.this, "Fallo al introducir datos", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }else{
                Toast.makeText(RegisterActivity.this, "Introduzca su contraseña por favor", Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(RegisterActivity.this, "Introduzca el usuario por favor", Toast.LENGTH_LONG).show();
        }
    }
}
