package client.hawker.com.hawkerclient.Utils;

import android.support.v7.widget.RecyclerView;

public interface RecyclerItemTouchHelperListener {
    void onSwipe(RecyclerView.ViewHolder viewHolder, int direction, int position);
}
