package com.intercom.interview.invitation.service;

import com.intercom.interview.invitation.Constants;
import com.intercom.interview.invitation.domain.Location;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {InviteCustomerFileService.class,InviteCustomerService.class, MapUtils.class})
public class InviteCustomerFileServiceTests {
    private static final Logger logger = LoggerFactory.getLogger(InviteCustomerFileServiceTests.class);

    @InjectMocks
    private InviteCustomerFileService inviteCustomerFileService;

    @Mock
    private InviteCustomerService inviteCustomerService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void writeCustomersToOutputFileIfInputAndOutputFilesAreValid() throws IOException {
        Location officeLocation = new Location(30.50, 40.50);

        File inputFile = File.createTempFile("temp-input-file", ".tmp");
        File outputFile = File.createTempFile("temp-output-file", ".tmp");

        String lineSeparator = System.getProperty("line.separator");
        String expectedContent = "line1 with spaces" + lineSeparator + "line2 with spaces and more spaces" + lineSeparator + "line3 ";

        Mockito.doAnswer((Answer) invocation -> {
            OutputStream arg1 = invocation.getArgument(1);
            Writer outputStreamWriter = new OutputStreamWriter(arg1);
            outputStreamWriter.write(expectedContent);
            outputStreamWriter.flush();
            return null;
        }).when(inviteCustomerService).writeCustomersWithinRangeToStream(any(),any(),any(),anyInt());


        inviteCustomerFileService.printCustomersWithinRangeFromFile(inputFile.getAbsolutePath(),outputFile.getAbsolutePath(),officeLocation, Constants.DISTANCE_TO_OFFICE);

        logger.info(outputFile.getAbsolutePath());

        assertThat(FileUtils.readFileToString(outputFile, "utf-8")).isEqualTo(expectedContent);

    }

    @Test
    public void failedToWriteCustomersToOutputFileIfInputAndOutputFileNamesAreNotValid() throws IOException {
        Location officeLocation = new Location(30.50, 40.50);

        File inputFile = File.createTempFile("temp-input-file", ".tmp");
        File outputFile = File.createTempFile("temp-output-file", ".tmp");

        assertThatThrownBy(() -> inviteCustomerFileService.printCustomersWithinRangeFromFile(null,outputFile.getAbsolutePath(),officeLocation,Constants.DISTANCE_TO_OFFICE) )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Input and output file names can not be null");

        assertThatThrownBy(() -> inviteCustomerFileService.printCustomersWithinRangeFromFile(inputFile.getAbsolutePath(),null,officeLocation,Constants.DISTANCE_TO_OFFICE) )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Input and output file names can not be null");

        assertThatThrownBy(() -> inviteCustomerFileService.printCustomersWithinRangeFromFile(null,null,officeLocation,Constants.DISTANCE_TO_OFFICE) )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Input and output file names can not be null");
    }

    @Test
    public void failedToWriteCustomersToOutputFileIfInputFileDoesNotExist() throws IOException {
        Location officeLocation = new Location(30.50, 40.50);

        File outputFile = File.createTempFile("temp-output-file", ".tmp");

        assertThatThrownBy(() -> inviteCustomerFileService.printCustomersWithinRangeFromFile("non-existing-input-file-name",outputFile.getAbsolutePath(),officeLocation,Constants.DISTANCE_TO_OFFICE) )
                .isInstanceOf(FileNotFoundException.class)
                .hasMessageContaining("Given customers input file is not exist");

    }

    @Test
    public void failedToWriteCustomersToOutputFileIfOutputFileCanNotCreated() throws IOException {
        Location officeLocation = new Location(30.50, 40.50);

        File inputFile = File.createTempFile("temp-input-file", ".tmp");

        assertThatThrownBy(() -> inviteCustomerFileService.printCustomersWithinRangeFromFile(inputFile.getAbsolutePath(),"/notexistingfolder",officeLocation,Constants.DISTANCE_TO_OFFICE) )
                .isInstanceOf(IOException.class)
                .hasMessageContaining("Unable to create output file");

    }

}
