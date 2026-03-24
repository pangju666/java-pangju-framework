<p align="center">
  <a href="https://github.com/pangju666/java-pangju-framework/releases">
    <img alt="GitHub release" src="https://img.shields.io/github/release/pangju666/java-pangju-framework.svg?style=flat-square&include_prereleases" />
  </a>

  <a href="https://central.sonatype.com/search?q=g:io.github.pangju666.framework%20%20a:framework-bom&smo=true">
    <img alt="maven" src="https://img.shields.io/maven-central/v/io.github.pangju666.framework/framework-bom.svg?style=flat-square">
  </a>

  <a href="https://www.apache.org/licenses/LICENSE-2.0">
    <img alt="license" src="https://img.shields.io/badge/license-Apache%202-4EB1BA.svg?style=flat-square">
  </a>
</p>

# Pangju Framework

Pangju Framework 是一个基于 Spring Framework 深度拓展的模块化 Java 开发框架。它旨在为开发者提供一套开箱即用的、标准化的开发套件，集成了
Web、持久层（MyBatis Plus/MongoDB/Redis）等常用组件的增强功能，显著降低 boilerplate 代码量，提升开发一致性和效率。

## 🌟 核心特性

- **模块化设计**：按需引入所需模块，保持项目精简。
- **Web 增强**：提供统一的异常处理机制、丰富的 REST 客户端工具、以及常用的 DTO/VO 模型。
- **持久层封装**：针对 MyBatis Plus 提供通用的 BaseEntity 和 BaseRepository，内置常用的 TypeHandler 和校验注解。
- **工具集**：提供 Bean 操作、反射增强、SpEL 评估等底层工具，简化日常开发。
- **BOM 支持**：通过 Maven BOM 统一管理版本，避免依赖冲突。

## 📦 模块概览

| 模块名                           | 描述              | 核心功能                                                       |
|:------------------------------|:----------------|:-----------------------------------------------------------|
| `framework-spring`            | 核心扩展            | Bean 操作、反射工具、SpEL 增强工具。                                    |
| `framework-web`               | Web 增强          | 异常体系、REST 客户端封装、常用 Filter/Interceptor。                     |
| `framework-data-mybatis-plus` | MyBatis Plus 增强 | 通用 Entity、Repository、JSON/List TypeHandler、UUID/AutoId 校验。 |
| `framework-data-redis`        | Redis 增强        | 基于 Spring Data Redis 的二次封装与增强。                             |
| `framework-data-mongodb`      | MongoDB 增强      | 基于 Spring Data MongoDB 的二次封装与增强。                           |
| `framework-bom`               | 版本管理            | 统一管理所有 framework 模块的版本依赖。                                  |
| `framework-all`               | 全量集成            | 一键引入上述所有功能模块。                                              |

## 🚀 快速开始

### 1. 引入 BOM (推荐)

在您的项目 `pom.xml` 中引入版本管理：

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>io.github.pangju666.framework</groupId>
            <artifactId>framework-bom</artifactId>
            <version>1.0.0</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

### 2. 引入功能模块

根据需要引入特定模块：

```xml
<dependencies>
    <!-- Web 开发增强 -->
    <dependency>
        <groupId>io.github.pangju666.framework</groupId>
        <artifactId>framework-web</artifactId>
    </dependency>

    <!-- MyBatis Plus 增强 -->
    <dependency>
        <groupId>io.github.pangju666.framework</groupId>
        <artifactId>framework-data-mybatis-plus</artifactId>
    </dependency>
</dependencies>
```

### 3. (可选) 全量引入

如果您需要使用所有功能，可以直接引入 `framework-all`：

```xml

<dependency>
    <groupId>io.github.pangju666.framework</groupId>
    <artifactId>framework-all</artifactId>
    <version>1.0.0</version>
</dependency>
```

## 📖 文档

更多详细说明请参考：[在线文档](https://pangju666.github.io/pangju-java-doc/framework/getting-started.html)

## 📄 许可证

本项目采用 [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0) 许可证。

---
感谢所有为项目做出贡献的开发者，以及项目所使用的开源框架和工具。
