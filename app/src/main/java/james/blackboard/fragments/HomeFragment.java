package james.blackboard.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.JsonReader;
import android.util.JsonToken;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import james.blackboard.R;

public class HomeFragment extends BaseFragment implements Toolbar.OnMenuItemClickListener {

    private DrawerLayout drawerLayout;
    private LinearLayout drawer;
    private TextView username;

    private Handler handler;
    private Runnable courseRunnable;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        drawerLayout = view.findViewById(R.id.drawerLayout);
        drawer = view.findViewById(R.id.drawer);
        username = view.findViewById(R.id.username);

        toolbar.setNavigationIcon(R.drawable.ic_drawer);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        handler = new Handler();
        courseRunnable = new Runnable() {

            @Override
            public void run() {
                getBlackboard().getCourses(new ValueCallback<String>() {

                    private List<String> courses;

                    @Override
                    public void onReceiveValue(String s) {
                        courses = new ArrayList<>();

                        JsonReader reader = new JsonReader(new StringReader(s));
                        reader.setLenient(true);

                        try {
                            if(reader.peek() != JsonToken.NULL) {
                                if (reader.peek() == JsonToken.STRING) {
                                    s = reader.nextString();
                                    Document document = Jsoup.parseBodyFragment(s);
                                    getChildren(document.getAllElements());
                                    if (courses.size() > 0)
                                        return;
                                }
                            }
                        } catch (Exception ignored) {
                        }

                        try {
                            reader.close();
                        } catch (IOException ignored) {
                        }

                        getBlackboard().callFunction("global-nav-link", "click", new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String s) {
                            }
                        });

                        handler.postDelayed(courseRunnable, 1000);
                    }

                    public void getChildren(Elements children) {
                        for (int i = 0; i < children.size(); i++) {
                            Element child = children.get(i);
                            if (child.tagName().equals("div") || child.tagName().equals("li") || child.tagName().equals("ul"))
                                getChildren(child.children());
                            else if (child.tagName().equals("span") || child.tagName().equals("a"))
                                addView(child);
                        }
                    }

                    public void addView(Element element) {
                        if (courses.contains(element.text()))
                            return;

                        View view = LayoutInflater.from(getContext()).inflate(R.layout.item_drawer, drawer, false);
                        ImageView icon = view.findViewById(R.id.icon);
                        TextView title = view.findViewById(R.id.title);

                        if (element.tagName().equals("span")) {
                            icon.setVisibility(View.GONE);
                            title.setText(element.text());
                        } else if (element.tagName().equals("a")) {
                            icon.setImageResource(R.drawable.ic_class);
                            title.setText(element.text());
                        }

                        drawer.addView(view);

                        courses.add(element.text());
                    }
                });
            }
        };

        handler.postDelayed(courseRunnable, 1000);

        return view;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                getBlackboard().callFunction("topframe.logout.label", "click", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String s) {
                    }
                });
                break;
        }
        return false;
    }

    @Override
    public void onPageFinished(String url) {
        getBlackboard().getAttribute("global-nav-link", "text", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String s) {
                username.setText(s);
            }
        });

        if (LoginFragment.isLoginUrl(url)) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.fragment, new LoginFragment())
                    .commit();
        }
    }
}
