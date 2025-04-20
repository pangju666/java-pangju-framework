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

package io.github.pangju666.framework.web.utils;

import inet.ipaddr.IPAddress;
import inet.ipaddr.IPAddressString;
import inet.ipaddr.IPAddressStringParameters;
import io.github.pangju666.framework.web.pool.WebConstants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;

/**
 * IP地址工具类
 * <p>
 * 提供IP地址相关的实用方法，包括但不限于：
 * <ul>
 *     <li>IP地址格式验证（IPv4/IPv6）</li>
 *     <li>IP地址范围判断</li>
 *     <li>IP地址转换</li>
 *     <li>获取本地IP地址</li>
 * </ul>
 * <p>
 * 该工具类基于 {@link inet.ipaddr.IPAddressString} 实现IP地址操作，支持IPv4和IPv6地址格式。
 *
 * <p>示例用法：</p>
 * <pre>{@code
 * // 验证IP地址
 * boolean isValidIp = IpUtils.isValid("192.168.1.1");  // 返回 true
 * boolean isValidIp = IpUtils.isValid("::1");  // 返回 true
 *
 * // 检查IP类型
 * boolean isIpv4 = IpUtils.isIpv4("192.168.1.1");      // 返回 true
 * boolean isIpv6 = IpUtils.isIpv6("::1");              // 返回 true
 *
 * // 检查IP是否在网络范围内
 * boolean inNetwork = IpUtils.isIpInNetwork("192.168.1.0/24", "192.168.1.100");  // 返回 true
 * boolean inNetwork = IpUtils.isIpInNetwork("192.168.1.*", "192.168.1.100");  // 返回 true
 * boolean inNetwork = IpUtils.isIpInNetwork("192.168.*.100", "192.168.11.100");  // 返回 true
 * boolean inNetwork = IpUtils.isIpInNetwork("192.168.1.1-255", "192.168.1.100");  // 返回 true
 *
 * // 获取本机IP地址
 * String localIp = IpUtils.getLocalHostAddress();  // 返回类似 "192.168.1.2"
 * }</pre>
 *
 * @author pangju666
 * @since 1.0.0
 */
public class IpUtils {
	// 10.x.x.x/8
	protected static final byte SECTION_1 = 0x0A;
	// 172.16.x.x/12
	protected static final byte SECTION_2 = (byte) 0xAC;
	protected static final byte SECTION_3 = (byte) 0x10;
	protected static final byte SECTION_4 = (byte) 0x1F;
	// 192.168.x.x/16
	protected static final byte SECTION_5 = (byte) 0xC0;
	protected static final byte SECTION_6 = (byte) 0xA8;

	/**
	 * 默认IP地址字符串参数配置
	 * <p>
	 * 配置用于解析IP地址的默认参数，禁用了大多数特殊格式支持以确保严格的IP地址验证。
	 * </p>
	 *
	 * @since 1.0.0
	 */
	protected static final IPAddressStringParameters DEFAULT_IP_ADDRESS_STRING_PARAMETERS = new IPAddressStringParameters.Builder()
		.setEmptyAsLoopback(false)
		.allowPrefix(false)
		.allowMask(false)
		.allowWildcardedSeparator(false)
		.allowEmpty(false)
		.allowAll(false)
		.allowPrefixOnly(false)
		.allowSingleSegment(false)
		.allow_inet_aton(false)
		.toParams();

	protected IpUtils() {
	}

	/**
	 * 判断IP地址是否为未知状态
	 * <p>
	 * 检查给定的IP地址是否为空白或"unknown"（不区分大小写）。
	 * 这通常用于处理代理服务器传递的IP信息，代理服务器可能在无法确定客户端IP时使用"unknown"标记。
	 * </p>
	 *
	 * @param ipAddress 待检查的IP地址字符串
	 * @return 如果IP为空白或"unknown"则返回true，否则返回false
	 * @since 1.0.0
	 */
	public static boolean isUnknown(final String ipAddress) {
		return StringUtils.isBlank(ipAddress) || StringUtils.equalsIgnoreCase(WebConstants.UNKNOWN_ADDRESS, ipAddress);
	}

