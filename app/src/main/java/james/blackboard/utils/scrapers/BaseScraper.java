package james.blackboard.utils.scrapers;

import java.util.ArrayList;
import java.util.List;

import james.blackboard.Blackboard;

public abstract class BaseScraper {

    private Blackboard blackboard;
    private List<ScrapeCallback> callbacks;

    private boolean isComplete;

    public BaseScraper(Blackboard blackboard) {
        this.blackboard = blackboard;
        callbacks = new ArrayList<>();
    }

    public final BaseScraper addCallback(ScrapeCallback callback) {
        callbacks.add(callback);
        return this;
    }

    public final void removeCallback(ScrapeCallback callback) {
        callbacks.remove(callback);
    }

    public final boolean isComplete() {
        return isComplete;
    }

    public void scrape() {
        isComplete = false;
    }

    final Blackboard getBlackboard() {
        return blackboard;
    }

    final void onComplete(String s) {
        isComplete = true;
        for (ScrapeCallback callback : callbacks) {
            callback.onComplete(this, s);
        }
    }

    final void onError(boolean fatal) {
        for (ScrapeCallback callback : callbacks) {
            callback.onError(this, fatal);
        }
    }

    public interface ScrapeCallback {
        void onComplete(BaseScraper scraper, String s);

        void onError(BaseScraper scraper, boolean fatal);
    }

}
