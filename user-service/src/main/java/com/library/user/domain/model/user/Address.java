package com.library.user.domain.model.user;

import com.library.user.domain.exception.InvalidUserDataException;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serializable;

/**
 * Address value object
 */
@Getter
@EqualsAndHashCode
public class Address implements Serializable {
    
    private final String street;
    private final String ward;
    private final String district;
    private final String city;
    private final String province;
    private final String postalCode;
    private final String country;
    
    public Address(String street, String ward, String district, String city, 
                   String province, String postalCode, String country) {
        validateAddress(street, city, province, country);
        
        this.street = street;
        this.ward = ward;
        this.district = district;
        this.city = city;
        this.province = province;
        this.postalCode = postalCode;
        this.country = country;
    }
    
    public static Address create(String street, String ward, String district, 
                               String city, String province, String postalCode) {
        return new Address(street, ward, district, city, province, postalCode, "Vietnam");
    }
    
    public static Address createSimple(String street, String city, String province) {
        return new Address(street, null, null, city, province, null, "Vietnam");
    }
    
    private void validateAddress(String street, String city, String province, String country) {
        if (street == null || street.trim().isEmpty()) {
            throw new InvalidUserDataException("address", "Street address cannot be empty");
        }
        
        if (city == null || city.trim().isEmpty()) {
            throw new InvalidUserDataException("address", "City cannot be empty");
        }
        
        if (province == null || province.trim().isEmpty()) {
            throw new InvalidUserDataException("address", "Province cannot be empty");
        }
        
        if (country == null || country.trim().isEmpty()) {
            throw new InvalidUserDataException("address", "Country cannot be empty");
        }
    }
    
    public String getFullAddress() {
        StringBuilder sb = new StringBuilder();
        
        if (street != null) sb.append(street);
        if (ward != null) sb.append(", ").append(ward);
        if (district != null) sb.append(", ").append(district);
        if (city != null) sb.append(", ").append(city);
        if (province != null) sb.append(", ").append(province);
        if (country != null) sb.append(", ").append(country);
        
        return sb.toString();
    }
    
    public boolean isInVietnam() {
        return "Vietnam".equalsIgnoreCase(country) || "Việt Nam".equalsIgnoreCase(country);
    }
}