import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Random;

public class Main {

    public static final BigInteger E_FOR_GENERATING_KEYPAIR = new BigInteger("2").pow(16).add(new BigInteger("1"));

    public static void main(String[] args) {

        BigInteger p;
        BigInteger p1;
        BigInteger q;
        BigInteger q1;

        do {
            p = generatePseudoPrimeNumber(255, 256);
            p1 = generatePseudoPrimeNumber(255, 256);
            q = generatePseudoPrimeNumber(255, 256);
            q1 = generatePseudoPrimeNumber(255, 256);
        } while (!(getFiForPrimeNumbers(p, q)).gcd(E_FOR_GENERATING_KEYPAIR).equals(new BigInteger("1"))
                || !(getFiForPrimeNumbers(p1, q1)).gcd(E_FOR_GENERATING_KEYPAIR).equals(new BigInteger("1")));

        if (p.multiply(q).compareTo(p1.multiply(q1)) > 0) {
            BigInteger tempP = p;
            BigInteger tempQ = q;
            p = p1;
            q = q1;
            p1 = tempP;
            q1 = tempQ;
        }

        System.out.println("\np = " + p);
        System.out.println("q = " + q);
        System.out.println("p1 = " + p1);
        System.out.println("q1 = " + q1);

        KeyPair keyPairA = generateKeyPair(p, q);
        KeyPair keyPairB = generateKeyPair(p1, q1);

        doingGenrateMessageEncryptedSignaturedDecrypted(keyPairA, "A");
        doingGenrateMessageEncryptedSignaturedDecrypted(keyPairB, "B");


        System.out.println("\n\nSending and Received protocols:\n");
        //Emulate sending and retrieving message
        BigInteger k = generateRandomBigIntegerFromRange(new BigInteger(String.valueOf(2)).pow(254),
                new BigInteger(String.valueOf(2)).pow(255));

        PairKeySignature pairKeySignatureSend = sendKey(keyPairA.getOpenKey().getE(), keyPairA.getOpenKey().getN(), keyPairA.getPrivateKey().getD(),
                keyPairB.getOpenKey().getE(), keyPairB.getOpenKey().getN(), k);
        System.out.println();
        try {
            PairKeySignature pairKeySignatureAfterDecryption = receiveKey(pairKeySignatureSend, keyPairB.getPrivateKey().getD(),
                    keyPairB.getOpenKey().getN(), keyPairA.getOpenKey().getE(), keyPairA.getOpenKey().getN());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        System.out.println("end");

        BigInteger myd = new BigInteger("279769577559580217116711251091193480559950930679388550917062815800078768031869860250352487579379443366265905703657422752142891408919228233152652039364433");
        BigInteger myn = new BigInteger("9149330740779545254080791049283207153421908255456630469786200478587705698854809941220692663749114847377331535449110062518894094297282109019404550660947969");
        BigInteger mye = E_FOR_GENERATING_KEYPAIR;

        try {
            receiveKey(new PairKeySignature(new BigInteger("5242256003952150571933719331407066541463963742395669419144720855684495742719244945119398326639604690764807812748541084926468400700444515584157422614932213"),
                    new BigInteger("5705017197015080502203841890943797956059291020269808529560753419139396746466958080354698415228430037580184804975904022612003186773820483864830107960375241")),
                    myd, myn, mye, new BigInteger("11142119262520184618646233557144297157636969160544854566922845266724165361544967036926060585158582808608640001956304143956542839923171086154196316662038073"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class PairKeySignature {
        private BigInteger k;
        private BigInteger s;

        public PairKeySignature(BigInteger k, BigInteger s) {
            this.k = k;
            this.s = s;
        }

        public BigInteger getK() {
            return k;
        }

        public BigInteger getS() {
            return s;
        }

        @Override
        public String toString() {
            return "PairKeySignature{" +
                    "k=" + k +
                    ", s=" + s +
                    '}';
        }
    }

    public static PairKeySignature sendKey(BigInteger e, BigInteger n, BigInteger d, BigInteger e1, BigInteger n1, BigInteger k) {
        System.out.println("Text for sending: " + k);
        BigInteger k1 = encrypt(k, e1, n1);
        System.out.println("Encrypted text for sending: " + k1);
        BigInteger s = sign(k, d, n);
        System.out.println("Signature of sender: " + s);
        BigInteger s1 = encrypt(s, e1, n1);
        System.out.println("Encrypted signature of sender: " + s1);
        return new PairKeySignature(k1, s1);
    }

    public static PairKeySignature receiveKey(PairKeySignature pair, BigInteger d1, BigInteger n1, BigInteger e, BigInteger n) throws Exception {
        System.out.println("Recieved k1 and s1:\n" + pair.toString());
        BigInteger k = decrypt(pair.getK(), d1, n1);
        BigInteger s = decrypt(pair.getS(), d1, n1);
        boolean verify = verify(k, s, e, n);
        if (!verify) {
            throw new Exception("Сообщение было повреждено и его содержание восстановить не удасться");
        }
        System.out.println("Verification successfully!");
        System.out.println("Decrypted signature: " + s);
        System.out.println("Decrypted message: " + k);
        return new PairKeySignature(k, s);
    }

    public static void doingGenrateMessageEncryptedSignaturedDecrypted(KeyPair keyPair, String user) {
        System.out.println("\n" + keyPair);
        BigInteger m = generateRandomBigIntegerFromRange(new BigInteger(String.valueOf(2)).pow(254),
                new BigInteger(String.valueOf(2)).pow(255));
        System.out.println("Message from user " + user + " : " + m);
        BigInteger encrypted = encrypt(m, keyPair.openKey.getE(), keyPair.getOpenKey().getN());
        System.out.println("Encrypted text from user " + user + " : c = " + encrypted);

        BigInteger signature = sign(m, keyPair.getPrivateKey().getD(), keyPair.getOpenKey().getN());
        System.out.println("Digital sinature from user " + user + " : s = " + signature);

        System.out.println("Verification from user " + user + " : " + verify(m, signature, keyPair.getOpenKey().getE(), keyPair.getOpenKey().getN()));

        BigInteger decrypt = decrypt(encrypted, keyPair.getPrivateKey().getD(), keyPair.getOpenKey().getN());
        System.out.println("Decrypt ecnrypted message from user " + user + " : " + decrypt);
        System.out.println("Звірено з результатами работі ресурса http://asymcryptwebservice.appspot.com/?section=rsa, всі результати збігаються.");
    }

    public static BigInteger encrypt(BigInteger m, BigInteger e, BigInteger n) {
        return m.modPow(e, n);
    }

    public static BigInteger decrypt(BigInteger c, BigInteger d, BigInteger n) {
        return c.modPow(d, n);
    }

    public static BigInteger sign(BigInteger m, BigInteger d, BigInteger n) {
        return m.modPow(d, n);
    }

    public static boolean verify(BigInteger m, BigInteger s, BigInteger e, BigInteger n) {
        return m.equals(s.modPow(e, n));
    }

    public static BigInteger reverseElement(BigInteger a, BigInteger n) {
        BigInteger x = new BigInteger("0"), y = new BigInteger("1"),
                lastx = new BigInteger("1"), lasty = new BigInteger("0"), temp;
        while (!n.equals(new BigInteger("0"))) {
            BigInteger q = a.divide(n);
            BigInteger r = a.mod(n);

            a = n;
            n = r;

            temp = x;
            x = lastx.subtract(q.multiply(x));
            lastx = temp;

            temp = y;
            y = lasty.subtract(q.multiply(y));
            lasty = temp;
        }
//        System.out.println("Roots  x : "+ lastx +" y :"+ lasty);
        return lastx;
    }

    public static KeyPair generateKeyPair(BigInteger p, BigInteger q) {
        KeyPair keyPair = new KeyPair(p, q);
        BigInteger n = p.multiply(q);
        BigInteger fi = getFiForPrimeNumbers(p, q);
        BigInteger e = E_FOR_GENERATING_KEYPAIR;
        BigInteger d = reverseElement(e, fi);
        keyPair.openKey.setE(e);
        keyPair.openKey.setN(n);
        keyPair.privateKey.setD(d);
        return keyPair;
    }

    private static BigInteger getFiForPrimeNumbers(BigInteger p, BigInteger q) {
        return p.subtract(new BigInteger("1")).multiply(q.subtract(new BigInteger("1")));
    }

    static class KeyPair {
        private OpenKey openKey;
        private PrivateKey privateKey;

        KeyPair(BigInteger p, BigInteger q) {
            openKey = new OpenKey();
            privateKey = new PrivateKey(p, q);
        }


        static class PrivateKey {
            private BigInteger d;
            private BigInteger p;
            private BigInteger q;

            public PrivateKey(BigInteger p, BigInteger q) {
                this.p = p;
                this.q = q;
            }

            public BigInteger getD() {
                return d;
            }

            public void setD(BigInteger d) {
                this.d = d;
            }

            public BigInteger getP() {
                return p;
            }

            public void setP(BigInteger p) {
                this.p = p;
            }

            public BigInteger getQ() {
                return q;
            }

            public void setQ(BigInteger q) {
                this.q = q;
            }

            @Override
            public String toString() {
                return "d=" + d +
                        ", p=" + p +
                        ", q=" + q +
                        '}';
            }
        }

        static class OpenKey {
            private BigInteger n;
            private BigInteger e;

            public BigInteger getN() {
                return n;
            }

            public void setN(BigInteger n) {
                this.n = n;
            }

            public BigInteger getE() {
                return e;
            }

            public void setE(BigInteger e) {
                this.e = e;
            }

            @Override
            public String toString() {
                return "n=" + n +
                        ", e=" + e +
                        '}';
            }
        }

        public OpenKey getOpenKey() {
            return openKey;
        }

        public PrivateKey getPrivateKey() {
            return privateKey;
        }

        @Override
        public String toString() {
            return "KeyPair{" +
                    "openKey=" + openKey +
                    "\nprivateKey=" + privateKey +
                    '}';
        }
    }

    public static BigInteger generatePseudoPrimeNumber(int amountMinBit, int amountMaxBit) {
        int count = 0;
        BigInteger number = null;
        do {
            count++;
            System.out.println("Attempt number : " + count);
            boolean satisfact = false;
            while (!satisfact) {
                number = generateRandomBigIntegerFromRange(new BigInteger(String.valueOf(2)).pow(amountMinBit),
                        new BigInteger(String.valueOf(2)).pow(amountMaxBit));
                satisfact = checkSimpleConstraints(number);
            }
            System.out.println("number: " + number);
        } while (!millerRabinProbabilityTest(number));
        System.out.println("result of Miller-Rabin test for this number: " + millerRabinProbabilityTest(number) + ", amount of attempt to find = " + count);
        System.out.println("Pseudo prime number:" + number);
        System.out.println();
        return number;
    }

    public static BigInteger generateRandomBigIntegerFromRange(BigInteger min, BigInteger max) {
        BigDecimal minD = new BigDecimal(min);
        BigDecimal maxD = new BigDecimal(max);
        BigDecimal randomBigInteger = minD.add(new BigDecimal(Math.random()).multiply(maxD.subtract(minD.add(BigDecimal.valueOf(1)))));
        return new BigInteger(String.valueOf(randomBigInteger.setScale(0, BigDecimal.ROUND_HALF_UP)));
    }

    public static boolean checkSimpleConstraints(BigInteger number) {
        boolean result = true;
        String text = String.valueOf(number);
        int length = text.length();
        int sum = 0;
        for (int i = 0; i < length; i++) {
            sum += Integer.valueOf(text.substring(i, i + 1));
        }
        if (text.substring(length - 1, length).equals("0") ||
                text.substring(length - 1, length).equals("2") ||
                text.substring(length - 1, length).equals("4") ||
                text.substring(length - 1, length).equals("5") ||
                text.substring(length - 1, length).equals("6") ||
                text.substring(length - 1, length).equals("8") ||
                sum % 3 == 0) {
            result = false;
        }
        return result;
    }

    public static boolean millerRabinProbabilityTest(BigInteger p) {
        int counter = 0;
        boolean changeCounter = false;
        int k = new Random().nextInt(46) + 5;

        //Крок 0
        BigInteger numberS1 = p.subtract(BigInteger.valueOf(1));
        BigInteger d;
        int s = 0;
        while (numberS1.mod(BigInteger.valueOf(2)).equals(new BigInteger(String.valueOf(0)))) {
            s++;
            numberS1 = numberS1.divide(BigInteger.valueOf(2));
        }
        d = numberS1;
        System.out.println("d = " + d + "\ns = " + s);


        while (counter < k) {
            changeCounter = false;
            //Крок 1
            BigInteger x = generateRandomX(BigInteger.valueOf(2), p);
            BigInteger gcd = new BigInteger(String.valueOf(x)).gcd(new BigInteger(String.valueOf(p)));

            if (!gcd.equals(new BigInteger(String.valueOf(1)))) {
                return false;
            }

            //Крок 2
            if (x.modPow(d, p).equals(new BigInteger("1")) || x.modPow(d, p).equals(new BigInteger("-1").mod(p))) {
                counter++;
                changeCounter = true;
            } else {
                for (int r = 1; r < s - 1; r++) {
                    BigInteger xr = x.modPow(d.subtract(new BigInteger("2").pow(r)), p);
                    if (xr.equals(new BigInteger("1"))) {
                        return false;
                    }
                    if (xr.equals(new BigInteger("-1")) || xr.equals(new BigInteger("-1").mod(p))) {
                        counter++;
                        changeCounter = true;
                        break;
                    }
                }
            }

            if (!changeCounter) {
                return false;
            }
        }

        return true;
    }

    public static BigInteger generateRandomX(BigInteger minLimit, BigInteger maxLimit) {
        BigInteger bigInteger = maxLimit.subtract(minLimit);
        Random randNum = new Random();
        int len = maxLimit.bitLength();
        int randomBitLength = new Random().nextInt(len) + 1;
        BigInteger res = new BigInteger(randomBitLength, randNum);
        if (res.compareTo(minLimit) < 0)
            res = res.add(minLimit);
        if (res.compareTo(bigInteger) >= 0)
            res = res.mod(bigInteger).add(minLimit);
        return res;
    }
}
