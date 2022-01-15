package com.alexp.cli;

import com.github.rvesse.airline.model.ArgumentsMetadata;
import com.github.rvesse.airline.model.OptionMetadata;
import com.github.rvesse.airline.parser.ParseState;
import com.github.rvesse.airline.types.TypeConverter;
import com.github.rvesse.airline.types.TypeConverterProvider;
import com.github.rvesse.airline.types.numerics.NumericTypeConverter;

import java.time.LocalDate;

public class LocalDateConverterProvider implements TypeConverterProvider {
  @Override
  public <T> TypeConverter getTypeConverter(OptionMetadata option, ParseState<T> state) {
    return LOCAL_DATE_CONVERTER;
  }

  @Override
  public <T> TypeConverter getTypeConverter(ArgumentsMetadata arguments, ParseState<T> state) {
    return LOCAL_DATE_CONVERTER;
  }

  private static final TypeConverter LOCAL_DATE_CONVERTER =
      new TypeConverter() {
        @Override
        public Object convert(String name, Class<?> type, String value) {
          return LocalDate.parse(value);
        }

        @Override
        public void setNumericConverter(NumericTypeConverter converter) {}
      };
}
