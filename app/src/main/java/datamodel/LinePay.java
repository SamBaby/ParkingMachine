package datamodel;

public class LinePay {
    private int id;
    private String ChannelId;
    private String ChannelSecret;
    private int test;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getChannelId() {
        return ChannelId;
    }

    public void setChannelId(String channelId) {
        ChannelId = channelId;
    }

    public String getChannelSecret() {
        return ChannelSecret;
    }

    public void setChannelSecret(String channelSecret) {
        ChannelSecret = channelSecret;
    }

    public int getTest() {
        return test;
    }

    public void setTest(int test) {
        this.test = test;
    }
}
