package com.example.firebasechat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firebasechat.apibot.ChatterBot;
import com.example.firebasechat.apibot.ChatterBotFactory;
import com.example.firebasechat.apibot.ChatterBotSession;
import com.example.firebasechat.apibot.ChatterBotType;
import com.example.firebasechat.receiver.ConnectivityStateReceiver;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;

public class ChatActivity extends AppCompatActivity implements TextToSpeech.OnInitListener{

    private static final int REQUEST_CODE_SPEECH_INPUT = 1000;
    private String url = "https://www.bing.com/ttranslatev3";
    private static final String TAG = "TTS";

    private String chatPersona = "";
    private String chatBot ="";
    private String respuesta = "";

    private String inglesBot = "";
    private String inglesPersona = "";

    private TextToSpeech textToSpeech;

    private Button btMicro;
    private TextView tvPersona;
    private TextView tvBot;

    public static FirebaseDatabase firebaseDatabase;
    public static DatabaseReference databaseReference;
    public static String fecha;
    public static String fecha2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        initFirebase();
        initComponents();
    }

    private void initFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    private void initUid() {
        Log.v("UID", "initUid");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        //database.setPersistenceEnabled(true);
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference referenciaItem = database.getReference("user/"+uid);
        referenciaItem.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.v("UID", "data changed" + dataSnapshot.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.v("UID", "error " + databaseError.toException());
            }
        });
    }

    private void initComponents() {
        btMicro = findViewById(R.id.btMicro);
        tvPersona = findViewById(R.id.tvPersona);
        tvBot = findViewById(R.id.tvBot);

        initUid();

        textToSpeech = new TextToSpeech(this,
                this
        );

        btMicro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recogerHabla();
            }
        });
    }

    private void init() {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();
        fecha = dateFormat.format(date);

        Calendar cal = new GregorianCalendar();
        int hora, minutos, segundos;

        hora =cal.get(Calendar.HOUR_OF_DAY);
        minutos = cal.get(Calendar.MINUTE);
        segundos = cal.get(Calendar.SECOND);

        fecha2 = hora + ":" + minutos + ":" + segundos;

        Sentence chatSentence = new Sentence(inglesPersona,tvPersona.getText().toString(),"Humano",fecha2);
        Sentence chatSentence2 = new Sentence(inglesBot,tvBot.getText().toString(),"Bot",fecha2);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference(FirebaseAuth.getInstance().getUid()).child(fecha);
        databaseReference.child(databaseReference.push().getKey()).setValue(chatSentence);
        databaseReference.child(databaseReference.push().getKey()).setValue(chatSentence2);

        /*
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();

        Calendar cal = new GregorianCalendar();
        int hora, minutos, segundos;

        hora =cal.get(Calendar.HOUR_OF_DAY);
        minutos = cal.get(Calendar.MINUTE);
        segundos = cal.get(Calendar.SECOND);

        fecha = dateFormat.format(date);
        fecha2 = hora + ":" + minutos + ":" + segundos;

        Sentence chatSentence = new Sentence(inglesPersona,tvPersona.getText().toString(),"Humano",fecha2);
        Sentence chatSentence2 = new Sentence(inglesBot,tvBot.getText().toString(),"Bot",fecha2);
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> map2 = new HashMap<>();
        String key = databaseReference.child(fecha).push().getKey();
        String key2 = databaseReference.child(fecha).push().getKey();
        //String key = referenciaItemunixtime.push().getKey();
        map.put(fecha + "/" + key, chatSentence.toMap());
        map2.put(fecha + "/" + key2, chatSentence2.toMap());
        databaseReference.updateChildren(map);
        databaseReference.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.v(TAG, "task succesfull");
                } else {
                    Log.v(TAG, task.getException().toString());
                }
            }
        });
        databaseReference.updateChildren(map2);
        databaseReference.updateChildren(map2).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.v(TAG, "task succesfull");
                } else {
                    Log.v(TAG, task.getException().toString());
                }
            }
        });*/


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.v(TAG, "data changed: " + dataSnapshot.toString());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.v(TAG, "error: " + databaseError.toException());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.historial:
                Intent intent = new Intent(ChatActivity.this, RecordActivity.class);
                startActivity(intent);
                return true;
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                Intent intent2 = new Intent(ChatActivity.this, MainActivity.class);
                startActivity(intent2);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void recogerHabla() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hable ahora");

        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
        }catch (Exception e){
            Toast.makeText(this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
        }

    }

    private void chat() {
        try {
            ChatterBotFactory factory = new ChatterBotFactory();
            ChatterBot bot1 = factory.create(ChatterBotType.PANDORABOTS, "b0dafd24ee35a477");
            ChatterBotSession bot1session = bot1.createSession();
            String r = chatPersona;
            String parametros = conversacion("es", "en", r);
            r = postHttps(url, parametros);
            r = filterTranslation(r);
            inglesPersona = r;

            r = bot1session.think(r);
            parametros = conversacion("en", "es", r);
            inglesBot = r;
            r = postHttps(url, parametros);
            r = filterTranslation(r);
            chatBot = r;

        } catch (Exception e) {

        }
    }

    private class Chat extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            chat();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    textoChat(chatBot);
                }
            });
            return null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

    }

    private void textoChat(String respuesta) {
        tvBot.setVisibility(View.VISIBLE);
        tvBot.setText(respuesta);
        this.respuesta = respuesta;
        init();
        sayText();
    }

    public String conversacion(String original, String traducido, String texto) {
        try {
            HashMap<String, String> httpBodyParams;
            httpBodyParams = new HashMap<>();
            httpBodyParams.put("fromLang", original);
            httpBodyParams.put("to", traducido);
            httpBodyParams.put("text", texto);

            StringBuilder result = new StringBuilder();
            boolean first = true;
            for (Map.Entry<String, String> entry : httpBodyParams.entrySet()) {
                if (first)
                    first = false;
                else
                    result.append("&");
                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            }
            return result.toString();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String postHttps(String src, String body) {
        StringBuffer buffer = new StringBuffer();
        try {
            URL url = new URL(src);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Accept", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.connect();
            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
            out.write(body);
            out.flush();
            out.close();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                buffer.append(line + "\n");
            }
            in.close();
        } catch (IOException e) {
        }
        return buffer.toString();
    }

    public String filterTranslation(String cadena){
        String resultado = "";
        JSONArray jArray = null;
        JSONObject jObject = null;


        try {
            //Obtenemos la única respuesta que tenemos
            jArray = new JSONArray(cadena);
            cadena = jArray.get(0).toString();


            jObject = new JSONObject(cadena);
            cadena = jObject.get("translations").toString();

            jArray = new JSONArray(cadena);
            cadena = jArray.get(0).toString();

            jObject = new JSONObject(cadena);
            cadena = jObject.get("text").toString();
            resultado = cadena;

        } catch (JSONException e) {
            Log.v("---error---", e.toString());
        }

        return resultado;
    }

    @Override
    public void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }

    public void onInit(int status) {
        Locale spanish = new Locale ("es","ES");
        if (status == TextToSpeech.SUCCESS) {
            int result = textToSpeech.setLanguage(spanish);
            if (result == TextToSpeech.LANG_MISSING_DATA ||
                    result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e(TAG, "Language is not available.");
            } else {
                sayText();
            }
        } else {
            Log.e(TAG, "Could not initialize TextToSpeech.");
        }
    }

    private static final Random RANDOM = new Random();
    private void sayText() {
        textToSpeech.setPitch(-500);
        textToSpeech.speak(respuesta,
                TextToSpeech.QUEUE_FLUSH,
                null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode){
            case REQUEST_CODE_SPEECH_INPUT:{
                if(resultCode == RESULT_OK && data != null){
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    tvPersona.setVisibility(View.VISIBLE);
                    tvPersona.setText(result.get(0));
                    chatPersona = result.get(0);
                    if (!chatPersona.equalsIgnoreCase("")) {
                        new Chat().execute();
                    }
                }
                break;
            }
        }
    }

    private ConnectivityStateReceiver connectivityStateReceiver;

    @Override
    protected void onResume() {
        super.onResume();
        connectivityStateReceiver = new ConnectivityStateReceiver();
        IntentFilter intent = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(connectivityStateReceiver, intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(connectivityStateReceiver);
    }
}
