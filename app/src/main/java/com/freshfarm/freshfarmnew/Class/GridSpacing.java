package com.freshfarm.freshfarmnew.Class;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class GridSpacing extends RecyclerView.ItemDecoration {

    private int halfSpace;

    public GridSpacing(int space) {
        this.halfSpace = space / 2;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        if (parent.getPaddingLeft() != halfSpace) {
            parent.setPadding(halfSpace, halfSpace, halfSpace, halfSpace);
            parent.setClipToPadding(false);
        }

        outRect.top = halfSpace;
        outRect.bottom = halfSpace;
        outRect.left = halfSpace;
        outRect.right = halfSpace;
    }
}
