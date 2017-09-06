package james.blackboard.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

public class ContentFragment extends BaseFragment {

    private String title;
    private String url;

    private boolean isCreated;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        isCreated = true;
        if (isSelected())
            getBlackboard().sendAction(url);
        return new FrameLayout(getContext());
    }

    @Override
    public void onDestroyView() {
        isCreated = false;
        super.onDestroyView();
    }

    @Override
    public String getTitle() {
        return title != null ? title : "";
    }

    public void setData(String title, String url) {
        this.title = title;
        this.url = url;
    }

    @Override
    void onSelect() {
        if (isCreated)
            getBlackboard().sendAction(url);
    }

    @Override
    public void onPageFinished(String url) {
        if (isSelected() && isCreated) {
            Toast.makeText(getContext(), title, Toast.LENGTH_SHORT).show();
        }
    }
}
