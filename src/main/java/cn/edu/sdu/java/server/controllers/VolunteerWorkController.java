package cn.edu.sdu.java.server.controllers;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.services.VolunteerWorkService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import cn.edu.sdu.java.server.payload.request.DataRequest;



@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/volunteerWork")
public class VolunteerWorkController {
    @Autowired
    private VolunteerWorkService volunteerWorkService;

    @PostMapping("/getVolunteerWorkList")
    public DataResponse getVolunteerWorkList(@Valid @RequestBody DataRequest dataRequest){
        return volunteerWorkService.getVolunteerWorkList(dataRequest);
    }

    @PostMapping("/addVolunteerWork")
    public DataResponse addVolunteerWork(@Valid @RequestBody DataRequest dataRequest) {
        return volunteerWorkService.addVolunteerWork(dataRequest);
    }

    @PostMapping("/editVolunteerWork")
    public DataResponse editVolunteerWork(@Valid @RequestBody DataRequest dataRequest){
        return volunteerWorkService.editVolunteerWork(dataRequest);
    }

    @PostMapping("/deleteVolunteerWork")
    public DataResponse deleteVolunteerWork(@Valid @RequestBody DataRequest dataRequest){
        return volunteerWorkService.deleteVolunteerWork(dataRequest);
    }

    @PostMapping("/getVolunteerWorkStudents")//初始化时获取学生列表
    public DataResponse getVolunteerWorkStudents(@Valid @RequestBody DataRequest dataRequest) {
        return volunteerWorkService.getVolunteerWorkStudents(dataRequest);
    }
    @PostMapping("/deleteVolunteerWorkStudent")
    public DataResponse deleteVolunteerWorkStudent(@Valid @RequestBody DataRequest dataRequest) {
        return volunteerWorkService.deleteVolunteerWorkStudent(dataRequest);
    }
    @PostMapping("/addVolunteerWorkStudent")
    public DataResponse addVolunteerWorkStudent(@Valid @RequestBody DataRequest dataRequest) {
        return volunteerWorkService.addVolunteerWorkStudent(dataRequest);
    }

    @PostMapping("/editVolunteerWorkStudent")
    public DataResponse editVolunteerWorkStudent(@Valid @RequestBody DataRequest dataRequest) {
        return volunteerWorkService.editVolunteerWorkStudent(dataRequest);
    }
    @PostMapping("/getVolunteerWorkStudent")//真正的查找
    public DataResponse getVolunteerWorkStudent(@Valid @RequestBody DataRequest dataRequest) {
        return volunteerWorkService.getVolunteerWorkStudent(dataRequest);
    }
}