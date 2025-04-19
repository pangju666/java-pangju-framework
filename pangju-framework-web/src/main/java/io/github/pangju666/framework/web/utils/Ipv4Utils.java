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

import inet.ipaddr.IPAddressString;
import io.github.pangju666.commons.lang.pool.RegExPool;
import io.github.pangju666.commons.lang.utils.RegExUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Pattern;

/**
 * IPv4地址工具类
 * <p>
 * 提供用于处理和验证IPv4地址的实用方法，包括：
 * <ul>
 *     <li>检查IP是否为内部地址</li>
 *     <li>将IP地址字符串转换为字节数组</li>
 *     <li>处理多级反向代理的IP获取</li>
 *     <li>验证IP地址是否为未知状态</li>
 * </ul>
 * </p>
 *
 * <p>
 * 该工具类主要用于网络应用中的IP地址处理，支持各种格式的IPv4地址解析和验证。
 * 提供了对内网IP地址范围（10.x.x.x、172.16-31.x.x、192.168.x.x）的识别。
 * </p>
 *
 * @author pangju666
 * @since 1.0.0
 */
public class Ipv4Utils {
	/**
	 * 未知地址标识
	 *
	 * @since 1.0.0
	 */
	public static final String UNKNOWN_ADDRESS = "unknown";
	public static final Pattern IP_PATTERN = RegExUtils.compile(RegExPool.IPV4, true, true);

	// 内部IP地址段的常量定义
	protected static final byte SECTION_1 = 0x0A; // 10.x.x.x/8
	protected static final byte SECTION_2 = (byte) 0xAC; // 172.16.x.x/12
	protected static final byte SECTION_3 = (byte) 0x10;
	protected static final byte SECTION_4 = (byte) 0x1F;
	protected static final byte SECTION_5 = (byte) 0xC0; // 192.168.x.x/16
	protected static final byte SECTION_6 = (byte) 0xA8;

	protected Ipv4Utils() {
	}

	/**
	 * 判断IP地址是否为未知状态
	 * <p>
	 * 检查给定的IP地址是否为"unknown"（不区分大小写）。
	 * 这通常用于处理代理服务器传递的IP信息，代理服务器可能在无法确定客户端IP时使用"unknown"标记。
	 * </p>
	 *
	 * @param ip 待检查的IP地址字符串
	 * @return 如果IP为"unknown"则返回true，否则返回false
	 * @since 1.0.0
	 */
	public static boolean isUnknown(final String ip) {
		return StringUtils.equalsIgnoreCase(UNKNOWN_ADDRESS, ip);
	}

	public static boolean isIp(String ip) {
		return RegExUtils.matches(IP_PATTERN, ip);
	}

	public static boolean isSubIp(String ip, String subIp) {
		IPAddressString address = new IPAddressString("192.168.1.5");
		IPAddressString network = new IPAddressString("192.168.1.0/24");
		return address.contains(network);
	}

	/**
	 * 判断IP是否为内网IPv4地址
	 * <p>
	 * 检查给定的IP地址是否属于以下内网IP范围：
	 * <ul>
	 *     <li>本地回环地址：127.0.0.1</li>
	 *     <li>A类私有地址：10.0.0.0 - 10.255.255.255</li>
	 *     <li>B类私有地址：172.16.0.0 - 172.31.255.255</li>
	 *     <li>C类私有地址：192.168.0.0 - 192.168.255.255</li>
	 * </ul>
	 * </p>
	 *
	 * @param ip 待检查的IP地址字符串
	 * @return 如果是内网IP则返回true，否则返回false
	 * @since 1.0.0
	 */
	public static boolean isInternalIp(final String ip) {
		try {
			InetAddress inetAddress = InetAddress.getByName(ip);
			if (inetAddress.isLoopbackAddress()) {
				return true;
			}

			byte[] addressBytes = inetAddress.getAddress();
			if (ArrayUtils.getLength(addressBytes) < 2) {
				return true;
			}

			final byte b0 = addressBytes[0];
			final byte b1 = addressBytes[1];
			// 判断是否属于内网IP地址段
			return switch (b0) {
				case SECTION_1 -> true; // 10.x.x.x/8
				case SECTION_2 -> b1 >= SECTION_3 && b1 <= SECTION_4; // 172.16.x.x/12
				case SECTION_5 -> b1 == SECTION_6; // 192.168.x.x/16
				default -> false;
			};
		} catch (UnknownHostException e) {
			throw new IllegalArgumentException("无效的ip地址", e);
		}
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
	 * @param ip 可能包含多个IP的字符串，以逗号分隔
	 * @return 处理后的实际客户端IP地址
	 * @since 1.0.0
	 */
	public static String getMultistageReverseProxyIp(final String ip) {
		// 多级反向代理检测
		if (StringUtils.indexOf(ip, ",") > 0) {
			final String[] subIps = ip.trim().split(",");
			for (String subIp : subIps) {
				if (!isUnknown(subIp)) {
					return subIp.trim();
				}
			}
		}
		return ip;
	}
}