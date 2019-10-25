package org.primefaces.util;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Collection;

import org.junit.jupiter.api.Test;

class SecurityUtilsTest {

    @Test
    void testConvertRoles_String() {
        //Arrange
        String roles = "role1,role2";

        //Act
        String[] result = SecurityUtils.convertRoles(roles);

        //Assert
        assertEquals("role1", result[0]);
        assertEquals("role2", result[1]);
    }

    @Test
    void testConvertRoles_Array() {
        //Arrange
        String[] roles = {"role1","role2"};

        //Act
        String[] result = SecurityUtils.convertRoles(roles);

        //Assert
        assertEquals("role1", result[0]);
        assertEquals("role2", result[1]);
    }

    @Test
    void testConvertRoles_Collection() {
        //Arrange
        Collection<String> roles = Arrays.asList("role1","role2");

        //Act
        String[] result = SecurityUtils.convertRoles(roles);

        //Assert
        assertEquals("role1", result[0]);
        assertEquals("role2", result[1]);
    }

}
