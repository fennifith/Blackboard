package james.blackboard.data;

public class WebLinkData extends ContentData {

    public String url;

    public WebLinkData(String title, String description, String url) {
        super(title, description);
        this.url = url;
    }
}
