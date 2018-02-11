import java.io.*;
import java.util.Scanner;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class MainOne {

  private static boolean firstTransaction;
  private static boolean interactiveMode;
  private static HashMap<String, Integer> transactions;
  private static HashMap<String, Integer> accounts;
  private static String ledger;

  public static boolean isWellFormatted(String transactionLine) {
    boolean isWellFormatted = false;
    String[] transactionColumns = transactionLine.split(";");
    // checks for the correct number of columns
    boolean correctNumCols = false;
    if (transactionColumns.length == 5) {
      correctNumCols = true;
    }
    // checks to make sure transactionId is a length of 8
    boolean transactionIdLength = false;
    String transactionId = transactionColumns[0].replaceAll("\\s+","");
    if (transactionId.length() == 8) {
      transactionIdLength = true;
    }
    // checks to make sure both number of columns and transaction id are true
    // if true then transaction is well formatted
    if (correctNumCols && transactionIdLength) {
      isWellFormatted = true;
    }

    return isWellFormatted;
  }

  public static boolean inputsArePreviousTransactions(int m, String inputsCol) {
    boolean arePreviousTransactions = true;
    String[] inputTransactionIds = new String[m];
    String[] inputTransaction = inputsCol.split("\\)");
    // checks to make sure there are the correct amount of inputs
    if (inputTransaction.length == m && m != 0 && !firstTransaction) {
      // insert the input transaction ids into an array
      for (int i = 0; i<inputTransaction.length; i++) {
        inputTransaction[i] = inputTransaction[i].replaceAll("\\(", "");
        String[] eachInput = inputTransaction[i].split(",");
        inputTransactionIds[i] = eachInput[0].replaceAll("\\s+", "");
      }
      // check to see if input ids were previous transactions
      for (int i = 0; i < m; i++) {
        if (!transactions.containsKey(inputTransactionIds[i])) {
          System.out.println("Transaction " + inputTransactionIds[i] + " is not a previous transaction");
          arePreviousTransactions = false;
        }
      }
    } else if (m == 0 || firstTransaction) {
      arePreviousTransactions = true;
    } else {
      arePreviousTransactions = false;
    }
    return arePreviousTransactions;
  }

  public static int outputsAreValid(int m, String inputsCol, int n, String outputsCol) {
    int total = -1;
    String[] inputs = inputsCol.split("\\)");
    String[] outputs = outputsCol.split("\\)");
    String[] inputTransactionIds = new String[m];
    String[] outputBalancesString = new String[n];
    int[] outputBalances = new int[n];
    if (outputs.length == n) {
      // prints the inputs and outputs
      System.out.println("inputs: ");
      for (int i = 0; i < m; i++) {
        inputs[i] = inputs[i].replaceAll("\\(", "");
        System.out.println(inputs[i]);
      }
      System.out.println("outputs: ");
      for (int i = 0; i < n; i++) {
        outputs[i] = outputs[i].replaceAll("\\(", "");
        System.out.println(outputs[i]);
      }
      // total inputs
      int totalInputs = 0;
      for (int i = 0; i < m; i++) {
        String[] eachInput = inputs[i].split(",");
        inputTransactionIds[i] = eachInput[0].replaceAll("\\s+", "");
      }
      for (int i = 0; i < m; i++) {
        totalInputs += transactions.get(inputTransactionIds[i]);
      }
      // total outputs
      int totalOutputs = 0;
      for (int i = 0; i < n; i++) {
        String[] eachOutput = outputs[i].split(",");
        outputBalancesString[i] = eachOutput[1].replaceAll("\\s+", "");
      }
      for (int i = 0; i < n; i++) {
        outputBalances[i] = Integer.parseInt(outputBalancesString[i]);
        totalOutputs += outputBalances[i];
      }
      // print both totals
      System.out.println("total inputs: " + totalInputs);
      System.out.println("total outputs: " + totalOutputs);
      if (totalInputs == totalOutputs || firstTransaction) {
        total = totalOutputs;
        if (firstTransaction) {
          firstTransaction = false;
        }
      } else {
        total = -1;
      }
    } else {
      total = -1;
    }
    System.out.println("total = " + total);
    return total;
  }

  public static void completeTransaction(String transactionLine) {
    boolean isWellFormatted = isWellFormatted(transactionLine);
    if (isWellFormatted) {
      String[] transactionColumns = transactionLine.split(";");
      String transactionId = transactionColumns[0].replaceAll("\\s+","");
      String mString = transactionColumns[1].replaceAll("\\s+", "");
      int m = Integer.parseInt(mString);
      boolean inputsArePreviousTransactions = inputsArePreviousTransactions(m, transactionColumns[2]);
      // checks to make sure the inputs are valid
      if (inputsArePreviousTransactions) {
        String nString = transactionColumns[3].replaceAll("\\s+", "");
        int n = Integer.parseInt(nString);
        // checks to make sure the outputs are valid
        int total = outputsAreValid(m, transactionColumns[2], n, transactionColumns[4]);
        if (total != -1) {
          // adds the transaction to the transaction hash map
          transactions.put(transactionId, total);
          ledger = ledger + transactionLine + "\n";
        } else {
          System.err.println("Error: invalid outputs");
        }
      } else {
        System.err.println("Error: invalid inputs");
      }
    } else {
      System.err.println("Error: mal-formed transaction");
    }
  }

  public static void mainMenu() {

    Scanner scanner = new Scanner(System.in);

    System.out.println("[F]ile");
    System.out.println("[T]ransaction");
    System.out.println("[P]rint");
    System.out.println("[H]elp");
    System.out.println("[D]ump");
    System.out.println("[W]ipe");
    System.out.println("[I]nteractive");
    System.out.println("[V]erbose");
    System.out.println("[B]alance");
    System.out.println("[E]xit");
    System.out.print("Select a commmand: ");
    String command = scanner.next();
    scanner.nextLine();

    switch(command.toLowerCase()) {
      case "f":
        System.out.print("Supply Filename: ");
        String fileName = scanner.nextLine();
        try {
          File file = new File(fileName);
          FileReader fileReader = new FileReader(file);
          BufferedReader bufferedReader = new BufferedReader(fileReader);
          StringBuffer stringBuffer = new StringBuffer();
          String transactionLine;
          while ((transactionLine = bufferedReader.readLine()) != null) {
            completeTransaction(transactionLine);
          }
          fileReader.close();
        } catch (IOException e) {
          System.err.println("Error: file " + fileName + " cannot be opened for reading");
        }
        mainMenu();
        break;
      case "t":
        System.out.print("Enter Transaction: ");
        String transactionLine = scanner.nextLine();
        completeTransaction(transactionLine);
        mainMenu();
        break;
      case "p":
        System.out.println(ledger);
        mainMenu();
        break;
      case "h":
        System.out.println("Command Summary");
        System.out.println("[F]ile:  Supply filename:<infilename>.  Read in a file of transactions. Any invalid transaction shall be identified with an error message to stderr, but not stored. Print an error message to stderr if the input file named cannot be opened. The message shall be 'Error: file <infilename> cannot be opened for reading' on a single line, where <infilename> is the name provided as additional command input.");
        System.out.println("[T]ransaction: Supply Transaction:<see format below>   Read in a single transaction in the format shown below.  It shall be checked for validity against the ledger, and added if it is valid. If it is not valid, then do not add it to the ledger and print a message to stderr with the transaction number followed by a colon, a space, and the reason it is invalid on a single line.");
        System.out.println("[P]rint:  Print current ledger (all transactions in the order they were added) to stdout in the transaction format given below, one transaction per line.");
        System.out.println("[H]elp:  Command Summary");
        System.out.println("[D]ump:  Supply filename:<outfilename>.  Dump ledger to the named file. Print an error message to stderr if the output file named cannot be opened. The message shall be 'Error: file <outfilename> cannot be opened for writing' on a single line, where <outfilename> is the name provided as additional command input.");
        System.out.println("[W]ipe:  Wipe the entire ledger to start fresh.");
        System.out.println("[I]nteractive: Toggle interactive mode. Start in non-interactive mode, where no command prompts are printed. Print command prompts and prompts for additional input in interactive mode, starting immediately (i.e., print a command prompt following the I command).");
        System.out.println("[V]erbose: Toggle verbose mode. Start in non-verbose mode. In verbose mode, print additional diagnostic information as you wish. At all times, output each transaction number as it is read in, followed by a colon, a space, and the result ('good' or 'bad').");
        System.out.println("[B]alance:  Supply username:  (e.g. Alice).  This command prints the current balance of a user.");
        System.out.println("[E]xit:  Quit the program\n\ng");
        mainMenu();
        break;
      case "d":
        System.out.print("Supply filename: ");
        String outFileName = scanner.nextLine();
        try {
          BufferedWriter writer = new BufferedWriter(new FileWriter(outFileName));
          writer.write(ledger);
          writer.close();
        } catch (IOException e) {
          System.err.println("Error: file " + outFileName + " cannot be opened for writing");
        }
        mainMenu();
        break;
      case "e":
        System.out.println("Goodbye");
        System.exit(0);
      default:
        System.out.println("please select a valid option");
        mainMenu();
    }

  }

  public static void main(String[] args) {
    firstTransaction = true;
    interactiveMode = false;
    transactions = new HashMap<>();
    accounts = new HashMap<>();
    ledger = "";
    mainMenu();
  }

}
