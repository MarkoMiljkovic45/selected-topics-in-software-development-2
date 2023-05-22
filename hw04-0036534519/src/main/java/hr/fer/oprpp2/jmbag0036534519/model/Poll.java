package hr.fer.oprpp2.jmbag0036534519.model;

public class Poll {
    private long id;
    private String title;
    private String message;

    public Poll() {
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Poll(" + id + ", " + title + ", " + message + ")";
    }
}
