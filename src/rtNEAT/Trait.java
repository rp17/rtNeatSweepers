package rtNEAT;

import java.io.BufferedReader;
import java.io.BufferedWriter;

//#ifndef _TRAIT_H_
//#define _TRAIT_H_
//
//#include <fstream>
//#include "neat.h"
//
//namespace NEAT {
//
//	// ------------------------------------------------------------------ 
//	// TRAIT: A Trait is a group of parameters that can be expressed     
//	//        as a group more than one time.  Traits save a genetic      
//	//        algorithm from having to search vast parameter landscapes  
//	//        on every node.  Instead, each node can simply point to a trait 
//	//        and those traits can evolve on their own 
//	class Trait {
class Trait{
	
//
//		// ************ LEARNING PARAMETERS *********** 
//		// The following parameters are for use in    
//		//   neurons that learn through habituation,
//		//   sensitization, or Hebbian-type processes  
//
//	public:
//		int trait_id; // Used in file saving and loading
	public int trait_id;
//		double params[NEAT::num_trait_params]; // Keep traits in an array
	public double[] params;
	private Neat neat = new Neat();
//
//		Trait ();
	public Trait(){
		for (int count=0;count<Neat.NUM_TRAIT_PARAMS;count++)
			params[count]=0;
		trait_id=0;
	}
//
//		Trait(int id,double p1,double p2,double p3,double p4,double p5,double p6,double p7,double p8,double p9);
	Trait(int id,double p1,double p2,double p3,double p4,double p5,double p6,double p7,double p8,double p9) {
		trait_id=id;
		params[0]=p1;
		params[1]=p2;
		params[2]=p3;
		params[3]=p4;
		params[4]=p5;
		params[5]=p6;
		params[6]=p7;
		params[7]=0;
	}
//
//		// Copy Constructor
//		Trait(const Trait& t);
	public Trait(Trait t) {
		for(int count=0; count < Neat.NUM_TRAIT_PARAMS; count++)
			params[count]=(t.params)[count];

		trait_id = t.trait_id;
	}
//
//		// Create a trait exactly like another trait
//		Trait(Trait *t);

	//This method would overlap with the above in Java
	
//
//		// Special constructor off a file assume word "trait" has been read in
//		Trait(const char *argline);
	public Trait(String argline) {

	    //std::stringstream ss(argline);
		//Read in trait id
	 //   std::string curword;
		//char delimiters[] = " \n";
		//int curwordnum = 0;

		//strcpy(curword, NEAT::getUnit(argline, curwordnum++, delimiters));
	    
//		trait_id = atoi(curword);
	    //ss >> trait_id;
		String[] splitArgLine = argline.split(" ");
		trait_id = Integer.parseInt(splitArgLine[0]);

	    //std::cout << ss.str() << " trait_id: " << trait_id << std::endl;

		//IS THE STOPPING CONDITION CORRECT?  ALERT
		for(int count=1;count<Neat.NUM_TRAIT_PARAMS;count++) {
			//strcpy(curword, NEAT::getUnit(argline, curwordnum++, delimiters));
			//params[count] = atof(curword);
	        //ss >> params[count];
			//iFile>>params[count];
			params[count-1] = Double.parseDouble(splitArgLine[count]);
		}

	}
//
//		// Special Constructor creates a new Trait which is the average of 2 existing traits passed in
//		Trait(Trait *t1,Trait *t2);
//
//		// Dump trait to a stream
//        void print_to_file(std::ostream &outFile);
//	void print_to_file(std::ofstream &outFile);
	public void print_to_file(BufferedWriter outFile) {
		  //outFile<<"trait "<<trait_id<<" ";
		  //for(int count=0;count<NEAT::num_trait_params;count++)
		  //  outFile<<params[count]<<" ";

		  //outFile<<std::endl;
		try{
			outFile.write("trait " + trait_id);
		}catch(Exception e){
			System.out.println("Trait had trouble writing to file");
			System.out.println( e.getMessage());		}
		

		}
//
//		// Perturb the trait parameters slightly
//		void mutate();
	public Trait mutate() {
		for(int count=0;count<Neat.NUM_TRAIT_PARAMS;count++) {
			if (Neat.randfloat()>Neat.trait_param_mut_prob) {
				params[count]+=(Neat.randposneg()*Neat.randfloat())*Neat.trait_mutation_power;
				if (params[count]<0) params[count]=0;
				if (params[count]>1.0) params[count]=1.0;
			}
		}
		return this;
	}
//
//	};
//
//} // namespace NEAT
	
}// end of class Trait

//
//#endif
//
