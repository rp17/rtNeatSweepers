package rtNEAT;

import java.util.Collections;
import java.util.Vector;

//#ifndef _SPECIES_H_
//#define _SPECIES_H_
//
//#include "neat.h"
//#include "organism.h"
//#include "population.h"
//
//namespace NEAT {
//
//	class Organism;
//	class Population;
//
//	// ---------------------------------------------  
//	// SPECIES CLASS:
//	//   A Species is a group of similar Organisms      
//	//   Reproduction takes place mostly within a
//	//   single species, so that compatible organisms
//	//   can mate.                                      
//	// ---------------------------------------------  
//	class Species {
class Species {
//
//	public:
//
//		int id;
	public int id;
//		int age; //The age of the Species
	public int age;
//		double ave_fitness; //The average fitness of the Species
	public double ave_fitness;
//		double max_fitness; //Max fitness of the Species
	public double max_fitness;
//		double max_fitness_ever; //The max it ever had
	public double max_fitness_ever;
//		int expected_offspring;
	public int expected_offspring;
//		bool novel;
	public boolean novel;
//		bool checked;
	public boolean checked;
//		bool obliterate;  //Allows killing off in competitive coevolution stagnation
	public boolean obliterate;
//		std::vector<Organism*> organisms; //The organisms in the Species
	public Vector<Organism> organisms;
//		//std::vector<Organism*> reproduction_pool;  //The organisms for reproduction- NOT NEEDED 
//		int age_of_last_improvement;  //If this is too long ago, the Species will goes extinct
	public int age_of_last_improvement;
//		double average_est; //When playing real-time allows estimating average fitness
	public double average_est;
//
//		bool add_Organism(Organism *o);
	
//
//		Organism *first();
//
//		bool print_to_file(std::ostream &outFile);
//		bool print_to_file(std::ofstream &outFile);
//
//		//Change the fitness of all the organisms in the species to possibly depend slightly on the age of the species
//		//and then divide it by the size of the species so that the organisms in the species "share" the fitness
//		void adjust_fitness();
	
