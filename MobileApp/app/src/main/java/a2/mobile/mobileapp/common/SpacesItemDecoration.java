package a2.mobile.mobileapp.common;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
    private int space;

    public SpacesItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(
            Rect outRect,
            @NonNull View view,
            @NonNull RecyclerView parent,
            @NonNull RecyclerView.State state) {

        outRect.left = space;
        outRect.right = space;

        int index = parent.getChildLayoutPosition(view);
        if (index == 0) {
            outRect.left = 0;
        }
    }
}