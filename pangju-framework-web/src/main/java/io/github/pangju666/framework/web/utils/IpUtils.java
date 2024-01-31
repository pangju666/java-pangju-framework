package io.github.pangju666.framework.web.utils;

import io.github.pangju666.commons.lang.pool.ConstantPool;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public class IpUtils {
	public static final String UNKNOWN_ADDRESS = "unknown";

	protected IpUtils() {
	}

	public static boolean isInternalIp(final String ip) {
		if (ConstantPool.LOCALHOST_IPV4_ADDRESS.equals(ip)) {
			return true;
		}
		byte[] addressBytes = toBytes(ip);
		if (ArrayUtils.getLength(addressBytes) < 2) {
			return true;
		}
		final byte b0 = addressBytes[0];
		final byte b1 = addressBytes[1];
		// 10.x.x.x/8
		final byte SECTION_1 = 0x0A;
		// 172.16.x.x/12
		final byte SECTION_2 = (byte) 0xAC;
		final byte SECTION_3 = (byte) 0x10;
		final byte SECTION_4 = (byte) 0x1F;
		// 192.168.x.x/16
		final byte SECTION_5 = (byte) 0xC0;
		final byte SECTION_6 = (byte) 0xA8;
		return switch (b0) {
			case SECTION_1 -> true;
			case SECTION_2 -> b1 >= SECTION_3 && b1 <= SECTION_4;
			case SECTION_5 -> b1 == SECTION_6;
			default -> false;
		};
	}

	public static byte[] toBytes(final String ip) {
		if (StringUtils.isBlank(ip)) {
			return ArrayUtils.EMPTY_BYTE_ARRAY;
		}
		byte[] bytes = new byte[4];
		String[] elements = ip.split("\\.", -1);
		long l = Long.parseLong(elements[0]);
		switch (elements.length) {
			case 1:
				if (l < 0L || l > 4294967295L) {
					return ArrayUtils.EMPTY_BYTE_ARRAY;
				}
				bytes[0] = (byte) (int) (l >> 24 & 0xFF);
				bytes[1] = (byte) (int) ((l & 0xFFFFFF) >> 16 & 0xFF);
				bytes[2] = (byte) (int) ((l & 0xFFFF) >> 8 & 0xFF);
				bytes[3] = (byte) (int) (l & 0xFF);
				break;
			case 2:
				if (l < 0L || l > 255L) {
					return ArrayUtils.EMPTY_BYTE_ARRAY;
				}
				bytes[0] = (byte) (int) (l & 0xFF);
				l = Integer.parseInt(elements[1]);
				if (l < 0L || l > 16777215L) {
					return ArrayUtils.EMPTY_BYTE_ARRAY;
				}
				bytes[1] = (byte) (int) (l >> 16 & 0xFF);
				bytes[2] = (byte) (int) ((l & 0xFFFF) >> 8 & 0xFF);
				bytes[3] = (byte) (int) (l & 0xFF);
				break;
			case 3:
				for (int i = 0; i < 2; ++i) {
					long ll = Integer.parseInt(elements[i]);
					if (ll < 0L || ll > 255L) {
						return ArrayUtils.EMPTY_BYTE_ARRAY;
					}
					bytes[i] = (byte) (int) (ll & 0xFF);
				}
				l = Integer.parseInt(elements[2]);
				if (l < 0L || l > 65535L) {
					return ArrayUtils.EMPTY_BYTE_ARRAY;
				}
				bytes[2] = (byte) (int) (l >> 8 & 0xFF);
				bytes[3] = (byte) (int) (l & 0xFF);
				break;
			case 4:
				for (int i = 0; i < 4; ++i) {
					long ll = Integer.parseInt(elements[i]);
					if (ll < 0L || ll > 255L) {
						return ArrayUtils.EMPTY_BYTE_ARRAY;
					}
					bytes[i] = (byte) (int) (ll & 0xFF);
				}
				break;
			default:
				return ArrayUtils.EMPTY_BYTE_ARRAY;
		}
		return bytes;
	}

	public static String getMultistageReverseProxyIp(final String ip) {
		// 多级反向代理检测
		if (StringUtils.indexOf(ip, ",") > 0) {
			final String[] subIps = ip.trim().split(",");
			for (String subIp : subIps) {
				if (!isUnknown(subIp)) {
					return subIp;
				}
			}
		}
		return ip;
	}

	public static boolean isUnknown(final String ip) {
		return StringUtils.isBlank(ip) || UNKNOWN_ADDRESS.equalsIgnoreCase(ip);
	}
}