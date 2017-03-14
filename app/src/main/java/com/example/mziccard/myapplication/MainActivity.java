package com.example.mziccard.myapplication;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // todo API_KEY should not be stored in plain sight
    private static final String API_KEY = "AIzaSyCIWb579dBtIL5BflFopri9L1OB1Md8Wsk";
    TextToSpeech t1;
    TextView textTarget;
    EditText editTextResource;
    Button buttonTranslate;
    Spinner spinnerResource,spinnerTarget;
    String resouceLanguageCode,targetLanguageCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textTarget = (TextView) findViewById(R.id.textTarget);
        editTextResource = (EditText) findViewById(R.id.editResource);
        buttonTranslate = (Button) findViewById(R.id.buttonTranslate);
        spinnerResource=(Spinner) findViewById(R.id.spinnerResource);
        spinnerTarget=(Spinner) findViewById(R.id.spinnerTarget);
        t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.US);
                }
            }
        });

        MyClass[] objLanguage ={
                new MyClass("English", "en"),
                new MyClass("Japanese", "ja"),
                new MyClass("French", "fr"),
                new MyClass("German", "de"),
                new MyClass("Korean", "ko"),
                new MyClass("Vietnamese", "vi"),
                new MyClass("Chinese", "zh-TW"),
                new MyClass("Italian", "it")
        };

        MySpinnerAdapter adapterResource =
                new MySpinnerAdapter(MainActivity.this,
                        android.R.layout.simple_spinner_item, objLanguage);
        adapterResource.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerResource.setAdapter(adapterResource);
        spinnerResource.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                MyClass obj = (MyClass)(adapterView.getItemAtPosition(i));
                resouceLanguageCode=String.valueOf(obj.getValue());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        MySpinnerAdapter adapterTarget =
                new MySpinnerAdapter(MainActivity.this,
                        android.R.layout.simple_spinner_item, objLanguage);
        adapterTarget.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTarget.setAdapter(adapterTarget);
        spinnerTarget.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                MyClass obj = (MyClass)(adapterView.getItemAtPosition(i));
                targetLanguageCode=String.valueOf(obj.getValue());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        buttonTranslate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String toSpeak = editTextResource.getText().toString();

                final Handler textViewHandler = new Handler();

                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        TranslateOptions options = TranslateOptions.newBuilder()
                                .setApiKey(API_KEY)
                                .build();
                        final Translate translate = options.getService();
                        final Translation translation =
                                translate.translate(toSpeak,Translate.TranslateOption.sourceLanguage(resouceLanguageCode),
                                        Translate.TranslateOption.targetLanguage(targetLanguageCode));
                        textViewHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                    t1.speak(translation.getTranslatedText(), TextToSpeech.QUEUE_FLUSH, null);
                                    textTarget.setText(translation.getTranslatedText());

                            }
                        });
                        return null;
                    }
                }.execute();
            }
        });

    }
    public void onPause(){
        if(t1 !=null){
            t1.stop();
            t1.shutdown();
        }
        super.onPause();
    }
    public class MyClass{

        private String text;
        private String value;


        public MyClass(String text, String value){
            this.text = text;
            this.value = value;
        }

        public void setText(String text){
            this.text = text;
        }

        public String getText(){
            return this.text;
        }

        public void setValue(String value){
            this.value = value;
        }

        public String getValue(){
            return this.value;
        }
    }

    //custom adapter
    public class MySpinnerAdapter extends ArrayAdapter<MyClass> {

        private Context context;
        private MyClass[] myObjs;

        public MySpinnerAdapter(Context context, int textViewResourceId,
                                MyClass[] myObjs) {
            super(context, textViewResourceId, myObjs);
            this.context = context;
            this.myObjs = myObjs;
        }

        public int getCount(){
            return myObjs.length;
        }

        public MyClass getItem(int position){
            return myObjs[position];
        }

        public long getItemId(int position){
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView label = new TextView(context);
            label.setText(myObjs[position].getText());
            return label;
        }

        @Override
        public View getDropDownView(int position, View convertView,
                                    ViewGroup parent) {
            TextView label = new TextView(context);
            label.setText(myObjs[position].getText());
            return label;
        }
    }

}

