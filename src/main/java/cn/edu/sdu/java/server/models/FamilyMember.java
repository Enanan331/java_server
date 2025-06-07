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
@Table(	name = "family_member",
        uniqueConstraints = {
        })

public class FamilyMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer memberId;

    @ManyToOne
    @JoinColumn(name="studentPersonId")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Student student;//通过输入的学生姓名查找出来

    @Size(max=10)
    private String relation;//?
    @Size(max=30)
    private String name;//
    @Size(max=10)
    private String gender;//
    @Size(max=3)
    private String age;//
    @Size(max=50)
    private String unit;//工作单位

}
