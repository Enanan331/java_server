package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.Competition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CompetitionRepository extends JpaRepository<Competition, Integer> {
    
    @Query("select c from Competition c where c.studentNum like %?1% or c.studentName like %?1%")
    List<Competition> findCompetitionListByNumName(String numName);
    
    // 添加根据学生ID查询竞赛记录的方法
    List<Competition> findByStudentPersonId(Integer personId);
}