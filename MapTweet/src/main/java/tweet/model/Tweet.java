package tweet.model; // ← ここだけ変更

import java.time.LocalDateTime;

public class Tweet {
    private String text;
    private double latitude;
    private double longitude;
    private LocalDateTime postedAt;
		private String userId; // ★ 追加
		
    public Tweet() {}

    public Tweet(String text, double latitude, double longitude, 
		    LocalDateTime postedAt, String userId) { // ★ userId 追加
        this.text = text;
        this.latitude = latitude;
        this.longitude = longitude;
        this.postedAt = postedAt;
        this.userId  = userId ;
    }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    public LocalDateTime getPostedAt() { return postedAt; }
    public void setPostedAt(LocalDateTime postedAt) { this.postedAt = postedAt; }
    public String getUserId() { return userId; } // ★ 追加
    public void setUserId(String userId) { this.userId = userId; } // ★ 追加
    }