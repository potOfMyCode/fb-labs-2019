import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

public class Main {


    public static final String KEY_2 = "як";
    public static final String KEY_3 = "лес";
    public static final String KEY_4 = "киев";
    public static final String KEY_5 = "осень";
    public static final String KEY_10 = "вольныйкот";
    public static final String KEY_11 = "белорусский";
    public static final String KEY_12 = "революцияроз";
    public static final String KEY_13 = "столетняягора";
    public static final String KEY_14 = "бордодождливый";
    public static final String KEY_15 = "внешняяразведка";
    public static final String KEY_16 = "немецлюбитмюнхен";
    public static final String KEY_17 = "отличныйутебявкус";
    public static final String KEY_18 = "государствобельгия";
    public static final String KEY_19 = "коричневорубашечник";
    public static final String KEY_20 = "иерусалимскаядевочка";
    private static final int CPACITY = 32;

    private static Map<String, Integer> indexOfLetter = new TreeMap<>();

    private static Map<Integer, String> letterByIndex = new TreeMap<>();

    private static StringBuffer getFileContent(){
        StringBuffer fileData = new StringBuffer();
        try(FileReader reader = new FileReader("pushkin.txt")){
            int c;
            while((c=reader.read())!=-1){
                if (c == 1105 || c == 1025)
                    c = 1077;
                if(((c >= 1072) &&(c <= 1103))||((c >= 1040) && (c <= 1071))) {
                    if (((c >= 1040) && (c <= 1071)))
                        c += 32;
                    if (c == ' ') {
                        c = '0';
                        if (fileData.charAt(fileData.length() - 1) == '0')
                            continue;
                    }
                    fileData.append((char) c);
                }
            }
        }catch(IOException ex){
            System.out.println(ex.getMessage());
        }

        return fileData;
    }

    public static void main(String[] args) {

        initAndShowIndexOfLetterMap();

        StringBuffer text = getFileContent();
        System.out.println("Text: \n" +text);

        StringBuffer cryptoTextKey2 = encrypt(text, KEY_2);
        StringBuffer cryptoTextKey3 = encrypt(text, KEY_3);
        StringBuffer cryptoTextKey4 = encrypt(text, KEY_4);
        StringBuffer cryptoTextKey5 = encrypt(text, KEY_5);
        StringBuffer cryptoTextKey10 = encrypt(text, KEY_10);
        StringBuffer cryptoTextKey11 = encrypt(text, KEY_11);
        StringBuffer cryptoTextKey12 = encrypt(text, KEY_12);
        StringBuffer cryptoTextKey13 = encrypt(text, KEY_13);
        StringBuffer cryptoTextKey14 = encrypt(text, KEY_14);
        StringBuffer cryptoTextKey15 = encrypt(text, KEY_15);
        StringBuffer cryptoTextKey16= encrypt(text, KEY_16);
        StringBuffer cryptoTextKey17 = encrypt(text, KEY_17);
        StringBuffer cryptoTextKey18 = encrypt(text, KEY_18);
        StringBuffer cryptoTextKey19 = encrypt(text, KEY_19);
        StringBuffer cryptoTextKey20 = encrypt(text, KEY_20);

        showCryptoText(cryptoTextKey2, "cryptoTextKey2: ");
        showCryptoText(cryptoTextKey3, "cryptoTextKey3: ");
        showCryptoText(cryptoTextKey4, "cryptoTextKey4: ");
        showCryptoText(cryptoTextKey5, "cryptoTextKey5: ");
        showCryptoText(cryptoTextKey10, "cryptoTextKey10: ");
        showCryptoText(cryptoTextKey11, "cryptoTextKey11: ");
        showCryptoText(cryptoTextKey12, "cryptoTextKey12: ");
        showCryptoText(cryptoTextKey13, "cryptoTextKey13: ");
        showCryptoText(cryptoTextKey14, "cryptoTextKey14: ");
        showCryptoText(cryptoTextKey15, "cryptoTextKey15: ");
        showCryptoText(cryptoTextKey16, "cryptoTextKey16: ");
        showCryptoText(cryptoTextKey17, "cryptoTextKey17: ");
        showCryptoText(cryptoTextKey18, "cryptoTextKey18: ");
        showCryptoText(cryptoTextKey19, "cryptoTextKey19: ");
        showCryptoText(cryptoTextKey20, "cryptoTextKey20: ");


    }

    private static void initAndShowIndexOfLetterMap() {
        String[] alphabetWithoutSpaces ={"а","б","в","г","д","е","ж","з","и","й","к","л","м","н","о","п","р","с","т","у","ф","х","ц","ч","ш","щ","ъ","ы","ь","э","ю","я"};

        for(int i=0; i<32; i++){
            indexOfLetter.put(alphabetWithoutSpaces[i], i);
            letterByIndex.put(i, alphabetWithoutSpaces[i]);
        }
        System.out.println("Alphabet: ");
        System.out.println(indexOfLetter);
        System.out.println("AlphabetReverse:\n" + letterByIndex);
    }

    private static StringBuffer encrypt(StringBuffer text, String key){
        StringBuffer result = new StringBuffer();
        for(int i=0; i< text.length(); i++){
            int index = indexOfLetter.get(String.valueOf(text.charAt(i)));

            String letterFromKey = String.valueOf(key.charAt(i%key.length()));

            result.append(letterByIndex.get((index+indexOfLetter.get(letterFromKey))%CPACITY));
        }
        return result;
    }

    private static void showCryptoText(StringBuffer text, String desc){
        System.out.println("\n" + desc + "\n" + text);
    }
}
