package james.blackboard.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;

import com.afollestad.aesthetic.AestheticActivity;

import james.blackboard.R;
import james.blackboard.fragments.BaseFragment;
import james.blackboard.fragments.LoginFragment;

public class MainActivity extends AestheticActivity implements FragmentManager.OnBackStackChangedListener {

    private BaseFragment fragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            fragment = new LoginFragment();

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment, fragment)
                    .commit();
        } else {
            if (fragment == null)
                fragment = new LoginFragment();

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment, fragment)
                    .commit();
        }

        getSupportFragmentManager().addOnBackStackChangedListener(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(new Bundle());
    }

    @Override
    public void onBackStackChanged() {
        fragment = (BaseFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
    }
}
