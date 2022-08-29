package com.hashmapinc.tempus.witsml.valve.mock.csv;

import com.hashmapinc.tempus.witsml.valve.mock.Field;
import java.nio.charset.Charset;
import java.time.Duration;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CsvConfig {
    private static final String CSV_FILE_PARAMETER = "csv.file";
    private static final String CSV_CHARSET_PARAMETER = "csv.charset";
    private static final String CSV_DELIMITER_PARAMETER = "csv.delimiter";
    private static final String CSV_CONTAINS_HEADER_PARAMETER = "csv.contains-header";
    private static final String CSV_FIRST_LINE_PARAMETER = "csv.first-line";
    private static final String CSV_LINE_AMOUNT_PARAMETER = "csv.line-amount";
    private static final String CSV_DECIMAL_SEPARATOR_PARAMETER = "csv.decimal-separator";
    private static final String CSV_TIME_STEP = "csv.time-step";

    private static final String FIELD_DEFAULT_VALUE_PARAMETER = "field.default-value";
    private static final String FIELD_ENABLE_RANDOM_IF_UNDEFINED = "field.random.enable-if-undefined";
    private static final String FIELD_RANDOM_ORIGIN = "field.random.origin";
    private static final String FIELD_RANDOM_BOUND = "field.random.bound";

    private static final String FIELD_TO_INDEX_PARAMETER_PREFIX = "field-to-index.";

    private static final String TRUE_VALUE = "true";

    private final Map<String, String> config;

    private boolean isParameterTrue(String parameter) {
        return TRUE_VALUE.equalsIgnoreCase(config.get(parameter));
    }

    public String getCsvFile() {
        return config.get(CSV_FILE_PARAMETER);
    }

    public Charset getCsvCharset() {
        return Charset.forName(config.get(CSV_CHARSET_PARAMETER));
    }

    public String getCsvDelimiter(String defaultValue) {
        return config.getOrDefault(CSV_DELIMITER_PARAMETER, defaultValue);
    }

    public boolean csvContainsHeader() {
        return isParameterTrue(CSV_CONTAINS_HEADER_PARAMETER);
    }

    public int getCsvFirstLine() {
        return Integer.parseInt(config.get(CSV_FIRST_LINE_PARAMETER));
    }

    public int getCsvLineAmount() {
        return Integer.parseInt(config.get(CSV_LINE_AMOUNT_PARAMETER));
    }

    public String getCsvDecimalSeparator() {
        return config.get(CSV_DECIMAL_SEPARATOR_PARAMETER);
    }

    public Duration getCsvTimeStep() {
        return Duration.parse(config.get(CSV_TIME_STEP));
    }

    public String getFieldDefaultValue() {
        return config.get(FIELD_DEFAULT_VALUE_PARAMETER);
    }

    public boolean enableRandomFieldIfUndefined() {
        return isParameterTrue(FIELD_ENABLE_RANDOM_IF_UNDEFINED);
    }

    public int getFieldRandomOrigin() {
        return Integer.parseInt(config.get(FIELD_RANDOM_ORIGIN));
    }

    public int getFieldRandomBound() {
        return Integer.parseInt(config.get(FIELD_RANDOM_BOUND));
    }

    public Map<Field, Integer> getFieldToIndexMap() {
        return config.entrySet().stream()
                .filter(e -> e.getKey().startsWith(FIELD_TO_INDEX_PARAMETER_PREFIX))
                .collect(
                        Collectors.toMap(
                                e -> Field.valueOf(e.getKey().substring(
                                        FIELD_TO_INDEX_PARAMETER_PREFIX.length())),
                                e -> Integer.parseInt(e.getValue())));
    }
}
