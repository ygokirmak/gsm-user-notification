package com.intercom.interview.invitation.service;

import com.intercom.interview.invitation.Constants;
import com.intercom.interview.invitation.domain.Customer;
import com.intercom.interview.invitation.domain.Location;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {InviteCustomerService.class,MapUtils.class})
public class InviteCustomerServiceTests {

	@InjectMocks
	private InviteCustomerService inviteCustomerService;

	@Mock
	private MapUtils mapUtils;

	@Before
	public void setUp() throws Exception {

		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void customerIsInRangeIfDistanceLessThanRange() {

		Location officeLocation = new Location(30.50, 40.50);
		Location customerLocation = new Location(30.50, 40.50);

		Mockito.when(mapUtils.distanceBetweenTwoPoints(any(),any())
		).thenReturn(100.0);
		assertThat(inviteCustomerService.isCustomerInRange(officeLocation,customerLocation, Constants.DISTANCE_TO_OFFICE)).isEqualTo(true);

		Mockito.when(mapUtils.distanceBetweenTwoPoints(any(),any())
		).thenReturn(99.99999);
		assertThat(inviteCustomerService.isCustomerInRange(officeLocation,customerLocation,Constants.DISTANCE_TO_OFFICE)).isEqualTo(true);

		Mockito.when(mapUtils.distanceBetweenTwoPoints(any(),any())
		).thenReturn(90.0);
		assertThat(inviteCustomerService.isCustomerInRange(officeLocation,customerLocation,Constants.DISTANCE_TO_OFFICE)).isEqualTo(true);

		Mockito.when(mapUtils.distanceBetweenTwoPoints(any(),any())
		).thenReturn(0.0);
		assertThat(inviteCustomerService.isCustomerInRange(officeLocation,customerLocation,Constants.DISTANCE_TO_OFFICE)).isEqualTo(true);
	}

	@Test
	public void customerIsInRangeIfDistanceMoreThanRange() {

		Location officeLocation = new Location(30.50, 40.50);
		Location customerLocation = new Location(30.50, 40.50);

		Mockito.when(mapUtils.distanceBetweenTwoPoints(any(),any())
		).thenReturn(100.00001);
		assertThat(inviteCustomerService.isCustomerInRange(officeLocation,customerLocation,Constants.DISTANCE_TO_OFFICE)).isEqualTo(false);

		Mockito.when(mapUtils.distanceBetweenTwoPoints(any(),any())
		).thenReturn(200.0);
		assertThat(inviteCustomerService.isCustomerInRange(officeLocation,customerLocation,Constants.DISTANCE_TO_OFFICE)).isEqualTo(false);

		Mockito.when(mapUtils.distanceBetweenTwoPoints(any(),any())
		).thenReturn(Double.MAX_VALUE);
		assertThat(inviteCustomerService.isCustomerInRange(officeLocation,customerLocation,Constants.DISTANCE_TO_OFFICE)).isEqualTo(false);

	}

	@Test
	public void failedToCheckCustomerIsInRangeIfArgumentsAreNull() {

		Location officeLocation = new Location(30.50, 40.50);
		Location customerLocation = new Location(30.50, 40.50);


		Mockito.when(mapUtils.distanceBetweenTwoPoints(any(),any())
		).thenReturn(100.00001);

		assertThatThrownBy(() -> inviteCustomerService.isCustomerInRange(null,customerLocation,Constants.DISTANCE_TO_OFFICE) )
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("Arguments can not be null");

		assertThatThrownBy(() -> inviteCustomerService.isCustomerInRange(officeLocation,null,Constants.DISTANCE_TO_OFFICE) )
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("Arguments can not be null");

		assertThatThrownBy(() -> inviteCustomerService.isCustomerInRange(null,null,Constants.DISTANCE_TO_OFFICE) )
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("Arguments can not be null");

	}

    @Test
    public void allCustomersAreInRangeIfDistancesAreLessThanRange() throws IOException {
        Location officeLocation = new Location(30.50, 40.50);

        Mockito.when(mapUtils.distanceBetweenTwoPoints(any(),any())
        ).thenReturn(90.0);

        String inputCustomerList = "{\"latitude\": \"52.986375\", \"user_id\": 12, \"name\": \"Christina McArdle\", \"longitude\": \"-6.043701\"}\n" +
                "{\"latitude\": \"51.92893\", \"user_id\": 1, \"name\": \"Alice Cahill\", \"longitude\": \"-10.27699\"}\n" +
                "{\"latitude\": \"51.8856167\", \"user_id\": 2, \"name\": \"Ian McArdle\", \"longitude\": \"-10.4240951\"}\n" +
                "{\"latitude\": \"52.3191841\", \"user_id\": 3, \"name\": \"Jack Enright\", \"longitude\": \"-8.5072391\"}\n" +
                "{\"latitude\": \"53.807778\", \"user_id\": 28, \"name\": \"Charlie Halligan\", \"longitude\": \"-7.714444\"}";


        InputStream stream = new ByteArrayInputStream(inputCustomerList.getBytes(StandardCharsets.UTF_8));

        List<Customer> customersWithinRange = inviteCustomerService.findCustomersWithinRange(stream,officeLocation,Constants.DISTANCE_TO_OFFICE);
        assertThat(customersWithinRange.size()).isEqualTo(5);
        assertThat(customersWithinRange.get(0).getUserId()).isEqualTo(12);
        assertThat(customersWithinRange.get(1).getUserId()).isEqualTo(1);
        assertThat(customersWithinRange.get(2).getUserId()).isEqualTo(2);
        assertThat(customersWithinRange.get(3).getUserId()).isEqualTo(3);
        assertThat(customersWithinRange.get(4).getUserId()).isEqualTo(28);

    }

    @Test
    public void noneCustomersAreInRangeIfDistancesAreMoreThanRange() throws IOException {
        Location officeLocation = new Location(30.50, 40.50);

        Mockito.when(mapUtils.distanceBetweenTwoPoints(any(),any())
        ).thenReturn(110.0);

        String inputCustomerList = "{\"latitude\": \"52.986375\", \"user_id\": 12, \"name\": \"Christina McArdle\", \"longitude\": \"-6.043701\"}\n" +
                "{\"latitude\": \"51.92893\", \"user_id\": 1, \"name\": \"Alice Cahill\", \"longitude\": \"-10.27699\"}\n" +
                "{\"latitude\": \"51.8856167\", \"user_id\": 2, \"name\": \"Ian McArdle\", \"longitude\": \"-10.4240951\"}\n" +
                "{\"latitude\": \"52.3191841\", \"user_id\": 3, \"name\": \"Jack Enright\", \"longitude\": \"-8.5072391\"}\n" +
                "{\"latitude\": \"53.807778\", \"user_id\": 28, \"name\": \"Charlie Halligan\", \"longitude\": \"-7.714444\"}";


        InputStream stream = new ByteArrayInputStream(inputCustomerList.getBytes(StandardCharsets.UTF_8));

        List<Customer> customersWithinRange = inviteCustomerService.findCustomersWithinRange(stream,officeLocation,Constants.DISTANCE_TO_OFFICE);
        assertThat(customersWithinRange.size()).isEqualTo(0);

    }

    @Test
    public void someCustomersAreInRangeIfDistancesAreMoreThanRange() throws IOException {
        Location officeLocation = new Location(30.50, 40.50);

        String inputCustomerList = "{\"latitude\": \"52.986375\", \"user_id\": 12, \"name\": \"Christina McArdle\", \"longitude\": \"-6.043701\"}\n" +
                "{\"latitude\": \"51.92893\", \"user_id\": 1, \"name\": \"Alice Cahill\", \"longitude\": \"-10.27699\"}\n" +
                "{\"latitude\": \"51.8856167\", \"user_id\": 2, \"name\": \"Ian McArdle\", \"longitude\": \"-10.4240951\"}\n" +
                "{\"latitude\": \"52.3191841\", \"user_id\": 3, \"name\": \"Jack Enright\", \"longitude\": \"-8.5072391\"}\n" +
                "{\"latitude\": \"53.807778\", \"user_id\": 28, \"name\": \"Charlie Halligan\", \"longitude\": \"-7.714444\"}";


        InputStream stream = new ByteArrayInputStream(inputCustomerList.getBytes(StandardCharsets.UTF_8));

        Mockito.when(mapUtils.distanceBetweenTwoPoints(any(),any())
        ).thenReturn(110.0).thenReturn(105.0).thenReturn(90.0);

        List<Customer> customersWithinRange = inviteCustomerService.findCustomersWithinRange(stream,officeLocation,Constants.DISTANCE_TO_OFFICE);
        assertThat(customersWithinRange.size()).isEqualTo(3);
        assertThat(customersWithinRange.get(0).getUserId()).isEqualTo(2);
        assertThat(customersWithinRange.get(1).getUserId()).isEqualTo(3);
        assertThat(customersWithinRange.get(2).getUserId()).isEqualTo(28);

        stream.reset();
        Mockito.when(mapUtils.distanceBetweenTwoPoints(any(),any())
        ).thenReturn(120.0).thenReturn(105.0).thenReturn(200.0).thenReturn(60.0).thenReturn(120.0);

        customersWithinRange = inviteCustomerService.findCustomersWithinRange(stream,officeLocation,Constants.DISTANCE_TO_OFFICE);
        assertThat(customersWithinRange.size()).isEqualTo(1);
        assertThat(customersWithinRange.get(0).getUserId()).isEqualTo(3);

    }

    @Test
    public void noneCustomersAreInRangeIfCustomerListEmpty() throws IOException {
        Location officeLocation = new Location(30.50, 40.50);

        String inputCustomerList = "";

        InputStream stream = new ByteArrayInputStream(inputCustomerList.getBytes(StandardCharsets.UTF_8));

        Mockito.when(mapUtils.distanceBetweenTwoPoints(any(),any())
        ).thenReturn(90.0);

        List<Customer> customersWithinRange = inviteCustomerService.findCustomersWithinRange(stream,officeLocation,Constants.DISTANCE_TO_OFFICE);
        assertThat(customersWithinRange.size()).isEqualTo(0);
    }

    @Test
    public void failedToFindCustomersInRangeIfInputIsNull() throws IOException {
        Location officeLocation = new Location(30.50, 40.50);
        InputStream stream = new ByteArrayInputStream("".getBytes(StandardCharsets.UTF_8));


        Mockito.when(mapUtils.distanceBetweenTwoPoints(any(),any())
        ).thenReturn(90.0);

        assertThatThrownBy(() -> inviteCustomerService.findCustomersWithinRange(null,officeLocation,Constants.DISTANCE_TO_OFFICE) )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Arguments can not be null");

        assertThatThrownBy(() -> inviteCustomerService.findCustomersWithinRange(stream, null,Constants.DISTANCE_TO_OFFICE) )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Arguments can not be null");

        assertThatThrownBy(() -> inviteCustomerService.findCustomersWithinRange(null, null,Constants.DISTANCE_TO_OFFICE) )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Arguments can not be null");

    }

    @Test
    public void sortCustomersAscendingIfCustomerListNotEmpty() throws IOException {
	    Customer customer1 = new Customer(1, "customer1", 50.0, 50.0);
        Customer customer2 = new Customer(10, "customer2", 50.0, 50.0);
        Customer customer3 = new Customer(-101, "customer3", 50.0, 50.0);
        Customer customer4 = new Customer(100, "customer3", 50.0, 50.0);
        Customer customer5 = new Customer(120, "customer4", 50.0, 50.0);
        Customer customer6 = new Customer(-100, "customer4", 50.0, 50.0);
        Customer customer7 = new Customer(-10000, "customer5", 50.0, 50.0);

        List<Customer> unsortedCustomers = new ArrayList<>();
        unsortedCustomers.add(customer1);
        unsortedCustomers.add(customer2);
        unsortedCustomers.add(customer3);
        unsortedCustomers.add(customer4);
        unsortedCustomers.add(customer5);
        unsortedCustomers.add(customer6);
        unsortedCustomers.add(customer7);

        List<Customer> sortedCustomers = inviteCustomerService.sortCustomers(unsortedCustomers);

        assertThat(sortedCustomers.size()).isEqualTo(7);
        assertThat(sortedCustomers.get(0).getUserId()).isEqualTo(-10000);
        assertThat(sortedCustomers.get(1).getUserId()).isEqualTo(-101);
        assertThat(sortedCustomers.get(2).getUserId()).isEqualTo(-100);
        assertThat(sortedCustomers.get(3).getUserId()).isEqualTo(1);
        assertThat(sortedCustomers.get(4).getUserId()).isEqualTo(10);
        assertThat(sortedCustomers.get(5).getUserId()).isEqualTo(100);
        assertThat(sortedCustomers.get(6).getUserId()).isEqualTo(120);

    }

    @Test
    public void notSortCustomersIfCustomerListEmpty() throws IOException {

        List<Customer> unsortedCustomers = new ArrayList<>();

        List<Customer> sortedCustomers = inviteCustomerService.sortCustomers(unsortedCustomers);

        assertThat(sortedCustomers.size()).isEqualTo(0);
    }

    @Test
    public void failedToSortCustomersIfCustomerListIsNull() throws IOException {

        assertThatThrownBy(() -> inviteCustomerService.sortCustomers(null) )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Argument can not be null");

    }

    @Test
    public void printCustomersIfCustomerListIsNotEmpty() throws IOException {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Customer customer1 = new Customer(1, "customer1", 51.123456, 61.123456);
        Customer customer2 = new Customer(10, "customer2", 10.654321, 20.654321);
        Customer customer3 = new Customer(20, "customer3", 20.654321, 40.654321);
        Customer customer4 = new Customer(30, "customer4", 30.654321, 50.654321);

        List<Customer> customers = new ArrayList<>();
        customers.add(customer1);
        customers.add(customer2);
        customers.add(customer3);
        customers.add(customer4);

        String expectedOutput = "{\"latitude\":\"51.123456\",\"user_id\":1,\"name\":\"customer1\",\"longitude\":\"61.123456\"}\n" +
                "{\"latitude\":\"10.654321\",\"user_id\":10,\"name\":\"customer2\",\"longitude\":\"20.654321\"}\n" +
                "{\"latitude\":\"20.654321\",\"user_id\":20,\"name\":\"customer3\",\"longitude\":\"40.654321\"}\n" +
                "{\"latitude\":\"30.654321\",\"user_id\":30,\"name\":\"customer4\",\"longitude\":\"50.654321\"}";

        inviteCustomerService.writeCustomersToStream(customers,outputStream);

        assertThat(outputStream.toString()).isEqualTo(expectedOutput);

    }

    @Test
    public void notPrintCustomersIfCustomerListIsEmpty() throws IOException {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        List<Customer> customers = new ArrayList<>();

        String expectedOutput = "";

        inviteCustomerService.writeCustomersToStream(customers,outputStream);

        assertThat(outputStream.toString()).isEqualTo(expectedOutput);

    }

    @Test
    public void failedToPrintCustomersIfArgumentsAreNull() throws IOException {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        List<Customer> customers = new ArrayList<>();

        assertThatThrownBy(() -> inviteCustomerService.writeCustomersToStream(null,outputStream) )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Arguments can not be null");

        assertThatThrownBy(() -> inviteCustomerService.writeCustomersToStream(customers,null) )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Arguments can not be null");

        assertThatThrownBy(() -> inviteCustomerService.writeCustomersToStream(null,null) )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Arguments can not be null");

    }

    @Test
    public void printCustomersAscendingIfCustomersAreInRange() throws IOException {
        Location officeLocation = new Location(30.50, 40.50);

        String inputCustomerList = "{\"latitude\": \"52.986375\", \"user_id\": 12, \"name\": \"Christina McArdle\", \"longitude\": \"-6.043701\"}\n" +
                "{\"latitude\": \"51.92893\", \"user_id\": 1, \"name\": \"Alice Cahill\", \"longitude\": \"-10.27699\"}\n" +
                "{\"latitude\": \"51.8856167\", \"user_id\": 2, \"name\": \"Ian McArdle\", \"longitude\": \"-10.4240951\"}\n" +
                "{\"latitude\": \"52.3191841\", \"user_id\": 3, \"name\": \"Jack Enright\", \"longitude\": \"-8.5072391\"}\n" +
                "{\"latitude\": \"53.807778\", \"user_id\": 28, \"name\": \"Charlie Halligan\", \"longitude\": \"-7.714444\"}";

        String expectedOutput = "{\"latitude\":\"51.92893\",\"user_id\":1,\"name\":\"Alice Cahill\",\"longitude\":\"-10.27699\"}\n" +
                "{\"latitude\":\"51.8856167\",\"user_id\":2,\"name\":\"Ian McArdle\",\"longitude\":\"-10.4240951\"}\n" +
                "{\"latitude\":\"52.3191841\",\"user_id\":3,\"name\":\"Jack Enright\",\"longitude\":\"-8.5072391\"}\n" +
                "{\"latitude\":\"52.986375\",\"user_id\":12,\"name\":\"Christina McArdle\",\"longitude\":\"-6.043701\"}\n" +
                "{\"latitude\":\"53.807778\",\"user_id\":28,\"name\":\"Charlie Halligan\",\"longitude\":\"-7.714444\"}";


        InputStream inputStream = new ByteArrayInputStream(inputCustomerList.getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Mockito.when(mapUtils.distanceBetweenTwoPoints(any(),any())
        ).thenReturn(80.0);

        inviteCustomerService.writeCustomersWithinRangeToStream(inputStream, outputStream, officeLocation, Constants.DISTANCE_TO_OFFICE);

        assertThat(outputStream.toString()).isEqualTo(expectedOutput);

    }

    @Test
    public void printCustomersAscendingIfSomeCustomersAreInRange() throws IOException {
        Location officeLocation = new Location(30.50, 40.50);

        String inputCustomerList = "{\"latitude\": \"52.986375\", \"user_id\": 12, \"name\": \"Christina McArdle\", \"longitude\": \"-6.043701\"}\n" +
                "{\"latitude\": \"51.92893\", \"user_id\": 1, \"name\": \"Alice Cahill\", \"longitude\": \"-10.27699\"}\n" +
                "{\"latitude\": \"51.8856167\", \"user_id\": 2, \"name\": \"Ian McArdle\", \"longitude\": \"-10.4240951\"}\n" +
                "{\"latitude\": \"52.3191841\", \"user_id\": 3, \"name\": \"Jack Enright\", \"longitude\": \"-8.5072391\"}\n" +
                "{\"latitude\": \"53.807778\", \"user_id\": 28, \"name\": \"Charlie Halligan\", \"longitude\": \"-7.714444\"}";

        String expectedOutput = "{\"latitude\":\"52.3191841\",\"user_id\":3,\"name\":\"Jack Enright\",\"longitude\":\"-8.5072391\"}\n"+
                "{\"latitude\":\"52.986375\",\"user_id\":12,\"name\":\"Christina McArdle\",\"longitude\":\"-6.043701\"}";


        InputStream inputStream = new ByteArrayInputStream(inputCustomerList.getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Mockito.when(mapUtils.distanceBetweenTwoPoints(any(),any())
        ).thenReturn(80.0).thenReturn(120.0).thenReturn(110.0).thenReturn(50.0).thenReturn(110.0);

        inviteCustomerService.writeCustomersWithinRangeToStream(inputStream, outputStream, officeLocation, Constants.DISTANCE_TO_OFFICE);

        assertThat(outputStream.toString()).isEqualTo(expectedOutput);

    }

    @Test
    public void notPrintCustomersAscendingIfNoneCustomersAreInRange() throws IOException {
        Location officeLocation = new Location(30.50, 40.50);

        String inputCustomerList = "{\"latitude\": \"52.986375\", \"user_id\": 12, \"name\": \"Christina McArdle\", \"longitude\": \"-6.043701\"}\n" +
                "{\"latitude\": \"51.92893\", \"user_id\": 1, \"name\": \"Alice Cahill\", \"longitude\": \"-10.27699\"}\n" +
                "{\"latitude\": \"51.8856167\", \"user_id\": 2, \"name\": \"Ian McArdle\", \"longitude\": \"-10.4240951\"}\n" +
                "{\"latitude\": \"52.3191841\", \"user_id\": 3, \"name\": \"Jack Enright\", \"longitude\": \"-8.5072391\"}\n" +
                "{\"latitude\": \"53.807778\", \"user_id\": 28, \"name\": \"Charlie Halligan\", \"longitude\": \"-7.714444\"}";

        String expectedOutput = "";

        InputStream inputStream = new ByteArrayInputStream(inputCustomerList.getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Mockito.when(mapUtils.distanceBetweenTwoPoints(any(),any())
        ).thenReturn(110.0);

        inviteCustomerService.writeCustomersWithinRangeToStream(inputStream, outputStream, officeLocation, Constants.DISTANCE_TO_OFFICE);

        assertThat(outputStream.toString()).isEqualTo(expectedOutput);

    }

    @Test
    public void failedToPrintCustomersAscendingIfArgumentsAreNull() throws IOException {
        Location officeLocation = new Location(30.50, 40.50);

        String inputCustomerList = "{\"latitude\": \"52.986375\", \"user_id\": 12, \"name\": \"Christina McArdle\", \"longitude\": \"-6.043701\"}\n" +
                "{\"latitude\": \"51.92893\", \"user_id\": 1, \"name\": \"Alice Cahill\", \"longitude\": \"-10.27699\"}\n" +
                "{\"latitude\": \"51.8856167\", \"user_id\": 2, \"name\": \"Ian McArdle\", \"longitude\": \"-10.4240951\"}\n" +
                "{\"latitude\": \"52.3191841\", \"user_id\": 3, \"name\": \"Jack Enright\", \"longitude\": \"-8.5072391\"}\n" +
                "{\"latitude\": \"53.807778\", \"user_id\": 28, \"name\": \"Charlie Halligan\", \"longitude\": \"-7.714444\"}";

        InputStream inputStream = new ByteArrayInputStream(inputCustomerList.getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Mockito.when(mapUtils.distanceBetweenTwoPoints(any(),any())
        ).thenReturn(110.0);

        inviteCustomerService.writeCustomersWithinRangeToStream(inputStream, outputStream, officeLocation, Constants.DISTANCE_TO_OFFICE);

        assertThatThrownBy(() -> inviteCustomerService.writeCustomersWithinRangeToStream(null, outputStream, officeLocation, Constants.DISTANCE_TO_OFFICE) )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Arguments can not be null");

        assertThatThrownBy(() -> inviteCustomerService.writeCustomersWithinRangeToStream(inputStream, null, officeLocation, Constants.DISTANCE_TO_OFFICE) )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Arguments can not be null");

        assertThatThrownBy(() -> inviteCustomerService.writeCustomersWithinRangeToStream(inputStream, outputStream, null, Constants.DISTANCE_TO_OFFICE) )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Arguments can not be null");

        assertThatThrownBy(() -> inviteCustomerService.writeCustomersWithinRangeToStream(null, null, null, Constants.DISTANCE_TO_OFFICE) )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Arguments can not be null");

    }

}
