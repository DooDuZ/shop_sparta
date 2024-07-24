# 예약 상품 E-Commerce PROJECT shop


## 프로잭트 개요

---

### 원하는 물품을 구매하고 판매하는 e-commerce
```
Online Open Run E-Commerce Shop Sparta 입니다. 
Spring Batch 서버를 통해 상품 등록, 예약, 상품 배송 등의 안정적인 스케쥴링을 제공합니다.
대규모 트래픽에 대한 동시성 처리와 빠른 응답을 위한 캐싱이 적용되어 있습니다.
```
## 개발 환경

---

* **JDK** : Amazon Corretto 21.0.3
* **Framework** : Spring Boot 3.3.0
    * Spring Security 3.3.0
    * Spring Data JPA 3.3.0
    * Spring Batch 5.1.2
* **Database** :
    * **RDBMS** : Mysql 9.0.0
    * **Cache Storage** : Redis 7.2.5
* **ETC** : AWS S3, Docker


<img src="https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=Spring%20Boot&logoColor=white"> <img src="https://img.shields.io/badge/Spring%20Security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white"> <img src="https://img.shields.io/badge/Spring%20Batch-6DB33F?style=for-the-badge&logo=Spring&logoColor=white">
<img src="https://img.shields.io/badge/JPA/Hibernate-59666C?style=for-the-badge&logo=hibernate&logoColor=white"> <img src="https://img.shields.io/badge/Mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white"> <img src="https://img.shields.io/badge/redis-ff4438?style=for-the-badge&logo=redis&logoColor=white">
<img src="https://img.shields.io/badge/docker-2496ed?style=for-the-badge&logo=docker&logoColor=white"> <img src="https://img.shields.io/badge/AWS%20S3-ff9900?style=for-the-badge&logo=amazons3&logoColor=white">


## Architecture

---

![architect-screenshot]

## ERD diagram

---

![erd-diagram]


<!-- GETTING STARTED 
## Getting Started

---

이 프로젝트를 시작하려면 아래의 단계들을 따르세요.

### 필수 요소

