package james.blackboard.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class ContentFragment extends BaseFragment {

    public static final String EXTRA_TITLE = "title";
    public static final String EXTRA_URL = "url";

    private String title;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        title = getArguments().getString(EXTRA_TITLE);
        return new FrameLayout(getContext());
    }

    @Override
    public String getTitle() {
        return title != null ? title : "";
    }
}
