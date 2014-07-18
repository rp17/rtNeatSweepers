package rtNEAT;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Random;

//#ifndef _NERO_NEAT_H_
//#define _NERO_NEAT_H_
//
//#include <cstdlib>
//#include <cstring>
//
//namespace NEAT {
public class Neat{
//
//	extern int time_alive_minimum; // Minimum time alive to be considered for selection or death in real-time evolution
	public static int time_alive_minimum;
//	const int num_trait_params = 8;
	final static int NUM_TRAIT_PARAMS = 8;
//
//	extern double trait_param_mut_prob;
	public static double trait_param_mut_prob = 0;
//	extern double trait_mutation_power; // Power of mutation on a signle trait param 
	public static double trait_mutation_power = 0;
//	extern double linktrait_mut_sig;  // Amount that mutation_num changes for a trait change inside a link
	public static double linktrait_mut_sig = 0;
//	extern double nodetrait_mut_sig; // Amount a mutation_num changes on a link connecting a node that changed its trait
	public static double nodetrait_mut_sig = 0;
//	extern double weight_mut_power;  // The power of a linkweight mutation
	//^This bad boy is 'weigh_mut_power' in the param file
	public static double weigh_mut_power = 0;
//	extern double recur_prob;        // Prob. that a link mutation which doesn't have to be recurrent will be made recurrent
	public static double recur_prob = 0;
//
//	// These 3 global coefficients are used to determine the formula for
//	// computating the compatibility between 2 genomes.  The formula is:
//	// disjoint_coeff*pdg+excess_coeff*peg+mutdiff_coeff*mdmg.
//	// See the compatibility method in the Genome class for more info
//	// They can be thought of as the importance of disjoint Genes,
//	// excess Genes, and parametric difference between Genes of the
//	// same function, respectively. 
//	extern double disjoint_coeff;
	public static double disjoint_coeff = 0;
//	extern double excess_coeff;
	public static double excess_coeff = 0;
//	extern double mutdiff_coeff;
	public static double mutdiff_coeff = 0;
//
//	// This global tells compatibility threshold under which two Genomes are considered the same species 
//	extern double compat_threshold;
	public static double compat_thresh = 0;
//
//	// Globals involved in the epoch cycle - mating, reproduction, etc.. 
//	extern double age_significance;          // How much does age matter?
	public static double age_significance = 0;
//	extern double survival_thresh;           // Percent of ave fitness for survival
	public static double survival_thresh = 0;
//	extern double mutate_only_prob;          // Prob. of a non-mating reproduction
	public static double mutate_only_prob = 0;
//	extern double mutate_random_trait_prob;
	public static double mutate_random_trait_prob = 0;
//	extern double mutate_link_trait_prob;
	public static double mutate_link_trait_prob = 0;
//	extern double mutate_node_trait_prob;
	public static double mutate_node_trait_prob = 0;
//	extern double mutate_link_weights_prob;
	public static double mutate_link_weights_prob = 0;
//	extern double mutate_toggle_enable_prob;
	public static double mutate_toggle_enable_prob = 0;
//	extern double mutate_gene_reenable_prob;
	public static double mutate_gene_reenable_prob = 0;
//	extern double mutate_add_node_prob;
	public static double mutate_add_node_prob = 0;
//	extern double mutate_add_link_prob;
	public static double mutate_add_link_prob = 0;
//	extern double interspecies_mate_rate;    // Prob. of a mate being outside species
	public static double interspecies_mate_rate = 0;
//	extern double mate_multipoint_prob;
	public static double mate_multipoint_prob = 0;
//	extern double mate_multipoint_avg_prob;
	public static double mate_multipoint_avg_prob = 0;
//	extern double mate_singlepoint_prob;
	public static double mate_singlepoint_prob = 0;
//	extern double mate_only_prob;            // Prob. of mating without mutation
	public static double mate_only_prob = 0;
//	extern double recur_only_prob;  // Probability of forcing selection of ONLY links that are naturally recurrent
	public static double recur_only_prob = 0;
//	extern int pop_size;  // Size of population
	public static int pop_size = 0;
//	extern int dropoff_age;  // Age where Species starts to be penalized
	public static int dropoff_age = 0;
//	extern int newlink_tries;  // Number of tries mutate_add_link will attempt to find an open link
	public static int newlink_tries = 0;
//	extern int print_every; // Tells to print population to file every n generations
	public static int print_every = 0;
//	extern int babies_stolen; // The number of babies to siphon off to the champions 
	public static int babies_stolen = 0;
//	extern int num_runs; //number of times to run experiment
	public static int num_runs = 0;
//
//	//extern MRandomR250 NEATRandGen; // Random number generator; can pass seed value as argument
	private static Random NEATRandGen = new Random();
//
//	//const char *getUnit(const char *string, int index, const char *set);
//	String getUnit(String string, int index, String set){
//		removed in original code. Don't know why.
//	}
//	//const char *getUnits(const char *string, int startIndex, int endIndex, const char *set);
//	String getUnits(String string, int startIndex, String set){
//		removed in original code. Don't know why.
//	}
//	int getUnitCount(const char *string, const char *set);
	public static int getUnitCount(String string, String set){
		int count = 0;
		for (int i = 0; i < string.length(); ++i){
			if (set.contains(string.subSequence(i, i))){
				count++;
				continue;
			}
		}
		
		return count;
	}
//
//	// Inline Random Functions 
//	extern inline int randposneg() {
//        if (rand()%2) 
//            return 1; 
//        else 
//            return -1;
//    }
	public static int randposneg(){
		if(NEATRandGen.nextInt() %2 == 1 ){
			return 1;
		}
		else {
			return -1;
		}
	}
//    
//	extern inline int randint(int x,int y) {
//        return rand()%(y-x+1)+x;
//    }
	public static int randint(int x, int y){
		return NEATRandGen.nextInt()%(y-x+1)+x;
	}
//
//    extern inline double randfloat() {
//        return rand() / (double) RAND_MAX;        
//    }
	public static double randfloat(){
		return NEATRandGen.nextFloat();
	}
//
//
//	// SIGMOID FUNCTION ********************************
//	// This is a signmoidal activation function, which is an S-shaped squashing function
//	// It smoothly limits the amplitude of the output of a neuron to between 0 and 1
//	// It is a helper to the neural-activation function get_active_out
//	// It is made inline so it can execute quickly since it is at every non-sensor 
//	// node in a network.
//	// NOTE:  In order to make node insertion in the middle of a link possible,
//	// the signmoid can be shifted to the right and more steeply sloped:
//	// slope=4.924273
//	// constant= 2.4621365
//	// These parameters optimize mean squared error between the old output,
//	// and an output of a node inserted in the middle of a link between
//	// the old output and some other node. 
//	// When not right-shifted, the steepened slope is closest to a linear
//	// ascent as possible between -0.5 and 0.5
//	extern double fsigmoid(double,double,double);
	public static double fsigmoid(double activesum,double slope,double constant){
		return (1/(1+(Math.exp(-(slope*activesum))))); //Compressed
	}
//
//	// Hebbian Adaptation Function
//	// Based on equations in Floreano & Urzelai 2000
//	// Takes the current weight, the maximum weight in the containing network,
//	// the activation coming in and out of the synapse,
//	// and three learning rates for hebbian, presynaptic, and postsynaptic
//	// modification
//	// Returns the new modified weight
//	// NOTE: For an inhibatory connection, it makes sense to
//	//      emphasize decorrelation on hebbian learning!
//	extern double oldhebbian(double weight, double maxweight, double active_in, double active_out, double hebb_rate, double pre_rate, double post_rate);
//
	public static double oldhebbian(double weight, double maxweight, double active_in, double active_out, double hebb_rate, double pre_rate, double post_rate) {

		boolean neg=false;
		double delta;

		//double weight_mag;

		if (maxweight<5.0) maxweight=5.0;

		if (weight>maxweight) weight=maxweight;

		if (weight<-maxweight) weight=-maxweight;

		if (weight<0) {
			neg=true;
			weight=-weight;
		}

		//if (weight<0) {
		//  weight_mag=-weight;
		//}
		//else weight_mag=weight;
		//^ this code was removed in the original, not by me

		if (!(neg)) {
			//if (true) {
			delta=
				hebb_rate*(maxweight-weight)*active_in*active_out+
				pre_rate*(weight)*active_in*(active_out-1.0)+
				post_rate*(weight)*(active_in-1.0)*active_out;

			//delta=delta-hebb_rate/2; //decay
			//removed in original

			//delta=delta+randposneg()*randfloat()*0.01; //noise
			//removed in original

			//cout<<"delta: "<<delta<<endl;
			//removed in original

			if (weight+delta>0)
				return weight+delta;
			//else return 0.01;

			//return weight+delta;

		}
		else {
			//In the inhibatory case, we strengthen the synapse when output is low and
			//input is high
			delta=
				hebb_rate*(maxweight-weight)*active_in*(1.0-active_out)+ //"unhebb"
				//hebb_rate*(maxweight-weight)*(1.0-active_in)*(active_out)+
				-5*hebb_rate*(weight)*active_in*active_out+ //anti-hebbian
				//hebb_rate*(maxweight-weight)*active_in*active_out+
				//pre_rate*weight*active_in*(active_out-1.0)+
				//post_rate*weight*(active_in-1.0)*active_out;
				0;

			//delta=delta-hebb_rate; //decay
			//removed in original
			
			//delta=delta+randposneg()*randfloat()*0.01; //noise
			//removed in original
			
			if (-(weight+delta)<0)
				return -(weight+delta);
			else return -0.01;

			//return -(weight+delta); //Unreachable??? Is this a mistake?

		}

		return 0;

	}
//	// Hebbian Adaptation Function
//	// Based on equations in Floreano & Urzelai 2000
//	// Takes the current weight, the maximum weight in the containing network,
//	// the activation coming in and out of the synapse,
//	// and three learning rates for hebbian, presynaptic, and postsynaptic
//	// modification
//	// Returns the new modified weight
//	// NOTE: For an inhibatory connection, it makes sense to
//	//      emphasize decorrelation on hebbian learning!	
//	extern double hebbian(double weight, double maxweight, double active_in, double active_out, double hebb_rate, double pre_rate, double post_rate);
	public static double hebbian(double weight, double maxweight, double active_in, double active_out, double hebb_rate, double pre_rate, double post_rate){
		boolean neg=false;
		double delta;

		//double weight_mag;
		//removed in original

		double topweight;

		if (maxweight<5.0) maxweight=5.0;

		if (weight>maxweight) weight=maxweight;

		if (weight<-maxweight) weight=-maxweight;

		if (weight<0) {
			neg=true;
			weight=-weight;
		}


		//if (weight<0) {
		//  weight_mag=-weight;
		//}
		//else weight_mag=weight;
		//removed in original


		topweight=weight+2.0;
		if (topweight>maxweight) topweight=maxweight;

		if (!(neg)) {
			//if (true) {
			delta=
				hebb_rate*(maxweight-weight)*active_in*active_out+
				pre_rate*(topweight)*active_in*(active_out-1.0);
			//post_rate*(weight+1.0)*(active_in-1.0)*active_out;
			//removed in original

			//delta=delta-hebb_rate/2; //decay
			//removed in original

			//delta=delta+randposneg()*randfloat()*0.01; //noise
			//removed in original

			//cout<<"delta: "<<delta<<endl;
			//removed in original

			//if (weight+delta>0)
			//  return weight+delta;
			//else return 0.01;
			//removed in original

			return weight+delta;

		}
		else {
			//In the inhibatory case, we strengthen the synapse when output is low and
			//input is high
			delta=
				pre_rate*(maxweight-weight)*active_in*(1.0-active_out)+ //"unhebb"
				//hebb_rate*(maxweight-weight)*(1.0-active_in)*(active_out)+
				-hebb_rate*(topweight+2.0)*active_in*active_out+ //anti-hebbian
				//hebb_rate*(maxweight-weight)*active_in*active_out+
				//pre_rate*weight*active_in*(active_out-1.0)+
				//post_rate*weight*(active_in-1.0)*active_out;
				//removed in original
				0;

			//delta=delta-hebb_rate; //decay
			//removed in original

			//delta=delta+randposneg()*randfloat()*0.01; //noise
			//removed in original

			//if (-(weight+delta)<0)
			//  return -(weight+delta);
			//  else return -0.01;
			//removed in original

			return -(weight+delta);

		}
	}
//
//	// Returns a normally distributed deviate with 0 mean and unit variance
//	// Algorithm is from Numerical Recipes in C, Second Edition
//	extern double gaussrand();
	public static double gaussrand(){
		int iset=0;
		double gset = 0;
		double fac,rsq,v1,v2;

		if (iset==0) {
			do {
				v1=2.0*(randfloat())-1.0;
				v2=2.0*(randfloat())-1.0;
				rsq=v1*v1+v2*v2;
			} while (rsq>=1.0 || rsq==0.0);
			fac=Math.sqrt(-2.0*Math.log(rsq)/rsq);
			gset=v1*fac;
			iset=1;
			return v2*fac;
		}
		else {
			iset=0;
			return gset;
		}
	}
//
//	//This is an incorrect gassian distribution...but it is faster than gaussrand (maybe it's good enough?)
//	//inline double gaussrand_wrong() {return (randposneg())*(sqrt(-log((rand()*1.0)/RAND_MAX)));}   
//
//	bool load_neat_params(const char *filename, bool output = false);
	public static boolean load_neat_params(String filename, boolean output){
//		std::ifstream paramFile(filename);
		try{
			InputStream is = new FileInputStream(filename);
			BufferedReader paramFile = new BufferedReader(new InputStreamReader(is));
//
//		if(!paramFile) {
//			return false;
//		}
		//covered by try-catch;
		
//		char curword[128];
//		//char delimiters[] = " \n"; // tab = bad, CR(int 13) = bad in the file
//		//char delimiters[] = " \t\n";
//		//char delimiters[] = {' ', '\n', (char)13};
//		//int curwordnum = 1;
//		//char filestring[1000000];
//		//paramFile.read(sizeof(filestring), filestring);
//
//		// **********LOAD IN PARAMETERS*************** //
//	    if(output)
//		    printf("NEAT READING IN %s", filename);
		if(output)
			System.out.println("NEAT READING " + filename);
		String currentLine;
		String[] splitLine = new String[2];
		
		while ((currentLine = paramFile.readLine()) != null){
			splitLine = currentLine.split(" ", 1);
			if (splitLine[0] == "trait_param_mut_prob"){
				trait_param_mut_prob = Integer.parseInt(splitLine[1]);
				System.out.println(splitLine[0] + " set to " + splitLine[1]);
			}
			else if (splitLine[0] == "trait_mutation_power"){
				trait_mutation_power = Integer.parseInt(splitLine[1]);
				System.out.println(splitLine[0] + " set to " + splitLine[1]);
			}
			else if (splitLine[0] == "linktrait_mut_sig"){
				linktrait_mut_sig = Integer.parseInt(splitLine[1]);
				System.out.println(splitLine[0] + " set to " + splitLine[1]);
			}
			else if (splitLine[0] == "nodetrait_mut_sig"){
				nodetrait_mut_sig = Integer.parseInt(splitLine[1]);
				System.out.println(splitLine[0] + " set to " + splitLine[1]);
			}
			else if (splitLine[0] == "weigh_mut_power"){
				weigh_mut_power = Integer.parseInt(splitLine[1]);
				System.out.println(splitLine[0] + " set to " + splitLine[1]);
			}
			else if (splitLine[0] == "recur_prob"){
				recur_prob = Integer.parseInt(splitLine[1]);
				System.out.println(splitLine[0] + " set to " + splitLine[1]);
			}
			else if (splitLine[0] == "disjoint_coeff"){
				disjoint_coeff = Integer.parseInt(splitLine[1]);
				System.out.println(splitLine[0] + " set to " + splitLine[1]);
			}
			else if (splitLine[0] == "excess_coeff"){
				excess_coeff = Integer.parseInt(splitLine[1]);
				System.out.println(splitLine[0] + " set to " + splitLine[1]);
			}
			else if (splitLine[0] == "mutdiff_coeff"){
				mutdiff_coeff = Integer.parseInt(splitLine[1]);
				System.out.println(splitLine[0] + " set to " + splitLine[1]);
			}
			else if (splitLine[0] == "compat_thresh"){
				compat_thresh = Integer.parseInt(splitLine[1]);
				System.out.println(splitLine[0] + " set to " + splitLine[1]);
			}
			else if (splitLine[0] == "age_significance"){
				age_significance = Integer.parseInt(splitLine[1]);
				System.out.println(splitLine[0] + " set to " + splitLine[1]);
			}
			else if (splitLine[0] == "survival_thresh"){
				survival_thresh = Integer.parseInt(splitLine[1]);
				System.out.println(splitLine[0] + " set to " + splitLine[1]);
			}
			else if (splitLine[0] == "mutate_only_prob"){
				mutate_only_prob = Integer.parseInt(splitLine[1]);
				System.out.println(splitLine[0] + " set to " + splitLine[1]);
			}
			else if (splitLine[0] == "mutate_random_trait_prob"){
				mutate_random_trait_prob = Integer.parseInt(splitLine[1]);
				System.out.println(splitLine[0] + " set to " + splitLine[1]);
			}
			else if (splitLine[0] == "mutate_link_trait_prob"){
				mutate_link_trait_prob = Integer.parseInt(splitLine[1]);
				System.out.println(splitLine[0] + " set to " + splitLine[1]);
			}
			else if (splitLine[0] == "mutate_node_trait_prob"){
				mutate_node_trait_prob = Integer.parseInt(splitLine[1]);
				System.out.println(splitLine[0] + " set to " + splitLine[1]);
			}
			else if (splitLine[0] == "mutate_link_weights_prob"){
				mutate_link_weights_prob = Integer.parseInt(splitLine[1]);
				System.out.println(splitLine[0] + " set to " + splitLine[1]);
			}
			else if (splitLine[0] == "mutate_toggle_enable_prob"){
				mutate_toggle_enable_prob = Integer.parseInt(splitLine[1]);
				System.out.println(splitLine[0] + " set to " + splitLine[1]);
			}
			else if (splitLine[0] == "mutate_gene_reenable_prob"){
				mutate_gene_reenable_prob = Integer.parseInt(splitLine[1]);
				System.out.println(splitLine[0] + " set to " + splitLine[1]);
			}
			else if (splitLine[0] == "mutate_add_node_prob"){
				mutate_add_node_prob = Integer.parseInt(splitLine[1]);
				System.out.println(splitLine[0] + " set to " + splitLine[1]);
			}
			else if (splitLine[0] == "mutate_add_link_prob"){
				mutate_add_link_prob = Integer.parseInt(splitLine[1]);
				System.out.println(splitLine[0] + " set to " + splitLine[1]);
			}
			else if (splitLine[0] == "interspecies_mate_rate"){
				interspecies_mate_rate = Integer.parseInt(splitLine[1]);
				System.out.println(splitLine[0] + " set to " + splitLine[1]);
			}
			else if (splitLine[0] == "mate_multipoint_prob"){
				mate_multipoint_prob = Integer.parseInt(splitLine[1]);
				System.out.println(splitLine[0] + " set to " + splitLine[1]);
			}
			else if (splitLine[0] == "mate_multipoint_avg_prob"){
				mate_multipoint_avg_prob = Integer.parseInt(splitLine[1]);
				System.out.println(splitLine[0] + " set to " + splitLine[1]);
			}
			else if (splitLine[0] == "mate_singlepoint_prob"){
				mate_singlepoint_prob = Integer.parseInt(splitLine[1]);
				System.out.println(splitLine[0] + " set to " + splitLine[1]);
			}
			else if (splitLine[0] == "mate_only_prob"){
				mate_only_prob = Integer.parseInt(splitLine[1]);
				System.out.println(splitLine[0] + " set to " + splitLine[1]);
			}
			else if (splitLine[0] == "recur_only_prob"){
				recur_only_prob = Integer.parseInt(splitLine[1]);
				System.out.println(splitLine[0] + " set to " + splitLine[1]);
			}
			else if (splitLine[0] == "pop_size"){
				pop_size = Integer.parseInt(splitLine[1]);
				System.out.println(splitLine[0] + " set to " + splitLine[1]);
			}
			else if (splitLine[0] == "dropoff_age"){
				dropoff_age = Integer.parseInt(splitLine[1]);
				System.out.println(splitLine[0] + " set to " + splitLine[1]);
			}
			else if (splitLine[0] == "newlink_tries"){
				newlink_tries = Integer.parseInt(splitLine[1]);
				System.out.println(splitLine[0] + " set to " + splitLine[1]);
			}
			else if (splitLine[0] == "print_every"){
				print_every = Integer.parseInt(splitLine[1]);
				System.out.println(splitLine[0] + " set to " + splitLine[1]);
			}
			else if (splitLine[0] == "babies_stolen"){
				babies_stolen = Integer.parseInt(splitLine[1]);
				System.out.println(splitLine[0] + " set to " + splitLine[1]);
			}
			else {
				System.out.println("Unexpected trait found in param file.");
			}
		} // end while loop
		paramFile.close();
//
//		paramFile>>curword;
//		paramFile>>NEAT::trait_param_mut_prob;
//
//		//strcpy(curword, getUnit(filestring, curwordnum, delimiters));
		
//		//NEAT::trait_param_mut_prob = atof(curword);
//		//curwordnum += 2;
//
//		paramFile>>curword;
//		paramFile>>NEAT::trait_mutation_power;
//
//		//strcpy(curword, getUnit(filestring, curwordnum, delimiters));
//		//NEAT::trait_mutation_power = atof(curword);
//		//curwordnum += 2;
//
//		paramFile>>curword;
//		paramFile>>NEAT::linktrait_mut_sig;
//
//		//strcpy(curword, getUnit(filestring, curwordnum, delimiters));
//		//NEAT::linktrait_mut_sig = atof(curword);
//		//curwordnum += 2;
//
//		paramFile>>curword;
//		paramFile>>NEAT::nodetrait_mut_sig;
//		
//	    //strcpy(curword, getUnit(filestring, curwordnum, delimiters));
//		//NEAT::nodetrait_mut_sig = atof(curword);
//		//curwordnum += 2;
//		
//	    paramFile>>curword;
//		paramFile>>NEAT::weight_mut_power;
//		
//	    //strcpy(curword, getUnit(filestring, curwordnum, delimiters));
//		//NEAT::weight_mut_power = atof(curword);
//		//curwordnum += 2;
//		
//	    paramFile>>curword;
//		paramFile>>NEAT::recur_prob;
//		
//	    //strcpy(curword, getUnit(filestring, curwordnum, delimiters));
//		//NEAT::recur_prob = atof(curword);
//		//curwordnum += 2;
//		
//	    paramFile>>curword;
//		paramFile>>NEAT::disjoint_coeff;
//		
//	    //strcpy(curword, getUnit(filestring, curwordnum, delimiters));
//		//NEAT::disjoint_coeff = atof(curword);
//		//curwordnum += 2;
//		
//	    paramFile>>curword;
//		paramFile>>NEAT::excess_coeff;
//		
//	    //strcpy(curword, getUnit(filestring, curwordnum, delimiters));
//		//NEAT::excess_coeff = atof(curword);
//		//curwordnum += 2;
//		
//	    paramFile>>curword;
//		paramFile>>NEAT::mutdiff_coeff;
//		
//	    //strcpy(curword, getUnit(filestring, curwordnum, delimiters));
//		//NEAT::mutdiff_coeff = atof(curword);
//		//curwordnum += 2;
//		
//	    paramFile>>curword;
//		paramFile>>NEAT::compat_threshold;
//		
//	    //strcpy(curword, getUnit(filestring, curwordnum, delimiters));
//		//NEAT::compat_threshold = atof(curword);
//		//curwordnum += 2;
//		
//	    paramFile>>curword;
//		paramFile>>NEAT::age_significance;
//		
//	    //strcpy(curword, getUnit(filestring, curwordnum, delimiters));
//		//NEAT::age_significance = atof(curword);
//		//curwordnum += 2;
//		
//	    paramFile>>curword;
//		paramFile>>NEAT::survival_thresh;
//		
//	    //strcpy(curword, getUnit(filestring, curwordnum, delimiters));
//		//NEAT::survival_thresh = atof(curword);
//		//curwordnum += 2;
//		
//	    paramFile>>curword;
//		paramFile>>NEAT::mutate_only_prob;
//		
//	    //strcpy(curword, getUnit(filestring, curwordnum, delimiters));
//		//NEAT::mutate_only_prob = atof(curword);
//		//curwordnum += 2;
//		
//	    paramFile>>curword;
//		paramFile>>NEAT::mutate_random_trait_prob;
//		
//	    //strcpy(curword, getUnit(filestring, curwordnum, delimiters));
//		//NEAT::mutate_random_trait_prob = atof(curword);
//		//curwordnum += 2;
//		
//	    paramFile>>curword;
//		paramFile>>NEAT::mutate_link_trait_prob;
//		
//	    //strcpy(curword, getUnit(filestring, curwordnum, delimiters));
//		//NEAT::mutate_link_trait_prob = atof(curword);
//		//curwordnum += 2;
//		
//	    paramFile>>curword;
//		paramFile>>NEAT::mutate_node_trait_prob;
//		
//	    //strcpy(curword, getUnit(filestring, curwordnum, delimiters));
//		//NEAT::mutate_node_trait_prob = atof(curword);
//		//curwordnum += 2;
//		
//	    paramFile>>curword;
//		paramFile>>NEAT::mutate_link_weights_prob;
//		
//	    //strcpy(curword, getUnit(filestring, curwordnum, delimiters));
//		//NEAT::mutate_link_weights_prob = atof(curword);
//		//curwordnum += 2;
//		
//	    paramFile>>curword;
//		paramFile>>NEAT::mutate_toggle_enable_prob;
//		
//	    //strcpy(curword, getUnit(filestring, curwordnum, delimiters));
//		//NEAT::mutate_toggle_enable_prob = atof(curword);
//		//curwordnum += 2;
//
//		paramFile>>curword;
//		paramFile>>NEAT::mutate_gene_reenable_prob;
//		
//	    //strcpy(curword, getUnit(filestring, curwordnum, delimiters));
//		//NEAT::mutate_gene_reenable_prob = atof(curword);
//		//curwordnum += 2;
//		
//	    paramFile>>curword;
//		paramFile>>NEAT::mutate_add_node_prob;
//		
//	    //strcpy(curword, getUnit(filestring, curwordnum, delimiters));
//		//NEAT::mutate_add_node_prob = atof(curword);
//		//curwordnum += 2;
//		
//	    paramFile>>curword;
//		paramFile>>NEAT::mutate_add_link_prob;
//		
//	    //strcpy(curword, getUnit(filestring, curwordnum, delimiters));
//		//NEAT::mutate_add_link_prob = atof(curword);
//		//curwordnum += 2;
//		
//	    paramFile>>curword;
//		paramFile>>NEAT::interspecies_mate_rate;
//		
//	    //strcpy(curword, getUnit(filestring, curwordnum, delimiters));
//		//NEAT::interspecies_mate_rate = atof(curword);
//		//curwordnum += 2;
//		
//	    paramFile>>curword;
//		paramFile>>NEAT::mate_multipoint_prob;
//		
//	    //strcpy(curword, getUnit(filestring, curwordnum, delimiters));
//		//NEAT::mate_multipoint_prob = atof(curword);
//		//curwordnum += 2;
//		
//	    paramFile>>curword;
//		paramFile>>NEAT::mate_multipoint_avg_prob;
//		
//	    //strcpy(curword, getUnit(filestring, curwordnum, delimiters));
//		//NEAT::mate_multipoint_avg_prob = atof(curword);
//		//curwordnum += 2;
//		
//	    paramFile>>curword;
//		paramFile>>NEAT::mate_singlepoint_prob;
//		
//	    //strcpy(curword, getUnit(filestring, curwordnum, delimiters));
//		//NEAT::mate_singlepoint_prob = atof(curword);
//		//curwordnum += 2;
//		
//	    paramFile>>curword;
//		paramFile>>NEAT::mate_only_prob;
//		
//	    //strcpy(curword, getUnit(filestring, curwordnum, delimiters));
//		//NEAT::mate_only_prob = atof(curword);
//		//curwordnum += 2;
//		
//	    paramFile>>curword;
//		paramFile>>NEAT::recur_only_prob;
//		
//	    //strcpy(curword, getUnit(filestring, curwordnum, delimiters));
//		//NEAT::recur_only_prob = atof(curword);
//		//curwordnum += 2;
//		
//	    paramFile>>curword;
//		paramFile>>NEAT::pop_size;
//		
//	    //strcpy(curword, getUnit(filestring, curwordnum, delimiters));
//		//NEAT::pop_size = atoi(curword);
//		//curwordnum += 2;
//		
//	    paramFile>>curword;
//		paramFile>>NEAT::dropoff_age;
//		
//	    //strcpy(curword, getUnit(filestring, curwordnum, delimiters));
//		//NEAT::dropoff_age = atoi(curword);
//		//curwordnum += 2;
//		
//	    paramFile>>curword;
//		paramFile>>NEAT::newlink_tries;
//		
//	    //strcpy(curword, getUnit(filestring, curwordnum, delimiters));
//		//NEAT::newlink_tries = atoi(curword);
//		//curwordnum += 2;
//		
//	    paramFile>>curword;
//		paramFile>>NEAT::print_every;
//		
//	    //strcpy(curword, getUnit(filestring, curwordnum, delimiters));
//		//NEAT::print_every = atoi(curword);
//		//curwordnum += 2;
//		
//	    paramFile>>curword;
//		paramFile>>NEAT::babies_stolen;
//		
//	    //strcpy(curword, getUnit(filestring, curwordnum, delimiters));
//		//NEAT::babies_stolen = atoi(curword);
//		//curwordnum += 2;
//
//	    paramFile>>curword;
//		paramFile>>NEAT::num_runs;
		} catch(Exception e){
			System.out.println("Neat had trouble opening param file");
			System.out.println( e.getMessage());
		}
//		
//
//	    if(output) {
//		    printf("trait_param_mut_prob=%f\n",trait_param_mut_prob);
//		    printf("trait_mutation_power=%f\n",trait_mutation_power);
//		    printf("linktrait_mut_sig=%f\n",linktrait_mut_sig);
//		    printf("nodetrait_mut_sig=%f\n",nodetrait_mut_sig);
//		    printf("weight_mut_power=%f\n",weight_mut_power);
//		    printf("recur_prob=%f\n",recur_prob);
//		    printf("disjoint_coeff=%f\n",disjoint_coeff);
//		    printf("excess_coeff=%f\n",excess_coeff);
//		    printf("mutdiff_coeff=%f\n",mutdiff_coeff);
//		    printf("compat_threshold=%f\n",compat_threshold);
//		    printf("age_significance=%f\n",age_significance);
//		    printf("survival_thresh=%f\n",survival_thresh);
//		    printf("mutate_only_prob=%f\n",mutate_only_prob);
//		    printf("mutate_random_trait_prob=%f\n",mutate_random_trait_prob);
//		    printf("mutate_link_trait_prob=%f\n",mutate_link_trait_prob);
//		    printf("mutate_node_trait_prob=%f\n",mutate_node_trait_prob);
//		    printf("mutate_link_weights_prob=%f\n",mutate_link_weights_prob);
//		    printf("mutate_toggle_enable_prob=%f\n",mutate_toggle_enable_prob);
//		    printf("mutate_gene_reenable_prob=%f\n",mutate_gene_reenable_prob);
//		    printf("mutate_add_node_prob=%f\n",mutate_add_node_prob);
//		    printf("mutate_add_link_prob=%f\n",mutate_add_link_prob);
//		    printf("interspecies_mate_rate=%f\n",interspecies_mate_rate);
//		    printf("mate_multipoint_prob=%f\n",mate_multipoint_prob);
//		    printf("mate_multipoint_avg_prob=%f\n",mate_multipoint_avg_prob);
//		    printf("mate_singlepoint_prob=%f\n",mate_singlepoint_prob);
//		    printf("mate_only_prob=%f\n",mate_only_prob);
//		    printf("recur_only_prob=%f\n",recur_only_prob);
//		    printf("pop_size=%d\n",pop_size);
//		    printf("dropoff_age=%d\n",dropoff_age);
//		    printf("newlink_tries=%d\n",newlink_tries);
//		    printf("print_every=%d\n",print_every);
//		    printf("babies_stolen=%d\n",babies_stolen);
//		    printf("num_runs=%d\n",num_runs);
//	    }
//
//		paramFile.close();
		return true;
	} //end of load_neat_params(String, boolean)
	
	boolean load_neat_params(String filename){
		return load_neat_params(filename, false);
	}
//
//} // namespace NEAT
} // class NEAT
//
//#endif
//
