[中文](README.md) | [English](README_EN.md)

<p align="center">
  <a href="https://github.com/pangju666/java-pangju-framework/releases">
    <img alt="GitHub release" src="https://img.shields.io/github/release/pangju666/java-pangju-framework.svg?style=flat-square&include_prereleases" />
  </a>

  <a href="https://central.sonatype.com/search?q=g:io.github.pangju666%20%20a:pangju-framework-bom&smo=true">
    <img alt="maven" src="https://img.shields.io/maven-central/v/io.github.pangju666/pangju-framework-bom.svg?style=flat-square">
  </a>

  <a href="https://www.apache.org/licenses/LICENSE-2.0">
    <img alt="code style" src="https://img.shields.io/badge/license-Apache%202-4EB1BA.svg?style=flat-square">
  </a>
</p>

# Pangju Framework 框架

[[_TOC_]]

## 项目简介

Pangju Framework 是一个基于 Spring Frameowrk拓展而来的业务型框架。
该框架通过模块化设计，实现Web模块和多个数据操作模块，包括 MyBatis-Plus、MongoDB 和 Redis，并提供了一系列实用工具，帮助开发者更高效地开发项目。

## 项目结构

框架由以下核心模块组成：

- **pangju-framework-all**：聚合所有模块的统一引入点
- **pangju-framework-bom**：依赖版本管理模块
- **pangju-framework-web**：Spring Framework Web 集成与扩展
- **pangju-framework-data-mongodb**： Spring Data MongoDB 集成与扩展
- **pangju-framework-data-mybatis-plus**：MyBatis-Plus-Spring 集成与扩展
- **pangju-framework-data-redis**：Spring Data Redis 集成与扩展

## 快速开始

### 引入依赖

在您的 Maven 项目中添加以下依赖管理配置：

``` xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>io.github.pangju666</groupId>
            <artifactId>pangju-framework-bom</artifactId>
            <version>最新版本</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

### 添加所需模块

根据您的需求添加相应的模块依赖：

``` xml
<dependencies>
    <!-- 引入全部模块 -->
    <dependency>
        <groupId>io.github.pangju666</groupId>
        <artifactId>pangju-framework-all</artifactId>
        <version>最新版本</version>
    </dependency>
    
    <!-- 或者按需引入特定模块 -->
    <dependency>
        <groupId>io.github.pangju666</groupId>
        <artifactId>pangju-framework-web</artifactId>
    </dependency>
    <dependency>
        <groupId>io.github.pangju666</groupId>
        <artifactId>pangju-framework-data-mongodb</artifactId>
    </dependency>
    <dependency>
        <groupId>io.github.pangju666</groupId>
        <artifactId>pangju-framework-data-mybatis-plus</artifactId>
    </dependency>
    <dependency>
        <groupId>io.github.pangju666</groupId>
        <artifactId>pangju-framework-data-redis</artifactId>
    </dependency>
