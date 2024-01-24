package org.tenten.tentenstomp.domain.trip.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.tenten.tentenstomp.domain.trip.dto.request.TripUpdateMsg;
import org.tenten.tentenstomp.domain.trip.dto.response.TripInfoMsg;
import org.tenten.tentenstomp.global.common.BaseTimeEntity;
import org.tenten.tentenstomp.global.common.enums.TripStatus;
import org.tenten.tentenstomp.global.converter.MapConverter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static jakarta.persistence.CascadeType.REMOVE;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static jakarta.persistence.InheritanceType.JOINED;
import static org.tenten.tentenstomp.global.common.enums.TripStatus.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Inheritance(strategy = JOINED)
public class Trip extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "tripId")
    private Long id;
    private Long numberOfPeople; // 인원수
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isDeleted;
    private String tripName;
    private Long budget;
    private String joinCode;
    private String encryptedId;
    @ColumnDefault("0")
    private Long tripItemPriceSum;
    @ColumnDefault("0")
    private Integer transportationPriceSum;
    @Convert(converter = MapConverter.class)
    @Column(columnDefinition = "JSON")
    private Map<String, Integer> tripPathPriceMap;
    @Convert(converter = MapConverter.class)
    @Column(columnDefinition = "JSON")
    private Map<String, String> tripTransportationMap;

    @OneToMany(mappedBy = "trip", fetch = LAZY, cascade = REMOVE)
    private final List<TripMember> tripMembers = new ArrayList<>();

    @OneToMany(mappedBy = "trip", fetch = LAZY, cascade = REMOVE)
    private final List<TripItem> tripItems = new ArrayList<>();

    @OneToMany(mappedBy = "trip", fetch = LAZY, cascade = REMOVE)
    private final List<TripLikedItem> tripLikedItems = new ArrayList<>();

    public TripInfoMsg changeTripInfo(TripUpdateMsg request) {
        this.startDate = LocalDate.parse(request.startDate());
        this.endDate = LocalDate.parse(request.endDate());
        this.numberOfPeople = request.numberOfPeople();
        this.tripName = request.tripName();
        this.budget = request.budget();
        LocalDate currentDate = LocalDate.now();

        TripStatus tripStatus;
        if (currentDate.isBefore(this.startDate)) {
            tripStatus = BEFORE;
        } else if (currentDate.isAfter(this.endDate)) {
            tripStatus = AFTER;
        } else {
            tripStatus = ING;
        }

        return new TripInfoMsg(this.getEncryptedId(), request.startDate(), request.endDate(), this.getNumberOfPeople(), this.getTripName(), tripStatus, this.getBudget());
    }

    public TripInfoMsg toTripInfo() {
        LocalDate currentDate = LocalDate.now();
        TripStatus tripStatus = null;
        if (currentDate.isBefore(this.startDate)) {
            tripStatus = BEFORE;
        } else if (currentDate.isAfter(this.endDate)) {
            tripStatus = AFTER;
        } else {
            tripStatus = ING;
        }
        return new TripInfoMsg(this.getEncryptedId(), this.startDate.toString(), this.endDate.toString(), this.getNumberOfPeople(), this.getTripName(), tripStatus, this.getBudget());
    }

    public void updateTripPathPriceMap(Map<String, Integer> tripPathPriceMap) {
        this.tripPathPriceMap = tripPathPriceMap;
    }

    public void updateTripTransportationMap(Map<String, String> tripTransportationMap) {
        this.tripTransportationMap = tripTransportationMap;
    }
    public void updateTransportationPriceSum(Integer oldVisitDateTransportationPriceSum, Integer newVisitDateTransportationPriceSum) {
        this.transportationPriceSum -= oldVisitDateTransportationPriceSum;
        this.transportationPriceSum += newVisitDateTransportationPriceSum;
    }

    public void updateTripItemPriceSum(Long oldTripItemPrice, Long newTripItemPrice) {
        this.tripItemPriceSum -= oldTripItemPrice;
        this.tripItemPriceSum += newTripItemPrice;
    }

    public void updateTripItemPriceSum(Long tripItemPriceSum) {
        this.tripItemPriceSum = tripItemPriceSum;
    }

    public void updateTransportationPriceSum(Integer transportationPriceSum) {
        this.transportationPriceSum = transportationPriceSum;
    }

    public void updateBudget(Long budget) {
        this.budget = budget;
    }
}