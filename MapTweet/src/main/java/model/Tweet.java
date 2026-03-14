package model;

import java.time.LocalDateTime;
// ツイートデータBean
public class Tweet {
    private String text;
    private double latitude;
    private double longitude;
    private LocalDateTime postedAt;

    public Tweet() {}

    public Tweet(String text, double latitude, double longitude, LocalDateTime postedAt) {
        this.text = text;
        this.latitude = latitude;
        this.longitude = longitude;
        this.postedAt = postedAt;
    }

    // Getters & Setters
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public LocalDateTime getPostedAt() { return postedAt; }
    public void setPostedAt(LocalDateTime postedAt) { this.postedAt = postedAt; }
}