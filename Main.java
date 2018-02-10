import java.io.*;
import java.util.Scanner;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Main {

    private static boolean firstTransaction;
    private static boolean interactiveMode;
    private static HashMap<String, Integer> transactions;
    private static HashMap<String, Integer> accounts;

    public static void printLedger(HashMap<String, Ledger> officialLedger) {
        System.out.println(officialLedger);
    }

    public static String[] parseOutputs(String outputs) {
      String[] parsed = outputs.split("\\)");
      System.out.println("first parsing: ");
      for (int i = 0;i < parsed.length ; i++) {
        System.out.println(parsed[i]);
        parsed[i] = parsed[i].replaceAll("\\(", "");
        System.out.println("without parenthesis");
        System.out.println(parsed[i]);
      }
      return parsed;
    }

    public static void updateAccounts(int n, String[] balances) {

      for (int i = 0; i < n; i++) {
        String[] accountAndBalance = balances[i].split(",");
        String username = accountAndBalance[0].replaceAll("\\s+", "");
        String samount = accountAndBalance[1].replaceAll("\\s+", "");
        int amount = Integer.parseInt(samount);
        // check if account already exists
        if (accounts.containsKey(username)) {
          // if the account already exists
          // add balance to current balance
          int balance = accounts.get(username);
          balance += amount;
          accounts.put(username, balance);
        } else {
          // if the account doesn't exist
          // create new key and value pair and add to accounts map
          accounts.put(username, amount);
        }
      }

    }

    public static int findTotal(int n, String[] balances) {
      int total = 0;
      System.out.println("balances to be totaled: ");
      for (int a = 0; a < balances.length; a++) {
        System.out.println(balances[a]);
      }
      for (int s = 0; s < balances.length; s++) {
        String[] accountAndBalance = balances[s].split(",");
        System.out.println("account " + accountAndBalance[0] + " is increasing by: " + accountAndBalance[1]);
        int additional = Integer.parseInt(accountAndBalance[1].replaceAll("\\s+",""));
        total += additional;
      }
      System.out.println("total: " + total);
      return total;
    }

    public static void mainMenu() {
        Scanner scanner = new Scanner(System.in);
	    // write your code here
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

	    // switch statement
	    switch(command.toLowerCase()) {
	        // case statements
	        // values must be of the same type of expression
            case "f":
                // Statements
                System.out.print("Supply filename: ");
                String fileName = scanner.nextLine();
                try {
                  File file = new File(fileName);
                  FileReader fileReader = new FileReader(file);
            			BufferedReader bufferedReader = new BufferedReader(fileReader);
            			StringBuffer stringBuffer = new StringBuffer();
            			String line;
            			while ((line = bufferedReader.readLine()) != null) {
                    String[] tranSplit = line.split(";");
                    System.out.println("transaction split: ");
                    if (tranSplit.length == 5) {
                      String transactionId = tranSplit[0].replaceAll("\\s+","");
                      System.out.println("transcation ID: " + transactionId);
                      // first transaction doesn't have to have any inputs
                      // can automatically credit accounts
                      if (firstTransaction) {
                        System.out.println("this is the first transaction");
                        String numOuts = tranSplit[3].replaceAll("\\s+","");
                        int n = Integer.parseInt(numOuts);
                        String[] balances = parseOutputs(tranSplit[4]);
                        int totalAmount = findTotal(n, balances);
                        // add transaction to transactions hash table
                        System.out.println("transaction id: " + transactionId);
                        System.out.println("total amount: " + totalAmount);
                        transactions.put(transactionId, totalAmount);
                        System.out.println("transactions hash: " + transactions);
                        // add balances to accounts hash table
                        updateAccounts(n, balances);
                        System.out.println("accounts hash table: " + accounts);
                        firstTransaction = false;
                      } else {
                        // if not the first transaction in the ledger
                        // checks to make sure there are inputs for the transaction
                        if (tranSplit[1].equals("0")) {
                          System.err.println("No UTXOS");
                        } else {

                        }
                      }
                    } else {
                      System.err.println("Error: transaction " + tranSplit[0] + " is not valid");
                    }
            				// stringBuffer.append(line);
            				// stringBuffer.append("\n");
            			}
            			fileReader.close();
            			// System.out.println("Contents of file:");
            			// System.out.println(stringBuffer.toString());
            		} catch (IOException e) {
            			System.err.println("Error: file " + fileName + " cannot be opened for reading");
            		}
	           mainMenu();
	           break;
            case "t":
                // Statements
                System.out.print("Enter Transaction: ");
                String transac = scanner.nextLine();
                System.out.println("transaction: " + transac);
                String[] tranSplit = transac.split(";");
                System.out.println("transaction split: ");
                for (int x = 0; x < tranSplit.length; x++){
                    System.out.println(tranSplit[x]);
                }
                mainMenu();
                break;
            case "p":
                // Statements
                System.out.println("you selected p");
                mainMenu();
                break;
            case "h":
                // Statements
                System.out.println("you selected h");
                mainMenu();
                break;
            case "d":
                // Statements
                System.out.println("you selected d");
                mainMenu();
                break;
            case "w":
                // statements
                System.out.println("you selected w");
                mainMenu();
                break;
            case "i":
                // statements
                System.out.println("you selected i");
                mainMenu();
                break;
            case "v":
                // statements
                System.out.println("you selected v");
                mainMenu();
                break;
            case "b":
                // statements
                System.out.println("you selected b");
                mainMenu();
                break;
            case "e":
                // statements
                System.out.println("Goodbye");
                System.exit(0);
                break;
            default:
                // statements
                System.out.println("please select a valid input");
                mainMenu();
	    }
    }

    public static void main(String[] args) {
      firstTransaction = true;
      interactiveMode = false;
      transactions = new HashMap<>();
      accounts = new HashMap<>();
        // HashMap<String, Integer> transactions = new HashMap<>();
        // HashMap<String, Integer> accounts = new HashMap<>();
        // boolean firstTransaction = true;
        // boolean interactiveMode = false;

//        Ledger ledg1 = new Ledger();
//        int m;
//        HashMap<String, Integer> utxos;
//        int n;
//        HashMap<String, Integer> transout;

//        4787df35; 1; (f2cea539, 0); 3; (Bob, 150)(Alice, 845)(Gopesh, 5)
//        int m = 1;
//        HashMap<String, Integer> utxos = new HashMap<>();
//        utxos.put("f2cea539", 0);
//        int n = 3;
//        HashMap<String, Integer> transout = new HashMap<>();
//        transout.put("Bob", 150);
//        transout.put("Alice", 845);
//        transout.put("Gopesh", 5);
//        Ledger ledg1 = new Ledger(m, utxos, n, transout);
//        officialLedger.put("4787df35", ledg1);
//        printLedger(officialLedger);
//        System.out.println(ledg1.utxos);
        mainMenu();
    }
}
