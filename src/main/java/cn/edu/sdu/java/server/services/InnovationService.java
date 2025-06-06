package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.Innovation;
import cn.edu.sdu.java.server.models.Student;
import cn.edu.sdu.java.server.models.Teacher;
import cn.edu.sdu.java.server.repositorys.InnovationRepository;
import cn.edu.sdu.java.server.repositorys.StudentRepository;
import cn.edu.sdu.java.server.repositorys.TeacherRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.payload.response.OptionItem;
import cn.edu.sdu.java.server.payload.response.OptionItemList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@Service
public class InnovationService {
    @Autowired
    private InnovationRepository innovationRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private SystemService systemService;

    private static final Logger log = LoggerFactory.getLogger(InnovationService.class);

    public DataResponse getInnovationList(DataRequest dataRequest) {
        String numName = dataRequest.getString("numName");
        if(numName == null)
            numName = "";
        List<Innovation> iList = innovationRepository.findInnovationListByNumName(numName);
        List<Map<String,Object>> dataList = new ArrayList<>();
        Map<String,Object> m;
        for (Innovation i : iList) {
            m = new HashMap<>();
            m.put("innovationId", i.getInnovationId()+"");
            m.put("studentNum", i.getStudentNum());
            m.put("studentName", i.getStudentName());
            m.put("achievement", i.getAchievement());
            m.put("advisorName", i.getAdvisorName());
            dataList.add(m);
        }
        return CommonMethod.getReturnData(dataList);
    }

    public DataResponse innovationSave(DataRequest dataRequest) {
        Map form = dataRequest.getMap("form");
        Integer innovationId = CommonMethod.getInteger(form, "innovationId");
        Integer studentId = CommonMethod.getInteger(form, "studentId");
        String achievement = CommonMethod.getString(form, "achievement");
        Integer advisorId = CommonMethod.getInteger(form, "advisorId");
        
        Innovation innovation = null;
        Optional<Innovation> op;
        if (innovationId != null) {
            op = innovationRepository.findById(innovationId);
            if (op.isPresent()) {
                innovation = op.get();
            }
        }
        
        if (innovation == null) {
            innovation = new Innovation();
            innovationId = null;
        }
        
        Student student = null;
        if (studentId != null) {
            Optional<Student> ops = studentRepository.findById(studentId);
            if (ops.isPresent()) {
                student = ops.get();
                innovation.setStudent(student);
                // 设置学生学号和姓名
                if (student.getPerson() != null) {
                    innovation.setStudentNum(student.getPerson().getNum());
                    innovation.setStudentName(student.getPerson().getName());
                }
            }
        }
        
        innovation.setAchievement(achievement);
        
        Teacher advisor = null;
        if (advisorId != null) {
            Optional<Teacher> opt = teacherRepository.findById(advisorId);
            if (opt.isPresent()) {
                advisor = opt.get();
                innovation.setAdvisor(advisor);
                // 设置指导教师姓名
                if (advisor.getPerson() != null) {
                    innovation.setAdvisorName(advisor.getPerson().getName());
                }
            }
        }
        
        innovationRepository.save(innovation);
        return CommonMethod.getReturnData(innovation.getInnovationId());
    }

    public DataResponse getInnovationInfo(DataRequest dataRequest) {
        Integer innovationId = dataRequest.getInteger("innovationId");
        Innovation i = null;
        Optional<Innovation> op;
        if(innovationId != null) {
            op = innovationRepository.findById(innovationId);
            if(op.isPresent()){
                i = op.get();
            }
        }
        Map<String,Object> m = new HashMap<>();
        if(i != null) {
            m.put("innovationId", i.getInnovationId()+"");
            m.put("studentNum", i.getStudentNum());
            m.put("studentName", i.getStudentName());
            m.put("achievement", i.getAchievement());
            m.put("teacherId", i.getAdvisor() != null ? i.getAdvisor().getPersonId() : 0);
            m.put("advisorName", i.getAdvisorName());
        }
        return CommonMethod.getReturnData(m);
    }

    public List<OptionItem> getTeacherOptionList(DataRequest dataRequest) {
        List<Teacher> teacherList = teacherRepository.findAll();
        List<OptionItem> itemList = new ArrayList<>();
        
        // 添加"无"选项
        itemList.add(new OptionItem(0, "0", "无"));
        
        for (Teacher t : teacherList) {
            if (t.getPerson() != null) {
                String teacherName = t.getPerson().getName();
                // 确保教师名称不为空
                if (teacherName == null || teacherName.isEmpty()) {
                    teacherName = "未知教师";
                }
                
                // 创建OptionItem对象
                OptionItem item = new OptionItem(t.getPersonId(), t.getPersonId().toString(), teacherName);
                
                // 移除使用反射设置label属性的代码
                
                itemList.add(item);
            }
        }
        
        return itemList;
    }

    public DataResponse innovationDelete(DataRequest dataRequest) {
        Integer innovationId = dataRequest.getInteger("innovationId");
        if (innovationId != null) {
            Optional<Innovation> optionalInnovation = innovationRepository.findById(innovationId);
            if (optionalInnovation.isPresent()) {
                Innovation innovation = optionalInnovation.get();
                innovationRepository.delete(innovation);
                systemService.modifyLog(innovation, false);
                return CommonMethod.getReturnMessageOK();
            }
        }
        return CommonMethod.getReturnMessageError("未找到要删除的创新成果");
    }

    /**
     * 更新学生信息变更
     * 当学生信息在学生管理中被修改时，同步更新创新成果中的学生信息
     * @param student 更新后的学生对象
     */
    @Transactional
    public void updateStudentInfo(Student student) {
        if (student == null || student.getPerson() == null) {
            log.warn("updateStudentInfo: student or student.person is null");
            return;
        }
        
        String studentNum = student.getPerson().getNum();
        String studentName = student.getPerson().getName();
        Integer personId = student.getPersonId();
        
        log.info("Updating innovation records for student: ID={}, Num={}, Name={}", 
                 personId, studentNum, studentName);
        
        // 查找该学生的所有创新成果记录
        List<Innovation> innovations = innovationRepository.findByStudentPersonId(personId);
        
        log.info("Found {} innovation records to update", innovations.size());
        
        // 更新每条记录中的学生姓名
        for (Innovation innovation : innovations) {
            log.info("Updating innovation ID={}: changing studentNum from {} to {}, studentName from {} to {}", 
                     innovation.getInnovationId(), innovation.getStudentNum(), studentNum, 
                     innovation.getStudentName(), studentName);
            
            innovation.setStudentNum(studentNum);
            innovation.setStudentName(studentName);
            innovationRepository.save(innovation);
        }
        
        log.info("Completed updating innovation records for student ID={}", personId);
    }
}
