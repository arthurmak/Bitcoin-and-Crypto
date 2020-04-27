

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class CompliantNode implements Node {

    double p_graph, p_malicious, p_txDistribution;
    int numRounds;
    //public ArrayList<Boolean> followee;
    HashSet<Integer> followee;
    Set<Transaction> pendingTransactions;
    final Boolean DEBUG = false;
         int k = 0;
         int t = 0;
    // constructor
    public CompliantNode(double p_graph, double p_malicious, double p_txDistribution, int numRounds) {
        this.p_graph = p_graph;
        this.p_malicious = p_malicious;
        this.p_txDistribution = p_txDistribution;
        this.numRounds = numRounds;
	      this.followee = new HashSet<Integer>();
    }

    // setter for followees
    public void setFollowees(boolean[] followees) {
      int s = followees.length;
      if (DEBUG) {
        k = 0;
      }
      for(int i = 0; i < s; i++) {
	if (followees[i]) {
          this.followee.add(i);
          if (DEBUG)
            k++;
        }
      }
      if (DEBUG) {
         System.out.println("Node has " + k + " followees");
      }
    }

    // setter for pendingTransactions
    public void setPendingTransaction(Set<Transaction> pendingTransactions) {
        this.pendingTransactions = pendingTransactions;
    }

    // send pendingTransactions to followers
    public Set<Transaction> sendToFollowers() {
        Set<Transaction> toSend = new HashSet<>(pendingTransactions);
        pendingTransactions.clear();
        return toSend;
    }

    // collect transactions from Followers
    public void receiveFromFollowees(Set<Candidate> candidates) {
        for (Candidate candidate : candidates) {
            pendingTransactions.add(candidate.tx);
        }
    }
}
