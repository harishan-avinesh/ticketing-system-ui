package core;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TicketPool {
    private final Queue<Ticket> tickets = new LinkedList<>();
    private final int maxCapacity;
    private final Lock lock = new ReentrantLock(true); // Fair lock
    private final Condition notFull = lock.newCondition();
    private final Condition notEmpty = lock.newCondition();

    public TicketPool(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public void addTickets(int numberOfTickets) {
        lock.lock();
        try {
            // Wait while the pool is at or near capacity
            while (tickets.size() + numberOfTickets > maxCapacity) {
                try {
                    notFull.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }

            // Add tickets
            for (int i = 0; i < numberOfTickets; i++) {
                tickets.add(new Ticket());
            }

            // Signal that tickets are available
            notEmpty.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public Ticket removeTicket() {
        lock.lock();
        try {
            // Wait while no tickets are available
            while (tickets.isEmpty()) {
                try {
                    notEmpty.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return null;
                }
            }

            // Remove and return a ticket
            Ticket ticket = tickets.poll();

            // Signal that there's space for more tickets
            notFull.signalAll();

            return ticket;
        } finally {
            lock.unlock();
        }
    }

    public int getTicketCount() {
        lock.lock();
        try {
            return tickets.size();
        } finally {
            lock.unlock();
        }
    }
}