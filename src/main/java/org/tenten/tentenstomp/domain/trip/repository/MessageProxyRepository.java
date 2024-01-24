package org.tenten.tentenstomp.domain.trip.repository;


import org.tenten.tentenstomp.domain.trip.dto.response.TripBudgetMsg;
import org.tenten.tentenstomp.domain.trip.dto.response.TripItemMsg;
import org.tenten.tentenstomp.domain.trip.dto.response.TripMemberMsg;
import org.tenten.tentenstomp.domain.trip.dto.response.TripPathMsg;
import org.tenten.tentenstomp.domain.trip.entity.Trip;

import java.util.HashSet;
import java.util.Map;

public interface MessageProxyRepository {

    TripMemberMsg getTripMemberMsg(String tripId, Map<String, HashSet<Long>> tripConnectedMemberMap);

    TripBudgetMsg getTripBudgetMsg(Trip trip);

    TripItemMsg getTripItemMsg(String tripId, String visitDate);

    TripPathMsg getTripPathMsg(String tripId, String visitDate);
}
