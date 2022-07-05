package data;
import  java.util.Scanner;

public class Decreate {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
//        System.out.println("Please input data:");
        while (true) {
            System.out.println("Please input data:");
            int data = sc.nextInt();
            switch (data) {
                case 1:
                    System.out.println("Running");
                    break;
                case 2:
                    System.out.println("Swimming");
                    break;
                case 3:
                    System.out.println("Walking");
                    break;
                case 4:
                    System.out.println("Rolking");
                    break;
                case 5:
                    System.out.println("Breaking");
                    break;
                case 6:
                    System.out.println("Clabming");
                    break;
                default:
                    System.out.println("Eating a lot");

            }
        }

    }
}
