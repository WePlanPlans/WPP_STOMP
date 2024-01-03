package org.tenten.tentenstomp.domain.trip.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tenten.tentenstomp.domain.trip.dto.request.TripCreateRequest;
import org.tenten.tentenstomp.domain.trip.dto.request.TripEditRequest;
import org.tenten.tentenstomp.domain.trip.dto.response.TripEditResponse;
import org.tenten.tentenstomp.domain.trip.repository.TripItemRepository;
import org.tenten.tentenstomp.domain.trip.repository.TripRepository;

@Service
@RequiredArgsConstructor
public class TripService {

    private final TripRepository tripRepository;
    private final TripItemRepository tripItemRepository;

    @Transactional
    public Long save(TripCreateRequest request) {

        return null;
    }

    @Transactional
    public TripEditResponse update(String planId, TripEditRequest request) {

        return null;
    }
}
