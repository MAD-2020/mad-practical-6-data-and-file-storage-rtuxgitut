package sg.edu.np.week_6_whackamole_3_0;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Random;

import static java.lang.String.format;
import static java.lang.String.valueOf;

public class Main4Activity extends AppCompatActivity {

    private static final String FILENAME = "Main4Activity.java";
    private static final String TAG = "Whack-A-Mole3.0!";
    private boolean resumeFlag = false;
    private int first_last_location = 0;
    private int second_last_location = 0;
    private Button back_button;
    private MyDBHandler database;

    Random ran = new Random();
    CountDownTimer readyTimer;
    int delay,level;
    String username;
    private int score = 0;
    TextView result;

    private final Handler handler = new Handler();
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Log.v(TAG, "New Mole Location!");
            setNewMoles();
            //10 second delay before calling runtime
            handler.postDelayed(this, delay * 1000);
        }
    };


    private void readyCountdown(){

        readyTimer = new CountDownTimer(10000, 1000){
            public void onTick(long millisUntilFinished){
                final Toast toast = Toast.makeText(getApplicationContext(),
                        format("Get Ready In %d seconds", millisUntilFinished/1000), Toast.LENGTH_SHORT);
                toast.show();
                Log.v(TAG, "Ready CountDown!" + millisUntilFinished/ 1000);
                Handler toast_handler = new Handler();
                toast_handler.postDelayed(new Runnable() { //this is to make sure every toast only run for 1 second
                    @Override
                    public void run() {
                        toast.cancel();
                    }
                }, 1000);
            }

            public void onFinish(){
                Toast.makeText(getApplicationContext(), "GO!", Toast.LENGTH_SHORT).show();
                Log.v(TAG, "Ready CountDown Complete!");
                readyTimer.cancel();
                setNewMoles();
                placeMoleTimer();
                populateButtons();
                resumeFlag = true; // Work after the countdown

            }
        };
        readyTimer.start();
    }

    private void placeMoleTimer(){
        handler.postDelayed(runnable, delay * 1000); // this will be called every "delay"
    }

    private static final int[] BUTTON_IDS = {R.id.button1, R.id.button2, R.id.button3, R.id.button4,
            R.id.button5, R.id.button6, R.id.button7, R.id.button8, R.id.button9};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);

        database = new MyDBHandler(this);

        result = findViewById(R.id.message);
        back_button = findViewById(R.id.back_button);

        Bundle b = getIntent().getExtras();
        username = b.getString("username");
        level = b.getInt("level");
        delay = Math.abs(level - 11); // Change speed where the mole changes its position.
        Log.v(TAG, FILENAME+ ": Load level " + level + " for: " + username);
        Log.d(TAG, "Level: "+ level + " Delay: "+ delay + "s");

        readyCountdown();
        result.setText(String.valueOf(score));
        Log.v(TAG, "Current Score: " + score);

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordScore();
            }
        });

    }
    @Override
    protected void onStart(){
        super.onStart();
    }

    @Override
    protected void onPause() {
        // Pause the Whack a Mole game when activity is paused
        Log.v(TAG,"Pause");
        handler.removeCallbacks(runnable);
        super.onPause();
    }

    @Override
    protected void onResume() {
        // to resume the place mole timer from the pause state
        Log.v(TAG,"Resume!");
        if (resumeFlag){
            placeMoleTimer(); //this will only be triggered after the countdown by turning the flag
        }
        super.onResume();
    }

    private void moleCheck(Button checkButton)
    {
        if (checkButton.getText().toString().equals("*")){
            score++;
            Log.v(TAG, "Hit, score added!");
        }

        else{
            if (score > 0){
                score--;
                Log.v(TAG, "Missed, point deducted!");
            }

            else{
                Log.v(TAG, "Missed!");
            }
        }
        result.setText(valueOf(score));

    }

    private void populateButtons(){
        for(final int id : BUTTON_IDS){
            Button btn = findViewById(id);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int _id = view.getId();
                    Button click_button = (Button) findViewById(_id);
                    moleCheck(click_button);
                    handler.removeCallbacks(runnable);
                    setNewMoles();
                    placeMoleTimer();

                }
            });
        }
    }

    public void setNewMoles()
    {
        if (level < 6)
        { // set a random mole
            int randomLocation = ran.nextInt(9);

            Button mole_button = findViewById(BUTTON_IDS[randomLocation]);
            mole_button.setText("*");

            Button empty_button = findViewById(BUTTON_IDS[first_last_location]);
            empty_button.setText("O");
            first_last_location = randomLocation;
        }

        else { // set two random moles
            int ranLocation = ran.nextInt(9);
            int ranLocation2 = ran.nextInt(9);
            // To ensure moles are in different locations
            while (ranLocation == ranLocation2){
                ranLocation2 = ran.nextInt(9);
            }

            Button mole_button = findViewById(BUTTON_IDS[ranLocation]);
            mole_button.setText("*");

            Button mole_button2 = findViewById(BUTTON_IDS[ranLocation2]);
            mole_button2.setText("*");

            Button empty_button = findViewById(BUTTON_IDS[first_last_location]);
            empty_button.setText("O");

            Button empty_button2 = findViewById(BUTTON_IDS[second_last_location]);
            empty_button2.setText("O");

            first_last_location = ranLocation;
            second_last_location = ranLocation2;
        }

    }

    private void recordScore()
    {
        Log.v(TAG, FILENAME + ": Update User Score...");
        UserData userData = database.findUser(username);

        readyTimer.cancel(); // Cancel the countdown timer
        handler.removeCallbacks(runnable); // Interrupt the runtime of Mole Timer


        int highest_score = userData.getScores().get(level-1);

        if (score > highest_score){
            // only update the score when the current score > highest record
            Log.d(TAG, "updateUserScore;");
            userData.getScores().set(level - 1,score);
            database.deleteAccount(username);
            database.addUser(userData);
        }

        // redirect back to level select page
        Log.v(TAG, FILENAME + ": Redirect to level select page");
        Intent activityName = new Intent(Main4Activity.this, Main3Activity.class);
        Bundle extras = new Bundle();
        extras.putString("username", username);
        activityName.putExtras(extras);

        startActivity(activityName);
    }

}
