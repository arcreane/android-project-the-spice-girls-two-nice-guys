package com.barometre.myapplication.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Core data model for a bar.
 * Extended with rating, tags, and photoUrl.
 * Implements Serializable so it can be passed through Intent extras.
 */
public class Bar implements Serializable {
    // Basic fields
    private String id;
    private String name;
    private String street;
    private String houseNumber;
    private String postcode;
    private String city;
    private String phone;
    private String website;
    private String openingHours;

    private String type;
    private double latitude;
    private double longitude;

    // Extra app fields
    private double rating;
    private List<String> tags;
    private String photoUrl;
    private boolean isFavorite;
    public Bar() {}

    // Main constructor
    public Bar(String id,
               String name,
               String street,
               String houseNumber,
               String postcode,
               String city,
               String phone,
               String website,
               String openingHours,
               String type,
               double latitude,
               double longitude,
               double rating,
               List<String> tags,
               String photoUrl) {

        this.id = id;
        this.name = name;
        this.street = street;
        this.houseNumber = houseNumber;
        this.postcode = postcode;
        this.city = city;
        this.phone = phone;
        this.website = website;
        this.openingHours = openingHours;
        this.type = type;
        this.latitude = latitude;
        this.longitude = longitude;

        this.rating = rating;

        // Prevent null pointer issues
        this.tags = tags != null ? tags : new ArrayList<>();

        this.photoUrl = photoUrl;

        // Default value when app starts
        this.isFavorite = false;
    }
    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(String openingHours) {
        this.openingHours = openingHours;
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

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
    // Helpers
    /**
     * Returns formatted address for display.
     */
    public String getFullAddress() {

        StringBuilder sb = new StringBuilder();

        if (houseNumber != null && !houseNumber.isEmpty()) {
            sb.append(houseNumber).append(" ");
        }

        if (street != null && !street.isEmpty()) {
            sb.append(street).append(", ");
        }

        if (postcode != null && !postcode.isEmpty()) {
            sb.append(postcode).append(" ");
        }

        if (city != null && !city.isEmpty()) {
            sb.append(city);
        }

        return sb.toString().trim();
    }

    /**
     * Returns true if bar has valid coordinates.
     */
    public boolean hasCoordinates() {
        return latitude != 0.0 && longitude != 0.0;
    }

    /**
     * Converts tags list into display text.
     * Example:
     * rooftop • cocktails • live music
     */
    public String getTagsString() {

        if (tags == null || tags.isEmpty()) {
            return "";
        }

        return String.join(" • ", tags);
    }
    // Debug helper
    @Override
    public String toString() {
        return "Bar{id='"
                + id
                + "', name='"
                + name
                + "', city='"
                + city
                + "'}";
    }
}