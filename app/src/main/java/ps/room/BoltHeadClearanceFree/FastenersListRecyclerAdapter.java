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

public class FastenersListRecyclerAdapter extends RecyclerView.Adapter<FastenersListRecyclerAdapter.ViewHolder>{
    private List<FastenerDescription> mFastenerDescriptionList;
    private final LayoutInflater mLayoutInflater;
    private ViewHolder.OnFastenerListener onFastenerListener;

    FastenersListRecyclerAdapter(Context context, List<FastenerDescription> FastenerDescriptionList, ViewHolder.OnFastenerListener onFastenerListener) {
        this.onFastenerListener = onFastenerListener;
        mFastenerDescriptionList = FastenerDescriptionList;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.fastener_desc_item, parent, false);
        float height = parent.getHeight();
        //itemView.getLayoutParams().height = Math.round(height/3);
        return new ViewHolder(itemView, onFastenerListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FastenerDescription FastenerDescription = mFastenerDescriptionList.get(position);
        holder.mFastenerDescription.setText(String.format("%s - %s", FastenerDescription.getFastenerName(), FastenerDescription.getStandard_detailed()));
        holder.mFastenerImage.setImageBitmap(FastenerDescription.getImage());
        holder.mIsFavoriteValue = FastenerDescription.getIsFavorite();
        int isFavorite = FastenerDescription.getIsFavorite();
        if(isFavorite == 1)
        {
            holder.mIsFavorite.setImageResource(R.drawable.star_on);
            holder.valueToSetOnFavorite = 0;
        }
        else
        {
            holder.valueToSetOnFavorite = 1;
            holder.mIsFavorite.setImageResource(R.drawable.star_off);
        }
        holder.fastenerID = Integer.parseInt(FastenerDescription.getId());
        holder.positionInList = position;
        holder.fastenerTypeId = Integer.parseInt(FastenerDescription.getFastenerTypeId());

        if(FastenerDescription.getIsAvailable() == 0){
            holder.mFastenerDescription.setAlpha(0.2f);
            holder.mFastenerImage.setAlpha(0.5f);
            holder.mIsFavorite.setAlpha(0.2f);
        }else{
            holder.mFastenerDescription.setAlpha(1f);
            holder.mFastenerImage.setAlpha(1f);
            holder.mIsFavorite.setAlpha(1f);
        }
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        super.onViewRecycled(holder);
    }

    @Override
    public int getItemCount() {
        return mFastenerDescriptionList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView mFastenerDescription;
        final ImageView mFastenerImage;
        final ImageView mIsFavorite;
        int mIsFavoriteValue;
        int fastenerID;
        int positionInList;
        int fastenerTypeId;
        int valueToSetOnFavorite;
        OnFastenerListener onFastenerListener;

        ViewHolder(@NonNull final View itemView, OnFastenerListener onFastenerListener) {
            super(itemView);
            mFastenerDescription = itemView.findViewById(R.id.fastener_name_rel);
            mFastenerImage = itemView.findViewById(R.id.fastener_image_frm);

            mIsFavorite = itemView.findViewById(R.id.favorite_img);
            this.onFastenerListener = onFastenerListener;

            itemView.setOnClickListener(this);

            mIsFavorite.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            onFastenerListener.onFastenerClick(getAdapterPosition(), v, fastenerID, fastenerTypeId, valueToSetOnFavorite);
        }

        public interface OnFastenerListener{
            void onFastenerClick(int position, View clickedView, int fastener_id, int fastener_type_id, int value_to_set_on_favorite);
        }
    }
}
