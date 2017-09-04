package james.blackboard;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.reactivex.annotations.Nullable;

public class Blackboard extends Application {

    private static final String PREF_URL = "url";

    private static final String BASE_URL = "https://%s.blackboard.com";

    private SharedPreferences prefs;

    private WebView webView;

    private List<BlackboardListener> listeners;

    @Override
    public void onCreate() {
        super.onCreate();
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        listeners = new ArrayList<>();
        webView = new WebView(this);
        webView.setWebViewClient(new WebClient(this));
        webView.setWebChromeClient(new ChromeClient());
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadsImagesAutomatically(false);
    }

    @Nullable
    public String getUrl() {
        return prefs.getString(PREF_URL, null);
    }

    public String getFullUrl() {
        return String.format(Locale.getDefault(), BASE_URL, getUrl());
    }

    public void loadUrl(String url) {
        webView.loadUrl(String.format(Locale.getDefault(), BASE_URL, url));
        prefs.edit().putString(PREF_URL, url).apply();
    }

    public void sendAction(String actionUrl) {
        webView.loadUrl(getUrl() + actionUrl);
    }

    public void getHtml(ValueCallback<String> callback) {
        webView.evaluateJavascript("(function(){return document.getElementById('body')[0].innerHTML;})();", callback);
    }

    public void getCourses(ValueCallback<String> callback) {
        webView.evaluateJavascript("(function(){return document.getElementById('CourseNavMenuSection.Course-content').innerHTML;})()", callback);
    }

    public void setAttribute(String id, String attribute, String value) {
        webView.evaluateJavascript("(function(){document.getElementById('" + id + "')." + attribute + " = " + value + ";})();", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String s) {
            }
        });
    }

    public void getAttribute(String id, String attribute, ValueCallback<String> callback) {
        webView.evaluateJavascript("(function(){return document.getElementById('" + id + "')." + attribute + ";})()", callback);
    }

    public void callFunction(String id, String function, ValueCallback<String> callback) {
        webView.evaluateJavascript("(function(){document.getElementById('" + id + "')." + function + "();})();", callback);
    }

    public void callFunctionByName(String name, int index, String function, ValueCallback<String> callback) {
        webView.evaluateJavascript("(function(){document.getElementsByName('" + name + "')[" + index + "]." + function + "();})();", callback);
    }

    public void addListener(BlackboardListener listener) {
        listeners.add(listener);
    }

    public void removeListener(BlackboardListener listener) {
        listeners.remove(listener);
    }

    private void onPageFinished(String url) {
        for (BlackboardListener listener : listeners) {
            listener.onPageFinished(url);
        }
    }

    private void onRequest(String url) {
        for (BlackboardListener listener : listeners) {
            listener.onRequest(url);
        }
    }

    public interface BlackboardListener {
        void onPageFinished(String url);
        void onRequest(String url);
    }

    private static class WebClient extends WebViewClient {

        private Blackboard blackboard;

        public WebClient(Blackboard blackboard) {
            this.blackboard = blackboard;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            blackboard.onPageFinished(url);
            super.onPageFinished(view, url);
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            blackboard.onRequest(url);
            return super.shouldInterceptRequest(view, url);
        }
    }

    private static class ChromeClient extends WebChromeClient {
    }
}
