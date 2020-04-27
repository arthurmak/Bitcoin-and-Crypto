// Example of a Simulation. This test runs the nodes on a random graph.
// At the end, it will print out the Transaction ids which each node
// believes consensus has been reached upon. You can use this simulation to
// test your nodes. You will want to try creating some deviant nodes and
// mixing them in the network to fully test.
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.HashMap;

public class SimulationDebug {

   public static void main(String[] args) {

      // There are four required command line arguments: p_graph (.1, .2, .3),
      // p_malicious (.15, .30, .45), p_txDistribution (.01, .05, .10),
      // and numRounds (10, 20). You should try to test your CompliantNode
      // code for all 3x3x3x2 = 54 combinations.

      final Boolean DEBUG = true;
      int k = 0;
      int t = 0;
      int l = 0;
      int m = 0;
      int n = 0;

      int numNodes = 100;
      double p_graph = Double.parseDouble(args[0]); // parameter for random graph: prob. that an edge will exist
      double p_malicious = Double.parseDouble(args[1]); // prob. that a node will be set to be malicious
      double p_txDistribution = Double.parseDouble(args[2]); // probability of assigning an initial transaction to each node
      int numRounds = Integer.parseInt(args[3]); // number of simulation rounds your nodes will run for

      // pick which nodes are malicious and which are compliant
      Node[] nodes = new Node[numNodes];
      if (DEBUG) {
        k = 0;
      }
      for (int i = 0; i < numNodes; i++) {
         if(Math.random() < p_malicious) {
            // When you are ready to try testing with malicious nodes, replace the
            // instantiation below with an instantiation of a MaliciousNode
            // nodes[i] = new MaliciousNode(p_graph, p_malicious, p_txDistribution, numRounds);
            nodes[i] = new MalDoNothing(p_graph, p_malicious, p_txDistribution, numRounds);
            k++;
         }
         else
            nodes[i] = new CompliantNode(p_graph, p_malicious, p_txDistribution, numRounds);
      }
      if (DEBUG) {

          System.out.println("Malicious:Compliant = " + k + ":" + (numNodes - k));
          //System.out.println("node[" + i + "][" + j +"]: " + followees[i][j]);
      }

      // initialize random follow graph
      boolean[][] followees = new boolean[numNodes][numNodes]; // followees[i][j] is true iff i follows j
      for (int i = 0; i < numNodes; i++) {
         for (int j = 0; j < numNodes; j++) {
            if (i == j) continue;
            if(Math.random() < p_graph) { // p_graph is .1, .2, or .3
               followees[i][j] = true;
            }
         }
      }

      if (DEBUG) {
        k = 0;
        for (int i = 0; i < numNodes; i++) {
            for (int j = 0; j < numNodes; j++) {
              if(i==j) continue;
              if(followees[i][j]) {
                k++;
                //System.out.println("node[" + i + "][" + j +"]: " + followees[i][j]);
                //System.out.println();
              }
            }
          }
          System.out.println("Number of edges: " + k);
          //System.out.println("node[" + i + "][" + j +"]: " + followees[i][j]);
      }

      // notify all nodes of their followees
      for (int i = 0; i < numNodes; i++)
         nodes[i].setFollowees(followees[i]);
/**


      if (DEBUG) {
           k = 0;
           t = 0;
           for (int i = 0; i < numNodes; i++) {
               k = 0;
               if(!nodes[i].followee == null) {
                   Boolean[] followeeList = nodes[i].followee;
                   for (Boolean fe : followeeList) {
                       if(fe) {
                      k++;
                   //System.out.println("node[" + i + "][" + j +"]: " + followees[i][j]);
               //System.out.println();
                  }
                }
              }
               System.out.println("No of followee of Node[" + i + "]: " + k);
            }

             //System.out.println("node[" + i + "][" + j +"]: " + followees[i][j]);
      }
**/
      // initialize a set of 500 valid Transactions with random ids
      int numTx = 500;

      HashSet<Integer> validTxIds = new HashSet<Integer>();
      Random random = new Random();
      for (int i = 0; i < numTx; i++) {
         int r = random.nextInt();
         validTxIds.add(r);
      }
      // distribute the 500 Transactions throughout the nodes, to initialize
      // the starting state of Transactions each node has heard. The distribution
      // is random with probability p_txDistribution for each Transaction-Node pair.

      if(DEBUG){
        k = 0;
        t = 0;
      }

      for (int i = 0; i < numNodes; i++) {
        if(DEBUG) {
          k = 0;
        }
         HashSet<Transaction> pendingTransactions = new HashSet<Transaction>();
         for(Integer txID : validTxIds) {

            if (Math.random() < p_txDistribution) {// p_txDistribution is .01, .05, or .10.
                pendingTransactions.add(new Transaction(txID));
                if(DEBUG) {
                  k++;
                }
             }
         }
         nodes[i].setPendingTransaction(pendingTransactions);
         if(DEBUG) {
           t = t+ k;
	   if (i == 0)
           System.out.println("Tx to node[" + i + "]:"  + k);

         }
      }


      if(DEBUG) {
        //System.out.println();
        System.out.println("Total number of Tx to all nodes:"  + t + " Average per node: " + t/numNodes);

      }


      // Simulate for numRounds times
      if (DEBUG) {
                k = 0;
                l = 0;
                t = 0;
         }

      for (int round = 0; round < numRounds; round++) { // numRounds is either 10 or 20
         // gather all the proposals into a map. The key is the index of the node receiving
         // proposals. The value is an ArrayList containing 1x2 Integer arrays. The first
         // element of each array is the id of the transaction being proposed and the second
         // element is the index # of the node proposing the transaction.
         HashMap<Integer, Set<Candidate>> allProposals = new HashMap<>();
         if (DEBUG) {
                k = 0;
                n = (int) ( numNodes * Math.random() );
                //t = 0;
         }
         for (int i = 0; i < numNodes; i++) {
            Set<Transaction> proposals = nodes[i].sendToFollowers();
            if (DEBUG)
                k = 0;
                l = 0;
            for (Transaction tx : proposals) {
               if (!validTxIds.contains(tx.id))
                  continue; // ensure that each tx is actually valid

               for (int j = 0; j < numNodes; j++) {
                  if(!followees[j][i]) continue; // tx only matters if j follows i
                     if(DEBUG) {
                        if (i == 0 )
                            k++;
                        if (i == n )
                            l++;
                     t++;
                     }
                  if (!allProposals.containsKey(j)) {
                	  Set<Candidate> candidates = new HashSet<>();
                	  allProposals.put(j, candidates);
                          
                  }

                  Candidate candidate = new Candidate(tx, i);
                  allProposals.get(j).add(candidate);
               }

            }
            if(DEBUG) {
                if (i == 0) 
                    System.out.println("Round " + round  + " Tx to node[" + i + "]: " + k);
                if ( i == n)
                    System.out.println("Round " + round  + " Tx to node[" + i + "]: " + l);
            }
         }

         // Distribute the Proposals to their intended recipients as Candidates
         for (int i = 0; i < numNodes; i++) {
            if (allProposals.containsKey(i))
               nodes[i].receiveFromFollowees(allProposals.get(i));
         }
         if(DEBUG) {
             
             System.out.println("Total number of Tx to all nodes: " + t);
         }   
      }
      // print results
      if(!DEBUG) {
      for (int i = 0; i < numNodes; i++) {
         Set<Transaction> transactions = nodes[i].sendToFollowers();
         System.out.println("Transaction ids that Node " + i + " believes consensus on:");
           for (Transaction tx : transactions)
              System.out.println(tx.id);
           System.out.println();
           System.out.println();
        }

     }
  }
}
