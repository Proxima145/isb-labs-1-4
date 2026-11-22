package ru.edu.pr01;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Scanner;

/** Практические работы 3–4. Озеров Максим Дмитриевич, ЭФБО-11-25. */
public class OrderCalculator {
    public static final int VARIANT = 4;
    public static final int MAX_QUANTITY = 10_000;
    public static final double MAX_UNIT_PRICE = 5_000_000.0;
    public static final double MAX_DISCOUNT_PERCENT = 30.0;
    /** Учебный параметр варианта 4, заданный методичкой, в процентах. */
    public static final double VAT_RATE = 25.0;

    public static boolean isValid(int quantity, double unitPrice, double discountPercent) {
        return quantity > 0 && quantity <= MAX_QUANTITY
                && Double.isFinite(unitPrice) && unitPrice > 0 && unitPrice <= MAX_UNIT_PRICE
                && Double.isFinite(discountPercent)
                && discountPercent >= 0 && discountPercent <= MAX_DISCOUNT_PERCENT;
    }

    private static void validate(int quantity, double price, double discount) {
        if (quantity < 1 || quantity > MAX_QUANTITY) {
            throw new IllegalArgumentException("Количество должно быть от 1 до " + MAX_QUANTITY + ".");
        }
        if (!Double.isFinite(price) || price <= 0 || price > MAX_UNIT_PRICE) {
            throw new IllegalArgumentException("Цена должна быть конечным числом больше 0 и не больше 5000000.");
        }
        validateDiscount(discount);
    }

    private static void validateDiscount(double discount) {
        if (!Double.isFinite(discount) || discount < 0 || discount > MAX_DISCOUNT_PERCENT) {
            throw new IllegalArgumentException("Скидка должна быть от 0 до 30 процентов.");
        }
    }

    private static void validateAmount(double amount) {
        if (!Double.isFinite(amount) || amount <= 0) {
            throw new IllegalArgumentException("Сумма должна быть положительным конечным числом.");
        }
    }

    public static double calculateBase(int quantity, double unitPrice) {
        validate(quantity, unitPrice, 0);
        return quantity * unitPrice;
    }

    /** Возвращает сумму после скидки, а не размер самой скидки. */
    public static double applyDiscount(double base, double discountPercent) {
        validateAmount(base);
        validateDiscount(discountPercent);
        return base * (1.0 - discountPercent / 100.0);
    }

    /** Возвращает сумму НДС. Аргумент сохраняет совместимость с исходным SelfCheck. */
    public static double calculateVat(double discounted, double vatPercent) {
        validateAmount(discounted);
        if (!Double.isFinite(vatPercent) || vatPercent < 0 || vatPercent > 100) {
            throw new IllegalArgumentException("Ставка НДС должна быть от 0 до 100 процентов.");
        }
        return discounted * vatPercent / 100.0;
    }

    public static double calculateTotal(int quantity, double unitPrice,
                                        double discountPercent, double vatPercent) {
        validate(quantity, unitPrice, discountPercent);
        double base = calculateBase(quantity, unitPrice);
        double discounted = applyDiscount(base, discountPercent);
        return discounted + calculateVat(discounted, vatPercent);
    }

    public static double calculateTotal(int quantity, double unitPrice, double discountPercent) {
        return calculateTotal(quantity, unitPrice, discountPercent, VAT_RATE);
    }

    static int parseQuantity(String value) {
        String text = required(value, "Количество");
        if (!text.matches("[+-]?[0-9]+")) {
            throw new IllegalArgumentException("Количество должно быть целым числом.");
        }
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Количество выходит за диапазон типа int.");
        }
    }

    static double parseDecimal(String value, String label) {
        String text = required(value, label);
        // Один десятичный разделитель: точка или запятая; группировка разрядов не допускается.
        if (!text.matches("[+-]?(?:[0-9]+(?:[.,][0-9]*)?|[.,][0-9]+)(?:[eE][+-]?[0-9]+)?")) {
            throw new IllegalArgumentException(label + ": неверный числовой формат.");
        }
        try {
            double valueNumber = Double.parseDouble(text.replace(',', '.'));
            if (!Double.isFinite(valueNumber)) {
                throw new IllegalArgumentException(label + ": требуется конечное число.");
            }
            return valueNumber;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(label + ": неверный числовой формат.");
        }
    }

    private static String required(String value, String label) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(label + ": значение отсутствует или пустое.");
        }
        return value.trim();
    }

    private static String readRequired(Scanner scanner, String label) {
        System.out.print(label + ": ");
        if (!scanner.hasNextLine()) {
            throw new IllegalArgumentException(label + ": входные данные закончились.");
        }
        return required(scanner.nextLine(), label);
    }

    private static void printCalculation(int quantity, double price, double discount, PrintStream out) {
        validate(quantity, price, discount);
        double base = calculateBase(quantity, price);
        double discounted = applyDiscount(base, discount);
        double vat = calculateVat(discounted, VAT_RATE);
        double total = calculateTotal(quantity, price, discount);
        out.printf(Locale.forLanguageTag("ru-RU"),
                "Вариант: %d%nБазовая сумма: %.2f руб.%nСкидка (%.2f%%): %.2f руб.%n"
                + "После скидки: %.2f руб.%nНДС (%.2f%%): %.2f руб.%nИтого: %.2f руб.%n",
                VARIANT, base, discount, base - discounted, discounted, VAT_RATE, vat, total);
    }

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8.name())) {
            String quantityText;
            String priceText;
            String discountText;
            if (args.length == 3) {
                quantityText = args[0]; priceText = args[1]; discountText = args[2];
            } else if (args.length == 0) {
                quantityText = readRequired(scanner, "Количество");
                priceText = readRequired(scanner, "Цена единицы в рублях");
                discountText = readRequired(scanner, "Скидка в процентах");
            } else {
                throw new IllegalArgumentException("Укажите ровно 3 аргумента: количество цена скидка; либо вводите их по строкам.");
            }
            int quantity = parseQuantity(quantityText);
            double price = parseDecimal(priceText, "Цена");
            double discount = parseDecimal(discountText, "Скидка");
            printCalculation(quantity, price, discount, System.out);
        } catch (IllegalArgumentException ex) {
            System.err.println("Ошибка: " + ex.getMessage());
            System.exit(2);
        }
    }
}
