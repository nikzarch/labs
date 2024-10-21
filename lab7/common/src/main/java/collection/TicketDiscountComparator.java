package collection;

import java.util.Comparator;

/**
 * Компаратор для сортировки по скидке
 */
public class TicketDiscountComparator implements Comparator<Ticket> {
    @Override
    public int compare(Ticket t1, Ticket t2) {
        return (int) (t1.getDiscount() - t2.getDiscount());
    }
}
