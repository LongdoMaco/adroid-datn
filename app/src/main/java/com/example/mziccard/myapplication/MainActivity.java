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
    TextView textView;
    EditText editText;
    Button button;
    Spinner spinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.text);
        editText = (EditText) findViewById(R.id.editText);
        textView.setText("Nothing to show");
        button = (Button) findViewById(R.id.button);
        spinner=(Spinner) findViewById(R.id.spinner);
        t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.FRANCE);
                }
            }
        });

        MyClass[] obj2 ={
                new MyClass("SUN", "0"),
                new MyClass("MON", "1"),
                new MyClass("TUE", "2"),
                new MyClass("WED", "3"),
                new MyClass("THU", "4"),
                new MyClass("FRI", "5"),
                new MyClass("SAT", "6")
        };

        MySpinnerAdapter adapter2 =
                new MySpinnerAdapter(MainActivity.this,
                        android.R.layout.simple_spinner_item, obj2);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter2);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                MyClass obj = (MyClass)(adapterView.getItemAtPosition(i));
                textView.setText(String.valueOf(obj.getValue()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String toSpeak = editText.getText().toString();

                final Handler textViewHandler = new Handler();

                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        TranslateOptions options = TranslateOptions.newBuilder()
                                .setApiKey(API_KEY)
                                .build();
                        final Translate translate = options.getService();
                        final Translation translation =
                                translate.translate(toSpeak,Translate.TranslateOption.sourceLanguage("fr"),
                                        Translate.TranslateOption.targetLanguage("vi"));
                        textViewHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                    t1.speak(translation.getTranslatedText(), TextToSpeech.QUEUE_FLUSH, null);
                                    textView.setText(translation.getTranslatedText());

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

