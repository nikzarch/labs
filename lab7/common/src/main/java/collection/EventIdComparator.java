package collection;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Компаратор для сортировки по id мероприятий(event)
 */
public class EventIdComparator implements Comparator<Ticket>, Serializable {
    @Override
    public int compare(Ticket t1, Ticket t2) {
        return t1.getEvent().getId() - t2.getEvent().getId();
    }
}
