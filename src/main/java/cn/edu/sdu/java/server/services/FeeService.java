package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.Fee;
import cn.edu.sdu.java.server.models.Pp;
import cn.edu.sdu.java.server.models.Person;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.FeeRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FeeService {
    private final FeeRepository feeRepository;
    FeeService(FeeRepository feeRepository) {
        this.feeRepository = feeRepository;
    }

    @Transactional
    public DataResponse getFee(DataRequest dataRequest) {//单条消费记录查询
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
        Integer personId = dataRequest.getInteger("personId");
        List<Map<String,Object>> datalist = getFeeMapList(personId);
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
        fee.setPerson((Pp)dataRequest.get("person"));
        fee.setDay(dataRequest.getString("day"));
        fee.setMoney(dataRequest.getDouble("money"));
        feeRepository.saveAndFlush(fee);
        return CommonMethod.getReturnMessageOK("添加成功！");
    }

    //实用性存疑，考虑实际，消费应设为不可改，没有改消费Person
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

    public Map<String,Object> getMapFromFee(Fee p) {
        Map<String,Object> m = new HashMap<>();
        if(p == null)
            return m;
        m.put("personId", p.getPerson().getPersonId());
        m.put("money",p.getMoney());
        m.put("day",p.getDay());
        m.put("feeId",p.getFeeId());
        m.put("name",p.getPerson().getName());
        return m;
    }

    public Optional<Fee> findFeeByPersonIdAndDay(Integer personId, String day) {
        return feeRepository.findByPersonPersonIdAndDay(personId,day);
    }
//    public List<Fee> findFeeByPersonId(Integer personId) {//按List查
//        return feeRepository.findListByPerson(personId);
//    }
    public List<Map<String,Object>> getFeeMapList(Integer personId){
        List<Map<String,Object>> datalist=new ArrayList<>();
        List<Fee> feeList = feeRepository.findListByPersonId(personId);
        if(feeList == null || feeList.isEmpty())
            return datalist;
        for (Fee fee : feeList) {
            datalist.add(getMapFromFee(fee));
        }
        return datalist;
    }


}

