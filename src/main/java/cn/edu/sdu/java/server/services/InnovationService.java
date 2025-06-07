package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.Innovation;
import cn.edu.sdu.java.server.models.Person;
import cn.edu.sdu.java.server.models.Student;
import cn.edu.sdu.java.server.models.Teacher;
import cn.edu.sdu.java.server.repositorys.InnovationRepository;
import cn.edu.sdu.java.server.repositorys.PersonRepository;
import cn.edu.sdu.java.server.repositorys.StudentRepository;
import cn.edu.sdu.java.server.repositorys.TeacherRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.payload.response.OptionItem;
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
    private PersonRepository personRepository;

    @Autowired
    private SystemService systemService;

    private static final Logger log = LoggerFactory.getLogger(InnovationService.class);

    public DataResponse getInnovationList(DataRequest dataRequest) {
        String numName = dataRequest.getString("numName");
        if(numName == null)
            numName = "";
        
        // 添加调试日志
        System.out.println("查询创新成果列表，参数numName=" + numName);
        
        // 调用仓库方法获取数据
        List<Innovation> iList = innovationRepository.findInnovationListByNumName(numName);
        
        // 打印查询结果数量
        System.out.println("数据库查询返回 " + (iList != null ? iList.size() : 0) + " 条记录");
        
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
        
        // 打印转换后的数据列表数量
        System.out.println("返回前端 " + dataList.size() + " 条记录");
        
        return CommonMethod.getReturnData(dataList);
    }

    public DataResponse innovationSave(DataRequest dataRequest) {
        Map form = dataRequest.getMap("form");
        String studentNum = CommonMethod.getString(form, "studentNum");
        String studentName = CommonMethod.getString(form, "studentName");
        String achievement = CommonMethod.getString(form, "achievement");
        Integer teacherId = CommonMethod.getInteger(form, "teacherId");
        Integer innovationId = CommonMethod.getInteger(form, "innovationId");
        
        // 添加调试日志
        log.info("保存创新成果: ID={}, 学号={}, 姓名={}, 成果={}, 教师ID={}", 
                innovationId, studentNum, studentName, achievement, teacherId);
        
        // 验证学生是否存在于学生管理系统中
        Optional<Student> studentOpt = studentRepository.findByPersonNum(studentNum);
        if (!studentOpt.isPresent()) {
            log.warn("学号为 {} 的学生不存在于学生管理系统中", studentNum);
            return CommonMethod.getReturnMessageError("该学生不存在于学生管理系统中，请先在学生管理中添加");
        }
        
        Student student = studentOpt.get();
        
        // 验证学生姓名是否匹配
        if (!student.getPerson().getName().equals(studentName)) {
            log.warn("学号 {} 对应的学生姓名为 {}，与输入的姓名 {} 不匹配", 
                    studentNum, student.getPerson().getName(), studentName);
            return CommonMethod.getReturnMessageError("学号与姓名不匹配，请检查输入");
        }
        
        Innovation innovation;
        
        // 如果是更新现有记录
        if (innovationId != null && innovationId > 0) {
            Optional<Innovation> innovationOpt = innovationRepository.findById(innovationId);
            if (innovationOpt.isPresent()) {
                innovation = innovationOpt.get();
                log.info("更新现有创新成果记录: ID={}", innovationId);
            } else {
                innovation = new Innovation();
                log.info("创建新的创新成果记录，指定ID={}", innovationId);
                innovation.setInnovationId(innovationId);
            }
        } else {
            innovation = new Innovation();
            log.info("创建新的创新成果记录");
        }
        
        innovation.setStudentNum(studentNum);
        innovation.setStudentName(studentName);
        innovation.setAchievement(achievement);
        
        // 设置学生关联 - 确保这一步正确执行
        innovation.setStudent(student);
        
        // 设置指导教师
        if (teacherId != null && teacherId > 0) {
            Optional<Teacher> teacherOpt = teacherRepository.findById(teacherId);
            if (teacherOpt.isPresent()) {
                Teacher teacher = teacherOpt.get();
                innovation.setAdvisor(teacher);
                innovation.setAdvisorName(teacher.getPerson().getName());
                log.info("设置指导教师: ID={}, 姓名={}", teacherId, teacher.getPerson().getName());
            } else {
                log.warn("未找到ID为 {} 的教师", teacherId);
            }
        } else {
            innovation.setAdvisor(null);
            innovation.setAdvisorName("无");
            log.info("未提供教师ID或ID无效，设置为无指导教师");
        }
        
        // 保存记录
        innovation = innovationRepository.save(innovation);
        
        // 记录系统修改日志
        systemService.modifyLog(innovation, innovationId == null);
        
        // 打印保存后的ID
        log.info("创新成果保存成功，ID={}", innovation.getInnovationId());
        
        // 返回新创建的记录ID
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
        List<Innovation> innovations = innovationRepository.findByStudentNum(studentNum);
        
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

    /**
     * 直接通过ID查询创新成果
     */
    public DataResponse getInnovationById(DataRequest dataRequest) {
        Integer innovationId = dataRequest.getInteger("innovationId");
        log.info("通过ID查询创新成果: ID={}", innovationId);
        
        if (innovationId == null) {
            log.warn("查询ID为空");
            return CommonMethod.getReturnMessageError("查询ID不能为空");
        }
        
        Optional<Innovation> optInnovation = innovationRepository.findById(innovationId);
        
        if (optInnovation.isPresent()) {
            Innovation innovation = optInnovation.get();
            log.info("找到创新成果: ID={}, 学号={}, 姓名={}, 成果={}, 指导教师={}",
                    innovation.getInnovationId(), innovation.getStudentNum(), 
                    innovation.getStudentName(), innovation.getAchievement(), 
                    innovation.getAdvisorName());
            
            Map<String, Object> data = new HashMap<>();
            data.put("innovationId", innovation.getInnovationId()+"");
            data.put("studentNum", innovation.getStudentNum());
            data.put("studentName", innovation.getStudentName());
            data.put("achievement", innovation.getAchievement());
            data.put("advisorName", innovation.getAdvisorName());
            
            return CommonMethod.getReturnData(data);
        } else {
            log.warn("未找到ID为{}的创新成果", innovationId);
            return CommonMethod.getReturnMessageError("未找到指定ID的创新成果");
        }
    }
}