이 프로젝트를 실행하기 위해서는 Docker가 필요합니다.
Docker가 설치되어 있지 않은 경우, [Docker 공식 웹사이트](https://www.docker.com/get-started)에서 Docker를 다운로드하고 설치할 수 있습니다.

### 설치 및 실행

1. **Docker 설치**

   먼저, Docker를 설치합니다. 아래의 링크를 통해 자신의 운영체제에 맞는 Docker 버전을 다운로드하여 설치하세요.

    - [Docker Desktop for Windows](https://docs.docker.com/desktop/install/windows-install/)
    - [Docker Desktop for Mac](https://docs.docker.com/desktop/install/mac-install/)
    - [Docker Engine for Linux](https://docs.docker.com/engine/install/)

2. **프로젝트 클론**

* shop 프로젝트를 클론합니다.
  ```bash
  git clone https://github.com/DooDuZ/shop_sparta.git
  ```
* clone한 디렉토리로 이동하여 docker compose를 실행합니다
  ```bash
  cd shop_sparta
  
  docker compose up -d
  ```
-->

## API 명세

---

* [Git Book](https://dooduzs-organization.gitbook.io/api/)
<!--
* 회원
* 상품
* 장바구니
* 주문
-->

## 주요 기능

---
### 회원
* **EMAIL 인증(SMTP)**
    * 회원 가입 시 인증 메일이 발송 됩니다.
        * 메일 발신/수신 실패 시 재요청 API 제공
    * 메일에 포함된 링크를 누르면 BASIC 회원 권한을 획득합니다.
* **JWT 인증 / 인가 (Access / Refresh)**
    * Client 별 Refresh Token을 발급하고 관리합니다.
    * 기기별 로그인 / 로그아웃을 제공합니다.
* **회원 정보 암호화**
    * 비밀번호 - 복호화가 불가능한 인코더를 사용하여 보안성을 향상 시켰습니다.
    * 개인정보 - AES 대칭키 암호화를 적용하여 사용자의 요청 시에만 복호화된 정보를 제공합니다.
* **마이 페이지**
    * 주소 관리
        * 다중 주소(최대 5개) 관리를 지원합니다.
    * 장바구니 조회
      * Redis 캐싱 스토리지를 통해 14일 간 데이터가 보관됩니다.
      * 담은 상품을 조회하고 수량을 수정할 수 있습니다.
      * 일부 상품을 선택하여 주문할 수 있습니다.

### 상품
* **상품 상태 예약(Spring Batch)**
    * 상품 공개 / 판매 오픈 / 판매 종료 / 판매 중단(비공개) 상태 지원
    * 시간을 지정하여 상품의 상태를 예약합니다.
    * Batch 서버에서 주기적으로(10분 마다) 예약 테이블을 모니터링하고 상태를 업데이트 합니다.
* **S3 연동 이미지 관리**
    * 상품 등록 시 최초 version 값이 부여됩니다.
    * 이미지가 S3에 업데이트 되고, URL이 version과 함께 DB에 저장됩니다.
    * 상품 이미지 변경 발생 시 version이 업데이트 되고 새로운 이미지가 S3와 DB에 업로드 됩니다.
    * 하루 중 설정한 시간(새벽 4시)에 Batch를 통해 사용하지 않는 version 이미지가 삭제됩니다.

### 주문
* **스케쥴링을 통한 자동 발송 / 반품 처리**
    * Batch를 통해 주기적으로 주문 상태가 업데이트 됩니다.
    * 주문 완료 후 24시간마다 주문 완료 - 배송 중 - 배송 완료 순서의 업데이트가 일어납니다.
* **주문 취소 기능**
    * Batch를 통해 스케쥴이 관리됩니다.
    * 주문 직후 24시간 내에 취소가 가능합니다.
    * 배송 완료 후 24시간 내에 반품 신청이 가능합니다.
        * 주문 취소 / 반품 완료 시 결제 금액이 환불됩니다.
* **주문 속도 향상 처리**
    * 캐시 스토리지를 통한 주문
    * 캐싱-DB 데이터 동기화 비동기 처리


## 트러블 슈팅

---

* **[동시성 이슈](https://dooduz.tistory.com/19)**
    * 기존 재고 변경 방식
        * DB 데이터 Get → 데이터 가공 → Set 방식
        * 여러 요청이 **동시에 같은 재고 데이터를 읽고 수정**하는 문제 발생
    * 해결
        * 재고 업데이트 방법 **Get / Set 방식 → Atomic 연산**으로 변경
        * **Redis Increment** 연산을 사용하여 **Lock 없이 동시성 해결**

## 성능 개선

---
* **[주문 응답 시간 개선](https://dooduz.tistory.com/20)**
    * Before
        * 주문 요청 시 DB 재고 확인 - 업데이트 query 반영 후 결제
    * After
        * **캐싱 읽기 전략 - Look Aside 적용**
            * Cache Hit 시 캐싱 데이터 사용
            * Cache Miss 시 DB 데이터 캐싱 후 사용
        * **쓰기 전략 - Write Back / Write Through 혼합**
            * 모든 쓰기 작업을 애플리케이션에서 Cache Storage와 DB에 반영
            * DB 반영 메서드 비동기 처리
* 개선 결과 - 동시 요청 5000 건 / 상품 4종 주문 요청 기준
    * **tps 30 / 최대 응답 시간 35000ms -> tps 270 / 최대 응답 시간 4000ms**
    * 추가 개선
        * 상품 상세 정보 캐싱 - 주문 시 캐시 데이터 사용 - DB 접근 최소화
        * **최대 응답 시간 4000ms -> 1478ms**



<!-- CONTACT -->
## Contact

**Email** : sin9158@naver.com  
**GitHub**: [Shop Sparta](https://github.com/DooDuZ/shop_sparta)  
**Blog**: [DooDuZ Tistory](https://dooduz.tistory.com/)


<!-- MARKDOWN LINKS & IMAGES -->
[architect-screenshot]: images/Architect.jpg
[erd-diagram]: images/erd5주차.png


<!-- 기능 목록
### Itellic은 구현 가능성
- User
    - 공통 기능
        - 회원 가입
            - 이메일 인증
        - 로그인
        - 로그아웃
        - 회원 정보 변경
            - 비밀번호
            - 주소
            - 전화번호
    - Role별 기능
        - 관리자
        - 구매자
            - *유저 등급 시스템*
                - *등급에 따른 할인/적립 정책 등 적용*
                - *상위 등급 한정 판매 상품 지정*
            - Details
                - 관심 상품 목록 조회
                - 구매 목록 조회
- Product
    - 상품 등록
    - 상품 삭제
    - 다수 데이터 조회
        - 목록
            - 전체 상품
            - *카테고리 별 상품*
            - *검색 목록*
                - *이름 포함*
                - *내용 포함*
    - 개별 상품 상세 조회
- Order
    - 장바구니(1주차 요구사항의 wishList)
        - 담기
        - 상품 목록 조회
        - 상품 수량 변경
        - 상품 제거
        - 주문
    - 주문 목록
      - 주문 확정 (주문 레코드 생성)
      - 주문 상태 조회
          - *배송 조회*
      - 주문 취소
          - 재고 반영
      - 반품
          - 재고 반영(D+1)
-->