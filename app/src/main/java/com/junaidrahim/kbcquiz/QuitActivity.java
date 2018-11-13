package com.junaidrahim.kbcquiz;

import android.app.AlertDialog;
import android.app.VoiceInteractor;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.service.voice.VoiceInteractionSession;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


public class QuitActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quit);

        Typeface gotham = Typeface.createFromAsset(getAssets(),"Fonts/gotham-black.otf");
        Typeface varela = Typeface.createFromAsset(getAssets(),"Fonts/VarelaRound-Regular.ttf");

        TextView well_done_text = (TextView) findViewById(R.id.welldone_text);
        well_done_text.setTypeface(gotham);
        well_done_text.setText("Well Done");

        TextView name_text = (TextView) findViewById(R.id.name_quitactivity);
        TextView id_text = (TextView) findViewById(R.id.id_quitactivity);

        name_text.setTypeface(varela);
        id_text.setTypeface(varela);

        name_text.setText(getSharedPreferences("USER_NAME",MODE_PRIVATE).getString("user_name","Name"));
        id_text.setText(getSharedPreferences("USER_REGISTRATION_ID",MODE_PRIVATE).getString("user_registration_id","0000"));

        Button start_again_button = (Button) findViewById(R.id.start_again_button);
        start_again_button.setTypeface(varela);



    }

    public void start_quiz_button_command(View view) {
        // ask before deleting -> Delete participant
        // move to the main activity

        final AlertDialog.Builder alert_builder = new AlertDialog.Builder(QuitActivity.this);
        alert_builder.setTitle("Are you sure you want to start over ?");
        alert_builder.setMessage("Your current score will be reset");

        alert_builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                send_delete_registration_post_request();
                Intent main_activity_intent = new Intent(QuitActivity.this, MainActivity.class);
                startActivity(main_activity_intent);
            }
        });

        alert_builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // let the dialog cancel
            }
        });
        alert_builder.setCancelable(true);
        AlertDialog alertDialog = alert_builder.create();
        alertDialog.show();

    }

    private void send_delete_registration_post_request(){
        HashMap<String,String> request_params = new HashMap<String,String>();
        request_params.put("name",getSharedPreferences("USER_NAME",MODE_PRIVATE).getString("user_name","test"));
        request_params.put("id",getSharedPreferences("USER_REGISTRATION_ID",MODE_PRIVATE).getString("user_registration_id","0000"));

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String ip = getSharedPreferences("IP_ADDRESS",MODE_PRIVATE).getString("ip_address","192.168.0.106");
        final String url = "http://" + ip + ":8000/api/post/delete_registration";


        JsonObjectRequest delete_post_request = new JsonObjectRequest(url, new JSONObject(request_params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getBoolean("success"))
                                Toast.makeText(QuitActivity.this, "Successfully Logged Out", Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(QuitActivity.this, "Some error occurred. Please Restart the app", Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error","Error " + error.getMessage());

                    }
                }
        );

        requestQueue.add(delete_post_request);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Toast.makeText(QuitActivity.this, "Successfully Logged Out", Toast.LENGTH_SHORT).show();
        send_delete_registration_post_request();

        Intent main_activity_intent = new Intent(QuitActivity.this, MainActivity.class);
        startActivity(main_activity_intent);
        moveTaskToBack(true);
    }
}
