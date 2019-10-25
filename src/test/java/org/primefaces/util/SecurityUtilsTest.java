package org.primefaces.util;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Collection;

import org.junit.jupiter.api.Test;

class SecurityUtilsTest {
    
    @Test
    void testConvertRoles_Null() {
        //Arrange
        Integer roles = Integer.valueOf(5);

        //Act
        String[] actual = SecurityUtils.convertRoles(roles);

        //Assert
        assertNull(actual);
    }

    @Test
    void testConvertRoles_String() {
        //Arrange
        String roles = "role1,role2";

        //Act
        String[] actual = SecurityUtils.convertRoles(roles);

        //Assert
        assertEquals("role1", actual[0]);
        assertEquals("role2", actual[1]);
    }

    @Test
    void testConvertRoles_Array() {
        //Arrange
        String[] roles = {"role1","role2"};

        //Act
        String[] actual = SecurityUtils.convertRoles(roles);

        //Assert
        assertEquals("role1", actual[0]);
        assertEquals("role2", actual[1]);
    }

    @Test
    void testConvertRoles_Collection() {
        //Arrange
        Collection<String> roles = Arrays.asList("role1","role2");

        //Act
        String[] actual = SecurityUtils.convertRoles(roles);

        //Assert
        assertEquals("role1", actual[0]);
        assertEquals("role2", actual[1]);
    }

}
