
package core;

public class Ticket {
    private static int idCounter = 0;
    private int ticketId;

    public Ticket() {
        this.ticketId = ++idCounter;
    }

    public int getTicketId() {
        return ticketId;
    }

    @Override
    public String toString() {
        return "Ticket #" + ticketId;
    }
}
