package com.example.android.hungrysnake;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class GameOverActivity extends AppCompatActivity {

    private int score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        // To get the score that player got from the game
        Intent i = getIntent();
        score = i.getIntExtra("score", 0);

        // The score text view
        TextView scoreText = (TextView) findViewById(R.id.score_text);
        String textScore = "Your score: " + score + " !";
        scoreText.setText(textScore);
    }

    public void restartGame(View view) {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }
}
