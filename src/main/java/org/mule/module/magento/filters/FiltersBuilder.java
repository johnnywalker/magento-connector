/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.magento.filters;

import com.magento.api.AssociativeEntity;
import com.magento.api.ComplexFilter;
import com.magento.api.ComplexFilterArray;
import com.magento.api.Filters;

import java.util.LinkedList;
import java.util.List;

/**
 * A simple builder for {@link Filters}
 *
 * @author flbulgarelli
 */
public class FiltersBuilder {
    private List<ComplexFilter> complexFilters = new LinkedList<ComplexFilter>();

    /**
     * Adds a binary expression as a new {@link ComplexFilter} to the built
     * {@link Filters}
     *
     * @param operation the operator
     * @param variable  the first operand
     * @param value     the second operand
     */
    public void addBinaryExpression(String operation, String variable, String value) {
        complexFilters.add(new ComplexFilter(variable, new AssociativeEntity(operation, convertValue(value))));
    }

    private String convertValue(String value) {
        if (value.length() >= 2 && value.charAt(0) == '\'' && value.charAt(value.length() - 1) == '\'') {
            return value.substring(1, value.length() - 1);
        }
        return value;
    }

    /**
     * Adds a unary expression as a new {@link ComplexFilter} to the built
     * {@link Filters}
     *
     * @param operation the operator
     * @param variable  the operand
     */
    public void addUnaryExpression(String operation, String variable) {
        if (operation.equals("istrue")) {
            addBinaryExpression("eq", variable, "1");
        } else if (operation.equals("isfalse")) {
            addBinaryExpression("eq", variable, "0");
        } else {
            addBinaryExpression(operation, variable, "");
        }
    }

    /**
     * Answers the built filters
     *
     * @return a new {@link Filters} object
     */
    public Filters build() {
        return new Filters(null,
                           new ComplexFilterArray(complexFilters.toArray(new ComplexFilter[complexFilters.size()])));
    }

}
