package ps.room.BoltHeadClearanceFree;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import ps.room.BoltHeadClearanceFree.DatabaseTablesContract.FastenerDescriptionEntry;

public class FastenerDetailActivity extends AppCompatActivity implements  FastenerDetailRecyclerAdapter.ViewHolder.OnGridCellClickListener {
    /*----------- grid variables -----------*/
    private static int NR_OF_COLUMNS;
    private static int NR_OF_ROWS;
    private static int NR_OF_CELLS;

    /*------------ other variables -----------*/
    private static final String TAG = "==GRID CLICK==";
    private static int mFastenerId;
    private static int mFastenerTypeId;
    private static Bitmap mFastenerLegendImage;
    private Map<String, FastenerSizes> mSizesMap;
    private int clicked_cell_position;
    private RecyclerView mRecyclerView;
    private List<String> mPreviousSizes;
    public static int VIBRATION_TOGGLE;

    @Override
    public void onBackPressed() {
        List<String> sizes = getSizesInGrid();
        Intent backIntent = new Intent(this, FastenersListActivity.class);
        backIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        backIntent.putExtra("sizes", (Serializable) sizes);
        backIntent.putExtra("FASTENER_TYPE_ID", mFastenerTypeId);
        backIntent.putExtra("VIBRATION_TOGGLE", VIBRATION_TOGGLE);
        setResult(RESULT_OK, backIntent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fastener_detail);

        /*------get bundle info from previous screen*/
        getBundleInformation();

        /*-----------fetch data from database, get fastener type, fastener image, fastener legend image and fastener sizes----------*/
        fetchDataFromDatabase(mFastenerId, mFastenerTypeId);

        /*-----grid recycler adapter-----*/
        mRecyclerView = findViewById(R.id.grid_recycler);
        int GRID_SPACING = 1;
        FastenerDetailRecyclerAdapter fastenerDetailRecyclerAdapter = new FastenerDetailRecyclerAdapter(this, NR_OF_ROWS, NR_OF_COLUMNS, GRID_SPACING, mPreviousSizes, mSizesMap, this);
        //mFastenerDetailRecyclerAdapter.setHasStableIds(true);

        /*--- set custom grid for the recycler ---*/
        CustomGridLayoutManager customGridLayoutManager = new CustomGridLayoutManager(this, NR_OF_COLUMNS);
        customGridLayoutManager.setScrollEnabled();
        mRecyclerView.setLayoutManager(customGridLayoutManager);
        mRecyclerView.setAdapter(fastenerDetailRecyclerAdapter);
        mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(NR_OF_COLUMNS, GRID_SPACING, true));

        /*---- set legend image image -----*/
        ImageView legendImage = findViewById(R.id.legend_image_frm);
        legendImage.setImageBitmap(mFastenerLegendImage);

        /*---- set fastener image -----*/
        ImageView fastenerImage = findViewById(R.id.fastener_image_frm);
        fastenerImage.setImageBitmap(DataManager.getInstance().getFastenerImage());

        /*---- set fastener name ----*/
        TextView fastenerName = findViewById(R.id.fastener_name_rel);
        fastenerName.setText(DataManager.getInstance().getFastenerName());
        fastenerName.setTextColor(Color.BLACK);

        /*---- change tab bar name with fastener type ----*/
        changeActionBar();

        /*---- prepare and inflate menu -----*/
        //prepareAndInflateMenu();