	/**
	 * 获取本地主机的网络地址
	 * <p>
	 * 返回第一个可用的非环回、非虚拟网络接口的IP地址。
	 * </p>
	 *
	 * <p>示例：</p>
	 * <pre>{@code
	 * try {
	 *     InetAddress address = IpUtils.getLocalInetAddress();
	 *     if (address != null) {
	 *         System.out.println("本机IP地址: " + address.getHostAddress());
	 *     } else {
	 *         System.out.println("无法获取本机IP地址");
	 *     }
	 * } catch (SocketException e) {
	 *     System.err.println("获取网络接口信息出错: " + e.getMessage());
	 * }
	 * }</pre>
	 *
	 * @return 本地主机的网络地址，如果没有找到合适的地址则返回null
	 * @throws SocketException 当获取网络接口信息出错时抛出
	 * @since 1.0.0
	 */
	public static InetAddress getLocalInetAddress() throws SocketException {
		Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
		while (networkInterfaces.hasMoreElements()) {
			NetworkInterface networkInterface = networkInterfaces.nextElement();
			// 跳过未启用、回环和虚拟网卡
			if (!networkInterface.isUp() || networkInterface.isLoopback() || networkInterface.isVirtual() ||
				isVirtualInterface(networkInterface)) {
				continue;
			}
			// 遍历该网络接口的所有 IP 地址
			List<InterfaceAddress> inetAddresses = networkInterface.getInterfaceAddresses();
			InterfaceAddress interfaceAddress = CollectionUtils.firstElement(inetAddresses);
			if (Objects.nonNull(interfaceAddress)) {
				return interfaceAddress.getAddress();
			}
		}
		return null;
	}

	/**
	 * 获取本地主机的IP地址字符串
	 * <p>
	 * 如果无法获取本地主机的有效IP地址，将返回本地主机IP常量（通常为"127.0.0.1"）。
	 * </p>
	 *
	 * <p>示例：</p>
	 * <pre>{@code
	 * String localIp = IpUtils.getLocalHostAddress();
	 * System.out.println("本机IP地址: " + localIp);  // 例如输出: "本机IP地址: 192.168.1.5"
	 * }</pre>
	 *
	 * @return 本地主机的IP地址字符串
	 * @since 1.0.0
	 */
	public static String getLocalHostAddress() {
		try {
			InetAddress inetAddress = getLocalInetAddress();
			if (Objects.nonNull(inetAddress)) {
				return inetAddress.getHostAddress();
			}
		} catch (SocketException ignored) {
		}
		return WebConstants.LOCAL_HOST_IP;
	}

	/**
	 * 判断给定的字符串是否为有效的IPv4地址
	 *
	 * <p>示例：</p>
	 * <pre>{@code
	 * boolean isV4_1 = IpUtils.isIpv4("192.168.1.1");   // 返回 true
	 * boolean isV4_2 = IpUtils.isIpv4("0.0.0.0");       // 返回 true
	 * boolean isV4_3 = IpUtils.isIpv4("256.0.0.1");     // 返回 false（超出范围）
	 * boolean isV4_4 = IpUtils.isIpv4("192.168.1");     // 返回 false（段数不足）
	 * boolean isV4_5 = IpUtils.isIpv4("::1");           // 返回 false（IPv6地址）
	 * }</pre>
	 *
	 * @param ipAddress 要验证的IP地址字符串
	 * @return 如果是有效的IPv4地址则返回true，否则返回false
	 * @since 1.0.0
	 */
	public static boolean isIpv4(final String ipAddress) {
		if (StringUtils.isBlank(ipAddress)) {
			return false;
		}

		IPAddressString ipAddressString = new IPAddressString(ipAddress, DEFAULT_IP_ADDRESS_STRING_PARAMETERS);
		return ipAddressString.isIPv4();
	}

