package cn.edu.sdu.java.server.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "innovation")
public class Innovation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer innovationId;

    @Column(length = 20)
    private String studentNum;

    @Column(length = 50)
    private String studentName;

    @Column(length = 500)
    private String achievement;

    @ManyToOne
    @JoinColumn(name = "advisor_id")
    private Teacher advisor;

    @Column(length = 50)
    private String advisorName;
    
    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;
}