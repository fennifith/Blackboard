package james.blackboard.adapters;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import james.blackboard.fragments.BaseFragment;

public class SimplePagerAdapter extends FragmentStatePagerAdapter implements ViewPager.OnPageChangeListener {

    private BaseFragment[] fragments;

    public SimplePagerAdapter(ViewPager viewPager, FragmentManager fm, BaseFragment... fragments) {
        super(fm);
        this.fragments = fragments;
        viewPager.addOnPageChangeListener(this);
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

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        for (int i = 0; i < fragments.length; i++) {
            fragments[i].setSelected(i == position);
        }
    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }
}
