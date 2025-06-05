package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.*;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.*;
import cn.edu.sdu.java.server.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class ClubService {
    @Autowired
    private ClubRepository clubRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private PersonRepository personRepository;

    // 获取社团列表
    public DataResponse getClubList(DataRequest dataRequest) {
        List<Map<String, Object>> dataList = new ArrayList<>();
        String name = dataRequest.getString("name");
        List<Club> list;

        if (name != null && !name.isEmpty()) {
            list = clubRepository.findByName(name);
        } else {
            list = clubRepository.findAll();
        }

        for (Club club : list) {
            dataList.add(getMapFromClub(club));
        }
        return CommonMethod.getReturnData(dataList);
    }

    // Club实体转Map
    public Map<String, Object> getMapFromClub(Club club) {
        Map<String, Object> map = new HashMap<>();
        if (club == null) return map;

        map.put("clubId", club.getClubId());
        map.put("name", club.getName());
        map.put("location", club.getLocation());
        map.put("description", club.getDescription());

        // 处理社长信息
        if (club.getPresident() != null) {
            Student president = club.getPresident();
            map.put("presidentId", president.getPersonId());
            map.put("presidentName", president.getPerson().getName());
            map.put("presidentNum", president.getPerson().getNum());
        } else {
            map.put("presidentId", null);
            map.put("presidentName", "");
            map.put("presidentNum", "");
        }

        // 处理指导老师信息
        if (club.getAdvisor() != null) {
            Teacher advisor = club.getAdvisor();
            map.put("advisorId", advisor.getPersonId());
            map.put("advisorName", advisor.getPerson().getName());
            map.put("advisorNum", advisor.getPerson().getNum());
        } else {
            map.put("advisorId", null);
            map.put("advisorName", "");
            map.put("advisorNum", "");
        }

        return map;
    }

    // 根据ID获取单个社团信息
    public DataResponse getClubInfo(DataRequest dataRequest) {
        Integer clubId = dataRequest.getInteger("clubId");
        Optional<Club> op = clubRepository.findById(clubId);
        if (op.isPresent()) {
            return CommonMethod.getReturnData(getMapFromClub(op.get()));
        }
        return CommonMethod.getReturnMessageError("社团不存在");
    }

    // 保存/更新社团
    @Transactional
    public DataResponse editClub(DataRequest dataRequest) {
        Integer clubId = dataRequest.getInteger("clubId");
        Club club;

        Optional<Club> op = clubRepository.findById(clubId);
        if (op.isPresent()) {
            club = op.get();
        } else {
            return CommonMethod.getReturnMessageError("社团不存在");
        }

        Map<String, Object> form = dataRequest.getMap("form");
        // 更新基本字段
        club.setName(CommonMethod.getString(form, "name"));
        club.setLocation(CommonMethod.getString(form, "location"));
        club.setDescription(CommonMethod.getString(form, "description"));

        // 更新社长
        Integer presidentId = CommonMethod.getInteger(form, "presidentId");
        Student president = null;
        if (presidentId != null) {
            Optional<Student> presidentOpt = studentRepository.findById(presidentId);
            if (presidentOpt.isPresent()) {
                president = presidentOpt.get();
                List<Club> allclubs = clubRepository.findAll();
                for (Club c : allclubs) {
                    if (!c.getClubId().equals(clubId) && c.getPresident() != null && c.getPresident().getPersonId().equals(president.getPersonId())) {
                        return CommonMethod.getReturnMessageError("该学生已经是其他社团的社长！一个学生只能担任一个社团的社长");
                    }
                }

            }
        } else {
            club.setPresident(null);
        }

        // 更新指导老师
        Integer advisorId = CommonMethod.getInteger(form, "advisorId");
        Teacher advisor = null;
        if (advisorId != null) {
            Optional<Teacher> advisorOpt = teacherRepository.findById(advisorId);
            if (advisorOpt.isPresent()) {
                advisor = advisorOpt.get();
                List<Club> allclubs = clubRepository.findAll();
                for (Club c : allclubs) {
                    if (!c.getClubId().equals(clubId) && c.getAdvisor() != null && c.getAdvisor().getPersonId().equals(advisor.getPersonId())) {
                        return CommonMethod.getReturnMessageError("该老师已经是其他社团的指导老师！一个老师只能担任一个社团的指导老师");
                    }
                }
            }
        } else {
            club.setAdvisor(null);
        }

        club.setPresident(president);
        club.setAdvisor(advisor);
        clubRepository.save(club);
        return CommonMethod.getReturnMessageOK("修改成功！");
    }

    // 删除社团
    public DataResponse deleteClub(DataRequest dataRequest) {
        Integer clubId = dataRequest.getInteger("clubId");
        Optional<Club> optionalClub = clubRepository.findById(clubId);
        if (optionalClub.isEmpty())
            return CommonMethod.getReturnMessageError("该社团不存在,无法删除!");
        clubRepository.deleteById(clubId);
        return CommonMethod.getReturnMessageOK("删除成功！");
    }

    // 添加社团
    @Transactional
    public DataResponse addClub(DataRequest dataRequest) {
        Map<String, Object> data = dataRequest.getMap("form");

        // 获取表单数据
        String name = CommonMethod.getString(data, "name");
        String location = CommonMethod.getString(data, "location");
        String description = CommonMethod.getString(data, "description");
        Integer presidentId = CommonMethod.getInteger(data, "presidentId");
        Integer advisorId = CommonMethod.getInteger(data, "advisorId");

        // 创建并保存社团
        Club club = new Club();
        club.setName(name);
        club.setLocation(location);
        club.setDescription(description);

        // 设置社长
        if (presidentId != null) {
            Optional<Student> presidentOpt = studentRepository.findById(presidentId);
            presidentOpt.ifPresent(club::setPresident);
        }

        // 设置指导老师
        if (advisorId != null) {
            Optional<Teacher> advisorOpt = teacherRepository.findById(advisorId);
            advisorOpt.ifPresent(club::setAdvisor);
        }

        clubRepository.save(club);
        return CommonMethod.getReturnMessageOK("社团添加成功！");
    }

    // 获取社团成员列表
    public DataResponse getClubMembers(DataRequest dataRequest) {
        Integer clubId = dataRequest.getInteger("clubId");
        Optional<Club> op = clubRepository.findById(clubId);
        List<Map<String, Object>> memberDataList = new ArrayList<>();
        if (op.isPresent()) {
            Club club = op.get();
            // 将Set转换为List
            List<Student> memberList = new ArrayList<>(club.getMembers());
            for (Student member : memberList) {
                memberDataList.add(getMapFromStudent(member));
            }
        }
        return CommonMethod.getReturnData(memberDataList);
    }

    // Student实体转Map
    public Map<String, Object> getMapFromStudent(Student s) {
        Map<String, Object> m = new HashMap<>();
        Person p;
        if (s == null)
            return m;
        p = s.getPerson();
        if (p == null)
            return m;
        m.put("personId", s.getPersonId());
        m.put("num", p.getNum());
        m.put("name", p.getName());
        m.put("dept", p.getDept());
        m.put("className", s.getClassName());
        m.put("major", s.getMajor());
        return m;
    }

    // 删除社团成员
    @Transactional
    public DataResponse deleteClubMember(DataRequest dataRequest) {
        Integer personId = dataRequest.getInteger("personId");
        Integer clubId = dataRequest.getInteger("clubId");
        Optional<Club> clubOpt = clubRepository.findById(clubId);
        if (clubOpt.isPresent()) {
            Club club = clubOpt.get();
            Student president = club.getPresident();
            if (president != null && president.getPersonId().equals(personId)) {
                return CommonMethod.getReturnMessageError("不能删除直接社长！请先转让社长再删除当前社长！");
            }
            Optional<Student> studentOpt = studentRepository.findById(personId);
            if (studentOpt.isPresent()) {
                Student student = studentOpt.get();
                // 双向移除
                club.getMembers().remove(student);
                student.getClubs().remove(club);

                clubRepository.save(club);
                studentRepository.save(student);
            }
        }
        return CommonMethod.getReturnMessageOK("删除成功！");
    }

    // 添加社团成员
    @Transactional
    public DataResponse addClubMember(DataRequest dataRequest) {
        String num = dataRequest.getString("num");
        String name = dataRequest.getString("name");
        Integer clubId = dataRequest.getInteger("clubId");
        Optional<Club> clubOpt = clubRepository.findById(clubId);
        if (clubOpt.isEmpty()) {
            return CommonMethod.getReturnMessageError("社团不存在！");
        }
        Club club = clubOpt.get();

        Optional<Person> op = personRepository.findByNum(num);
        if (op.isPresent()) {
            Person p = op.get();
            if (!p.getName().equals(name)) {
                return CommonMethod.getReturnMessageError("输入有误，姓名与学号不匹配！");
            }
            Optional<Student> studentOpt = studentRepository.findById(p.getPersonId());
            if (studentOpt.isPresent()) {
                Student student = studentOpt.get();

                // 检查是否已存在关联
                if (club.getMembers().contains(student)) {
                    return CommonMethod.getReturnMessageError("该学生已是社团成员！");
                }

                // 双向添加
                club.getMembers().add(student);
                student.getClubs().add(club);

                clubRepository.save(club);
                studentRepository.save(student);
                return CommonMethod.getReturnMessageOK("添加成功！");
            }
        }
        return CommonMethod.getReturnMessageError("该学生不存在！");
    }

    // 编辑社团成员（先添加后删除）
    @Transactional
    public DataResponse editClubMember(DataRequest dataRequest) {
        DataResponse addResponse = addClubMember(dataRequest);
        if (addResponse.getCode() == 0) {
            return deleteClubMember(dataRequest);
        } else {
            return CommonMethod.getReturnMessageError("修改失败！" + addResponse.getMsg());
        }
    }

    // 查找社团成员
    public DataResponse getClubMember(DataRequest dataRequest) {
        Integer clubId = dataRequest.getInteger("clubId");
        String numName = dataRequest.getString("numName");
        Optional<Club> oClub = clubRepository.findById(clubId);
        if (oClub.isEmpty()) {
            return CommonMethod.getReturnData(new ArrayList<>());
        }

        Club club = oClub.get();
        List<Student> memberList = new ArrayList<>(club.getMembers());
        List<Student> targetMembers = new ArrayList<>();

        for (Student member : memberList) {
            Person p = member.getPerson();
            if (p.getNum().equals(numName) || p.getName().equals(numName)) {
                targetMembers.add(member);
            }
        }

        List<Map<String, Object>> memberDataList = new ArrayList<>();
        for (Student tm : targetMembers) {
            memberDataList.add(getMapFromStudent(tm));
        }
        return CommonMethod.getReturnData(memberDataList);
    }

    public DataResponse getTeacherListAll() {
        List<Teacher> teacherList = teacherRepository.findAll();
        List<Map<String, Object>> dataList = new ArrayList<>();
        for (Teacher t : teacherList) {
            Map<String, Object> m = new HashMap<>();
            Person p = t.getPerson();
            m.put("personId", t.getPersonId());
            m.put("num", p.getNum());
            m.put("name", p.getName());
            dataList.add(m);
        }
        return CommonMethod.getReturnData(dataList);
    }

    public DataResponse getStudentListAll() {
        List<Student> studentList = studentRepository.findAll();
        List<Map<String, Object>> dataList = new ArrayList<>();
        for (Student s : studentList) {
            Map<String, Object> m = new HashMap<>();
            Person p = s.getPerson();
            m.put("personId", s.getPersonId());
            m.put("num", p.getNum());
            m.put("name", p.getName());
            dataList.add(m);
        }
        return CommonMethod.getReturnData(dataList);
    }
}