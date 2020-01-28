package com.example.firebasechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private TextView etUsuario, etContraseña;
    private Button btLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initComponents();
        //initUser();
        //initUid();
    }

    private void initComponents() {
        etUsuario = findViewById(R.id.etUsuario);
        etContraseña = findViewById(R.id.etContraseña);
        btLogin = findViewById(R.id.btLogin);

        FirebaseAuth.getInstance().signOut();

        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initLogin();
            }
        });
    }

    private void initLogin() {
        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if(!etUsuario.getText().toString().equalsIgnoreCase("")){
            if(!etContraseña.getText().toString().equalsIgnoreCase("")) {
                firebaseAuth.signInWithEmailAndPassword(etUsuario.getText().toString(), etContraseña.getText().toString()).
                        addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Log.v("///////////////////////", "Usuario logueado correctamente");
                                    //initUid();
                                    Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                                    startActivity(intent);

                                } else {
                                    Log.v("///////////////////////", task.getException().toString());
                                    Toast.makeText(MainActivity.this, "Fallo al introducir datos", Toast.LENGTH_LONG).show();
                                    return;
                                }
                            }
                        });
            }else {
                Toast.makeText(MainActivity.this, "Introduzca su contraseña por favor", Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(MainActivity.this, "Introduzca su usuario por favor", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_register, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.resgister:
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
