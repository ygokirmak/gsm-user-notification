package com.intercom.interview.invitation.service;

import com.intercom.interview.invitation.domain.Location;
import com.intercom.interview.invitation.service.InviteCustomerService;
import com.intercom.interview.invitation.service.MapUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@RunWith(SpringRunner.class)

@SpringBootTest(classes = MapUtils.class)
public class MapUtilsTests {

	@Autowired
	private MapUtils mapUtils;

	@Test
	public void distanceBetweenValidPoints() {

		/*
			Test case taken from https://software.intel.com/en-us/blogs/2012/11/25/calculating-geographic-distances-in-location-aware-apps

			Portland: 45.5371781 N, -122.6500385 E
			London: 51.5141667 N, 0.0936111 E
			Distance: 7912.891 km (4916.841 miles)
		 */

		Location p1 = new Location(45.5371781, -122.6500385);
		Location p2 = new Location(51.5141667, 0.0936111);

		double distance = mapUtils.distanceBetweenTwoPoints(p1,p2);

        distance = Math.round(distance*1000)/1000.0d;

        assertThat(distance).isEqualTo(7912.891);

    }

	@Test
	public void distanceBetweenNullPoints() {
		Location point = new Location(45.5371781, -122.6500385);

		assertThatThrownBy(() -> mapUtils.distanceBetweenTwoPoints(null,null) )
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("Arguments can not be null");

		assertThatThrownBy(() -> mapUtils.distanceBetweenTwoPoints(point,null) )
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("Arguments can not be null");

		assertThatThrownBy(() -> mapUtils.distanceBetweenTwoPoints(null,point) )
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("Arguments can not be null");

	}

}
