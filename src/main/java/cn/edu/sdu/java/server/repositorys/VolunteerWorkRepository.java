package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.VolunteerWork;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface VolunteerWorkRepository extends JpaRepository<VolunteerWork,Integer> {
    List<VolunteerWork> findByName(String name);
    List<VolunteerWork> findAll();
}
