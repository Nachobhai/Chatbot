package com.example.chatgpt;

import static android.app.PendingIntent.getActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
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
import android.content.Context;
import android.content.res.Resources;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity3 extends AppCompatActivity {
    RecyclerView recyclerView;
    public EditText messageEditText;
    ImageButton sendButton;

    List<Message> messageList;
    MessageAdapter messageAdapter;
    ImageView iv_mic;
    Context context;
    Resources resources;
    RadioGroup radioGroup;

    RadioButton radioButton1;
    RadioButton radioButton2;
    TextView welcomeText;

    ImageButton refreshBtn;
    ImageButton LangButton;
    String translatedText = "";
    private boolean isBanglaSelected = true;
    boolean isSpacePressed = false;
    OkHttpClient client = new OkHttpClient();

    private static final int REQUEST_CODE_SPEECH_INPUT = 1000;
    int selected = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    loadLocal();
    loadValue();
        messageList = new ArrayList<>();

        iv_mic = findViewById(R.id.iv_mic);
        iv_mic.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        welcomeText = findViewById(R.id.welcome_Text);
        sendButton = findViewById(R.id.send_btn);
        sendButton.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        LangButton = findViewById(R.id.lang_Btn);
        LangButton.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        LangButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangeLanguageDialog();
            }
        });

        radioGroup = findViewById(R.id.group_radio);

        //krishi
        radioButton1 = (RadioButton)findViewById(R.id.radioButton_1);
        radioButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(MainActivity3.this,"Option 1 is clicked", Toast.LENGTH_SHORT).show();
                String option = radioButton1.getText().toString().trim() ;
                addToChat(option , Message.SENT_BY_ME);
                addToChat(getString(R.string.BOTQUSASK),Message.SENT_BY_BOT);
                radioGroup.setVisibility(View.GONE);
                welcomeText.setVisibility(View.GONE);
            }
        });
        //pashupalan
        radioButton2 = (RadioButton)findViewById(R.id.radioButton_2);
        radioButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(MainActivity3.this,"Option 2 is clicked", Toast.LENGTH_SHORT).show();
                String option = radioButton2.getText().toString().trim() ;
                addToChat(option , Message.SENT_BY_ME);
                addToChat(getString(R.string.BOTQUSASK),Message.SENT_BY_BOT);
                radioGroup.setVisibility(View.GONE);
                welcomeText.setVisibility(View.GONE);
            }
        });
        refreshBtn = findViewById(R.id.refreshBtn);
        refreshBtn.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity3.this, MainActivity3.class);
                finish();
                overridePendingTransition(0, 0);
                startActivity(i);
                overridePendingTransition(0, 0);
                Locale locale = new Locale("hi");
                Locale.setDefault(locale);

                Configuration config = new Configuration();
                config.locale = locale;
                getResources().updateConfiguration(config, getResources().getDisplayMetrics());
                recreate();

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
                    // do something.
                    Toast.makeText(getApplicationContext(), "Code " + keyCode, Toast.LENGTH_SHORT).show();
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
                    String word = s.toString().trim();
                    System.out.println("word " + word);
                    new RetrieveFeedTask().execute(word);
                } else {
                    isSpacePressed = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        recyclerView = findViewById(R.id.recycler_view);

        messageAdapter = new MessageAdapter(messageList);
        recyclerView.setAdapter(messageAdapter);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setStackFromEnd(true);
        recyclerView.setLayoutManager(llm);

        sendButton.setOnClickListener((v) -> {
            String question = messageEditText.getText().toString().trim();
            addToChat(question, Message.SENT_BY_ME);
            addToChat(getString(R.string.OPTIMALANS), Message.SENT_BY_BOT);
            messageEditText.setText("");
        });

    }

    private void showChangeLanguageDialog() {
        final String[] listLang = {"Hindi", "Bengali"};
        AlertDialog.Builder langBuilder = new AlertDialog.Builder(MainActivity3.this);
        langBuilder.setTitle("Choose Language...");
        langBuilder.setCancelable(false);
        langBuilder.setSingleChoiceItems(listLang, selected, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                if (i == 0) {
                    setLocal("hi");
                }
                if (i == 1) {
                    setLocal("bn");
                }

                setValue(i);
                Intent intent = getIntent();
                finish();
                startActivity(intent);
                dialog.dismiss();
            }

        });

        langBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog mDialog = langBuilder.create();
        mDialog.show();

    }

    private void setLocal(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("LANG", lang);
        editor.apply();

    }

    private void loadLocal() {
        SharedPreferences settings = getSharedPreferences("Settings", MODE_PRIVATE);
        String lang = settings.getString("LANG", "");
        setLocal(lang);

    }

    private void loadValue() {
        SharedPreferences settings = getSharedPreferences("Settings", MODE_PRIVATE);
        selected = settings.getInt("value", 0);
        setValue(selected);

    }

    private void setValue(int value) {
        SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putInt("value", value);
        editor.apply();

    }



    private void changeLanguage(String languagecode) {
        Locale locale = new Locale(languagecode);
        Configuration config = getResources().getConfiguration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        // Refresh the activity to apply the language change
        recreate();
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
                List<Message> messageList = messageAdapter.getMessageList();
                messageList.add(new Message(message, sentBy));
                messageAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(messageAdapter.getItemCount());
                messageEditText.requestFocus();
            }
        });
    }
    void addResponse(String response){
        messageList.remove(messageList.size()-1);
        addToChat(response,Message.SENT_BY_BOT);
    }

    public void showKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }


    class RetrieveFeedTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            messageEditText.setFocusable(true);
        }

        protected String doInBackground(String... urls) {
            System.out.println("urls[0] ::  " + urls[0]);
            String url = "http://10.10.10.214:8083/hi_translit/" + urls[0];
            Request request = new Request.Builder()
                    .url(url)
                    .header("Accept-Encoding", "identity")
                    .build();
            try {
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }

        protected void onPostExecute(String result) {
            translateWord(result);
        }
    }

    private void translateWord(String word) {
        messageEditText.setFocusableInTouchMode(true);
        messageEditText.setClickable(true);
        messageEditText.requestFocus();
        messageEditText.setText(word + " ");
        messageEditText.setSelection(word.length() + 1);
    }

}