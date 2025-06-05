package cn.edu.sdu.java.server.models;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.CascadeType;

@Getter
@Setter
@Entity
@Table(name = "volunteer_work")
public class VolunteerWork {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer volunteerWorkId; // 唯一标识

    private String name;    // 活动名称（如"社区敬老服务"）
    private String location;        // 服务地点（详细地址）
    private String date;
    private String startTime; // 服务开始时间
    private String endTime;   // 服务结束时间
    private Double serviceHours;    // 服务时长（小时）


    @ManyToMany
    @JoinTable(
            name = "volunteer_work_student",
            joinColumns = @JoinColumn(name = "volunteer_work_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id")
    )
    @Cascade({CascadeType.SAVE_UPDATE, CascadeType.MERGE})
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<Student> students = new HashSet<>();

    private String organizer;       // 志愿服务组织单位/个人，组织者

}