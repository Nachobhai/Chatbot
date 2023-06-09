//package com.example.chatgpt;
//
//
//import android.content.Intent;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.speech.RecognizerIntent;
//import android.text.Editable;
//import android.text.TextWatcher;
//import android.text.method.ScrollingMovementMethod;
//import android.util.Log;
//import android.view.KeyEvent;
//import android.view.View;
//import android.widget.EditText;
//import android.widget.ImageButton;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//import android.widget.Button;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.android.volley.RequestQueue;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.StringRequest;
//import com.android.volley.toolbox.Volley;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Locale;
//import java.util.Objects;
//
//import okhttp3.Call;
//import okhttp3.Callback;
//import okhttp3.MediaType;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.RequestBody;
//import okhttp3.Response;
//
//public class MainActivity extends AppCompatActivity {
//    RecyclerView recyclerView;
////    TextView welcomeTextView;
////    TextView welcomeTextView2;
//    EditText messageEditText;
//    ImageButton sendButton;
//
//    List<Message> messageList;
//    MessageAdapter messageAdapter;
//    ImageView iv_mic;
//
//
//    public static final MediaType JSON
//            =MediaType.get("application/json; charset=utf-8");
//    OkHttpClient client = new OkHttpClient();
//
//        private TextView tv_Speech_to_text;
//        private static final int REQUEST_CODE_SPEECH_INPUT = 1000;
//        private ImageView logoIV;
//        private ImageView sendBtn;
//        private ImageButton refreshBtn;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState){
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//
//        messageList = new ArrayList<>();
//
//        iv_mic = findViewById(R.id.iv_mic);
//        iv_mic.setBackgroundColor(getResources().getColor(android.R.color.transparent));
//        sendBtn = findViewById(R.id.send_btn);
//        sendBtn.setBackgroundColor(getResources().getColor(android.R.color.transparent));
//
//        logoIV = findViewById(R.id.iv_mic);
//
//
//        refreshBtn = findViewById(R.id.refreshBtn);
//        refreshBtn.setBackgroundColor(getResources().getColor(android.R.color.transparent));
//        refreshBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(MainActivity.this, MainActivity.class);
//                finish();
//                overridePendingTransition(0, 0);
//                startActivity(i);
//                overridePendingTransition(0, 0);
//            }
//        });
//
//
//
////        R.string.CHATHEADING
//
//        iv_mic.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v)
//            {
//                speak();
//            }
//        });
////        EditText editText = (EditText) findViewById(R.id.message_edit_text);
//        messageEditText = findViewById(R.id.message_edit_text);
////        editText.setOnKeyListener(new View.OnKeyListener() {
////            @Override
////            public boolean onKey(View v, int keyCode, KeyEvent event) {
////                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_SPACE)) {
////                    Toast.makeText(MainActivity.this, "Onkey", Toast.LENGTH_SHORT).show();
////                    return true;
////                }
////                return false;
////            }
////        });
//
//        messageEditText.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                Toast.makeText(getBaseContext(), "keyCode :: " + keyCode, Toast.LENGTH_LONG).show();
//                return false;
//            }
//        });
//
//        messageEditText.addTextChangedListener(new TextWatcher() {
//            int c=0;
//            @Override
//            public void beforeTextChanged (CharSequence s,int start, int count,
//                                           int after){
//
//
//            }
//            @Override
//            public void onTextChanged ( final CharSequence s, int start, int before,
//                                        int count){
//                c = count;
//                if (s.length() > 0) {
//                    if (c>0 && s.charAt(s.length() - 1) == ' '){
//                        showToast("space");
//                    }
//                } else {
//
//                }
//
//
//            }
//
//            @Override
//            public void afterTextChanged ( final Editable s){
//
//                if (s.length() > 0) {
//                    if (c>0 && s.charAt(s.length() - 1) == ' '){
//
//                        if (!s.toString().trim().contains(" ")){
//                            messageEditText.setText("Change");
//                            messageEditText.setSelection(messageEditText.getText().length());
//                        }else{
//                            String[] wordArray = s.toString().trim().split(" ");
//                            String s2 = wordArray[wordArray.length - 1].toString();
//                            String updated = "";
//                            for(int i=0; i<wordArray.length - 1; i++){
//                                updated = updated + wordArray[i] + " ";
//                            }
//                            messageEditText.setText(updated.trim() + " " + "Change2");
//                        }
//
//
//
//                    }
//                } else {
//
//                }
//            }
//        });
//
//
//
//        recyclerView = findViewById(R.id.recycler_view);
////        welcomeTextView = findViewById(R.id.welcome_text);
////        welcomeTextView.setMovementMethod(new ScrollingMovementMethod());
////
////        welcomeTextView2 = findViewById(R.id.welcome_text);
////        welcomeTextView2.setMovementMethod(new ScrollingMovementMethod());
//
//        sendButton = findViewById(R.id.send_btn);
//
//        messageAdapter = new MessageAdapter(messageList);
//        recyclerView.setAdapter(messageAdapter);
//        LinearLayoutManager llm = new LinearLayoutManager(this );
//        llm.setStackFromEnd(true);
//        recyclerView.setLayoutManager(llm);
//
//        sendButton.setOnClickListener((v)->{
//            String question = messageEditText.getText().toString().trim();
//            addToChat(question,Message.SENT_BY_ME);
//            messageEditText.setText("");
//            callAPI(question);
//            OkHttpHandler okHttpHandler = new OkHttpHandler();
//            okHttpHandler.execute("http://10.10.10.214:8083/hi_translit/abc");
//        });
//
//    }
//
//    private void showToast(String space_key_pressed) {
//        Toast.makeText(this, space_key_pressed, Toast.LENGTH_SHORT).show();
//    }
//
//    void setBtnReset(View v){
//    }
//    public class OkHttpHandler extends AsyncTask<String, String, String> {
//
//        OkHttpClient client = new OkHttpClient();
//
//        @Override
//        protected String doInBackground(String... params) {
//
//            Request.Builder builder = new Request.Builder();
//            builder.url(params[0]);
//            Request request = builder.build();
//
//            try {
//                Response response = client.newCall(request).execute();
//                return response.body().string();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//            System.out.println(s);
//            addResponse(s);
//        }
//
//    }
//    void callAPI(String question){
//        messageList.add(new Message("Typing...", Message.SENT_BY_BOT));
////        JSONObject jsonBody = new JSONObject();
////        try {
////            jsonBody.put("model", "text-davinci-003");
////            jsonBody.put("prompt", question);
////            jsonBody.put("max_tokens", 4000);
////            jsonBody.put("temperature", 0);
////        } catch (JSONException e) {
////            throw new RuntimeException(e);
//////        }
////        RequestBody body = RequestBody.create(jsonBody.toString(), JSON);
//        Request request = new Request.Builder()
//                .url("http://10.10.10.214:8083/hi_translit/" +question )
//                .build();
//        try {
//            Response response = client.newCall(request).execute();
//            System.out.println("response :: " + response);
////            addResponse(response);
//        } catch (IOException e) {
//            e.printStackTrace();
////            throw new RuntimeException(e);
//        }
//
//
//
////        client.newCall(request).enqueue(new Callback() {
////            @Override
////            public void onFailure(@NonNull Call call, @NonNull IOException e) {
////                addResponse("Failed to load response due to "+e.getMessage());
////            }
////
////
////
////            @Override
////            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
////                if(response.isSuccessful()){
//////                    JSONObject jsonObject = null;
//////                    try {
//////                        //jsonObject = new JSONObject(response.body().string());
//////                        JSONArray jsonArray = jsonObject.getJSONArray("choices");
//////                        String result = jsonArray.getJSONObject(0).getString("text");
//////                        addResponse(result.trim());
//////                    } catch (JSONException e) {
//////                        throw new RuntimeException(e);
//////                    }
////                    System.out.println(response);
////                }else{
////                    addResponse("Failed to load response due to "+response.body().toString());
////                }
////            }
////        });
//
//    }
//
//
//    private void speak() {
//        Intent intent
//                = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
//        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
//                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
//        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
//        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hi speak something");
//
//
//        try {
//            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
//        }
//        catch (Exception e) {
//            Toast
//                    .makeText(MainActivity.this, " " + e.getMessage(),
//                            Toast.LENGTH_SHORT)
//                    .show();
//        }
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        switch (requestCode){
//            case REQUEST_CODE_SPEECH_INPUT:{
//                if (resultCode == RESULT_OK && null!=data){
//                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
//                    tv_Speech_to_text.setText(result.get(0));
//                    messageEditText.setText(result.get(0));
//                }
//                break;
//            }
//        }
//    }
//
//    void addToChat(String message, String sentBy){
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                messageList.add(new Message(message,sentBy));
//                messageAdapter.notifyDataSetChanged();
//                recyclerView.smoothScrollToPosition(messageAdapter.getItemCount());
//
//            }
//        });
//        }
//
//        void addResponse(String response){
//            messageList.remove(messageList.size()-1);
//            addToChat(response,Message.SENT_BY_BOT);
//        }
//
//
//
//}
//
