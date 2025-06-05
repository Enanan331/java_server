package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.Person;
import cn.edu.sdu.java.server.models.Student;
import cn.edu.sdu.java.server.models.VolunteerWork;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.PersonRepository;
import cn.edu.sdu.java.server.repositorys.StudentRepository;
import cn.edu.sdu.java.server.repositorys.VolunteerWorkRepository;
import cn.edu.sdu.java.server.util.ComDataUtil;
import cn.edu.sdu.java.server.util.CommonMethod;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class VolunteerWorkService {
    @Autowired
    private VolunteerWorkRepository volunteerWorkRepository;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private StudentRepository studentRepository;

    // 获取志愿者活动列表
    public DataResponse getVolunteerWorkList(DataRequest dataRequest) {
        List<Map<String, Object>> dataList = new ArrayList<>();
        String name = dataRequest.getString("name");  // 可选查询条件
        List<VolunteerWork> list;

        if (name != null && !name.isEmpty()) {
            list = volunteerWorkRepository.findByName(name);
        } else {
            list = volunteerWorkRepository.findAll();
        }

        for (VolunteerWork vw : list) {
            dataList.add(getMapFromVolunteerWork(vw));
        }
        return CommonMethod.getReturnData(dataList);
    }

    // 父窗口实体转Map
    public Map<String, Object> getMapFromVolunteerWork(VolunteerWork vw) {
        Map<String, Object> map = new HashMap<>();
        if (vw == null) return map;

        map.put("volunteerWorkId", vw.getVolunteerWorkId());
        map.put("name", vw.getName());
        map.put("location", vw.getLocation());
        map.put("date", vw.getDate());
        map.put("startTime", vw.getStartTime());
        map.put("endTime", vw.getEndTime());
        map.put("serviceHours", vw.getServiceHours());
        map.put("organizer", vw.getOrganizer());
        return map;
    }

    // 根据ID获取单个活动info
    public DataResponse getVolunteerWorkInfo(DataRequest dataRequest) {
        Integer volunteerWorkId = dataRequest.getInteger("volunteerWorkId");
        Optional<VolunteerWork> op = volunteerWorkRepository.findById(volunteerWorkId);
        if (op.isPresent()) {
            return CommonMethod.getReturnData(getMapFromVolunteerWork(op.get()));
        }
        return CommonMethod.getReturnMessageError("活动不存在");
    }

    // 保存/更新活动
    public DataResponse editVolunteerWork(DataRequest dataRequest) {
        Integer volunteerWorkId = dataRequest.getInteger("volunteerWorkId");
        VolunteerWork vw;

        Optional<VolunteerWork> op = volunteerWorkRepository.findById(volunteerWorkId);
        if (op.isPresent()) {
            vw = op.get();
        } else {
            return CommonMethod.getReturnMessageError("活动不存在");
        }
        Map<String,Object> form=dataRequest.getMap("form");
        // 更新字段
        vw.setName(CommonMethod.getString(form, "name"));
        vw.setLocation(CommonMethod.getString(form, "location"));
        vw.setDate(CommonMethod.getString(form,"date"));
        vw.setStartTime(CommonMethod.getString(form,"startTime"));
        vw.setEndTime(CommonMethod.getString(form,"endTime"));
        vw.setServiceHours(CommonMethod.getDouble(form,"serviceHours"));
        vw.setOrganizer(CommonMethod.getString(form,"organizer"));

        // 处理关联学生（根据实际需求实现）
        // List<Integer> studentIds = dataRequest.getList("studentIds", Integer.class);
        // updateStudents(vw, studentIds);

        volunteerWorkRepository.save(vw);
        return CommonMethod.getReturnMessageOK("修改成功！");
    }

    // 删除活动
    public DataResponse deleteVolunteerWork(DataRequest dataRequest) {
        Integer volunteerWorkId = dataRequest.getInteger("volunteerWorkId");
        Optional<VolunteerWork> optionalVolunteerWork = volunteerWorkRepository.findById(volunteerWorkId);
        if (optionalVolunteerWork.isEmpty())
            return CommonMethod.getReturnMessageError("该志愿服务不存在,无法删除!");
        volunteerWorkRepository.deleteById(volunteerWorkId);
        return CommonMethod.getReturnMessageOK("删除成功！");

    }

    @Transactional
    public DataResponse addVolunteerWork(DataRequest dataRequest) {
        Map<String, Object> data = dataRequest.getMap("form");

        // 获取表单数据
        String name = CommonMethod.getString(data, "name");
        String location = CommonMethod.getString(data, "location");
        String date = CommonMethod.getString(data, "date");
        String startTime = CommonMethod.getString(data, "startTime");
        String endTime = CommonMethod.getString(data, "endTime");
        Double serviceHours = CommonMethod.getDouble(data, "serviceHours");
        String organizer = CommonMethod.getString(data, "organizer");

        // 创建并保存志愿活动
        VolunteerWork volunteerWork = new VolunteerWork();
        volunteerWork.setName(name);
        volunteerWork.setLocation(location);
        volunteerWork.setDate(date);
        volunteerWork.setStartTime(startTime);
        volunteerWork.setEndTime(endTime);
        volunteerWork.setServiceHours(serviceHours);
        volunteerWork.setOrganizer(organizer);

        volunteerWorkRepository.saveAndFlush(volunteerWork);
        return CommonMethod.getReturnMessageOK("志愿活动添加成功！");
    }

    public DataResponse getVolunteerWorkStudents(@Valid DataRequest dataRequest) {
        Integer volunteerWorkId = dataRequest.getInteger("volunteerWorkId");
        Optional<VolunteerWork> op = volunteerWorkRepository.findById(volunteerWorkId);
        List<Map<String, Object>> studentDataList = new ArrayList<>();
        if (op.isPresent()) {
            VolunteerWork vw = op.get();
            // 将 Set 转换为 List
            List<Student> studentList = new ArrayList<>(vw.getStudents());
            for (Student student : studentList) {
                studentDataList.add(getMapFromStudent(student));
            }
        }
        return CommonMethod.getReturnData(studentDataList);
    }

    public Map<String, Object> getMapFromStudent(Student s) {
        Map<String, Object> m = new HashMap<>();
        Person p;
        if (s == null)
            return m;
        m.put("major", s.getMajor());
        m.put("className", s.getClassName());
        p = s.getPerson();
        if (p == null)
            return m;
        m.put("personId", s.getPersonId());
        m.put("num", p.getNum());
        m.put("name", p.getName());
        m.put("dept", p.getDept());
        m.put("card", p.getCard());
        String gender = p.getGender();
        m.put("gender", gender);
        m.put("genderName", ComDataUtil.getInstance().getDictionaryLabelByValue("XBM", gender)); //性别类型的值转换成数据类型名
        m.put("birthday", p.getBirthday());  //时间格式转换字符串
        m.put("email", p.getEmail());
        m.put("phone", p.getPhone());
        m.put("address", p.getAddress());
        m.put("introduce", p.getIntroduce());
        return m;
    }

    public DataResponse deleteVolunteerWorkStudent(@Valid DataRequest dataRequest) {
        Integer personId = dataRequest.getInteger("personId");
        Integer volunteerWorkId = dataRequest.getInteger("volunteerWorkId");
        Optional<VolunteerWork> vw = volunteerWorkRepository.findById(volunteerWorkId);
        if (vw.isPresent()) {
            VolunteerWork volunteerWork = vw.get();
            Optional<Student> studentOpt = studentRepository.findById(personId);
            if (studentOpt.isPresent()) {
                Student student = studentOpt.get();
                // 双向移除
                volunteerWork.getStudents().remove(student);
                student.getVolunteerWorks().remove(volunteerWork);

                volunteerWorkRepository.save(volunteerWork);
                studentRepository.save(student);
            }
        }
        return CommonMethod.getReturnMessageOK("删除成功！");
    }

    public DataResponse addVolunteerWorkStudent(@Valid DataRequest dataRequest) {
        String num = dataRequest.getString("num");
        String name = dataRequest.getString("name");
        Integer volunteerWorkId = dataRequest.getInteger("volunteerWorkId");
        Optional<VolunteerWork> vwOpt = volunteerWorkRepository.findById(volunteerWorkId);
        if (!vwOpt.isPresent()) {
            return CommonMethod.getReturnMessageError("志愿活动不存在！");
        }
        VolunteerWork volunteerWork = vwOpt.get();

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
                if (volunteerWork.getStudents().contains(student)) {
                    return CommonMethod.getReturnMessageError("该学生已报名！");
                }
                if (hasTimeConflict(student, volunteerWork.getDate(), volunteerWork.getStartTime(), volunteerWork.getEndTime())) {
                    return CommonMethod.getReturnMessageError("该学生在同一天同一时间段已有其他志愿活动！");
                }
                // 双向添加
                volunteerWork.getStudents().add(student);
                student.getVolunteerWorks().add(volunteerWork);

                volunteerWorkRepository.save(volunteerWork);
                studentRepository.save(student);
                return CommonMethod.getReturnMessageOK("添加成功！");
            }
        }
        return CommonMethod.getReturnMessageError("该学生不存在！");
    }

    public DataResponse editVolunteerWorkStudent(@Valid DataRequest dataRequest) {
        DataResponse addResponse= addVolunteerWorkStudent(dataRequest);
        if(addResponse.getCode()==0){
            deleteVolunteerWorkStudent(dataRequest);
            return CommonMethod.getReturnMessageOK("修改成功！");
        }
        else{
            return CommonMethod.getReturnMessageError("修改失败！"+addResponse.getMsg());
        }
    }

    public DataResponse getVolunteerWorkStudent(@Valid DataRequest dataRequest) {
        Integer volunteerWorkId = dataRequest.getInteger("volunteerWorkId");
        String numName = dataRequest.getString("numName");
        Optional<VolunteerWork> ovw = volunteerWorkRepository.findById(volunteerWorkId);
        if (ovw.isEmpty()) {
            return CommonMethod.getReturnData(new ArrayList<>()); // 返回空列表而不是错误
        }

        VolunteerWork volunteerWork = ovw.get();
        // 将 Set 转换为 List
        List<Student> studentList = new ArrayList<>(volunteerWork.getStudents());
        List<Student> targetStudent = new ArrayList<>();

        for (Student s : studentList) {
            if(s.getPerson().getNum().equals(numName) ||
                    s.getPerson().getName().equals(numName)) {
                targetStudent.add(s);
            }
        }

        List<Map<String, Object>> studentDataList = new ArrayList<>();
        for(Student ts : targetStudent) {
            studentDataList.add(getMapFromStudent(ts));
        }
        return CommonMethod.getReturnData(studentDataList);
    }

    private boolean hasTimeConflict(Student student, String date, String startTime, String endTime) {
        Set<VolunteerWork> existingWorks = student.getVolunteerWorks();
        if (existingWorks == null || existingWorks.isEmpty()) {
            return false;
        }

        // 将时间转换为分钟数便于比较
        int newStart = convertTimeToMinutes(startTime);
        int newEnd = convertTimeToMinutes(endTime);

        for (VolunteerWork work : existingWorks) {
            // 检查同一天的活动
            if (date.equals(work.getDate())) {
                int existStart = convertTimeToMinutes(work.getStartTime());
                int existEnd = convertTimeToMinutes(work.getEndTime());

                // 检查时间段重叠: (新开始 < 旧结束) && (新结束 > 旧开始)
                if (newStart < existEnd && newEnd > existStart) {
                    return true;
                }
            }
        }
        return false;
    }

    // 时间字符串转分钟数工具方法
    private int convertTimeToMinutes(String time) {
        String[] parts = time.split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
        return hours * 60 + minutes;
    }

}
