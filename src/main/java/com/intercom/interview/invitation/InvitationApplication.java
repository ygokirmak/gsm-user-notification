package com.intercom.interview.invitation;

import com.intercom.interview.invitation.domain.Location;
import com.intercom.interview.invitation.service.InviteCustomerFileService;
import com.intercom.interview.invitation.service.InviteCustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class InvitationApplication implements ApplicationRunner {

	private static final Logger logger = LoggerFactory.getLogger(InvitationApplication.class);

	@Autowired
	InviteCustomerFileService inviteCustomerFileService;

	public static void main(String[] args) {
		SpringApplication.run(InvitationApplication.class, args);
	}


	@Override
	public void run(ApplicationArguments args) throws Exception {

		String inputFile = null ;
		String outputFile = null ;

		if( !args.containsOption("inputFile")){
			inputFile = Constants.DEFAULT_INPUT_FILE;
		}else{
		    inputFile = args.getOptionValues("inputFile").get(0);
        }

		if( !args.containsOption("outputFile")){
			outputFile = Constants.DEFAULT_OUTPUT_FILE;
		}else{
            outputFile = args.getOptionValues("outputFile").get(0);
        }

		Location intercomOffice = new Location(53.339428, -6.257664);

		try {
            inviteCustomerFileService.printCustomersWithinRangeFromFile(inputFile,outputFile,intercomOffice,Constants.DISTANCE_TO_OFFICE);
        }catch (IOException ex){
		    logger.error("Program execution error",ex);
        }
	}

}
