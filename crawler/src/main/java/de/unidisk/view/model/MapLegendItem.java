package de.unidisk.view.model;

public class MapLegendItem {
    private String iconUrl;
    private String name;

    public MapLegendItem(String iconUrl, String name) {
        this.iconUrl = iconUrl;
        this.name = name;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public String getName() {
        return name;
    }
}
