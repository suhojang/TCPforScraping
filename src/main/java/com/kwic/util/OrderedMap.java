package com.kwic.util;

import java.util.Locale;

import org.apache.commons.collections.map.ListOrderedMap;

import egovframework.rte.psl.dataaccess.util.CamelUtil;

/**
 * Camel Case 표기법 변환 처리를 포함하는 Map 확장 클래스
 * <p>
 * <b>NOTE</b>: commons Collections 의 ListOrderedMap 을
 * extends 하고 있으며 Map 의 key 를 입력 시 Camel Case 표기법으로
 * 변경하여 처리하는 Map 의 구현체이다. (iBatis 의 경우 egovMap 으로 결과 조회
 * 시 별도의 alias 없이 DB 칼럼명 그대로 조회하는 것 만으로도 일반적인 VO 의
 * attribute (camel case) 에 대한 resultMap 과 같은 효과를 낼 수
 * 있도록 추가하였음)
 * </p>
 * @author 장정훈
 * @since 2016.11.08
 * @version 1.0
 * @see <pre>
 * </pre>
 */
public class OrderedMap extends ListOrderedMap {

	private static final long serialVersionUID = 6723434363565852261L;

	public Object put(Object key, Object value) {
		return super.put(((String) key).toUpperCase(Locale.KOREA), value);
	}
	/**
	 * key 에 대하여 Camel Case 변환하여 super.put
	 * (ListOrderedMap) 을 호출한다.
	 * @param key
	 *        - '_' 가 포함된 변수명
	 * @param value
	 *        - 명시된 key 에 대한 값 (변경 없음)
	 * @return previous value associated with specified
	 *         key, or null if there was no mapping for
	 *         key
	 */
	public Object putCamel(Object key, Object value) {
		return super.put(CamelUtil.convert2CamelCase((String) key), value);
	}

}
