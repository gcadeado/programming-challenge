package de.bcxp.challenge.adapters.csv;

import com.opencsv.bean.FieldMapByName;
import com.opencsv.bean.FieldMapByNameEntry;
import org.apache.commons.lang3.ArrayUtils;

import java.util.*;
import java.util.stream.Collectors;

public class RequiredFieldMapByName<T> extends FieldMapByName<T> {
    public RequiredFieldMapByName(Locale errorLocale) {
        super(errorLocale);
    }

    public List<FieldMapByNameEntry<T>> determineMissingRequiredHeaders(
            String[] headersPresent,
            String[] requiredHeaders) {

        List<FieldMapByNameEntry<T>> missingRequiredHeaders = new LinkedList<>();

        for (String header : requiredHeaders) {
            if (!Arrays.stream(headersPresent).map(String::toUpperCase).collect(Collectors.toList()).contains(header)) {
                missingRequiredHeaders.add(new FieldMapByNameEntry<>(header, this.simpleMap.get(header), false));
            }
        }

        return missingRequiredHeaders;
    }

    public String[] generateHeader(T bean) {
        List<String> headerList = new ArrayList<>(this.simpleMap.keySet());
        return headerList.toArray(ArrayUtils.EMPTY_STRING_ARRAY);
    }
}
