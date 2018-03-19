package com.intercom.interview.invitation.domain;

import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

public class CustomerMapper {

    private static ObjectMapper jsonMapper = new ObjectMapper();

    public static Customer toCustomer(String customerString) throws IOException {
        return jsonMapper.readValue(customerString, Customer.class);
    }

    public static String toString(Customer customer) throws IOException {
        return jsonMapper.writeValueAsString(customer);
    }
}
