package io.github.pangju666.framework.web.spec


import io.github.pangju666.framework.web.lang.WebConstants
import io.github.pangju666.framework.web.utils.IpUtils
import spock.lang.Specification
import spock.lang.Unroll

class IpUtilsSpec extends Specification {
	@Unroll
	def "isUnknown方法应正确判断IP是否未知: #description"() {
		expect:
		IpUtils.isUnknown(ip) == expected

		where:
		ip          | expected | description
		"unknown"   | true     | "未知地址字符串"
		"UNKNOWN"   | true     | "未知地址字符串（大写）"
		""          | true     | "空字符串"
		null        | true     | "null值"
		"127.0.0.1" | false    | "有效IP地址"
		"   "       | true     | "空白字符串"
	}

	def "getLocalInetAddress方法应返回本机有效的网络地址"() {
		when:
		InetAddress address = IpUtils.getLocalInetAddress()

		then:
		address != null
		!address.isLoopbackAddress()
		// 不测试可能不存在的接口，只验证返回地址不为空且不是回环地址
	}

	def "getLocalHostAddress方法应返回本机IP地址字符串或默认本地IP"() {
		when:
		String address = IpUtils.getLocalHostAddress()

		then:
		address != null
		address.length() > 0
		// 地址可能是本地地址，也可能是默认本地IP
		address == IpUtils.getLocalInetAddress()?.hostAddress ?: WebConstants.LOCAL_HOST_IP
	}

	@Unroll
	def "isIpv4方法应正确判断IPv4地址: #description"() {
		expect:
		IpUtils.isIpv4(ip) == expected

		where:
		ip                                        | expected | description
		"192.168.1.1"                             | true     | "标准IPv4地址"
		"0.0.0.0"                                 | true     | "全零IPv4地址"
		"255.255.255.255"                         | true     | "广播IPv4地址"
		"256.0.0.1"                               | false    | "无效IPv4地址（数值超范围）"
		"192.168.1"                               | false    | "无效IPv4地址（段数不足）"
		"192.168.1.1.1"                           | false    | "无效IPv4地址（段数过多）"
		"2001:0db8:85a3:0000:0000:8a2e:0370:7334" | false    | "IPv6地址"
		""                                        | false    | "空字符串"
		null                                      | false    | "null值"
	}

	@Unroll
	def "isIpv6方法应正确判断IPv6地址: #description"() {
		expect:
		IpUtils.isIpv6(ip) == expected

		where:
		ip                                        | expected | description
		"2001:0db8:85a3:0000:0000:8a2e:0370:7334" | true     | "标准IPv6地址"
		"::1"                                     | true     | "本地环回IPv6地址"
		"2001:db8::"                              | true     | "压缩格式IPv6地址"
		"2001:db8:0:0:0:0:2:1"                    | true     | "标准格式IPv6地址"
		"192.168.1.1"                             | false    | "IPv4地址"
		"2001:db8:g1::"                           | false    | "无效IPv6地址（非法字符）"
		"1:2:3:4:5:6:7:8:9"                       | false    | "无效IPv6地址（段数过多）"
		""                                        | false    | "空字符串"
		null                                      | false    | "null值"
	}

	@Unroll
	def "isValid方法应正确判断IP地址有效性: #description"() {
		expect:
		IpUtils.isValid(ip) == expected

		where:
		ip                                        | expected | description
		"192.168.1.1"                             | true     | "有效IPv4地址"
		"2001:0db8:85a3:0000:0000:8a2e:0370:7334" | true     | "有效IPv6地址"
		"::1"                                     | true     | "压缩格式IPv6地址"
		"256.0.0.1"                               | false    | "无效IPv4地址"
		"2001:db8:g1::"                           | false    | "无效IPv6地址"
		""                                        | false    | "空字符串"
		null                                      | false    | "null值"
	}

	@Unroll
	def "toBytes方法应正确将IP地址转换为字节数组: #description"() {
		when:
		byte[] bytes = IpUtils.toBytes(ip)

		then:
		bytes == expected

		where:
		ip            | expected                                                   | description
		"192.168.1.1" | [192, 168, 1, 1] as byte[]                                 | "IPv4地址"
		"127.0.0.1"   | [127, 0, 0, 1] as byte[]                                   | "本地环回IPv4地址"
		"::1"         | [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1] as byte[] | "IPv6本地环回地址"
	}

	@Unroll
	def "isIpInNetwork方法应正确判断IP是否属于子网: #description"() {
		expect:
		IpUtils.isIpInNetwork(network, ip) == expected

		where:
		network           | ip              | expected | description
		"192.168.1.0/24"  | "192.168.1.100" | true     | "IPv4地址在子网内"
		"192.168.1.0/24"  | "192.168.2.1"   | false    | "IPv4地址不在子网内"
		"2001:db8::/64"   | "2001:db8::1"   | true     | "IPv6地址在子网内"
		"2001:db8::/64"   | "2001:db9::1"   | false    | "IPv6地址不在子网内"
		"192.168.1.1"     | "192.168.1.1"   | true     | "完全相同的地址"
		"192.168.1.0/24"  | null            | false    | "IP地址为null"
		"not-valid-cidr"  | "192.168.1.1"   | false    | "网络地址格式无效"
		"192.168.1.0/24"  | "not-valid-ip"  | false    | "IP地址格式无效"
		"192.168.1.*"     | "192.168.1.100" | true     | "完全相同的地址"
		"192.168.*.1"     | "192.168.11.1"  | true     | "完全相同的地址"
		"192.168.1.1-255" | "192.168.1.100" | true     | "完全相同的地址"
	}

	@Unroll
	def "isInternalIpv4方法应正确判断是否为内部IP: #description"() {
		expect:
		IpUtils.isInternalIpv4(ip) == expected

		where:
		ip            | expected | description
		"127.0.0.1"   | true     | "本地回环地址"
		"10.0.0.1"    | true     | "A类私有地址"
		"172.16.0.1"  | true     | "B类私有地址"
		"192.168.0.1" | true     | "C类私有地址"
		"8.8.8.8"     | false    | "公网IP地址"
	}

	@Unroll
	def "getMultistageReverseProxyIp方法应正确解析多级代理IP: #description"() {
		expect:
		IpUtils.getMultistageReverseProxyIp(ip) == expected

		where:
		ip                               | expected                    | description
		"192.168.1.1"                    | "192.168.1.1"               | "单个IP地址"
		"192.168.1.1, 10.0.0.1"          | "192.168.1.1"               | "多个IP地址，第一个有效"
		"unknown, 192.168.1.1, 10.0.0.1" | "192.168.1.1"               | "第一个IP未知，取第二个"
		"unknown, unknown, 192.168.1.1"  | "192.168.1.1"               | "前两个IP未知，取第三个"
		"unknown, unknown, unknown"      | "unknown, unknown, unknown" | "所有IP都未知，返回原始字符串"
		"  192.168.1.1  , 10.0.0.1"      | "192.168.1.1"               | "IP地址含空格，应去除空格"
		""                               | ""                          | "空字符串"
		null                             | ""                          | "null值"
	}
}