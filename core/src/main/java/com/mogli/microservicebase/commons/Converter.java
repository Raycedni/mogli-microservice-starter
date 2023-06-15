package com.mogli.microservicebase.commons;

import java.util.List;
import java.util.function.Supplier;

public interface Converter<E, D> {
    D convertToDto(E source, D target);

    E convertToEntity(D source, E target);

    default List<D> convertToDto(List<E> source, Supplier<D> target) {
        return source.stream().map((e) -> {
            return this.convertToDto(e, target.get());
        }).toList();
    }

    default D convertToDto(E source, Supplier<D> target) {
        return convertToDto(source, target.get());
    }
}
