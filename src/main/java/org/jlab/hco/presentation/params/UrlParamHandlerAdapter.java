package org.jlab.hco.presentation.params;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

@SuppressWarnings("unchecked")
public abstract class UrlParamHandlerAdapter<E> implements UrlParamHandler<E> {

    private final HttpServletRequest request;

    public UrlParamHandlerAdapter(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public E convert() {
        Type superType = getClass().getGenericSuperclass();
        Type actualType = ((ParameterizedType) superType).getActualTypeArguments()[0];
        Object o;
        try {
            o = (Class.forName(actualType.toString()).getConstructor().newInstance());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException("Unable to convert URL parameters", e);
        }

        for (Field f : o.getClass().getDeclaredFields()) {
            Class<?> t = f.getType();
        }

        return (E) o;
    }

    @Override
    public void validate(E params) {

    }

    @Override
    public void store(E params) {

    }

    @Override
    public E defaults() {
        return null;
    }

    public E load() {
        return null;
    }

    public boolean isQualifiedRequest() {
        return false;
    }

    public String selectionMessage() {
        return null;
    }
}
