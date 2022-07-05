package Leecode;

public class test {


    public static void dfs(int[] a) {
        a[0] = 1;
        a[1] = 2;
        a[3] = 3;
        a[4] = 4;
    }

    public static void main(String[] args) {
        int[] a = new int[5];
        dfs(a);
        for(int i : a)
            System.out.println(i);

    }
}
