//import java.util.HashSet;
import java.util.*;



public class TxHandler {

    /**
     * Creates a public ledger whose current UTXOPool (collection of unspent transaction outputs) is
     * {@code utxoPool}. This should make a copy of utxoPool by using the UTXOPool(UTXOPool uPool)
     * constructor.
     */

    // Assume existing UTXOPool is provided

    private UTXOPool utxoPool;
    Crypto cc =  new Crypto();

    //cc = Crypto.getInstance();

    public TxHandler(UTXOPool utxoPool) {

        // IMPLEMENT THIS
        // Assume the existing
        this.utxoPool = new UTXOPool(utxoPool);
    }
    /**
     * @return true if:
     * (1) all outputs claimed by {@code tx} are in the current UTXO pool,
     * (2) the signatures on each input of {@code tx} are valid,
     * (3) no UTXO is claimed multiple times by {@code tx},
     * (4) all of {@code tx}s output values are non-negative, and
     * (5) the sum of {@code tx}s input values is greater than or equal to the sum of its output
     *     values; and false otherwise.
     */
    public boolean isValidTx(Transaction tx) {
        // IMPLEMENT THIS
	// Create HashSet of scaned UTXO
	HashSet<UTXO> scanedUtxo = new HashSet<UTXO>();

	// initialize the total value of inputs and outputs in the transaction
	double inputVal = 0.0;
	double outputVal = 0.0;
	int index = 0;

	for (Transaction.Input in : tx.getInputs() ) {
		UTXO ut = new UTXO(in.prevTxHash, in.outputIndex );
		// test whether current input in UTXO pool (1)
		if (!this.utxoPool.contains(ut)) {
			return false;
		}

		// accumulate input value
		double prevOutVal = utxoPool.getTxOutput(ut).value;
		inputVal += prevOutVal;

		// test double spent (3)
		if (scanedUtxo.contains(ut) ) {
			return false;
		}
		scanedUtxo.add(ut);

		// verify signature (2)
    		int outputNo = ut.getIndex();

		if (! Crypto.verifySignature ( utxoPool.getTxOutput(ut).address, tx.getRawDataToSign(index), in.signature ) ) {
			return false;
		}
		index++;

	}

	for (Transaction.Output out : tx.getOutputs() ) {
		// test non-negative value (4)
		if (out.value < 0.0 ) {
			return false;
		}
		outputVal += out.value;

	}

	//  check sum of input >= sum of outputs (5)
	if (outputVal > inputVal ) {
		return false;
	}

        return true;

    }

    /**
     * Handles each epoch by receiving an unordered array of proposed transactions, checking each
     * transaction for correctness, returning a mutually valid array of accepted transactions, and
     * updating the current UTXO pool as appropriate.
     * unordered array of proposed transactions as possibleTxs
     * 1. return only valid transactions
     * 2. one transaction input may depend on an output of another transaction in same epoch
     * 3. update UTXO pool - add new output and remove spent output
     * 4. return mutually valid transaction set
    */
    public Transaction[] handleTxs(Transaction[] possibleTxs) {

    	/* Algorithm
         * 1. check validity - multiple pass - validity of input may depend on an output of another tx in same epoch
    	 *    for valid txs - updatePool
    	 *                    	remove input from utxoPool
    	 *                    	add outputs to utxoPool
    	 *
    	*/
    	// HashSet of transaction pool to be handled
    	HashSet<Transaction> txs = new HashSet<Transaction>(Arrays.asList(possibleTxs));

    	int txCount = 0;
    	ArrayList<Transaction> valid = new ArrayList<Transaction>();

    	do {
    		txCount = txs.size();
    		// tx set to be removed
    		HashSet<Transaction> toRemove = new HashSet<Transaction>();
    		for (Transaction tx : txs) {
    			if(!isValidTx(tx)) {
    				continue;
    			}
    			valid.add(tx);
    			updatePool(tx);
    			toRemove.add(tx);

    		}
    		for (Transaction tx : toRemove) {
    			txs.remove(tx);

    		}


    	} while (txCount != txs.size() && txCount != 0);

    	return valid.toArray(new Transaction[valid.size()]);

    }

    private void updatePool(Transaction tx) {
    	for(Transaction.Input input : tx.getInputs()) {
    		UTXO utxo = new UTXO(input.prevTxHash, input.outputIndex);
    		this.utxoPool.removeUTXO(utxo);


    	}

    	byte[] txHash = tx.getHash();
    	int index = 0;
    	for (Transaction.Output output: tx.getOutputs()) {
    		UTXO utxo = new UTXO(txHash, index);
    		index++;
    		this.utxoPool.addUTXO(utxo, output);

    	}


    }



}
