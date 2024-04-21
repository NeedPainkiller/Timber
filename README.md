<p align='center'>
    <img src="https://capsule-render.vercel.app/api?type=waving&color=auto&height=300&section=header&text=TIMBER&fontSize=90&animation=fadeIn&fontAlignY=38&desc=Contrainer Backend Service&descAlignY=51&descAlign=62"/>
</p>
# TIMBER

<p align="center">
  <a href="https://blog.needpainkiller.xyz/" target="blank"><img src="./img/timber-logo.svg" width="200" alt="Timber Logo" /></a>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Java-67493A?style=flat-square&logo=OpenJDK&logoColor=white"/>
  <img src="https://img.shields.io/badge/Spring%20Boot-6DB33F?style=flat-square&logo=SpringBoot&logoColor=white"/>
  <img src="https://img.shields.io/badge/Spring%20Security-6DB33F?style=flat-square&logo=springsecurity&logoColor=white"/>
</p>
<p align="center">
  <img src="https://img.shields.io/badge/Apache Kafka-231F20?style=flat-square&logo=apachekafka&logoColor=white"/>
  <img src="https://img.shields.io/badge/Hibernate-59666C?style=flat-square&logo=Hibernate&logoColor=white"/>
  <img src="https://img.shields.io/badge/Mysql-4479A1?style=flat-square&logo=mysql&logoColor=white"/>
  <img src="https://img.shields.io/badge/Mariadb-003545?style=flat-square&logo=mariadb&logoColor=white"/>
  <img src="https://img.shields.io/badge/Redis-DC382D?style=flat-square&logo=redis&logoColor=white"/>
</p>
<p align="center">
  <img src="https://img.shields.io/badge/Microsoft Azure-0078D4?style=flat-square&logo=microsoftazure&logoColor=white"/>
  <img src="https://img.shields.io/badge/Docker-2496ED?style=flat-square&logo=docker&logoColor=white"/>
  <img src="https://img.shields.io/badge/Gradle-02303A?style=flat-square&logo=gradle&logoColor=white"/>
</p>
  <!--[![Backers on Open Collective](https://opencollective.com/nest/backers/badge.svg)](https://opencollective.com/nest#backer)
  [![Sponsors on Open Collective](https://opencollective.com/nest/sponsors/badge.svg)](https://opencollective.com/nest#sponsor)-->
This project uses Spring Boot with OpenJDK 21 and Gradle7.2.1

This project forked from monolithic architecture [Timber-Monolith](https://github.com/NeedPainkiller/Timber-Monolith)

please visit its Blog: https://blog.needpainkiller.xyz


## Description
Main Backend service for [Timber](https://github.com/NeedPainkiller/Timber) Framework
## Requirements
- <img src="https://img.shields.io/badge/Java-67493A?style=flat-square&logo=OpenJDK&logoColor=white"/>
- <img src="https://img.shields.io/badge/Spring%20Boot-6DB33F?style=flat-square&logo=SpringBoot&logoColor=white"/>
- <img src="https://img.shields.io/badge/Apache Kafka-231F20?style=flat-square&logo=apachekafka&logoColor=white"/>
- <img src="https://img.shields.io/badge/Hibernate-59666C?style=flat-square&logo=Hibernate&logoColor=white"/>
- <img src="https://img.shields.io/badge/Mysql-4479A1?style=flat-square&logo=mysql&logoColor=white"/>
- <img src="https://img.shields.io/badge/Mariadb-003545?style=flat-square&logo=mariadb&logoColor=white"/>
- <img src="https://img.shields.io/badge/Redis-DC382D?style=flat-square&logo=redis&logoColor=white"/>
- <img src="https://img.shields.io/badge/Gradle-02303A?style=flat-square&logo=gradle&logoColor=white"/>
## Installation
```bash
https://github.com/NeedPainkiller/Timber-Monolith.git
```

## Running on IntelliJ IDEA

```bash
# VM Options
-server -Dspring.profiles.active=local,mariadb,jwt-header,ehcache,kafka -Djava.net.preferIPv4Stack=true -Dlog4j2.formatMsgNoLookups=true -Xms1024m -Xmx1024m -XX:MaxNewSize=384m -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=./logs/java_pid<pid>.hprof -XX:ParallelGCThreads=2 -Xlog:gc:./logs/gclog

# development
./.run/TIMBER-LOCAL-MARIADB.run.xml
```

## Build
```Bash
# Build 
gradle bootJar
```

## Run Jar
```Bash
java -jar build/libs/Timber-0.0.1-SNAPSHOT.jar
```

## Stay in touch
<p>
  <a href="https://home.needpainkiller.xyz/" target="_blank"><img src="https://img.shields.io/badge/Home-EF3346?style=flat-square&logo=googlehome&logoColor=white"/></a>
  <a href="https://blog.needpainkiller.xyz/" target="_blank"><img src="https://img.shields.io/badge/Blog-15171A?style=flat-square&logo=Ghost&logoColor=white"/></a>
  <a href="mailto:kam6512@gmail.com" target="_blank"><img src="https://img.shields.io/badge/kam6512@gmail.com-EA4335?style=flat-square&logo=Gmail&logoColor=white"/></a>
  <a href="mailto:needpainkiller6512@gmail.com" target="_blank"><img src="https://img.shields.io/badge/needpainkiller6512@gmail.com-EA4335?style=flat-square&logo=Gmail&logoColor=white"/></a>
</p>

- Author - [NeedPainkiller](https://home.needpainkiller.xyz/)
- Blog - [https://blog.needpainkiller.xyz](https://blog.needpainkiller.xyz/)
- Github - [@PainKiller](https://github.com/NeedPainkiller)

## License

Timber is [MIT licensed](LICENSE)
