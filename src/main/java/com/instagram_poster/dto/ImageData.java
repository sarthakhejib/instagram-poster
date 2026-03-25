package com.instagram_poster.dto;

public class ImageData {

    private String url;
    private String name;
    private String publicId;

    public ImageData(String url, String name, String publicId) {
        this.url = url;
        this.name = name;
        this.publicId = publicId;
    }

    public String getUrl() { return url; }
    public String getName() { return name; }
    public String getPublicId() { return publicId; }
}