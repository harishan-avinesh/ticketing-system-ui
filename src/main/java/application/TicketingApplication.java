package application;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import core.*;

import java.util.ArrayList;
import java.util.List;

public class TicketingApplication extends Application {

    private SystemConfig config;
    private TicketPool ticketPool;
    private List<Thread> vendorThreads = new ArrayList<>();
    private List<Thread> customerThreads = new ArrayList<>();
    private TextArea logArea;
    //private Label ticketCountLabel;
    private Label vendorsRunningLabel;
    private Label customersRunningLabel;
    private Label totalTicketsIssuedLabel;

    private int totalTicketsIssued = 0;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // UI Components
        TextField totalTicketsField = new TextField("100");
        TextField ticketReleaseRateField = new TextField("1000");
        TextField customerRetrievalRateField = new TextField("1500");
        TextField maxTicketCapacityField = new TextField("50");
        TextField numberOfVendorsField = new TextField("5");
        TextField numberOfCustomersField = new TextField("10");
        Button startButton = new Button("Start Simulation");
        Button stopButton = new Button("Stop Simulation");

        // Labels for real-time metrics
        //ticketCountLabel = new Label("Tickets Available: 0");
        vendorsRunningLabel = new Label("Vendors Running: 0");
        customersRunningLabel = new Label("Customers Running: 0");
        totalTicketsIssuedLabel = new Label("Total Tickets Sold: 0");

        logArea = new TextArea();
        logArea.setEditable(false);

        // Layout
        GridPane configGrid = new GridPane();
        configGrid.setVgap(10);
        configGrid.setHgap(10);
        configGrid.setPadding(new Insets(10));

        // Add configuration fields to grid
        configGrid.addRow(0, new Label("Total Tickets:"), totalTicketsField);
        configGrid.addRow(1, new Label("Ticket Release Rate (ms):"), ticketReleaseRateField);
        configGrid.addRow(2, new Label("Customer Retrieval Rate (ms):"), customerRetrievalRateField);
        configGrid.addRow(3, new Label("Max Ticket Capacity:"), maxTicketCapacityField);
        configGrid.addRow(4, new Label("Number of Vendors:"), numberOfVendorsField);
        configGrid.addRow(5, new Label("Number of Customers:"), numberOfCustomersField);

        // Buttons layout
        HBox buttonBox = new HBox(10, startButton, stopButton);

        // Metrics layout
        VBox metricsBox = new VBox(10,
                //ticketCountLabel,
                vendorsRunningLabel,
                customersRunningLabel,
                totalTicketsIssuedLabel
        );

        // Main layout
        VBox mainLayout = new VBox(10,
                configGrid,
                buttonBox,
                metricsBox,
                new Label("Simulation Log:"),
                logArea
        );
        mainLayout.setPadding(new Insets(10));

        // Event Handlers
        startButton.setOnAction(e -> {
            try {
                // Parse configuration
                int totalTickets = Integer.parseInt(totalTicketsField.getText());
                int ticketReleaseRate = Integer.parseInt(ticketReleaseRateField.getText());
                int customerRetrievalRate = Integer.parseInt(customerRetrievalRateField.getText());
                int maxTicketCapacity = Integer.parseInt(maxTicketCapacityField.getText());
                int numberOfVendors = Integer.parseInt(numberOfVendorsField.getText());
                int numberOfCustomers = Integer.parseInt(numberOfCustomersField.getText());

                if (numberOfVendors > totalTickets) {
                    showAlert("Input Error", "Number of vendors cannot exceed total tickets.");
                    return;
                }
                if (numberOfCustomers > totalTickets) {
                    showAlert("Input Error", "Number of customers cannot exceed total tickets.");
                    return;
                }
                if (maxTicketCapacity > totalTickets) {
                    showAlert("Input Error", "Max ticket capacity cannot exceed total tickets.");
                    return;
                }

                if(customerRetrievalRate>5000 || ticketReleaseRate>5000){
                    showAlert("Too slow","Release and Retrieval rates must be less than 5 seconds to limit delay during demonstration.");
                    return;
                }

                config = new SystemConfig(
                        totalTickets,
                        ticketReleaseRate,
                        customerRetrievalRate,
                        maxTicketCapacity,
                        numberOfVendors,
                        numberOfCustomers
                );
                ticketPool = new TicketPool(config.getMaxTicketCapacity());

                // Reset metrics
                totalTicketsIssued = 0;
                updateTicketIssuedLabel(0);

                // Start Simulation
                startSimulation();
            } catch (NumberFormatException ex) {
                showAlert("Invalid Input", "Please enter valid numeric values.");
            }
        });

        stopButton.setOnAction(e -> stopSimulation());

        // Setup Stage
        Scene scene = new Scene(mainLayout, 600, 600);
        primaryStage.setTitle("Advanced Event Ticketing System Simulation");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Update Ticket Count and Metrics Periodically
        //startMetricsUpdater();
    }

    private void startSimulation() {
        log("Simulation started.");

        int numVendors = config.getNumberOfVendors();
        int numCustomers = config.getNumberOfCustomers();
        int ticketsPerVendor = config.getTotalTickets() / numVendors;

        // Vendors
        for (int i = 0; i < numVendors; i++) {
            Vendor vendor = new Vendor(
                    i + 1,
                    ticketPool,
                    config.getTicketReleaseRate(),
                    ticketsPerVendor,
                    this::log,
                    this::updateTotalTicketsIssued
            );
            Thread vendorThread = new Thread(vendor);
            vendorThreads.add(vendorThread);
            vendorThread.start();
        }

        // Customers
        for (int i = 0; i < numCustomers; i++) {
            Customer customer = new Customer(
                    i + 1,
                    ticketPool,
                    config.getCustomerRetrievalRate(),
                    this::log
            );
            Thread customerThread = new Thread(customer);
            customerThreads.add(customerThread);
            customerThread.start();
        }

        // Update running threads count
        updateThreadCountLabels();
    }

    private void stopSimulation() {
        log("Stopping simulation...");
        // Interrupt Vendor Threads
        for (Thread vendorThread : vendorThreads) {
            vendorThread.interrupt();
        }

        // Interrupt Customer Threads
        for (Thread customerThread : customerThreads) {
            customerThread.interrupt();
        }

        vendorThreads.clear();
        customerThreads.clear();

        // Reset thread count labels
        Platform.runLater(() -> {
            vendorsRunningLabel.setText("Vendors Running: 0");
            customersRunningLabel.setText("Customers Running: 0");
        });
    }

    private void log(String message) {
        Platform.runLater(() -> {
            logArea.appendText(message + "\n");
        });
    }

    private void updateTotalTicketsIssued(int additionalTickets) {
        Platform.runLater(() -> {
            totalTicketsIssued += additionalTickets;
            updateTicketIssuedLabel(totalTicketsIssued);
        });
    }
/*
    private void startMetricsUpdater() {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() {
                while (true) {
                    if (ticketPool != null) {
                        int count = ticketPool.getTicketCount();
                        Platform.runLater(() -> {
                            ticketCountLabel.setText("Tickets Available: " + count);
                        });
                    }
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
                return null;
            }

        };
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }
*/
    private void updateThreadCountLabels() {
        Platform.runLater(() -> {
            vendorsRunningLabel.setText("Vendors Running: " + vendorThreads.size());
            customersRunningLabel.setText("Customers Running: " + customerThreads.size());
        });
    }

    private void updateTicketIssuedLabel(int issued) {
        Platform.runLater(() -> {
            totalTicketsIssuedLabel.setText("Total Tickets Sold: " + issued);
        });
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}