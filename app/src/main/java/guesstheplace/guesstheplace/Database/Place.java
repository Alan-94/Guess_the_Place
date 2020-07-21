package guesstheplace.guesstheplace.Database;



public class Place {

    private String name;
    private String imageUrl;
    private double latitude;
    private double longitude;

    public Place( String name, String imageUrl, double latitude, double longitude) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.latitude = latitude;
        this.longitude = longitude;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
