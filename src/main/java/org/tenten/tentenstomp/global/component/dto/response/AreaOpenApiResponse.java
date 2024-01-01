package org.tenten.tentenstomp.global.component.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public record AreaOpenApiResponse(Response response) {

    public record Response(Header header, Body body) {
    }

    public record Header(String resultCode, String resultMsg) {
    }

    public record Body(Item items, Integer numOfRows, Integer pageNo, Integer totalCount) {
    }

    public record Item(List<Area> item) {
    }

    public record Area(Integer rnum, String code, String name) {
    }


}