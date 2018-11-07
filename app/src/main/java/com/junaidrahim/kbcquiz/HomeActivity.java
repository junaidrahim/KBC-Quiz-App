package com.junaidrahim.kbcquiz;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Typeface gotham = Typeface.createFromAsset(getAssets(),"Fonts/gotham-black.otf");
        Typeface varela = Typeface.createFromAsset(getAssets(),"Fonts/VarelaRound-Regular.ttf");

        TextView user_name_textview = (TextView) findViewById(R.id.user_name_text);
        TextView user_age_textview =(TextView) findViewById(R.id.user_age_text);
        TextView user_id_textview = (TextView) findViewById(R.id.id_text);
        TextView user_highest_score_textview = (TextView) findViewById(R.id.highest_score_text);

        user_name_textview.setTypeface(gotham);
        user_age_textview.setTypeface(varela);
        user_id_textview.setTypeface(varela);
        user_highest_score_textview.setTypeface(varela);

        user_name_textview.setText(getSharedPreferences("USER_NAME",MODE_PRIVATE).getString("user_name","Test Name"));
        user_age_textview.setText(getSharedPreferences("USER_AGE",MODE_PRIVATE).getString("user_age","10") + " yr old");
        user_id_textview.setText("ID: " + getSharedPreferences("USER_REGISTRATION_ID",MODE_PRIVATE).getString("user_registration_id","1235as"));



        Button start_quiz_button = (Button) findViewById(R.id.start_quiz_button);
        start_quiz_button.setTypeface(varela);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true); // TODO Work on the quitting mech
    }

    public void start_quiz(View view) {

        // open the next activity
        // as questions keep coming keep sending the resoponses to the server
        // do all the game rules

        Intent game_activity_intent = new Intent(HomeActivity.this, GameActivity.class);
        startActivity(game_activity_intent); // Let the game Begin !

    }
}
