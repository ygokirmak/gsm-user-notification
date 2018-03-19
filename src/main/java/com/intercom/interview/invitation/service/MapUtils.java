package com.intercom.interview.invitation.service;

import com.intercom.interview.invitation.domain.Location;
import org.springframework.stereotype.Service;

@Service
public class MapUtils {
    public double distanceBetweenTwoPoints(Location point1, Location point2) {

        if (point1 == null || point2==null) {
            throw new IllegalArgumentException("Arguments can not be null");
        }

        double x1 = Math.toRadians(point1.getLatitude());
        double y1 = Math.toRadians(point1.getLongitude());
        double x2 = Math.toRadians(point2.getLatitude());
        double y2 = Math.toRadians(point2.getLongitude());

        /*************************************************************************
         * Compute using law of cosines
         *************************************************************************/
        // great circle distance in radians
        double angle1 = Math.acos(
                (Math.sin(x1) * Math.sin(x2))
                        + (Math.cos(x1) * Math.cos(x2) * Math.cos(y1 - y2)));

        // convert back to degrees
        // angle1 = Math.toDegrees(angle1);

        // each degree on a great circle of Earth is 60 nautical miles
        double distanceBetweenTwoPoints = 6371.01 * angle1;

        return distanceBetweenTwoPoints;

    }
}
