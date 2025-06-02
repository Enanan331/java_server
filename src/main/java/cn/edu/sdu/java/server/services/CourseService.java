package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.Course;
import cn.edu.sdu.java.server.models.Homework;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.payload.response.OptionItem;
import cn.edu.sdu.java.server.payload.response.OptionItemList;
import cn.edu.sdu.java.server.repositorys.CourseRepository;
import cn.edu.sdu.java.server.repositorys.HomeworkRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class CourseService {
    private final CourseRepository courseRepository;
    private final HomeworkRepository homeworkRepository;
    private final SystemService systemService;

    public CourseService(CourseRepository courseRepository, HomeworkRepository homeworkRepository, SystemService systemService) {
        this.courseRepository = courseRepository;
        this.homeworkRepository = homeworkRepository;
        this.systemService = systemService;
    }
    public Map<String,Object> getMapFromCourse(Course c){
        Map<String,Object> m = new HashMap<>();
        Course pc;
        if(c==null)
            return m;
        m.put("courseId",c.getCourseId());
        m.put("num",c.getNum());
        m.put("name",c.getName());
        m.put("credit",c.getCredit()+"");
        m.put("selectNum",c.getSelectNum());
        m.put("preCourse",c.getPreCourse());
        m.put("attendenceNum",c.getAttendenceNum());
        m.put("textbooks",c.getTextbooks());
        m.put("coursePath",c.getCoursePath());
        pc =c.getPreCourse();
        if(pc != null) {
            m.put("preCourse",pc.getName());
            m.put("preCourseId",pc.getCourseId());
        }
        return m;
    }

    public DataResponse getCourseList(DataRequest dataRequest) {
        String numName = dataRequest.getString("numName");
        if(numName == null)
            numName = "";
        List<Course> cList = courseRepository.findCourseListByNumName(numName);  //数据库查询操作
        List<Map<String,Object>> dataList = new ArrayList<>();
        Map<String,Object> m;
        Course pc;
        for (Course c : cList) {
            m = new HashMap<>();
            m.put("courseId", c.getCourseId()+"");
            m.put("num",c.getNum());
            m.put("name",c.getName());
            m.put("credit",c.getCredit()+"");
            m.put("selectNum",c.getSelectNum());
            m.put("attendenceNum",c.getAttendenceNum());
            m.put("textbooks",c.getTextbooks());
            m.put("coursePath",c.getCoursePath());
            pc =c.getPreCourse();
            if(pc != null) {
                m.put("preCourse",pc.getName());
                m.put("preCourseId",pc.getCourseId());
            }
            dataList.add(m);
        }
        return CommonMethod.getReturnData(dataList);
    }
    public DataResponse courseEdit(DataRequest dataRequest) {
        Map<String,Object> form = dataRequest.getMap("form");
        String num=CommonMethod.getString(form,"num");
        String selectNum = CommonMethod.getString(form,"selectNum");
        String attendenceNum = CommonMethod.getString(form,"attendenceNum");
        String credit = CommonMethod.getString(form,"credit");
        Course c=null;
        Optional<Course> op;
        boolean isNem=false;
        if(num != null) {
            op = courseRepository.findByNum(num);
            if(op.isPresent()) {
                c = op.get();
            }
        }
        if(CommonMethod.getList(form,"textbooks")!=null) {
            List<String> textbooks = (List<String>) CommonMethod.getList(form, "textbooks");
            c.setTextbooks(textbooks);
        }
        c.setCredit(credit);
        c.setAttendenceNum(attendenceNum);
        c.setSelectNum(selectNum);
        courseRepository.save(c);
        System.out.println("OK");
        return CommonMethod.getReturnMessageOK();
    }
    public DataResponse courseSave(DataRequest dataRequest) {
         Map<String,Object> form=dataRequest.getMap("form");
         String num=CommonMethod.getString(form,"num");
         String name=CommonMethod.getString(form,"name");
         Integer preCourseId=CommonMethod.getInteger(form,"preCourseId");
         Course c = null;
         Optional<Course> op;
         boolean isNew = false;
//         if(courseId!=null){
//             op=courseRepository.findById(courseId);
//             if(op.isPresent()) {
//                 c=op.get();
//             }
//         }
        Optional<Course> existingCourse = courseRepository.findByNum(num);
        if (existingCourse.isPresent() &&
                (c== null || !c.getCourseId().equals(num))) {
            return CommonMethod.getReturnMessageError("课序号已存在");
        }
        if (existingCourse.isPresent() &&
                (c== null || !c.getCourseId().equals(name))) {
            return CommonMethod.getReturnMessageError("课程名已存在");
        }
         if(c==null) {
             c = new Course();
             c.setNum(num);
//             c.setCourseId(courseId);
             courseRepository.saveAndFlush(c);
             isNew = true;
         }
         c.setName(CommonMethod.getString(form,"name"));
         if(preCourseId != null) {
             c.setPreCourse(courseRepository.findById(preCourseId).get());
         }
        courseRepository.save(c);
        systemService.modifyLog(c,isNew);
        return CommonMethod.getReturnData(c.getCourseId());
    }
    @Transactional
    public DataResponse courseDelete(DataRequest dataRequest) {
        Integer courseId = dataRequest.getInteger("courseId");
        Optional<Course> op;
        Course c= null;
        if (courseId != null) {
            courseRepository.findById(courseId).ifPresent(course -> {
                // 1. 找到所有将此课程作为preCourse的课程
                List<Course> dependentCourses = courseRepository.findByPreCourse(course);

                // 2. 清除这些课程的preCourse引用
                dependentCourses.forEach(dependent -> {
                    dependent.setPreCourse(null);
                    courseRepository.save(dependent);
                });

                // 3. 删除作业和课程本身
                homeworkRepository.deleteAll(course.getHomework());
                courseRepository.delete(course);
            });
        }
        return CommonMethod.getReturnMessageOK();
    }
    public DataResponse getCourseInfo(DataRequest dataRequest) {
        Integer courseId = dataRequest.getInteger("courseId");
        Course c= null;
        Optional<Course> op;
        if(courseId != null) {
            op = courseRepository.findById(courseId);
            if(op.isPresent()){
                c = op.get();
            }
        }
        return CommonMethod.getReturnData(getMapFromCourse(c));
    }

    public OptionItemList getCourseItemOptionList(DataRequest dataRequest) {
        List<Course> cList = courseRepository.findCourseListByNumName("");
        List<OptionItem> itemList = new ArrayList<>();
        for (Course c : cList) {
            itemList.add(new OptionItem(c.getCourseId(),c.getCourseId()+"",c.getName()));
        }
        return new OptionItemList(0,itemList);
    }


}
