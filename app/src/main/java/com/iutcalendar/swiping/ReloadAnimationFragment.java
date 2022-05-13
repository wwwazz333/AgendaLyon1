package com.iutcalendar.swiping;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import androidx.fragment.app.Fragment;
import com.calendar.iutcalendar.R;


public class ReloadAnimationFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reload_animation, container, false);
        // Inflate the layout for this fragment
        RotateAnimation rotate = new RotateAnimation(0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        rotate.setDuration(1500);
        rotate.setRepeatCount(Animation.INFINITE);

        view.findViewById(R.id.realoadIco).setAnimation(rotate);
        return view;
    }
}