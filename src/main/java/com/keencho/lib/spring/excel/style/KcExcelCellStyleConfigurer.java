package com.keencho.lib.spring.excel.style;

import org.apache.poi.ss.usermodel.*;

public interface KcExcelCellStyleConfigurer {

    short fillForegroundColor();

    FillPatternType fillPatternType();

    HorizontalAlignment horizontalAlignment();

    VerticalAlignment verticalAlignment();

    Font font(Font font);

    BorderStyle borderStyle();

    short borderColor();

}
