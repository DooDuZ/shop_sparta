package com.sparta.shop_sparta.member.repository;

import com.sparta.shop_sparta.member.domain.entity.AddrEntity;
import com.sparta.shop_sparta.member.domain.entity.MemberEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddrRepository extends JpaRepository<AddrEntity, Long> {
    List<AddrEntity> findAllByMemberEntity(MemberEntity memberEntity);
}
