/*
 *   Copyright 2025 pangju666
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package io.github.pangju666.framework.web.client;

import com.google.gson.JsonElement;
import io.github.pangju666.commons.lang.pool.Constants;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import java.awt.image.BufferedImage;
import java.net.URI;
import java.util.*;

/**
 * RestClient辅助类
 * <p>
 * 提供流式API风格的HTTP请求构建器，简化RestClient的使用。支持以下功能：
 * <ul>
 *     <li>URI构建：支持路径、查询参数、URI变量的设置</li>
 *     <li>请求头管理：支持单个或批量添加请求头</li>
 *     <li>请求体处理：支持JSON、表单数据、文本、二进制、{@link Resource 资源}等多种格式</li>
 *     <li>响应处理：支持多种响应类型的转换（JSON、{@link BufferedImage 图像}、{@link Resource 资源}、二进制、文本等）</li>
 * </ul>
 * </p>
 *
 * <p>使用示例</p>
 * <pre>{@code
 *     // 使用RestClient原生方法获取返回值
 *     Result result = RestClientHelper.fromUriString(restClient, "https://api.example.com")
 *     .path("/api/test/{id}") // 可选，可以多次调用添加多个路径
 *     .method(HttpMethod.POST) // 可选，默认为HttpMethod.GET
 *     .header("Authorization", "Bearer token") // 可选，可以多次调用添加多个请求头
 *     .queryParam("param", 123) // 可选，可以多次调用添加多个请求参数
 *     .uriVariable("id", 1) // 可选，可以多次调用添加多个路径模板参数
 *     .jsonBody(new User("admin", "password")) // 可选，只能调用一次，重复调用会覆盖之前的body
 *     .buildRequest() // 返回 RestClient.RequestBodySpec
 *     .retrieve()
 *     .accept(MediaType.APPLICATION_JSON)
 *     .toEntity(Result.class);
 *
 *     // 使用RestClientHelper封装的方法获取返回值
 *     Result result = RestClientHelper.fromUriString(restClient, "https://api.example.com")
 *     .path("/api/test/{id}") // 可选，可以多次调用添加多个路径
 *     .method(HttpMethod.POST) // 可选，默认为HttpMethod.GET
 *     .header("Authorization", "Bearer token") // 可选，可以多次调用添加多个请求头
 *     .queryParam("param", 123) // 可选，可以多次调用添加多个请求参数
 *     .uriVariable("id", 1) // 可选，可以多次调用添加多个路径模板参数
 *     .jsonBody(new User("admin", "password")) // 可选，只能调用一次，重复调用会覆盖之前的body
 *     .toJsonEntity(Result.class);
 *
 * 	   // 使用RestClientHelper封装的方法获取返回值
 *     Result result = RestClientHelper.fromUriString(restClient, "https://api.example.com")
 *     .path("/api/test/{id}") // 可选，可以多次调用添加多个路径
 *     .method(HttpMethod.POST) // 可选，默认为HttpMethod.GET
 *     .header("Authorization", "Bearer token") // 可选，可以多次调用添加多个请求头
 *     .queryParam("param", 123) // 可选，可以多次调用添加多个请求参数
 *     .uriVariable("id", 1) // 可选，可以多次调用添加多个路径模板参数
 *     .jsonBody(new User("admin", "password")) // 可选，只能调用一次，重复调用会覆盖之前的body
 *     .toEntity(Result.class, MediaType.APPLICATION_JSON);
 *
 *     // 使用RestClientHelper封装的方法获取无响应体结果
 *     RestClientHelper.fromUriString(restClient, "https://api.example.com")
 *     .path("/api/test/{id}") // 可选，可以多次调用添加多个路径
 *     .method(HttpMethod.POST) // 可选，默认为HttpMethod.GET
 *     .header("Authorization", "Bearer token") // 可选，可以多次调用添加多个请求头
 *     .queryParam("param", 123) // 可选，可以多次调用添加多个请求参数
 *     .uriVariable("id", 1) // 可选，可以多次调用添加多个路径模板参数
 *     .jsonBody(new User("admin", "password")) // 可选，只能调用一次，重复调用会覆盖之前的body
 *     .toBodilessEntity();
 *
 *     // 使用RestClientHelper封装的方法返回字节数组
 *     byte[] bytes = RestClientHelper.fromUriString(restClient, "https://api.example.com")
 *     .path("/api/download/{id}") // 可选，可以多次调用添加多个路径
 *     .method(HttpMethod.POST) // 可选，默认为HttpMethod.GET
 *     .header("Authorization", "Bearer token") // 可选，可以多次调用添加多个请求头
 *     .queryParam("param", 123) // 可选，可以多次调用添加多个请求参数
 *     .uriVariable("id", 1) // 可选，可以多次调用添加多个路径模板参数
 *     .jsonBody(new User("admin", "password")) // 可选，只能调用一次，重复调用会覆盖之前的body
 *     .toBytesEntity();
 *
 * 	   // 使用RestClientHelper封装的方法返回输入流
 *     InputStream inputStream = RestClientHelper.fromUriString(restClient, "https://api.example.com")
 *     .path("/api/download/{id}") // 可选，可以多次调用添加多个路径
 *     .method(HttpMethod.POST) // 可选，默认为HttpMethod.GET
 *     .header("Authorization", "Bearer token") // 可选，可以多次调用添加多个请求头
 *     .queryParam("param", 123) // 可选，可以多次调用添加多个请求参数
 *     .uriVariable("id", 1) // 可选，可以多次调用添加多个路径模板参数
 *     .jsonBody(new User("admin", "password")) // 可选，只能调用一次，重复调用会覆盖之前的body
 *     .toResourceEntity()
 *     .getInputStream();
 *
 *     // 使用RestClientHelper封装的方法上传文件
 *     RestClientHelper.fromUriString(restClient, "https://api.example.com")
 *     .path("/api/upload/{id}") // 可选，可以多次调用添加多个路径
 *     .method(HttpMethod.POST) // 可选，默认为HttpMethod.GET
 *     .header("Authorization", "Bearer token") // 可选，可以多次调用添加多个请求头
 *     .uriVariable("id", 1) // 可选，可以多次调用添加多个路径模板参数
 *     .formData("file", new FileSystemResource(new File("xxxx"))) // 可选，可以多次调用添加多个表单参数
 *     .toBodilessEntity();
 * }</pre>
 *
 * @author pangju666
 * @see RestClient
 * @since 1.0.0
 */
