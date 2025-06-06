package cn.edu.sdu.java.server.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "competition")
public class Competition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer competitionId;

    @Size(max = 20)
    private String studentNum;

    @Size(max = 50)
    private String studentName;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @Size(max = 50)
    private String subject;

    @Size(max = 100)
    private String result;

    @Size(max = 12)
    private String competitionTime;
}
