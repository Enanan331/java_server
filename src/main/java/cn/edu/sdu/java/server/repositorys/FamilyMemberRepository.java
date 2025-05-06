package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.FamilyMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FamilyMemberRepository extends JpaRepository<FamilyMember,Integer> {
    List<FamilyMember> findByStudentPersonId(Integer personId);
//    @Query(value = "from FamilyMember where ?1='' or name like %?1% or student.person.name like %?1%")
    List<FamilyMember> findByName(String name);

    Optional<FamilyMember> findByMemberId(Integer memberId);
}
