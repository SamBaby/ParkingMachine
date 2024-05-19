package com.example.machine;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import pages.CarViewFragment;
import pages.CarrierFragment;
import pages.CompanyIDFragment;
import pages.EndingFragment;
import pages.PayFragment;
import pages.PaymentChooseFragment;
import pages.SearchFragment;

public class MachinePagerAdapter extends FragmentPagerAdapter {
    private static final int NUM_PAGES = 7;
    public MachinePagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new SearchFragment();
            case 1:
                return new CarViewFragment();
            case 2:
                return new PaymentChooseFragment();
            case 3:
                return new PayFragment();
            case 4:
                return new CompanyIDFragment();
            case 5:
                return new CarrierFragment();
            case 6:
                return new EndingFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }
}
