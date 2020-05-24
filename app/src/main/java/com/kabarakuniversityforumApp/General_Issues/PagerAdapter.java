package com.kabarakuniversityforumApp.General_Issues;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;



public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                General general= new General();
                return general;
            case 1:
                Events events = new Events();
                return events;
            case 3:
                KabaUniversity kabaUniversity = new KabaUniversity();
                return kabaUniversity;
            case 2:
                Emergencies emergencies = new Emergencies();
                return emergencies;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}

