package ps.room.BoltHeadClearanceFree;

import android.content.Context;

import androidx.recyclerview.widget.GridLayoutManager;

public class CustomGridLayoutManager extends GridLayoutManager {
    private boolean isScrollEnabled = true;
    CustomGridLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
    }
    void setScrollEnabled() {
        this.isScrollEnabled = false;
    }

    @Override
    public boolean canScrollVertically() {
        return isScrollEnabled && super.canScrollVertically();
    }
}
