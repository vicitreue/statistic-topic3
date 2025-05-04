package com.thema3statistik;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Thema3 {

    // Levene Test
    static List<Double> absAbweichungvonTrainingE = new ArrayList<>(); 
    static List<Double> absAbweichungvonRennenE = new ArrayList<>(); 
    static Double totaleAbweichungenT = 0.0; 
    static Double totaleAbweichungenR = 0.0;

    // ANOVA
    static List<Double> alleAbweichungenQuadrat = new ArrayList<>();
    static Double totaleAbweichungen = 0.0;

    public static void main(String[] args) {

        String[] trainingZeiten = FileManager.fileData("src/files/training-data.txt").split(",");
        String[] rennenZeiten = FileManager.fileData("src/files/rennen-data.txt").split(",");

        // Mittelwert, Standardabweichung
        double trainingE = StatisticHelper.durchschnittBerechnen(trainingZeiten);
        double rennenE = StatisticHelper.durchschnittBerechnen(rennenZeiten);
        System.out.println("DURCHSCHNITT: (T) " + trainingE + " | (R) " + rennenE);
        System.out.println("STANDARDABWEICHUNG: (T) " + standardabweichungBerechnen(trainingZeiten, 200, trainingE)
                + " | (R) " + standardabweichungBerechnen(rennenZeiten, 100, rennenE));

        // Mann Whitney U Test
        List<TabelleEintrag> alleZeiten = new ArrayList<>();
        for (String zeit : trainingZeiten) {
            alleZeiten.add(new TabelleEintrag(0, Double.parseDouble(zeit), Reaktionszeit.TRAINING));
        }
        for (String zeit : rennenZeiten) {
            alleZeiten.add(new TabelleEintrag(0, Double.parseDouble(zeit), Reaktionszeit.RENNEN));
        }
        alleZeiten.sort(Comparator.comparingDouble(TabelleEintrag::getReaktionszeit));

        int rangsummeTraining = 0;
        int rangsumeRennen = 0;
        for (int i = 0; i < alleZeiten.size(); i++) {
            TabelleEintrag aktuellerEintrag = alleZeiten.get(i);
            aktuellerEintrag.setRang(i + 1);

            if (aktuellerEintrag.getReaktionszeitTyp() == Reaktionszeit.TRAINING) {
                rangsummeTraining += aktuellerEintrag.getRang();
            } else if (aktuellerEintrag.getReaktionszeitTyp() == Reaktionszeit.RENNEN) {
                rangsumeRennen += aktuellerEintrag.getRang();
            }
        }
        System.out.println("\nRANGSUMMEN -- Training: " + rangsummeTraining + " | Rennen: " + rangsumeRennen);
        System.out.println("RANGSUMMEN (Mittelwert) -- Training: " + Double.valueOf(rangsummeTraining) / 200
                + " | Rennen: " + Double.valueOf(rangsumeRennen) / 100);

        double u1 = 100 * 200 + (200 * 201 * 1 / 2) - rangsummeTraining;
        double u2 = 100 * 200 + (100 * 101 * 1 / 2) - rangsumeRennen;
        double u = u1 < u2 ? u1 : u2;
        System.out.println("U-WERT: " + u + " || U1 (Training): " + u1 + " || U2 (Rennen): " + u2);

        double E = 100 * 200 / 2;
        System.out.println("Erwartungswert von U: " + E);

        double S = Math.sqrt(100 * 200 * (100 + 200 + 1) / 12);
        System.out.println("Standardfehler von U: " + S);

        // Normalapproximation
        System.out.println("z-Wert: " + StatisticHelper.zWertNormalapproximation(E, S, u)); 

        // Exakte Berechnung
        int uKritisch5Unter = StatisticHelper.uWertKritisch(Grenze.UNTER, Signifikanzniveau.FUENF_PROZENT, E, S);
        int uKritisch5Ober = StatisticHelper.uWertKritisch(Grenze.OBER, Signifikanzniveau.FUENF_PROZENT, E, S);
        System.out.println("\nKrtischer U-Wert (alpha = 0.05): U<" + uKritisch5Unter + " oder U>" + uKritisch5Ober);

        int uKritisch10Unter = StatisticHelper.uWertKritisch(Grenze.UNTER, Signifikanzniveau.ZEHN_PROZENT, E, S);
        int uKritisch10Ober = StatisticHelper.uWertKritisch(Grenze.OBER, Signifikanzniveau.ZEHN_PROZENT, E, S);
        System.out.println("Krtischer U-Wert (alpha = 0.1): U<" + uKritisch10Unter + " oder U>" + uKritisch10Ober);


        // t Test für unabhängige Stichproben

        // Alle Kombinationen von Gruppe A
        List<Double> alleWerte = new ArrayList<>();
        for (TabelleEintrag eintrag : alleZeiten)
            alleWerte.add(eintrag.getReaktionszeit());

        int extremerOderGleich = 0;
        for (int i = 0; i < 10000; i++) {
            Collections.shuffle(alleWerte);
            List<Double> permA = alleWerte.subList(0, 199);
            List<Double> permB = alleWerte.subList(200, 299);

            if (StatisticHelper.berechneU(permA, permB, alleZeiten) <= u) {
                extremerOderGleich++;
            }
        }

        double pWert = (double) extremerOderGleich / 10000;
        System.out.println("Extremer (Anzahl): " + extremerOderGleich);
        System.out.println("p-Wert: " + pWert);


        // LEVENE TEST
        LeveneTest trainingLevene = new LeveneTest();
        trainingLevene.leveneTest(absAbweichungvonTrainingE, totaleAbweichungenT, 200); 
        Double varianzT = trainingLevene.getVarianz();
        Double xQuerT = trainingLevene.getXQuer();

        LeveneTest rennenLevene = new LeveneTest();
        rennenLevene.leveneTest(absAbweichungvonRennenE, totaleAbweichungenR, 100); 
        Double varianzR = trainingLevene.getVarianz();
        Double xQuerR = trainingLevene.getXQuer();

        System.out.println("t-Wert: " + ZweiStichprobenTtest.tWertBerechnen(xQuerT, xQuerR, varianzT, varianzR, 200, 100));
    }

    public static double standardabweichungBerechnen(String[] zeiten, int n, double E) {
        double abweichung = 0;
        for (String zeitString : zeiten) {
            double zeit = Double.parseDouble(zeitString);
            abweichung += ((zeit - E) * (zeit - E));

            // Mittelwert der Abweichungen
            if (n == 200) {
                absAbweichungvonTrainingE.add(Math.abs(zeit - E));
                totaleAbweichungenT += Math.abs(zeit - E);
            } else if (n == 100) {
                absAbweichungvonRennenE.add(Math.abs(zeit - E));
                totaleAbweichungenR += Math.abs(zeit - E);
            }
            totaleAbweichungen += Math.abs(zeit - E); // ANOVA
            alleAbweichungenQuadrat.add((zeit - E) * (zeit - E)); // ANOVA
        }
        return Math.sqrt(abweichung / (n - 1));
    }
}
