package sagar.machinelearning;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import sagar.machinelearning.exceptions.FileNotScannedException;

/**
 * This program is to illustrate the working of Adaptive Boosting algorithm in machine learning
 * 
 * @author Sagar
 * 
 */
public class AdaBoosting {
	private Scanner scanner;
	private String inputFilePath;
	private BinaryAda binaryAda;
	private RealAda realAda;
	private static boolean fileScanned = false;

	public AdaBoosting() {
		this.inputFilePath = "";
	}

	public String getInputFilePath() {
		return inputFilePath;
	}

	public void setInputFilePath(String inputFilePath) {
		this.inputFilePath = inputFilePath;
	}

	public void setScanner(Scanner scanner) {
		this.scanner = scanner;
	}

	public static void main(String[] args) {
		System.out.println("Deshpande Sagar");
		System.out.println("#***************************************");
		System.out.println("#\tAda Boosting");

		AdaBoosting ada = new AdaBoosting();
		int choice = 0;
		do {
			// This is a menu driven program
			choice = ada.displayMenu();
			switch (choice) {
			case 1:
				ada.readFile();
				break;
			case 2:
				System.out.println(ada.binaryAda.boost().toString());
				break;
			case 3:
				System.out.println(ada.realAda.boost().toString());
				break;
			case 4:
				System.exit(0);
			default:
				System.out.println("#Wrong Choice. Enter Again.");
				break;
			}
		} while (choice != 4);

	}

	/**
	 * Reads the input file
	 * Gets the following data from the input file:
	 * 1. Number of iterations
	 * 2. Number of examples
	 * 3. Smoothing value
	 * 4. Learning examples
	 * 5. Example classifications
	 * 6. Example's initial probabilities
	 */
	public void readFile() {
		System.out.print("#Enter input file path::");

		try {
			scanner = new Scanner(System.in);
			inputFilePath = scanner.nextLine();

			scanner = new Scanner(new File(inputFilePath));
			int counter = 1;
			ArrayList<Double> examples = new ArrayList<Double>();
			ArrayList<Boolean> values = new ArrayList<Boolean>();
			ArrayList<Double> probabilities = new ArrayList<Double>();
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				switch (counter++) {
				case 1:
					realAda = new RealAda();
					realAda.setT(Integer.parseInt(line.split(" ")[0]));
					realAda.setN(Integer.parseInt(line.split(" ")[1]));
					realAda.setEpsilon(Double.parseDouble(line.split(" ")[2]));
					System.out.println("#File Scanned successfully.");
					break;
				case 2:
					for (String example : line.split(" ")) {
						examples.add(Double.parseDouble(example));
					}
					break;
				case 3:
					for (String value : line.split(" ")) {
						values.add(Integer.parseInt(value) == 1 ? true : false);
					}
					break;
				case 4:
					for (String probability : line.split(" ")) {
						probabilities.add(Double.parseDouble(probability));
					}
					break;
				default:
					break;
				}
			}
			ArrayList<ADAInput> inputs = new ArrayList<ADAInput>();
			for (int exampleCounter = 0; exampleCounter < examples.size(); exampleCounter++) {
				inputs.add(new ADAInput(examples.get(exampleCounter), values.get(exampleCounter), probabilities
					.get(exampleCounter)));
			}
			realAda.setInputs(inputs);
			binaryAda = new BinaryAda(realAda.getT(), realAda.getN(), realAda.getInputs());
		} catch (FileNotFoundException e) {
			System.out.println("#File not found. Please retry.");
			readFile();
		}
	}

	private int displayMenu() {
		System.out.println("#***************************************");
		System.out.println("#Display Menu");
		System.out.println("#***************************************");
		System.out.println("#(1) Enter input file");
		System.out.println("#(2) Binary Ada Boosting");
		System.out.println("#(3) Real Ada Boosting");
		System.out.println("#(4) Exit");
		System.out.print("#Enter Choice::");
		scanner = new Scanner(System.in);
		int choice = -1;
		try {
			choice = Integer.parseInt(scanner.next());

			if (choice == 1 || choice == 4) {
				fileScanned = true;
			} else {
				if (!fileScanned) {
					throw new FileNotScannedException("#Please scan the file first");
				}
			}
		} catch (NumberFormatException e) {
			System.out.println("#Please enter a numeric choice");
			return displayMenu();
		} catch (FileNotScannedException e) {
			System.out.println(e.getMessage());
			return displayMenu();
		} finally {
			// closeScanner();
		}

		return choice;

	}
}
