package cn.edu.sdu.java.server.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "club")
public class Club {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer clubId;

    private String name;
    private String location;
    private String description;


    // 社长（一对一关系）
    @OneToOne
    @JoinColumn(name = "president_id", unique = true)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Student president;

    // 指导老师（一对一关系）
    @OneToOne
    @JoinColumn(name = "advisor_id", unique = true)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Teacher advisor;

    // 社团成员（多对多关系）
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "club_member",
            joinColumns = @JoinColumn(name = "club_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id")
    )
    @Cascade({CascadeType.SAVE_UPDATE, CascadeType.MERGE})
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<Student> members = new HashSet<>();
}