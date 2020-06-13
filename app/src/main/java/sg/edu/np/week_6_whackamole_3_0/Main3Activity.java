package sg.edu.np.week_6_whackamole_3_0;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static java.lang.String.format;

public class Main3Activity extends AppCompatActivity {

    private static final String FILENAME = "Main3Activity.java";
    private static final String TAG = "Whack-A-Mole3.0!";

    private Button backlogin;
    CustomScoreAdaptor mAdapter;
    MyDBHandler database;
    private String username;
    RecyclerView scoreRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        database = new MyDBHandler(this);
        backlogin = findViewById(R.id.backtrack_login);

        Bundle b = getIntent().getExtras();
        username = b.getString("username");

        Log.v(TAG, FILENAME + ": Show level for User: "+ username);

        UserData userData = database.findUser(username);

        //Allow user to scroll the recycler view
        scoreRecyclerView = findViewById(R.id.level_scroller_menu);
        scoreRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new CustomScoreAdaptor(this, userData);
        scoreRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new CustomScoreAdaptor.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Log.d(TAG, "onItemClick: " + position);
                nextLevelQuery(username, position+1);
            }
        });

        backlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect user back to login page
                Log.v(TAG, FILENAME + ": Redirect to login page");
                Intent redirect_to_login = new Intent(Main3Activity.this, MainActivity.class);
                startActivity(redirect_to_login);
                finish();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    private void nextLevelQuery(final String username, final int level){
        Log.v(TAG, "Option given to user!");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Warning! Whack-A-Mole Incoming!");
        builder.setMessage(format("Would you like to challenge Level %d ?",level));
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int id){
                Log.v(TAG, "User accepts the challenge!");
                nextLevel(username, level);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int id){
                Log.v(TAG,"User decline!");
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void nextLevel(String username, int level){
        // redirect to whack-a-mole gaming page
        Intent activityName = new Intent(Main3Activity.this, Main4Activity.class);
        Bundle extras = new Bundle();
        extras.putString("username", username);
        extras.putInt("level", level);
        activityName.putExtras(extras);
        startActivity(activityName);
    }
}
