package net.croz.nrich.excel.converter;

import hr.apis.m19.jlprs.infrastructure.excel.ExcelExportConstants;
import lombok.Getter;
import org.apache.poi.ss.usermodel.Cell;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Date;

@Getter
public enum CellValueConverter {

    DATE(Date.class) {
        @Override
        public void setCellValue(final Cell cell, final Object value) {
            cell.setCellValue((Date) value);
        }

        @Override
        public String getDataFormat() {
            return ExcelExportConstants.DATE_FORMAT;
        }
    },

    INSTANT(Instant.class) {
        @Override
        public void setCellValue(final Cell cell, final Object value) {
            cell.setCellValue(new Date(((Instant) value).toEpochMilli()));
        }

        @Override
        public String getDataFormat() {
            return ExcelExportConstants.DATE_FORMAT;
        }
    },

    LOCAL_DATE(LocalDate.class) {
        @Override
        public void setCellValue(final Cell cell, final Object value) {
            cell.setCellValue((LocalDate) value);
        }

        @Override
        public String getDataFormat() {
            return ExcelExportConstants.DATE_FORMAT;
        }
    },

    LOCAL_DATE_TIME(LocalDateTime.class) {
        @Override
        public void setCellValue(final Cell cell, final Object value) {
            cell.setCellValue((LocalDateTime) value);
        }

        @Override
        public String getDataFormat() {
            return ExcelExportConstants.DATE_TIME_FORMAT;
        }
    },

    ZONED_DATE_TIME(ZonedDateTime.class) {
        @Override
        public void setCellValue(final Cell cell, final Object value) {
            cell.setCellValue(((ZonedDateTime) value).toLocalDateTime());
        }

        @Override
        public String getDataFormat() {
            return ExcelExportConstants.DATE_TIME_FORMAT;
        }
    },

    BIG_DECIMAL(BigDecimal.class) {
        @Override
        public void setCellValue(final Cell cell, final Object value) {
            cell.setCellValue(((Number) value).doubleValue());
        }

        @Override
        public String getDataFormat() {
            return ExcelExportConstants.DECIMAL_NUMBER_FORMAT;
        }
    },
    FLOAT(Float.class) {
        @Override
        public void setCellValue(final Cell cell, final Object value) {
            cell.setCellValue(((Number) value).doubleValue());
        }

        @Override
        public String getDataFormat() {
            return ExcelExportConstants.DECIMAL_NUMBER_FORMAT;
        }
    },
    DOUBLE(Double.class) {
        @Override
        public void setCellValue(final Cell cell, final Object value) {
            cell.setCellValue(((Number) value).doubleValue());
        }

        @Override
        public String getDataFormat() {
            return ExcelExportConstants.DECIMAL_NUMBER_FORMAT;
        }
    },

    SHORT(Short.class) {
        @Override
        public void setCellValue(final Cell cell, final Object value) {
            cell.setCellValue(((Number) value).longValue());
        }

        @Override
        public String getDataFormat() {
            return ExcelExportConstants.INTEGER_NUMBER_FORMAT;
        }
    },

    INTEGER(Integer.class) {
        @Override
        public void setCellValue(final Cell cell, final Object value) {
            cell.setCellValue(((Number) value).longValue());
        }

        @Override
        public String getDataFormat() {
            return ExcelExportConstants.INTEGER_NUMBER_FORMAT;
        }
    },

    LONG(Long.class) {
        @Override
        public void setCellValue(final Cell cell, final Object value) {
            cell.setCellValue(((Number) value).longValue());
        }

        @Override
        public String getDataFormat() {
            return ExcelExportConstants.INTEGER_NUMBER_FORMAT;
        }
    };

    private final Class<?> type;

    CellValueConverter(final Class<?> type) {
        this.type = type;
    }

    public static CellValueConverter forType(final Class<?> type) {
        return Arrays.stream(values()).filter(value -> value.getType().equals(type)).findFirst().orElse(null);
    }


    public abstract void setCellValue(final Cell cell, final Object value);

    public abstract String getDataFormat();
}