	/**
	 * 判断给定的字符串是否为有效的IPv6地址
	 *
	 * <p>示例：</p>
	 * <pre>{@code
	 * boolean isV6_1 = IpUtils.isIpv6("2001:0db8:85a3:0000:0000:8a2e:0370:7334");  // 返回 true
	 * boolean isV6_2 = IpUtils.isIpv6("::1");                  // 返回 true（本地环回地址）
	 * boolean isV6_3 = IpUtils.isIpv6("2001:db8::");           // 返回 true（压缩格式）
	 * boolean isV6_4 = IpUtils.isIpv6("192.168.1.1");          // 返回 false（IPv4地址）
	 * boolean isV6_5 = IpUtils.isIpv6("2001:db8:g1::");        // 返回 false（非法字符）
	 * }</pre>
	 *
	 * @param ipAddress 要验证的IP地址字符串
	 * @return 如果是有效的IPv6地址则返回true，否则返回false
	 * @since 1.0.0
	 */
	public static boolean isIpv6(final String ipAddress) {
		if (StringUtils.isBlank(ipAddress)) {
			return false;
		}

		IPAddressString ipAddressString = new IPAddressString(ipAddress, DEFAULT_IP_ADDRESS_STRING_PARAMETERS);
		return ipAddressString.isIPv6();
	}

	/**
	 * 判断给定的字符串是否为有效的IP地址（IPv4或IPv6）
	 *
	 * <p>示例：</p>
	 * <pre>{@code
	 * boolean isValid1 = IpUtils.isValid("192.168.1.1");                         // 返回 true（IPv4）
	 * boolean isValid2 = IpUtils.isValid("2001:0db8:85a3:0000:0000:8a2e:0370:7334"); // 返回 true（IPv6）
	 * boolean isValid3 = IpUtils.isValid("::1");                                 // 返回 true（IPv6压缩格式）
	 * boolean isValid4 = IpUtils.isValid("256.0.0.1");                           // 返回 false（无效IPv4）
	 * boolean isValid5 = IpUtils.isValid("hello");                               // 返回 false（非IP格式）
	 * }</pre>
	 *
	 * @param ipAddress 要验证的IP地址字符串
	 * @return 如果是有效的IP地址则返回true，否则返回false
	 * @since 1.0.0
	 */
	public static boolean isValid(final String ipAddress) {
		if (StringUtils.isBlank(ipAddress)) {
			return false;
		}

		IPAddressString ipAddressString = new IPAddressString(ipAddress, DEFAULT_IP_ADDRESS_STRING_PARAMETERS);
		return ipAddressString.isValid();
	}

	/**
	 * 将IP地址字符串转换为字节数组
	 *
	 * <p>示例：</p>
	 * <pre>{@code
	 * byte[] bytes1 = IpUtils.toBytes("192.168.1.1");  // 返回 [192, 168, 1, 1]
	 * byte[] bytes2 = IpUtils.toBytes("127.0.0.1");    // 返回 [127, 0, 0, 1]
	 * byte[] bytes3 = IpUtils.toBytes("::1");          // 返回 [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1]
	 *
	 * // 打印字节数组
	 * System.out.println(Arrays.toString(IpUtils.toBytes("192.168.1.1")));
	 * }</pre>
	 *
	 * @param ipAddress 要转换的IP地址字符串
	 * @return IP地址对应的字节数组
	 * @throws IllegalArgumentException 如果IP地址为空或格式无效
	 * @since 1.0.0
	 */
	public static byte[] toBytes(final String ipAddress) {
		Assert.hasText(ipAddress, "ipAddress 不可为空");

		IPAddressString ipAddressString = new IPAddressString(ipAddress);
		return ipAddressString.getAddress().getBytes();
	}

