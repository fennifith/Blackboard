package james.blackboard.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import james.blackboard.R;
import james.blackboard.adapters.SimplePagerAdapter;
import james.blackboard.utils.scrapers.BaseScraper;
import james.blackboard.utils.scrapers.CourseMenuScraper;

public class CourseFragment extends BaseFragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course, container, false);
        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager);

        viewPager.setOffscreenPageLimit(0);

        new CourseMenuScraper(getBlackboard())
                .addCallback(new BaseScraper.ScrapeCallback() {

                    private List<String> strings;
                    private List<BaseFragment> fragments;

                    @Override
                    public void onComplete(BaseScraper scraper, String s) {
                        strings = new ArrayList<>();
                        fragments = new ArrayList<>();
                        Document document = Jsoup.parseBodyFragment(s);
                        getChildren(document.getAllElements());
                        if (fragments.size() > 0) {
                            viewPager.setAdapter(new SimplePagerAdapter(getChildFragmentManager(), fragments.toArray(new BaseFragment[fragments.size()])));
                            tabLayout.setupWithViewPager(viewPager);
                        }
                    }

                    @Override
                    public void onError(BaseScraper scraper, boolean fatal) {
                    }

                    public void getChildren(Elements children) {
                        for (int i = 0; i < children.size(); i++) {
                            Element child = children.get(i);
                            if (child.tagName().equals("div") || child.tagName().equals("li") || child.tagName().equals("ul"))
                                getChildren(child.children());
                            else if (child.tagName().equals("a"))
                                addFragment(child);
                        }
                    }

                    public void addFragment(Element element) {
                        if (strings.contains(element.text()))
                            return;
                        
                        String text = element.text();
                        if (element.children().size() > 0)
                            text = element.child(0).text();

                        Bundle args = new Bundle();
                        args.putString(ContentFragment.EXTRA_TITLE, text);

                        ContentFragment fragment = new ContentFragment();
                        fragment.setArguments(args);

                        fragments.add(fragment);

                        strings.add(element.text());
                    }
                })
                .scrape();

        return view;
    }
}
