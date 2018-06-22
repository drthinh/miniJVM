/*
 * @(#)Constructor.java	1.49 04/05/04
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package java.lang.reflect;

import org.mini.reflect.ReflectMethod;

/**
 * <code>Constructor</code> provides information about, and access to, a single
 * constructor for a class.
 *
 * <p>
 * <code>Constructor</code> permits widening conversions to occur when matching
 * the actual parameters to newInstance() with the underlying constructor's
 * formal parameters, but throws an <code>IllegalArgumentException</code> if a
 * narrowing conversion would occur.
 *
 * @see Member
 * @see java.lang.Class
 * @see java.lang.Class#getConstructors()
 * @see java.lang.Class#getConstructor(Class[])
 * @see java.lang.Class#getDeclaredConstructors()
 *
 * @author	Kenneth Russell
 * @author	Nakul Saraiya
 */
public final class Constructor<T> {

    Class<T> clazz;
    ReflectMethod refMethod;

    public Constructor(Class cl, ReflectMethod refm) {
        refMethod = refm;
        clazz = cl;
    }

    public String getName() {
        return refMethod.methodName;
    }

    public Class<?>[] getParameterTypes() {
        return refMethod.getParameterTypes();
    }

    public T newInstance(Object... initargs)
            throws InstantiationException, IllegalAccessException,
            IllegalArgumentException {
        return (T) refMethod.invoke(clazz.newInstance(), initargs);
    }

    @Override
    public boolean equals(Object o) {
        return refMethod == o;
    }
}