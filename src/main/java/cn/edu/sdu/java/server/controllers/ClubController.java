package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.services.ClubService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import cn.edu.sdu.java.server.payload.request.DataRequest;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/club")
public class ClubController {
    @Autowired
    private ClubService clubService;

    @PostMapping("/getClubList")
    public DataResponse getClubList(@Valid @RequestBody DataRequest dataRequest) {
        return clubService.getClubList(dataRequest);
    }

    @PostMapping("/addClub")
    public DataResponse addClub(@Valid @RequestBody DataRequest dataRequest) {
        return clubService.addClub(dataRequest);
    }

    @PostMapping("/editClub")
    public DataResponse editClub(@Valid @RequestBody DataRequest dataRequest) {
        return clubService.editClub(dataRequest);
    }

    @PostMapping("/deleteClub")
    public DataResponse deleteClub(@Valid @RequestBody DataRequest dataRequest) {
        return clubService.deleteClub(dataRequest);
    }

    // 添加获取社团成员列表的接口
    @PostMapping("/getClubMemberList")
    public DataResponse getClubMemberList(@Valid @RequestBody DataRequest dataRequest) {
        return clubService.getClubMembers(dataRequest);
    }

    @PostMapping("/deleteClubMember")
    public DataResponse deleteClubMember(@Valid @RequestBody DataRequest dataRequest) {
        return clubService.deleteClubMember(dataRequest);
    }

    @PostMapping("/addClubMember")
    public DataResponse addClubMember(@Valid @RequestBody DataRequest dataRequest) {
        return clubService.addClubMember(dataRequest);
    }

    @PostMapping("/editClubMember")
    public DataResponse editClubMember(@Valid @RequestBody DataRequest dataRequest) {
        return clubService.editClubMember(dataRequest);
    }

    @PostMapping("/getClubMember")
    public DataResponse getClubMember(@Valid @RequestBody DataRequest dataRequest) {
        return clubService.getClubMember(dataRequest);
    }

    @PostMapping("/getTeacherListAll")
    public DataResponse getTeacherListAll() {
        return clubService.getTeacherListAll();
    }

    // 添加获取所有学生列表的接口
    @PostMapping("/getStudentListAll")
    public DataResponse getStudentListAll() {
        return clubService.getStudentListAll();
    }
}