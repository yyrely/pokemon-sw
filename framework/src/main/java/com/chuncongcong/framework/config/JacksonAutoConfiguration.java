package com.chuncongcong.framework.config;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;

import java.time.format.DateTimeFormatter;

@AutoConfiguration
public class JacksonAutoConfiguration {

    // 定义时间格式
    private static final String DATE_TIME_FORMAT = "yyyy/MM/dd HH:mm:ss";
    private static final String DATE_FORMAT = "yyyy/MM/dd";
    private static final String TIME_FORMAT = "HH:mm:ss";

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
        return builder -> {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(TIME_FORMAT);

            builder.simpleDateFormat(DATE_TIME_FORMAT);
            builder.serializers(
                    new LocalDateTimeSerializer(dateTimeFormatter),
                    new LocalDateSerializer(dateFormatter),
                    new LocalTimeSerializer(timeFormatter)
            );
            builder.deserializers(
                    new LocalDateTimeDeserializer(dateTimeFormatter),
                    new LocalDateDeserializer(dateFormatter),
                    new LocalTimeDeserializer(timeFormatter)
            );
            builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        };
    }
}