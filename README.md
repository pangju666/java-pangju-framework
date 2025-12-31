<p align="center">
  <a href="https://github.com/pangju666/java-pangju-framework/releases">
    <img alt="GitHub release" src="https://img.shields.io/github/release/pangju666/java-pangju-framework.svg?style=flat-square&include_prereleases" />
  </a>

  <a href="https://central.sonatype.com/search?q=g:io.github.pangju666.framework%20%20a:framework-bom&smo=true">
    <img alt="maven" src="https://img.shields.io/maven-central/v/io.github.pangju666.framework/framework-bom.svg?style=flat-square">
  </a>

  <a href="https://www.apache.org/licenses/LICENSE-2.0">
    <img alt="code style" src="https://img.shields.io/badge/license-Apache%202-4EB1BA.svg?style=flat-square">
  </a>
</p>

# Pangju Framework

## 简介

Pangju Framework 是基于 Spring Framework 拓展的模块化 Java 开发框架，提供 Web、MyBatis Plus、Redis、MongoDB
等常用组件的增强集成，旨在简化开发流程，提高开发效率。

## 快速开始

1. 引入 BOM（推荐）

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>io.github.pangju666.framework</groupId>
            <artifactId>framework-bom</artifactId>
            <version>最新版本</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

2. 引入模块

```xml
<dependencies>
    <!-- Spring 核心扩展与工具 -->
    <dependency>
        <groupId>io.github.pangju666.framework</groupId>
        <artifactId>framework-spring</artifactId>
    </dependency>
    <!-- Web 开发增强支持 -->
    <dependency>
        <groupId>io.github.pangju666.framework</groupId>
        <artifactId>framework-web</artifactId>
    </dependency>
    <!-- MyBatis Plus 增强与集成 -->
    <dependency>
        <groupId>io.github.pangju666.framework</groupId>
        <artifactId>framework-data-mybatis-plus</artifactId>
    </dependency>
    <!-- Redis 操作与缓存支持 -->
    <dependency>
        <groupId>io.github.pangju666.framework</groupId>
        <artifactId>framework-data-redis</artifactId>
    </dependency>
    <!-- MongoDB 操作支持 -->
    <dependency>
        <groupId>io.github.pangju666.framework</groupId>
        <artifactId>framework-data-mongodb</artifactId>
    </dependency>
</dependencies>
```

3. 或者一次性引入全部模块

```xml

<dependencies>
    <dependency>
        <groupId>io.github.pangju666.framework</groupId>
        <artifactId>framework-all</artifactId>
        <version>最新版本</version>
    </dependency>
</dependencies>
```

## 许可证

本项目采用 Apache License 2.0 许可证 - 详情请参阅 [LICENSE](LICENSE) 文件。

## 致谢

感谢所有为项目做出贡献的开发者，以及项目所使用的开源框架和工具。
