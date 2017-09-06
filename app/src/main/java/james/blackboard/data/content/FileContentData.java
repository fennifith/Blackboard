package james.blackboard.data.content;

public class FileContentData extends ContentData {

    public String url;

    public FileContentData(String title, String description, String url) {
        super(title, description);
        this.url = url;
    }
}
