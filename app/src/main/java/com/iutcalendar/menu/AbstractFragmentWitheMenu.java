package com.iutcalendar.menu;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

public abstract class AbstractFragmentWitheMenu extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
}
