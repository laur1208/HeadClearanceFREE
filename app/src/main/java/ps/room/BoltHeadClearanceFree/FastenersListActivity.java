package ps.room.BoltHeadClearanceFree;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import ps.room.BoltHeadClearanceFree.DatabaseTablesContract.FastenerDescriptionEntry;

public class FastenersListActivity extends AppCompatActivity implements FastenersListRecyclerAdapter.ViewHolder.OnFastenerListener {
    private static final String TAG = "==FASTENER LIST LOG==";
    private MyDatabaseHelper mMyDatabaseHelper;
    private FastenersListRecyclerAdapter mFastenerTypesRecyclerAdapter;
    private List<FastenerDescription> mFastenerDescriptionList;
    private List<String> previousSizes;
    public static int VIBRATION_TOGGLE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fasteners_list);

        Bundle extras = getIntent().getExtras();
        int fastenerTypeId;
        if (extras == null) {
            fastenerTypeId = -1;
        }else{
            fastenerTypeId = extras.getInt("FASTENER_TYPE_ID");
            VIBRATION_TOGGLE = extras.getInt("VIBRATION_TOGGLE");
            if(extras.getStringArrayList("sizes") != null){
                previousSizes = extras.getStringArrayList("sizes");
            }
        }

        fetchFastenerDescriptionList(fastenerTypeId);

        displayFastenersList();
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

    public void fetchFastenerDescriptionList(int fastenerTypeId) {
        mMyDatabaseHelper = MyDatabaseHelper.getInstance(this);
            try {

            mMyDatabaseHelper.createDataBase();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String selection = FastenerDescriptionEntry.FASTENER_TYPE_ID + " = ?";
        String[] selectionArgs = {Integer.toString(fastenerTypeId)};

        SQLiteDatabase database = mMyDatabaseHelper.getReadableDatabase();
        Cursor cursor = database.query("vw_fasteners_description", null, selection, selectionArgs, null, null, null);

        DataManager.loadFastenerDescriptionList(cursor);
        mMyDatabaseHelper.close();
        database.close();
    }

    private void displayFastenersList() {
        RecyclerView recyclerFastenersList = findViewById(R.id.fasteners_list_recycler);
        final LinearLayoutManager fastenerTypesLayoutManager = new LinearLayoutManager(this);
        recyclerFastenersList.setLayoutManager(fastenerTypesLayoutManager);
        recyclerFastenersList.setHasFixedSize(true);

        mFastenerDescriptionList = DataManager.getInstance().getFastenersList();
        mFastenerTypesRecyclerAdapter = new FastenersListRecyclerAdapter(this, mFastenerDescriptionList, this);
        recyclerFastenersList.setAdapter(mFastenerTypesRecyclerAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        Intent backIntent = new Intent(this, FastenerTypesActivity.class);
        backIntent.putExtra("VIBRATION_TOGGLE", VIBRATION_TOGGLE);
        setResult(RESULT_OK, backIntent);
        finish();
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
            startActivityForResult(intent, 2);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        mMyDatabaseHelper.close();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            if(resultCode == RESULT_OK){
                assert data != null;
                previousSizes = Objects.requireNonNull(data.getExtras()).getStringArrayList("sizes");
                VIBRATION_TOGGLE= Objects.requireNonNull(data.getExtras().getInt("VIBRATION_TOGGLE"));
            }
        }
        else if(requestCode == 2){
            if(resultCode == RESULT_OK){
                assert data != null;
                VIBRATION_TOGGLE = Objects.requireNonNull(data.getExtras()).getInt("VIBRATION_TOGGLE");
            }
        }
    }

    @Override
    public void onFastenerClick(int position, View clickedView, int fastener_id, int fastener_type_id, int value_to_set_on_favorite) {
        FastenerDescription fastenerDescription = mFastenerDescriptionList.get(position);
        /*--- check if fastener is on free version or not ---*/
        if (fastenerDescription.getIsAvailable() == 1){
            /*--- if favorite click ---*/
            if (clickedView.getId() == R.id.favorite_img) {
                updateFastenerFavoriteValue(fastener_id, fastener_type_id, value_to_set_on_favorite);
                /*--- if fastener click ---*/
            } else {
                Intent intent = new Intent(this, FastenerDetailActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("fastenerTypeId", fastener_type_id);
                intent.putExtra("fastenerId", fastener_id);
                intent.putExtra("VIBRATION_TOGGLE", VIBRATION_TOGGLE);
                if(previousSizes != null){
                    intent.putExtra("sizes", (Serializable) previousSizes);
                }
                startActivityForResult(intent, 1);
            }
            vibrate(VIBRATION_TOGGLE);
        }else{
            Toast.makeText(this,"Fastener available only on PRO version. Go to settings to check PRO version.",Toast.LENGTH_SHORT).show();
        }
    }

    void updateFastenerFavoriteValue(int fastener_id, int fastener_type_id, int value_to_set_on_favorite){
        mMyDatabaseHelper = MyDatabaseHelper.getInstance(this);
        try {

            mMyDatabaseHelper.createDataBase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ContentValues values = new ContentValues();
        values.put(FastenerDescriptionEntry.IS_FAVORITE, value_to_set_on_favorite);
        String whereClause = FastenerDescriptionEntry.ID + " = ?";
        String[] whereArgs = {fastener_id +" "};

        SQLiteDatabase database = mMyDatabaseHelper.getReadableDatabase();

        try{
            int nr_of_updated_rows = database.update("FastenerDescription", values, whereClause, whereArgs);

            if(nr_of_updated_rows > 0){
                mMyDatabaseHelper.close();
                database.close();

                if(value_to_set_on_favorite == 1)
                    Toast.makeText(this,"Favorite fastener moved up",Toast.LENGTH_SHORT).show();

                fetchFastenerDescriptionList(fastener_type_id);
                mFastenerDescriptionList = DataManager.getInstance().getFastenersList();
                mFastenerTypesRecyclerAdapter.notifyDataSetChanged();
            }
        }catch (SQLException ex)
        {
            //Log.i(TAG,"[Update fastener error]: " + ex);
        }
    }
}
