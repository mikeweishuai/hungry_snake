package com.example.android.hungrysnake;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;

public class SnakeActivity extends Activity {

    SnakeEngine mSnakeEngine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the pixel dimensions of the screen
        Display display = getWindowManager().getDefaultDisplay();

        // Initialize the result into a Point object
        Point size = new Point();
        display.getSize(size);

        // Create a new instance of the SnakeEngine class
        mSnakeEngine = new SnakeEngine(this, size);

        // Make snakeEngine the view of the Activity
        setContentView(mSnakeEngine);
    }

    // Start the thread in snakeEngine
    @Override
    protected void onResume() {
        super.onResume();
        mSnakeEngine.resume();
    }

    // Stop the thread in snakeEngine
    @Override
    protected void onPause() {
        super.onPause();
        mSnakeEngine.pause();
    }
}
