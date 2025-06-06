package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.Competition;
import cn.edu.sdu.java.server.models.Student;
import cn.edu.sdu.java.server.repositorys.CompetitionRepository;
import cn.edu.sdu.java.server.repositorys.StudentRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.payload.response.OptionItem;
import cn.edu.sdu.java.server.payload.response.OptionItemList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class CompetitionService {
    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private SystemService systemService;

    @Autowired
    private StudentRepository studentRepository;

    public DataResponse getCompetitionList(DataRequest dataRequest) {
        String numName = dataRequest.getString("numName");
        if(numName == null)
            numName = "";
        List<Competition> cList = competitionRepository.findCompetitionListByNumName(numName);
        List<Map<String,Object>> dataList = new ArrayList<>();
        Map<String,Object> m;
        for (Competition c : cList) {
            m = new HashMap<>();
            m.put("competitionId", c.getCompetitionId()+"");
            m.put("studentNum", c.getStudentNum());
            m.put("studentName", c.getStudentName());
            m.put("subject", c.getSubject());
            m.put("result", c.getResult());
            m.put("competitionTime", c.getCompetitionTime());
            dataList.add(m);
        }
        return CommonMethod.getReturnData(dataList);
    }

    public DataResponse competitionSave(DataRequest dataRequest) {
        Map<String,Object> form = dataRequest.getMap("form");
        String studentNum = CommonMethod.getString(form, "studentNum");
        String studentName = CommonMethod.getString(form, "studentName");
        String subject = CommonMethod.getString(form, "subject");
        String result = CommonMethod.getString(form, "result");
        String competitionTime = CommonMethod.getString(form, "competitionTime");

        // 通过学号查找学生
        Optional<Student> studentOpt = studentRepository.findByPersonNum(studentNum);
        if (!studentOpt.isPresent()) {
            return CommonMethod.getReturnMessageError("该学生不存在，请先在学生管理中添加");
        }
        
        Student student = studentOpt.get();
        // 验证学生姓名是否匹配
        if (!student.getPerson().getName().equals(studentName)) {
            return CommonMethod.getReturnMessageError("学号与姓名不匹配");
        }
        
        Competition c = new Competition();
        c.setStudentNum(studentNum);
        c.setStudentName(studentName);
        c.setStudent(student);
        c.setSubject(subject);
        c.setResult(result);
        c.setCompetitionTime(competitionTime);

        competitionRepository.save(c);
        systemService.modifyLog(c, true);
        return CommonMethod.getReturnData(c.getCompetitionId());
    }

    @Transactional
    public DataResponse competitionDelete(DataRequest dataRequest) {
        Integer competitionId = dataRequest.getInteger("competitionId");
        if (competitionId != null) {
            competitionRepository.findById(competitionId).ifPresent(competition -> {
                competitionRepository.delete(competition);
            });
        }
        return CommonMethod.getReturnMessageOK();
    }

    public DataResponse getCompetitionInfo(DataRequest dataRequest) {
        Integer competitionId = dataRequest.getInteger("competitionId");
        Competition c = null;
        Optional<Competition> op;
        if(competitionId != null) {
            op = competitionRepository.findById(competitionId);
            if(op.isPresent()){
                c = op.get();
            }
        }
        Map<String,Object> m = new HashMap<>();
        if(c != null) {
            m.put("competitionId", c.getCompetitionId()+"");
            m.put("studentNum", c.getStudentNum());
            m.put("studentName", c.getStudentName());
            m.put("subject", c.getSubject());
            m.put("result", c.getResult());
            m.put("competitionTime", c.getCompetitionTime());
        }
        return CommonMethod.getReturnData(m);
    }

    /**
     * 更新学生信息变更
     * 当学生信息在学生管理中被修改时，同步更新学科竞赛中的学生信息
     * @param student 更新后的学生对象
     */
    @Transactional
    public void updateStudentInfo(Student student) {
        if (student == null || student.getPerson() == null) {
            // 如果学生对象或学生的人员信息为空，记录警告并返回
            System.out.println("updateStudentInfo: student or student.person is null");
            return;
        }
        
        String studentNum = student.getPerson().getNum();
        String studentName = student.getPerson().getName();
        Integer personId = student.getPersonId();
        
        System.out.println("Updating competition records for student: ID=" + personId + 
                          ", Num=" + studentNum + ", Name=" + studentName);
        
        // 查找该学生的所有学科竞赛记录
        List<Competition> competitions = competitionRepository.findByStudentPersonId(personId);
        
        System.out.println("Found " + competitions.size() + " competition records to update");
        
        // 更新每条记录中的学生学号和姓名
        for (Competition competition : competitions) {
            System.out.println("Updating competition ID=" + competition.getCompetitionId() + 
                              ": changing studentNum from " + competition.getStudentNum() + 
                              " to " + studentNum + ", studentName from " + 
                              competition.getStudentName() + " to " + studentName);
            
            competition.setStudentNum(studentNum);
            competition.setStudentName(studentName);
            competitionRepository.save(competition);
        }
        
        System.out.println("Completed updating competition records for student ID=" + personId);
    }
}
