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

##### The document is translated by Google. If there is any error, please contact me to modify it.

# Pangju Framework

[[_TOC_]]

## Project Introduction

Pangju Framework is a business-oriented framework based on Spring Frameowrk.
The framework implements web modules and multiple data operation modules through modular design, including MyBatis-Plus,
MongoDB and Redis, and provides a series of practical tools to help developers develop projects more efficiently.

## Project structure

The framework consists of the following core modules:

- **pangju-framework-all**: A unified introduction point for aggregating all modules
- **pangju-framework-bom**: Depend on version management module
- **pangju-framework-web**: Spring Framework Web Integration and Extension
- **pangju-framework-data-mongodb**: Spring Data MongoDB integration and extension
- **pangju-framework-data-mybatis-plus**: MyBatis-Plus-Spring integration and extension
- **pangju-framework-data-redis**: Spring Data Redis integration and extension

## Start quickly

### Introduce dependencies

Add the following dependency management configuration to your Maven project:

``` xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>io.github.pangju666</groupId>
            <artifactId>pangju-framework-bom</artifactId>
            <version>Latest version</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

### Add required modules

Add corresponding module dependencies according to your needs:

``` xml
<dependencies>
    <!-- Introduce all modules -->
    <dependency>
        <groupId>io.github.pangju666</groupId>
        <artifactId>pangju-framework-all</artifactId>
        <version>Latest version</version>
    </dependency>
    
    <!-- Or introduce specific modules as needed -->
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

## Module Description

### 1. pangju-framework-web (Web module)

**Introduction**: Based on`Spring MVC`Web assist module.

**Function**:

- RestClientHelper: RestClient auxiliary class
    - Provides a streaming API-style HTTP request builder to simplify the use of RestClient
    - Support URI construction, request header management, request body processing and response processing
    - Supports multiple response type conversions (image, resource, byte array, string, etc.)
    - Supports various request formats such as JSON, form data, text, binary, etc.
- IpUtils: IP address processing tool class
    - Handle multi-level proxy IP addresses
    - Determine whether the IP address is an unknown address
    - Provide IP address comparison function
- RequestUtils: HTTP request tool class
    - Client information identification (judging whether the request is the source of the mobile device or Ajax)
    - IP address acquisition (extract real client IP from various proxy headers)
    - Request parameters and header information processing (get and convert to standard data structures)
    - File upload processing (get the file part in the multipart request)
    - Request body content processing (supports to obtain request bodies in original, string and JSON format)
- ResponseUtils: HTTP response tool class
    - Unified response object construction
    - Response status and content settings
- RangeDownloadUtils: RangeDownloadUtils class
    - Support HTTP range request processing
    - Provide file shard download function
    - Support breakpoint continuous transmission and multi-threaded download
- Exception handling:
    - Customized business exception system, support error code
    - Supports configuring exception information through annotation (@HttpException)
- Filter:
    - Request wrapper filter (ContentCachingWrapperFilter), supports multiple reads of request bodies
    - Cross-domain filter (CorsFilter), simplifying cross-domain resource sharing (CORS) configuration
    - HTTP exception information filter (HttpExceptionFilter), providing exception information query endpoints
    - Basic HTTP filter (BaseHttpOncePerRequestFilter), supports path exclusion configuration
- Interceptor:
    - BaseHttpHandlerInterceptor: Base class for request interceptor, providing basic request intercepting function
        - Supports configuring the interceptor execution order
        - Supports configuration of intercept path mode
        - Supports configuration of exclusion path mode
- Data transfer object:
    - ListDTO: General list data transfer object
    - RequiredListDTO: Required list data transfer object
    - UniqueListDTO: Unique List Data Transfer Object
    - RequiredUniqueListDTO: Required and unique list data transfer object
    - RequiredStringListDTO: Required string list data transfer object
    - RequiredUniqueStringListDTO: Required and unique string list data transfer object
- Error handling model:
    - DataOperationError: Data operation error logging
    - HttpRemoteServiceError: Remote Service Error Message Record
- General Model:
    - Result: Unified response result encapsulation
    - Range: HTTP range request model, supporting some content requests
    - EnumVO: Enum value object for front-end display
    - HttpExceptionVO: HTTP exception information value object
- Constant pool:
    - WebConstants: Web-related constant definition, including success/failure status code, default message, etc.
- Enumeration type:
    - HttpExceptionType: HTTP exception type enumeration, defines the classification types of all HTTP exceptions in the
      system
        - SERVER: Internal error of server (base code: 5000)
        - SERVICE: Business logic error (base code: 1000)
        - DATA_OPERATION: Data operation error (base code: 2000)
        - AUTHENTICATION: Authentication error (base code: 3000)
        - VALIDATION: Parameter verification error (base code: 4000)
        - CUSTOM: Custom error (base code: 6000)
        - UNKNOWN: Unknown error (fixed error code: -1)

### 2. pangju-framework-data-mongodb (MongoDB module)

**Introduction**: Based on`Spring Data MongoDB`data operation auxiliary module.
Function :

- Basic Document Class:
    - BaseDocument: MongoDB basic document class
        - Provides common ID processing functions
        - Use the hexadecimal string of ObjectId as document ID
        - Provide collection ID extraction methods (getIdList, getIdSet, getUniqueIdList)
- Note:
    - @MongoId: MongoDB ObjectId format verification annotation to verify whether the string complies with MongoDB's
      ObjectId format
    - @MongoIds: MongoDB ObjectId collection format verification annotation, verify that each element in the string
      collection complies with the MongoDB ObjectId format, and supports configuration notEmpty parameter to control
      whether empty collections are allowed
