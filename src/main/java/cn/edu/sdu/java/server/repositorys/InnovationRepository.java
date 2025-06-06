package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.Innovation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface InnovationRepository extends JpaRepository<Innovation, Integer> {
    // 更新查询，通过学生的学号或姓名搜索
    @Query("select i from Innovation i join i.student s join s.person p where p.num like %?1% or p.name like %?1%")
    List<Innovation> findInnovationListByNumName(String numName);
    
    // 保留现有的通过指导教师查找的方法
    List<Innovation> findByAdvisorPersonId(Integer personId);
    
    // 添加通过学生ID查找创新成果的方法
    @Query("select i from Innovation i where i.student.personId = ?1")
    List<Innovation> findByStudentPersonId(Integer personId);
}
