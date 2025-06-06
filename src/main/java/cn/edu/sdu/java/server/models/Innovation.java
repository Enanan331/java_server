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
    @Column(name = "innovation_id")
    private Integer innovationId;

    @ManyToOne
    @JoinColumn(name = "student_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Student student;

    @Size(max = 20)
    @Column(name = "student_num")
    private String studentNum;

    @Size(max = 50)
    @Column(name = "student_name")
    private String studentName;

    @Size(max = 200)
    @Column(name = "achievement")
    private String achievement;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher advisor;
    
    @Size(max = 50)
    @Column(name = "advisor_name")
    private String advisorName;
}