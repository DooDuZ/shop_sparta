package com.sparta.batch.repository;

import com.sparta.batch.domain.entity.member.AddrEntity;
import com.sparta.batch.domain.entity.member.MemberEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddrRepository extends JpaRepository<AddrEntity, Long> {
    List<AddrEntity> findAllByMemberEntity(MemberEntity memberEntity);
}
