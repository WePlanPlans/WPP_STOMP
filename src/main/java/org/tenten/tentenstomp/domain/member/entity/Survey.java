package org.tenten.tentenstomp.domain.member.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.tenten.tentenstomp.global.converter.JsonConverter;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Survey {
    private String planning;
    private String activeHours;
    private String priority;
    private String accommodation;
    private String food;
    private String tripStyle;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Survey survey = (Survey) o;
        return Objects.equals(planning, survey.planning) && Objects.equals(activeHours, survey.activeHours) && Objects.equals(priority, survey.priority) && Objects.equals(accommodation, survey.accommodation) && Objects.equals(food, survey.food) && Objects.equals(tripStyle, survey.tripStyle);
    }

    @Override
    public int hashCode() {
        return Objects.hash(planning, activeHours, priority, accommodation, food, tripStyle);
    }

    public static class SurveyConverter extends JsonConverter<Survey> {

    }
}