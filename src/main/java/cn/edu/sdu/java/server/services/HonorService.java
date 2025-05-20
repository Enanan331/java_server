package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.Honor;
import cn.edu.sdu.java.server.models.Student;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.payload.response.OptionItem;
import cn.edu.sdu.java.server.payload.response.OptionItemList;
import cn.edu.sdu.java.server.repositorys.HonorRepository;
import cn.edu.sdu.java.server.repositorys.StudentRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import org.springframework.stereotype.Service;

import java.util.*;
@Service
public class HonorService {
    private final StudentRepository studentRepository;
    private final HonorRepository honorRepository;

    public HonorService(StudentRepository studentRepository, HonorRepository honorRepository) {
        this.studentRepository = studentRepository;
        this.honorRepository = honorRepository;
    }
    public OptionItemList getStudentItemOptionList( DataRequest dataRequest) {
        List<Student> sList = studentRepository.findStudentListByNumName("");  //数据库查询操作
        List<OptionItem> itemList = new ArrayList<>();
        for (Student s : sList) {
            itemList.add(new OptionItem( s.getPersonId(),s.getPersonId()+"", s.getPerson().getNum()+"-"+s.getPerson().getName()));
        }
        return new OptionItemList(0, itemList);
    }
    public DataResponse getHonorList( DataRequest dataRequest) {
        Integer personId=dataRequest.getInteger("personId");
        if(personId==null)
            personId=0;
        List<Honor> hList = honorRepository.findByStudentId(personId);
        List<Map<String,Object>> dataList = new ArrayList<>();
        Map<String,Object> m;
        for (Honor h : hList) {
            m = new HashMap<>();
            m.put("honorId",h.getHonorId()+"");
            m.put("personId",h.getStudent().getPersonId());
            m.put("studentName",h.getStudent().getPerson().getName());
            m.put("studentNum",h.getStudent().getPerson().getNum());
            m.put("className",h.getStudent().getClassName());
            m.put("honorName",h.getHonorName());
            m.put("mark",h.getMark());
            dataList.add(m);
        }
        return CommonMethod.getReturnData(dataList);
    }
    public DataResponse honorSave(DataRequest dataRequest) {
        Integer personId=dataRequest.getInteger("personId");
        Integer mark=dataRequest.getInteger("mark");
        String honorName=dataRequest.getString("honorName");
        Integer honorId=dataRequest.getInteger("honorId");
        Optional<Honor> op;
        Honor h=null;
        if(honorId!=null){
            op = honorRepository.findById(honorId);
            if(op.isPresent()){
                h=op.get();
            }
        }
        if(h==null){
            h=new Honor();
            h.setStudent(studentRepository.findById(personId).get());
        }
        h.setMark(mark);
        h.setHonorName(honorName);
        honorRepository.save(h);
        return CommonMethod.getReturnMessageOK();
    }
    public DataResponse honorDelete(DataRequest dataRequest) {
        Integer honorId=dataRequest.getInteger("honorId");
        Optional<Honor> op;
        Honor h=null;
        if(honorId!=null){
            op = honorRepository.findById(honorId);
            if(op.isPresent()){
                h=op.get();
                honorRepository.delete(h);
            }
        }
        return CommonMethod.getReturnMessageOK();
    }
}
