package com.sparta.shop_sparta.util.validator.member;

import jakarta.persistence.Column;
import java.lang.reflect.Field;

public class EntityFieldValidator {
    public <T> Boolean validateParams(T entity) {
        Field[] fields = entity.getClass().getDeclaredFields();

        for (Field field : fields) {
            Column column = field.getAnnotation(Column.class);

            if (column != null && !column.nullable()) {
                field.setAccessible(true);
                try {
                    if (field.get(entity) == null) {
                        return false;
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        return true;
    }
}
