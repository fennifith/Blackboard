package james.blackboard.adapters;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import james.blackboard.fragments.BaseFragment;

public class SimplePagerAdapter extends FragmentStatePagerAdapter {

    private BaseFragment[] fragments;

    public SimplePagerAdapter(FragmentManager fm, BaseFragment... fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public BaseFragment getItem(int position) {
        return fragments[position];
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return fragments[position].getTitle();
    }

    @Override
    public int getCount() {
        return fragments.length;
    }
}
