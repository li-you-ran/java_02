package data.array;
import  java.util.Scanner;

public class Array {
    public static void main(String[] args) {
//        int [] arr;
//        int arr[];
        //array 动态初始
//        Scanner sr = new Scanner(System.in);

        int[] arr = new int[3];
        for (int i = 0; i < 3; i++) {
            arr[i] = i;
        }

        int c = add(3, 4);
        int d = add(5);
        System.out.println("\n\n hellow");
        System.out.println(d);
        System.out.println(c);
    }

    public static int add(int a, int b) { return a + b ;}
    public  static  int add(int a){return a +1;}

}





