package well.com.it.cv.model;

public class Content {
    private String id;
    private String content;
    private String section;

    public Content(String id, String content, String section) {
        this.id = id;
        this.content = content;
        this.section = section;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }
} 