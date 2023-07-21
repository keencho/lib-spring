package com.keencho.lib.spring.excel;

import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;

import java.util.function.Supplier;

public class KcJpaExcelDownloader {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final SXSSFWorkbook workbook;
    private boolean showSequence = false;

    public KcJpaExcelDownloader(SXSSFWorkbook workbook, Supplier<Page<?>> querySupplier, boolean showSequence) {
        this.workbook = new SXSSFWorkbook();
        this.showSequence = showSequence;
    }
}
