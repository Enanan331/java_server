package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.Competition;
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
}