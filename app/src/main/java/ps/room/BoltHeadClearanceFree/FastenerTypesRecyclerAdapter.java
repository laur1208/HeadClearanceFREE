package ps.room.BoltHeadClearanceFree;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FastenerTypesRecyclerAdapter extends RecyclerView.Adapter<FastenerTypesRecyclerAdapter.ViewHolder>{
    private final List<FastenerType> mFastenerTypeList;
    private final LayoutInflater mLayoutInflater;
    /*----- other variables -----*/
    private ViewHolder.OnFastenerTypeClickListener mOnFastenerTypeClickListener;

    FastenerTypesRecyclerAdapter(Context context, List<FastenerType> fastenerTypeList, ViewHolder.OnFastenerTypeClickListener onFastenerTypeClickListener) {
        mFastenerTypeList = fastenerTypeList;
        mLayoutInflater = LayoutInflater.from(context);
        this.mOnFastenerTypeClickListener = onFastenerTypeClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.fastener_type_item, parent, false);
        float height = parent.getHeight();
        itemView.getLayoutParams().height = Math.round(height/3);
        return new ViewHolder(itemView, mOnFastenerTypeClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FastenerType fastenerType = mFastenerTypeList.get(position);
        holder.mFastenerType.setText(fastenerType.getFastenerType());
        holder.mFastenerImage.setImageBitmap(fastenerType.getImageBitmap());
        //holder.mCurrentPosition = position;
        holder.mCurrentPosition  = Integer.parseInt(fastenerType.getId());
    }

    @Override
    public int getItemCount() {
        return mFastenerTypeList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        final TextView mFastenerType;
        final ImageView mFastenerImage;
        int mCurrentPosition;
        OnFastenerTypeClickListener mOnFastenerTypeClickListener;

        ViewHolder(@NonNull View itemView, OnFastenerTypeClickListener onFastenerTypeClickListener) {
            super(itemView);
            mFastenerType = itemView.findViewById(R.id.fastener_type);
            mFastenerImage = itemView.findViewById(R.id.fastener_image);
            this.mOnFastenerTypeClickListener = onFastenerTypeClickListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mOnFastenerTypeClickListener.onFastenerTypeClick(mCurrentPosition, v);
        }

        public interface OnFastenerTypeClickListener{
            void onFastenerTypeClick(int position, View clickedCell);
        }
    }
}
