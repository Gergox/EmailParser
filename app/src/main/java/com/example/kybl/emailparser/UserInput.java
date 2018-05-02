package com.example.kybl.emailparser;

public class UserInput {

    private String url;
    private int deepLevel;

    public UserInput() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getDeepLevel() {
        return deepLevel;
    }

    public void setDeepLevel(String deepLevel) {
        this.deepLevel = Integer.parseInt(deepLevel);
    }
}
