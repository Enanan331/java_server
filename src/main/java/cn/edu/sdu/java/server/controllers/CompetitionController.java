package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.services.CompetitionService;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/competition")
public class CompetitionController {
    @Autowired
    private CompetitionService competitionService;

    @PostMapping("/getCompetitionList")
    public DataResponse getCompetitionList(@Valid @RequestBody DataRequest dataRequest) {
        return competitionService.getCompetitionList(dataRequest);
    }

    @PostMapping("/getCompetitionInfo")
    public DataResponse getCompetitionInfo(@Valid @RequestBody DataRequest dataRequest) {
        return competitionService.getCompetitionInfo(dataRequest);
    }

    @PostMapping("/competitionSave")
    public DataResponse competitionSave(@Valid @RequestBody DataRequest dataRequest) {
        return competitionService.competitionSave(dataRequest);
    }

    @PostMapping("/competitionDelete")
    public DataResponse competitionDelete(@Valid @RequestBody DataRequest dataRequest) {
        return competitionService.competitionDelete(dataRequest);
    }
}
