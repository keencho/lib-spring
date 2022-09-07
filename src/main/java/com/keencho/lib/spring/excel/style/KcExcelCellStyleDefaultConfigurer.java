package com.keencho.lib.spring.excel.style;

import org.apache.poi.ss.usermodel.*;

public class KcExcelCellStyleDefaultConfigurer implements KcExcelCellStyleConfigurer {

    @Override
    public short fillForegroundColor() {
        return IndexedColors.WHITE.getIndex();
    }

    @Override
    public FillPatternType fillPatternType() {
        return FillPatternType.NO_FILL;
    }

    @Override
    public HorizontalAlignment horizontalAlignment() {
        return HorizontalAlignment.LEFT;
    }

    @Override
    public VerticalAlignment verticalAlignment() {
        return VerticalAlignment.CENTER;
    }

    @Override
    public Font font(Font font) {
        return font;
    }

    @Override
    public BorderStyle borderStyle() {
        return BorderStyle.NONE;
    }

    @Override
    public short borderColor() {
        return IndexedColors.BLACK.getIndex();
    }
}
