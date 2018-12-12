package com.example.android.hungrysnake;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.util.Random;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;


class SnakeEngine extends SurfaceView implements Runnable {

    // Current difficulty
    private int difficulty;

    // Thread for the game
    private Thread thread = null;

    // To hold a reference to the Activity
    private Context context;

    // For tracking movement Heading
    public enum Heading {UP, RIGHT, DOWN, LEFT}
    // Start by heading to the right
    private Heading heading = Heading.UP;

    // To hold the screen size in pixels
    private int screenX;
    private int screenY;

    // How long is the snake
    private int snakeLength;

    // The position of the apple
    private int appleX;
    private int appleY;

    // The position and length of the wall
    private int wallX;
    private int wallY;
    private int wallLength = 10;

    // The position and scale of the cloud
    private int cloudX;
    private int cloudY;
    private int cloudWidth = 15;
    private int cloudHeight = 15;

    // The size in pixels of a snake segment
    private int blockSize;

    // The size in segments of the playable area
    private final int NUM_BLOCKS_WIDE = 20;
    private int numBlocksHigh;

    // Control pausing between updates
    private long nextFrameTime;
    // Update the game 10 times per second
    private long FPS = 1;
    // There are 1000 milliseconds in a second
    private final long MILLIS_PER_SECOND = 1000;

    // How many points does the player have
    private int score;

    // The location in the grid of all the segments
    private int[] snakeXs;
    private int[] snakeYs;

    // Everything we need for drawing
    // Is the game currently playing?
    private volatile boolean isPlaying;

    // A canvas for our paint
    private Canvas canvas;

    // Required to use canvas
    private SurfaceHolder surfaceHolder;

    // Some paint for our canvas
    private Paint paint;

    private Activity myActivity;

    public SnakeEngine(Context context, Point size, Activity activity, AttributeSet attrs,
                       String difficulty) {
        super(context, attrs);
        myActivity = activity;

        context = context;

        //initialize FPS as the difficulty
        if (difficulty.equals("easy")) {
            this.difficulty = 1;
            FPS = 4;
        } else if (difficulty.equals("medium")) {
            this.difficulty = 2;
            FPS = 8;
        } else if (difficulty.equals("hard")){
            this.difficulty = 3;
            // This does not work due to unknown reason
            FPS = 10;
        }
        screenX = size.x;
        screenY = size.y;

        // Work out how many pixels each block is
        blockSize = screenX / NUM_BLOCKS_WIDE;
        // How many blocks of the same size will fit into the height
        numBlocksHigh = screenY / blockSize;



        // Initialize the drawing objects
        surfaceHolder = getHolder();
        paint = new Paint();

        // If you score 200 you are rewarded with a crash achievement!
        snakeXs = new int[200];
        snakeYs = new int[200];

        // Start the game
        newGame();
    }

    @Override
    public void run() {

        while (isPlaying) {

            // Update 10 times a second
            if(updateRequired()) {
                update();
                draw();
            }

        }
    }



