package james.blackboard.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import james.blackboard.Blackboard;

public abstract class BaseFragment extends Fragment implements Blackboard.BlackboardListener {

    private Blackboard blackboard;
    private boolean isSelected;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        blackboard = (Blackboard) getContext().getApplicationContext();
        blackboard.addListener(this);
    }

    @Override
    public void onDestroy() {
        blackboard.removeListener(this);
        super.onDestroy();
    }

    Blackboard getBlackboard() {
        return blackboard;
    }

    @Override
    public void onPageFinished(String url) {

    }

    @Override
    public void onRequest(String url) {

    }

    public String getTitle() {
        return "";
    }

    void onSelect() {

    }

    void onDeselect() {

    }

    public final void setSelected(boolean isSelected) {
        if (this.isSelected != isSelected) {
            this.isSelected = isSelected;
            if (isSelected)
                onSelect();
            else onDeselect();
        }
    }

    public final boolean isSelected() {
        return isSelected;
    }
}