public class RestClientHelper {
    /**
     * 表单媒体类型集合，包含Spring支持的表单数据类型
     *
     * @since 1.0.0
     * @see FormHttpMessageConverter#getSupportedMediaTypes()
     */
	public static final Set<MediaType> FORM_MEDIA_TYPES = Set.of(MediaType.MULTIPART_FORM_DATA,
		MediaType.MULTIPART_MIXED, MediaType.MULTIPART_RELATED);

    /**
     * RestClient实例，用于执行HTTP请求
     *
     * @since 1.0.0
     */
    protected final RestClient restClient;
    /**
     * URI构建器，用于构建请求URI
     *
     * @since 1.0.0
     */
    protected final UriComponentsBuilder uriComponentsBuilder;

    /**
     * 请求头集合
     *
     * @since 1.0.0
     */
    protected final HttpHeaders headers = new HttpHeaders();
    /**
     * URI变量映射，用于替换URI模板中的变量
     *
     * @since 1.0.0
     */
    protected final Map<String, Object> uriVariables = new HashMap<>(4);
    /**
     * 表单数据集合，用于构建表单请求体
     *
     * @since 1.0.0
     */
    protected final MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();

    /**
     * HTTP请求方法，默认为GET
     *
     * @since 1.0.0
     */
    protected HttpMethod method = HttpMethod.GET;
    /**
     * 内容类型，默认为application/x-www-form-urlencoded
     *
     * @since 1.0.0
     */
    protected MediaType contentType = MediaType.APPLICATION_FORM_URLENCODED;
    /**
     * 请求体
     *
     * @since 1.0.0
     */
    protected Object body = null;

