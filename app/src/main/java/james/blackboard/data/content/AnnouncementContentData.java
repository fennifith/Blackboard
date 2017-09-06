package james.blackboard.data.content;

public class AnnouncementContentData extends ContentData {

    public String date;

    public AnnouncementContentData(String title, String description, String date) {
        super(title, description);
        this.date = date;
    }
}
