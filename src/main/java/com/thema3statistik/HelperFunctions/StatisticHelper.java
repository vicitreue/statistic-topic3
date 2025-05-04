package com.thema3statistik;

import java.util.List;

public class StatisticHelper {

    public static double durchschnittBerechnen(String[] zeiten) {
        double total = 0;
        double n = 0;
        for (String zeitString : zeiten) {
            total += Double.parseDouble(zeitString);
            n++;
        }
        return total / n;
    }

        public static double berechneU(List<Double> a, List<Double> b, List<TabelleEintrag> rangMap) {
        double rangSummeA = 0;
        for (double v : a) {
            for (TabelleEintrag eintrag : rangMap) {
                if (eintrag.getReaktionszeit() == v)
                    rangSummeA += eintrag.getRang();
            }
        }

        double rangSummeB = 0;
        for (double v : b) {
            for (TabelleEintrag eintrag : rangMap) {
                if (eintrag.getReaktionszeit() == v)
                    rangSummeB += eintrag.getRang();
            }
        }

        double u1 = 100 * 200 + (200 * 201 * 1 / 2) - rangSummeA;
        double u2 = 100 * 200 + (100 * 101 * 1 / 2) - rangSummeB;
        return u1 < u2 ? u1 : u2;
    }

    static public int uWertKritisch(Grenze grenze, Signifikanzniveau signifikanzNiveau, double E, double S) {
        double zWert;
        switch (signifikanzNiveau) {
            // Signifikanzniveau von 10%: z[kritisch]<0.10> = 1.645
            case ZEHN_PROZENT:
                zWert = 1.645;
                break;
            // Signifikanzniveau von 5%: z[kritisch]<0.05> = 1.96
            default:
                zWert = 1.96;
                break;
        }
        return (grenze == Grenze.UNTER) ? (int) Math.round(E - zWert * S) : (int) Math.round(E + zWert * S);
    }
}
