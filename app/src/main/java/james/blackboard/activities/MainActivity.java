package james.blackboard.activities;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;

import com.afollestad.aesthetic.AestheticActivity;

import james.blackboard.Blackboard;
import james.blackboard.R;
import james.blackboard.fragments.BaseFragment;
import james.blackboard.fragments.LoginFragment;

public class MainActivity extends AestheticActivity implements FragmentManager.OnBackStackChangedListener, Blackboard.ProgressListener {

    private Blackboard blackboard;
    private BaseFragment fragment;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        blackboard = (Blackboard) getApplicationContext();
        blackboard.addListener(this);

        progressBar = findViewById(R.id.progressBar);

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
    protected void onDestroy() {
        blackboard.removeListener(this);
        super.onDestroy();
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

    @Override
    public void onProgressChanged(int progress) {
        if (progressBar != null) {
            progressBar.setVisibility(progress == 100 ? View.GONE : View.VISIBLE);
            if (progress < progressBar.getProgress())
                progressBar.setProgress(progress);
            else {
                ObjectAnimator animator = ObjectAnimator.ofInt(progressBar, "progress", progressBar.getProgress(), progress);
                animator.setInterpolator(new DecelerateInterpolator());
                animator.setDuration(1000);
                animator.start();
            }
        }
    }
}
