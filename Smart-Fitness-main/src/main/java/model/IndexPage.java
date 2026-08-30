package model;

public class IndexPage {

    private String welcomeMessage;
    public IndexPage() {
    }
    public IndexPage(String welcomeMessage) {
        this.welcomeMessage = welcomeMessage;
    }
    public String getWelcomeMessage() {
        return welcomeMessage;
    }
    public void setWelcomeMessage(String welcomeMessage) {
        this.welcomeMessage = welcomeMessage;
    }
}
