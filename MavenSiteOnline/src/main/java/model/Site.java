package model;

public class Site {
    private String url;
    private String lastContent;
    private boolean isOnline;
    private String lastChangeTime;

    public Site(String url) {
        this.url = ensureHttpProtocol(url);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = ensureHttpProtocol(url);
    }

    public String getLastContent() {
        return lastContent;
    }

    public void setLastContent(String lastContent) {
        this.lastContent = lastContent;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public String getLastChangeTime() {
		return lastChangeTime;
	}

	public void setLastChangeTime(String lastChangeTime) {
		this.lastChangeTime = lastChangeTime;
	}

	public void setOnline(boolean isOnline) {
        this.isOnline = isOnline;
    }

    private String ensureHttpProtocol(String url) {
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            return "http://" + url;
        }
        return url;
    }
}
