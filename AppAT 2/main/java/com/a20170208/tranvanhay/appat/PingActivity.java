package com.a20170208.tranvanhay.appat;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class PingActivity extends AppCompatActivity {
    private static final String TAG = "PingActivity";
    EditText editTextIPAddress;
    Button btnPing,btnChangeToDisplayActivity;
    TextView textViewClearPingResult,textViewPingResultPingActivity;
    ScrollView scrollView;
    StringBuilder stringBuilder = new StringBuilder();
    private String pingResult = "";
    private String hostIP = "";
    int messageFailed = 0, messageSuccessfull = 0,messageError = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ping);
        mapping();
        addControl();
        init();
    }

    private void init() {
    }

    private void mapping() {
        editTextIPAddress = (EditText)findViewById(R.id.editTextIPAddressPingActivity);
        btnPing = (Button)findViewById(R.id.btnPingPingActivity);
        btnChangeToDisplayActivity = (Button)findViewById(R.id.btnChangeToDisplayActivityPingActivity);
        textViewClearPingResult = (TextView)findViewById(R.id.textViewClearPingResultActivity);
        textViewPingResultPingActivity = (TextView)findViewById(R.id.textViewPingResultPingActivity);
        scrollView = (ScrollView)findViewById(R.id.scrollViewPingActivity);
    }
    private void addControl() {
        btnPing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hostIP = editTextIPAddress.getText().toString();
                if(!hostIP.equals("")){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new PingExecution().execute(hostIP);
                        }
                    });
                } else {
                    Toast.makeText(PingActivity.this, "You must fulfill the IP Address", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnChangeToDisplayActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"Ping Activity Change To Display Activity");
                Intent intent = new Intent(PingActivity.this,DisplayActivity.class);
                startActivity(intent);
            }
        });
        textViewClearPingResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int stringBuilderLength = stringBuilder.length();
                stringBuilder.delete(0,stringBuilderLength);
                Toast.makeText(PingActivity.this, "Cleared content of console display", Toast.LENGTH_SHORT).show();
                textViewPingResultPingActivity.setText(stringBuilder.toString());
            }
        });
        scrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                scrollView.post(new Runnable() {
                    public void run() {
                        scrollView.fullScroll(View.FOCUS_DOWN);
                    }
                });
            }
        });
    }
    // Implement ping to device in the same LAN network
    private class PingExecution extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... strings) {
            try {
                for (int i = 0; i <= 3; i ++){
                    pingResult = NetworkPing.ping(strings[0]);
                    publishProgress(i);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPreExecute() {
            hostIP = editTextIPAddress.getText().toString();
            stringBuilder.append("Pinging "+ hostIP + " with 64 bytes of data:\n");
            textViewPingResultPingActivity.setText(stringBuilder.toString());
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            if (pingResult.equals("1")){
                messageFailed ++;
                pingResult = "Destination host unreachable";
            } else if(pingResult.equals("2")){
                messageError ++;
                pingResult = "System has error";
            } else {
                messageSuccessfull++;
            }
            Log.d(TAG,"Ping Result: "+ pingResult);
            stringBuilder.append(pingResult +"\n");
            textViewPingResultPingActivity.setText(stringBuilder.toString());
        }
        @Override
        protected void onPostExecute(String s) {
            hostIP = editTextIPAddress.getText().toString();
            float failedRate = 0;
            //  When user type the wrong address.
            if (messageError != 0){
                stringBuilder.append("\nMight you input the wrong address or the system has error, please try again\n"
                                    + "<<------END------>>\n");
            }
            else {
                //  When user type address properly.
                //  if statement to avoid divide for 0.
                if (messageSuccessfull == 0){
                    failedRate = 100;
                } else {
                    failedRate = (messageFailed*100)/messageSuccessfull;
                }
                stringBuilder.append("\n"
                        + "Ping statistics for " + hostIP + ":\n"
                        + "Packets: Sent = 4, Receive = "+messageSuccessfull+", Lost = "+messageFailed+ "  ("+failedRate+" %)\n"
                        + "<<------END------>>\n\n\n");
            }
            textViewPingResultPingActivity.setText(stringBuilder.toString());
            //  Reset variable for next ping.
            messageSuccessfull = 0;
            messageFailed = 0;
            messageError = 0;
        }
    }
}
