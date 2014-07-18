package rtNEAT;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

//#ifndef _ORGANISM_H_
//#define _ORGANISM_H_
//
//#include "genome.h"
//#include "species.h"
//
//namespace NEAT {
//
//	class Species;
//	class Population;
//
//	// ---------------------------------------------  
//	// ORGANISM CLASS:
//	//   Organisms are Genomes and Networks with fitness
//	//   information 
//	//   i.e. The genotype and phenotype together
//	// ---------------------------------------------  
//	class Organism {
class Organism implements Comparable<Organism>{
//
//	public:
//		double fitness;  //A measure of fitness for the Organism
	public double fitness;
//		double orig_fitness;  //A fitness measure that won't change during adjustments
	public double orig_fitness;
//		double error;  //Used just for reporting purposes
	public double error;
//		bool winner;  //Win marker (if needed for a particular task)
	public boolean winner;
//		Network *net;  //The Organism's phenotype
	public Network net;
//		Genome *gnome; //The Organism's genotype 
	public Genome gnome;
//		Species *species;  //The Organism's Species 
	public Species species;
//		double expected_offspring; //Number of children this Organism may have
	public double expected_offspring;
//		int generation;  //Tells which generation this Organism is from
	public int generation;
//		bool eliminate;  //Marker for destruction of inferior Organisms
	public boolean eliminate;
//		bool champion; //Marks the species champ
	public boolean champion;
//		int super_champ_offspring;  //Number of reserved offspring for a population leader
	public int super_champ_offspring;
//		bool pop_champ;  //Marks the best in population
	public boolean pop_champ;
//		bool pop_champ_child; //Marks the duplicate child of a champion (for tracking purposes)
	public boolean pop_champ_child;
//		double high_fit; //DEBUG variable- high fitness of champ
	public double high_fit;
//		int time_alive; //When playing in real-time allows knowing the maturity of an individual
	public int time_alive;
//
//		// Track its origin- for debugging or analysis- we can tell how the organism was born
//		bool mut_struct_baby;
	public boolean mut_struct_baby;
//		bool mate_baby;
	public boolean mate_baby;
//
//		// MetaData for the object
//		char metadata[128];
	public String metadata;
//		bool modified;
	public boolean modified;
//
//		// Regenerate the network based on a change in the genotype 
//		void update_phenotype();
	public void update_phenotype() {

		//First, delete the old phenotype (net)
		net = null;

		//Now, recreate the phenotype off the new genotype
		net= new Network(gnome.genesis(gnome.genome_id));

		modified = true;
	}
//
//		// Print the Organism's genome to a file preceded by a comment detailing the organism's species, number, and fitness 
//		bool print_to_file(char *filename);   
//		bool write_to_file(std::ostream &outFile);

	public boolean print_to_file(String filename) {
		try{
			OutputStream is = new FileOutputStream(filename);
			BufferedWriter oFile = new BufferedWriter(new OutputStreamWriter(is));
			return write_to_file(oFile);
		}catch(Exception e){
			System.out.println("Organism could not open new file stream thingy");
			System.out.println(e.getMessage());
		}
		return false;
	}
	
	public boolean write_to_file(BufferedWriter outFile) {
		
		//char tempbuf2[1024];
		String tempbuf = new String("");
		if(modified == true) {
			//sprintf(tempbuf2, "/* Organism #%d Fitness: %f Time: %d */\n", (gnome)->genome_id, fitness, time_alive);
			tempbuf = new String (tempbuf + "/*Organims " + gnome.genome_id + " Fitness: " + fitness + "Time: "
					+ time_alive + "*/\n");
		} else {
			//sprintf(tempbuf2, "/* %s */\n", metadata);
			tempbuf = new String (tempbuf + "/* " + metadata + " */\n");
		}
		//outFile << tempbuf2;
		try{
			outFile.write(tempbuf);
			gnome.print_to_file(outFile);
		}catch(Exception e){
			System.out.println("Organism has had trouble writing to file");
			System.out.println(e.getMessage());
			return false;
		}
		return true;
	}
//
//		Organism(double fit, Genome *g, int gen, const char* md = 0);
	public Organism(double fit, Genome g,int gen, String md) {
		fitness=fit;
		orig_fitness=fitness;
		gnome=g;
		net=gnome.genesis(gnome.genome_id);
		species=null;  //Start it in no Species
		expected_offspring=0;
		generation=gen;
		eliminate=false;
		error=0;
		winner=false;
		champion=false;
		super_champ_offspring=0;

		// If md is null, then we don't have metadata, otherwise we do have metadata so copy it over
		if(md == null) {
			//strcpy(metadata, "");
			metadata = new String("");
		} else {
			//strncpy(metadata, md, 128);
			metadata = new String(md);
		}

		time_alive=0;

		//DEBUG vars
		pop_champ=false;
		pop_champ_child=false;
		high_fit=0;
		mut_struct_baby=false;
		mate_baby=false;

		modified = true;
	}
//		Organism(const Organism& org);	// Copy Constructor
	public Organism(Organism org)
	{
		fitness = org.fitness;
		orig_fitness = org.orig_fitness;
		gnome = new Genome((org.gnome));	// Associative relationship
		//gnome = org.gnome->duplicate(org.gnome->genome_id);
		net = new Network((org.net)); // Associative relationship
		species = org.species;	// Delegation relationship
		expected_offspring = org.expected_offspring;
		generation = org.generation;
		eliminate = org.eliminate;
		error = org.error;
		winner = org.winner;
		champion = org.champion;
		super_champ_offspring = org.super_champ_offspring;

		//strcpy(metadata, org.metadata);
		metadata = new String(org.metadata);
		//printf("copying %s did it work? %s", org.metadata, metadata);

		time_alive = org.time_alive;
		pop_champ = org.pop_champ;
		pop_champ_child = org.pop_champ_child;
		high_fit = org.high_fit;
		mut_struct_baby = org.mut_struct_baby;
		mate_baby = org.mate_baby;

		modified = false;
	}
	
	public static boolean order_orgs(Organism x, Organism y) {
		return (x).fitness > (y).fitness;
	}

	public static boolean order_orgs_by_adjusted_fit(Organism x, Organism y) {
		return (x).fitness / (x.species).organisms.size()  > (y).fitness / (y.species).organisms.size();
	}
//		~Organism();
//
//	};
//
//	// This is used for list sorting of Organisms by fitness..highest fitness first
//	bool order_orgs(Organism *x, Organism *y);
//
//	bool order_orgs_by_adjusted_fit(Organism *x, Organism *y);
	@Override
	public int compareTo(Organism org) {
		if (Organism.order_orgs(this, org)) return 1;
		else if (Organism.order_orgs(org, this)) return -1;
		else return 0;
	}
	
} // end of Organism class
	
//
//} // namespace NEAT
//
//#endif
//
