package de.bcxp.challenge.adapters.csv;

import com.opencsv.bean.*;
import com.opencsv.exceptions.CsvBadConverterException;

import java.lang.reflect.Field;
import java.util.Currency;
import java.util.UUID;

/**
 * A custom extension of OpenCSV's HeaderColumnNameTranslateMappingStrategy class to support localization when converting field values.
 * <p>
 * This class addresses the need for localized field value conversion without requiring annotations on entity classes.
 * OpenCSV does not provide an API for custom validations that avoids the use of annotations, and this extension offers
 * a solution to that limitation.
 * <p>
 * Note: It is generally recommended that entities should not contain logic regarding data reading or extraction.
 * This approach avoids the necessity of annotating entity classes with CSV annotations.
 *
 * @see <a href="https://sourceforge.net/p/opencsv/feature-requests/125/">Feature Request Thread on SourceForge</a>
 */
public class LocalizedHeaderColumnNameTranslateMappingStrategy<T> extends HeaderColumnNameTranslateMappingStrategy<T> {
    private String locale = null;

    /**
     * Note: This function must be called before setType() when building object
     */
    public void setLocale(String locale) {
        this.locale = locale;
    }

    @Override
    protected CsvConverter determineConverter(
            Field field,
            Class<?> elementType,
            String locale,
            String writeLocale,
            Class<? extends AbstractCsvConverter> customConverter)
            throws CsvBadConverterException {
        CsvConverter converter;

        // Ignoring all CsvAnnotations since we don't want to annotate entities for csv

        if (elementType.equals(Currency.class)) {
            converter = new ConverterCurrency(this.errorLocale);
        } else if (elementType.isEnum()) {
            converter = new ConverterEnum(elementType, this.locale, this.locale, this.errorLocale);
        } else if (elementType.equals(UUID.class)) {
            converter = new ConverterUUID(this.errorLocale);
        } else {
            converter = new ConverterPrimitiveTypes(elementType, this.locale, this.locale, this.errorLocale);
        }

        return converter;
    }
}
