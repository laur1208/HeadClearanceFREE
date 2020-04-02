package ps.room.BoltHeadClearanceFree;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FastenerDetailRecyclerAdapter extends RecyclerView.Adapter<FastenerDetailRecyclerAdapter.ViewHolder> {
    private final LayoutInflater mLayoutInflater;
    private final int nrOfRows;
    private final int nrOfColumns;
    private final int gridSpacing;
    private final List<String> mPreviousSizes;
    private Map<String, FastenerSizes> mSizesMap;
    private Context mContext;

    /*-----variables for the type of view that will be populated in grid---------*/
    private static final int HEADER_TYPE = 1;
    private static final int BUTTON_TYPE = 2;
    private static final int TEXT_TYPE = 3;

    /*----- variables for the type of cell ----*/
    private static final int MAIN_SIZE = 0;
    private static final int A_MIN_SIZE = 1;
    private static final int A_SIZE = 2;
    private static final int A_MAX_SIZE = 3;
    private static final int B_SIZE = 4;
    private static final int C_SIZE = 5;
    private static final int D_SIZE = 6;

    /*----- other variables -----*/
    private ViewHolder.OnGridCellClickListener mOnGridCellClickListener;


    FastenerDetailRecyclerAdapter(Context context, int nrOfRows, int nrOfColumns, int gridSpacing, List<String> previousSizes, Map<String, FastenerSizes> sizesMap, ViewHolder.OnGridCellClickListener onGridCellClickListener){
        this.mOnGridCellClickListener = onGridCellClickListener;
        this.mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        this.nrOfRows = nrOfRows;
        this.nrOfColumns = nrOfColumns;
        this.gridSpacing = gridSpacing;
        this.mPreviousSizes = previousSizes;
        this.mSizesMap = sizesMap;
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        /*-------check what view type should be inflated ------------*/
        switch (viewType){
            case 1: /*---- header -----*/
            case 2: /*---- button -----*/
            case 3: /*---- field -----*/
                /* inflate the  textView */
                View itemView = mLayoutInflater.inflate(R.layout.cell_textview_layout, parent, false);
                ViewGroup.LayoutParams layoutParams = itemView.getLayoutParams();
                /* set the height of the cell */
                double cellHeight = (double)(parent.getHeight() - (nrOfRows * gridSpacing)) / (double)nrOfRows;
                layoutParams.height = (int) Math.ceil(cellHeight);
                itemView.setLayoutParams(layoutParams);
                return new ViewHolder(itemView, viewType, mOnGridCellClickListener);
              default:
                  return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int viewTypeId = holder.getItemViewType();
        switch (viewTypeId){
            case 1: /*---- header -----*/
                holder.header.setBackgroundColor(Color.GRAY);
                holder.header.setTextColor(Color.BLACK);
                holder.header.setTypeface(Typeface.DEFAULT_BOLD);
                switch (position){
                    case 0:
                        holder.header.setText(R.string.size);
                        break;
                    case 1:
                        holder.header.setText(R.string.a_min);
                        break;
                    case 2:
                        holder.header.setText("A");
                        break;
                    case 3:
                        holder.header.setText(R.string.a_max);
                        break;
                    case 4:
                        holder.header.setText("B");
                        break;
                    case 5:
                        holder.header.setText("C");
                        break;
                    case 6:
                        holder.header.setText("D");
                        break;
                }
                holder.position = position;
                break;
            case 2: /*----- buttons on the left -----*/
                holder.btn.setBackgroundResource(R.color.head_clearance_blue);
                holder.btn.setAllCaps(false);
                holder.btn.setId(position);
                holder.btn.setTag(position);
                holder.btn.setTypeface(Typeface.DEFAULT_BOLD);
                holder.position = position;
                /*--- check if we have previous sizes,
                        if the position is passed the headers row
                            if the position of the current row is less than the previous sizes list, otherwise set 'Add suze'---*/
                if(mPreviousSizes != null &&
                        position >= nrOfColumns &&
                            (Math.floor((double)position / nrOfColumns)) <= mPreviousSizes.size()){
                    holder.btn.setText(getPreviousSizeToSet(position));
                }else holder.btn.setText(R.string.add_size);
                break;
            case 3: /*----- text areas ----------*/
                holder.field.setBackgroundColor(Color.WHITE);
                holder.field.setId(position);
                holder.field.setTag(position);
                holder.field.setTypeface(Typeface.DEFAULT_BOLD);
                holder.position = position;
                /*--- check if we have previous sizes,
                       and if the position is passed the headers row
                          and if the position of the current row is less than the previous sizes list, otherwise set ''---*/
                if(mPreviousSizes != null &&
                        position >= nrOfColumns &&
                            (Math.floor((double)position / nrOfColumns)) <= mPreviousSizes.size()){
                    holder.field.setText(getPreviousSizeToSet(position));
                }else holder.field.setText(mContext.getResources().getString(R.string.empty));
                break;
        }
    }

    private String getPreviousSizeToSet(int position){
        /*--- row number - 1 to be compared with previousSizes array
        index, to know in what cell to place the size
        ...get row number compared with sizes index*/
        int row_by_position = (int) (Math.floor((double)position / nrOfColumns) - 1);

        for(int sizeKey = 0; sizeKey < mPreviousSizes.size(); sizeKey++){
            /*--- if the row of current position is in previous sizes list, add a previous size ---*/
            if(row_by_position == sizeKey){
                /*--- if we find the previous size in sizes of the fastener, add it ---*/
                if(mSizesMap.get(mPreviousSizes.get(sizeKey)) != null){
                    switch (position % nrOfColumns){
                        case MAIN_SIZE:
                            return String.valueOf(Objects.requireNonNull(mSizesMap.get(mPreviousSizes.get(sizeKey))).getMainSize());
                        case A_MIN_SIZE:
                            return String.valueOf(Objects.requireNonNull(mSizesMap.get(mPreviousSizes.get(sizeKey))).getaMinSize());
                        case A_SIZE:
                            return String.valueOf(Objects.requireNonNull(mSizesMap.get(mPreviousSizes.get(sizeKey))).getaSize());
                        case A_MAX_SIZE:
                            return String.valueOf(Objects.requireNonNull(mSizesMap.get(mPreviousSizes.get(sizeKey))).getaMaxSize());
                        case B_SIZE:
                            return String.valueOf(Objects.requireNonNull(mSizesMap.get(mPreviousSizes.get(sizeKey))).getbSize());
                        case C_SIZE:
                            return String.valueOf(Objects.requireNonNull(mSizesMap.get(mPreviousSizes.get(sizeKey))).getcSize());
                        case D_SIZE:
                            return String.valueOf(Objects.requireNonNull(mSizesMap.get(mPreviousSizes.get(sizeKey))).getdSize());
                        default: return "NOT_APPLICABLE";
                    }
                }
                /*--- otherwise add corresponding string ---*/
                else
                {
                    switch (position % nrOfColumns){
                        case MAIN_SIZE:
                            return mContext.getResources().getString(R.string.add_size);
                        case A_MIN_SIZE:
                        case A_SIZE:
                        case A_MAX_SIZE:
                        case B_SIZE:
                        case C_SIZE:
                        case D_SIZE:
                            return mContext.getResources().getString(R.string.empty);
                        default: return "NOT_APPLICABLE";
                    }
                }
            }
        }
        return null;
    }

    /*----return the number of items that will be inflated -----*/
    @Override
    public int getItemCount() {
        return nrOfColumns * nrOfRows;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(hasStableIds);
    }

    @Override
    /*-----return the type of view that must be populated in grid ------*/
    public int getItemViewType(int position) {
        /*if first row is inflated, header must be populated*/
        if(position < nrOfColumns){
            return HEADER_TYPE;
            /*if first column is populated, buttons must pe populated*/
        }else if(position % nrOfColumns == 0){
            return BUTTON_TYPE;
            /*other are text fields*/
        }else return TEXT_TYPE;
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView field, header, btn;
        int position;
        OnGridCellClickListener mOnGridCellClickListener;

        ViewHolder(@NonNull View itemView, int viewType, OnGridCellClickListener onGridCellClickListener) {
            super(itemView);
            this.mOnGridCellClickListener = onGridCellClickListener;

            itemView.setOnClickListener(this);

            switch (viewType){
                case 1:
                    header = itemView.findViewById(R.id.cell_textview);
                    break;
                case 2:
                    btn = itemView.findViewById(R.id.cell_textview);
                    btn.setId(position);
                    btn.setOnClickListener(this);
                    break;
                case 3:
                    field = itemView.findViewById(R.id.cell_textview);
                    field.setId(position);
                    field.setOnClickListener(this);
                    break;
            }
        }

        @Override
        public void onClick(View v){
            mOnGridCellClickListener.onGridCellClick(getAdapterPosition(), v);
        }

        public interface OnGridCellClickListener{
            void onGridCellClick(int position, View clickedCell);
        }
    }
}