	public void adjust_fitness() {
		//std::vector<Organism*>::iterator curorg;

		int num_parents;
		int count;

		int age_debt; 

		//std::cout<<"Species "<<id<<" last improved "<<(age-age_of_last_improvement)<<" steps ago when it moved up to "<<max_fitness_ever<<std::endl;

		age_debt=(age-age_of_last_improvement+1)-Neat.dropoff_age;

		if (age_debt==0) age_debt=1;

		//for(curorg=organisms.begin();curorg!=organisms.end();++curorg) {
		for (Organism curorg : organisms){

			//Remember the original fitness before it gets modified
			(curorg).orig_fitness=(curorg).fitness;

			//Make fitness decrease after a stagnation point dropoff_age
			//Added an if to keep species pristine until the dropoff point
			//obliterate is used in competitive coevolution to mark stagnation
			//by obliterating the worst species over a certain age
			if ((age_debt>=1)||obliterate) {

				//Possible graded dropoff
				//((*curorg)->fitness)=((*curorg)->fitness)*(-atan(age_debt));

				//Extreme penalty for a long period of stagnation (divide fitness by 100)
				((curorg).fitness)=((curorg).fitness)*0.01;
				//std::cout<<"OBLITERATE Species "<<id<<" of age "<<age<<std::endl;
				//std::cout<<"dropped fitness to "<<((*curorg)->fitness)<<std::endl;
			}

			//Give a fitness boost up to some young age (niching)
			//The age_significance parameter is a system parameter
			//  if it is 1, then young species get no fitness boost
			if (age<=10) ((curorg).fitness)=((curorg).fitness)*Neat.age_significance; 

			//Do not allow negative fitness
			if (((curorg).fitness)<0.0) (curorg).fitness=0.0001; 

			//Share fitness with the species
			(curorg).fitness=((curorg).fitness)/(organisms.size());

		}

		//Sort the population and mark for death those after survival_thresh*pop_size
		//organisms.qsort(order_orgs);
		Collections.sort(organisms);

		//Update age_of_last_improvement here
		if ((((organisms.firstElement())).orig_fitness)> 
		    max_fitness_ever) {
		  age_of_last_improvement=age;
		  max_fitness_ever=(((organisms.firstElement())).orig_fitness);
		}

		//Decide how many get to reproduce based on survival_thresh*pop_size
		//Adding 1.0 ensures that at least one will survive
		num_parents=(int) Math.floor((Neat.survival_thresh*((double) organisms.size()))+1.0);
		
		//Mark for death those who are ranked too low to be parents
		curorg=organisms.begin();
		(*curorg)->champion=true;  //Mark the champ as such
		for(count=1;count<=num_parents;count++) {
		  if (curorg!=organisms.end())
		    ++curorg;
		}
		while(curorg!=organisms.end()) {
		  (*curorg)->eliminate=true;  //Mark for elimination
		  //std::std::cout<<"marked org # "<<(*curorg)->gnome->genome_id<<" fitness = "<<(*curorg)->fitness<<std::std::endl;
		  ++curorg;
		}             

	}

//
//		double compute_average_fitness(); 
//
//		double compute_max_fitness();
//
//		//Counts the number of offspring expected from all its members skim is for keeping track of remaining 
//		// fractional parts of offspring and distributing them among species
//		double count_offspring(double skim);
//
//		//Compute generations since last improvement
//		int last_improved() {
//			return age-age_of_last_improvement;
//		}
//
//		//Remove an organism from Species
//		bool remove_org(Organism *org);
//
//		double size() {
//			return organisms.size();
//		}
//
//		Organism *get_champ();
//
//		//Perform mating and mutation to form next generation
//		bool reproduce(int generation, Population *pop,std::vector<Species*> &sorted_species);
//
//		// *** Real-time methods *** 
//
//		//Place organisms in this species in order by their fitness
//		bool rank();
	public boolean rank() {
		//organisms.qsort(order_orgs);
	    //std::sort(organisms.begin(), organisms.end(), order_orgs);
		Collections.sort(organisms);
		return true;
	}
//
//		//Compute an estimate of the average fitness of the species
//		//The result is left in variable average_est and returned
//		//New variable: average_est, NEAT::time_alive_minimum (const) 
//		//Note: Initialization requires calling estimate_average() on all species
//		//      Later it should be called only when a species changes 
//		double estimate_average();
	public double estimate_average() {
		//std::vector<Organism*>::iterator curorg;
		double total = 0.0; //running total of fitnesses

		//Note: Since evolution is happening in real-time, some organisms may not
		//have been around long enough to count them in the fitness evaluation

		double num_orgs = 0; //counts number of orgs above the time_alive threshold


		//for(curorg = organisms.begin(); curorg != organisms.end(); ++curorg) {
		for (Organism curorg : organisms){
			//New variable time_alive
			if (((curorg).time_alive) >= Neat.time_alive_minimum) {    
				total += (curorg).fitness;
				++num_orgs;
			}
		}

		if (num_orgs > 0)
			average_est = total / num_orgs;
		else {
			average_est = 0;
		}

		return average_est;
	} 
//
//		//Like the usual reproduce() method except only one offspring is produced
//		//Note that "generation" will be used to just count which offspring # this is over all evolution
//		//Here is how to get sorted species:
//		//    Sort the Species by max fitness (Use an extra list to do this)
//		//    These need to use ORIGINAL fitness
//		//      sorted_species.sort(order_species);
//		Organism *reproduce_one(int generation, Population *pop,std::vector<Species*> &sorted_species);
////		Organism *reproduce_one(int generation, Population *pop,Vector<Species*> &sorted_species, bool addAdv, Genome* adv);
//
//		Species(int i);
	public Species(int i) {
		id=i;
		age=1;
		ave_fitness=0.0;
		expected_offspring=0;
		novel=false;
		age_of_last_improvement=0;
		max_fitness=0;
		max_fitness_ever=0;
		obliterate=false;

		average_est=0;
	}
//
//		//Allows the creation of a Species that won't age (a novel one)
//		//This protects new Species from aging inside their first generation
//		Species(int i,bool n);
	public Species(int i,boolean n) {
		id=i;
		age=1;
		ave_fitness=0.0;
		expected_offspring=0;
		novel=n;
		age_of_last_improvement=0;
		max_fitness=0;
		max_fitness_ever=0;
		obliterate=false;

		average_est=0;
	}
//
//		~Species();
//
//	};
//
//	// This is used for list sorting of Species by fitness of best organism highest fitness first 
//	bool order_species(Species *x, Species *y);
//
//	bool order_new_species(Species *x, Species *y);
//
//}
	
} // end of Species class
	
//
//#endif
//
