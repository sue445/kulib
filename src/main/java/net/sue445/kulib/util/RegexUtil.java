package net.sue445.kulib.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility for regular expression
 * @author sue445
 * @since 0.0.3
 */
public final class RegexUtil {
	private RegexUtil(){

	}

	/**
	 * get subexpression group
	 *
	 * @param regex
	 * @param str
	 * @return subexpression group. if not matched, return empty
	 */
	public static List<String> getSubexpressionGroup(String regex, String str) {
		Matcher matcher = Pattern.compile(regex).matcher(str);
		if (!matcher.find()) {
			return new ArrayList<String>();
		}

		List<String> groupList = new ArrayList<String>(matcher.groupCount());

		for (int group = 0; group <= matcher.groupCount(); group++) {
			groupList.add(matcher.group(group));
		}
		return groupList;
	}

	/**
	 * whether inexact match
	 * @param regex
	 * @param str
	 * @return
	 */
	public static boolean isInexactMatch(String regex, String str){
		List<String> list = getSubexpressionGroup(regex, str);
		return EmptyCheckUtil.isNotEmpty(list);
	}

}
