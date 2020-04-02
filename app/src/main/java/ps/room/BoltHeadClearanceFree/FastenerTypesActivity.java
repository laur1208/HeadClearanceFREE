package ps.room.BoltHeadClearanceFree;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Objects;

public class FastenerTypesActivity extends AppCompatActivity implements FastenerTypesRecyclerAdapter.ViewHolder.OnFastenerTypeClickListener {
    public static int VIBRATION_TOGGLE;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*--- if back comes from settings or fastener list activity ---*/
        if(requestCode == 1 || requestCode == 2){
            if(resultCode == RESULT_OK){
                assert data != null;
                VIBRATION_TOGGLE = Objects.requireNonNull(data.getExtras()).getInt("VIBRATION_TOGGLE");
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fastener_types);

        fetchFastenerTypes();

        displayFastenerTypes();

    }

    private void vibrate(int VIBRATION_TOGGLE){
        if(VIBRATION_TOGGLE == 1){
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                assert v != null;
                v.vibrate(VibrationEffect.createOneShot(80, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                //deprecated in API 26
                assert v != null;
                v.vibrate(80);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            intent.putExtra("VIBRATION_TOGGLE", VIBRATION_TOGGLE);
            intent.putExtra("CALLEE_ACTIVITY", this.getClass().getSimpleName());
            startActivityForResult(intent, 1);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*-------place fastener types into the recycler view-----------*/
    private void displayFastenerTypes() {
        final RecyclerView recyclerFastenerTypes = findViewById(R.id.fastener_types_recycler);
        final LinearLayoutManager fastenerTypesLayoutManager = new LinearLayoutManager(this);
        recyclerFastenerTypes.setLayoutManager(fastenerTypesLayoutManager);
        recyclerFastenerTypes.setHasFixedSize(true);
        recyclerFastenerTypes.setOverScrollMode(View.OVER_SCROLL_NEVER);

        List<FastenerType> fastenerTypeList = DataManager.getInstance().getFastenerTypes();
        FastenerTypesRecyclerAdapter fastenerTypesRecyclerAdapter = new FastenerTypesRecyclerAdapter(this, fastenerTypeList, this);
        recyclerFastenerTypes.setAdapter(fastenerTypesRecyclerAdapter);
    }

    /*-------get fastener types from database---------*/
    public void fetchFastenerTypes() {
        MyDatabaseHelper myDatabaseHelper = MyDatabaseHelper.getInstance(this);
        try {
            myDatabaseHelper.createDataBase();
        } catch (Exception e) {
            e.printStackTrace();
        }

        SQLiteDatabase database = myDatabaseHelper.getReadableDatabase();
        Cursor cursor = database.query("vw_fastener_types", null, null, null, null, null, null);

        getVibrationEffect(database);

        DataManager.loadFastenerTypes(cursor);
        myDatabaseHelper.close();
        database.close();
    }

    public void getVibrationEffect(SQLiteDatabase database){
        Cursor vibrationCursor = database.query("VibrationEffect", null, null, null, null, null, null);
        int isSetPos = vibrationCursor.getColumnIndex("IS_SET");
        while(vibrationCursor.moveToNext()){
            VIBRATION_TOGGLE = vibrationCursor.getInt(isSetPos);
        }
        vibrationCursor.close();
    }

    @Override
    public void onFastenerTypeClick(int position, View clickedCell) {
        Intent intent = new Intent(this, FastenersListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("FASTENER_TYPE_ID", position);
        intent.putExtra("VIBRATION_TOGGLE", VIBRATION_TOGGLE);
        startActivityForResult(intent, 2);
        //this.startActivity(intent);
        vibrate(VIBRATION_TOGGLE);
    }
}
