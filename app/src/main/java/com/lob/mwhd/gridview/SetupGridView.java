package com.lob.mwhd.gridview;

/**
 * *******************************************************************************
 * Init the GridView, using a custom GridView Adapter and a custom ScrollListener
 * *******************************************************************************
 */

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.GridView;

import com.lob.mwhd.R;
import com.lob.mwhd.helpers.Utils;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;

public class SetupGridView {

    private static void setGridViewItemsAnimation(GridView gridView, CustomGridViewAdapter customGridViewAdapter) {
        SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(customGridViewAdapter);
        swingBottomInAnimationAdapter.setAbsListView(gridView);
        gridView.setAdapter(swingBottomInAnimationAdapter);
    }

    public static void init(final Activity activity, final Fragment fragment, Context context, View rootView, GridView gridView, int numColumns, String path) {
        CustomGridViewAdapter customGridViewAdapter = new CustomGridViewAdapter(activity, rootView, path);
        gridView.setAdapter(customGridViewAdapter);
        gridView.setOnScrollListener(new CustomGridViewScrollListener(context));
        gridView.setNumColumns(numColumns);
        gridView.setHorizontalSpacing(8);
        gridView.setVerticalSpacing(8);

        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout)rootView.findViewById(R.id.swipe_container);
        swipeRefreshLayout.setColorSchemeColors(activity.getResources().getColor(R.color.ColorPrimary));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Utils.setFragment(com.lob.mwhd.helpers.FragmentManager.fragmentManager, fragment);
            }
        });
        setGridViewItemsAnimation(gridView, customGridViewAdapter);
    }
}
