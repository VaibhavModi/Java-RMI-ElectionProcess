import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.*;


public interface RmiInterface extends Remote {
  
    void invalidCand(String candName) throws RemoteException;
  
    ArrayList<String> removeCandidate(String candName) throws RemoteException;

    ArrayList<Float> statistics() throws RemoteException;

    int registerVoter(String x, String gender) throws RemoteException;

	  ArrayList<String> Candidates() throws RemoteException;

    String VerifyVoter(int x) throws RemoteException;

    String Vote(int id,int candidateIndex) throws RemoteException;

    HashMap<String, Integer> tally() throws RemoteException;

    String Winner()throws RemoteException;

    ArrayList<String> addCandidate(String candName) throws RemoteException;
}