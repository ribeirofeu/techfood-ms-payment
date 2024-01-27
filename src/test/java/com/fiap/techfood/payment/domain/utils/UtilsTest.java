package com.fiap.techfood.payment.domain.utils;

import com.fiap.techfood.payment.domain.commons.utils.Utils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UtilsTest {

    @Test
    void testConvertToBase64() {
        // Arrange
        String input = "Test String";
        String expectedBase64 = "VGVzdCBTdHJpbmc=";

        // Act
        String actualBase64 = Utils.convertToBase64(input);

        // Assert
        assertEquals(expectedBase64, actualBase64);
    }

}
