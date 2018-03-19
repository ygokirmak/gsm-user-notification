package com.intercom.interview.invitation.service;

import com.intercom.interview.invitation.domain.Customer;
import com.intercom.interview.invitation.domain.CustomerMapper;
import com.intercom.interview.invitation.domain.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class InviteCustomerService {
    private static final Logger logger = LoggerFactory.getLogger(InviteCustomerService.class);

    @Autowired
    MapUtils mapUtils;

    public void writeCustomersWithinRangeToStream2(InputStream inputStream, OutputStream outputStream, Location reference, int distance) {
        logger.info("test");

    }


    public void writeCustomersWithinRangeToStream(InputStream inputStream, OutputStream outputStream, Location reference, int distance) throws IOException {

        if (inputStream == null || outputStream == null || reference == null ) {
            throw new IllegalArgumentException("Arguments can not be null");
        }

        List<Customer> filteredCustomers = findCustomersWithinRange(inputStream,reference,distance);

        List<Customer> sortedCustomers = sortCustomers(filteredCustomers);

        writeCustomersToStream(sortedCustomers,outputStream);

    }


    void writeCustomersToStream(List<Customer> sortedCustomers, OutputStream outputStream) throws IOException {

        if (sortedCustomers == null || outputStream == null ) {
            throw new IllegalArgumentException("Arguments can not be null");
        }

        String lineSeparator = System.getProperty("line.separator");
        Writer outputStreamWriter = new OutputStreamWriter(outputStream);

        for( int i=0 ; i < sortedCustomers.size(); i++){
            Customer customer = sortedCustomers.get(i);
            outputStreamWriter.write(CustomerMapper.toString(customer));
            if( i != sortedCustomers.size()-1){
                outputStreamWriter.write(lineSeparator);
            }
        }

        outputStreamWriter.flush();
    }

    List<Customer> sortCustomers(List<Customer> filteredCustomers) {
        if (filteredCustomers == null ) {
            throw new IllegalArgumentException("Argument can not be null");
        }
        filteredCustomers.sort(Comparator.comparingInt(Customer::getUserId));

        return filteredCustomers;
    }

    List<Customer> findCustomersWithinRange(InputStream inputStream, Location reference, int distance) throws IOException {
        if (inputStream == null || reference==null) {
            throw new IllegalArgumentException("Arguments can not be null");
        }
        List<Customer> nearCustomers = new ArrayList<>();

        if( inputStream != null){
            BufferedReader reader=new BufferedReader(new InputStreamReader(inputStream));

            while(reader.ready())
            {
                String line = reader.readLine();
                Customer customer = CustomerMapper.toCustomer(line);
                Location customerLocation = new Location(customer.getLatitude(),customer.getLongitude());
                if( isCustomerInRange(reference,customerLocation,distance)){
                    nearCustomers.add(customer);
                }
            }
        }

        return nearCustomers;
    }

    boolean isCustomerInRange(Location reference, Location customerLocation, int distance) {
        if (reference == null || customerLocation==null) {
            throw new IllegalArgumentException("Arguments can not be null");
        }

        double customerDistanceToRef = mapUtils.distanceBetweenTwoPoints(reference,customerLocation) ;
        return customerDistanceToRef <= distance;
    }

    @Override
    public String toString() {
        return "InviteCustomerService{" +
                "mapUtils=" + mapUtils +
                '}';
    }
}
