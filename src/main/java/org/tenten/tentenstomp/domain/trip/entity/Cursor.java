package org.tenten.tentenstomp.domain.trip.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Cursor {
    private Double x;
    private Double y;

    public void updateXY(Double x, Double y) {
        this.x = x;
        this.y = y;
    }
}
