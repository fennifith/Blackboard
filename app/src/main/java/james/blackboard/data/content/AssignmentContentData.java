package james.blackboard.data.content;

public class AssignmentContentData extends ContentData {

    public String url;

    public AssignmentContentData(String title, String description, String url) {
        super(title, description);
        this.url = url;
    }

}
