package james.blackboard.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
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

import java.util.ArrayList;
import java.util.List;

import james.blackboard.R;
import james.blackboard.utils.scrapers.BaseScraper;
import james.blackboard.utils.scrapers.CourseScraper;
import james.blackboard.utils.scrapers.UsernameScraper;

public class HomeFragment extends BaseFragment {

    private DrawerLayout drawerLayout;
    private LinearLayout coursesLayout;
    private TextView username;
    private ImageView logout;
    private TextView url;

    private BaseScraper courseScraper;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        drawerLayout = view.findViewById(R.id.drawerLayout);
        coursesLayout = view.findViewById(R.id.courses);
        username = view.findViewById(R.id.username);
        url = view.findViewById(R.id.url);
        logout = view.findViewById(R.id.logout);

        toolbar.setNavigationIcon(R.drawable.ic_drawer);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        url.setText(getBlackboard().getFullUrl());

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getBlackboard().callFunction("topframe.logout.label", "click", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String s) {
                    }
                });
            }
        });

        new UsernameScraper(getBlackboard())
                .addCallback(new BaseScraper.ScrapeCallback() {
                    @Override
                    public void onComplete(BaseScraper scraper, String s) {
                        username.setText(s);
                    }

                    @Override
                    public void onError(BaseScraper scraper, boolean fatal) {

                    }
                })
                .scrape();

        courseScraper = new CourseScraper(getBlackboard())
                .addCallback(new BaseScraper.ScrapeCallback() {

                    private List<String> courses;

                    @Override
                    public void onComplete(BaseScraper scraper, String s) {
                        courses = new ArrayList<>();
                        Document document = Jsoup.parseBodyFragment(s);
                        coursesLayout.removeAllViews();
                        getChildren(document.getAllElements());
                    }

                    @Override
                    public void onError(BaseScraper scraper, boolean fatal) {
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

                        View view = LayoutInflater.from(getContext()).inflate(R.layout.item_drawer, coursesLayout, false);
                        ImageView icon = view.findViewById(R.id.icon);
                        TextView title = view.findViewById(R.id.title);

                        if (element.tagName().equals("span")) {
                            icon.setVisibility(View.GONE);
                            title.setText(element.text());
                        } else if (element.tagName().equals("a")) {
                            icon.setImageResource(R.drawable.ic_class);
                            title.setText(element.text());
                        }

                        coursesLayout.addView(view);

                        courses.add(element.text());
                    }

                });

        courseScraper.scrape();

        return view;
    }

    @Override
    public void onPageFinished(String url) {
        if (LoginFragment.isLoginUrl(url)) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.fragment, new LoginFragment())
                    .commit();
        }
    }
}
