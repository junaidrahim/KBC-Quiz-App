package com.junaidrahim.kbcquiz;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Typeface gotham = Typeface.createFromAsset(getAssets(),"Fonts/gotham-black.otf");
        Typeface varela = Typeface.createFromAsset(getAssets(),"Fonts/VarelaRound-Regular.ttf");

        TextView main_title = (TextView) findViewById(R.id.welldone_text);
        main_title.setTypeface(gotham);

        EditText name_input_text = (EditText) findViewById(R.id.name_input_text);
        name_input_text.setTypeface(varela);

        EditText age_input_text = (EditText) findViewById(R.id.age_input_text);
        age_input_text.setTypeface(varela);

        Button join_game_button = (Button) findViewById(R.id.join_game_button);
        join_game_button.setTypeface(gotham);

    }

    public void register_user(View view) {
        EditText name_input_text = (EditText) findViewById(R.id.name_input_text);
        EditText age_input_text = (EditText) findViewById(R.id.age_input_text);

        final String name_text = name_input_text.getText().toString();
        String age_text = age_input_text.getText().toString();

        final String ip_address = "192.168.0.104";

        if(name_text.equals("") || name_text.equals(" ") || age_text.equals("") || age_text.equals(" "))
            Toast.makeText(MainActivity.this,"Please enter all your details",Toast.LENGTH_SHORT).show();
        else{
            // Initiate the login procedure
            // and move to the next activity

            // setting up the shared preferences
            getSharedPreferences("IP_ADDRESS",MODE_PRIVATE).edit().putString("ip_address",ip_address).commit();

            getSharedPreferences("USER_NAME",MODE_PRIVATE).edit().putString("user_name",name_text).commit();
            getSharedPreferences("USER_AGE",MODE_PRIVATE).edit().putString("user_age",age_text).commit();

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            final String url = "http://" + ip_address + ":8000/api/post/register";

            HashMap<String,String> params = new HashMap<String, String>();
            params.put("name",name_text);

            JsonObjectRequest registration_post_request = new JsonObjectRequest(url, new JSONObject(params),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                boolean success = response.getBoolean("success");
                                if(success){
                                    // storing the id as a shared preference
                                    // and moving to the next activity
                                    String user_registration_id = response.getString("id");
                                    getSharedPreferences("USER_REGISTRATION_ID",MODE_PRIVATE).edit().putString("user_registration_id",user_registration_id).commit();

                                    Intent HomeActivityIntent = new Intent(MainActivity.this, HomeActivity.class);
                                    startActivity(HomeActivityIntent);
                                }
                                else{
                                    String error = response.getString("error");
                                    Toast.makeText(MainActivity.this, error, Toast.LENGTH_LONG).show();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.w("Error",error.getMessage());
                            Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
            );

            requestQueue.add(registration_post_request);

        }
    }
}
