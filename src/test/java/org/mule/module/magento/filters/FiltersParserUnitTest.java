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
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Test for {@link FiltersParser}
 *
 * @author flbulgarelli
 */
public class FiltersParserUnitTest
{

    /***
     * Tests that a bad formed expression is rejected by parser
     */
    @Test(expected = ParseException.class)
    public void testParseBadExpression() throws Exception
    {
        try
        {
            parse("lalalal");
        }
        catch (IllegalArgumentException e)
        {
            throw (Exception) e.getCause();
        }
    }

    /***
     * Tests that a well formed expression but that pass less parameters than needed
     * is rejected by parser
     */
    @Test(expected = ParseException.class)
    public void testParseUnaryInsteadOfBinaryExpression() throws Exception
    {
        try
        {
            parse("eq(customer_id)");
        }
        catch (IllegalArgumentException e)
        {
            throw (Exception) e.getCause();
        }
    }

    /**
     * Tests that unary expressions are accepted
     */
    @Test
    public void testParseSimpleUnaryExpressionIntegerType() throws Exception
    {
        parse("null(customer_id)");
        parse("notnull(customer_id)");
    }

    /**
     * Tests that binary expressions are accepted
     */
    @Test
    public void testParseSimpleBinaryExpressionIntegerType() throws Exception
    {
        parse("eq(customer_id, 500)");
        parse("neq(customer_id, 500)");
    }

    /**
     * Tests that expressions that use string literals are accepted
     */
    @Test
    public void testParseSimpleBinaryExpressionStringValue() throws Exception
    {
        parse("like(customer_name, '% DOE')");
        parse("nlike(customer_name, '% DOE')");
    }

    /**
     * Tests that expressions that use simple "and" conjunction are accepted
     */
    @Test
    public void testParseSimpleExpressionAnd() throws Exception
    {
        parse("lt(customer_id, 156), gt(customer_id, 100)");
    }

    /**
     * Tests that expressions that use multiple "and" conjunction are accepted
     */
    @Test
    public void testParseSimpleExpressionAndAnd() throws Exception
    {
        parse("lteq(customer_id, 156), gt(customer_id, 100), gteq(customer_city_code, 9986)");
    }



    /**
     * Tests that expressions once parsed can be interpreted
     */
    @Test
    public void testFilterCreationWithBinary() throws Exception
    {
        assertEquals(parse("eq(customer_name, 900)"), new Filters(
                null,
                new ComplexFilterArray(
                        new ComplexFilter[]{new ComplexFilter("customer_name", new AssociativeEntity("eq", "900"))}
            )
        ));
    }

    /**
     * Tests that expressions once parsed can be interpreted
     */
    @Test
    public void testFilterCreationWithUnary() throws Exception
    {
        assertEquals(parse("notnull(customer_name)"), new Filters(
                null,
                new ComplexFilterArray(
                        new ComplexFilter[]{new ComplexFilter("customer_name", new AssociativeEntity("notnull", ""))}
                )
        ));
    }

    /**
     * Tests that expressions once parsed can be interpreted
     */
    @Test
    public void testFilterCreationWithAnd() throws Exception
    {
        assertEquals(parse("notnull(customer_name), lt(customer_city_code, 56)"), // 
            new Filters(null, new ComplexFilterArray(new ComplexFilter[]{
                new ComplexFilter("customer_name", new AssociativeEntity("notnull", "")),
                new ComplexFilter("customer_city_code", new AssociativeEntity("lt", "56"))}
            )));
    }

    /**
     * Tests that the apos of a string argument are not added to the filtering value
     */
    @Test
    public void testParseStringArgument() throws Exception
    {
        assertEquals(new Filters(null,
                                 new ComplexFilterArray(
                                         new ComplexFilter[]{
                                                 new ComplexFilter("name", new AssociativeEntity("eq", "Hardware"))
                                         }
                                 )), parse("eq(name, 'Hardware')"));
        assertEquals(new Filters(null,
                                 new ComplexFilterArray(
                                         new ComplexFilter[]{
                                                 new ComplexFilter("name", new AssociativeEntity("eq", ""))
                                         }
                                 )), parse("eq(name, '')"));
    }


    /**
     * Tests that in expressions can be parsed
     */
    @Test
    public void testParseIn() throws Exception
    {
        parse("in(customer_id, '10,20,60')");
        parse("nin(customer_id, '10,20,60')");
    }
    /**
     * Tests that the custom istrue expression is equivalent to eq(_,1)
     */
    @Test
    public void testIsTrue() throws Exception
    {
        assertEquals(parse("eq(is_active, 1)"), parse("istrue(is_active)"));
    }

    /**
     * Tests that the custom isfalse expression is equivalent to eq(_,0)
     */
    @Test
    public void testIsFalse() throws Exception
    {
        assertEquals(parse("eq(is_active, 0)"), parse("isfalse(is_active)"));
    }



    public Filters parse(String expression) throws ParseException
    {
        return FiltersParser.parse(expression);
    }



}
