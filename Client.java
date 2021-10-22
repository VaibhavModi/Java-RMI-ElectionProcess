import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
//Importing arraylist 
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Client {

	private Client() {
	}

	
	public static void main(String[] args)
	{

		int flag = 0;

		Scanner s = new Scanner(System.in);// reading from the system
		// String host = (args.length < 1) ? null : args[0];
		try {
			Registry registry = LocateRegistry.getRegistry(2525);
			RmiInterface stub = (RmiInterface) registry.lookup("Election");

			HashMap<String, Integer> voteTally = new HashMap<String, Integer>();


			ArrayList<String> invalidCandArray = new ArrayList<String>();


			System.out.println("\n[Client] Connected to voting system servers!\n");

			while (flag == 0) {
				System.out.println("The choices:");
				System.out.println("1. Register to Vote");
				System.out.println("2. Verify Voter");
				System.out.println("3. Vote");
				System.out.println("4. Tally Results");
				System.out.println("5. Announce Winner");
				System.out.println("6. Supervisor mode");
				System.out.println("0. Quit");
				System.out.println("_____________________");
				System.out.print("Enter your choice: ");
				int choice = s.nextInt();
				System.out.println("_____________________");

				switch (choice) {
					case 1:

						System.out.print("Enter the voter name: ");
						String Vname = s.next();
						System.out.print("Enter your Gender(F or M): ");
						String gender = s.next().toUpperCase();

						System.out.print("\nVoter is registered with the id (check the id in server terminal): ");
						int Vid = stub.registerVoter(Vname,gender);
						System.out.println(Vid);
						System.out.println("_____________________\n");

						break;
					case 2:
						System.out.println("_____________________");
						System.out.print("Enter your id to check: ");
						int x = s.nextInt();
						System.out.println("Valid Voter with name - "+stub.VerifyVoter(x));
						System.out.println("_____________________");
						break;
					case 3:
						ArrayList<String> Candidates = stub.Candidates();	
						System.out.println("Invalid candidates: ");
						System.out.println("______________________");
						for (int i = 0; i < invalidCandArray.size(); i++) {
							System.out.print(i);
							System.out.print(". ");
							System.out.println(invalidCandArray.get(i));
						}

						System.out.println("\nEligible Candidates List");
						System.out.println("_____________________");
						
						
						for (int i = 0; i < Candidates.size(); i++) {
							if (invalidCandArray.indexOf(Candidates.get(i))== -1){
								System.out.print(i);
								System.out.print(". ");
								System.out.println(Candidates.get(i));
							}
						}

						System.out.println("_____________________");
						System.out.print("Enter your VoterId to vote: ");
						int id = s.nextInt();
						System.out.print("Enter the index number of candidate to vote: ");
						int candIndex = s.nextInt();
						System.out.println(stub.Vote(id, candIndex));

						System.out.println("_____________________");
						break;
					case 4:
						System.out.println("\nThe vote counts are as follows: ");
						System.out.println("");
						voteTally = stub.tally();
						for (String i : voteTally.keySet()) {
							System.out.println("Name: " + i + " > Votes: " + voteTally.get(i));
						}
						System.out.println("_____________________");
						break;
					case 5:
						System.out.println(stub.Winner());
						break;
					case 6:
						System.out.print("Enter username(root): ");
						String user = s.next();
						System.out.print("Enter Password(root): ");
						String password = s.next();
						if (!"root".equals(user) && !"root".equals(password)){
							System.out.println("Auth denied");
							break;
						}
						System.out.println("\nAuth successfull");
						System.out.println("\n");
						int secondFlag=0;
						ArrayList<Float> stats = new ArrayList<>();
						while (secondFlag == 0) {
							System.out.println("The choices for Supervisor:");
							System.out.println("1. Add candidate");
							System.out.println("2. Remove a candidate");
							System.out.println("3. Invalid a candidate");
							System.out.println("4. Calculate statistics");
							System.out.println("5. Quit and return to main menu");
							System.out.print("\nEnter your choice: ");
							int superChoice = s.nextInt();
							String candName = "";
							switch (superChoice) {
								case 1:
									System.out.print("\nName of the candidate to add: ");
									candName = s.next();
									System.out.println("\nUpdated list of candidates: ");
									for (String i : stub.addCandidate(candName)) {
										System.out.println("> " + i);
									}
									System.out.println();
									break;
								case 2:
									System.out.print("\nName of the candidate to remove: ");
									candName = s.next();
									System.out.println("\nUpdated list of candidates: ");
									for (String i : stub.removeCandidate(candName)) {
										System.out.println("> " + i);
									}
									System.out.println("\nSuccessfull!!!");
									break;
								case 3:
									System.out.print("\nName of the candidate to invalidate: ");
									candName = s.next();
									invalidCandArray.add(candName);
									stub.invalidCand(candName);
									System.out.print("\n"+candName+" is ineligible now!");
									break;
								case 4:
									stats = stub.statistics();
									System.out.println("Statistics of the Election - \n");
									System.out.println("Total percentage of registered voters voted: " + stats.get(0));
									System.out.println("Total percentage of Male voted: " + stats.get(1));
									System.out.println("Total percentage of Female voted: " + stats.get(2));
									ArrayList<String> candidates = stub.Candidates();
									for(int i=3;i<stats.size();i++){
										System.out.println("Percentage of votes for "+ candidates.get(i-3) +": " + stats.get(i));
									}
									break;
								case 5:
									secondFlag=1;
									break;
								default:
									break;
							}
							System.out.print("\nEnter [Y or y] to confirm: ");
							s.next();
							System.out.println("\n");
						}
						
						break;
					case 0:
						flag++;
						break;
					default:
						break;
				}
				System.out.print("Enter [Y or y] to confirm: ");
				s.next();
				System.out.println("\n");
			}

			s.close();
		} catch (Exception ex) {
			System.err.println("Client exception: " + ex.toString());
			ex.printStackTrace();
		}
	}
}