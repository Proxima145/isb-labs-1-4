package ru.edu.pr01;

/** Дополнительные проверки границ и независимых контрольных результатов. */
public class BoundaryCheck {
    private static int checks;

    private static void check(String name, boolean condition) {
        checks++;
        if (!condition) throw new AssertionError(name);
    }

    private static void close(String name, double actual, double expected) {
        check(name, Math.abs(actual - expected) <= Math.max(1e-8, Math.abs(expected) * 1e-12));
    }

    private static void rejects(String name, Runnable action) {
        boolean rejected = false;
        try { action.run(); } catch (IllegalArgumentException expected) { rejected = true; }
        check(name, rejected);
    }

    public static void main(String[] args) {
        check("variant", OrderCalculator.VARIANT == 4 && OrderCalculator.VAT_RATE == 25.0);
        check("typical", OrderCalculator.isValid(12, 2490.50, 5));
        check("lower included", OrderCalculator.isValid(1, 0.01, 0));
        check("small positive price", OrderCalculator.isValid(1, 0.001, 0));
        check("upper included", OrderCalculator.isValid(10000, 5000000, 30));
        check("quantity zero", !OrderCalculator.isValid(0, 100, 0));
        check("quantity negative", !OrderCalculator.isValid(-1, 100, 0));
        check("quantity above maximum", !OrderCalculator.isValid(10001, 100, 0));
        check("price zero", !OrderCalculator.isValid(1, 0, 0));
        check("price negative", !OrderCalculator.isValid(1, -0.01, 0));
        check("price above maximum", !OrderCalculator.isValid(1, 5000000.01, 0));
        check("discount negative", !OrderCalculator.isValid(1, 100, -0.01));
        check("discount above maximum", !OrderCalculator.isValid(1, 100, 30.01));
        check("discount NaN", !OrderCalculator.isValid(1, 100, Double.NaN));
        check("price NaN", !OrderCalculator.isValid(1, Double.NaN, 0));
        check("price infinity", !OrderCalculator.isValid(1, Double.POSITIVE_INFINITY, 0));
        check("discount infinity", !OrderCalculator.isValid(1, 100, Double.POSITIVE_INFINITY));
        close("base", OrderCalculator.calculateBase(2, 100), 200);
        close("discounted", OrderCalculator.applyDiscount(200, 10), 180);
        close("vat after discount", OrderCalculator.calculateVat(180, 25), 45);
        close("variant total", OrderCalculator.calculateTotal(2, 100, 10), 225);
        close("starter compatibility", OrderCalculator.calculateTotal(2, 100, 10, 20), 216);
        close("sample row 1", OrderCalculator.calculateTotal(12, 2490.50, 5), 35489.625);
        close("sample row 2", OrderCalculator.calculateTotal(100, 150, 0), 18750);
        close("max total", OrderCalculator.calculateTotal(10000, 5000000, 0), 62500000000.0);
        close("max with discount", OrderCalculator.calculateTotal(10000, 5000000, 30), 43750000000.0);
        close("no VAT", OrderCalculator.calculateTotal(2, 100, 10, 0), 180);
        close("comma", OrderCalculator.parseDecimal("2490,50", "Цена"), 2490.5);
        close("dot", OrderCalculator.parseDecimal("2490.50", "Цена"), 2490.5);
        close("exponent", OrderCalculator.parseDecimal("1e2", "Цена"), 100);
        check("integer trim", OrderCalculator.parseQuantity(" 12 ") == 12);
        rejects("invalid input prevents calculation", () -> OrderCalculator.calculateTotal(0, 100, 0));
        rejects("discount API", () -> OrderCalculator.applyDiscount(200, 35));
        rejects("VAT NaN", () -> OrderCalculator.calculateVat(180, Double.NaN));
        rejects("VAT negative", () -> OrderCalculator.calculateVat(180, -1));
        rejects("VAT above maximum", () -> OrderCalculator.calculateVat(180, 101));
        rejects("amount NaN", () -> OrderCalculator.applyDiscount(Double.NaN, 0));
        rejects("empty quantity", () -> OrderCalculator.parseQuantity(""));
        rejects("missing price", () -> OrderCalculator.parseDecimal(null, "Цена"));
        rejects("empty price", () -> OrderCalculator.parseDecimal("  ", "Цена"));
        rejects("fractional quantity", () -> OrderCalculator.parseQuantity("2.5"));
        rejects("integer overflow", () -> OrderCalculator.parseQuantity("2147483648"));
        rejects("wrong format", () -> OrderCalculator.parseDecimal("abc", "Цена"));
        rejects("ambiguous separator", () -> OrderCalculator.parseDecimal("1,234.50", "Цена"));
        rejects("NaN text", () -> OrderCalculator.parseDecimal("NaN", "Цена"));
        rejects("Infinity text", () -> OrderCalculator.parseDecimal("Infinity", "Цена"));
        rejects("overflow double", () -> OrderCalculator.parseDecimal("1e309", "Цена"));
        rejects("invalid price API", () -> OrderCalculator.calculateBase(1, 5000001));
        System.out.println("PASS " + checks + " checks");
    }
}
