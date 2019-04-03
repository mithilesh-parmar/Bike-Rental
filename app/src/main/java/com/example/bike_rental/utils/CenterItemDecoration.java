package com.example.bike_rental.utils;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class CenterItemDecoration extends RecyclerView.ItemDecoration {

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int totalWidth = parent.getWidth();

        //first element
        if (parent.getChildAdapterPosition(view) == 0) {
            int firstPadding = (totalWidth - (view.getWidth() + view.getPaddingLeft()) ) / 2;
            firstPadding = Math.max(0, firstPadding);
            outRect.set(firstPadding, 0, 0, 0);
        }

        //last element
        if (parent.getChildAdapterPosition(view) == parent.getAdapter().getItemCount() - 1 && parent.getAdapter().getItemCount() > 1) {
            int lastPadding = (totalWidth - (view.getWidth() + view.getPaddingRight()) ) / 2;
            lastPadding = Math.max(0, lastPadding);
            outRect.set(0, 0, lastPadding, 0);
        }
    }
}
