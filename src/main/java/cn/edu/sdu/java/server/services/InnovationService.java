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
        Map<String,Object> form = dataRequest.getMap("form");
        String studentNum = CommonMethod.getString(form, "studentNum");
        String studentName = CommonMethod.getString(form, "studentName");
        String achievement = CommonMethod.getString(form, "achievement");
        Integer teacherId = CommonMethod.getInteger(form, "teacherId");

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

        Innovation i = new Innovation();
        i.setStudentNum(studentNum);
        i.setStudentName(studentName);
        i.setStudent(student);
        i.setAchievement(achievement);

        // 设置辅导老师
        if (teacherId != null && teacherId > 0) {
            Optional<Teacher> teacherOpt = teacherRepository.findById(teacherId);
            if (teacherOpt.isPresent()) {
                Teacher teacher = teacherOpt.get();
                i.setAdvisor(teacher);
                i.setAdvisorName(teacher.getPerson().getName());
            } else {
                i.setAdvisorName("无");
            }
        } else {
            i.setAdvisorName("无");
        }

        innovationRepository.save(i);
        systemService.modifyLog(i, true);
        return CommonMethod.getReturnData(i.getInnovationId());
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
                
                // 创建OptionItem对象，确保label属性不为空
                OptionItem item = new OptionItem();
                item.setId(t.getPersonId());
                item.setValue(t.getPersonId().toString());
                item.setTitle(teacherName); // 设置title属性
                
                // 如果OptionItem类中有setLabel方法，也设置label属性
                try {
                    java.lang.reflect.Method setLabelMethod = item.getClass().getMethod("setLabel", String.class);
                    setLabelMethod.invoke(item, teacherName);
                } catch (Exception e) {
                    System.err.println("无法设置label属性: " + e.getMessage());
                }
                
                itemList.add(item);
            }
        }
        
        return itemList;
    }
}