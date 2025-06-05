package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.Club;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClubRepository extends JpaRepository<Club, Integer> {
    List<Club> findByName(String name);
    List<Club> findAll();
}