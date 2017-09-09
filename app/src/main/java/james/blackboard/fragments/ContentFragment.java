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
import james.blackboard.adapters.BreadcrumbsAdapter;
import james.blackboard.adapters.ContentsAdapter;
import james.blackboard.data.BreadcrumbData;
import james.blackboard.data.content.AssignmentContentData;
import james.blackboard.data.content.BlankPageContentData;
import james.blackboard.data.content.ContentData;
import james.blackboard.data.content.FileContentData;
import james.blackboard.data.content.FolderContentData;
import james.blackboard.data.content.WebLinkContentData;
import james.blackboard.utils.HtmlUtils;
import james.blackboard.utils.scrapers.BaseScraper;
import james.blackboard.utils.scrapers.ContentScraper;

public class ContentFragment extends BaseFragment implements BreadcrumbsAdapter.BreadcrumbCallback {

    public List<BreadcrumbData> breadcrumbs = new ArrayList<>();

    private RecyclerView recycler;
    private RecyclerView breadcrumbsView;
    private BreadcrumbsAdapter breadcrumbsAdapter;
    private View empty;

    private BaseScraper scraper;
    private boolean isCreated;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_content, container, false);
        recycler = view.findViewById(R.id.recycler);
        breadcrumbsView = view.findViewById(R.id.breadcrumbs);
        empty = view.findViewById(R.id.empty);

        recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        breadcrumbsAdapter = new BreadcrumbsAdapter(breadcrumbs, this);
        breadcrumbsView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        breadcrumbsView.setAdapter(breadcrumbsAdapter);

        if (isSelected())
            getBlackboard().sendAction(breadcrumbs.get(breadcrumbs.size() - 1).tag);
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
        return breadcrumbs.size() > 0 ? breadcrumbs.get(0).title : "";
    }

    public void setData(String title, String url) {
        if (breadcrumbs.size() == 0)
            breadcrumbs.add(new BreadcrumbData(title, url));
    }

    @Override
    void onSelect() {
        if (isCreated)
            getBlackboard().sendAction(breadcrumbs.get(breadcrumbs.size() - 1).tag);
    }

    @Override
    void onDeselect() {
        if (scraper != null)
            scraper.cancel();
    }

    @Override
    public void onPageFinished(String url) {
        if (isSelected() && isCreated && (scraper == null || scraper.isCancelled() || scraper.isComplete())) {
            scraper = new ContentScraper(getBlackboard())
                    .addCallback(new BaseScraper.ScrapeCallback() {

                        private List<ContentData> contents;

                        @Override
                        public void onComplete(BaseScraper scraper, String s) {
                            if (isSelected() && isCreated && recycler != null) {
                                contents = new ArrayList<>();
                                Document document = Jsoup.parseBodyFragment(s);
                                HtmlUtils.removeUselessAttributes(document);
                                getChildren(document.getAllElements());
                                empty.setVisibility(contents.size() > 0 ? View.GONE : View.VISIBLE);
                                if (contents.size() > 0) {
                                    ContentsAdapter adapter = new ContentsAdapter(contents);
                                    adapter.setBreadcrumbCallback(ContentFragment.this);
                                    recycler.setAdapter(adapter);
                                }
                            }
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
                                String title = element.getElementsByTag("h3").get(0).text();
                                String description = HtmlUtils.getBasicHtml(element.getElementsByClass("details").get(0));
                                String type = element.getElementsByTag("img").get(0).attr("alt");

                                ContentData content;
                                switch (type) {
                                    case "Web Link":
                                        content = new WebLinkContentData(title, description, element.getElementsByTag("a").get(0).attr("href"));
                                        break;
                                    case "Content Folder":
                                        content = new FolderContentData(title, description, element.getElementsByTag("a").get(0).attr("href"));
                                        break;
                                    case "Assignment":
                                        content = new AssignmentContentData(title, description, getBlackboard().getFullUrl() + element.getElementsByTag("a").get(0).attr("href"));
                                        break;
                                    case "Blank Page":
                                        content = new BlankPageContentData(title, description, getBlackboard().getFullUrl() + element.getElementsByTag("a").get(0).attr("href"));
                                        break;
                                    case "File":
                                        content = new FileContentData(title, description, getBlackboard().getFullUrl() + element.getElementsByTag("a").get(0).attr("href"));
                                        break;
                                    default:
                                        content = new ContentData(title, description);
                                        break;
                                }

                                contents.add(content);
                            }
                        }
                    });

            scraper.scrape();
        }
    }

    @Override
    public void moveTo(BreadcrumbData breadcrumb) {
        if (isCreated && isSelected() && (scraper == null || scraper.isCancelled() || scraper.isComplete())) {
            getBlackboard().sendAction(breadcrumb.tag);

            if (breadcrumbs.contains(breadcrumb)) {
                int index = breadcrumbs.indexOf(breadcrumb);
                int size = breadcrumbs.size();
                breadcrumbs.subList(index + 1, size).clear();
                breadcrumbsAdapter.notifyItemRangeRemoved(index, size - index);
                breadcrumbsAdapter.notifyItemChanged(index);
            } else {
                breadcrumbs.add(breadcrumb);
                breadcrumbsAdapter.notifyItemInserted(breadcrumbs.size() - 1);
                breadcrumbsAdapter.notifyItemChanged(breadcrumbs.size() - 2);
                breadcrumbsView.smoothScrollToPosition(breadcrumbs.size() - 1);
            }

            breadcrumbsView.setVisibility(breadcrumbs.size() > 1 ? View.VISIBLE : View.GONE);
        }
    }
}
