package utility;

import lombok.experimental.UtilityClass;

@UtilityClass
public class LogUtility {

    private final boolean DEBUG = false;
    private final boolean LOGGING = true;
    private final boolean WARN = true;
    private final boolean DTW_PRINTING = false;
    private final boolean KKN_PRINTING = true;

    public boolean isDebugging() {
        return DEBUG;
    }

    public boolean isLogging() {
        return LOGGING;
    }

    public boolean isDtwPrinting() {
        return DTW_PRINTING;
    }

    public boolean isKknPrinting() {
        return KKN_PRINTING;
    }

    public static boolean isWarning() {
        return WARN;
    }

    public void printIfLogging(String statement) {
        if (isLogging()) {
            System.out.println(statement);
        }
    }

    public void printIfKknPrinting(String statement) {
        if (isKknPrinting()) {
            System.out.println(statement);
        }
    }

    public void printIfDebugging(String statement) {
        if (isDebugging()) {
            System.out.println(statement);
        }
    }

    public void printIndentedLine(String statement) {
        System.out.println("   >> " + statement);
    }

    public void printCurrentStep() {
        Throwable e = new Throwable();
        StackTraceElement[] elements = e.getStackTrace();

        if (elements.length > 1) {
            String unsanitizedStep = elements[1].toString();
            String sanitizedStep = unsanitizedStep.replaceAll("\\(.*\\)", "");
            String[] split = sanitizedStep.split("\\.");
            String unqualifiedMethod = split[split.length - 1];

            System.out.println("\nStep " + unqualifiedMethod);
        }
    }

    public void printCurrentMethod() {
        Throwable e = new Throwable();
        StackTraceElement[] elements = e.getStackTrace();

        if (elements.length > 1) {
            String unsanitizedStep = elements[1].toString();
            String sanitizedStep = unsanitizedStep.replaceAll("\\(.*\\)", "");
            String[] split = sanitizedStep.split("\\.");
            String unqualifiedMethod = split[split.length - 1];

            System.out.println("\nMethod " + unqualifiedMethod);
        }
    }
}
