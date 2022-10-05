package com.keencho.lib.spring.excel;

import com.keencho.lib.spring.common.utils.KcReflectionUtils;
import com.keencho.lib.spring.excel.annotation.KcExcelColumn;
import com.keencho.lib.spring.excel.annotation.KcExcelDocument;
import com.keencho.lib.spring.excel.exception.KcExcelNoDataException;
import com.keencho.lib.spring.excel.exception.KcExcelNotEffectiveClassException;
import com.keencho.lib.spring.excel.resolver.KcExcelMaskingDefaultResolver;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

public class KcExcelDownloader {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final SXSSFWorkbook workbook;
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

    private enum CellPosition { HEADER, BODY }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void configureCellStyle(Cell cell, CellPosition cellPosition, KcExcelColumn kcExcelColumn) {

        var configurerClass = cellPosition == CellPosition.HEADER ? kcExcelColumn.headerStyleConfigurer() : kcExcelColumn.bodyStyleConfigurer();
        var newInstance = KcReflectionUtils.initNewInstance(configurerClass);

        var style = this.workbook.createCellStyle();
        var font = this.workbook.createFont();
        style.setFillForegroundColor(newInstance.fillForegroundColor());
        style.setFillPattern(newInstance.fillPatternType());
        style.setAlignment(newInstance.horizontalAlignment());
        style.setVerticalAlignment(newInstance.verticalAlignment());
        style.setFont(newInstance.font(font));

        var borderStyle = newInstance.borderStyle();
        style.setBorderTop(borderStyle);
        style.setBorderRight(borderStyle);
        style.setBorderBottom(borderStyle);
        style.setBorderLeft(borderStyle);

        var borderColor = newInstance.borderColor();
        style.setTopBorderColor(borderColor);
        style.setRightBorderColor(borderColor);
        style.setBottomBorderColor(borderColor);
        style.setLeftBorderColor(borderColor);

        cell.setCellStyle(style);
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
            var excelDocumentAnnotation = clazz.getAnnotation(KcExcelDocument.class);

            var fields = Arrays.stream(clazz.getDeclaredFields())
                    .filter(f -> f.getAnnotation(KcExcelColumn.class) != null).toList();

            workbook.setSheetName(sheetNo++, StringUtils.hasText(key) ? key : "Sheet" + sheetNo);

            var rowCount = 0;
            var columnIdx = 0;
            var row = sheet.createRow(rowCount++);

            /////////////////////////////////////////////////
            //////////////////// 헤더 정의
            /////////////////////////////////////////////////
            if (this.showSequence) {
                var cell = row.createCell(columnIdx ++);
                cell.setCellValue("번호");
                sheet.setColumnWidth(0, 50 * 32);
            }

            for (var h = 0; h < fields.size(); h++) {
                var headerField = fields.get(h);
                var headerColumn = headerField.getAnnotation(KcExcelColumn.class);
                var headerCell = row.createCell(columnIdx);

                headerCell.setCellValue(headerColumn.headerName());
                this.configureCellStyle(headerCell, CellPosition.HEADER, headerColumn);

                sheet.setColumnWidth(columnIdx, headerColumn.width() * 32);

                if (h == 0) {
                    row.setHeight((short) (excelDocumentAnnotation.headerHeight() * 20));
                }

                columnIdx++;
            }

            /////////////////////////////////////////////////
            //////////////////// 본문 정의
            /////////////////////////////////////////////////
            for (int i = 0; i < value.size(); i ++) {
                var data = value.get(i);
                row = sheet.createRow(rowCount ++);
                var bodyColumnIdx = 0;

                if (i == 0) {
                    row.setHeight((short) (excelDocumentAnnotation.bodyHeight() * 20));
                }

                if (this.showSequence) {
                    var cell = row.createCell(bodyColumnIdx ++);
                    cell.setCellValue(i + 1);
                    sheet.setColumnWidth(0, 50 * 32);
                }

                for (var field : fields) {
                    var column = field.getAnnotation(KcExcelColumn.class);
                    var cell = row.createCell(bodyColumnIdx ++);

                    var fieldValue = KcReflectionUtils.invokeGetter(clazz, field.getName(), data);
                    var resolverClass = column.resolver();

                    if (resolverClass == KcExcelMaskingDefaultResolver.class) {
                        cell.setCellValue((String) fieldValue);
                    } else {
                        var instance = KcReflectionUtils.initNewInstance(resolverClass);
                        cell.setCellValue(instance.apply(fieldValue));
                    }

                    this.configureCellStyle(cell, CellPosition.BODY, column);
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
