import java.util.ArrayList;
import java.util.Scanner;

public class App {
    static Scanner scanner;

    public static void calculateBill() {
        System.out.println();
        System.out.println("Select a tariff to calculate the bill:");
        ArrayList<Tariff> tariffs = Tariff.OBJECTS.findAll();
        for (int i = 0; i < tariffs.size(); i++) {
            Tariff tariff = tariffs.get(i);
            System.out.println((i + 1) + ". " + tariff.getName() + " - " + tariff.getMeterType());
        }
        System.out.print("Enter the number of the tariff: ");
        scanner = new Scanner(System.in);
        int tariffChoice = scanner.nextInt();

        if (tariffChoice < 1 || tariffChoice > tariffs.size()) {
            System.out.println("Invalid choice. Returning to dashboard.");
            return;
        }
        Tariff selectedTariff = tariffs.get(tariffChoice - 1);
        System.out.print("Enter number of days in billing period: ");
        int days = scanner.nextInt();
        System.out.print("Enter opening meter reading: ");
        double openingReading = scanner.nextDouble();
        System.out.print("Enter closing meter reading: ");
        double closingReading = scanner.nextDouble();

        double billAmount = selectedTariff.calculateBill(days, openingReading, closingReading);
        System.out.println(
                "The calculated bill for " + selectedTariff.getName() + " for " + days + " days is: " + billAmount);

    }

    public static String getMeterType(Scanner scanner) {
        System.out.print("What is the meter type (1: Gas/ 2: Electricity): ");
        int meterTypeChoice = scanner.nextInt();
        switch (meterTypeChoice) {
            case 1:
                return "Gas";
            case 2:
                return "Electricity";
            default:
                System.out.println("Invalid choice. Please try again.");
                return getMeterType(scanner);
        }
    }

    public static void addTariffForCustomer(Customer customer) {
        scanner = new Scanner(System.in);

        System.out.print("Enter tariff name: ");
        String name = scanner.nextLine();

        String meterType = getMeterType(scanner);

        System.out.print("Enter tariff rate (pence) (per unit): ");
        double rate = scanner.nextDouble();

        System.out.print("Enter daily standing charge (pence) (fixed daily cost): ");
        double dailyStandingCharge = scanner.nextDouble();

        System.out.print("Enter VAT (percentage): ");
        double vat = scanner.nextDouble();

        Tariff newTariff = new Tariff(customer.getId(), name, meterType, rate, dailyStandingCharge, vat);
        newTariff.save();
        System.out.println("Tariff " + name + " added successfully!");
    }

    public static void showDashboard(Customer customer) {
        System.out.println("Welcome to your dashboard, " + customer.getName() + "!");
        // Further dashboard implementation goes here
        System.out.println("What would you like to do today?");
        System.out.println("1. View Tariffs");
        System.out.println("2. Add Tariff");
        System.out.println("3. Calculate Bill");
        System.out.println("4. Exit");
        System.out.print("Please enter your choice (1-4): ");

        scanner = new Scanner(System.in);
        int choice = scanner.nextInt();
        System.out.println();
        switch (choice) {
            case 1:
                System.out.println("Your Tariffs:");
                ArrayList<Tariff> tariffs = Tariff.OBJECTS.filter(t -> t.getUserId().equals(customer.getId()));
                if (tariffs.isEmpty()) {
                    System.out.println("No tariffs found.");
                    System.out.println();
                } else {
                    for (Tariff tariff : tariffs) {
                        System.out.println(tariff.getName() + " - " + tariff.getMeterType() +
                                " - Rate: " + tariff.getRate() +
                                " - Daily Standing Charge: " + tariff.getDailyStandingCharge());
                    }
                }
                showDashboard(customer);

                break;
            case 2:
                addTariffForCustomer(customer);
                showDashboard(customer);
                break;
            case 3:
                calculateBill();
                showDashboard(customer);
                break;
            case 4:
                System.out.println("Exiting the dashboard. Goodbye!");
                System.exit(0);
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
        }

    }

    public static Customer registerCustomer() {
        scanner = new Scanner(System.in);

        System.out.print("Enter your name: ");
        String name = scanner.nextLine();

        System.out.print("Enter your email: ");
        String email = scanner.nextLine();

        System.out.print("Enter your phone number: ");
        String phoneNumber = scanner.nextLine();

        System.out.print("Enter your address: ");
        String address = scanner.nextLine();
        System.out.println();
        Customer newCustomer = new Customer(name, email, phoneNumber, address);
        newCustomer.save();
        System.out.println("Registration successful! Your customer ID is: " + newCustomer.getId());

        return newCustomer;
    }

    public static Customer loginCustomer() {
        scanner = new Scanner(System.in);

        System.out.print("Enter your customer ID: ");
        String id = scanner.next();
        System.out.println();
        Customer customer = Customer.OBJECTS.findById(id).orElse(null);
        if (customer != null) {
            System.out.println("Login successful! Welcome back, " + customer.getName() + ".");
        } else {
            System.out.println("Customer ID not found. Please register first. Or try again.");
            System.out.println();
            intro();
            return null;
        }
        return customer;
    }

    public static void intro() {
        System.out.println("Welcome to the Bill Management System!");
        System.out.println("Manage your customers and tariffs with ease.");
        System.out.println("=========================================");
        System.out.println();
        System.out.println("Are you a new or existing customer?");
        System.out.println("1. New Customer");
        System.out.println("2. Existing Customer");
        System.out.println();
        System.out.print("Please enter your choice (1 or 2): ");
        scanner = new Scanner(System.in);
        int choice = scanner.nextInt();
        System.out.println();
        Customer customer = null;
        if (choice == 1) {
            System.out.println("Redirecting to New Customer Registration...");
            customer = registerCustomer();
        } else if (choice == 2) {
            System.out.println("Redirecting to Existing Customer Login...");
            customer = loginCustomer();
        } else {
            System.out.println("Invalid choice. Please restart the application.");
            intro();
            return;
        }
        showDashboard(customer);
    }

    public static void main(String[] args) throws Exception {
        intro();
        scanner.close();
    }
}
