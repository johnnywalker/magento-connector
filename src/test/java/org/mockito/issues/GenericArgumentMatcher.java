/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mockito.issues;

import org.mockito.ArgumentMatcher;

/**
 * Abstract implementation of {@link ArgumentMatcher} to get rid of the {@link Object} in the method-signature.
 *
 * @author florian.patzl
 *
 */
public abstract class GenericArgumentMatcher<T> extends ArgumentMatcher<T> {

    @SuppressWarnings("unchecked")
    @Override
    public boolean matches(Object argument) {
        try {
            return doesMatch((T) argument);
        } catch (ClassCastException e) {
            return false;
        }
    }

    protected abstract boolean doesMatch(T argument);
}

