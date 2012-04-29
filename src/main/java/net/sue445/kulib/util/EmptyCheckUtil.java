package net.sue445.kulib.util;

import java.util.Collection;
import java.util.Map;

import org.slim3.util.StringUtil;

/**
 * isEmpty util for Array, {@link Collection}, {@link Map}<br/>
 * String methods is not support, because supported by Slim3 {@link StringUtil}
 * @author sue445
 *
 */
public final class EmptyCheckUtil {
	private EmptyCheckUtil(){

	}

	/**
	 *
	 * @param coll
	 * @return
	 */
	public static boolean isEmpty(Collection<?> coll){
		return coll == null || coll.size() == 0;
	}

	/**
	 *
	 * @param coll
	 * @return
	 */
	public static boolean isNotEmpty(Collection<?> coll){
		return !isEmpty(coll);
	}

	/**
	 *
	 * @param map
	 * @return
	 */
	public static boolean isEmpty(Map<?, ?> map){
		return map == null || map.size() == 0;
	}

	/**
	 *
	 * @param map
	 * @return
	 */
	public static boolean isNotEmpty(Map<?, ?> map){
		return !isEmpty(map);
	}

	/**
	 *
	 * @param array
	 * @return
	 */
	public static <T> boolean isEmpty(T[] array){
		return array == null || array.length == 0;
	}

	/**
	 *
	 * @param array
	 * @return
	 */
	public static <T> boolean isNotEmpty(T[] array){
		return !isEmpty(array);
	}
}
