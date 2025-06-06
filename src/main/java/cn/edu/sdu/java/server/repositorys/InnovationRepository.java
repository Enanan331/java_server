package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.Innovation;
import cn.edu.sdu.java.server.models.Student;
import cn.edu.sdu.java.server.models.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InnovationRepository extends JpaRepository<Innovation, Integer> {
    @Query(value = "from Innovation where ?1='' or studentNum like %?1% or studentName like %?1% ")
    List<Innovation> findInnovationListByNumName(String numName);

    List<Innovation> findByStudent(Student student);

    List<Innovation> findByAdvisor(Teacher teacher);
}