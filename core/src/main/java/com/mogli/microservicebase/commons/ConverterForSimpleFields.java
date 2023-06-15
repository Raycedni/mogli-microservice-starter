package com.ifa12b_gruppe1.klasseninfo.commons;

import com.mogli.microservicebase.commons.Converter;
import com.mogli.microservicebase.commons.Dto;
import com.mogli.microservicebase.commons.Entity;
import lombok.NoArgsConstructor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Als Basis für speziellere Converter, wie z.B. StudentConvert gedacht, um schreibarbeit für simple Felder zu verringern
 *
 * @author LoeffleLuk
 */
@NoArgsConstructor
public abstract class ConverterForSimpleFields<E, D> implements Converter<E, D> {
    private static final String GET_METHOD_PREFIX = "get";
    private static final String SET_METHOD_PREFIX = "set";

    private final ArrayList<Class> UNSUPPORTED_CLASSES = new ArrayList<>(Arrays.asList(
            Entity.class,
            Dto.class,
            List.class));


    /**
     * Convertiert zwischen 2 Objekten, konzipiert wurde es für die Verwendung zwischen {@link Dto}s und {@link Entity}s <br>
     * Diese Methode geht durch die getter Methoden von source und vergleicht sie mit den setter Methoden von dem Target.
     * Es wird dann durch die kompatiblen Setter/Getter paare iteriert (z.B. setId und getId) und die setter Funktionen
     * mit den Werten aus der getter Funktion befüllt.
     *
     * @param source Objekt aus dem Daten extrahiert werden sollen
     * @param target Objekt in dem die extrahierten Daten eingetragen werden
     * @param <S>    Klasse des Source Objekts
     * @param <T>    Klasse des Target Objekts
     * @return Objekt das im target parameter übergeben wurde
     */
    protected <S, T> T convertBetweenSourceAndTarget(S source, T target) {
        try {
            List<String> supportedFields = new ArrayList<>();
            // Holt sich alle getter methoden und filtert alle Felder aus, dessen Datentyp ihren eigenen converter bräuchten
            for (Method method : source.getClass().getMethods()) {
                if (method.getName().contains(GET_METHOD_PREFIX) && !UNSUPPORTED_CLASSES.contains(method.getReturnType()))
                    supportedFields.add(getFieldNameFromMethod(method));
            }

            // Geht die set Methoden der Klasse des Target Objekts durch und filtert alle raus, zu denen keine zugehörige Set-Methode bekannt ist
            List<Method> supportedSetterMethods = Arrays.stream(target.getClass().getMethods())
                    .filter(method -> method.getName().contains(SET_METHOD_PREFIX) && supportedFields.contains(getFieldNameFromMethod(method)))
                    .toList();

            // Ruft jede Set-Methode des targets mit den Werten aus den Get-Methoden des source Objekts auf
            supportedSetterMethods.forEach(method -> {
                try {
                    Method getMethodOfSource = source.getClass().getMethod(method.getName().replace(SET_METHOD_PREFIX, GET_METHOD_PREFIX));

                    method.invoke(target, getMethodOfSource.invoke(source, null));
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                } catch (NoSuchMethodException ignored) {

                }
            });
            return target;
        } catch (NullPointerException e) {
            return null;
        }
    }

    @Override
    public D convertToDto(E source, D target) {
        return convertBetweenSourceAndTarget(source, target);
    }

    @Override
    public E convertToEntity(D source, E target) {
        return convertBetweenSourceAndTarget(source, target);
    }

    private String getFieldNameFromMethod(Method method) {
        return method.getName().substring(2);
    }
}
