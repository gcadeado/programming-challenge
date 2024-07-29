package de.bcxp.challenge.adapters.csv;

import com.opencsv.CSVReader;
import com.opencsv.bean.*;
import com.opencsv.exceptions.CsvBadConverterException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import org.apache.commons.collections4.ListValuedMap;
import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;

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
    protected RequiredFieldMapByName<T> fieldMap = null;
    private String locale = null;

    /**
     * Note: This function must be called before setType() when building object
     */
    public void setLocale(String locale) {
        this.locale = locale;
    }

    @Override
    protected void initializeFieldMap() {
        this.fieldMap = new RequiredFieldMapByName<>(this.errorLocale);
        this.fieldMap.setColumnOrderOnWrite(this.writeOrder);
    }

    @Override
    protected void loadUnadornedFieldMap(ListValuedMap<Class<?>, Field> fields) {
        fields.entries().stream().filter((entry) -> !Serializable.class.isAssignableFrom(entry.getKey()) ||
                                                !"serialVersionUID".equals(entry.getValue().getName())).forEach((entry) -> {
            CsvConverter converter = this.determineConverter(entry.getValue(),
                    entry.getValue().getType(),
                    null,
                    null,
                    null);
            this.fieldMap.put(entry.getValue().getName().toUpperCase(),
                    new BeanFieldSingleValue<>(entry.getKey(),
                            entry.getValue(),
                            true,
                            this.errorLocale,
                            converter,
                            null,
                            null));
        });
    }

    @Override
    protected BeanField<T, String> findField(int col) throws CsvBadConverterException {
        BeanField<T, String> beanField = null;
        String columnName = this.getColumnName(col);
        if (columnName == null) {
            return null;
        } else {
            columnName = columnName.trim();
            if (!columnName.isEmpty()) {
                beanField = this.fieldMap.get(columnName.toUpperCase());
            }
            return beanField;
        }
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

    @Override
    public void captureHeader(CSVReader reader) throws IOException, CsvRequiredFieldEmptyException {
        if (this.type == null) {
            throw new IllegalStateException(ResourceBundle.getBundle("opencsv", this.errorLocale)
                    .getString("type.unset"));
        } else {
            String[] header = ArrayUtils.nullToEmpty(reader.readNextSilently());

            for (int i = 0; i < header.length; ++i) {
                if (header[i] == null) {
                    header[i] = "";
                }
            }

            this.headerIndex.initializeHeaderIndex(header);
            List<FieldMapByNameEntry<T>> missingRequiredHeaders = this.fieldMap.determineMissingRequiredHeaders(header,
                    getColumnMapping().keySet().toArray(new String[0]));
            if (!missingRequiredHeaders.isEmpty()) {
                String[] requiredHeaderNames = new String[missingRequiredHeaders.size()];
                List<Field> requiredFields = new ArrayList<>(missingRequiredHeaders.size());

                for (int i = 0; i < missingRequiredHeaders.size(); ++i) {
                    FieldMapByNameEntry<T> fme = missingRequiredHeaders.get(i);
                    if (fme.isRegexPattern()) {
                        requiredHeaderNames[i] = String.format(ResourceBundle.getBundle("opencsv", this.errorLocale)
                                .getString("matching"), fme.getName());
                    } else {
                        requiredHeaderNames[i] = fme.getName();
                    }

                    requiredFields.add(fme.getField().getField());
                }

                String missingRequiredFields = String.join(", ", requiredHeaderNames);
                String allHeaders = String.join(",", header);
                CsvRequiredFieldEmptyException e = new CsvRequiredFieldEmptyException(this.type,
                        requiredFields,
                        String.format(ResourceBundle.getBundle("opencsv", this.errorLocale)
                                .getString("header.required.field.absent"), missingRequiredFields, allHeaders));
                e.setLine(header);
                throw e;
            }
        }
    }
}
