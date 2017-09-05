package james.blackboard.utils.scrapers;

import android.os.Handler;
import android.util.JsonReader;
import android.util.JsonToken;
import android.webkit.ValueCallback;

import java.io.IOException;
import java.io.StringReader;

import james.blackboard.Blackboard;

public class UsernameScraper extends BaseScraper {

    private Handler handler;
    private Runnable runnable;

    public UsernameScraper(Blackboard blackboard) {
        super(blackboard);
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                getBlackboard().getAttribute("global-nav-link", "innerText", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String s) {
                        JsonReader reader = new JsonReader(new StringReader(s));
                        reader.setLenient(true);

                        try {
                            if (reader.peek() != JsonToken.NULL) {
                                if (reader.peek() == JsonToken.STRING)
                                    onComplete(reader.nextString().replace("[0-9]", ""));
                            }
                        } catch (Exception ignored) {
                        }

                        try {
                            reader.close();
                        } catch (IOException ignored) {
                        }

                        if (!isComplete()) {
                            onError(false);
                            handler.postDelayed(runnable, 1000);
                        }
                    }
                });
            }
        };
    }

    @Override
    public void scrape() {
        super.scrape();
        handler.postDelayed(runnable, 500);
    }
}
