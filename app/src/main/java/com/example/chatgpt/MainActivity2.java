package com.example.chatgpt;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

//import com.android.volley.Request;
//import com.android.volley.RequestQueue;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.StringRequest;
//import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity2 extends AppCompatActivity {
    RecyclerView recyclerView;
    public EditText messageEditText;
    ImageButton sendButton;

    List<Message> messageList;
    MessageAdapter messageAdapter;
    ImageView iv_mic;
    ImageButton refreshBtn;
    String translatedText = "";
    boolean isSpacePressed = false;
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    OkHttpClient client = new OkHttpClient();

    private static final int REQUEST_CODE_SPEECH_INPUT = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        messageList = new ArrayList<>();

        iv_mic = findViewById(R.id.iv_mic);
        iv_mic.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        sendButton = findViewById(R.id.send_btn);
        sendButton.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        refreshBtn = findViewById(R.id.refreshBtn);
        refreshBtn.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity2.this, MainActivity2.class);
                finish();
                overridePendingTransition(0, 0);
                startActivity(i);
                overridePendingTransition(0, 0);
            }
        });
        iv_mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speak();
            }
        });

        messageEditText = findViewById(R.id.message_edit_text);

        messageEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyCode == KeyEvent.KEYCODE_SPACE && keyEvent.getAction() == KeyEvent.ACTION_UP) {
                    //do something.
                    Toast.makeText(getApplicationContext(), "Code "+ keyCode, Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        });




        messageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count > 0 && s.toString().trim().length() > 0 && s.charAt(s.length() - 1) == ' ' && !isSpacePressed) {
                    isSpacePressed = true;
                    String sentence = s.toString().trim();
                    System.out.println("sentence " + sentence);
                    new RetrieveFeedTask().execute(sentence);

//                    new APICallTask().execute(sentence);
                }else {
                    isSpacePressed = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        recyclerView = findViewById(R.id.recycler_view);
        sendButton = findViewById(R.id.send_btn);

        messageAdapter = new MessageAdapter(messageList);
        recyclerView.setAdapter(messageAdapter);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setStackFromEnd(true);
        recyclerView.setLayoutManager(llm);

        sendButton.setOnClickListener((v) -> {
            String question = messageEditText.getText().toString().trim();

            addToChat(question, Message.SENT_BY_ME);
            messageEditText.setText("");
        });
    }

    private void speak() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hi, speak something");

        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
        } catch (Exception e) {
            Toast.makeText(this, "Sorry, speech recognition not supported on this device.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SPEECH_INPUT && resultCode == RESULT_OK && data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String spokenText = result.get(0);
            addToChat(spokenText, Message.SENT_BY_ME);
            messageEditText.setText(spokenText);
        }
    }

    void addToChat(String message, String sentBy) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messageList.add(new Message(message, sentBy));
                messageAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(messageAdapter.getItemCount());
                messageEditText.requestFocus();
            }
        });
    }
    public void showKeyboard(){
        InputMethodManager inputMethodManager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }
    class RetrieveFeedTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            messageEditText.setEnabled(false);
            messageEditText.setFocusable(false);

        }

        protected String doInBackground(String... urls) {
            System.out.println("urls[0] ::  " + urls[0]);
            OkHttpClient client = new OkHttpClient();
            String url = "http://10.10.10.214:8083/hi_sent_translit/" + urls[0];
            Request request = new Request.Builder()
                    .url(url)
                    .header("Accept-Encoding", "identity")
                    .build();
            Response response = null;
            String result = "";
            try {
                response = client.newCall(request).execute();
                result = response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        protected void onPostExecute(String result) {
            Log.e("========", result);
//            translatedText = result;
            translateSentence(result);

        }
    }

//    class APICallTask extends AsyncTask<String, Void, String> {
//        private static final int MAX_RETRY_COUNT = 3;
//        private static final long RETRY_DELAY_MS = 1000;
//
//        @Override
//        protected String doInBackground(String... strings) {
//            String sentence = strings[0];
//            final String[] responseStr = {""};
//            int retryCount = 0;
//
////            while (retryCount < MAX_RETRY_COUNT) {
//                try {
//                    RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
//                    String url = "http://10.10.10.214:8083/hi_sent_translit/" + sentence;
//
//                    // Create the request
//                    StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
//                            new Response.Listener<String>() {
//                                @Override
//                                public void onResponse(String response) {
//                                    responseStr[0] = response + " ";
//                                }
//                            },
//                            new Response.ErrorListener() {
//                                @Override
//                                public void onErrorResponse(VolleyError error) {
//                                    Log.e("Translation Error", error.toString());
//                                }
//                            });
//
//                    // Add the request to the RequestQueue.
//                    queue.add(stringRequest);
//
//                    if (!responseStr[0].isEmpty()) {
//                        return responseStr[0];
//                    } else {
//                        retryCount++;
//                        Thread.sleep(RETRY_DELAY_MS);
//                    }
//                } catch (InterruptedException e) {
//                    Log.e("doInBackground: ", "Exception: " + e.getMessage());
//                    e.printStackTrace();
//                    retryCount++;
//                    try {
//                        Thread.sleep(RETRY_DELAY_MS);
//                    } catch (InterruptedException ex) {
//                        ex.printStackTrace();
//                    }
//                }
////            }
//
//            return responseStr[0];
//
//        }
//
//        @Override
//        protected void onPostExecute(String response) {
//            super.onPostExecute(response);
//            translateSentence(response);
//        }
//    }

    private void translateSentence(String sentence) {
        // Update the translated sentence in the UI element (e.g., EditText)
//        messageEditText.setEnabled(true);
        messageEditText.setFocusableInTouchMode(true);
        messageEditText.setClickable(true);
        messageEditText.setFocusable(true);
        messageEditText.setText(sentence + " ");
        messageEditText.setSelection(sentence.length() + 1);// Set cursor position at the end of the text
//        showKeyboard();
    }
}