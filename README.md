<<<<<<< HEAD
# README File for Ticketing System

## Introduction
The Ticketing System is a Java-based application designed to simulate an event ticket purchasing process. It utilizes JavaFX for the graphical user interface (GUI) and employs multithreading to manage vendors and customers concurrently. The system allows users to configure various parameters such as total tickets, ticket release rate, customer retrieval rate, and the number of vendors and customers. This simulation provides real-time updates on ticket availability and transaction logs.

## Setup Instructions
To build and run the Ticketing System application, follow these steps:

1. **Prerequisites**:
    - Ensure you have Java Development Kit (JDK) 8 or higher installed on your machine.
    - Install an Integrated Development Environment (IDE) such as IntelliJ IDEA or Eclipse.

2. **Clone the Repository**:
   Clone the repository containing the project files to your local machine using:
   ```bash
   git clone <repository-url>
    ```
3. **Import the Project**:

- Open your IDE and import the cloned project.

- Ensure that all necessary libraries for JavaFX are included in your project settings.

4. **Build the Application**:

- In your IDE, navigate to the build options and compile the project.

- Make sure there are no compilation errors.

5. **Run the Application**:

- Locate the main class TicketingApplication in the application package.

- Run this class to start the application.

## Usage Instructions
To configure and start the Ticketing System:

1. **Configuration**:

- Upon launching, you will see input fields for configuring:

- - Total Tickets: Enter the total number of tickets available for sale.

- - Ticket Release Rate (ms): Specify how frequently tickets are released by vendors.

- - Customer Retrieval Rate (ms): Set how quickly customers attempt to purchase tickets.

- - Max Ticket Capacity: Define the maximum number of tickets that can be held in the pool at any time.

- - Number of Vendors: Input how many vendors will be releasing tickets.

- - Number of Customers: Specify how many customers will be purchasing tickets.

- After entering the desired values, click on the "Start Simulation" button to begin.

2. **Starting the System**:

- The system will initialize based on your configuration, launching vendor and customer threads to simulate ticket sales.

- You can monitor real-time metrics such as available tickets and total tickets issued in designated labels on the GUI.

3. **UI Controls Explanation**:

- Text Fields: Allow users to input configuration parameters.

- Buttons:

- - Start Simulation: Initiates the ticket selling process based on configured parameters.

- - Stop Simulation: Halts all ongoing vendor and customer threads safely.

- Labels: Display real-time statistics about ticket availability and transaction
=======
1
>>>>>>> 6eee8b6caac1ad47c8f7da37f928849c83185aaa
