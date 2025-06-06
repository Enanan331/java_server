package cn.edu.sdu.java.server.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(	name = "teacher",
        uniqueConstraints = {
        })

public class Teacher {
    @Id
    private Integer personId;

    @OneToOne
    @JoinColumn(name="person_id")//注意连接数据库时的格式
    @JsonIgnore
    private Person person;

    @Size(max = 20)
    private String title;

    @Size(max = 50)
    private String degree;

}
