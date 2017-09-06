package james.blackboard.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import james.blackboard.utils.scrapers.BaseScraper;
import james.blackboard.utils.scrapers.ContentScraper;

public class ContentFragment extends BaseFragment {

    private String title;
    private String url;

    private BaseScraper scraper;
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
        if (scraper != null)
            scraper.cancel();
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
    void onDeselect() {
        scraper.cancel();
    }

    @Override
    public void onPageFinished(String url) {
        if (isSelected() && isCreated) {
            scraper = new ContentScraper(getBlackboard())
                    .addCallback(new BaseScraper.ScrapeCallback() {

                        @Override
                        public void onComplete(BaseScraper scraper, String s) {
                            Document document = Jsoup.parseBodyFragment(s);
                            getChildren(document.getAllElements());
                        }

                        @Override
                        public void onError(BaseScraper scraper, boolean fatal) {
                        }

                        public void getChildren(Elements children) {
                            for (int i = 0; i < children.size(); i++) {
                                Element child = children.get(i);
                                if (child.tagName().equals("div") || child.tagName().equals("ul"))
                                    getChildren(child.children());
                                else if (child.tagName().equals("li"))
                                    addItem(child);
                            }
                        }

                        public void addItem(Element element) {
                            if (element.id().startsWith("contentListItem")) {
                                Log.d("HTML", element.html());
                            }
                        }
                    });

            scraper.scrape();
        }
    }
}
