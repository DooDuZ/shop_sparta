# 예약 상품 E-Commerce PROJECT shop


## 프로잭트 개요
### 원하는 물품을 구매하고 판매하는 e-commerce
```
예약 구매/판매를 위한 API를 제공합니다.
회원은 상품을 등록하고 대기/ 판매 / 판매 중단 등 여러 상태를 예약할 수 있습니다.
구매자는 원하는 상품을 wish list에 등록하거나 장바구니에 추가하고 주문할 수 있습니다.
```

### 기술 스택
<img src="https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=Spring%20Boot&logoColor=white"><br/>
<img src="https://img.shields.io/badge/Spring%20Security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white"><br/>
<img src="https://img.shields.io/badge/Spring%20Batch-6DB33F?style=for-the-badge&logo=Spring&logoColor=white"><br/>
<img src="https://img.shields.io/badge/JPA/Hibernate-59666C?style=for-the-badge&logo=hibernate&logoColor=white"><br/>
<img src="https://img.shields.io/badge/Mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white"><br/>
<img src="https://img.shields.io/badge/redis-ff4438?style=for-the-badge&logo=redis&logoColor=white"><br/>
<img src="https://img.shields.io/badge/docker-2496ed?style=for-the-badge&logo=docker&logoColor=white"><br/>
<img src="https://img.shields.io/badge/AWS%20S3-ff9900?style=for-the-badge&logo=amazons3&logoColor=white"><br/>

### Architecture
![architect-screenshot]

### ERD diagram
![erd-diagram]


<!-- GETTING STARTED -->
## Getting Started
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
<!--
** 멀티 모듈 통합 후 한 번에 실행 적용 예정
* batch 프로젝트를 클론합니다.
  ```bash
  git clone https://github.com/DooDuZ/shop_batch.git
  ```
* clone한 디렉토리로 이동하여 docker compose를 실행합니다
  ```bash
  cd shop_batch
  
  docker compose up -d
  ```
-->


<!-- USAGE EXAMPLES -->
## API 명세
* 회원
* 상품
* 장바구니
* 주문

<!-- CONTACT -->
## Contact

신지웅 - [@your_twitter](https://twitter.com/your_username) - email@example.com

Project Link: [https://github.com/DooDuZ/shop_sparta](https://github.com/DooDuZ/shop_sparta)


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