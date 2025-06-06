package cn.edu.sdu.java.server.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;


/**
 * Student学生表实体类 保存每个学生的信息，
 * Integer personId 学生表 student 主键 person_id 与Person表主键相同
 * Person person 关联到该用户所用的Person对象，账户所对应的人员信息 person_id 关联 person 表主键 person_id
 * String major 专业
 * String className 班级
 *
 */
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(	name = "student",
        uniqueConstraints = {
        })
public class Student {
    @Id
    private Integer personId;

    @OneToOne
    @JoinColumn(name="person_id")//注意连接数据库时的格式
    @JsonIgnore
    private Person person;

    @Size(max = 20)
    private String major;

    @Size(max = 50)
    private String className;

    @ManyToMany(
            mappedBy = "students",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE}
    )
    @OnDelete(action = OnDeleteAction.CASCADE) // 确保数据库级联删除
    private Set<VolunteerWork> volunteerWorks = new HashSet<>();

    @ManyToMany(
            mappedBy = "members",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE}
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<Club> clubs = new HashSet<>();

//    @ManyToMany(
//            mappedBy = "participants",
//            cascade = {CascadeType.PERSIST, CascadeType.MERGE}
//    )
//    @OnDelete(action = OnDeleteAction.CASCADE)
//    private Set<StudentActivity> activities = new HashSet<>();

    @OneToMany(mappedBy = "student", cascade = CascadeType.REMOVE)
    private List<Competition> competitions;

    @OneToMany(mappedBy = "student", cascade = CascadeType.REMOVE)
    private List<Innovation> innovations;
}