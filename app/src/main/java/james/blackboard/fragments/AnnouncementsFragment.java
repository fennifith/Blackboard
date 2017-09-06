package james.blackboard.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import james.blackboard.adapters.ContentsAdapter;
import james.blackboard.data.content.AnnouncementContentData;
import james.blackboard.data.content.ContentData;
import james.blackboard.data.content.WebLinkContentData;
import james.blackboard.utils.HtmlUtils;
import james.blackboard.utils.scrapers.AnnouncementsScraper;
import james.blackboard.utils.scrapers.BaseScraper;

public class AnnouncementsFragment extends BaseFragment {

    private String url;

    private RecyclerView recycler;

    private BaseScraper scraper;
    private boolean isCreated;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_content, container, false);
        recycler = view.findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        if (isSelected())
            getBlackboard().sendAction(url);
        isCreated = true;
        return view;
    }

    @Override
    public void onDestroyView() {
        isCreated = false;
        if (scraper != null)
            scraper.cancel();
        super.onDestroyView();
    }

    @Override
    public String getTitle() {
        return "Announcements";
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    void onSelect() {
        if (isCreated)
            getBlackboard().sendAction(url);
    }

    @Override
    void onDeselect() {
        if (scraper != null)
            scraper.cancel();
    }

    @Override
    public void onPageFinished(String url) {
        if (isSelected() && isCreated && scraper == null || scraper.isCancelled() || scraper.isComplete()) {
            scraper = new AnnouncementsScraper(getBlackboard())
                    .addCallback(new BaseScraper.ScrapeCallback() {

                        private List<ContentData> contents;

                        @Override
                        public void onComplete(BaseScraper scraper, String s) {
                            if (isSelected() && isCreated && recycler != null) {
                                contents = new ArrayList<>();
                                Document document = Jsoup.parseBodyFragment(s);
                                HtmlUtils.removeUselessAttributes(document);
                                getChildren(document.getAllElements());
                                if (contents.size() > 0)
                                    recycler.setAdapter(new ContentsAdapter(contents));
                            }
                        }

                        @Override
                        public void onError(BaseScraper scraper, boolean fatal) {
                        }

                        public void getChildren(Elements children) {
                            for (int i = 0; i < children.size(); i++) {
                                if (children.get(i).tagName().equals("li")) {
                                    try {
                                        addItem(children.get(i));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        public void addItem(Element element) {
                            String title = element.getElementsByTag("h3").get(0).text();
                            String description = HtmlUtils.getBasicHtml(element.getElementsByClass("vtbegenerated").get(0));
                            String date = element.getElementsByClass("details").get(0).child(0).text();
                            String url = null;
                            if (element.getElementsByTag("iframe").size() > 0) {
                                Element iframe = element.getElementsByTag("iframe").get(0);
                                if (iframe.hasAttr("src"))
                                    url = iframe.attr("src");
                            }

                            ContentData content;
                            if (url != null)
                                content = new WebLinkContentData(title, date, url);
                            else content = new AnnouncementContentData(title, description, date);

                            contents.add(content);
                        }
                    });

            scraper.scrape();
        }
    }
}
