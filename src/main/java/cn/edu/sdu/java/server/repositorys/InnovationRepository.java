package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.Innovation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InnovationRepository extends JpaRepository<Innovation, Integer> {
    @Query("select i from Innovation i where " +
           "i.studentNum like %?1% or " +
           "i.studentName like %?1% or " +
           "i.achievement like %?1% or " +
           "i.advisorName like %?1%")
    List<Innovation> findInnovationListByNumName(String numName);
    
    // 添加通过学号查找创新成果的方法
    List<Innovation> findByStudentNum(String studentNum);
    
    // 添加通过指导教师ID查找创新成果的方法
    List<Innovation> findByAdvisorPersonId(Integer personId);
}