</dependencies>
```

## 模块说明

### 1. pangju-framework-web（Web 模块）

**简介**：基于 `Spring MVC` 的 Web 辅助模块。

**功能**：

- RestClientHelper ：RestClient辅助类
    - 提供流式API风格的HTTP请求构建器，简化RestClient的使用
    - 支持URI构建、请求头管理、请求体处理和响应处理
    - 支持多种响应类型转换（图片、资源、字节数组、字符串等）
    - 支持JSON、表单数据、文本、二进制等多种请求体格式
- IpUtils ：IP地址处理工具类
    - 处理多级代理IP地址
    - 判断IP地址是否为未知地址
    - 提供IP地址比较功能
- RequestUtils ：HTTP请求工具类
    - 客户端信息识别（判断请求来源是移动设备还是Ajax）
    - IP地址获取（从各种代理头中提取真实客户端IP）
    - 请求参数和头信息处理（获取并转换为标准数据结构）
    - 文件上传处理（获取multipart请求中的文件部分）
    - 请求体内容处理（支持获取原始、字符串和JSON格式的请求体）
- ResponseUtils ：HTTP响应工具类
    - 统一响应对象构建
    - 响应状态和内容设置
- RangeDownloadUtils ：范围下载工具类
    - 支持HTTP范围请求（Range Request）处理
    - 提供文件分片下载功能
    - 支持断点续传和多线程下载
- 异常处理 ：
    - 自定义业务异常体系，支持错误码
    - 支持通过注解（@HttpException）配置异常信息
- 过滤器 ：
    - 请求包装过滤器（ContentCachingWrapperFilter），支持请求体的多次读取
    - 跨域过滤器（CorsFilter），简化跨域资源共享(CORS)配置
    - HTTP异常信息过滤器（HttpExceptionFilter），提供异常信息查询端点
    - 基础HTTP过滤器（BaseHttpOncePerRequestFilter），支持路径排除配置
- 拦截器 ：
    - BaseHttpHandlerInterceptor：请求拦截器基类，提供基础的请求拦截功能
        - 支持配置拦截器执行顺序
        - 支持配置拦截路径模式
        - 支持配置排除路径模式
- 数据传输对象 ：
    - ListDTO：通用列表数据传输对象
    - RequiredListDTO：必填列表数据传输对象
    - UniqueListDTO：唯一列表数据传输对象
    - RequiredUniqueListDTO：必填且唯一的列表数据传输对象
    - RequiredStringListDTO：必填字符串列表数据传输对象
    - RequiredUniqueStringListDTO：必填且唯一的字符串列表数据传输对象
- 错误处理模型 ：
    - DataOperationError：数据操作错误记录
    - HttpRemoteServiceError：远程服务错误信息记录
- 通用模型 ：
    - Result：统一响应结果封装
    - Range：HTTP范围请求模型，支持部分内容请求
    - EnumVO：枚举值对象，用于前端展示
    - HttpExceptionVO：HTTP异常信息值对象
- 常量池 ：
    - WebConstants：Web相关常量定义，包括成功/失败状态码、默认消息等
- 枚举类型 ：
    - HttpExceptionType：HTTP异常类型枚举，定义了系统中所有HTTP异常的分类类型
        - SERVER：服务器内部错误（基础码：5000）
        - SERVICE：业务逻辑错误（基础码：1000）
        - DATA_OPERATION：数据操作错误（基础码：2000）
        - AUTHENTICATION：认证错误（基础码：3000）
        - VALIDATION：参数校验错误（基础码：4000）
        - CUSTOM：自定义错误（基础码：6000）
        - UNKNOWN：未知错误（固定错误码：-1）

### 2. pangju-framework-data-mongodb（MongoDB 模块）

**简介**：基于 `Spring Data MongoDB` 的数据操作辅助模块。
功能 ：

- 基础文档类 ：
    - BaseDocument ：MongoDB基础文档类
        - 提供通用的ID处理功能
        - 使用ObjectId的十六进制字符串作为文档ID
        - 提供集合ID提取方法（getIdList、getIdSet、getUniqueIdList）
- 注解 ：
    - @MongoId ：MongoDB ObjectId格式校验注解，验证字符串是否符合MongoDB的ObjectId格式
    - @MongoIds ：MongoDB ObjectId集合格式校验注解，验证字符串集合中的每个元素是否符合MongoDB的ObjectId格式，支持配置notEmpty参数控制是否允许空集合
- 仓库类 ：
    - MongoBaseRepository ：基础仓库类，扩展MongoRepository功能
        - 提供基本的CRUD操作（insert、save、getById、removeById等）
        - 支持按字段值查询（getByKeyValue）
        - 支持正则表达式查询（listByRegex）
        - 支持批量操作（batchInsert、batchSave、batchRemove等）
- 数据传输对象 ：
    - MongoIdListDTO ：MongoDB ObjectId列表数据传输对象
    - MongoIdRequiredListDTO ：必填MongoDB ObjectId列表数据传输对象
- 工具类 ：
    - QueryUtils ：MongoDB查询工具类，简化Query对象的构建和操作
- 常量池 ：
    - MongoConstants ：MongoDB相关常量定义，如ID字段名称

### 3. pangju-framework-data-mybatis-plus（Mybatis Plus 模块）

**简介**：基于 `Mybatis Plus` 的数据操作辅助模块。

**功能**：

- 基础实体类 ：
    - BasicEntity ：提供基础的创建时间和更新时间字段
    - LogicBasicEntity ：支持逻辑删除功能的基础实体类
    - LogicTimeBasicEntity ：支持时间戳逻辑删除功能的基础实体类
    - VersionBasicEntity ：支持乐观锁功能的基础实体类
    - VersionLogicTimeBasicEntity ：同时支持乐观锁和时间戳逻辑删除功能的基础实体类
- ID类型实体类 ：
    - AutoIdBasicEntity/AutoIdLogicBasicEntity/AutoIdLogicTimeBasicEntity ：使用自增ID的各类基础实体
    - UUIdBasicEntity/UUIdLogicBasicEntity/UUIdLogicTimeBasicEntity ：使用UUID的各类基础实体
    - SnowflakeIdBasicEntity/SnowflakeIdLogicBasicEntity/SnowflakeIdLogicTimeBasicEntity ：使用雪花算法ID的各类基础实体
- 数据传输对象 ：
    - AutoIdListDTO/AutoIdRequiredListDTO ：自增ID列表数据传输对象
    - UUIdListDTO/UUIdRequiredListDTO ：UUID列表数据传输对象
    - SnowflakeIdListDTO/SnowflakeIdRequiredListDTO ：雪花算法ID列表数据传输对象
- 仓库类 ：
    - BaseRepository ：基础仓库类，扩展MyBatis-Plus的BaseMapper功能
        - 提供更丰富的查询方法
        - 优化批量操作和批量查询
        - 简化常用数据操作
    - BaseViewRepository ：视图基础仓库类，专门用于处理数据库视图的只读访问
        - 防止对视图进行写操作
- 注解 ：
    - @SnowflakeId/@SnowflakeIds ：雪花算法ID校验注解
    - @UUId/@UUIds ：UUID格式校验注解
    - @AutoId/@AutoIds ：自增ID校验注解
    - @TableLogicFill ：表字段逻辑删除自动填充注解
- 验证器 ：
    - SnowflakeIdValidator/SnowflakeIdsValidator ：雪花算法ID校验器
    - UuIdValidator/UuIdsValidator ：UUID校验器
    - AutoIdValidator/AutoIdsValidator ：自增ID校验器
      类型处理器 ：
    - BigDecimalVarcharToListTypeHandler ：BigDecimal类型的VARCHAR转List类型处理器
        - 支持BigDecimal列表与字符串的互相转换
    - IntegerVarcharToListTypeHandler ：Integer类型的VARCHAR转List类型处理器
        - 支持整数列表与字符串的互相转换
    - LongVarcharToListTypeHandler ：Long类型的VARCHAR转List类型处理器
        - 支持长整数列表与字符串的互相转换
    - StringVarcharToListTypeHandler ：String类型的VARCHAR转List类型处理器
        - 支持字符串列表与单一字符串的互相转换
    - GenericsVarcharToListTypeHandler ：通用的VARCHAR转List类型处理器
        - 支持任意类型列表与字符串的互相转换
    - JsonTypeHandler ：JSON类型处理器，支持JSON字符串与复杂对象的转换
        - 自动处理JSON序列化和反序列化
        - 支持复杂对象结构

### 4. pangju-framework-data-redis（Redis 模块）

**简介**：基于 `Spring Data Redis` 的数据操作辅助模块。

功能 ：

- 增强模板类 ：
    - ScanRedisTemplate ：支持游标扫描操作的Redis模板类
    - StringScanRedisTemplate ：字符串序列化的Redis模板类，专门用于处理字符串格式数据
    - JsonScanRedisTemplate ：JSON序列化的Redis模板类，专门用于处理JSON格式数据
    - JavaScanRedisTemplate ：Java对象序列化的Redis模板类
- 常量池 ：
    - RedisConstants：Redis相关常量定义
        - 定义键名分隔符
        - 定义扫描模式符号
        - 提供其他Redis操作相关常量
- 工具类 ：
    - RedisUtils ：Redis操作工具类
        - 提供键名组合功能，使用统一分隔符组合多级键名
        - 支持多种扫描选项构建（前缀匹配、后缀匹配、关键字匹配）
        - 支持按Redis数据类型筛选
- 数据模型 ：
    - ZSetValue：有序集合值对象
        - 封装有序集合的值和分数
        - 简化有序集合的操作

## 版本说明

详细的版本更新记录请参阅 [CHANGELOG.md](CHANGELOG.md)。

## 许可证

本项目采用 Apache License 2.0 许可证 - 详情请参阅 [LICENSE](LICENSE) 文件。

## 常见问题

### 模块之间的依赖关系是怎样的？

- 依赖所有其他模块 `pangju-framework-all`
- 其他模块可以单独使用，也可以组合使用
- 仅提供依赖版本管理，不包含实际代码 `pangju-framework-bom`

## 致谢

感谢所有为项目做出贡献的开发者，以及项目所使用的开源框架和工具。
