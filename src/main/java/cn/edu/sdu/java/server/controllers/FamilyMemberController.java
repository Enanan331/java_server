package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.services.FamilyMemberService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/family_member")

public class FamilyMemberController {
    @Autowired
    private FamilyMemberService familyMemberService;

    @PostMapping("/getFamilyMemberList")
    public DataResponse getFamilyMemberList(@Valid @RequestBody DataRequest dataRequest){
        return familyMemberService.getFamilyMemberList(dataRequest);
    }

    @PostMapping("/addFamilyMember")
    public DataResponse addFamilyMember(@Valid @RequestBody DataRequest dataRequest){
        return familyMemberService.addFamilyMember(dataRequest);
    }

    @PostMapping("/editFamilyMember")
    public DataResponse editFamilyMember(@Valid @RequestBody DataRequest dataRequest){
        return familyMemberService.editFamilyMember(dataRequest);
    }

    @PostMapping("/deleteFamilyMember")
    public DataResponse deleteFamilyMember(@Valid @RequestBody DataRequest dataRequest){
        return familyMemberService.deleteFamilyMember(dataRequest);
    }
}
