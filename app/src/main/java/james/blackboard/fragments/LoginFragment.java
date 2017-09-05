package james.blackboard.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import james.blackboard.R;

public class LoginFragment extends BaseFragment implements View.OnClickListener {

    private LinearLayout url;
    private EditText school;
    private LinearLayout login;
    private TextView changeUrl;
    private EditText username;
    private EditText password;
    private ProgressBar progress;
    private AppCompatButton go;

    private boolean hasUrl;
    private boolean hasLogin;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        url = view.findViewById(R.id.url);
        school = view.findViewById(R.id.school);
        login = view.findViewById(R.id.login);
        changeUrl = view.findViewById(R.id.changeUrl);
        username = view.findViewById(R.id.username);
        password = view.findViewById(R.id.password);
        progress = view.findViewById(R.id.progress);
        go = view.findViewById(R.id.go);

        changeUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hasUrl = false;
                setLoading(false);
            }
        });

        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                getBlackboard().setAttribute("user_id", "value", "'" + username.getText().toString() + "'");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                getBlackboard().setAttribute("password", "value", "'" + password.getText().toString() + "'");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        go.setOnClickListener(this);

        String urlString = getBlackboard().getUrl();
        if (urlString != null) {
            getBlackboard().loadUrl(urlString);
            changeUrl.setText(getBlackboard().getFullUrl());
            hasUrl = true;
            setLoading(true);
        }

        return view;
    }

    private void setLoading(boolean isLoading) {
        progress.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        go.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        url.setVisibility(isLoading || hasUrl ? View.GONE : View.VISIBLE);
        login.setVisibility(isLoading || !hasUrl ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onClick(View view) {
        if (!hasUrl) {
            getBlackboard().loadUrl(school.getText().toString());
            changeUrl.setText(getBlackboard().getFullUrl());
            hasUrl = true;
        } else if (login.getVisibility() == View.VISIBLE) {
            getBlackboard().callFunctionByName("login", 0, "submit", new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String s) {
                }
            });

            hasLogin = true;
        }

        setLoading(true);
    }

    @Override
    public void onPageFinished(String url) {
        setLoading(false);

        if (isLoginUrl(url)) {
            if (hasLogin)
                Toast.makeText(getContext(), "Login failed!", Toast.LENGTH_SHORT).show();
        } else if (url.contains("/webapps/portal")) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.fragment, new HomeFragment())
                    .commit();
        }
    }

    public static boolean isLoginUrl(String url) {
        return url.contains("/webapps/login") || url.endsWith(".blackboard.com") || url.endsWith(".blackboard.com/");
    }
}
