package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.Fee;
import cn.edu.sdu.java.server.models.Person;
import cn.edu.sdu.java.server.models.Student;
import cn.edu.sdu.java.server.models.Teacher;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.FeeRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.*;

@Service
public class FeeService {
    private final FeeRepository feeRepository;
    FeeService(FeeRepository feeRepository) {
        this.feeRepository = feeRepository;
    }

    @Transactional
    public DataResponse getFee(@Valid @RequestBody DataRequest dataRequest) {//单条消费记录查询
        String day = dataRequest.getString("day");
        Integer personId = dataRequest.getInteger("personId");
        Optional<Fee> fee = findFeeByPersonIdAndDay(personId,day);
        if(fee.isEmpty()) {
            return CommonMethod.getReturnMessageError("未查询到消费记录!");
        }
        return CommonMethod.getReturnData(fee);
    }
    @Transactional
    public DataResponse getFeeList(DataRequest dataRequest) {//单人，查询此人所有消费记录
        List<Map<String,Object>> datalist;
        Integer personId = dataRequest.getInteger("personId");
        datalist = getFeeMapList(personId);
        return CommonMethod.getReturnData(datalist);
    }
    @Transactional
    public DataResponse getSumFee(DataRequest dataRequest) {//单人，按日期算消费总和;
        Integer personId = dataRequest.getInteger("personId");
        String day = dataRequest.getString("day");
        Double sum = feeRepository.getMoneyByPersonIdAndDate(personId,day);
        return CommonMethod.getReturnData(sum);
    }
    @Transactional
    public DataResponse getLatestFeeRecord() {//查询所有人的消费记录中，最新的一条消费记录
        List<Map<String,Object>> datalist;
        Integer feeId = feeRepository.findMaxId();
        if(feeId == null) {
            return CommonMethod.getReturnMessageError("没有任何人的消费记录");
        }
        datalist = getFeeMapList(feeId);
        return CommonMethod.getReturnData(datalist);
    }

    @Transactional
    public DataResponse addFee(DataRequest dataRequest) {
        Fee fee = new Fee();
        fee.setFeeId(dataRequest.getInteger("feeId"));
        fee.setDay(dataRequest.getString("day"));
        fee.setMoney(dataRequest.getDouble("money"));
        if(fee.getStudent()!=null){fee.setStudent(fee.getStudent());}
        else if(fee.getTeacher()!=null){fee.setTeacher(fee.getTeacher());}
        feeRepository.saveAndFlush(fee);
        return CommonMethod.getReturnMessageOK("添加成功！");
    }

    //实用性存疑，考虑实际，消费应设为不可改
    @Transactional
    public DataResponse updateFee(DataRequest dataRequest){
        Integer feeId = dataRequest.getInteger("feeId");
        Fee fee = new Fee();
        Optional<Fee> optionalFee=feeRepository.findById(feeId);
        if(optionalFee.isPresent()){
            fee.setFeeId(feeId);
        }else{
            return CommonMethod.getReturnMessageError("id为"+feeId+"的消费记录不存在!");
        }
        //考虑是否转换成表，再利用CommonMethod类
        fee.setDay(dataRequest.getString("day"));
        fee.setMoney(dataRequest.getDouble("money"));
        feeRepository.saveAndFlush(fee);
        return CommonMethod.getReturnData(feeId,"id为"+feeId+"的消费记录被更改了!");
    }
    public DataResponse deleteFee(DataRequest dataRequest) {
        Integer feeId = dataRequest.getInteger("feeId");
        Optional<Fee> optionalFee=feeRepository.findById(feeId);
        if(optionalFee.isEmpty()){
            return CommonMethod.getReturnMessageError("该消费记录不存在,无法删除!");
        }
        feeRepository.deleteById(feeId);
        return CommonMethod.getReturnMessageOK("id为"+feeId+"的消费记录被删除了!");
    }

    public Map<String,Object> getMapFromPerson(Student s) {
        Map<String,Object> m = new HashMap<>();
        if(s == null)
            return m;
        Person p = s.getPerson();
        if(p == null)
            return m;
        m.put("personId", s.getPersonId());
        m.put("num",p.getNum());
        m.put("name",p.getName());
        m.put("card",p.getCard());
        return m;
    }
    public Map<String,Object> getMapFromPerson(Teacher t) {
        Map<String,Object> m = new HashMap<>();
        if(t == null)
            return m;
        Person p = t.getPerson();
        if(p == null)
            return m;
        m.put("personId", t.getPersonId());
        m.put("num",p.getNum());
        m.put("name",p.getName());
        m.put("card",p.getCard());
        return m;
    }


    public Optional<Fee> findFeeByPersonIdAndDay(Integer personId, String day) {
        // 先查学生记录，不存在再查教师记录
        return feeRepository.findByStudentPersonIdAndDay(personId, day)
                .or(() -> feeRepository.findByTeacherPersonIdAndDay(personId, day));
    }
    public List<Fee> findFeeByPersonId(Integer personId) {//按List查
        List<Fee> studentFee = feeRepository.findListByStudent(personId);
        List<Fee> teacherFee = feeRepository.findListByTeacher(personId);
        if(studentFee != null){
            return studentFee;
        }
        return teacherFee;
    }
    public List<Map<String,Object>> getFeeMapList(Integer personId){
        List<Map<String,Object>> datalist=new ArrayList<>();
        List<Fee> feeList = findFeeByPersonId(personId);
        if(feeList.isEmpty()){
            return datalist;
        }
        for(Fee f : feeList){
            if(f.getStudent()==null)break;
            Map<String,Object> data = getMapFromPerson(f.getStudent());
            datalist.add(data);
        }
        for(Fee f : feeList){
            if(f.getTeacher()==null)break;
            Map<String,Object> data = getMapFromPerson(f.getTeacher());
            datalist.add(data);
        }
        //查到的人要么是老师要么是学生
        return datalist;
    }


}

