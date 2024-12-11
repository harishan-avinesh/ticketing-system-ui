package core;

import java.util.function.Consumer;
import java.util.function.IntConsumer;

public class Vendor implements Runnable {
    private final int vendorId;
    private final TicketPool ticketPool;
    private final int ticketReleaseRate;
    private final int totalTicketsToRelease;
    private final Consumer<String> logger;
    private final IntConsumer ticketIssuedTracker;

    public Vendor(
            int vendorId,
            TicketPool ticketPool,
            int ticketReleaseRate,
            int totalTicketsToRelease,
            Consumer<String> logger,
            IntConsumer ticketIssuedTracker
    ) {
        this.vendorId = vendorId;
        this.ticketPool = ticketPool;
        this.ticketReleaseRate = ticketReleaseRate;
        this.totalTicketsToRelease = totalTicketsToRelease;
        this.logger = logger;
        this.ticketIssuedTracker = ticketIssuedTracker;
    }

    @Override
    public void run() {
        int ticketsReleased = 0;
        while (ticketsReleased < totalTicketsToRelease && !Thread.currentThread().isInterrupted()) {
            ticketPool.addTickets(1);
            ticketsReleased++;

            // Log and track ticket issuance
            logger.accept("Vendor " + vendorId + " released ticket. Total released: " + ticketsReleased);
            ticketIssuedTracker.accept(1);

            try {
                Thread.sleep(ticketReleaseRate);
            } catch (InterruptedException e) {
                logger.accept("Vendor " + vendorId + " interrupted.");
                break;
            }
        }
        logger.accept("Vendor " + vendorId + " has finished releasing tickets.");
    }
}