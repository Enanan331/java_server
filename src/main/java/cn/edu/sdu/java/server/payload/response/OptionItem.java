package cn.edu.sdu.java.server.payload.response;

import lombok.Getter;
import lombok.Setter;

/**
 * OptionItem 选项数据类
 * Integer id  数据项id
 * String value 数据项值
 * String label 数据值标题
 */
@Setter
@Getter
public class OptionItem {
    private Integer id;
    private String value;
    private String title;
    private String label; // 添加label属性
    
    public OptionItem(){
    }
    
    public OptionItem(Integer id, String value, String title){
        this.id = id;
        this.value = value;
        this.title = title;
        this.label = title; // 设置label与title相同
    }

    public String toString(){
        return label != null ? label : title; // 修改toString方法，优先返回label
    }

}
