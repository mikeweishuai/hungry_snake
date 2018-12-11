package com.example.android.hungrysnake;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class DifficultyChoose extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_difficulty_choose);
    }

    public void mediumMode(View view) {
        Intent i = new Intent(this, SnakeActivity.class);
        i.putExtra("difficulty", "easy");
        startActivity(i);
    }

    public void hardMode(View view) {
        Intent i = new Intent(this, SnakeActivity.class);
        i.putExtra("difficulty", "medium");
        startActivity(i);
    }

    public void darkSoulsMode(View view) {
        Intent i = new Intent(this, SnakeActivity.class);
        i.putExtra("difficulty", "hard");
        startActivity(i);
    }
}
