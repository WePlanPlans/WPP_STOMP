package org.tenten.tentenstomp.domain.trip.repository;


import org.tenten.tentenstomp.domain.trip.dto.response.*;
import org.tenten.tentenstomp.domain.trip.entity.Trip;

import java.util.HashMap;
import java.util.Map;

public interface MessageProxyRepository {

    TripMemberMsg getTripMemberMsg(Long tripId, Map<String, HashMap<Long, TripMemberInfoMsg>> tripConnectedMemberMap);

    TripBudgetMsg getTripBudgetMsg(Trip trip);

    TripItemMsg getTripItemMsg(Long tripId, String visitDate);

    TripPathMsg getTripPathMsg(Long tripId, String visitDate);
}
