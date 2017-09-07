package james.blackboard.data.content;

public class BlankPageContentData extends ContentData {

    public String url;

    public BlankPageContentData(String title, String description, String url) {
        super(title, description);
        this.url = url;
    }

}
