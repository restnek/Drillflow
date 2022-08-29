package com.hashmapinc.tempus.witsml.valve.mock.csv;

import com.hashmapinc.tempus.WitsmlObjects.v1311.CsLogCurveInfo;
import com.hashmapinc.tempus.WitsmlObjects.v1311.ObjLog;
import com.hashmapinc.tempus.witsml.valve.mock.Field;
import com.hashmapinc.tempus.witsml.valve.mock.MockObjLogsGeneratorException;
import com.hashmapinc.tempus.witsml.valve.mock.TimeUtil;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.core.io.ClassPathResource;

public class CsvDrillState {

    private static final String DEFAULT_DELIMITER = ",";
    private static final String CLASSPATH_PREFIX = "classpath:";

    private static final Random RANDOM = new Random();

    private final CSVFormat csvFormat;
    private final Map<Field, Integer> csvIndexByField;
    private final String csvFile;
    private final Charset csvCharset;
    private final String csvDecimalSeparator;
    private final Duration csvTimeStep;

    private final String fieldDefaultValue;
    private final boolean enableRandomFieldIfUndefined;
    private final int fieldRandomOrigin;
    private final int fieldRandomBound;

    private Instant currentTime;

    private final ObjLog log;

    public CsvDrillState(CsvConfig config, ObjLog log) {
        csvFormat = initFormat(config);
        csvFile = config.getCsvFile();
        csvCharset = config.getCsvCharset();
        csvDecimalSeparator = config.getCsvDecimalSeparator();
        csvTimeStep = config.getCsvTimeStep();

        fieldDefaultValue = config.getFieldDefaultValue();
        enableRandomFieldIfUndefined = config.enableRandomFieldIfUndefined();
        fieldRandomOrigin = config.getFieldRandomOrigin();
        fieldRandomBound = config.getFieldRandomBound();

        csvIndexByField = config.getFieldToIndexMap();

        currentTime = Instant.parse(log.getStartDateTimeIndex());

        this.log = log;
    }

    private CSVFormat initFormat(CsvConfig config) {
        CSVFormat.Builder csvFormatBuilder = CSVFormat.DEFAULT.builder()
                .setDelimiter(config.getCsvDelimiter(DEFAULT_DELIMITER))
                .setIgnoreEmptyLines(true)
                .setIgnoreSurroundingSpaces(true);

        if (config.csvContainsHeader()) {
            csvFormatBuilder.setHeader().setSkipHeaderRecord(true);
        }

        return csvFormatBuilder.build();
    }

    public List<String> read(int offset, int size) {
        try (
                Reader reader = getCsvReader();
                CSVParser parser = csvFormat.parse(reader)
        ) {
            return parser.stream()
                    .skip(offset)
                    .limit(size)
                    .map(this::buildDataString)
                    .collect(Collectors.toList());
        } catch (Exception exception) {
            throw new MockObjLogsGeneratorException(exception);
        }
    }

    private Reader getCsvReader() throws IOException {
        InputStream inputStream = csvFile.startsWith(CLASSPATH_PREFIX)
                ? new ClassPathResource(csvFile.substring(CLASSPATH_PREFIX.length())).getInputStream()
                : new FileInputStream(csvFile);
        return new InputStreamReader(inputStream, csvCharset);
    }

    private String buildDataString(CSVRecord csvRecord) {
        return log.getLogCurveInfo().stream()
                .map(CsLogCurveInfo::getUid)
                .map(u -> buildDataString(u, csvRecord))
                .collect(Collectors.joining(","));
    }

    private String buildDataString(String fieldUid, CSVRecord csvRecord) {
        return Field.byUid(fieldUid)
                .map(csvIndexByField::get)
                .flatMap(i -> getValueFromRecord(csvRecord, i))
                .map(this::putRightDecimalPoint)
                .orElseGet(() -> getDefaultValue(fieldUid));
    }

    private Optional<String> getValueFromRecord(CSVRecord csvRecord, int index) {
        return csvRecord.stream()
                .skip(index)
                .findFirst();
    }

    private String putRightDecimalPoint(String value) {
        return ".".equals(csvDecimalSeparator)
                ? value
                : value.replace(csvDecimalSeparator, ".");
    }

    private String getDefaultValue(String fieldUid) {
        if (Field.TIME.getUid().equals(fieldUid)) {
            String result = TimeUtil.format(currentTime);
            currentTime = currentTime.plus(csvTimeStep);
            return result;
        } else if (enableRandomFieldIfUndefined) {
            return fieldRandomOrigin + Integer.toString(RANDOM.nextInt(fieldRandomBound));
        }
        return fieldDefaultValue;
    }
}
