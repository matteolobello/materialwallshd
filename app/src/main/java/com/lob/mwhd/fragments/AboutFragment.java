package com.lob.mwhd.fragments;

import android.animation.Animator;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.lob.mwhd.R;
import com.lob.mwhd.URLs;
import com.lob.mwhd.helpers.GetWhichFragment;
import com.lob.mwhd.helpers.Utils;

public class AboutFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.about_fragment, container, false);
        final TextView title = (TextView) rootView.findViewById(R.id.title_about);
        final TextView subTitle = (TextView) rootView.findViewById(R.id.subtitle_about);
        final FloatingActionButton floatingActionButton = (FloatingActionButton) rootView.findViewById(R.id.fab_website);
        final Button googlePlusCommunityButton = (Button) rootView.findViewById(R.id.google_plus);

        final Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Thin.ttf");

        GetWhichFragment.fragment = null;

        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
        
        floatingActionButton.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.zoom_in));
        floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
        floatingActionButton.setRippleColor(Color.GRAY);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                revealWebsite(floatingActionButton, rootView);
            }
        });

        googlePlusCommunityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(URLs.GPLUS_COMMUNITY)));
            }
        });

        subTitle.setTypeface(typeface);
        title.setTypeface(typeface);
        googlePlusCommunityButton.setTypeface(typeface);
        return rootView;
    }

    private void revealWebsite(FloatingActionButton floatingActionButton, final View rootView) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            floatingActionButton.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.zoom_out));

            final View viewRoot = rootView.findViewById(R.id.layout_invisible);

            int cx = (floatingActionButton.getLeft() + floatingActionButton.getRight()) / 2;
            int cy = (floatingActionButton.getTop() + floatingActionButton.getBottom()) / 2;
            int finalRadius = Math.max(viewRoot.getWidth(), viewRoot.getHeight());

            Animator anim = ViewAnimationUtils.createCircularReveal(viewRoot, cx, cy, 0, finalRadius);
            viewRoot.setVisibility(View.VISIBLE);
            anim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getActivity().finish();
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(URLs.WEBSITE)));
                        }
                    }, 500);
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                }

                @Override
                public void onAnimationRepeat(Animator animator) {
                }
            });
            anim.setDuration(350);
            anim.start();
        } else {
            Intent startWebiste = new Intent(Intent.ACTION_VIEW, Uri.parse(URLs.WEBSITE));
            startActivity(startWebiste);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Utils.setFragment(getActivity().getSupportFragmentManager(), new AboutFragment());
    }
}