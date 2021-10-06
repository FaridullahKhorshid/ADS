package models;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class Product {
    private final long barcode;
    private String title;
    private double price;

    public Product(long barcode) {
        this.barcode = barcode;
    }
    public Product(long barcode, String title, double price) {
        this(barcode);
        this.title = title;
        this.price = price;
    }

    /**
     * parses product information from a textLine with format: barcode, title, price
     * @param textLine
     * @return  a new Product instance with the provided information
     *          or null if the textLine is corrupt or incomplete
     */
    public static Product fromLine(String textLine) {
        if (textLine == null || textLine.length() == 0) {
            return null;
        }

        List<String> parsedLine = Arrays.asList(textLine.split(","));

        if (parsedLine.size() < 3) {
            // Text line misses information
            return null;
        }

        String title = parsedLine.get(1).trim();
        if (title.length() == 0) {
            return null;
        }

        long barcode;
        double price;

        try {
            barcode = Long.parseLong(parsedLine.get(0));
            price = Double.parseDouble(parsedLine.get(2).trim());
        } catch (NumberFormatException e) {
            // Text line is corrupted
            return null;
        }

        return new Product(barcode, title, price);
    }

    public long getBarcode() {
        return barcode;
    }

    public String getTitle() {
        return title;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Product)) return false;
        return this.getBarcode() == ((Product)other).getBarcode();
    }

    // TODO add public and private methods as per your requirements


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getBarcode());
        sb.append("/");
        sb.append(this.getTitle());
        sb.append("/");
        // Set the locale to use a period instead of a comma for fractionals
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.UK);
        // Print trailing zeroes up to and including the second fractional
        numberFormat.setMinimumFractionDigits(2);
        sb.append(numberFormat.format(this.getPrice()));
        return sb.toString();
    }
}
