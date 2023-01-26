package com.keencho.lib.spring.test.utils;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class SpringUtils {

    public static Pageable buildPageable(int pageSize, int offset) {
       return buildPageable(pageSize, offset, null);
    }

    public static Pageable buildPageable(int pageSize, int offset, Sort sort) {
        return new Pageable() {
            @Override
            public int getPageNumber() {
                return 0;
            }

            @Override
            public int getPageSize() {
                return pageSize;
            }

            @Override
            public long getOffset() {
                return offset;
            }

            @Override
            public Sort getSort() {
                return sort;
            }

            @Override
            public Pageable next() {
                return null;
            }

            @Override
            public Pageable previousOrFirst() {
                return null;
            }

            @Override
            public Pageable first() {
                return null;
            }

            @Override
            public Pageable withPage(int pageNumber) {
                return null;
            }

            @Override
            public boolean hasPrevious() {
                return false;
            }
        };
    }
}
