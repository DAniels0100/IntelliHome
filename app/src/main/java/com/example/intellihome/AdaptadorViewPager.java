package com.example.intellihome;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class AdaptadorViewPager extends FragmentStateAdapter {
    private final int NUM_TABS = 2; // Número de pestañas

    public AdaptadorViewPager(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new PestanaUno(); // Pestaña 1
            case 1:
                return new PestanaDos(); // Pestaña 2
            default:
                return new PestanaUno(); // Fallback
        }
    }

    @Override
    public int getItemCount() {
        return NUM_TABS; // Devolver el número de pestañas
    }
}