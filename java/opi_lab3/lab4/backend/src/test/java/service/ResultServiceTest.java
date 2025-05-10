package service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ResultServiceTest {

    private final ResultService resultService = new ResultService();

    @Test
    void testCheckHitInFirstQuadrantTriangle() {
        assertTrue(resultService.checkHit(1, 1, 3));
    }

    @Test
    void testCheckHitInFirstQuadrantOutsideTriangle() {
        assertFalse(resultService.checkHit(2, 3, 2));
    }

    @Test
    void testCheckHitInThirdQuadrantInsideCircle() {
        assertTrue(resultService.checkHit(-0.5, -0.5, 2));
    }

    @Test
    void testCheckHitInSecondQuadrantAlwaysFalse() {
        assertFalse(resultService.checkHit(-1, 1, 2));
    }
}