- Warehouse category:
    - MongoBaseRepository: Basic repository class, extending MongoRepository functions
        - Provide basic CRUD operations (insert, save, getById, removeById, etc.)
        - Supports query by field value (getByKeyValue)
        - Support regular expression query (listByRegex)
        - Support batch operations (batchInsert, batchSave, batchRemove, etc.)
- Data transfer object:
    - MongoIdListDTO: MongoDB ObjectId list data transfer object
    - MongoIdRequiredListDTO: Required MongoDB ObjectId list data transfer object
- Tools:
    - QueryUtils: MongoDB query tool class, simplifies the construction and operation of Query objects
- Constant pool:
    - MongoConstants: MongoDB related constant definition, such as ID field name

### 3. pangju-framework-data-mybatis-plus (Mybatis Plus module)

**Introduction**: Based on`Mybatis Plus`data operation auxiliary module.

**Function**:

- Basic entity class:
    - BasicDO: Provides basic creation time and update time fields
    - LogicBasicDO: Basic entity class that supports logical deletion function
    - LogicTimeBasicDO: Basic entity class that supports the time stamp logic deletion function
    - VersionBasicDO: Basic entity class that supports optimistic locking function
    - VersionLogicTimeBasicDO: Basic entity class that supports both optimistic locking and timestamp logic deletion
      functions
- ID type entity class:
    - AutoIdBasicDO/AutoIdLogicBasicDO/AutoIdLogicTimeBasicDO: Various basic entities that use self-increase IDs
    - UUIdBasicDO/UUIdLogicBasicDO/UUIdLogicTimeBasicDO: Various basic entities that use UUID
    - SnowflakeIdBasicDO/SnowflakeIdLogicBasicDO/SnowflakeIdLogicTimeBasicDO: Various basic entities using snowflake
      algorithm ID
- Data transfer object:
    - AutoIdListDTO/AutoIdRequiredListDTO: Auto-increment ID list data transmission object
    - UUIdListDTO/UUIdRequiredListDTO: UUID list data transfer object
    - SnowflakeIdListDTO/SnowflakeIdRequiredListDTO: Snowflake algorithm ID list data transmission object
- Warehouse category:
    - BaseRepository: Basic repository class, extending the BaseMapper function of MyBatis-Plus
        - Provide richer query methods
        - Optimize batch operations and batch queries
        - Simplify common data operations
    - BaseViewRepository: View base repository class, specially used to handle read-only access to database views
        - Prevent views from writing
- Note:
    - @SnowflakeId/@SnowflakeIds: Snowflake Algorithm ID verification annotation
    - @UUId/@UUIds: UUID format verification annotation
    - @AutoId/@AutoIds: Self-increasing ID verification annotation
    - @TableLogicFill: Table field logic delete automatic fill annotation
- Verifier:
    - SnowflakeIdValidator/SnowflakeIdsValidator: Snowflake algorithm ID verification device
    - UuIdValidator/UuIdsValidator: UUID Verifier
    - AutoIdValidator/AutoIdsValidator: AutoIdID Verifier
      Type Processor:
    - BigDecimalVarcharToListTypeHandler: BigDecimal type VARCHAR to List type processor
        - Supports the conversion of BigDecimal lists and strings
    - IntegerVarcharToListTypeHandler: VARCHAR to List type processor of Integer type
        - Supports the conversion of integer lists and strings
    - LongVarcharToListTypeHandler: VARCHAR to List type processor of Long type
        - Supports the conversion of long integer lists and strings
    - StringVarcharToListTypeHandler: String type VARCHAR to List type processor
        - Supports the conversion of string lists and single strings
    - GenericsVarcharToListTypeHandler: General VARCHAR to List type processor
        - Supports conversion of arbitrary type lists and strings
    - JsonTypeHandler: JSON type processor, supports conversion of JSON strings and complex objects
        - Automatically handle JSON serialization and deserialization
        - Support complex object structures

### 4. pangju-framework-data-redis (Redis module)

**Introduction**: Based on`Spring Data Redis`data operation auxiliary module.

Function :

- Enhanced template class:
    - ScanRedisTemplate: Redis template class that supports cursor scanning operations
    - StringScanRedisTemplate: Redis template class for string serialization, specifically used to process string format
      data
    - JsonScanRedisTemplate: JSON serialized Redis template class, specially used to process JSON format data
    - JavaScanRedisTemplate: Redis template class for Java object serialization
- Constant pool:
    - RedisConstants: Redis-related constant definition
        - Define key name separator
        - Define the scan pattern symbol
        - Provide other Redis operations related constants
- Tools:
    - RedisUtils: Redis operation tool class
        - Provide key name combination function, use unified separator to combine multi-level key names
        - Supports multiple scanning options to build (prefix matching, suffix matching, keyword matching)
        - Support filtering by Redis data type
- Data Model:
    - ZSetValue: Ordered collection value object
        - Encapsulate the values ​​and fractions of ordered sets
        - Simplify the operation of ordered collections

## Version Description

For detailed version update records, please refer to[CHANGELOG.md](CHANGELOG.md)。

## license

This project is licensed under the Apache License 2.0 - please see[LICENSE](LICENSE)document.

## Frequently Asked Questions

### What are the dependencies between modules?

- Rely on all other modules`pangju-framework-all`
- Other modules can be used individually or in combination
- Only provide dependency version management, does not include actual code`pangju-framework-bom`

## Acknowledgements

Thanks to all the developers who contributed to the project, as well as the open source frameworks and tools used by the
project.
