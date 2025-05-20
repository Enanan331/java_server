package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.payload.response.OptionItemList;
import cn.edu.sdu.java.server.services.HonorService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins="*",maxAge = 3600)
@RestController
@RequestMapping("/api/honor")
public class HonorController {
    private final HonorService honorService;
    public HonorController(HonorService honorService) {this.honorService = honorService;}

    @PostMapping("/getStudentItemOptionList")
    public OptionItemList getStudentItemOptionList(@Valid @RequestBody DataRequest dataRequest) {
        return honorService.getStudentItemOptionList(dataRequest);
    }
    @PostMapping("/getHonorList")
    public DataResponse getHonorList(@Valid @RequestBody DataRequest dataRequest) {
        return honorService.getHonorList(dataRequest);
    }
    @PostMapping("/honorSave")
    public DataResponse honorSave(@Valid @RequestBody DataRequest dataRequest) {
        return honorService.honorSave(dataRequest);
    }
    @PostMapping("/honorDelete")
    public DataResponse honorDelete(@Valid @RequestBody DataRequest dataRequest) {
        return honorService.honorDelete(dataRequest);
    }
}
