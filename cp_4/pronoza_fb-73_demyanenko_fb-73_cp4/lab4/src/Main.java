import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

public class Main {
    public static final int[] coefficientsP1 = {1, 0, 1, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0};
    public static final int[] coefficientsP2 = {1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1, 0, 1, 1, 0, 0, 0, 0, 0, 0};

    public static ArrayList<Integer> sequenceP1 = new ArrayList<>();
    public static ArrayList<Integer> sequenceP2 = new ArrayList<>();

    public static void main(String[] args) {

        System.out.println("var7:\n" +
                "p1(x)= x^23 + x^20 + x^7 + x^6 + x^5 + x^2 + 1\n" +
                "p2(x)= x^20 + x^13 + x^12 + x^10 + x^6 + x^5 + x^4 + x^3 + x^2 + x + 1");

        int[] impulseFunctionP1 = new int[coefficientsP1.length];
        impulseFunctionP1[coefficientsP1.length - 1] = 1;
        int[] impulseFunctionP2 = new int[coefficientsP2.length];
        impulseFunctionP2[coefficientsP2.length - 1] = 1;
        System.out.println("impulse function for p1: " + Arrays.toString(impulseFunctionP1));
        System.out.println("impulse function for p2: " + Arrays.toString(impulseFunctionP2));

        int periodP1 = linearShiftRegister(coefficientsP1, impulseFunctionP1, sequenceP1, "sequenceP1.txt");
        int periodP2 = linearShiftRegister(coefficientsP2, impulseFunctionP2, sequenceP2, "sequenceP2.txt");

        System.out.println("Згенеровані послідовності ви можете переглянути у файлах sequenceP1.txt та sequenceP2.txt");

        System.out.println("Period P1: " + periodP1);
        System.out.println("Period P2: " + periodP2);

        int maxPeriodP1 = (int) Math.pow(2, coefficientsP1.length) - 1;
        int maxPeriodP2 = (int) Math.pow(2, coefficientsP2.length) - 1;

        detectPolinomType(periodP1, maxPeriodP1, "P1");
        detectPolinomType(periodP2, maxPeriodP2, "P2");


        // HERE WE CALCULATE FREQUENCY N-GRAM AND WRITE RESULT IN THE FILE (THIS PROCESS ENOUGH LONG, SO IT'S COMMENTED,
        // BECAUSE FILES WAS ALREADY CREATED AND IT WILL BE JUST REWRITED IF WE UNCOMMENTED THIS)
//        for (int i = 2; i < coefficientsP1.length + 1; i++) {
//            createNgramFrequency(readFile("sequenceP1.txt"), i, "P1");
//        }
//        for (int i = 2; i < coefficientsP2.length + 1; i++) {
//            createNgramFrequency(readFile("sequenceP2.txt"), i, "P2");
//        }

        System.out.println("Calculate auto correlation coefficients: ");
        System.out.println("For P1:");
        for (int i =1 ; i< 10 ; i++){
            System.out.println("d = " + i + " : " + calculateAutoCor(sequenceP1, periodP1, i));
        }
        System.out.println("\nFor P2:");
        for (int i =1 ; i< 10 ; i++){
            System.out.println("d = " + i + " : " + calculateAutoCor(sequenceP2, periodP2, i));
        }


    }

    public static void showLinearRecurrenceRelations(String num, int... coef) {
        System.out.println("Лінійне рекурентне співвідношення для p" + num + "(x):\n" +
                "s(i+n)=");
        for (int i = coef.length - 1; i >= 0; i--) {
            if (coef[i] == 1) {
                System.out.print("1*s(i+" + i + ")");
            }
        }
    }


    public static int linearShiftRegister(int[] coefficients, int[] impulseFunction, ArrayList<Integer> sequence, String fileName) {
        int[] register = new int[impulseFunction.length];
        System.arraycopy(impulseFunction, 0, register, 0, impulseFunction.length);
        int period = 0;

        try (FileWriter writer = new FileWriter(fileName, true)) {
            cleanFile(fileName);
            do {
                period++;
                int tempBit = shiftSequenceOneTact(coefficients, register);
                writer.write(String.valueOf(tempBit));
                sequence.add(tempBit);
            } while (!Arrays.equals(register, impulseFunction));
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return period;
    }

    private static void cleanFile(String fileName) {
        try (FileWriter writer = new FileWriter(fileName, false)) {
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static int shiftSequenceOneTact(int[] coefficients, int[] register) {
        int firstBit = register[0];
        int generetedBit = 0;
        for (int i = 0; i < register.length; i++) {
            if (coefficients[i] != 0) {
                generetedBit += register[i];
            }
        }
        for (int i = 0; i < register.length - 1; i++) {
            register[i] = register[i + 1];
        }
        generetedBit = generetedBit % 2;
        register[register.length - 1] = generetedBit;
        return firstBit;
    }

    public static void detectPolinomType(int realPeriod, int maxPeriod, String name) {
        if (realPeriod == maxPeriod) {
            System.out.println(name + " є примітивним поліномом, так як його період дорівнює макс.періоду(T=q^n-1)");
        } else if (maxPeriod % realPeriod == 0) {
            System.out.println(name + " є непримітивним та незвідним поліномом над даним полем, так як його період кратний q^n-1");
        } else {
            System.out.println(name + " є непримітивним  і звідним поліномом над даним полем");
        }
    }

    public static StringBuffer readFile(String fileName) {
        StringBuffer text = new StringBuffer();
        FileReader fr;
        try {
            fr = new FileReader(fileName);
            int symbol;
            while ((symbol = fr.read()) != -1) {
                if (symbol == 48) {
                    text.append("0");
                } else if (symbol == 49) {
                    text.append("1");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return text;
    }

    private static int countAmountNgram(StringBuffer fileData, int step) {
        return fileData.length() - step + 1;
    }

    private static void createNgramFrequency(StringBuffer fileData, int n, String version) {
        int total = countAmountNgram(fileData, n);
        Map<String, Integer> alphabet = new HashMap<>();
        for (int i = 0; i < fileData.length() - n; i++) {
            String ngram = fileData.substring(i, i + n);

            int temp = alphabet.getOrDefault(ngram, 0);
            temp++;

            alphabet.put(ngram, temp);
        }
        String filename = "..//n-gram_frequency//" + n + "-gram frequency" + version + ".txt";
        try (FileWriter writer = new FileWriter(filename, true)) {
            cleanFile(filename);
            for (Map.Entry<String, Integer> e : alphabet.entrySet()) {
                String frequency = new DecimalFormat("#0.0000000000").format((double) e.getValue() / total);
                writer.write("\"" + e.getKey() + "\" = " + frequency + System.getProperty("line.separator"));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static int calculateAutoCor(ArrayList<Integer> sequence, int period, int step) {
        int coefficient = 0;
        for (int i = 0; i < sequence.size(); i++) {
            coefficient += (sequence.get(i) + sequence.get((i + step) % period)) % 2;
        }
        return coefficient;
    }
}
