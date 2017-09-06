package james.blackboard.data.content;

public class WebLinkContentData extends ContentData {

    public String url;

    public WebLinkContentData(String title, String description, String url) {
        super(title, description);
        this.url = url;
    }
}
