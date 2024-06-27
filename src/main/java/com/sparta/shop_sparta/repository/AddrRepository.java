package com.sparta.shop_sparta.repository;

import com.sparta.shop_sparta.domain.entity.member.AddrEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddrRepository extends JpaRepository<AddrEntity, Long> {
    List<AddrEntity> findAllByMemberEntity_MemberId(Long memberId);
}
