package console;

import java.util.ArrayList;
import java.util.Scanner;

import backend.enums.FuelType;
import backend.enums.PaymentMethod;
import backend.enums.RateType;
import backend.enums.RegisterType;
import backend.models.Tariff;

public class Common {
    private static Scanner scanner;

    public static boolean checkIfTariffsExist() {
        ArrayList<Tariff> tariffs = Tariff.objects.findAll();
        return !tariffs.isEmpty();
    }

    public static FuelType chooseFuelType() {
        scanner = new Scanner(System.in);
        System.out.println("Select Fuel Type:");
        System.out.println("1. Gas");
        System.out.println("2. Electricity");
        System.out.print("Enter choice (1-2): ");
        int choice = scanner.nextInt();
        switch (choice) {
            case 1:
                return FuelType.GAS;
            case 2:
                return FuelType.ELECTRICITY;
            default:
                System.out.println("Invalid choice. Please try again.");
                return chooseFuelType();
        }
    }

    public static RateType chooseRateType() {
        scanner = new Scanner(System.in);
        System.out.println("Select Rate Type:");
        System.out.println("1. Single Rate");
        System.out.println("2. Dual Rate (Day/Night)");
        System.out.print("Enter choice (1-2): ");
        int choice = scanner.nextInt();
        switch (choice) {
            case 1:
                return RateType.SINGLE_RATE;
            case 2:
                return RateType.TWO_RATE;
            default:
                System.out.println("Invalid choice. Please try again.");
                return chooseRateType();
        }
    }

    public static RegisterType chooseRegisterType(FuelType fuelType) {
        scanner = new Scanner(System.in);
        if (fuelType == FuelType.GAS) {
            System.out.println("Selected fuel type is Gas. Defaulting to Single Register.");
            return RegisterType.SINGLE_REGISTER;
        }
        System.out.println("Select Register Type:");
        System.out.println("1. Single Register");
        System.out.println("2. Two Register");
        System.out.print("Enter choice (1-2): ");
        int choice = scanner.nextInt();
        switch (choice) {
            case 1:
                return RegisterType.SINGLE_REGISTER;
            case 2:
                return RegisterType.TWO_REGISTER;
            default:
                System.out.println("Invalid choice. Please try again.");
                return chooseRegisterType(fuelType);
        }
    }

    public static Tariff getTariffToUpdate() {
        scanner = new Scanner(System.in);
        System.out.println("Current Tariffs:");
        ArrayList<Tariff> tariffs = Tariff.objects.findAll();
        int index = 1;
        for (Tariff tariff : tariffs) {
            System.out.println(
                    index + ". " + tariff.getId() + ": " + tariff.getName() + " - " + tariff.getFuelType() + " - "
                            + tariff.getRateType());
            index++;
        }
        System.out.print("Enter the number of the tariff to update: ");
        int choice = scanner.nextInt();
        if (choice < 1 || choice > tariffs.size()) {
            System.out.println("Invalid choice. Please try again.");
            return getTariffToUpdate();
        }
        return tariffs.get(choice - 1);
    }

    public static PaymentMethod choosePaymentMethod() {
        scanner = new Scanner(System.in);
        System.out.println("Select Payment Method:");
        System.out.println("1. Credit Card");
        System.out.println("2. Direct Debit");
        System.out.println("3. Cash");
        System.out.print("Enter choice (1-3): ");
        int choice = scanner.nextInt();
        switch (choice) {
            case 1:
                return PaymentMethod.CARD;
            case 2:
                return PaymentMethod.DIRECT_DEBIT;
            case 3:
                return PaymentMethod.CASH;
            default:
                System.out.println("Invalid choice. Please try again.");
                return choosePaymentMethod();
        }
    }

}
