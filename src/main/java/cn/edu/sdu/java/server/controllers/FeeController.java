package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.services.FeeService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("api/Fee")
public class FeeController {
    private final FeeService feeService;
    FeeController(FeeService feeService) {
        this.feeService = feeService;
    }

    @GetMapping("/getFee/{id}/{date}")//精确查询某人某日期的消费（单条查询）
    public DataResponse getFee(@PathVariable Integer id, @PathVariable String date) {
        DataRequest dataRequest = new DataRequest();
        dataRequest.add("personId", id);
        dataRequest.add("day", date);
        return feeService.getFee(dataRequest);
    }
    @GetMapping("/getFee/{id}")//查询某人全部的消费记录，按日期排序（查出一个List）
    public DataResponse getFeeList(@PathVariable Integer id) {
        DataRequest dataRequest = new DataRequest();
        dataRequest.add("personId", id);
        return feeService.getFeeList(dataRequest);
    }
    @GetMapping("/getSumFee/{id}/{date}")//查询某人 某日期开头的 全部日期 的消费总和（结构只是数字，不建立新List）
    public DataResponse getSumFee(@PathVariable Integer id, @PathVariable String date) {
        DataRequest dataRequest = new DataRequest();
        dataRequest.add("personId", id);
        return feeService.getSumFee(dataRequest);
    }
    @GetMapping("/getLatestFeeRecord()")
    public DataResponse getLatestFeeRecord() {
        return feeService.getLatestFeeRecord();
    }
    @PostMapping("/addFee")
    public DataResponse addFee(@RequestBody @Valid DataRequest dataRequest) {
        return feeService.addFee(dataRequest);
    }
    @PutMapping("/updateFee")//考虑实际情况，消费记录不可改，尽量不用这个方法
    public DataResponse updateFee(@RequestBody @Valid DataRequest dataRequest) {
        return feeService.updateFee(dataRequest);
    }
    @DeleteMapping("/deleteFee")
    public DataResponse deleteFee(@RequestBody @Valid DataRequest dataRequest) {
        return feeService.deleteFee(dataRequest);
    }
}
