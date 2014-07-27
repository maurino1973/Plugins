package ue.unifiedviews.plugins.loaders.files.local;

public class Configuration {
	
    private String destination = "/tmp";

    private boolean replaceExisting = false;

    public Configuration() {
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public boolean isReplaceExisting() {
        return replaceExisting;
    }

    public void setReplaceExisting(boolean replaceExisting) {
        this.replaceExisting = replaceExisting;
    }

}
