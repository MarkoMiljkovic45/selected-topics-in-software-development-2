package hr.fer.oprpp2.jmbag0036534519.model;

public class PollOption {
    private long id;
    private String optionTitle;
    private String optionLink;
    private long pollID;
    private long votesCount;

    public PollOption() {
    }

    public long getId() {
        return id;
    }

    public String getOptionTitle() {
        return optionTitle;
    }

    public String getOptionLink() {
        return optionLink;
    }

    public long getPollID() {
        return pollID;
    }

    public long getVotesCount() {
        return votesCount;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setOptionTitle(String optionTitle) {
        this.optionTitle = optionTitle;
    }

    public void setOptionLink(String optionLink) {
        this.optionLink = optionLink;
    }

    public void setPollID(long pollID) {
        this.pollID = pollID;
    }

    public void setVotesCount(long votesCount) {
        this.votesCount = votesCount;
    }

    @Override
    public String toString() {
        return "PollOption(" + id + ", " + optionTitle + ", " + optionLink + '\'' + ", " + pollID + ")";
    }
}
