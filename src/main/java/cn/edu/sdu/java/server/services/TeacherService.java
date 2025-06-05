package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.*;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.*;
import cn.edu.sdu.java.server.util.ComDataUtil;
import cn.edu.sdu.java.server.util.CommonMethod;
import cn.edu.sdu.java.server.util.DateTimeTool;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.List;

@Service
public class TeacherService {
    private static final Logger log = LoggerFactory.getLogger(TeacherService.class);
    private final PersonRepository personRepository;  //人员数据操作自动注入
    private final TeacherRepository teacherRepository;  //教师数据操作自动注入
    private final UserRepository userRepository;  //教师数据操作自动注入
    private final UserTypeRepository userTypeRepository; //用户类型数据操作自动注入
    private final PasswordEncoder encoder;  //密码服务自动注入
//    private final FeeRepository feeRepository;  //消费数据操作自动注入
    private final FamilyMemberRepository familyMemberRepository;
    private final SystemService systemService;
    public TeacherService(PersonRepository personRepository, TeacherRepository teacherRepository, UserRepository userRepository, UserTypeRepository userTypeRepository, PasswordEncoder encoder, FamilyMemberRepository familyMemberRepository, SystemService systemService) {
        this.personRepository = personRepository;
        this.teacherRepository = teacherRepository;
        this.userRepository = userRepository;
        this.userTypeRepository = userTypeRepository;
        this.encoder = encoder;
//        this.feeRepository = feeRepository;
        this.familyMemberRepository = familyMemberRepository;
        this.systemService = systemService;
    }

    public Map<String,Object> getMapFromTeacher(Teacher t) {
        Map<String,Object> m = new HashMap<>();
        Person p;
        if(t == null)
            return m;
        m.put("title",t.getTitle());
        m.put("degree",t.getDegree());
        p = t.getPerson();
        if(p == null)
            return m;
        m.put("personId", t.getPersonId());
        m.put("num",p.getNum());
        m.put("name",p.getName());
        m.put("dept",p.getDept());
        m.put("card",p.getCard());
        String gender = p.getGender();
        m.put("gender",gender);
        m.put("genderName", ComDataUtil.getInstance().getDictionaryLabelByValue("XBM", gender)); //性别类型的值转换成数据类型名
        m.put("birthday", p.getBirthday());  //时间格式转换字符串
        m.put("email",p.getEmail());
        m.put("phone",p.getPhone());
        m.put("address",p.getAddress());
        m.put("introduce",p.getIntroduce());
        return m;
    }

    public List<Map<String,Object>> getTeacherMapList(String numName) {
        List<Map<String,Object>> dataList = new ArrayList<>();
        List<Teacher> tList = teacherRepository.findTeacherListByNumName(numName);  //数据库查询操作
        if (tList == null || tList.isEmpty())
            return dataList;
        for (Teacher teacher : tList) {
            dataList.add(getMapFromTeacher(teacher));
        }
        return dataList;
    }

