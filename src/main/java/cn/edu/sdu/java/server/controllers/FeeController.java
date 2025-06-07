package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.services.FeeService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/fee")
public class FeeController {
    private final FeeService feeService;
    FeeController(FeeService feeService) {
        this.feeService = feeService;
    }

    @PostMapping("/getFee")//精确查询某人某日期的消费（单条查询）
    public DataResponse getFee(@RequestBody @Valid DataRequest dataRequest) {
        return feeService.getFee(dataRequest);
    }
    @PostMapping("/getFeeList")//查询某人全部的消费记录，按日期排序（查出一个List）
    public DataResponse getFeeList(@RequestBody @Valid DataRequest dataRequest) {
        return feeService.getFeeList(dataRequest);
    }
    @PostMapping("/getSumFee")//查询某人 某日期开头的 全部日期 的消费总和（结构只是数字，不建立新List）
    public DataResponse getSumFee(@RequestBody @Valid DataRequest dataRequest) {
        return feeService.getSumFee(dataRequest);
    }
    @PostMapping("/getLatestFeeRecord")
    public DataResponse getLatestFeeRecord() {
        return feeService.getLatestFeeRecord();
    }

    @PostMapping("/addFee")
    public DataResponse addFee(@RequestBody @Valid DataRequest dataRequest) {
        return feeService.addFee(dataRequest);
    }

    @PostMapping("/updateFee")//考虑实际情况，消费记录不可改，尽量不用这个方法
    public DataResponse updateFee(@RequestBody @Valid DataRequest dataRequest) {
        return feeService.updateFee(dataRequest);
    }
    @PostMapping("/deleteFee")
    public DataResponse deleteFee(@RequestBody @Valid DataRequest dataRequest) {
        return feeService.deleteFee(dataRequest);
    }
}
