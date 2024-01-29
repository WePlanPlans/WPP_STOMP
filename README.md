<img width="900" alt="Screenshot 2024-01-25 at 15 07 35" src="https://github.com/FinalDoubleTen/TenTenSTOMP/assets/95599193/25ecdd9c-c19f-4f34-b278-66d72de5baa8">


## 배포 링크

> **애플리케이션 메인 페이지** : [https://weplanplans.vercel.app](https://weplanplans.vercel.app)
> <br/>
> **Swagger API 문서 배포 링크** : [https://api.weplanplans.site/swagger-ui/index.html](https://api.weplanplans.site/swagger-ui/index.html)
> <br/>
> **API Repo link** : [https://github.com/FinalDoubleTen/TenTenBE](https://github.com/FinalDoubleTen/TenTenBE)


# 📚 Stack
<br/>
<div algin = left>
  <img src="https://img.shields.io/badge/java-FF5A00?style=for-the-badge&logo=Java&logoColor=white">
  <img src="https://img.shields.io/badge/gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white">
  <br/>
  <img src="https://img.shields.io/badge/spring-6DB33F?style=for-the-badge&logo=springboot&logoColor=black">
  <img src="https://img.shields.io/badge/spring security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=black">
  <br/>
  <img src="https://img.shields.io/badge/swagger-85EA2D?style=for-the-badge&logo=swagger&logoColor=black">

  <br/>
  <img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white">
  <br/>
  <img src="https://img.shields.io/badge/github actions-2088FF?style=for-the-badge&logo=githubactions&logoColor=white">
  <img src="https://img.shields.io/badge/github-181717?style=for-the-badge&logo=github&logoColor=white">
  <br/>
  <img src="https://img.shields.io/badge/ubuntu-E95420?style=for-the-badge&logo=ubuntu&logoColor=black">
  <img src="https://img.shields.io/badge/aws ec2-232F3E?style=for-the-badge&logo=amazonec2&logoColor=white">
  <img src="https://img.shields.io/badge/aws s3-569A31?style=for-the-badge&logo=amazons3&logoColor=white">
  <br/>
  <img src="https://img.shields.io/badge/notion-000000?style=for-the-badge&logo=notion&logoColor=white">
  <img src="https://img.shields.io/badge/slack-4A154B?style=for-the-badge&logo=slack&logoColor=white">
  <img src="https://img.shields.io/badge/intellij-000000?style=for-the-badge&logo=intellijidea&logoColor=white">
  <img src="https://img.shields.io/badge/postman-FF6C37?style=for-the-badge&logo=postman&logoColor=black">
</div>



# 팀원

<br/>

|    [@yuhyun1](https://github.com/yuhyun1)   |      [@ypd06021](https://github.com/ypd06021)         |           [@Jundev21](https://github.com/Jundev21)        |
|:-------------------------------------------------------------------------:|:---------------------------------------------------:|:-----------------------------------:|
| <img width="220" alt="스크린샷 2021-11-19 오후 3 52 02" src="https://github.com/FinalDoubleTen/TenTenBe/assets/95599193/e77c2c9d-3cb5-469a-8ea9-074a50daacd2"> | <img width="220" alt="스크린샷 2021-11-19 오후 3 52 02" src="https://github.com/FinalDoubleTen/TenTenBe/assets/95599193/9706d573-8787-4ffb-8eb7-952e64039a84"> | <img width="220" alt="스크린샷 2021-11-19 오후 3 52 02" src="https://github.com/FinalDoubleTen/TenTenBe/assets/95599193/fe93e567-f389-4bfc-adb2-d3e446395fd8"> |
|                                                                    `CI/CD` <br/> `Spring Security` <br/> `Mypage API`                                                                    |                                                           `OAuth` <br/> `Spring Security` <br/> `Mypage API`                                                           |                                                                    `Comment API` <br/> `Test Code`                                                                     |

|                                                                   [@Wonbn](https://github.com/Wonbn)                                                                   |                                                       [@Kim-Dong-Jun99](https://github.com/Kim-Dong-Jun99)                                                       |
|:----------------------------------------------------------------------------------------------------------------------------------------------------------------------:|:----------------------------------------------------------------------------------------------------------------------------------------------------------------:|
| <img width="220" alt="스크린샷 2021-11-19 오후 3 52 02" src="https://github.com/FinalDoubleTen/TenTenBe/assets/95599193/d73ce956-6b12-4518-8ca7-4ad227ddbd4e"> | <img width="220" alt="스크린샷 2021-11-19 오후 3 52 02" src="https://github.com/FinalDoubleTen/TenTenBe/assets/95599193/1617078f-5056-42a4-9bae-0ac0c70d03af"> |
|                                                                    `TourItem API` <br/> `Trip API`                                                                     |                                                        `Review API` <br/> `Keyword API` <br/> `STOMP API`                                                         |

</div>

# ERD

<img width="1435" alt="Screenshot 2024-01-25 at 13 23 15" src="https://github.com/WePlanPlans/WPP_STOMP/assets/95599193/f6ded1a4-3e75-4de0-9274-700512db02a8">

# 프로젝트 아키택쳐 

![image](https://github.com/WePlanPlans/WPP_STOMP/assets/95599193/02820999-5114-4e63-a5a0-cde5b7567501)


# 패키지 구조
```
├── main
│   ├── java
│   │   └── org
│   │       └── tenten
│   │           └── tentenstomp
│   │               ├── TenTenStompApplication.java
│   │               ├── config
│   │               │   ├── AsyncConfig.java
│   │               │   ├── KafkaConsumerConfig.java
│   │               │   ├── KafkaProducerConfig.java
│   │               │   ├── KafkaTopicConfig.java
│   │               │   ├── OpenApiConfig.java
│   │               │   ├── RedisConfig.java
│   │               │   ├── WebConfig.java
│   │               │   └── WebSocketConfig.java
│   │               ├── domain
│   │               │   ├── comment
│   │               │   ├── member
│   │               │   ├── review
│   │               │   ├── tour
│   │               │   └── trip
│   │               └── global
│   │                   ├── aspect
│   │                   ├── cache
│   │                   ├── common
│   │                   ├── component
│   │                   ├── converter
│   │                   ├── exception
│   │                   ├── handler
│   │                   ├── jwt
│   │                   ├── messaging
│   │                   ├── response
│   │                   ├── stomp
│   │                   └── util
│   └── resources
│       ├── application.yml
│       ├── static
│       └── templates
└── test

```
