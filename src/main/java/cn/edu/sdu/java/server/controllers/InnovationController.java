package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.payload.response.OptionItem;
import cn.edu.sdu.java.server.services.InnovationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/innovation")
public class InnovationController {
    @Autowired
    private InnovationService innovationService;

    @PostMapping("/getInnovationList")
    public DataResponse getInnovationList(@Valid @RequestBody DataRequest dataRequest) {
        return innovationService.getInnovationList(dataRequest);
    }

    @PostMapping("/getInnovationInfo")
    public DataResponse getInnovationInfo(@Valid @RequestBody DataRequest dataRequest) {
        return innovationService.getInnovationInfo(dataRequest);
    }

    @PostMapping("/innovationSave")
    public DataResponse innovationSave(@Valid @RequestBody DataRequest dataRequest) {
        return innovationService.innovationSave(dataRequest);
    }

    @PostMapping("/getTeacherOptionList")
    public List<OptionItem> getTeacherOptionList(@Valid @RequestBody DataRequest dataRequest) {
        return innovationService.getTeacherOptionList(dataRequest);
    }

    @PostMapping("/innovationDelete")
    public DataResponse innovationDelete(@Valid @RequestBody DataRequest dataRequest) {
        return innovationService.innovationDelete(dataRequest);
    }
}
