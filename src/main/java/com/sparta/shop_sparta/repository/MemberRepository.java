package com.sparta.shop_sparta.repository;

import com.sparta.shop_sparta.domain.entity.member.MemberEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<MemberEntity, Long> {
    // 아이디 검색
    Optional<MemberEntity> findByLoginId(String loginId);

    // 이메일 검색
    Optional<MemberEntity> findByEmail(String email);
}
