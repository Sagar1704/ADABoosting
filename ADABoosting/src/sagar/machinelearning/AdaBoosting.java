package sagar.machinelearning;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class AdaBoosting {
	private Scanner scanner;
	private String inputFilePath;
	private BinaryAda binaryAda;
	private RealAda realAda;

	public AdaBoosting() {
		this.inputFilePath = "";
		this.binaryAda = new BinaryAda();
		this.realAda = new RealAda();
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
		System.out.println("#***************************************");
		System.out.println("#Ada Boosting");
		System.out.println("#***************************************");

		AdaBoosting ada = new AdaBoosting();
		int choice = 0;
		do {
			choice = ada.displayMenu();
			switch (choice) {
			case 1:
				ada.readFile();
				break;
			case 2:
				ada.binaryAda.boost();
				break;
			case 3:
				ada.realAda.boost();
				break;
			case 4:
				System.exit(0);
			default:
				System.out.println("#Wrong Choice. Enter Again.");
				break;
			}
		} while (choice != 4);

	}

	public void readFile() {
		System.out.print("#Enter input file path::");
		scanner = new Scanner(System.in);
		inputFilePath = scanner.nextLine();

		try {
			scanner = new Scanner(new File(inputFilePath));
			int counter = 1;
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				switch (counter++) {
				case 1:
					realAda.setT(Integer.parseInt(line.split(" ")[0]));
					realAda.setN(Integer.parseInt(line.split(" ")[1]));
					realAda.setEpsilon(Double.parseDouble(line.split(" ")[2]));
					break;
				case 2:
					ArrayList<Double> examples = new ArrayList<Double>();
					for (String example : line.split(" ")) {
						examples.add(Double.parseDouble(example));
					}
					realAda.setX(examples);
					break;
				case 3:
					ArrayList<Boolean> values = new ArrayList<Boolean>();
					for (String value : line.split(" ")) {
						values.add(Integer.parseInt(value) == 1 ? true : false);
					}
					realAda.setY(values);
					break;
				case 4:
					ArrayList<Double> probabilities = new ArrayList<Double>();
					for (String probability : line.split(" ")) {
						probabilities.add(Double.parseDouble(probability));
					}
					realAda.setP(probabilities);
					break;
				default:
					break;
				}
			}
			binaryAda = new BinaryAda(realAda.getT(), realAda.getN(), realAda.getX(), realAda.getY(), realAda.getP());
		} catch (FileNotFoundException e) {
			System.out.println("#File not found. Please retry.");
			readFile();
		} finally {
			closeScanner();
		}
	}

	public void closeScanner() {
		if (scanner != null) {
			scanner.close();
		}
	}

	private int displayMenu() {
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
		} catch (NumberFormatException e) {
			System.out.println("#Please enter a numeric choice");
			return displayMenu();
		} finally {
			closeScanner();
		}

		return choice;

	}
}