    public void pause() {
        isPlaying = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            // Error
        }
    }

    public void resume() {
        isPlaying = true;
        thread = new Thread(this);
        thread.start();
    }

    public void newGame() {
        // Start with a single snake segment
        snakeLength = 3;
        snakeXs[0] = NUM_BLOCKS_WIDE / 2;
        snakeYs[0] = numBlocksHigh / 2;

        if (difficulty > 1) {
            // Set the wall
            spawnWall();
            if (difficulty > 2) {
                // Set the cloud
                spawnCloud();
            }
        }

        // Randomly put the apple
        spawnApple();

        // Reset the score
        score = 0;

        // Setup nextFrameTime so an update is triggered
        nextFrameTime = System.currentTimeMillis();
    }

    public void spawnApple() {
        Random random = new Random();
        appleX = random.nextInt(NUM_BLOCKS_WIDE - 1) + 1;
        appleY = random.nextInt(numBlocksHigh - 1) + 1;
    }

    public void spawnWall() {
        Random random = new Random();
        wallX = random.nextInt(NUM_BLOCKS_WIDE - wallLength - 1) + 1;
        wallY = random.nextInt(numBlocksHigh / 2 - 1) + numBlocksHigh / 2;
    }

    public void spawnCloud() {
        Random random = new Random();
        cloudX = random.nextInt(NUM_BLOCKS_WIDE - cloudWidth - 1) + 1;
        cloudY = random.nextInt(numBlocksHigh - cloudHeight - 1) + 1;
    }

    private void eatApple(){
        // Increase the size of the snake
        snakeLength++;
        // Replace apple
        spawnApple();
        //add to the score
        score = score + 1;
    }

    private void moveSnake(){
        // Move the body
        for (int i = snakeLength; i > 0; i--) {
            // Start at the back and move it
            // to the position of the segment in front of it
            snakeXs[i] = snakeXs[i - 1];
            snakeYs[i] = snakeYs[i - 1];

            // Exclude the head because
            // the head has nothing in front of it
        }

        // Move the head in the appropriate heading
        // I don't know why i cannot write default case here
        switch (heading) {
            case UP:
                snakeYs[0]--;
                break;

            case RIGHT:
                snakeXs[0]++;
                break;

            case DOWN:
                snakeYs[0]++;
                break;

            case LEFT:
                snakeXs[0]--;
                break;
        }
    }

    private boolean detectDeath(){
        // Has the snake died?
        boolean dead = false;

        // Hit the screen edge
        if (snakeXs[0] == -1) {
            dead = true;
        }
        if (snakeXs[0] >= NUM_BLOCKS_WIDE) {
            dead = true;
        }
        if (snakeYs[0] == -1) {
            dead = true;
        }
        if (snakeYs[0] == numBlocksHigh) {
            dead = true;
        }

        // Eaten itself
        for (int i = snakeLength - 1; i > 0; i--) {
            if ((i > 4) && (snakeXs[0] == snakeXs[i]) && (snakeYs[0] == snakeYs[i])) {
                dead = true;
            }
        }

        // Hit the wall
        Boolean hitX = false;
        for (int i = wallX; i < wallLength + wallX; i++) {
            if (snakeXs[0] == i) {
                hitX = true;
            }
        }
        if (hitX && snakeYs[0] == wallY) {
            dead = true;
        }

        return dead;
    }

    public void update() {
        // If the snake eat the apple
        if (snakeXs[0] == appleX && snakeYs[0] == appleY) {
            eatApple();
        }

        moveSnake();

        if (detectDeath()) {
            // jump to game over page
            myActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(myActivity, GameOverActivity.class);
                    i.putExtra("score", score);
                    myActivity.startActivity(i);
                }
            });
        }
    }

    public void draw() {
        // Get a lock on the canvas
        if (surfaceHolder.getSurface().isValid()) {
            canvas = surfaceHolder.lockCanvas();

            // Draw Background
            Bitmap background = BitmapFactory.decodeResource(myActivity.getResources(),R.mipmap.background);
            Rect srcBg = new Rect(0,0,background.getWidth(),background.getHeight());
            Rect dstBg = new Rect(0,0,screenX,screenY);
            canvas.drawBitmap(background,srcBg, dstBg,new Paint());


            // Draw Snake
            Bitmap snakeBody = BitmapFactory.decodeResource(myActivity.getResources(),R.mipmap.snake_body);
            Rect srcSnake = new Rect(0, 0, snakeBody.getWidth(), snakeBody.getHeight());
            //Determine which way the snake is heading
            Bitmap snakeHead;
            if (heading == Heading.UP) {
                snakeHead = BitmapFactory.decodeResource(myActivity.getResources(),R.mipmap.snake_head_up);
            } else if (heading == Heading.LEFT) {
                snakeHead = BitmapFactory.decodeResource(myActivity.getResources(),R.mipmap.snake_head_left);
            } else if (heading == Heading.RIGHT) {
                snakeHead = BitmapFactory.decodeResource(myActivity.getResources(),R.mipmap.snake_head_right);
            } else {
                snakeHead = BitmapFactory.decodeResource(myActivity.getResources(),R.mipmap.snake_head_down);
            }
            // Draw the snake head
            Rect dstSnakeHead = new Rect(snakeXs[0] * blockSize,
                    (snakeYs[0] * blockSize),
                    (snakeXs[0] * blockSize) + blockSize,
                    (snakeYs[0] * blockSize) + blockSize);
            canvas.drawBitmap(snakeHead, srcSnake, dstSnakeHead,new Paint());
            // Area of the snake body (and drawing)
            for (int i = 1; i < snakeLength; i++) {
                Rect dstSnake = new Rect(snakeXs[i] * blockSize,
                        (snakeYs[i] * blockSize),
                        (snakeXs[i] * blockSize) + blockSize,
                        (snakeYs[i] * blockSize) + blockSize);
                canvas.drawBitmap(snakeBody,srcSnake, dstSnake,new Paint());
            }

            // Draw Cloud
            if (difficulty > 2) {
                Bitmap cloud = BitmapFactory.decodeResource(myActivity.getResources(), R.mipmap.cloud);
                Rect srcCloud = new Rect(0, 0, cloud.getWidth(), cloud.getHeight());
                Rect dstCloud = new Rect(cloudX * blockSize,
                        (cloudY * blockSize),
                        (cloudX * blockSize) + blockSize * cloudWidth,
                        (cloudY * blockSize) + blockSize * cloudHeight);
                // Draw the cloud
                canvas.drawBitmap(cloud, srcCloud, dstCloud, new Paint());
            }


            // Draw Apple
            Bitmap apple = BitmapFactory.decodeResource(myActivity.getResources(), R.mipmap.apple);
            Rect dstApple = new Rect(appleX * blockSize,
                    (appleY * blockSize),
                    (appleX * blockSize) + blockSize,
                    (appleY * blockSize) + blockSize);
            // Draw Apple
            canvas.drawBitmap(apple, srcSnake, dstApple, new Paint());

            // Draw Wall
            if (difficulty > 1) {
                // Wall color
                paint.setColor(Color.argb(255, 113, 64, 43));
                // draw the wall
                for (int i = 0; i < wallLength; i++) {
                    canvas.drawRect(wallX * blockSize + i * blockSize,
                            (wallY * blockSize),
                            (wallX * blockSize) + blockSize + i * blockSize,
                            (wallY * blockSize) + blockSize, paint);
                }
            }


            // Score text color
            paint.setColor(Color.argb(255, 139, 71, 38));

            // Scale the HUD text
            paint.setTextSize(90);
            canvas.drawText("Score:" + score, 10, 70, paint);

            // Unlock the canvas and reveal the graphics for this frame
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    public boolean updateRequired() {

        // Are we due to update the frame
        if(nextFrameTime <= System.currentTimeMillis()){
            // Tenth of a second has passed

            // Setup when the next update will be triggered
            nextFrameTime =System.currentTimeMillis() + MILLIS_PER_SECOND / FPS;

            // Return true so that the update and draw
            // functions are executed
            return true;
        }

        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        // I don't know why i cannot write default case here
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                if (motionEvent.getX() >= screenX / 2) {
                    switch(heading){
                        case UP:
                            heading = Heading.RIGHT;
                            break;
                        case RIGHT:
                            heading = Heading.DOWN;
                            break;
                        case DOWN:
                            heading = Heading.LEFT;
                            break;
                        case LEFT:
                            heading = Heading.UP;
                            break;
                    }
                } else {
                    switch(heading){
                        case UP:
                            heading = Heading.LEFT;
                            break;
                        case LEFT:
                            heading = Heading.DOWN;
                            break;
                        case DOWN:
                            heading = Heading.RIGHT;
                            break;
                        case RIGHT:
                            heading = Heading.UP;
                            break;

                    }
                }
        }
        return true;
    }



}