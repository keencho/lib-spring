package com.keencho.lib.spring.excel;

import com.keencho.lib.spring.excel.annotation.KcExcelColumn;
import com.keencho.lib.spring.excel.annotation.KcExcelDocument;
import com.keencho.lib.spring.excel.exception.KcExcelException;
import com.keencho.lib.spring.excel.exception.KcExcelNoDataException;
import com.keencho.lib.spring.excel.exception.KcExcelNotEffectiveClassException;
import com.keencho.lib.spring.excel.resolver.KcExcelMaskingDefaultResolver;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

public class KcExcelDownloader {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private SXSSFWorkbook workbook;
    private boolean showSequence = false;
    private final LinkedHashMap<String, List<?>> data;

    public KcExcelDownloader(LinkedHashMap<String, List<?>>  data) {
        this.data = data;
        this.workbook = new SXSSFWorkbook();
    }

    public KcExcelDownloader(LinkedHashMap<String, List<?>>  data, boolean showSequence) {
        this(data);
        this.showSequence = showSequence;
    }

    private void create() throws IOException {
        // 엑셀 어노테이션 검증
        data.forEach((k,v) -> {
            v.stream().findFirst().ifPresent(i -> {
                var clazz = i.getClass();
                if (!clazz.isAnnotationPresent(KcExcelDocument.class)) {
                    if (logger.isDebugEnabled()) {
                        logger.info("error occurred while validate excel document object: KcExcelDocument annotation must be presented!");
                    }
                    throw new KcExcelNotEffectiveClassException();
                }
            });
        });

        /////////////////////////////////////////////////
        //////////////////// 엑셀 write 시작
        /////////////////////////////////////////////////
        var sheetNo = 0;
        for (var key : this.data.keySet()) {
            var sheet = workbook.createSheet();
            var value = data.get(key);

            if (value.isEmpty()) {
                throw new KcExcelNoDataException();
            }

            var clazz = value.get(0).getClass();

            var fields = Arrays.stream(clazz.getDeclaredFields())
                    .filter(f -> f.getAnnotation(KcExcelColumn.class) != null).toList();

            workbook.setSheetName(sheetNo++, StringUtils.hasText(key) ? key : "Sheet" + sheetNo);

            var rowCount = 0;
            var columnCount = 0;
            var row = sheet.createRow(rowCount++);


            /////////////////////////////////////////////////
            //////////////////// 헤더 정의
            /////////////////////////////////////////////////

            if (this.showSequence) {
                var cell = row.createCell(columnCount ++);
                cell.setCellValue("번호");
            }

            for (var headerField : fields) {
                var headerColumn = headerField.getAnnotation(KcExcelColumn.class);
                var headerCell = row.createCell(columnCount++);
                headerCell.setCellValue(headerColumn.headerName());
            }

            /////////////////////////////////////////////////
            //////////////////// 본문 정의
            /////////////////////////////////////////////////
            for (int i = 0; i < value.size(); i ++) {
                var data = value.get(i);
                row = sheet.createRow(rowCount ++);
                var bodyColumnIdx = 0;

                if (this.showSequence) {
                    var cell = row.createCell(bodyColumnIdx ++);
                    cell.setCellValue(i + 1);
                }

                for (var field : fields) {
                    var column = field.getAnnotation(KcExcelColumn.class);
                    var cell = row.createCell(bodyColumnIdx++);

                    Object fieldValue;
                    try {
                        fieldValue = new PropertyDescriptor(field.getName(), clazz).getReadMethod().invoke(data);
                    } catch (IntrospectionException  | InvocationTargetException | IllegalAccessException ex) {
                        // getter 없음, 리플렉션 실패
                        if (logger.isDebugEnabled()) {
                            logger.info("error occurred white invoke getter method via reflection: " + ex.getMessage());
                        }
                        throw new KcExcelException();
                    }

                    var resolverClass = column.resolver();

                    if (resolverClass == KcExcelMaskingDefaultResolver.class) {
                        cell.setCellValue((String) fieldValue);
                    } else {
                        try {
                            cell.setCellValue((String) resolverClass.getMethod("apply", Object.class).invoke(resolverClass.getDeclaredConstructor().newInstance(), fieldValue));
                        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException ex) {
                            if (logger.isDebugEnabled()) {
                                logger.info("error occurred while masking data with custom masking resolver, default value will be set: " + ex.getMessage());
                            }
                            cell.setCellValue((String) fieldValue);
                        }
                    }
                }

                sheet.flushRows(value.size());
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void export(HttpServletResponse response, String fileName) throws IOException {
        if (!fileName.toLowerCase().endsWith(".xlsx") && !fileName.toLowerCase().contains("xlsx")) {
            fileName += ".xlsx";
        }

        fileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replace("+", "%20");
        response.setHeader("Content-disposition", "attachment; filename=" + fileName);

        this.create();

        var outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();

        outputStream.close();
    }
}
