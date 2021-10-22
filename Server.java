
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.awt.List;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.Scanner;


public class Server implements RmiInterface {
	// generating hashmap
	public static HashMap<Integer, String> nameGender = new HashMap<Integer, String>(); 

    public static HashMap<String, Integer> voteCounter = new HashMap<String,Integer>(); 

	public static ArrayList<Integer> VoterID= new ArrayList<Integer>();
	public static ArrayList<String> Voter= new ArrayList<String>();

	public static ArrayList<String> Vote= new ArrayList<String>();
    public static ArrayList<String> Gender= new ArrayList<String>();
    
	public static ArrayList<String> candidateList = new ArrayList<String>();

	public static ArrayList<String> invalidCandArray = new ArrayList<String>();



    
 
    public int registerVoter(String name, String gender) throws RemoteException 
	{
		
       	Voter.add(name);
        int x = UUIDgen(); 
    	VoterID.add(x);
		Vote.add(null);
        Gender.add(gender);

		System.out.println("_____________________");
		System.out.println("Registered Voters are >");
		for (int i=0;i<Voter.size();i++)
        {
            System.out.print(">");
    		System.out.print(Voter.get(i));
    		System.out.print(":");
    		System.out.print(VoterID.get(i));
            System.out.println("");
		}		

	System.out.println(" ");	
        return x;
    }
	
    public ArrayList<String> Candidates() throws RemoteException{	
		return candidateList;
	}   
    
    public ArrayList<String> addCandidate(String candName) throws RemoteException{
        candidateList.add(candName);
        voteCounter.put(candName, 0);
        return Candidates();
    }

    public ArrayList<String> removeCandidate(String candName) throws RemoteException{
        voteCounter.remove(candName);
        candidateList.remove(candName);
        return Candidates();
    }

    public int  UUIDgen() 
    {       

        Random rand =new Random();
        int uuid = rand.nextInt(500000-100000);
        if (VoterID.indexOf(uuid)>-1){
            uuid += rand.nextInt(600000-500000);
        }
        return uuid;
	}

    public String VerifyVoter(int id) 
    {	
			int index = VoterID.indexOf(id);
    		if (index != -1){
                return Voter.get(index);
            }
			return "Voter verification failed XX";
    }

    public ArrayList<Float> statistics(){
        ArrayList<Float> stats = new ArrayList<Float>();
        // ArrayList<Integer> eachCandidate = new ArrayList<Integer>();
        float totalUsersVoted = 0;
        float genderMale = 0;
        float genderFemale = 0;
        float totalVotes = 0;
        
        for(int i=0; i<Vote.size(); i++){
            if (Vote.get(i) != null){
                totalUsersVoted += 1;
                if("M".equals(Gender.get(i))){
                    genderMale+=1;
                }
                else{
                    genderFemale+=1;
                }
            }
        }
        totalVotes = (float) (totalUsersVoted/Vote.size())*100;
        genderMale = (float) (genderMale/totalUsersVoted)*100;
        genderFemale = (float) (genderFemale/totalUsersVoted)*100;
        stats.add(totalVotes);
        stats.add(genderMale);
        stats.add(genderFemale);

        for(int i=0; i<candidateList.size(); i++){
            Float tempPercentage = (float) ((voteCounter.get(candidateList.get(i))/totalUsersVoted)*100);
            stats.add(tempPercentage);
        }

        return stats;
    }

	public String Vote(int id,int candidateIndex)
 	{
		int index = VoterID.indexOf(id);
    	if (index == -1){
				return "Voter verification failed XX";
		}
		if(Vote.get(index) != null){
			return "You already voted";
		}

		String candidate = candidateList.get(candidateIndex);
		Vote.set(index, candidate);
		voteCounter.put(candidate, voteCounter.get(candidate) + 1);
		return "Vote successfully registered!";
 	} 

    public HashMap<String, Integer> tally() throws RemoteException{	
            return voteCounter;
        }   

    public void invalidCand(String candName) throws RemoteException{
        invalidCandArray.add(candName);
    }

    public String Winner()throws RemoteException
    {
        int maxVote = 0;
        ArrayList<String> WinnerList= new ArrayList<String>();
        String winner = candidateList.get(0);
        int count = 1;
        WinnerList.add(winner);
        for (HashMap.Entry<String, Integer> entry : voteCounter.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            if(value > maxVote){
                WinnerList = new ArrayList<String>();
                maxVote = value;
                winner = key;
                count = 1;
                WinnerList.add(winner);
            }
            else if(value == maxVote){
                count += 1;
                winner = key;
                WinnerList.add(winner);
            }
            else{

            }
        }

        if(maxVote == 0){
            return "No one has voted yet!! No Data to show";
        }

        if(count == 1){
            return winner + " has won the election and got " + maxVote + " votes!!";
        }
        
        return "More than one candidate got same maximum number of votes. These candidates got same votes" + WinnerList.toString();

    }
        
    public static void main(String args[]) throws IOException {
        
        try {
        	String candName = "";

            Server server = new Server();

            // System.setProperty("java.rmi.server.hostname","192.168.1.2");

            RmiInterface stub = (RmiInterface) UnicastRemoteObject.exportObject(server, 0);

            Registry registry = LocateRegistry.createRegistry(2525);
            registry.bind("Election", stub);
            
            Scanner namesFile = new Scanner(new File("candidate-name.txt"));
            System.err.println("Server ready");
            
            System.out.println("\nThe voting system is now active!\n");
            while (namesFile.hasNext()) 
            {
              candName = namesFile.next();
              candidateList.add(candName);
            }
            namesFile.close();

            System.out.println("The list of Candidates are: ");
            System.out.println("_____________________");            
            

            // for (String i : voteCounter.keySet()) {
            //     System.out.println("key: " + i + " value: " + voteCounter.get(i));
            //     System.out.println("next");
            //   }
            
            
            for (String candidate : candidateList) 
            {
              System.out.println(candidate);
              voteCounter.put(candidate, 0);
            }
            
	    }
       	 catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}