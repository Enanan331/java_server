package cn.edu.sdu.java.server.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(name = "innovation")
public class Innovation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer innovationId;

    @Size(max = 20)
    private String studentNum;

    @Size(max = 50)
    private String studentName;

    @ManyToOne
    @JoinColumn(name = "student_id")
    @OnDelete(action = OnDeleteAction.CASCADE) // 添加级联删除注解
    private Student student;

    @Size(max = 200)
    private String achievement;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher advisor;

    @Size(max = 50)
    private String advisorName;
}