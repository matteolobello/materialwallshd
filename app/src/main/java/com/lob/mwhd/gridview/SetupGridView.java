package com.lob.mwhd.gridview;

/**
 * *******************************************************************************
 * Init the GridView, using a custom GridView Adapter and a custom ScrollListener
 * *******************************************************************************
 */

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.GridView;

import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;

public class SetupGridView {

    private static void setGridViewItemsAnimation(GridView gridView, CustomGridViewAdapter customGridViewAdapter) {
        SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(customGridViewAdapter);
        swingBottomInAnimationAdapter.setAbsListView(gridView);
        gridView.setAdapter(swingBottomInAnimationAdapter);
    }

    public static void init(Activity activity, Context context, View rootView, GridView gridView, int numColumns, String path) {
        CustomGridViewAdapter customGridViewAdapter = new CustomGridViewAdapter(activity, rootView, path);
        gridView.setAdapter(customGridViewAdapter);
        gridView.setOnScrollListener(new CustomGridViewScrollListener(context));
        gridView.setNumColumns(numColumns);
        gridView.setHorizontalSpacing(8);
        gridView.setVerticalSpacing(8);
        setGridViewItemsAnimation(gridView, customGridViewAdapter);
    }
}
