package com.keencho.lib.spring.test.utils;

import org.junit.jupiter.api.Assertions;

public class KcAssertions extends Assertions {
    public static void assertMultipleEquals(Object... objects) {
        if (objects == null || objects.length == 1) return;

        for (var i = 0; i < objects.length; i ++) {
            if (i != objects.length - 1) {
                assertEquals(objects[i], objects[i + 1]);
            }
        }
    }
}