    public DataResponse getTeacherList(DataRequest dataRequest) {
        String numName = dataRequest.getString("numName");
        List<Map<String,Object>> dataList = getTeacherMapList(numName);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    public DataResponse teacherDelete(DataRequest dataRequest) {
        Integer personId = dataRequest.getInteger("personId");  //获取teacher_id值
        Teacher t = null;
        Optional<Teacher> op;
        if (personId != null && personId > 0) {
            op = teacherRepository.findById(personId);   //查询获得实体对象
            if(op.isPresent()) {
                t = op.get();
                Optional<User> uOp = userRepository.findById(personId); //查询对应该教师的账户
                //删除对应该教师的账户
                uOp.ifPresent(userRepository::delete);
                Person p = t.getPerson();
                teacherRepository.delete(t);    //首先数据库永久删除教师信息
                personRepository.delete(p);   // 然后数据库永久删除教师信息
            }
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }

    public DataResponse getTeacherInfo(DataRequest dataRequest) {
        Integer personId = dataRequest.getInteger("personId");
        Teacher t = null;
        Optional<Teacher> op;
        if (personId != null) {
            op = teacherRepository.findById(personId); //根据教师主键从数据库查询教师的信息
            if (op.isPresent()) {
                t = op.get();
            }
        }
        return CommonMethod.getReturnData(getMapFromTeacher(t)); //这里回传包含教师信息的Map对象
    }

    public DataResponse teacherEditSave(DataRequest dataRequest) {
        Integer personId = dataRequest.getInteger("personId");
        Map<String,Object> form = dataRequest.getMap("form"); //参数获取Map对象
        String num = CommonMethod.getString(form, "num");  //Map 获取属性的值
        Teacher t = null;
        Person p;
        User u;
        Optional<Teacher> op;
        boolean isNew = false;
        if (personId != null) {
            op = teacherRepository.findById(personId);  //查询对应数据库中主键为id的值的实体对象
            if (op.isPresent()) {
                t = op.get();
            }
        }
        Optional<Person> nOp = personRepository.findByNum(num); //查询是否存在num的人员
        if (nOp.isPresent()) {
            if (t == null || !t.getPerson().getNum().equals(num)) {
                return CommonMethod.getReturnMessageError("新工号已经存在，不能添加或修改！");
            }
        }
        if (t == null) {
            p = new Person();
            p.setNum(num);
            p.setType("2");
            personRepository.saveAndFlush(p);  //插入新的Person记录
            personId = p.getPersonId();
            String password = encoder.encode("123456");
            u = new User();
            u.setPersonId(personId);
            u.setUserName(num);
            u.setPassword(password);
            u.setUserType(userTypeRepository.findByName(EUserType.ROLE_TEACHER.name()));
            u.setCreateTime(DateTimeTool.parseDateTime(new Date()));
            u.setCreatorId(CommonMethod.getPersonId());
            userRepository.saveAndFlush(u); //插入新的User记录
            t = new Teacher();   // 创建实体对象
            t.setPersonId(personId);
            teacherRepository.saveAndFlush(t);  //插入新的Teacher记录
            isNew = true;
        } else {
            p = t.getPerson();
        }
        personId = p.getPersonId();
        if (!num.equals(p.getNum())) {   //如果人员编号变化，修改人员编号和登录账号
            Optional<User> uOp = userRepository.findByPersonPersonId(personId);
            if (uOp.isPresent()) {
                u = uOp.get();
                u.setUserName(num);
                userRepository.saveAndFlush(u);
            }
            p.setNum(num);  //设置属性
        }
        p.setName(CommonMethod.getString(form, "name"));
        p.setDept(CommonMethod.getString(form, "dept"));
        p.setCard(CommonMethod.getString(form, "card"));
        p.setGender(CommonMethod.getString(form, "gender"));
        p.setBirthday(CommonMethod.getString(form, "birthday"));
        p.setEmail(CommonMethod.getString(form, "email"));
        p.setPhone(CommonMethod.getString(form, "phone"));
        p.setAddress(CommonMethod.getString(form, "address"));
        personRepository.save(p);  // 修改保存人员信息
        t.setTitle(CommonMethod.getString(form, "title"));
        t.setDegree(CommonMethod.getString(form, "degree"));
        teacherRepository.save(t);  //修改保存教师信息
        systemService.modifyLog(t,isNew);
        return CommonMethod.getReturnData(t.getPersonId());  // 将personId返回前端
    }


//    public List<Map<String,Object>> getTeacherFeeList(Integer personId) {
//        List<Fee> sList = feeRepository.findListByTeacher(personId);  // 查询某个教师消费记录集合
//        List<Map<String,Object>> list = new ArrayList<>();
//        if (sList == null || sList.isEmpty())
//            return list;
//        Map<String,Object> m;
//        Course c;
//        for (Fee s : sList) {
//            m = new HashMap<>();
//            m.put("title", s.getDay());
//            m.put("value", s.getMoney());
//            list.add(m);
//        }
//        return list;
//    }

//    public String importFeeData(Integer personId, InputStream in){
//        try {
//            Teacher teacher = teacherRepository.findById(personId).get();
//            XSSFWorkbook workbook = new XSSFWorkbook(in);  //打开Excl数据流
//            XSSFSheet sheet = workbook.getSheetAt(0);
//            Iterator<Row> rowIterator = sheet.iterator();
//            Row row;
//            Cell cell;
//            int i;
//            i = 1;
//            String day, money;
//            Optional<Fee> fOp;
//            double dMoney;
//            Fee f;
//            rowIterator.next();
//            while (rowIterator.hasNext()) {
//                row = rowIterator.next();
//                cell = row.getCell(0);
//                if (cell == null)
//                    break;
//                day = cell.getStringCellValue();  //获取一行消费记录 日期 金额
//                cell = row.getCell(1);
//                money = cell.getStringCellValue();
//                fOp = feeRepository.findByTeacherPersonIdAndDay(personId, day);  //查询是否存在记录
//                if (fOp.isEmpty()) {
//                    f = new Fee();
//                    f.setDay(day);
//                    f.setTeacher(teacher);  //不存在 添加
//                } else {
//                    f = fOp.get();  //存在 更新
//                }
//                if (money != null && !money.isEmpty())
//                    dMoney = Double.parseDouble(money);
//                else
//                    dMoney = 0d;
//                f.setMoney(dMoney);
//                feeRepository.save(f);
//            }
//            workbook.close();  //关闭Excl输入流
//            return null;
//        } catch (Exception e) {
//            log.error(e.getMessage());
//            return "上传错误！";
//        }
//
//    }

//    public DataResponse importFeeData(@RequestBody byte[] barr,
//                                      String personIdStr
//    ) {
//        Integer personId =  Integer.parseInt(personIdStr);
//        String msg = importFeeData(personId,new ByteArrayInputStream(barr));
//        if(msg == null)
//            return CommonMethod.getReturnMessageOK();
//        else
//            return CommonMethod.getReturnMessageError(msg);
//    }

    public ResponseEntity<StreamingResponseBody> getTeacherListExcl( DataRequest dataRequest) {
        String numName = dataRequest.getString("numName");
        List<Map<String,Object>> list = getTeacherMapList(numName);
        Integer[] widths = {8, 20, 10, 15, 15, 15, 25, 10, 15, 30, 20, 30};
        int i, j, k;
        String[] titles = {"序号", "工号", "姓名", "学院", "职称", "学位", "证件号码", "性别", "出生日期", "邮箱", "电话", "地址"};
        String outPutSheetName = "teacher.xlsx";
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFCellStyle styleTitle = CommonMethod.createCellStyle(wb, 20);
        XSSFSheet sheet = wb.createSheet(outPutSheetName);
        for (j = 0; j < widths.length; j++) {
            sheet.setColumnWidth(j, widths[j] * 256);
        }
        //合并第一行
        XSSFCellStyle style = CommonMethod.createCellStyle(wb, 11);
        XSSFRow row = null;
        XSSFCell[] cell = new XSSFCell[widths.length];
        row = sheet.createRow((int) 0);
        for (j = 0; j < widths.length; j++) {
            cell[j] = row.createCell(j);
            cell[j].setCellStyle(style);
            cell[j].setCellValue(titles[j]);
            cell[j].getCellStyle();
        }
        Map<String,Object> m;
        if (list != null && !list.isEmpty()) {
            for (i = 0; i < list.size(); i++) {
                row = sheet.createRow(i + 1);
                for (j = 0; j < widths.length; j++) {
                    cell[j] = row.createCell(j);
                    cell[j].setCellStyle(style);
                }
                m = list.get(i);
                cell[0].setCellValue((i + 1) + "");
                cell[1].setCellValue(CommonMethod.getString(m, "num"));
                cell[2].setCellValue(CommonMethod.getString(m, "name"));
                cell[3].setCellValue(CommonMethod.getString(m, "dept"));
                cell[4].setCellValue(CommonMethod.getString(m, "title"));
                cell[5].setCellValue(CommonMethod.getString(m, "degree"));
                cell[6].setCellValue(CommonMethod.getString(m, "card"));
                cell[7].setCellValue(CommonMethod.getString(m, "genderName"));
                cell[8].setCellValue(CommonMethod.getString(m, "birthday"));
                cell[9].setCellValue(CommonMethod.getString(m, "email"));
                cell[10].setCellValue(CommonMethod.getString(m, "phone"));
                cell[11].setCellValue(CommonMethod.getString(m, "address"));
            }
        }
        try {
            StreamingResponseBody stream = wb::write;
            return ResponseEntity.ok()
                    .contentType(CommonMethod.exelType)
                    .body(stream);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }

    }

    public DataResponse getTeacherPageData(DataRequest dataRequest) {
        String numName = dataRequest.getString("numName");
        Integer cPage = dataRequest.getCurrentPage();
        int dataTotal = 0;
        int size = 40;
        List<Map<String,Object>> dataList = new ArrayList<>();
        Page<Teacher> page = null;
        Pageable pageable = PageRequest.of(cPage, size);
        page = teacherRepository.findTeacherPageByNumName(numName, pageable);
        Map<String,Object> m;
        if (page != null) {
            dataTotal = (int) page.getTotalElements();
            List<Teacher> list = page.getContent();
            if (!list.isEmpty()) {
                for (Teacher teacher : list) {
                    m = getMapFromTeacher(teacher);
                    dataList.add(m);
                }
            }
        }
        Map<String,Object> data = new HashMap<>();
        data.put("dataTotal", dataTotal);
        data.put("pageSize", size);
        data.put("dataList", dataList);
        return CommonMethod.getReturnData(data);
    }
}


//    public DataResponse importFeeDataWeb(Map<String,Object> request,MultipartFile file) {
//        Integer personId = CommonMethod.getInteger(request, "personId");
//        try {
//            String msg= importFeeData(personId,file.getInputStream());
//            if(msg == null)
//                return CommonMethod.getReturnMessageOK();
//            else
//                return CommonMethod.getReturnMessageError(msg);
//        } catch (Exception e) {
//            log.error(e.getMessage());
//        }
//        return CommonMethod.getReturnMessageError("上传错误！");
//    }
//}