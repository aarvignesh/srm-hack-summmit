package com.truiton.customspeechrecognizer;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class VoiceRecognitionActivity extends AppCompatActivity implements
        RecognitionListener {

    private static final int REQUEST_RECORD_PERMISSION = 100;
    public static final String PREFS_VC ="vccd";
    public static final String VCD= "vbcd";
    private TextView returnedText;
    private ToggleButton toggleButton;
    private ProgressBar progressBar;
    private SpeechRecognizer speech = null;
    private Intent recognizerIntent;
    private String LOG_TAG = "VoiceRecognitionActivity";
    private ArrayList<String> resultsarr;
    private Button gobutton;
    private RadioGroup category;
    private RadioGroup  type;
    private RadioButton selected_R_btn;
    String[] results;
    ProgressDialog pd;
    String highConfRes;
    String fetchedJson;
    static String SEARCH_QUERY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        returnedText = (TextView) findViewById(R.id.textView1);
        progressBar = (ProgressBar) findViewById(R.id.progressBar1);
        toggleButton = (ToggleButton) findViewById(R.id.toggleButton1);
        type=findViewById(R.id.type);
        category=findViewById(R.id.category);
        gobutton=findViewById(R.id.gobutton);
        gobutton.setVisibility(View.GONE);
        resultsarr=new ArrayList<>();



//======== Code to save data ===================


//========= Code to get saved/ retrieve data ==============


        final TestAdapter mDbHelper = new TestAdapter(getApplicationContext());
        mDbHelper.createDatabase();
        mDbHelper.open();
//        Cursor testdata = mDbHelper.getAudio("BREAK ME Down");
//        Toast.makeText(this, testdata.toString(),Toast.LENGTH_SHORT).show();
//        mDbHelper.close();

        SEARCH_QUERY="";

        progressBar.setVisibility(View.INVISIBLE);
        speech = SpeechRecognizer.createSpeechRecognizer(this);
        Log.i(LOG_TAG, "isRecognitionAvailable: " + SpeechRecognizer.isRecognitionAvailable(this));
        speech.setRecognitionListener(this);
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
                "en");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);

        gobutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String selection=getCat();
                String type=getType();
                if(type.equals("Exact")){
                    SEARCH_QUERY=highConfRes;
                    switch (selection){
                        case "Audio":
                            results= mDbHelper.getAudio(SEARCH_QUERY);
                            Toast.makeText(VoiceRecognitionActivity.this, results.toString(), Toast.LENGTH_SHORT).show();
                            break;

                        case "Video":
                            results= mDbHelper.getVideo(SEARCH_QUERY);
                            break;

                        case "E-Book":
                            SharedPreferences sp = getSharedPreferences(PREFS_VC , Context.MODE_PRIVATE);
                            sp.edit().putString(VCD,SEARCH_QUERY).commit();
                            Intent intent3 = new Intent(getBaseContext(), DisplayResultsForEbook.class);
                            startActivity(intent3);
                            break;
                    }
                    if (!selection.equals("E-Book")) {
                        switch (results[2]) {
                            case "o":
                            case "O":
                                Intent intent = new Intent(getBaseContext(), DisplayResultsForVideoO.class);
                                intent.putExtra("VIDEO_URI", results[1]);
                                intent.putExtra("START", results[0]);
                                intent.putExtra("MODE", results[2]);
                                startActivity(intent);
                                break;
                            case "y":
                            case "Y":
                                Intent intent2 = new Intent(getBaseContext(), DisplayResultsForYT.class);
                                intent2.putExtra("VIDEO_URI", results[1]);
                                intent2.putExtra("START", results[0]);
                                intent2.putExtra("MODE", results[2]);
                                startActivity(intent2);
                                break;

                        }
                    }
                }else{
                    String BASE="https://api.datamuse.com/words?ml=";
                    String enc_wrd=highConfRes.replace(" ","+");
                    String url=BASE+enc_wrd+"&max=1";
                    new JsonTask().execute(url);
                    Log.d("JSO",url);
//                    Log.d("JSO",SEARCH_QUERY);
                }
            }
        });

        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setIndeterminate(true);
                    ActivityCompat.requestPermissions
                            (VoiceRecognitionActivity.this,
                                    new String[]{Manifest.permission.RECORD_AUDIO,Manifest.permission.READ_EXTERNAL_STORAGE},
                                    REQUEST_RECORD_PERMISSION);
                } else {
                    progressBar.setIndeterminate(false);
                    progressBar.setVisibility(View.INVISIBLE);
                    speech.stopListening();
                }
            }
        });

    }

    public String  getCat(){

    int id=category.getCheckedRadioButtonId();
    RadioButton sel=findViewById(id);
    return  sel.getText().toString();

    }

    public String  getType(){

        int id=type.getCheckedRadioButtonId();
        RadioButton typ=findViewById(id);
        return  typ.getText().toString();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_RECORD_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    speech.startListening(recognizerIntent);
                } else {
                    Toast.makeText(VoiceRecognitionActivity.this, "Permission Denied!", Toast
                            .LENGTH_SHORT).show();
                }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (speech != null) {
            speech.destroy();
            Log.i(LOG_TAG, "destroy");
        }
    }


    @Override
    public void onBeginningOfSpeech() {
        Log.i(LOG_TAG, "onBeginningOfSpeech");
        progressBar.setIndeterminate(false);
        progressBar.setMax(10);
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        Log.i(LOG_TAG, "onBufferReceived: " + buffer);
    }

    @Override
    public void onEndOfSpeech() {
        Log.i(LOG_TAG, "onEndOfSpeech");
        progressBar.setIndeterminate(true);
        toggleButton.setChecked(false);
    }

    @Override
    public void onError(int errorCode) {
        String errorMessage = getErrorText(errorCode);
        Log.d(LOG_TAG, "FAILED " + errorMessage);
        returnedText.setText(errorMessage);
        toggleButton.setChecked(false);
    }

    @Override
    public void onEvent(int arg0, Bundle arg1) {
        Log.i(LOG_TAG, "onEvent");
    }

    @Override
    public void onPartialResults(Bundle arg0) {
        Log.i(LOG_TAG, "onPartialResults");
    }

    @Override
    public void onReadyForSpeech(Bundle arg0) {
        Log.i(LOG_TAG, "onReadyForSpeech");
    }

    @Override
    public void onResults(Bundle results) {
        Log.i(LOG_TAG, "onResults");
        ArrayList<String> matches = results
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        String text = "";
        for (String result : matches)
            resultsarr.add(result);

        returnedText.setText(resultsarr.toString());
        highConfRes=resultsarr.get(0);
        gobutton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onRmsChanged(float rmsdB) {
        Log.i(LOG_TAG, "onRmsChanged: " + rmsdB);
        progressBar.setProgress((int) rmsdB);
    }

    public static String getErrorText(int errorCode) {
        String message;
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                message = "Audio recording error";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = "Client side error";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "Insufficient permissions";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                message = "Network error";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "Network timeout";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "No match";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "RecognitionService busy";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                message = "error from server";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "No speech input";
                break;
            default:
                message = "Didn't understand, please try again.";
                break;
        }
        return message;
    }

    private class JsonTask extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();


            pd = new ProgressDialog(VoiceRecognitionActivity.this);
            pd.setMessage("Please wait");
            pd.setCancelable(false);
            pd.show();
        }

        protected String doInBackground(String... params) {


            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();


                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line+"\n");
                    Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)

                }

                return buffer.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (pd.isShowing()){
                pd.dismiss();
            }
            JSONObject jsa= null;
            String confsyn=null;
            try {
                jsa = new JSONArray(result).getJSONObject(0);
                 confsyn=jsa.getString("word");
                SEARCH_QUERY=confsyn;
                SharedPreferences sp = getSharedPreferences(PREFS_VC , Context.MODE_PRIVATE);
                sp.edit().putString(VCD,SEARCH_QUERY).commit();

            } catch (JSONException e) {
                e.printStackTrace();
            }
            final TestAdapter mDbHelper = new TestAdapter(getApplicationContext());
            mDbHelper.open();

            String selection=getCat();
            String type=getType();

            switch (selection){
                case "Audio":
                    results= mDbHelper.getAudio(SEARCH_QUERY);
                    break;

                case "Video":
                    results= mDbHelper.getVideo(SEARCH_QUERY);
                    break;

                case "E-Book":
                    SharedPreferences sp = getSharedPreferences(PREFS_VC , Context.MODE_PRIVATE);
                    sp.edit().putString(VCD,SEARCH_QUERY).commit();
                    Intent intent3 = new Intent(getBaseContext(), DisplayResultsForEbook.class);
                    startActivity(intent3);
                    break;
            }
            if (!selection.equals("E-Book")) {
                switch (results[2]) {
                    case "o":
                    case "O":
                        Intent intent = new Intent(getBaseContext(), DisplayResultsForVideoO.class);
                        intent.putExtra("VIDEO_URI", results[1]);
                        intent.putExtra("START", results[0]);
                        intent.putExtra("MODE", results[2]);
                        startActivity(intent);
                        break;
                    case "y":
                    case "Y":
                        Intent intent2 = new Intent(getBaseContext(), DisplayResultsForYT.class);
                        intent2.putExtra("VIDEO_URI", results[1]);
                        intent2.putExtra("START", results[0]);
                        intent2.putExtra("MODE", results[2]);
                        startActivity(intent2);
                        break;

                }
            }


            Log.d("JSO",confsyn+SEARCH_QUERY+result);
//            Toast.makeText(VoiceRecognitionActivity.this, result, Toast.LENGTH_SHORT).show();
        }
    }
}
