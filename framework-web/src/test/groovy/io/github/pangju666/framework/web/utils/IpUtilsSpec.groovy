package io.github.pangju666.framework.web.utils

import io.github.pangju666.framework.web.lang.WebConstants
import org.springframework.boot.SpringBootConfiguration
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification
import spock.lang.Unroll

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = Config)
class IpUtilsSpec extends Specification {

	@SpringBootConfiguration
	@EnableAutoConfiguration
	static class Config {
		// 不需要任何Bean，这里只是提供一个最小的Spring Boot配置上下文
	}

	@Unroll
	def "isUnknown 返回正确结果: '#input' -> #expected"(String input, boolean expected) {
		expect:
		IpUtils.isUnknown(input) == expected

		where:
		input           | expected
		null            | true
		""              | true
		"   "           | true
		"unknown"       | true
		"UnKnOwN"       | true
		"127.0.0.1"     | false
		"192.168.1.100" | false
	}

	def "getLocalHostAddress 返回有效IP或回退到本地回环地址"() {
		when:
		String ip = IpUtils.getLocalHostAddress()

		then:
		ip != null
		ip.trim().length() > 0
		IpUtils.isValid(ip) || ip == WebConstants.LOCALHOST_IPV4_ADDRESS
	}

	@Unroll
	def "isIpv4 校验: '#ip' -> #expected"(String ip, boolean expected) {
		expect:
		IpUtils.isIpv4(ip) == expected

		where:
		ip                | expected
		"192.168.1.1"     | true
		"0.0.0.0"         | true
		"255.255.255.255" | true
		"256.0.0.1"       | false
		"192.168.1"       | false
		"::1"             | false
		""                | false
		null              | false
	}

	@Unroll
	def "isIpv6 校验: '#ip' -> #expected"(String ip, boolean expected) {
		expect:
		IpUtils.isIpv6(ip) == expected

		where:
		ip                                        | expected
		"2001:0db8:85a3:0000:0000:8a2e:0370:7334" | true
		"2001:db8::"                              | true
		"::1"                                     | true
		"192.168.1.1"                             | false
		"2001:db8:g1::"                           | false
		""                                        | false
		null                                      | false
	}

	@Unroll
	def "isValid 校验: '#ip' -> #expected"(String ip, boolean expected) {
		expect:
		IpUtils.isValid(ip) == expected

		where:
		ip                | expected
		"192.168.1.1"     | true
		"255.255.255.255" | true
		"2001:db8::"      | true
		"::1"             | true
		"256.0.0.1"       | false
		"hello"           | false
		""                | false
		null              | false
	}

	def "toBytes IPv4 转换为 4 字节数组"() {
		when:
		byte[] bytes = IpUtils.toBytes("192.168.1.1")

		then:
		bytes.length == 4
		Arrays.equals(bytes, [(byte) 192, (byte) 168, (byte) 1, (byte) 1] as byte[])
	}

	def "toBytes IPv6 转换为 16 字节数组"() {
		when:
		byte[] bytes = IpUtils.toBytes("::1")

		then:
		bytes.length == 16
		bytes[15] == (byte) 1
		// 前 15 字节均为 0
		Arrays.equals(Arrays.copyOfRange(bytes, 0, 15), new byte[15])
	}

	def "toBytes 空字符串抛出 IllegalArgumentException"() {
		when:
		IpUtils.toBytes("")

		then:
		thrown(IllegalArgumentException)
	}

	@Unroll
	def "isIpInNetwork 支持 CIDR/通配符/范围/单IP/IPv6: network='#network', ip='#ip' -> #expected"(String network, String ip, boolean expected) {
		expect:
		IpUtils.isIpInNetwork(network, ip) == expected

		where:
		network           | ip               | expected
		"192.168.1.0/24"  | "192.168.1.100"  | true
		"192.168.1.0/24"  | "192.168.2.1"    | false
		"2001:db8::/64"   | "2001:db8::1"    | true
		"2001:db9::/64"   | "2001:db8::1"    | false
		"192.168.1.1"     | "192.168.1.1"    | true
		"192.168.1.1"     | "192.168.1.2"    | false
		"192.168.1.*"     | "192.168.1.100"  | true
		"192.168.*.100"   | "192.168.11.100" | true
		"192.168.1.1-255" | "192.168.1.100"  | true
		"192.168.1.1-255" | "192.168.1.0"    | false
	}

	@Unroll
	def "isIpInNetwork 对非法或空 IP 返回 false: network='#network', ip='#ip'"(String network, String ip) {
		expect:
		!IpUtils.isIpInNetwork(network, ip)

		where:
		network          | ip
		"192.168.1.0/24" | ""
		"192.168.1.0/24" | null
		"192.168.1.0/24" | "not-an-ip"
		"2001:db8::/64"  | "hello"
	}

	@Unroll
	def "isInternalIpv4 私有/环回/公网校验: '#ip' -> #expected"(String ip, boolean expected) {
		expect:
		IpUtils.isInternalIpv4(ip) == expected

		where:
		ip               | expected
		"127.0.0.1"      | true   // 环回
		"10.0.0.1"       | true   // A类私网
		"172.16.0.1"     | true   // B类私网起点
		"172.31.255.255" | true   // B类私网终点
		"172.32.0.0"     | false  // 超出 B 类私网
		"192.168.0.1"    | true   // C类私网
		"8.8.8.8"        | false  // 公网
		""               | false
		null             | false
	}

	@Unroll
	def "getMultistageReverseProxyIp 多级代理取第一个非unknown: '#raw' -> '#expected'"(String raw, String expected) {
		expect:
		IpUtils.getMultistageReverseProxyIp(raw) == expected

		where:
		raw                           | expected
		"192.168.1.1"                 | "192.168.1.1"
		"  192.168.1.1  , 10.0.0.1"   | "192.168.1.1"
		"unknown, 192.168.1.1"        | "192.168.1.1"
		"unknown, unknown"            | "unknown, unknown"   // 都是 unknown，返回原始字符串 strip 后
		null                          | ""                   // 空/空白 -> 空字符串
		""                            | ""
		"  192.168.1.1  ,  10.0.0.1 " | "192.168.1.1"
	}
}