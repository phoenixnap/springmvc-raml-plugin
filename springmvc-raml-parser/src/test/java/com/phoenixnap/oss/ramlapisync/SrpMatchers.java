package com.phoenixnap.oss.ramlapisync;

import java.util.Map;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

/**
 * @author aweisser
 */
public abstract class SrpMatchers {

    public static <K, V> org.hamcrest.Matcher<Map<K, V>> emptyMap() {
        return new BaseMatcher<Map<K, V>>() {
            @Override
            public boolean matches(Object o) {
                return ((Map)o).isEmpty();
            }

            @Override
            public void describeMismatch(Object item, Description description) {
                super.describeMismatch(mapSizeMessage(item), description);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Empty Map");
            }
        };
    }

    public static <K, V> org.hamcrest.Matcher<Map<K, V>> mapWithSize(final int size) {
        return new BaseMatcher<Map<K, V>>() {
            @Override
            public boolean matches(Object o) {
                return ((Map) o).size() == size;
            }

            @Override
            public void describeMismatch(Object item, Description description) {
                super.describeMismatch(mapSizeMessage(item), description);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Map with size "+size);
            }
        };
    }

    private static String mapSizeMessage(Object item) {
        return "Map with size "+((Map)item).size() + ": " +item;
    }

}
