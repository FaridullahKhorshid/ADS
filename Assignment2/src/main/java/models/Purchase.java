package models;

import java.util.Arrays;
import java.util.List;

public class Purchase {
    private final Product product;
    private int count;

    public Purchase(Product product, int count) {
        this.product = product;
        this.count = count;
    }

    /**
     * parses purchase summary information from a textLine with format: barcode, amount
     * @param textLine
     * @param products  a list of products ordered and searchable by barcode
     *                  (i.e. the comparator of the ordered list shall consider only the barcode when comparing products)
     * @return  a new Purchase instance with the provided information
     *          or null if the textLine is corrupt or incomplete
     */
    public static Purchase fromLine(String textLine, List<Product> products) {
        if (textLine == null || products == null) {
            return null;
        }

        List<String> parsedLine = Arrays.asList(textLine.split(","));
        if (parsedLine.size() < 2) {
            // Incomplete line
            return null;
        }

        long barcode;
        int count;

        try {
            barcode = Long.parseLong(parsedLine.get(0));
            count = Integer.parseInt(parsedLine.get(1));
        } catch(NumberFormatException e) {
            // Corrupted line
            return null;
        }

        int index = products.indexOf(new Product(barcode));
        if (index == -1) {
            // No product exists with this barcode
            return null;
        }

        Product product = products.get(index);

        return new Purchase(product, count);
    }

    /**
     * add a delta amount to the count of the purchase summary instance
     * @param delta
     */
    public void addCount(int delta) {
        this.count += delta;
    }

    public long getBarcode() {
        return this.product.getBarcode();
    }
    public int getCount() {
        return count;
    }
    public void setCount(int count) {
        this.count = count;
    }
    public Product getProduct() {
        return product;
    }

    // TODO add public and private methods as per your requirements
}