	/**
	 * 判断IP地址是否在网络范围内
	 * <p>
	 * 该方法检查指定的IP地址是否包含在给定的网络地址范围内。
	 * 网络地址可以是CIDR格式（如192.168.1.0/24）或单个IP地址。
	 * </p>
	 *
	 * <p>示例：</p>
	 * <pre>{@code
	 * // 使用CIDR格式
	 * boolean inNetwork1 = IpUtils.isIpInNetwork("192.168.1.0/24", "192.168.1.100");  // 返回 true
	 * boolean inNetwork2 = IpUtils.isIpInNetwork("192.168.1.0/24", "192.168.2.1");    // 返回 false
	 *
	 * // 使用IPv6网络
	 * boolean inNetwork3 = IpUtils.isIpInNetwork("2001:db8::/64", "2001:db8::1");     // 返回 true
	 * boolean inNetwork4 = IpUtils.isIpInNetwork("2001:db8::/64", "2001:db9::1");     // 返回 false
	 *
	 * // 使用单个IP
	 * boolean inNetwork5 = IpUtils.isIpInNetwork("192.168.1.1", "192.168.1.1");       // 返回 true
	 *
	 * // 使用通配符
	 * boolean inNetwork = IpUtils.isIpInNetwork("192.168.1.*", "192.168.1.100");  // 返回 true
	 * boolean inNetwork = IpUtils.isIpInNetwork("192.168.*.100", "192.168.11.100");  // 返回 true
	 *
	 * // 使用范围分隔符
	 * boolean inNetwork = IpUtils.isIpInNetwork("192.168.1.1-255", "192.168.1.100");  // 返回 true
	 * }</pre>
	 *
	 * @param networkAddress 网络地址（CIDR格式、通配符格式或IP范围格式等，具体请参阅<a href="https://seancfoley.github.io/IPAddress/">IPAddressString文档</a>），不能为空
	 * @param ipAddress      要检查的IP地址，如果为空则返回false
	 * @return 如果IP地址在网络范围内返回true，否则返回false
	 * @throws IllegalArgumentException 如果网络地址为空
	 * @since 1.0.0
	 */
	public static boolean isIpInNetwork(final String networkAddress, final String ipAddress) {
		Assert.hasText(networkAddress, "networkAddress 不可为空");
		if (StringUtils.isBlank(ipAddress)) {
			return false;
		}

		try {
			IPAddressString address = new IPAddressString(ipAddress, DEFAULT_IP_ADDRESS_STRING_PARAMETERS);
			IPAddressString network = new IPAddressString(networkAddress);
			return network.contains(address);
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 判断IP地址是否为内部IPv4地址
	 * <p>
	 * 检查给定的IPV4地址是否为本地网络内的地址，包括环回地址、私有网络地址等。
	 * </p>
	 *
	 * <p>代码修改自RuoYi Common IpUtils</p>
	 *
	 * <p>示例：</p>
	 * <pre>{@code
	 * boolean isInternal1 = IpUtils.isInternalIpv4("127.0.0.1");    // 返回 true（本地回环地址）
	 * boolean isInternal2 = IpUtils.isInternalIpv4("10.0.0.1");     // 返回 true（A类私有地址）
	 * boolean isInternal3 = IpUtils.isInternalIpv4("172.16.0.1");   // 返回 true（B类私有地址）
	 * boolean isInternal4 = IpUtils.isInternalIpv4("192.168.0.1");  // 返回 true（C类私有地址）
	 * boolean isInternal5 = IpUtils.isInternalIpv4("8.8.8.8");      // 返回 false（公网IP地址）
	 * }</pre>
	 *
	 * @param ipAddress 要检查的IP地址字符串
	 * @return 如果是内部IPv4地址则返回true，否则返回false；如果输入为空则返回false
	 * @since 1.0.0
	 */
	public static boolean isInternalIpv4(final String ipAddress) {
		if (StringUtils.isBlank(ipAddress)) {
			return false;
		}

		IPAddressString ipAddressString = new IPAddressString(ipAddress, DEFAULT_IP_ADDRESS_STRING_PARAMETERS);
		IPAddress address = ipAddressString.getAddress();
		if (address.isLoopback()) {
			return true;
		}

		byte[] addressBytes = address.getBytes();
		final byte b0 = addressBytes[0];
		final byte b1 = addressBytes[1];
		// 判断是否属于内网IP地址段
		return switch (b0) {
			case SECTION_1 -> true; // 10.x.x.x/8
			case SECTION_2 -> b1 >= SECTION_3 && b1 <= SECTION_4; // 172.16.x.x/12
			case SECTION_5 -> b1 == SECTION_6; // 192.168.x.x/16
			default -> false;
		};
	}

	/**
	 * 处理多级反向代理的IP获取
	 * <p>
	 * 当使用多级反向代理时，HTTP请求头中的IP地址可能包含多个IP，以逗号分隔。
	 * 此方法从这些IP中提取第一个非未知（非"unknown"）的IP地址作为客户端的真实IP。
	 * </p>
	 *
	 * <p>
	 * 如果所有IP都是未知的，则返回原始IP字符串。
	 * </p>
	 *
	 * <p>代码修改自RuoYi Common IpUtils</p>
	 *
	 * <p>示例：</p>
	 * <pre>{@code
	 * // 单个IP地址
	 * String ip1 = IpUtils.getMultistageReverseProxyIp("192.168.1.1");               // 返回 "192.168.1.1"
	 *
	 * // 多级代理，第一个IP有效
	 * String ip2 = IpUtils.getMultistageReverseProxyIp("192.168.1.1, 10.0.0.1");     // 返回 "192.168.1.1"
	 *
	 * // 第一个IP未知，取第二个
	 * String ip3 = IpUtils.getMultistageReverseProxyIp("unknown, 192.168.1.1");      // 返回 "192.168.1.1"
	 *
	 * // 所有IP都未知
	 * String ip4 = IpUtils.getMultistageReverseProxyIp("unknown, unknown, unknown"); // 返回 "unknown"
	 *
	 * // 处理空格
	 * String ip5 = IpUtils.getMultistageReverseProxyIp("  192.168.1.1  , 10.0.0.1"); // 返回 "192.168.1.1"
	 * }</pre>
	 *
	 * @param ipAddress 可能包含多个IP的字符串，以逗号分隔
	 * @return 处理后的实际客户端IP地址，如果输入为null或空字符串则返回空字符串
	 * @since 1.0.0
	 */
	public static String getMultistageReverseProxyIp(final String ipAddress) {
		if (StringUtils.isBlank(ipAddress)) {
			return StringUtils.EMPTY;
		}

		// 多级反向代理检测
		if (StringUtils.indexOf(ipAddress, ",") > 0) {
			final String[] splitIpAddresses = ipAddress.split(",");
			for (String splitIpAddress : splitIpAddresses) {
				String trimmedIpAddress = StringUtils.trim(splitIpAddress);
				if (!isUnknown(trimmedIpAddress)) {
					return trimmedIpAddress;
				}
			}
		}
		return ipAddress.trim();
	}

	/**
	 * 判断网络接口是否为虚拟接口
	 * <p>
	 * 通过检查网络接口的显示名称中是否包含常见虚拟化平台的关键字来判断。
	 * </p>
	 *
	 * @param networkInterface 要检查的网络接口
	 * @return 如果是虚拟接口则返回true，否则返回false
	 * @since 1.0.0
	 */
	protected static boolean isVirtualInterface(final NetworkInterface networkInterface) {
		String displayName = StringUtils.lowerCase(networkInterface.getDisplayName());
		return Objects.nonNull(displayName) && (displayName.contains("wsl") ||
			displayName.contains("vmware") ||
			displayName.contains("virtualbox") ||
			displayName.contains("hyper-v") ||
			displayName.contains("docker")
		);
	}
}