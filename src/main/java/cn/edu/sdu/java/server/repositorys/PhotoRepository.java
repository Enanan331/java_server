package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Integer> {
    Photo findByPersonId(Integer personId);
}
