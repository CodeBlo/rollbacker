package org.rollbacker.core.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CompareUtil {

    /**
     * @param c  first class to compare
     * @param c1 second class to compare
     * @return 0 if classes are the same,
     * 1 if c is super class/interface of c1,
     * -1 if c1 is super class/interface of c
     */
    public static int compareClassesByHierarchy(Class<?> c, Class<?> c1) {
        if (c.equals(c1)) return 0;
        return c.isAssignableFrom(c1) ? 1 : -1;
    }
}
