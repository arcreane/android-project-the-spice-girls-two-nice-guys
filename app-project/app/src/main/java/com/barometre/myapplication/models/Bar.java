package com.barometre.myapplication.models;

import java.io.Serializable;
import java.util.List;

/**
 * Core data model for a bar.
 * extended with rating and photo_url
 * it can be passed between activities via intent extras.
 */
public class Bar implements Serializable {

    // fields
    private String id;
    private String name;
    private String street;
    private String houseNumber;
    private String postcode;
    private String city;
    private String phone;
    private String website;
    private String openingHours;
    private double latitude;
    private double longitude;

    // new fields
    private double rating;
    private String photoUrl;     //loaded by glide
    private boolean isFavorite;

    public Bar() {}

    public Bar(String id, String name, String street, String houseNumber,
               String postcode, String city, String phone, String website,
               String openingHours, double latitude, double longitude,
               double rating, List<String> tags, String photoUrl) {
        this.id = id;
        this.name = name;
        this.street = street;
        this.houseNumber = houseNumber;
        this.postcode = postcode;
        this.city = city;
        this.phone = phone;
        this.website = website;
        this.openingHours = openingHours;
        this.latitude = latitude;
        this.longitude = longitude;
        this.rating = rating;
        this.photoUrl = photoUrl;
        this.isFavorite = false;
    }

// setters and getters

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }

    public String getHouseNumber() { return houseNumber; }
    public void setHouseNumber(String houseNumber) { this.houseNumber = houseNumber; }

    public String getPostcode() { return postcode; }
    public void setPostcode(String postcode) { this.postcode = postcode; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }

    public String getOpeningHours() { return openingHours; }
    public void setOpeningHours(String openingHours) { this.openingHours = openingHours; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public boolean isFavorite() { return isFavorite; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }

    // ----------------------------------------------------------------
    // Helpers
    // ----------------------------------------------------------------

    /**  address to display in the detail screen. */
    public String getFullAddress() {
        StringBuilder sb = new StringBuilder();
        if (houseNumber != null && !houseNumber.isEmpty()) sb.append(houseNumber).append(" ");
        if (street != null && !street.isEmpty()) sb.append(street).append(", ");
        if (postcode != null && !postcode.isEmpty()) sb.append(postcode).append(" ");
        if (city != null && !city.isEmpty()) sb.append(city);
        return sb.toString().trim();
    }

    /** returns true if the bar has  GPS coordinates. */
    public boolean hasCoordinates() {
        return latitude != 0.0 && longitude != 0.0;
    }


    @Override
    public String toString() {
        return "Bar{id='" + id + "', name='" + name + "', city='" + city + "'}";
    }
}