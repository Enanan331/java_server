package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.Competition;
import cn.edu.sdu.java.server.models.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompetitionRepository extends JpaRepository<Competition, Integer> {
    @Query(value = "from Competition where ?1='' or studentNum like %?1% or studentName like %?1% ")
    List<Competition> findCompetitionListByNumName(String numName);

    Optional<Competition> findByStudentNum(String studentNum);
    
    // 根据学生实体查询竞赛记录
    List<Competition> findByStudent(Student student);
    
    // 根据学生ID查询竞赛记录
    List<Competition> findByStudentPersonId(Integer personId);
}