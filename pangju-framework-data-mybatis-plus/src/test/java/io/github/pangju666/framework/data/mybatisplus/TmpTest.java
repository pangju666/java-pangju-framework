package io.github.pangju666.framework.data.mybatisplus;

import io.github.pangju666.commons.lang.enums.RegexFlag;
import io.github.pangju666.commons.lang.utils.JsonUtils;
import org.junit.jupiter.api.Test;


public class TmpTest {
	@Test
	void testListByJsonObjectValue() {
		System.out.println(JsonUtils.toString(false));
		System.out.println(JsonUtils.toString(null));
		System.out.println(JsonUtils.toString(1));
		System.out.println(JsonUtils.toString(1.00));
		System.out.println(JsonUtils.toString("test"));
		System.out.println(JsonUtils.toString(RegexFlag.CANON_EQ));
	}
}
