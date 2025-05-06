package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.FamilyMember;
import cn.edu.sdu.java.server.models.Student;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.FamilyMemberRepository;
import cn.edu.sdu.java.server.repositorys.StudentRepository;
import cn.edu.sdu.java.server.util.ComDataUtil;
import cn.edu.sdu.java.server.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class FamilyMemberService {
    @Autowired
    private FamilyMemberRepository familyMemberRepository;
    @Autowired
    private StudentRepository studentRepository;


    public DataResponse getFamilyMemberList(DataRequest dataRequest) {
        List<Map<String,Object>> datalist;
        Integer personId = dataRequest.getInteger("personId");
        String name = dataRequest.getString("name");
        if(name!=null){
//            String name = dataRequest.getString("name");
            datalist = getFamilyMamberMapList(name);
        }
        else
            datalist = getFamilyMamberMapList(personId);
        return CommonMethod.getReturnData(datalist);
    }
    public List<Map<String,Object>> getFamilyMamberMapList(Integer personId){//初始化 刷新
        List<Map<String,Object>> datalist=new ArrayList<>();
        List<FamilyMember> familyMemberList = familyMemberRepository.findByStudentPersonId(personId);
        if(familyMemberList.isEmpty())
            return datalist;
        for(FamilyMember familyMember:familyMemberList){
            Map<String,Object> data=getMapFromFamilyMember(familyMember);
            datalist.add(data);
        }
        return datalist;
    }

    public List<Map<String,Object>> getFamilyMamberMapList(String name){//搜索框搜索名字
        List<Map<String,Object>> datalist=new ArrayList<>();
        List<FamilyMember> familyMemberList = familyMemberRepository.findByName(name);
        if(familyMemberList.isEmpty())
            return datalist;
        for(FamilyMember familyMember:familyMemberList){
            Map<String,Object> data=getMapFromFamilyMember(familyMember);
            datalist.add(data);
        }
        return datalist;
    }

    public Map<String,Object> getMapFromFamilyMember(FamilyMember familyMember){
        Map<String,Object> data=new HashMap<>();
        if(familyMember==null)
            return data;
        data.put("memberId",familyMember.getMemberId());
        data.put("studentPersonId",familyMember.getStudent().getPersonId());
        data.put("studentName",familyMember.getStudent().getPerson().getName());
        data.put("relation",familyMember.getRelation());
        data.put("name",familyMember.getName());
        String gender=familyMember.getGender();
        data.put("gender",familyMember.getGender());
        data.put("genderName", ComDataUtil.getInstance().getDictionaryLabelByValue("XBM", gender));
        data.put("age",familyMember.getAge());
        data.put("unit",familyMember.getUnit());
        return data;
    }

    @Transactional
    public DataResponse addFamilyMember(DataRequest dataRequest) {
        Map<String,Object> data=dataRequest.getMap("form");
        Integer PersonId=CommonMethod.getInteger(data,"personId");
        Optional<Student> optionalStudent=studentRepository.findByPersonId(PersonId);
        Student student=optionalStudent.get();
        String relation=CommonMethod.getString(data,"relation");
        String name=CommonMethod.getString(data,"name");
        String gender=CommonMethod.getString(data,"gender");
        String age=CommonMethod.getString(data,"age");
        String unit=CommonMethod.getString(data,"unit");
        FamilyMember familyMember=new FamilyMember();
        familyMember.setStudent(student);
        familyMember.setRelation(relation);
        familyMember.setName(name);
        familyMember.setGender(gender);
        familyMember.setAge(age);
        familyMember.setUnit(unit);
        familyMemberRepository.saveAndFlush(familyMember);
        return CommonMethod.getReturnMessageOK("添加成功！");
    }

    @Transactional
    public DataResponse editFamilyMember(DataRequest dataRequest) {
        Map<String,Object> data=dataRequest.getMap("form");
        Integer memberId=CommonMethod.getInteger(data,"memberId");
        Optional<FamilyMember> optionalFamilyMember =familyMemberRepository.findByMemberId(memberId);
        if(optionalFamilyMember.isEmpty())
            return CommonMethod.getReturnMessageError("该家庭成员不存在！");
        FamilyMember familyMember=optionalFamilyMember.get();
        Integer PersonId=CommonMethod.getInteger(data,"personId");
        Optional<Student> optionalStudent=studentRepository.findByPersonId(PersonId);
        Student student=optionalStudent.get();
//        String studentName=CommonMethod.getString(data,"studentName");//对应学生姓名
//        List<Student> studentlist=studentRepository.findByPersonName(studentName);
//        if(studentlist.isEmpty())
//            return CommonMethod.getReturnMessageError("家庭成员不能对应一个不存在的学生！");
//        Student student=studentlist.get(0);

        String relation=CommonMethod.getString(data,"relation");
        String name=CommonMethod.getString(data,"name");
        String gender=CommonMethod.getString(data,"gender");
        String age=CommonMethod.getString(data,"age");
        String unit=CommonMethod.getString(data,"unit");
        familyMember.setStudent(student);
        familyMember.setRelation(relation);
        familyMember.setName(name);
        familyMember.setGender(gender);
        familyMember.setAge(age);
        familyMember.setUnit(unit);
        familyMemberRepository.saveAndFlush(familyMember);
        return CommonMethod.getReturnMessageOK("修改成功！");
    }

    public DataResponse deleteFamilyMember(DataRequest dataRequest) {
        Integer memberId=dataRequest.getInteger("memberId");
        Optional<FamilyMember> optionalFamilyMember =familyMemberRepository.findByMemberId(memberId);
        if(optionalFamilyMember.isEmpty())
            return CommonMethod.getReturnMessageError("该家庭成员不存在,无法删除!");
        familyMemberRepository.deleteById(memberId);
        return CommonMethod.getReturnMessageOK("删除成功！");

    }
}