    /**
     * 使用RestClient实例和URI构建器构造辅助类
     * <p>
     * 该构造方法为protected，推荐使用静态工厂方法创建实例
     * </p>
     *
     * @param restClient           RestClient实例
     * @param uriComponentsBuilder URI构建器
     * @since 1.0.0
     */
    protected RestClientHelper(RestClient restClient, UriComponentsBuilder uriComponentsBuilder) {
        this.restClient = restClient;
        this.uriComponentsBuilder = uriComponentsBuilder;
    }

    /**
     * 从URI字符串创建RestClientHelper实例
     *
     * @param restClient RestClient实例
     * @param uriString  URI字符串（可选），例如：{@code "https://api.example.com/users"}
     * @return 新的RestClientHelper实例
     * @throws IllegalArgumentException 当restClient为null时抛出
     * @since 1.0.0
     */
    public static RestClientHelper fromUriString(RestClient restClient, String uriString) {
        Assert.notNull(restClient, "restClient 不可为null");

        if (StringUtils.isNotBlank(uriString)) {
            return new RestClientHelper(restClient, UriComponentsBuilder.fromUriString(uriString));
        } else {
            return new RestClientHelper(restClient, UriComponentsBuilder.newInstance());
        }
    }

    /**
     * 从URI对象创建RestClientHelper实例
     *
     * @param restClient RestClient实例
     * @param uri        URI对象（可选），例如：{@code new URI("https://api.example.com/users")}
     * @return 新的RestClientHelper实例
     * @throws IllegalArgumentException 当restClient为null时抛出
     * @since 1.0.0
     */
    public static RestClientHelper fromUri(RestClient restClient, URI uri) {
        Assert.notNull(restClient, "restClient 不可为null");

        if (Objects.nonNull(uri)) {
            return new RestClientHelper(restClient, UriComponentsBuilder.fromUri(uri));
        } else {
            return new RestClientHelper(restClient, UriComponentsBuilder.newInstance());
        }
    }

    /**
     * 设置HTTP请求方法
     *
     * @param method HTTP方法，例如：{@code HttpMethod.POST}
     * @return 当前实例
     * @since 1.0.0
     */
    public RestClientHelper method(HttpMethod method) {
        if (Objects.nonNull(method)) {
            this.method = method;
        }
        return this;
    }

    /**
     * 添加请求路径
     *
     * @param path 请求路径，例如：{@code "/api/users"}
     * @return 当前实例
     * @since 1.0.0
     * @see UriComponentsBuilder#path(String)
     */
    public RestClientHelper path(String path) {
        if (StringUtils.isNotBlank(path)) {
            this.uriComponentsBuilder.path(path);
        }
        return this;
    }

	/**
     * 添加单个查询参数
     *
     * @param name   参数名，例如：{@code "page"}
     * @param values 参数值数组，例如：{@code 1, 2, 3}
     * @return 当前实例
     * @throws IllegalArgumentException 当name为空时抛出
     * @since 1.0.0
     * @see UriComponentsBuilder#queryParam(String, Object...)
     */
    public RestClientHelper queryParam(String name, @Nullable Object... values) {
        Assert.hasText(name, "name 不可为空");

        this.uriComponentsBuilder.queryParam(name, values);
        return this;
    }

    /**
     * 批量添加查询参数
     *
     * @param params 参数映射，例如：{@code new LinkedMultiValueMap<>()}
     * @return 当前实例
     * @since 1.0.0
     * @see UriComponentsBuilder#queryParams(MultiValueMap)
     */
    public RestClientHelper queryParams(@Nullable MultiValueMap<String, String> params) {
        this.uriComponentsBuilder.queryParams(params);
        return this;
    }

