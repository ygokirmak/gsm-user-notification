package com.intercom.interview.invitation.service;

import com.intercom.interview.invitation.domain.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
public class InviteCustomerFileService {
    private static final Logger logger = LoggerFactory.getLogger(InviteCustomerFileService.class);

    @Autowired
    MapUtils mapUtils;

    @Autowired
    InviteCustomerService inviteCustomerService;

    public void printCustomersWithinRangeFromFile(String inputCustomersFileName, String outputFileName, Location reference, int distance) throws IOException {

        if (inputCustomersFileName == null || outputFileName == null || reference == null ) {
            throw new IllegalArgumentException("Input and output file names can not be null");
        }

        File inputFile = new File(inputCustomersFileName);
        File outputFile = new File(outputFileName);
        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;

        try {
            fileInputStream = new FileInputStream(inputFile);

        } catch (FileNotFoundException ex){
            throw new FileNotFoundException("Given customers input file is not exist");
        }

        try {
            outputFile.createNewFile();
        } catch (IOException e) {
            throw new IOException("Unable to create output file");
        }

        try {
            fileOutputStream = new FileOutputStream(outputFile);

            inviteCustomerService.writeCustomersWithinRangeToStream(fileInputStream, fileOutputStream, reference,distance);

        } catch (IOException e) {
            logger.error("File processing exception.. ", e);
        } finally {
            try {
                if (fileInputStream != null)
                    fileInputStream.close();
            } catch (IOException ex) {
                logger.error("Cloud not close input file stream, ", ex);
            }

            try {
                if (fileOutputStream != null)
                    fileOutputStream.close();
            } catch (IOException ex) {
                logger.error("Cloud not close output file stream, ", ex);
            }
        }

    }


}
