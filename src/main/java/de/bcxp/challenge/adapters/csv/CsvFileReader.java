package de.bcxp.challenge.adapters.csv;

import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.MappingStrategy;
import de.bcxp.challenge.exceptions.CsvFileFormatException;
import de.bcxp.challenge.exceptions.FileNotFoundException;
import de.bcxp.challenge.ports.IFileReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;

/**
 * Base class for reading CSV files.
 * <p>
 * This class utilizes OpenCSV in conjunction with Java Beans to parse and map CSV data into Java objects.
 */

public abstract class CsvFileReader<T> implements IFileReader<T> {

    // Required fields
    private final InputStream inputStream;

    // Optional fields
    protected String locale;
    private final char separator;
    private final Charset charset;

    protected CsvFileReader(Builder<T> builder) {
        this.inputStream = builder.inputStream;
        this.locale = builder.locale;
        this.separator = builder.separator;
        this.charset = builder.charset;
    }

    protected abstract LocalizedHeaderColumnNameTranslateMappingStrategy<T> createMappingStrategy();

    @Override
    public List<T> readData() throws CsvFileFormatException, FileNotFoundException {
        try {
            if (inputStream == null || inputStream.available() == 0) {
                throw new FileNotFoundException("The file is empty or not found");
            }
        } catch (IOException e) {
            throw new FileNotFoundException("The file is empty or not found");
        }

        try (InputStreamReader reader = new InputStreamReader(inputStream, charset)) {
            // Instantiate custom mapping strategy to leverage CSV to bean with localizations
            LocalizedHeaderColumnNameTranslateMappingStrategy<T> strategy = createMappingStrategy();
            CsvToBean<T> csvToBean = new CsvToBeanBuilder<T>(reader)
                    .withThrowExceptions(true)
                    .withSeparator(separator)
                    .withMappingStrategy(strategy)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            return csvToBean.parse();
        } catch (Exception e) {
            throw new CsvFileFormatException("Error reading CSV file", e);
        }
    }

    /**
     * Builder base class for constructing a CsvFileReader using the Builder Pattern
     */
    public static abstract class Builder<T> {
        // Required fields
        private final InputStream inputStream;

        // Optional fields
        private String locale = Locale.getDefault().getLanguage();
        private char separator = ',';
        private Charset charset = StandardCharsets.UTF_8;

        public Builder(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        /**
         *
         * @param locale the alpha-2 or alpha-3 language code (default: default Locale set by the Java Virtual Machine)
         */
        public Builder<T> withLocale(String locale) {
            this.locale = locale;
            return this;
        }

        /**
         *
         * @param separator the character used to separate values (default: ',')
         */
        public Builder<T> withSeparator(char separator) {
            this.separator = separator;
            return this;
        }

        /**
         *
         * @param charset the charset used to parse the file (default: UTF-8)
         */
        public Builder<T> withCharset(Charset charset) {
            this.charset = charset;
            return this;
        }

        public abstract CsvFileReader<T> build();
    }
}