        /*---- add sizes to menu-----*/
        //addSizesToMenu();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            if(resultCode == RESULT_OK){
                assert data != null;
                VIBRATION_TOGGLE = Objects.requireNonNull(data.getExtras()).getInt("VIBRATION_TOGGLE");
            }
        }
    }

    /*---- get the sizes present in grid ----*/
    private List<String> getSizesInGrid(){
        /*-- row counter---*/
        int rowCnt = 1;
        List<String> sizes = new ArrayList<>();
        do{
            /*--- position reference for Main size columns ---*/
            int sizeColumn = rowCnt * NR_OF_COLUMNS;
            /*--- get reference of the view holder of sizeColumn position ----*/
            RecyclerView.ViewHolder clickedViewHolder = mRecyclerView.findViewHolderForItemId(sizeColumn);
            TextView cellTextView = clickedViewHolder.itemView.findViewWithTag(sizeColumn);
            String size = (String) cellTextView.getText();
            /*---- check if size is different from Add size text*/
            if(!size.equals(getResources().getString(R.string.add_size))){
                /*--- add size to sizes array ---*/
                sizes.add(size);
            }
            rowCnt++;
        }while(rowCnt * NR_OF_COLUMNS < NR_OF_CELLS);
        return sizes;
    }

    /*---- check size in grid -----*/
    private boolean checkSizeInGrid(String size){
        List<String> sizesInGrid = getSizesInGrid();
        return !sizesInGrid.contains(size);
    }

    /*----sets the value on a cell in the grid -----*/
    private void setCellGridValue(int position, String value){
        RecyclerView.ViewHolder clickedViewHolder = mRecyclerView.findViewHolderForItemId(position);
        TextView cellTextView = clickedViewHolder.itemView.findViewWithTag(position);
        cellTextView.setText(value);
    }

    private void startCellAnimation(int position){
        RecyclerView.ViewHolder clickedViewHolder = mRecyclerView.findViewHolderForItemId(position);
        final TextView cellTextView = clickedViewHolder.itemView.findViewWithTag(position);

        ValueAnimator valueAnimator = ObjectAnimator.ofInt(
                cellTextView, // Target object
                "backgroundColor", // Property name
                Color.RED, // Value
                Color.WHITE // Value
        );
        // Set value animator evaluator
        valueAnimator.setEvaluator(new ArgbEvaluator());
        // Set animation duration in milliseconds
        valueAnimator.setDuration(400);
        // Animation repeat count and mode
        valueAnimator.setRepeatCount(0);
        valueAnimator.setRepeatMode(ValueAnimator.RESTART);
        // Finally, start the animation
        valueAnimator.start();
    }

    /*----get the value on a cell in the grid -----*/
    private String getCellGridValue(int position){
        RecyclerView.ViewHolder clickedViewHolder = mRecyclerView.findViewHolderForItemId(position);
        TextView cellTextView = clickedViewHolder.itemView.findViewWithTag(position);
        return cellTextView.getText().toString();
    }

    private void changeActionBar() {
        ActionBar actionBar = getSupportActionBar();
        switch (mFastenerTypeId){
            case 1:
                Objects.requireNonNull(actionBar).setTitle(R.string.counterbore);
                break;
            case 2:
                Objects.requireNonNull(actionBar).setTitle(R.string.countersunk);
                break;
            case 3:
                Objects.requireNonNull(actionBar).setTitle(R.string.spotface);
                break;
        }
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

    /*---get info from previous activity---*/
    private void getBundleInformation() {
        Bundle extras = getIntent().getExtras();
        assert extras != null;
        mFastenerId = extras.getInt("fastenerId");
        mFastenerTypeId = extras.getInt("fastenerTypeId");
        VIBRATION_TOGGLE = extras.getInt("VIBRATION_TOGGLE");

        /*--- set the number of cells in grid based on fastener type ----*/
        if(mFastenerTypeId == 1 || mFastenerTypeId == 3){
            NR_OF_CELLS = 30;
            NR_OF_COLUMNS = 6;
            NR_OF_ROWS = NR_OF_CELLS / NR_OF_COLUMNS;
        }else{
            NR_OF_CELLS = 35;
            NR_OF_COLUMNS = 7;
            NR_OF_ROWS = NR_OF_CELLS / NR_OF_COLUMNS;
        }

        /*--- check if we have sizes previously selected ---*/
        if(extras.getStringArrayList("sizes") != null){
            mPreviousSizes = extras.getStringArrayList("sizes");
        }
    }

    /*---get fastener type, fastener name, fastener image, fastener legend image and fastener sizes---*/
    private void fetchDataFromDatabase(int mFastenerId, int mFastenerTypeId) {
        /*-----db instance-------*/
        MyDatabaseHelper myDatabaseHelper = MyDatabaseHelper.getInstance(this);
        try { myDatabaseHelper.createDataBase(); }
        catch (Exception e) { e.printStackTrace(); }

        /*---------open db for queries-------------*/
        SQLiteDatabase database = myDatabaseHelper.getReadableDatabase();

        /*--------get fastener sizes--------------*/
        String fastenerSizesSelectionColumn = FastenerDescriptionEntry.ID + " = ?";
        String[] fastenerSizesSelectionArgs = {Integer.toString(mFastenerId)};
        Cursor fastenerSizesCursor = database.query("vw_fastener_sizes",null,fastenerSizesSelectionColumn,fastenerSizesSelectionArgs,null,null,null);
        DataManager.loadFastenerSizes(fastenerSizesCursor);

        /*---- load sizes into object -----*/
        //mFastenerSizesList = DataManager.getInstance().getFastenerSizesList();

        /*--- place fastener sizes into key value hash map of M size and Fastener object*/
        mSizesMap = DataManager.getInstance().getSizesHashMap();

        /*---------get fastener legend image----------*/
        String[] fastenerTypeIdArgs = {Integer.toString(mFastenerTypeId)};
        Cursor fastenerLegendImageCursor = database.rawQuery(getString(R.string.select_fastener_legend_image), fastenerTypeIdArgs);
        int fastenerLegendImagePos = fastenerLegendImageCursor.getColumnIndex("Image");
        while(fastenerLegendImageCursor.moveToNext()){
            byte[] image = fastenerLegendImageCursor.getBlob(fastenerLegendImagePos);
            mFastenerLegendImage = BitmapFactory.decodeByteArray(image,0,image.length);
        }
        fastenerLegendImageCursor.close();


        myDatabaseHelper.close();
        database.close();
    }

    /*--- set the alert dialog with the sizes ---*/
    private void showSizesDialog(){
        final CharSequence[] sizes = mSizesMap.keySet().toArray(new CharSequence[0]);
        AlertDialog.Builder sizesDialog = new AlertDialog.Builder(this);
        sizesDialog.setTitle("Select a size");
        sizesDialog.setItems(sizes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                setSizeOnGrid(sizes[item].toString());
            }
        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        AlertDialog alert = sizesDialog.create();
        alert.show();
    }

    /*--- dialog for deleting the row ---*/
    private void showDeleteSizeDialog(final String size, final int mainSizePosition){
        AlertDialog.Builder deleteSizeDialog = new AlertDialog.Builder(this);
        deleteSizeDialog.setTitle("Delete "+ size + "?");
        deleteSizeDialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteRowFromGrid(mainSizePosition);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        AlertDialog alert = deleteSizeDialog.create();
        alert.show();
    }

    /*--- place sizes in grid ---*/
    private void setSizeOnGrid(String size) {
        /*--- check if selected size already in grid ---*/
        if(checkSizeInGrid(size))
        {
            /*---check fastener type ----*/
            switch (mFastenerTypeId){
                case 1:
                case 3:
                    setCellGridValue(clicked_cell_position, String.valueOf(Objects.requireNonNull(mSizesMap.get(size)).getMainSize()));
                    setCellGridValue(clicked_cell_position + 1, String.valueOf(Objects.requireNonNull(mSizesMap.get(size)).getaMinSize()));
                    setCellGridValue(clicked_cell_position + 2, String.valueOf(Objects.requireNonNull(mSizesMap.get(size)).getaSize()));
                    setCellGridValue(clicked_cell_position + 3, String.valueOf(Objects.requireNonNull(mSizesMap.get(size)).getaMaxSize()));
                    setCellGridValue(clicked_cell_position + 4, String.valueOf(Objects.requireNonNull(mSizesMap.get(size)).getbSize()));
                    setCellGridValue(clicked_cell_position + 5, String.valueOf(Objects.requireNonNull(mSizesMap.get(size)).getcSize()));
                    break;
                case 2:
                    setCellGridValue(clicked_cell_position, String.valueOf(Objects.requireNonNull(mSizesMap.get(size)).getMainSize()));
                    setCellGridValue(clicked_cell_position + 1, String.valueOf(Objects.requireNonNull(mSizesMap.get(size)).getaMinSize()));
                    setCellGridValue(clicked_cell_position + 2, String.valueOf(Objects.requireNonNull(mSizesMap.get(size)).getaSize()));
                    setCellGridValue(clicked_cell_position + 3, String.valueOf(Objects.requireNonNull(mSizesMap.get(size)).getaMaxSize()));
                    setCellGridValue(clicked_cell_position + 4, String.valueOf(Objects.requireNonNull(mSizesMap.get(size)).getbSize()));
                    setCellGridValue(clicked_cell_position + 5, String.valueOf(Objects.requireNonNull(mSizesMap.get(size)).getcSize()));
                    setCellGridValue(clicked_cell_position + 6, String.valueOf(Objects.requireNonNull(mSizesMap.get(size)).getdSize()));
                    break;
            }
        }
    }

    /*--- set row with corresponding position with empty values ---*/
    private void deleteRowFromGrid(int position){
        switch (mFastenerTypeId){
            case 1:
            case 3:
                setCellGridValue(position, getResources().getString(R.string.add_size));
                setCellGridValue(position + 1, getResources().getString(R.string.empty));
                setCellGridValue(position + 2, getResources().getString(R.string.empty));
                setCellGridValue(position + 3, getResources().getString(R.string.empty));
                setCellGridValue(position + 4, getResources().getString(R.string.empty));
                setCellGridValue(position + 5, getResources().getString(R.string.empty));
                break;
            case 2:
                setCellGridValue(position, getResources().getString(R.string.add_size));
                setCellGridValue(position + 1, getResources().getString(R.string.empty));
                setCellGridValue(position + 2, getResources().getString(R.string.empty));
                setCellGridValue(position + 3, getResources().getString(R.string.empty));
                setCellGridValue(position + 4, getResources().getString(R.string.empty));
                setCellGridValue(position + 5, getResources().getString(R.string.empty));
                setCellGridValue(position + 6, getResources().getString(R.string.empty));
                break;
        }
    }

    private void deleteSize(int position) {
        /*--- get the equivalent row of the position ---*/
        int row = (int)(Math.floor((double)position / NR_OF_COLUMNS));
        int firstColumnInRow = row * NR_OF_COLUMNS;
        /*--- get the size from the first column ---*/
        String mainSize = getCellGridValue(firstColumnInRow);

        /*--- start animation on cells ---*/
        for(int i = 1; i < NR_OF_COLUMNS; i++){
            startCellAnimation(firstColumnInRow + i);
        }

        /*--- show delete dialog ---*/
        showDeleteSizeDialog(mainSize, row * NR_OF_COLUMNS);
    }

    @Override
    public void onGridCellClick(int position, View clickedCell) {
        /*----header click -----*/
        if(position < NR_OF_COLUMNS){
            //Log.d(TAG, "onGridCellClick: HEADER");
        }
        /*---- button click -----*/
        else if (position % NR_OF_COLUMNS == 0){
            clicked_cell_position = position;
            showSizesDialog();
            vibrate(VIBRATION_TOGGLE);
        }
        /*----- field click ----*/
        else {
            /*--- if the value in field is not empty, show dialog for deleting ---*/
            if(!getCellGridValue(position).equals(getResources().getString(R.string.empty))){
                deleteSize(position);
                vibrate(VIBRATION_TOGGLE);
            }
        }
    }
}
