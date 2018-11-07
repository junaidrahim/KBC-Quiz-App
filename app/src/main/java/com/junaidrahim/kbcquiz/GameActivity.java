package com.junaidrahim.kbcquiz;

import android.app.DownloadManager;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.junaidrahim.kbcquiz.Questions;

import org.json.JSONException;
import org.json.JSONObject;


import java.util.HashMap;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


// TODO 1. Make sure to ask before quitting the activity


public class GameActivity extends AppCompatActivity {

    int timer_time = 30;
    int wrongs = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        start_questions(0);

    }

    private int randomNumber(){
        Random random = new Random();

        return random.nextInt(30); // that bound is no of questions - 1
    }

    private void start_questions(final int i) {
        Typeface varela = Typeface.createFromAsset(getAssets(),"Fonts/VarelaRound-Regular.ttf");

        TextView question_textview = (TextView) findViewById(R.id.question_text);
        question_textview.setTypeface(varela);


        Questions question_generator = new Questions();
        String[] question_data = question_generator.get_easy_question(i);


        String question_text = question_data[0];
        String[] options_array = {question_data[1],question_data[2],question_data[3],question_data[4]};
        final String correct_answer = question_data[5];

        question_textview.setText(question_text);


        // Starting the timer
        final TextView timer_text = (TextView) findViewById(R.id.time_left_time_text);
        timer_text.setText("");

        final Timer timer = new Timer();
        timer_time = 30;

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
             runOnUiThread(new Runnable() {
                 @Override
                 public void run() {
                     timer_text.setText(String.valueOf(timer_time));

                     if(timer_time == 0){
                         timer.cancel();

                         send_result_post_request(0);
                         start_questions(randomNumber());
                     }
                 }
             });
             timer_time--;
            }
        },10,1000);

        TextView wrongs_text = (TextView) findViewById(R.id.wrongs_textview);
        wrongs_text.setText("Wrong: " + String.valueOf(wrongs));

        ListView options_listview = (ListView) findViewById(R.id.options_listview);
        OptionAdapter optionAdapter = new OptionAdapter(GameActivity.this,options_array);
        options_listview.setAdapter(optionAdapter);

        options_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                timer.cancel();
                String[] alphabets = {"A","B","C","D"};

                if(alphabets[position].equals(correct_answer)) {
                    toast_message("Correct, +4");
                    send_result_post_request(4);
                    start_questions(randomNumber());
                }
                else {
                    wrongs++;
                    toast_message("Wrong, 0");
                    send_result_post_request(0);
                    start_questions(randomNumber());
                }
            }
        });

        if(wrongs==5){
            timer.cancel();
            Intent home_activity_intent = new Intent(GameActivity.this, QuitActivity.class);
            startActivity(home_activity_intent);
        }

    }


    private void toast_message(String message) {
        Toast.makeText(GameActivity.this,message,Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true); // TODO make sure the user cant exit just like that
    }

    private void send_result_post_request(int score){
        // code to send the post request

        String ip_address = getSharedPreferences("IP_ADDRESS",MODE_PRIVATE).getString("ip_address","192.168.0.104");
        final String url = "http://" + ip_address + ":8000/api/post/submission";

        String user_name = getSharedPreferences("USER_NAME",MODE_PRIVATE).getString("user_name","Test Name");
        String user_registration_id = getSharedPreferences("USER_REGISTRATION_ID",MODE_PRIVATE).getString("user_registration_id","0000");

        HashMap<String,String> request_params = new HashMap<String, String>();
        request_params.put("name",user_name);
        request_params.put("id",user_registration_id);
        request_params.put("score", Integer.toString(score));

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest score_post_request = new JsonObjectRequest(url, new JSONObject(request_params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // check response and tell to move to next question
                        try {
                            Log.w("E",response.getString("error"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        try {
                            boolean success = response.getBoolean("success");

                            if(success) {
                                Log.d("Submission Response","Successfully submitted");
                            }
                            else {
                                toast_message(response.getString("error"));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            toast_message(e.getMessage());
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // report the error
                        Log.w("Error",error.getMessage());
                        toast_message(error.getMessage());
                    }
                }
        );
        requestQueue.add(score_post_request);

    }
}
