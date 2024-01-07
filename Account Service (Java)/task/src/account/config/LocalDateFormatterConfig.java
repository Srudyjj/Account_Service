package account.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.Formatter;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Locale;
import java.util.regex.Pattern;

@Configuration
public class LocalDateFormatterConfig {

    @Bean
    public Formatter<LocalDate> localDateFormatter() {
        return new Formatter<LocalDate>() {
            @Override
            public LocalDate parse(String text, Locale locale) throws ParseException {

                if (Pattern.matches("\\d{2}-\\d{4}", text)) {
                    DateTimeFormatter dtf = new DateTimeFormatterBuilder().appendPattern("MM-yyyy")
                            // default value for day
                            .parseDefaulting(ChronoField.DAY_OF_MONTH, 1).toFormatter();
                    return LocalDate.parse(text, dtf);
                }

                return LocalDate.parse(text, DateTimeFormatter.ISO_DATE);
            }

            @Override
            public String print(LocalDate object, Locale locale) {
                return DateTimeFormatter.ISO_DATE.format(object);
            }
        };
    }
}
