package cn.edu.sdu.java.server.models;


/*
 * Course 课程表实体类  保存课程的的基本信息信息，
 * Integer courseId 人员表 course 主键 course_id
 * String num 课程编号
 * String name 课程名称
 * Integer credit 学分
 * Course preCourse 前序课程 pre_course_id 关联前序课程的主键 course_id
 */

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(	name = "course",
        uniqueConstraints = {
        })
public class Course  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer courseId;
    @NotBlank
    @Size(max = 20)
    private String num;

    @Size(max = 50)
    private String name;
    private String credit;
    private String selectNum;
    private String attendenceNum;
    @ManyToOne
    @JoinColumn(name="pre_course_id")
    private Course preCourse;
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Homework> homework = new ArrayList<>();//作业,一门课可能有多个作业

    @ElementCollection
    @CollectionTable(
            name = "course_textbooks",
            joinColumns = @JoinColumn(name = "course_id")
    )
    @Column(name = "textbook_name")
    @Size(max = 50) // 单个课本名称的最大长度
    private List<String> textbooks = new ArrayList<>(); // 课本名称列表

    @Size(max = 12)
    private String coursePath;

    // 添加课本的便利方法
    public void addTextbook(String textbookName) {
        this.textbooks.add(textbookName);
    }

}
