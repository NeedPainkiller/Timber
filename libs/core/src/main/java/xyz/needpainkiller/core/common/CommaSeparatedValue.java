package xyz.needpainkiller.core.common;

import com.fasterxml.jackson.dataformat.csv.CsvSchema;

public interface CommaSeparatedValue {

    CsvSchema makeSchema();
}
