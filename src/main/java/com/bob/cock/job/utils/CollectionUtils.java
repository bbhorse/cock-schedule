package com.bob.cock.job.utils;

import java.util.Collection;

public final class CollectionUtils {
	public static boolean isEmpty(Collection<?> collection) {
		return (collection == null || collection.isEmpty());
	}
	
	private CollectionUtils() {
	}
}
