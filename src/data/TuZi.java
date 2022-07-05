package data;

import java.awt.*;

public class TuZi {
    public static void main(String[] args) {
        int g_c = 0;
        int m_c = 0;
        A:
        for (int i = 1; i < 21; i++) {
            for (int j = 1; j < 34; j++) {
                for (int k = 1; k < 33; k++) {
                    if ((5 * i + 3 * j + k) == 100 && (i + j + 3 * k) == 100) {
                        g_c = i;
                        m_c = j;
                        System.out.println("Big ji is:" + i);
                        System.out.println("Little ji is :" + j);
                        System.out.println("Xiao ji is :" + k * 3);
                        break A;
                    }

                }

            }
        }
    }
}
