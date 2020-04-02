package ps.room.BoltHeadClearanceFree;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

public class SettingsActivity extends AppCompatActivity {
    public static int VIBRATION_TOGGLE;
    public static String CALLEE_ACTIVITY;


    @Override
    public void onBackPressed() {
        try {
            Class<?> c = Class.forName("ps.room.BoltHeadClearanceFree."+CALLEE_ACTIVITY);
            Intent backIntent = new Intent(this, c);
            backIntent.putExtra("VIBRATION_TOGGLE", VIBRATION_TOGGLE);
            setResult(RESULT_OK, backIntent);
            finish();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        Bundle extras = getIntent().getExtras();
        assert extras != null;
        VIBRATION_TOGGLE = extras.getInt("VIBRATION_TOGGLE");
        CALLEE_ACTIVITY = extras.getString("CALLEE_ACTIVITY");

        getVibrationEffect();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public void getVibrationEffect(){
        MyDatabaseHelper myDatabaseHelper = MyDatabaseHelper.getInstance(this);
        try {

            myDatabaseHelper.createDataBase();
        } catch (Exception e) {
            e.printStackTrace();
        }

        SQLiteDatabase database = myDatabaseHelper.getReadableDatabase();
        Cursor vibrationCursor = database.query("VibrationEffect", null, null, null, null, null, null);
        int isSetPos = vibrationCursor.getColumnIndex("IS_SET");
        while(vibrationCursor.moveToNext()){
            VIBRATION_TOGGLE = vibrationCursor.getInt(isSetPos);
        }
        vibrationCursor.close();
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        private FragmentActivity mContext;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            mContext = this.getActivity();

            assert mContext != null;
            final SwitchPreferenceCompat vibration_toggle = findPreference(mContext.getString(R.string.vibration_toggle));
            final Preference pro_version = findPreference(mContext.getString(R.string.pro_version));
            if(VIBRATION_TOGGLE == 0) {
                assert vibration_toggle != null;
                vibration_toggle.setChecked(false);
            }
            else {
                assert vibration_toggle != null;
                vibration_toggle.setChecked(true);
            }

            /*--- vibration toggle tap ---*/
            vibration_toggle.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    if(vibration_toggle.isChecked()){
                        updateVibrationSetting(0);
                        VIBRATION_TOGGLE = 0;

                        // Checked the switch programmatically
                        vibration_toggle.setChecked(false);
                    }else {
                        updateVibrationSetting(1);
                        VIBRATION_TOGGLE = 1;

                        // Unchecked the switch programmatically
                        vibration_toggle.setChecked(true);
                    }
                    return false;
                }
            });

            /*--- pro version tap ---*/
            assert pro_version != null;
            pro_version.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.PRO_VERSION_LINK))));
                    return false;
                }
            });
        }

        private void updateVibrationSetting(int value){
            MyDatabaseHelper myDatabaseHelper = MyDatabaseHelper.getInstance(mContext);
            try {

                myDatabaseHelper.createDataBase();
            } catch (Exception e) {
                e.printStackTrace();
            }
            ContentValues values = new ContentValues();
            values.put("IS_SET", value);
            SQLiteDatabase database = myDatabaseHelper.getReadableDatabase();
            database.update("VibrationEffect", values, null, null);
        }

    }
}