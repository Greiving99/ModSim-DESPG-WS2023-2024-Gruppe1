package dev.despg.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class Probability2ValueTest
{

    @Test
    public void testCompareTo()
    {
        Probability2Value<String> p1 = new Probability2Value<>(0.5, "A");
        Probability2Value<String> p2 = new Probability2Value<>(0.5, "B");
        Probability2Value<String> p3 = new Probability2Value<>(0.6, "C");

        // Test equal probabilities
        assertEquals(0, p1.compareTo(p2));

        // Test different probabilities
        assertTrue(p1.compareTo(p3) < 0);
        assertTrue(p3.compareTo(p1) > 0);
    }
}
