package core;

import java.util.function.Consumer;

public class Customer implements Runnable {
    private final int customerId;
    private final TicketPool ticketPool;
    private final int customerRetrievalRate;
    private final Consumer<String> logger;

    public Customer(
            int customerId,
            TicketPool ticketPool,
            int customerRetrievalRate,
            Consumer<String> logger
    ) {
        this.customerId = customerId;
        this.ticketPool = ticketPool;
        this.customerRetrievalRate = customerRetrievalRate;
        this.logger = logger;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            Ticket ticket = ticketPool.removeTicket();
            if (ticket != null) {
                logger.accept("Customer " + customerId + " purchased " + ticket);
            }
            try {
                Thread.sleep(customerRetrievalRate);
            } catch (InterruptedException e) {
                logger.accept("Customer " + customerId + " interrupted.");
                break;
            }
        }
    }
}