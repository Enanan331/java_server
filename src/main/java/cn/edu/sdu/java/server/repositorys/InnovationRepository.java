package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.Innovation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface InnovationRepository extends JpaRepository<Innovation, Integer> {
    @Query("select i from Innovation i where i.studentNum like %?1% or i.studentName like %?1%")
    List<Innovation> findInnovationListByNumName(String numName);
    
    // 添加根据教师ID查询创新成果的方法
    List<Innovation> findByAdvisorPersonId(Integer personId);
}
