package sg.edu.np.week_6_whackamole_3_0;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class MyDBHandler extends SQLiteOpenHelper {

    private static final String FILENAME = "MyDBHandler.java";
    public static final String DATABASE_NAME = "WhackAMole.database";
    private static final int DATABASE_VERSION = 3;
    private static final String TAG = "Whack-A-Mole3.0!";

    public MyDBHandler(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        // initialise the database
    }
    @Override
    public void onCreate(SQLiteDatabase database)
    {
        database.execSQL(UserData.CREATE_USERS_TABLE);
        Log.v(TAG, "DATABASE Created: " + UserData.CREATE_USERS_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion)
    {
        database.execSQL(UserData.DROP_USERS_TABLE);
        onCreate(database);
        Log.d(TAG, "Update database: ");
    }

    public void onDowngrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.setVersion(oldVersion);
    }

    public void addUser(UserData userData)
    {

        SQLiteDatabase database = this.getWritableDatabase();

        for (int i = 0; i < 10; i++){
            ContentValues values = new ContentValues();
            values.put(UserData.COLUMN_USER_NAME, userData.getMyUserName());
            values.put(UserData.COLUMN_USER_PASSWORD, userData.getMyPassword());
            values.put(UserData.COLUMN_USER_LEVELS, userData.getLevels().get(i));
            values.put(UserData.COLUMN_USER_SCORES, userData.getScores().get(i));
//            //This adds the user to the database based on the information given.
            Log.v(TAG, FILENAME + ": Adding data for Database: " + values.toString());
            database.insert(UserData.TABLE_NAME, null, values);
        }
        database.close();
    }

    public UserData findUser(String username)
    {
        SQLiteDatabase database = this.getReadableDatabase();

        String query = "SELECT * FROM " + UserData.TABLE_NAME + " WHERE " +
                UserData.COLUMN_USER_NAME  + " = \"" + username + "\"";
        Log.d(TAG, FILENAME + ": Find user from database: "+ query);

        Cursor res = database.rawQuery( query, null );

        String password;
        ArrayList<Integer> levelList = new ArrayList<>();
        ArrayList<Integer> scoreList = new ArrayList<>();

        if(res.moveToFirst()){
            password = res.getString(1);
            do{
                levelList.add(res.getInt(2));
                scoreList.add(res.getInt(3));

            }

            while(res.moveToNext());
        }
        else {
            Log.v(TAG, FILENAME + ": No data found!");
            res.close();
            database.close();
            return null;

        }


        UserData queryData = new UserData(username, password, levelList, scoreList);
        Log.v(TAG, FILENAME + ": QueryData: " + queryData.getLevels().toString() + queryData.getScores().toString());

        res.close();
        database.close();

        return queryData;
    }

    public boolean deleteAccount(String username) {
        String query = "SELECT * FROM " + UserData.TABLE_NAME +
                " WHERE " + UserData.COLUMN_USER_NAME  + " = \"" + username + "\"";

        Log.v(TAG, FILENAME + ": Database delete user: " + query);
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor res = database.rawQuery(query, null);

        if(res.moveToFirst()) {
            do {
                database.delete(UserData.TABLE_NAME, UserData.COLUMN_USER_NAME + " =?", new String[] { username });
            } while (res.moveToNext());

        }

        else{
            Log.v(TAG, FILENAME+ ": Data not found!");
            res.close();
            database.close();
            return false;
        }

        res.close();
        database.close();
        return true;
    }
}
