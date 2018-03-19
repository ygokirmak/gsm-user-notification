package com.intercom.interview.invitation.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.ser.std.ToStringSerializer;

@JsonPropertyOrder({ "latitude", "userId", "name", "longitude" })
public class Customer {

    @JsonSerialize(using = ToStringSerializer.class)
    private double latitude;
    @JsonSerialize(using = ToStringSerializer.class)
    private double longitude;
    @JsonProperty( "user_id")
    private int userId;
    private String name;

    public Customer() {
    }

    public Customer(int userId, String name, double latitude, double longitude) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.name = name;
        this.userId = userId;
    }

    public double getLongitude() {
        return longitude;
    }

    public Customer setLongitude(double longitude) {
        this.longitude = longitude;
        return this;
    }

    public double getLatitude() {
        return latitude;
    }

    public Customer setLatitude(double latitude) {
        this.latitude = latitude;
        return this;
    }

    public String getName() {
        return name;
    }

    public Customer setName(String name) {
        this.name = name;
        return this;
    }

    public int getUserId() {
        return userId;
    }

    public Customer setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "longitude=" + longitude +
                ", latitude=" + latitude +
                ", name='" + name + '\'' +
                ", userId=" + userId +
                '}';
    }
}
