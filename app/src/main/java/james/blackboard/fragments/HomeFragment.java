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
import james.blackboard.utils.scrapers.OrganizationScraper;
import james.blackboard.utils.scrapers.UsernameScraper;

public class HomeFragment extends BaseFragment {

    private DrawerLayout drawerLayout;
    private LinearLayout organizationsLayout;
    private LinearLayout coursesLayout;
    private TextView username;
    private ImageView logout;
    private TextView url;
    private View refresh;

    private BaseScraper organizationScraper;
    private BaseScraper courseScraper;
    private BaseFragment fragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        final Toolbar toolbar = view.findViewById(R.id.toolbar);
        drawerLayout = view.findViewById(R.id.drawerLayout);
        organizationsLayout = view.findViewById(R.id.organizations);
        coursesLayout = view.findViewById(R.id.courses);
        username = view.findViewById(R.id.username);
        url = view.findViewById(R.id.url);
        logout = view.findViewById(R.id.logout);
        refresh = view.findViewById(R.id.refresh);

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

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (courseScraper.isCancelled() || courseScraper.isComplete()) {
                    coursesLayout.removeAllViews();
                    courseScraper.scrape();
                }
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

        organizationScraper = new OrganizationScraper(getBlackboard())
                .addCallback(new BaseScraper.ScrapeCallback() {

                    List<String> organizations;

                    @Override
                    public void onComplete(BaseScraper scraper, String s) {
                        organizations = new ArrayList<>();
                        Document document = Jsoup.parseBodyFragment(s);
                        organizationsLayout.removeAllViews();
                        getChildren(document.getAllElements());

                        if (organizationsLayout.getChildCount() > 0)
                            organizationsLayout.getChildAt(0).performClick();
                        else organizationScraper.scrape();
                    }

                    @Override
                    public void onError(BaseScraper scraper, boolean fatal) {

                    }

                    public void getChildren(Elements children) {
                        for (int i = 0; i < children.size(); i++) {
                            Element child = children.get(i);
                            if (child.tagName().equals("a"))
                                addView(child);
                        }
                    }

                    public void addView(Element element) {
                        if (organizations.contains(element.text()))
                            return;

                        View view = LayoutInflater.from(getContext()).inflate(R.layout.item_drawer, organizationsLayout, false);
                        ImageView icon = view.findViewById(R.id.icon);
                        TextView title = view.findViewById(R.id.title);

                        icon.setImageResource(R.drawable.ic_class);
                        title.setText(element.text());
                        String onclick = element.attributes().get("onclick").replace("return false;", "");
                        view.setTag(onclick.substring(0, onclick.length() - 1));
                        view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (view.getTag() != null && view.getTag() instanceof String) {
                                    toolbar.setTitle(((TextView) view.findViewById(R.id.title)).getText().toString());
                                    drawerLayout.closeDrawer(GravityCompat.START);
                                    getBlackboard().callFunction((String) view.getTag(), new ValueCallback<String>() {
                                        @Override
                                        public void onReceiveValue(String s) {
                                            boolean shouldAdd = fragment == null;
                                            fragment = new CourseFragment();
                                            if (shouldAdd) {
                                                getChildFragmentManager().beginTransaction()
                                                        .add(R.id.fragment, fragment)
                                                        .commit();
                                            } else {
                                                getChildFragmentManager().beginTransaction()
                                                        .replace(R.id.fragment, fragment)
                                                        .commit();
                                            }
                                        }
                                    });
                                }
                            }
                        });

                        organizationsLayout.addView(view);
                        organizations.add(element.text());
                    }
                });

        courseScraper = new CourseScraper(getBlackboard())
                .addCallback(new BaseScraper.ScrapeCallback() {

                    private List<String> courses;

                    @Override
                    public void onComplete(BaseScraper scraper, String s) {
                        courses = new ArrayList<>();
                        Document document = Jsoup.parseBodyFragment(s);
                        coursesLayout.removeAllViews();
                        getChildren(document.getAllElements());

                        if (courses.size() > 0)
                            organizationScraper.scrape();
                        else courseScraper.scrape();
                    }

                    @Override
                    public void onError(BaseScraper scraper, boolean fatal) {
                    }

                    public void getChildren(Elements children) {
                        for (int i = 0; i < children.size(); i++) {
                            Element child = children.get(i);
                            if (child.tagName().equals("span") || child.tagName().equals("a"))
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
                            String onclick = element.attributes().get("onclick").replace("return false;", "");
                            view.setTag(onclick.substring(0, onclick.length() - 1));
                            view.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (view.getTag() != null && view.getTag() instanceof String) {
                                        toolbar.setTitle(((TextView) view.findViewById(R.id.title)).getText().toString());
                                        drawerLayout.closeDrawer(GravityCompat.START);
                                        getBlackboard().callFunction((String) view.getTag(), new ValueCallback<String>() {
                                            @Override
                                            public void onReceiveValue(String s) {
                                                boolean shouldAdd = fragment == null;
                                                fragment = new CourseFragment();
                                                if (shouldAdd) {
                                                    getChildFragmentManager().beginTransaction()
                                                            .add(R.id.fragment, fragment)
                                                            .commit();
                                                } else {
                                                    getChildFragmentManager().beginTransaction()
                                                            .replace(R.id.fragment, fragment)
                                                            .commit();
                                                }
                                            }
                                        });
                                    }
                                }
                            });
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
