package org.tenten.tentenstomp.global.component.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tenten.tentenstomp.global.common.constant.ResponseConstant;
import org.tenten.tentenstomp.global.component.PathComponent;
import org.tenten.tentenstomp.global.component.dto.request.TempPathCalculateRequest;
import org.tenten.tentenstomp.global.response.GlobalDataResponse;

import static org.tenten.tentenstomp.global.common.constant.ResponseConstant.SUCCESS;

@RestController
@RequiredArgsConstructor
@RequestMapping("/path")
public class PathController {
    private final PathComponent pathComponent;

    @GetMapping("/car")
    public ResponseEntity<?> carPathTest(@RequestBody TempPathCalculateRequest calculateRequest) {

        return ResponseEntity.ok(GlobalDataResponse.ok(SUCCESS, pathComponent.calculatePathByCar(calculateRequest.from(), calculateRequest.to())));
    }

    @GetMapping("/public")
    public ResponseEntity<?> publicPathTest(@RequestBody TempPathCalculateRequest calculateRequest) {
        pathComponent.calculatePathByPublicTransportation(calculateRequest.from(), calculateRequest.to());
        return ResponseEntity.ok("CHECK LOG");
    }
}
