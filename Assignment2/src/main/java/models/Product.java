package models;

import java.util.Arrays;
import java.util.List;

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
        if (textLine == null) {
            return null;
        }

        List<String> parsedLine = Arrays.asList(textLine.split(","));

        if (parsedLine.size() < 3) {
            // Text line misses information
            return null;
        }

        long barcode;
        String title;
        double price;

        try {
            barcode = Long.parseLong(parsedLine.get(0));
            title = parsedLine.get(1).trim();
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
}