    /**
     * 批量添加查询参数（Map形式）
     *
     * @param params 参数映射，例如：{@code Map.of("page", 1, "size", 10)}
     * @return 当前实例
     * @since 1.0.0
     * @see UriComponentsBuilder#queryParam(String, Object...)
     */
    public RestClientHelper queryParams(@Nullable Map<String, Object> params) {
        if (!CollectionUtils.isEmpty(params)) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                if (Objects.nonNull(entry.getValue()) && entry.getValue().getClass().isArray()) {
                    this.uriComponentsBuilder.queryParam(entry.getKey(), (Object[]) entry.getValue());
                } else {
                    this.uriComponentsBuilder.queryParam(entry.getKey(), entry.getValue());
                }
            }
        }
        return this;
    }

    /**
     * 设置原始查询字符串
     *
     * @param query 查询字符串，例如：{@code "page=1&size=10&sort=name,asc"}
     * @return 当前实例
     * @since 1.0.0
     * @see UriComponentsBuilder#query(String)
     */
    public RestClientHelper query(@Nullable String query) {
        this.uriComponentsBuilder.query(query);
        return this;
    }

    /**
     * 添加单个URI变量
     *
     * @param name  变量名，例如：{@code "id"}
     * @param value 变量值，例如：{@code 123}
     * @return 当前实例
     * @throws IllegalArgumentException 当name为空时抛出
     * @since 1.0.0
     */
    public RestClientHelper uriVariable(String name, @Nullable Object value) {
        Assert.hasText(name, "name 不可为空");

        if (Objects.nonNull(value)) {
            this.uriVariables.put(name, value);
        }
        return this;
    }

    /**
     * 批量添加URI变量
     *
     * @param uriVariables URI变量映射，例如：{@code Map.of("id", 123, "name", "test")}
     * @return 当前实例
     * @since 1.0.0
     */
    public RestClientHelper uriVariables(@Nullable Map<String, Object> uriVariables) {
        if (!CollectionUtils.isEmpty(uriVariables)) {
            this.uriVariables.putAll(uriVariables);
        }
        return this;
    }

    /**
     * 添加单个请求头
     *
     * @param headerName 请求头名称，例如：{@code "Authorization"}
     * @param headerValue 请求头值，例如：{@code "Bearer token123"}
     * @return 当前实例
     * @throws IllegalArgumentException 当headerName为空时抛出
     * @since 1.0.0
     * @see HttpHeaders#add(String, String)
     * @see Objects#toString(Object, String)
     */
    public RestClientHelper header(String headerName, @Nullable Object headerValue) {
        Assert.hasText(headerName, "headerName 不可为空");

        this.headers.add(headerName, Objects.toString(headerValue, null));
        return this;
    }

    /**
     * 添加多值请求头
     *
     * @param key 请求头名称，例如：{@code "Accept"}
     * @param values 请求头值列表，例如：{@code List.of("application/json", "text/plain")}
     * @return 当前实例
     * @since 1.0.0
     * @see HttpHeaders#addAll(String, List)
     * @see Objects#toString(Object, String)
     */
    public RestClientHelper header(String key, @Nullable List<?> values) {
        if (!CollectionUtils.isEmpty(values)) {
            if (values.size() == 1) {
                this.headers.add(key, Objects.toString(values.get(0), null));
            } else {
                this.headers.addAll(key, values.stream()
                        .map(value -> Objects.toString(value, null))
                        .toList()
                );
            }
        }
        return this;
    }

    /**
     * 批量添加请求头
     *
     * @param headers 请求头映射，例如：{@code new HttpHeaders()}
     * @return 当前实例
     * @since 1.0.0
     * @see HttpHeaders#addAll(MultiValueMap)
     * @see Objects#toString(Object, String)
     */
    public RestClientHelper headers(@Nullable MultiValueMap<String, Object> headers) {
        if (!CollectionUtils.isEmpty(headers)) {
            for (Map.Entry<String, List<Object>> entry : headers.entrySet()) {
                header(entry.getKey(), entry.getValue());
            }
        }
        return this;
    }

    /**
     * 批量添加请求头
     *
     * @param headers 请求头映射，例如：{@code Map.of("Authorization", "Bearer token123", "Accept", "application/json")}
     * @return 当前实例
     * @since 1.0.0
     * @see HttpHeaders#add(String, String)
     * @see Objects#toString(Object, String)
     */
    public RestClientHelper headers(@Nullable Map<String, Object> headers) {
        if (!CollectionUtils.isEmpty(headers)) {
            for (Map.Entry<String, Object> entry : headers.entrySet()) {
                this.headers.add(entry.getKey(), Objects.toString(entry.getValue(), null));
            }
        }
        return this;
    }

    /**
     * 添加资源类型的表单部分
     *
     * @param name  表单字段名，例如：{@code "file"}
     * @param value 资源值，例如：{@code new FileSystemResource(new File("example.txt"))}
     * @return 当前实例
     * @throws IllegalArgumentException 当name为空时抛出
     * @see org.springframework.http.converter.FormHttpMessageConverter
     * @since 1.0.0
     */
	public RestClientHelper formPart(String name, @Nullable Resource value) {
        Assert.hasText(name, "name 不可为空");

        this.contentType = MediaType.MULTIPART_FORM_DATA;
        this.formData.add(name, value);
        return this;
    }

    /**
     * 添加表单字段
     *
     * @param name 表单字段名，例如：{@code "username"}
     * @param value 字段值，例如：{@code "admin"}
     * @return 当前实例
     * @throws IllegalArgumentException 当name为空时抛出
     * @since 1.0.0
     * @see org.springframework.http.converter.FormHttpMessageConverter
     */
	public RestClientHelper formData(String name, @Nullable Object value) {
        Assert.hasText(name, "name 不可为空");

        this.contentType = MediaType.MULTIPART_FORM_DATA;
        this.formData.add(name, value);
        return this;
    }

    /**
     * 批量添加表单数据
     *
     * @param formData 表单数据映射，例如：{@code new LinkedMultiValueMap<>()}
     * @return 当前实例
     * @since 1.0.0
     * @see org.springframework.http.converter.FormHttpMessageConverter
     */
	public RestClientHelper formData(@Nullable MultiValueMap<String, Object> formData) {
        this.contentType = MediaType.MULTIPART_FORM_DATA;
        if (!CollectionUtils.isEmpty(formData)) {
            this.formData.addAll(formData);
        }
        return this;
    }

    /**
     * 设置JSON请求体
     * <p>
     * 当body为null时，默认使用空JSON对象
     * </p>
     *
     * @param body 请求体对象，例如：{@code new User("admin", "password")}
     * @return 当前实例
     * @since 1.0.0
	 * @apiNote 依赖于jackson-bind或gson库
     * @see org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
     * @see org.springframework.http.converter.json.GsonHttpMessageConverter
     */
    public RestClientHelper jsonBody(@Nullable Object body) {
        return jsonBody(body, true);
    }

    /**
     * 设置JSON请求体，可控制null值处理
     *
     * @param body 请求体对象，例如：{@code new User("admin", "password")}
     * @param emptyIfNull 当body为null时是否使用空JSON对象，例如：{@code true}
     * @return 当前实例
     * @since 1.0.0
	 * @apiNote 依赖于jackson-bind或gson库
     * @see org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
     * @see org.springframework.http.converter.json.GsonHttpMessageConverter
     */
    public RestClientHelper jsonBody(@Nullable Object body, boolean emptyIfNull) {
        this.contentType = MediaType.APPLICATION_JSON;
        if (Objects.isNull(body)) {
            this.body = emptyIfNull ? Constants.EMPTY_JSON_OBJECT_STR : null;
        } else {
            if (body instanceof JsonElement jsonElement) {
                this.body = jsonElement.toString();
            } else {
                this.body = body;
            }
        }
        return this;
    }

	/**
	 * 设置文本请求体
	 * <p>
	 * 当body为null时，默认使用空字符串
	 * </p>
	 *
	 * @param body 文本内容，例如：{@code "Hello, World!"}
	 * @return 当前实例
	 * @see org.springframework.http.converter.StringHttpMessageConverter
	 * @since 1.0.0
	 */
    public RestClientHelper textBody(@Nullable String body) {
		return textBody(body, true);
	}

	/**
	 * 设置文本请求体，可控制null值处理
	 *
	 * @param body        文本内容，例如：{@code "Hello, World!"}
	 * @param emptyIfNull 当body为null时是否使用空字符串，例如：{@code true}
	 * @return 当前实例
	 * @see org.springframework.http.converter.StringHttpMessageConverter
	 * @since 1.0.0
	 */
	public RestClientHelper textBody(@Nullable String body, boolean emptyIfNull) {
		this.contentType = MediaType.TEXT_PLAIN;
		this.body = ObjectUtils.defaultIfNull(body, emptyIfNull ? StringUtils.EMPTY : null);
		return this;
	}

	/**
	 * 设置二进制请求体
	 * <p>
	 * 当body为null时，默认使用空字节数组
	 * </p>
	 *
	 * @param body        二进制数据，例如：{@code Files.readAllBytes(Paths.get("example.bin"))}
	 * @return 当前实例
	 * @see org.springframework.http.converter.ByteArrayHttpMessageConverter
	 * @since 1.0.0
	 */
    public RestClientHelper bytesBody(@Nullable byte[] body) {
		return bytesBody(body, true);
	}

	/**
	 * 设置二进制请求体，可控制null值处理
	 *
	 * @param body        二进制数据，例如：{@code Files.readAllBytes(Paths.get("example.bin"))}
	 * @param emptyIfNull 当body为null时是否使用空字节数组，例如：{@code true}
	 * @return 当前实例
	 * @see org.springframework.http.converter.ByteArrayHttpMessageConverter
	 * @since 1.0.0
	 */
	public RestClientHelper bytesBody(@Nullable byte[] body, boolean emptyIfNull) {
		this.contentType = MediaType.APPLICATION_OCTET_STREAM;
		this.body = ObjectUtils.defaultIfNull(body, emptyIfNull ? ArrayUtils.EMPTY_BYTE_ARRAY : null);
		return this;
	}

	/**
	 * 设置资源请求体
	 *
	 * @param body 资源对象，例如：{@code new FileSystemResource(new File("example.txt"))}
	 * @return 当前实例
	 * @see org.springframework.http.converter.ResourceHttpMessageConverter
	 * @since 1.0.0
	 */
	public RestClientHelper resourceBody(@Nullable Resource body) {
		this.contentType = MediaType.APPLICATION_OCTET_STREAM;
		this.body = body;
		return this;
	}

    /**
     * 设置资源请求体，指定媒体类型
     *
     * @param body      资源对象，例如：{@code new FileSystemResource(new File("example.txt"))}
     * @param mediaType 媒体类型，例如：{@code MediaType.APPLICATION_OCTET_STREAM}
     * @return 当前实例
     * @throws IllegalArgumentException 当mediaType为null时抛出
     * @see org.springframework.http.converter.ResourceHttpMessageConverter
     * @since 1.0.0
     */
    public RestClientHelper resourceBody(@Nullable Resource body, MediaType mediaType) {
        Assert.notNull(mediaType, "mediaType 不可为null");

        this.contentType = mediaType;
		this.body = body;
		return this;
	}

	/**
	 * 设置请求体，指定媒体类型
	 *
	 * @param body      请求体对象，例如：{@code new User("admin", "password")}
     * @param mediaType 媒体类型，例如：{@code MediaType.APPLICATION_JSON}
     * @return 当前实例
     * @throws IllegalArgumentException 当mediaType为null时抛出
     * @see org.springframework.http.converter.HttpMessageConverter
     * @since 1.0.0
     */
    public RestClientHelper body(@Nullable Object body, MediaType mediaType) {
        Assert.notNull(mediaType, "mediaType 不可为null");

        this.contentType = mediaType;
        this.body = body;
        return this;
    }

    /**
     * 将请求结果转换为BufferedImage响应实体
     *
     * @return BufferedImage响应实体
     * @throws RestClientResponseException 当请求失败时抛出
     * @see java.awt.image.BufferedImage
     * @since 1.0.0
     */
    public ResponseEntity<BufferedImage> toBufferedImageEntity() throws RestClientResponseException {
        return buildRequestBodySpec()
                .retrieve()
                .toEntity(BufferedImage.class);
    }

    /**
     * 将请求结果转换为BufferedImage响应实体，指定可接受的媒体类型
     *
     * @param acceptableMediaTypes 可接受的媒体类型数组，例如：{@code MediaType.IMAGE_JPEG, MediaType.IMAGE_PNG}
     * @return BufferedImage响应实体
     * @throws RestClientResponseException 当请求失败时抛出
     * @see java.awt.image.BufferedImage
     * @since 1.0.0
     */
    public ResponseEntity<BufferedImage> toBufferedImageEntity(final MediaType... acceptableMediaTypes) throws RestClientResponseException {
        return buildRequestBodySpec()
                .accept(acceptableMediaTypes)
                .retrieve()
                .toEntity(BufferedImage.class);
    }

    /**
     * 将请求结果转换为Resource响应实体
     *
     * @return Resource响应实体
     * @throws RestClientResponseException 当请求失败时抛出
     * @see org.springframework.core.io.Resource
     * @since 1.0.0
     */
	public ResponseEntity<Resource> toResourceEntity() throws RestClientResponseException {
        return buildRequestBodySpec()
                .retrieve()
                .toEntity(Resource.class);
    }

    /**
     * 将请求结果转换为Resource响应实体，指定可接受的媒体类型
     *
     * @param acceptableMediaTypes 可接受的媒体类型数组，例如：{@code MediaType.APPLICATION_OCTET_STREAM}
     * @return Resource响应实体
     * @throws RestClientResponseException 当请求失败时抛出
     * @see org.springframework.core.io.Resource
     * @since 1.0.0
     */
    public ResponseEntity<Resource> toResourceEntity(final MediaType... acceptableMediaTypes) throws RestClientResponseException {
        return buildRequestBodySpec()
                .accept(acceptableMediaTypes)
                .retrieve()
                .toEntity(Resource.class);
    }

    /**
     * 将请求结果转换为字节数组响应实体
     *
     * @return 字节数组响应实体
     * @throws RestClientResponseException 当请求失败时抛出
     * @since 1.0.0
	 */
	public ResponseEntity<byte[]> toBytesEntity() throws RestClientResponseException {
        return buildRequestBodySpec()
                .retrieve()
                .toEntity(byte[].class);
    }

    /**
     * 将请求结果转换为字节数组响应实体，指定可接受的媒体类型
     *
     * @param acceptableMediaTypes 可接受的媒体类型数组，例如：{@code MediaType.APPLICATION_OCTET_STREAM}
     * @return 字节数组响应实体
     * @throws RestClientResponseException 当请求失败时抛出
     * @since 1.0.0
     */
    public ResponseEntity<byte[]> toBytesEntity(final MediaType... acceptableMediaTypes) throws RestClientResponseException {
        return buildRequestBodySpec()
                .accept(acceptableMediaTypes)
                .retrieve()
                .toEntity(byte[].class);
    }

    /**
     * 将请求结果转换为字符串响应实体
     *
     * @return 字符串响应实体
     * @throws RestClientResponseException 当请求失败时抛出
     * @since 1.0.0
	 */
	public ResponseEntity<String> toStringEntity() throws RestClientResponseException {
        return buildRequestBodySpec()
                .retrieve()
                .toEntity(String.class);
    }

    /**
     * 将请求结果转换为字符串响应实体，指定可接受的媒体类型
     *
     * @param acceptableMediaTypes 可接受的媒体类型数组，例如：{@code MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON}
     * @return 字符串响应实体
     * @throws RestClientResponseException 当请求失败时抛出
     * @since 1.0.0
     */
    public ResponseEntity<String> toStringEntity(final MediaType... acceptableMediaTypes) throws RestClientResponseException {
        return buildRequestBodySpec()
                .accept(acceptableMediaTypes)
                .retrieve()
                .toEntity(String.class);
    }

    /**
     * 将请求结果转换为指定类型的JSON响应实体
     * <p>
     * 自动设置Accept头为{@code application/json}
     * </p>
     *
     * @param bodyType 响应体类型，例如：{@code User.class}
     * @param <T>      响应体类型
     * @return 指定类型的JSON响应实体
     * @throws RestClientResponseException 当请求失败时抛出
     * @throws IllegalArgumentException    当bodyType为null时抛出
     * @see org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
     * @see org.springframework.http.converter.json.GsonHttpMessageConverter
     * @since 1.0.0
     */
    public <T> ResponseEntity<T> toJsonEntity(Class<T> bodyType) throws RestClientResponseException {
		Assert.notNull(bodyType, "bodyType 不可为null");

        return buildRequestBodySpec()
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntity(bodyType);
    }

    /**
     * 将请求结果转换为指定泛型类型的JSON响应实体
     * <p>
     * 自动设置Accept头为{@code application/json}，适用于需要处理泛型的场景，如列表或嵌套对象
     * </p>
     *
     * @param bodyType 响应体泛型类型，例如：{@code new ParameterizedTypeReference<List<User>>(){}}
     * @param <T>      响应体类型
     * @return 指定泛型类型的JSON响应实体
     * @throws RestClientResponseException 当请求失败时抛出
     * @throws IllegalArgumentException    当bodyType为null时抛出
     * @see org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
     * @see org.springframework.http.converter.json.GsonHttpMessageConverter
     * @since 1.0.0
     */
    public <T> ResponseEntity<T> toJsonEntity(ParameterizedTypeReference<T> bodyType) throws RestClientResponseException {
		Assert.notNull(bodyType, "bodyType 不可为null");

        return buildRequestBodySpec()
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntity(bodyType);
    }

    /**
     * 将请求结果转换为指定类型的响应实体，指定可接受的媒体类型
     *
     * @param bodyType             响应体类型，例如：{@code User.class}
     * @param acceptableMediaTypes 可接受的媒体类型数组，例如：{@code MediaType.APPLICATION_JSON}
     * @param <T>                  响应体类型
     * @return 指定类型的响应实体
     * @throws RestClientResponseException 当请求失败时抛出
     * @throws IllegalArgumentException    当bodyType为null时抛出
     * @since 1.0.0
     */
    public <T> ResponseEntity<T> toEntity(Class<T> bodyType, MediaType... acceptableMediaTypes) throws RestClientResponseException {
		Assert.notNull(bodyType, "bodyType 不可为null");

        return buildRequestBodySpec()
                .accept(acceptableMediaTypes)
                .retrieve()
                .toEntity(bodyType);
    }

    /**
     * 将请求结果转换为指定泛型类型的响应实体，指定可接受的媒体类型
     *
     * @param bodyType             响应体泛型类型，例如：{@code new ParameterizedTypeReference<List<User>>(){}}
     * @param acceptableMediaTypes 可接受的媒体类型数组，例如：{@code MediaType.APPLICATION_JSON}
     * @param <T>                  响应体类型
     * @return 指定泛型类型的响应实体
     * @throws RestClientResponseException 当请求失败时抛出
     * @throws IllegalArgumentException    当bodyType为null时抛出
     * @since 1.0.0
     */
    public <T> ResponseEntity<T> toEntity(ParameterizedTypeReference<T> bodyType, MediaType... acceptableMediaTypes) throws RestClientResponseException {
		Assert.notNull(bodyType, "bodyType 不可为null");

        return buildRequestBodySpec()
                .accept(acceptableMediaTypes)
                .retrieve()
                .toEntity(bodyType);
    }

    /**
     * 将请求结果转换为无响应体的响应实体
     *
     * @return 无响应体的响应实体
     * @throws RestClientResponseException 当请求失败时抛出
     * @since 1.0.0
	 */
	public ResponseEntity<Void> toBodilessEntity() throws RestClientResponseException {
        return buildRequestBodySpec()
                .retrieve()
                .toBodilessEntity();
    }

    /**
     * 构建请求
     * <p>
     * 根据当前配置构建完整的请求规范，包括：
     * <ul>
     *     <li>设置请求方法和URI</li>
     *     <li>配置请求头</li>
     *     <li>处理请求体（根据内容类型区分表单数据和其他类型）</li>
     * </ul>
     * </p>
     *
     * @return 构建的请求规范
	 * @throws IllegalArgumentException 当请求uri为空时抛出
     * @since 1.0.0
     */
    public RestClient.RequestBodySpec buildRequestBodySpec() {
        URI uri = uriComponentsBuilder.build(uriVariables);
        if (StringUtils.isBlank(uri.toString())) {
            throw new IllegalArgumentException("uri 不可为空");
        }

        RestClient.RequestBodySpec requestBodySpec = restClient
                .method(method)
                .uri(uri)
                .contentType(contentType)
			.headers(httpHeaders -> httpHeaders.addAll(headers));

		if (!MediaType.APPLICATION_FORM_URLENCODED.equals(contentType)) {
			if (FORM_MEDIA_TYPES.contains(contentType)) {
				requestBodySpec.body(formData);
			} else {
				requestBodySpec.body(body);
			}
		}

        return requestBodySpec;
    }
}