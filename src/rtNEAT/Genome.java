package rtNEAT;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.Vector;

//#ifndef _GENOME_H_
//#define _GENOME_H_
//
//#include <vector>
//#include "gene.h"
//#include "innovation.h"
//
//namespace NEAT {
//
//	enum mutator {
//		GAUSSIAN = 0,
//		COLDGAUSSIAN = 1
//	};
//
//	//----------------------------------------------------------------------- 
//	//A Genome is the primary source of genotype information used to create   
//	//a phenotype.  It contains 3 major constituents:                         
//	//  1) A list of Traits                                                 
//	//  2) A list of NNodes pointing to a Trait from (1)                      
//	//  3) A list of Genes with Links that point to Traits from (1)           
//	//(1) Reserved parameter space for future use
//	//(2) NNode specifications                                                
//	//(3) Is the primary source of innovation in the evolutionary Genome.     
//	//    Each Gene in (3) has a marker telling when it arose historically.   
//	//    Thus, these Genes can be used to speciate the population, and the   
//	//    list of Genes provide an evolutionary history of innovation and     
//	//    link-building.
//
//	class Genome {
class Genome{
	
	public enum mutator {GAUSSIAN, COLDGAUSSIAN};
//
//	public:
//		int genome_id;
	public int genome_id;
//
//		std::vector<Trait*> traits; //parameter conglomerations
	public Vector<Trait> traits;
//		std::vector<NNode*> nodes; //List of NNodes for the Network
	public Vector<Nnode> nodes;
//		std::vector<Gene*> genes; //List of innovation-tracking genes
	public Vector<Gene> genes;
//
//		Network *phenotype; //Allows Genome to be matched with its Network
	public Network phenotype;
//
//		int get_last_node_id(); //Return id of final NNode in Genome
	public int get_last_node_id() {
		return nodes.lastElement().node_id;
	}
//		double get_last_gene_innovnum(); //Return last innovation number in Genome
	public double get_last_gene_innovnum() {
		//return ((*(genes.end() - 1))->innovation_num)+1;
		return genes.lastElement().innovation_num;
	}
//
//		void print_genome(); //Displays Genome on screen
//
//		//Constructor which takes full genome specs and puts them into the new one
//		Genome(int id, std::vector<Trait*> t, std::vector<NNode*> n, std::vector<Gene*> g);
//	public Genome(int id, Vector<Trait> t, Vector<Nnode> n, Vector<Gene> g) {
//		genome_id=id;
//		traits= new Vector<Trait>(t);
//		nodes= new Vector<Nnode>(n); 
//		genes= new Vector<Gene>(g);
//	}
//
//		//Constructor which takes in links (not genes) and creates a Genome
//		Genome(int id, std::vector<Trait*> t, std::vector<NNode*> n, std::vector<Link*> links);
	public Genome(int id, Vector<Trait> t, Vector<Nnode> n, Vector<Gene> g, Vector<Link> links) {
		//std::vector<Link*>::iterator curlink;
		Gene tempgene;
		if (t != null) traits= new Vector<Trait>(t);
		if (n != null) nodes= new Vector<Nnode>(n); 
		if (g != null) genes= new Vector<Gene>(g);

		genome_id=id;

		//We go through the links and turn them into original genes
		//for(curlink=links.begin();curlink!=links.end();++curlink) {
		for (Link curlink : links){
			//Create genes one at a time
			tempgene=new Gene((curlink).linktrait, (curlink).weight,(curlink).in_node,(curlink).out_node,(curlink).is_recurrent,1.0,0.0);
			genes.add(tempgene);
		}

	}
//
//		// Copy constructor
//		Genome(const Genome& genome);
	
	public Genome(Genome genome)
	{
		genome_id = genome.genome_id;

//		Vector<Trait>::const_iterator curtrait;
//		std::vector<NNode*>::const_iterator curnode;
//		std::vector<Gene*>::const_iterator curgene;

		//for(curtrait=genome.traits.begin(); curtrait!=genome.traits.end(); ++curtrait) {
		for (Trait curtrait : genome.traits){
			traits.add(new Trait(curtrait));
		}

		Trait assoc_trait = new Trait();
		//Duplicate NNodes
		//for(curnode=genome.nodes.begin();curnode!=genome.nodes.end();++curnode) {
		for(Nnode curnode : genome.nodes){
			//First, find the trait that this node points to
			if (((curnode).nodetrait)==null) assoc_trait=null;
			else {
				//curtrait=traits.firstElement();
//				while(((*curtrait)->trait_id)!=(((*curnode)->nodetrait)->trait_id))
//					++curtrait;
				for(Trait curtrait : genome.traits){
					if (curtrait.trait_id != curnode.nodetrait.trait_id) continue;
					assoc_trait = new Trait(curtrait);
					break;
				}
				//assoc_trait=(*curtrait);
			}

			Nnode newnode=new Nnode(curnode,assoc_trait);

			(curnode).dup=newnode;  //Remember this node's old copy
			//    (*curnode)->activation_count=55;
			nodes.add(newnode);    
		}

		Nnode inode; //For forming a gene 
		Nnode onode; //For forming a gene
		Trait traitptr;

		//Duplicate Genes
		//for(curgene=genome.genes.begin(); curgene!=genome.genes.end(); ++curgene) {
		for (Gene curgene : genome.genes){
			//First find the nodes connected by the gene's link

			inode=(((curgene).lnk).in_node).dup;
			onode=(((curgene).lnk).out_node).dup;

			//Get a pointer to the trait expressed by this gene
			traitptr=((curgene).lnk).linktrait;
			if (traitptr==null) assoc_trait=null;
			else {
//				curtrait=traits.begin();
//				while(((*curtrait)->trait_id)!=(traitptr->trait_id))
//					++curtrait;
//				assoc_trait=(*curtrait);
				for (Trait curtrait : traits){
					if (curtrait.trait_id != traitptr.trait_id) continue;
					assoc_trait = new Trait(curtrait);
					break;
				}
			}

			Gene newgene=new Gene(curgene,assoc_trait,inode,onode);
			genes.add(newgene);

		}
	}

//
//		//Special constructor which spawns off an input file
//		//This constructor assumes that some routine has already read in GENOMESTART
//        Genome(int id, std::ifstream &iFile);
	public Genome(int id, BufferedReader iFile) {
		try{
			//char curword[128];  //max word size of 128 characters
			String curword;
	//		char curline[1024]; //max line size of 1024 characters
			String curline;
	//		char delimiters[] = " \n";
			String delimeters = new String(" \n");
	
			//int done=0;
			boolean done = false;
	
			//int pause;
	
			genome_id=id;
	
			//iFile.getline(curline, sizeof(curline));
			curline = iFile.readLine();
			//int wordcount = NEAT::getUnitCount(curline, delimiters);
			int wordcount = Neat.getUnitCount(curline, delimeters);
			int curwordnum = 0;
	
			//Loop until file is finished, parsing each line
			while (!done) {
	
		        //std::cout << curline << std::endl;
	
				if (curwordnum > wordcount || wordcount == 0) {
					//iFile.getline(curline, sizeof(curline));
					curline = iFile.readLine();
					//wordcount = NEAT::getUnitCount(curline, delimiters);
					wordcount = Neat.getUnitCount(curline, delimeters);
					curwordnum = 0;
				}
		        
		        //std::stringstream ss(curline);
				//strcpy(curword, NEAT::getUnit(curline, curwordnum++, delimiters));
		       // ss >> curword;
				curword = curline.split(" ", 2)[0];
				curline = new String(curline.split(" ", 2)[1]);
	
				//printf(curword);
				//printf(" test\n");
				//Check for end of Genome
				//if (strcmp(curword,"genomeend")==0) {
				if (curword == "genomeend"){
					//strcpy(curword, NEAT::getUnit(curline, curwordnum++, delimiters));
		            //ss >> curword;
					curword = curline.split(" ", 2)[0];
					curline = new String(curline.split(" ", 2)[1]);
					int idcheck = Integer.parseInt(curword);
					//iFile>>idcheck;
					if (idcheck!=genome_id) System.out.println("ERROR: id mismatch in genome");
					done=true;
				}
	
				//Ignore genomestart if it hasn't been gobbled yet
				else if ((curword == "genomestart")) {
					++curwordnum;
					//cout<<"genomestart"<<endl;
				}
	
				//Ignore comments surrounded by - they get printed to screen
				else if ((curword == "/*")) {
					//strcpy(curword, NEAT::getUnit(curline, curwordnum++, delimiters));
		            //ss >> curword;
		            curword = curline.split(" ", 2)[0];
					curline = new String(curline.split(" ", 2)[1]);
					while ((curword != "*/")) {
						//cout<<curword<<" ";
						//strcpy(curword, NEAT::getUnit(curline, curwordnum++, delimiters));
		                //ss >> curword;
		                curword = curline.split(" ", 2)[0];
						curline = new String(curline.split(" ", 2)[1]);
					}
					//cout<<endl;
				}
	
				//Read in a trait
				else if ((curword == "trait")) {
					Trait newtrait;
	
					//char argline[1024];
					String argline;
					//strcpy(argline, NEAT::getUnits(curline, curwordnum, wordcount, delimiters));
	
					curwordnum = wordcount + 1;
	
		            //ss.getline(argline, 1024);
					argline = curline.split("\n", 2)[0];
					curline = curline.split("\n", 2)[1];
					//Allocate the new trait
					newtrait=new Trait(argline);
	
					//Add trait to vector of traits
					traits.add(newtrait);
				}
	
				//Read in a node
				else if ((curword == "node")) {
					Nnode newnode = null;
	
					//char argline[1024];
					String argline;
					//strcpy(argline, NEAT::getUnits(curline, curwordnum, wordcount, delimiters));
					curwordnum = wordcount + 1;
		            
		            //ss.getline(argline, 1024);
					argline = curline.split("\n", 2)[0];
					curline = curline.split("\n", 2)[1];
					//Allocate the new node
					newnode=new Nnode(argline,traits);
	
					//Add the node to the list of nodes
					nodes.add(newnode);
				}
	
				//Read in a Gene
				else if ((curword == "gene")) {
					Gene newgene = null;
	
					//char argline[1024];
					String argline;
					//strcpy(argline, NEAT::getUnits(curline, curwordnum, wordcount, delimiters));
					curwordnum = wordcount + 1;
	
		            //ss.getline(argline, 1024);
					argline = curline.split("\n", 2)[0];
					curline = curline.split("\n", 2)[1];
		            //std::cout << "New gene: " << ss.str() << std::endl;
					//Allocate the new Gene
		            newgene=new Gene(argline,traits,nodes);
	
					//Add the gene to the genome
					genes.add(newgene);
	
		            //std::cout<<"Added gene " << newgene << std::endl;
				}
	
			}
		}catch(Exception e){
			System.out.println("Genome has had trouble reading from file");
			System.out.println(e.getMessage());
		}

	}
//
//		// This special constructor creates a Genome
//		// with i inputs, o outputs, n out of nmax hidden units, and random
//		// connectivity.  If r is true then recurrent connections will
//		// be included. 
//		// The last input is a bias
//		// Linkprob is the probability of a link  
//		Genome(int new_id,int i, int o, int n,int nmax, bool r, double linkprob);
	public Genome(int new_id,int i, int o, int n,int nmax, boolean r, double linkprob) {
		int totalnodes;
		boolean[] cm; //The connection matrix which will be randomized
		boolean[] cmp; //Connection matrix pointer
		int matrixdim;
		int count;

		int ncount; //Node and connection counters
		int ccount;

		int row;  //For navigating the matrix
		int col;

		double new_weight;

		int maxnode; //No nodes above this number for this genome

		int first_output; //Number of first output node

		totalnodes=i+o+nmax;
		matrixdim=totalnodes*totalnodes;
		cm=new boolean[matrixdim];  //Dimension the connection matrix
		maxnode=i+n;

		first_output=totalnodes-o+1;

		//For creating the new genes
		Nnode newnode;
		Gene newgene;
		Trait newtrait;
		Nnode in_node = null;
		Nnode out_node = null;

		//Retrieves the nodes pointed to by connection genes
		//Vector<Nnode>::iterator node_iter;

		//Assign the id
		genome_id=new_id;

		//cout<<"Assigned id "<<genome_id<<endl;

		//Step through the connection matrix, randomly assigning bits
		cmp=cm;
		for(count=0;count<matrixdim;count++) {
			if (Neat.randfloat()<linkprob)
				cmp[count]=true;
			else cmp[count]=false;
			//cmp++;
		}

		//Create a dummy trait (this is for future expansion of the system)
		newtrait=new Trait(1,0,0,0,0,0,0,0,0,0);
		traits.add(newtrait);

		//Build the input nodes
		for(ncount=1;ncount<=i;ncount++) {
			if (ncount<i)
				newnode=new Nnode(Nnode.nodetype.SENSOR,ncount,Nnode.nodeplace.INPUT);
			else newnode=new Nnode(Nnode.nodetype.SENSOR,ncount,Nnode.nodeplace.BIAS);

			newnode.nodetrait=newtrait;

			//Add the node to the list of nodes
			nodes.add(newnode);
		}

		//Build the hidden nodes
		for(ncount=i+1;ncount<=i+n;ncount++) {
			newnode=new Nnode(Nnode.nodetype.NEURON,ncount,Nnode.nodeplace.HIDDEN);
			newnode.nodetrait=newtrait;
			//Add the node to the list of nodes
			nodes.add(newnode);
		}

		//Build the output nodes
		for(ncount=first_output;ncount<=totalnodes;ncount++) {
			newnode=new Nnode(Nnode.nodetype.NEURON,ncount,Nnode.nodeplace.OUTPUT);
			newnode.nodetrait=newtrait;
			//Add the node to the list of nodes
			nodes.add(newnode);
		}

		//cout<<"Built nodes"<<endl;

		//Connect the nodes 
		ccount=1;  //Start the connection counter

		//Step through the connection matrix, creating connection genes
		cmp=cm;
		count=0;
		for(col=1;col<=totalnodes;col++)
			for(row=1;row<=totalnodes;row++) {
				//Only try to create a link if it is in the matrix
				//and not leading into a sensor

				if ((cmp[row]==true)&&(col>i)&&
					((col<=maxnode)||(col>=first_output))&&
					((row<=maxnode)||(row>=first_output))) {
						//If it isn't recurrent, create the connection no matter what
						if (col>row) {

							//Retrieve the in_node
//							node_iter=nodes.begin();
//							while((*node_iter)->node_id!=row)
//								node_iter++;
//
//							in_node=(*node_iter);
							for (Nnode node_iter : nodes){
								if(node_iter.node_id != row) continue;
								in_node = new Nnode(node_iter);
								break;
							}

							//Retrieve the out_node
//							node_iter=nodes.begin();
//							while((*node_iter)->node_id!=col)
//								node_iter++;
//
//							out_node=(*node_iter);
							for (Nnode node_iter : nodes){
								if(node_iter.node_id != col) continue;
								out_node = new Nnode(node_iter);
								break;
							}

							//Create the gene
							new_weight=Neat.randposneg() * Neat.randfloat();
							newgene=new Gene(newtrait,new_weight, in_node, out_node,false,count,new_weight);

							//Add the gene to the genome
							genes.add(newgene);
						}
						else if (r) {
							//Create a recurrent connection

							//Retrieve the in_node
//							node_iter=nodes.begin();
//							while((*node_iter)->node_id!=row)
//								node_iter++;
//
//							in_node=(*node_iter);
							for (Nnode node_iter : nodes){
								if(node_iter.node_id != row) continue;
								in_node = new Nnode(node_iter);
								break;
							}

							//Retrieve the out_node
//							node_iter=nodes.begin();
//							while((*node_iter)->node_id!=col)
//								node_iter++;
//
//							out_node=(*node_iter);
							for (Nnode node_iter : nodes){
								if(node_iter.node_id != col) continue;
								out_node = new Nnode(node_iter);
								break;
							}

							//Create the gene
							new_weight=Neat.randposneg() * Neat.randfloat();
							newgene=new Gene(newtrait,new_weight, in_node, out_node,true,count,new_weight);

							//Add the gene to the genome
							genes.add(newgene);

						}

					}

					count++; //increment gene counter	    
					//cmp++;
			}

			//delete [] cm;

	}
//
//		//Special constructor that creates a Genome of 3 possible types:
//		//0 - Fully linked, no hidden nodes
//		//1 - Fully linked, one hidden node splitting each link
//		//2 - Fully connected with a hidden layer, recurrent 
//		//num_hidden is only used in type 2
//		Genome(int num_in,int num_out,int num_hidden,int type);
	public Genome(int num_in,int num_out,int num_hidden,int type) {

		//Temporary lists of nodes
		Vector<Nnode> inputs = new Vector<Nnode>();
		Vector<Nnode> outputs = new Vector<Nnode>();
		Vector<Nnode> hidden = new Vector<Nnode>();
		Nnode bias = null; //Remember the bias

//		std::vector<NNode*>::iterator curnode1; //Node iterator1
//		std::vector<NNode*>::iterator curnode2; //Node iterator2
//		std::vector<NNode*>::iterator curnode3; //Node iterator3

		//For creating the new genes
		Nnode newnode;
		Gene newgene;
		Trait newtrait;

		int count;
		int ncount;


		//Assign the id 0
		genome_id=0;

		//Create a dummy trait (this is for future expansion of the system)
		newtrait=new Trait(1,0,0,0,0,0,0,0,0,0);
		traits.add(newtrait);

		//Adjust hidden number
		if (type==0) 
			num_hidden=0;
		else if (type==1)
			num_hidden=num_in*num_out;

		//Create the inputs and outputs

		//Build the input nodes
		for(ncount=1;ncount<=num_in;ncount++) {
			if (ncount<num_in)
				newnode=new Nnode(Nnode.nodetype.SENSOR,ncount,Nnode.nodeplace.INPUT);
			else { 
				newnode=new Nnode(Nnode.nodetype.SENSOR,ncount,Nnode.nodeplace.BIAS);
				bias=newnode;
			}

			//newnode->nodetrait=newtrait;

			//Add the node to the list of nodes
			nodes.add(newnode);
			inputs.add(newnode);
		}

		//Build the hidden nodes
		for(ncount=num_in+1;ncount<=num_in+num_hidden;ncount++) {
			newnode=new Nnode(Nnode.nodetype.NEURON,ncount,Nnode.nodeplace.HIDDEN);
			//newnode->nodetrait=newtrait;
			//Add the node to the list of nodes
			nodes.add(newnode);
			hidden.add(newnode);
		}

		//Build the output nodes
		for(ncount=num_in+num_hidden+1;ncount<=num_in+num_hidden+num_out;ncount++) {
			newnode=new Nnode(Nnode.nodetype.NEURON,ncount,Nnode.nodeplace.OUTPUT);
			//newnode->nodetrait=newtrait;
			//Add the node to the list of nodes
			nodes.add(newnode);
			outputs.add(newnode);
		}

		//Create the links depending on the type
		if (type==0) {
			//Just connect inputs straight to outputs

			count=1;

			//Loop over the outputs
			//for(curnode1=outputs.begin();curnode1!=outputs.end();++curnode1) {
			for (Nnode curnode1 : outputs){
				//Loop over the inputs
				//for(curnode2=inputs.begin();curnode2!=inputs.end();++curnode2) {
				for (Nnode curnode2 : inputs){
					//Connect each input to each output
					newgene=new Gene(newtrait,0, (curnode2), (curnode1),false,count,0);

					//Add the gene to the genome
					genes.add(newgene);	 

					count++;

				}

			}

		} //end type 0
		//A split link from each input to each output
		else if (type==1) {
			count=1; //Start the gene number counter

			//curnode3=hidden.begin(); //One hidden for ever input-output pair
			int curnode3 = 0;
			//Loop over the outputs
			//for(curnode1=outputs.begin();curnode1!=outputs.end();++curnode1) {
			for (Nnode curnode1 : outputs){
				//Loop over the inputs
				//for(curnode2=inputs.begin();curnode2!=inputs.end();++curnode2) {
				for (Nnode curnode2 : inputs){

					//Connect Input to hidden
					newgene=new Gene(newtrait,0, (curnode2), (hidden.get(curnode3)),false,count,0);
					//Add the gene to the genome
					genes.add(newgene);

					count++; //Next gene

					//Connect hidden to output
					newgene=new Gene(newtrait,0, (hidden.get(curnode3)), (curnode1),false,count,0);
					//Add the gene to the genome
					genes.add(newgene);

					++curnode3; //Next hidden node
					count++; //Next gene

				}
			}

		}//end type 1
		//Fully connected 
		else if (type==2) {
			count=1; //Start gene counter at 1


			//Connect all inputs to all hidden nodes
			//for(curnode1=hidden.begin();curnode1!=hidden.end();++curnode1) {
			for (Nnode curnode1 : hidden){
				//Loop over the inputs
				//for(curnode2=inputs.begin();curnode2!=inputs.end();++curnode2) {
				for(Nnode curnode2 : inputs){
					//Connect each input to each hidden
					newgene=new Gene(newtrait,0, (curnode2), (curnode1),false,count,0);

					//Add the gene to the genome
					genes.add(newgene);	 

					count++;

				}
			}

			//Connect all hidden units to all outputs
			//for(curnode1=outputs.begin();curnode1!=outputs.end();++curnode1) {
			for (Nnode curnode1 : outputs){
				//Loop over the inputs
				//for(curnode2=hidden.begin();curnode2!=hidden.end();++curnode2) {
				for (Nnode curnode2 : hidden){
					//Connect each input to each hidden
					newgene=new Gene(newtrait,0, (curnode2), (curnode1),false,count,0);

					//Add the gene to the genome
					genes.add(newgene);	 

					count++;

				}
			}

			//Connect the bias to all outputs
			//for(curnode1=outputs.begin();curnode1!=outputs.end();++curnode1) {
			for (Nnode curnode1 : outputs){
				newgene=new Gene(newtrait,0, bias, (curnode1),false,count,0);

				//Add the gene to the genome
				genes.add(newgene);	 

				count++;
			}

			//Recurrently connect the hidden nodes
			//for(curnode1=hidden.begin();curnode1!=hidden.end();++curnode1) {
			for (Nnode curnode1 : hidden){
				//Loop Over all Hidden
				//for(curnode2=hidden.begin();curnode2!=hidden.end();++curnode2) {
				for (Nnode curnode2 : hidden){
					//Connect each hidden to each hidden
					newgene=new Gene(newtrait,0, (curnode2), (curnode1),true,count,0);

					//Add the gene to the genome
					genes.add(newgene);	 

					count++;

				}

			}

		}//end type 2

	}
//
//		// Loads a new Genome from a file (doesn't require knowledge of Genome's id)
//		static Genome *new_Genome_load(char *filename);
	public static Genome new_Genome_load(String filename) {
		Genome newgenome;

		int id;

		//char curline[1024];
		char curword[] = new char[20];  //max word size of 20 characters
		//String curword;
		//char delimiters[] = " \n";
		//int curwordnum = 0;

		//std::ifstream iFile(filename);
		try{
			InputStream is = new FileInputStream("filename");
			BufferedReader iFile = new BufferedReader(new InputStreamReader(is));

		//Make sure it worked
		//if (!iFile) {
		//	cerr<<"Can't open "<<filename<<" for input"<<endl;
		//	return 0;
		//}

		//iFile>>curword;
		iFile.read(curword);	
		//iFile.getline(curline, sizeof(curline));
		//strcpy(curword, NEAT::getUnit(curline, curwordnum++, delimiters));

		//Bypass initial comment
		if ((new String(curword) == "/*")) {
			//strcpy(curword, NEAT::getUnit(curline, curwordnum++, delimiters));
			//iFile>>curword;
			iFile.read(curword);
			while ((new String(curword) != "*/")) {
				System.out.println(curword);
				//strcpy(curword, NEAT::getUnit(curline, curwordnum++, delimiters));
				//iFile>>curword;
				iFile.read(curword);
			}

			//cout<<endl;
			//iFile>>curword;
			iFile.read(curword);
			//strcpy(curword, NEAT::getUnit(curline, curwordnum++, delimiters));
		}

		//strcpy(curword, NEAT::getUnit(curline, curwordnum++, delimiters));
		//id = atoi(curword);
		//iFile>>id;
		id = iFile.read();

		newgenome=new Genome(id,iFile);

		iFile.close();
		return newgenome;
		}catch(Exception e){
			System.out.println("trouble constructing new Genome from file");
			System.out.println(e.getMessage());
			return null;
		}	
	}
//
//		//Destructor kills off all lists (including the trait vector)
//		~Genome();
//
//		//Generate a network phenotype from this Genome with specified id
//		Network *genesis(int);
	public Network genesis(int id) {
		//std::vector<NNode*>::iterator curnode; 
		//std::vector<Gene*>::iterator curgene;
		Nnode newnode;
		Trait curtrait;
		Link curlink;
		Link newlink;

		double maxweight=0.0; //Compute the maximum weight for adaptation purposes
		double weight_mag; //Measures absolute value of weights

		//Inputs and outputs will be collected here for the network
		//All nodes are collected in an all_list- 
		//this will be used for later safe destruction of the net
		Vector<Nnode> inlist = null;
		Vector<Nnode> outlist = null;
		Vector<Nnode> all_list = null;

		//Gene translation variables
		Nnode inode;
		Nnode onode;

		//The new network
		Network newnet;

		//Create the nodes
		//for(curnode=nodes.begin();curnode!=nodes.end();++curnode) {
		for(Nnode curnode : nodes){
			newnode=new Nnode((curnode).type,(curnode).node_id);

			//Derive the node parameters from the trait pointed to
			curtrait=(curnode).nodetrait;
			newnode.derive_trait(curtrait);

			//Check for input or output designation of node
			if (((curnode).gen_node_label)== Nnode.nodeplace.INPUT) 
				inlist.add(newnode);
			if (((curnode).gen_node_label)== Nnode.nodeplace.BIAS) 
				inlist.add(newnode);
			if (((curnode).gen_node_label)== Nnode.nodeplace.OUTPUT)
				outlist.add(newnode);

			//Keep track of all nodes, not just input and output
			all_list.add(newnode);

			//Have the node specifier point to the node it generated
			(curnode).analogue=newnode;

		}

		//Create the links by iterating through the genes
		//for(curgene=genes.begin();curgene!=genes.end();++curgene) {
		for (Gene curgene : genes){
			//Only create the link if the gene is enabled
			if (((curgene).enable)==true) {
				curlink=(curgene).lnk;
				inode=(curlink.in_node).analogue;
				onode=(curlink.out_node).analogue;
				//NOTE: This line could be run through a recurrency check if desired
				// (no need to in the current implementation of NEAT)
				newlink=new Link(curlink.weight,inode,onode,curlink.is_recurrent);

				(onode.incoming).add(newlink);
				(inode.outgoing).add(newlink);

				//Derive link's parameters from its Trait pointer
				curtrait=(curlink.linktrait);

				newlink.derive_trait(curtrait);

				//Keep track of maximum weight
				if (newlink.weight > 0)
					weight_mag=newlink.weight;
				else weight_mag=-newlink.weight;
				if (weight_mag>maxweight)
					maxweight=weight_mag;
			}
		}

		//Create the new network
		newnet=new Network(inlist,outlist,all_list,id);

		//Attach genotype and phenotype together
		newnet.genotype=this;
		phenotype=newnet;

		newnet.maxweight=maxweight;

		return newnet;

	}
//
//		// Dump this genome to specified file
//		void print_to_file(std::ostream &outFile);
//		void print_to_file(std::ofstream &outFile);
	public void print_to_file(BufferedWriter outFile) {
		  //std::vector<Trait*>::iterator curtrait;
		  //std::vector<NNode*>::iterator curnode;
		  //std::vector<Gene*>::iterator curgene;
		try{
			  //outFile<<"genomestart "<<genome_id<<std::endl;
			outFile.write("genomestart " + genome_id + "\n");
	
			  //Output the traits
			  //for(curtrait=traits.begin();curtrait!=traits.end();++curtrait) {
			for(Trait curtrait : traits){
	//		    (curtrait).trait_id=curtrait-traits.begin()+1;
	//		    (*curtrait)->print_to_file(outFile);
				curtrait.print_to_file(outFile);
			  }
	
			  //Output the nodes
			  //for(curnode=nodes.begin();curnode!=nodes.end();++curnode) {
			for(Nnode curnode : nodes){
			    //(*curnode)->print_to_file(outFile);
				curnode.print_to_file(outFile);
			  }
	
			  //Output the genes
			  //for(curgene=genes.begin();curgene!=genes.end();++curgene) {
			for(Gene curgene : genes){
			    //(*curgene)->print_to_file(outFile);
				curgene.print_to_file(outFile);
			  }
	
			  //outFile<<"genomeend "<<genome_id<<std::endl;
			outFile.write("genomeend" + genome_id + "\n");
		}catch(Exception e){
			System.out.println("Genome had trouble writing to fiel.");
			System.out.println(e.getMessage());
		}

		}
//
//		// Wrapper for print_to_file above
//		void print_to_filename(char *filename);
	public void print_to_filename(String filename) {
		//std::ofstream oFile(filename);
		//oFile.open(filename, std::ostream::Write);
		try{
			OutputStream is = new FileOutputStream("filename");
			BufferedWriter oFile = new BufferedWriter(new OutputStreamWriter(is));
			print_to_file(oFile);
			oFile.close();
		}catch(Exception e){
			System.out.println("Trouble writing to output file in Genome");
			System.out.println(e.getMessage());
		}
	}
//
//		// Duplicate this Genome to create a new one with the specified id 
//		Genome *duplicate(int new_id);
	public Genome duplicate(int new_id) {
		//Collections for the new Genome
		Vector<Trait> traits_dup = new Vector<Trait>();
		Vector<Nnode> nodes_dup = new Vector<Nnode>();
		Vector<Gene> genes_dup = new Vector<Gene>();

		//Iterators for the old Genome
		//std::vector<Trait*>::iterator curtrait;
		//std::vector<NNode*>::iterator curnode;
		//std::vector<Gene*>::iterator curgene;

		//New item pointers
		Trait newtrait = null;
		Nnode newnode = null;
		Gene newgene = null;
		Trait assoc_trait = null;  //Trait associated with current item

		Nnode inode = null; //For forming a gene 
		Nnode onode = null; //For forming a gene
		Trait traitptr = null;

		Genome newgenome;

		//verify();

		//Duplicate the traits
		//for(curtrait=traits.begin();curtrait!=traits.end();++curtrait) {
		for (Trait curtrait : traits){
			newtrait=new Trait(curtrait);
			traits_dup.add(newtrait);
		}

		//Duplicate NNodes
		//for(curnode=nodes.begin();curnode!=nodes.end();++curnode) {
		for (Nnode curnode : nodes){
			//First, find the trait that this node points to
			if (((curnode).nodetrait)==null) assoc_trait=null;
			else {
//				curtrait=traits_dup.begin();
//				while(((*curtrait)->trait_id)!=(((*curnode)->nodetrait)->trait_id))
//					++curtrait;
//				assoc_trait=(*curtrait);
				for (Trait curtrait : traits){
					if(curtrait.trait_id != curnode.nodetrait.trait_id) continue;
					assoc_trait = new Trait(curtrait);
					break;
				}
			}

			newnode=new Nnode(curnode,assoc_trait);

			(curnode).dup=newnode;  //Remember this node's old copy
			//    (*curnode)->activation_count=55;
			nodes_dup.add(newnode);    
		}

		//Duplicate Genes
		//for(curgene=genes.begin();curgene!=genes.end();++curgene) {
		for (Gene curgene : genes){
			//First find the nodes connected by the gene's link

			inode=(((curgene).lnk).in_node).dup;
			onode=(((curgene).lnk).out_node).dup;

			//Get a pointer to the trait expressed by this gene
			traitptr=((curgene).lnk).linktrait;
			if (traitptr==null) assoc_trait=null;
			else {
//				curtrait=traits_dup.begin();
//				while(((*curtrait)->trait_id)!=(traitptr->trait_id))
//					++curtrait;
//				assoc_trait=(*curtrait);
				for (Trait curtrait : traits){
					if(curtrait.trait_id != traitptr.trait_id) continue;
					assoc_trait = new Trait(curtrait);
					break;
				}
			}

			newgene=new Gene(curgene,assoc_trait,inode,onode);
			genes_dup.add(newgene);

		}

		//Finally, return the genome
		newgenome=new Genome(new_id,traits_dup,nodes_dup,genes_dup, null);

		return newgenome;

	}
//
//		// For debugging: A number of tests can be run on a genome to check its
//		// integrity
//		// Note: Some of these tests do not indicate a bug, but rather are meant
//		// to be used to detect specific system states
//		bool verify();
	public boolean verify() {
		//std::vector<NNode*>::iterator curnode;
		//std::vector<Gene*>::iterator curgene;
		//std::vector<Gene*>::iterator curgene2;
		Nnode inode;
		Nnode onode;

		boolean disab;

		int last_id;

		//int pause;

		//cout<<"Verifying Genome id: "<<this->genome_id<<endl;

		if (this==null) {
			//cout<<"ERROR GENOME EMPTY"<<endl;
			//cin>>pause;
		}

		//Check each gene's nodes
		//for(curgene=genes.begin();curgene!=genes.end();++curgene) {
		for (Gene curgene : genes){
			inode=((curgene).lnk).in_node;
			onode=((curgene).lnk).out_node;

			//Look for inode
//			curnode=nodes.begin();
//			while((curnode!=nodes.end())&&
//				((*curnode)!=inode))
//				++curnode;
//
//			if (curnode==nodes.end()) {
//				//cout<<"MISSING iNODE FROM GENE NOT IN NODES OF GENOME!!"<<endl;
//				//cin>>pause;
//				return false;
//			}
			for (Nnode curnode : nodes){
				if (curnode!=nodes.lastElement() && curnode != inode) continue;
				if (curnode == nodes.lastElement()){
					return false;
				}
			}

			//Look for onode
//			curnode=nodes.begin();
//			while((curnode!=nodes.end())&&
//				((*curnode)!=onode))
//				++curnode;
//
//			if (curnode==nodes.end()) {
//				//cout<<"MISSING oNODE FROM GENE NOT IN NODES OF GENOME!!"<<endl;
//				//cin>>pause;
//				return false;
//			}
			for (Nnode curnode : nodes){
				if (curnode!=nodes.lastElement() && curnode != onode) continue;
				if (curnode == nodes.lastElement()){
					return false;
				}
			}

		}

		//Check for NNodes being out of order
		last_id=0;
		//for(curnode=nodes.begin();curnode!=nodes.end();++curnode) {
		for(Nnode curnode : nodes){
			if ((curnode).node_id<last_id) {
				//cout<<"ALERT: NODES OUT OF ORDER in "<<this<<endl;
				//cin>>pause;
				return false;
			}

			last_id=(curnode).node_id;
		}


		//Make sure there are no duplicate genes
		//for(curgene=genes.begin();curgene!=genes.end();++curgene) {
		for (Gene curgene : genes){

			//for(curgene2=genes.begin();curgene2!=genes.end();++curgene2) {
			for (Gene curgene2 : genes){
				if (((curgene)!=(curgene2))&&
					((((curgene).lnk).is_recurrent)==(((curgene2).lnk).is_recurrent))&&
					((((((curgene2).lnk).in_node).node_id)==((((curgene).lnk).in_node).node_id))&&
					(((((curgene2).lnk).out_node).node_id)==((((curgene).lnk).out_node).node_id)))) {
						//cout<<"ALERT: DUPLICATE GENES: "<<(*curgene)<<(*curgene2)<<endl;
						//cout<<"INSIDE GENOME: "<<this<<endl;

						//cin>>pause;
					}


			}
		}

		//See if a gene is not disabled properly
		//Note this check does not necessary mean anything is wrong
		//
		//if (nodes.size()>=15) {
		//disab=false;
		////Go through genes and see if one is disabled
		//for(curgene=genes.begin();curgene!=genes.end();++curgene) {
		//if (((*curgene)->enable)==false) disab=true;
		//}

		//if (disab==false) {
		//cout<<"ALERT: NO DISABLED GENE IN GENOME: "<<this<<endl;
		////cin>>pause;
		//}

		//}
		//

		//Check for 2 disables in a row
		//Note:  Again, this is not necessarily a bad sign
		if (nodes.size()>=500) {
			disab=false;
			//for(curgene=genes.begin();curgene!=genes.end();++curgene) {
			for (Gene curgene : genes){
				if ((((curgene).enable)==false)&&(disab==true)) {
					//cout<<"ALERT: 2 DISABLES IN A ROW: "<<this<<endl;
				}
				if (((curgene).enable)==false) disab=true;
				else disab=false;
			}
		}

		//cout<<"GENOME OK!"<<endl;

		return true;
	}
//
//		// ******* MUTATORS *******
//
//		// Perturb params in one trait
//		void mutate_random_trait();
	public void mutate_random_trait() {
		//std::vector<Trait*>::iterator thetrait; //Trait to be mutated
		int traitnum;

		//Choose a random traitnum
		traitnum=Neat.randint(0,(traits.size())-1);

		//Retrieve the trait and mutate it
		//thetrait=traits.begin();
		//(*(thetrait[traitnum])).mutate();
		traits.setElementAt(new Trait(traits.get(traitnum)), traitnum);

		//TRACK INNOVATION? (future possibility)

	}
//
//		// Change random link's trait. Repeat times times
//		void mutate_link_trait(int times);
	public void mutate_link_trait(int times) {
		int traitnum;
		int genenum;
		//std::vector<Gene*>::iterator thegene;     //Link to be mutated
		//std::vector<Trait*>::iterator thetrait; //Trait to be attached
		int count;
		int loop;

		for(loop=1;loop<=times;loop++) {

			//Choose a random traitnum
			traitnum=Neat.randint(0,(traits.size())-1);

			//Choose a random linknum
			genenum=Neat.randint(0,genes.size()-1);

			//set the link to point to the new trait
			//thegene=genes.begin();
//			for(count=0;count<genenum;count++)
//				++thegene;
			Gene thegene = genes.get(genenum);

			//Do not alter frozen genes
			if (!((thegene).frozen)) {
				//thetrait=traits.begin();

				//((*thegene)->lnk)->linktrait=thetrait[traitnum];
				thegene.lnk.linktrait = traits.get(traitnum);
				genes.set(genenum, thegene);

			}
			//TRACK INNOVATION- future use
			//(*thegene)->mutation_num+=randposneg()*randfloat()*linktrait_mut_sig;

		}
	}
//
//		// Change random node's trait times times 
//		void mutate_node_trait(int times);
	public void mutate_node_trait(int times) {
		int traitnum;
		int nodenum;
		//std::vector<NNode*>::iterator thenode;     //Link to be mutated
		Nnode thenode;
		//std::vector<Gene*>::iterator thegene;  //Gene to record innovation
		//std::vector<Trait*>::iterator thetrait; //Trait to be attached
		Trait thetrait;
		int count;
		int loop;

		for(loop=1;loop<=times;loop++) {

			//Choose a random traitnum
			traitnum= Neat.randint(0,(traits.size())-1);

			//Choose a random nodenum
			nodenum= Neat.randint(0,nodes.size()-1);

			//set the link to point to the new trait
//			thenode=nodes.begin();
//			for(count=0;count<nodenum;count++)
//				++thenode;
			thenode = nodes.get(nodenum);

			//Do not mutate frozen nodes
			if (!((thenode).frozen)) {

				//thetrait=traits.begin();
				thetrait = traits.get(traitnum);
				//(*thenode)->nodetrait=thetrait[traitnum];
				thenode.nodetrait = thetrait;
				nodes.set(nodenum, thenode);

			}
			//TRACK INNOVATION! - possible future use
			//for any gene involving the mutated node, perturb that gene's
			//mutation number
			//for(thegene=genes.begin();thegene!=genes.end();++thegene) {
			//  if (((((*thegene)->lnk)->in_node)==(*thenode))
			//  ||
			//  ((((*thegene)->lnk)->out_node)==(*thenode)))
			//(*thegene)->mutation_num+=randposneg()*randfloat()*nodetrait_mut_sig;
			//}
		}
	}
//
//		// Add Gaussian noise to linkweights either GAUSSIAN or COLDGAUSSIAN (from zero)
//		void mutate_link_weights(double power,double rate,mutator mut_type);
	public void mutate_link_weights(double power,double rate,mutator mut_type) {
		//std::vector<Gene*>::iterator curgene;
		double num;  //counts gene placement
		double gene_total;
		double powermod; //Modified power by gene number
		//The power of mutation will rise farther into the genome
		//on the theory that the older genes are more fit since
		//they have stood the test of time

		double randnum;
		double randchoice; //Decide what kind of mutation to do on a gene
		double endpart; //Signifies the last part of the genome
		double gausspoint;
		double coldgausspoint;

		boolean severe;  //Once in a while really shake things up

		//Wright variables
		//double oldval;
		//double perturb;


		// --------------- WRIGHT'S MUTATION METHOD -------------- 

		////Use the fact that we know ends are newest
		//gene_total=(double) genes.size();
		//endpart=gene_total*0.8;
		//num=0.0;

		//for(curgene=genes.begin();curgene!=genes.end();curgene++) {

		////Mutate rate 0.2 controls how many params mutate in the list
		//if ((randfloat()<rate)||
		//((gene_total>=10.0)&&(num>endpart))) {

		//oldval=((*curgene)->lnk)->weight;

		////The amount to perturb the value by
		//perturb=randfloat()*power;

		////Once in a while leave the end part alone
		//if (num>endpart)
		//if (randfloat()<0.2) perturb=0;  

		////Decide positive or negative
		//if (gRandGen.randI()%2) {
		////Positive case

		////if it goes over the max, find something smaller
		//if (oldval+perturb>100.0) {
		//perturb=(100.0-oldval)*randfloat();
		//}

		//((*curgene)->lnk)->weight+=perturb;

		//}
		//else {
		////Negative case

		////if it goes below the min, find something smaller
		//if (oldval-perturb<100.0) {
		//perturb=(oldval+100.0)*randfloat();
		//}

		//((*curgene)->lnk)->weight-=perturb;

		//}
		//}

		//num+=1.0;

		//}

		

		// ------------------------------------------------------ 

		if (Neat.randfloat()>0.5) severe=true;
		else severe=false;

		//Go through all the Genes and perturb their link's weights
		num=0.0;
		gene_total=(double) genes.size();
		endpart=gene_total*0.8;
		//powermod=randposneg()*power*randfloat();  //Make power of mutation random
		//powermod=randfloat();
		powermod=1.0;

		//Possibility: Jiggle the newest gene randomly
		//if (gene_total>10.0) {
		//  lastgene=genes.end();
		//  lastgene--;
		//  if (randfloat()>0.4)
		//    ((*lastgene)->lnk)->weight+=0.5*randposneg()*randfloat();
		//}

	/*
		//KENHACK: NOTE THIS HAS BEEN MAJORLY ALTERED
		//     THE LOOP BELOW IS THE ORIGINAL METHOD
		if (mut_type==COLDGAUSSIAN) {
			//printf("COLDGAUSSIAN");
			for(curgene=genes.begin();curgene!=genes.end();curgene++) {
				if (randfloat()<0.9) {
					randnum=randposneg()*randfloat()*power*powermod;
					((*curgene)->lnk)->weight+=randnum;
				}
			}
		}

		
		for(curgene=genes.begin();curgene!=genes.end();curgene++) {
			if (randfloat()<0.2) {
				randnum=randposneg()*randfloat()*power*powermod;
				((*curgene)->lnk)->weight+=randnum;

				//Cap the weights at 20.0 (experimental)
				if (((*curgene)->lnk)->weight>1.0) ((*curgene)->lnk)->weight=1.0;
				else if (((*curgene)->lnk)->weight<-1.0) ((*curgene)->lnk)->weight=-1.0;
			}
		}

		*/


		//Loop on all genes  (ORIGINAL METHOD)
		//for(curgene=genes.begin();curgene!=genes.end();curgene++) {
		for (Gene curgene : genes){

			
			//Possibility: Have newer genes mutate with higher probability
			//Only make mutation power vary along genome if it's big enough
			//if (gene_total>=10.0) {
			//This causes the mutation power to go up towards the end up the genome
			//powermod=((power-0.7)/gene_total)*num+0.7;
			//}
			//else powermod=power;

			//The following if determines the probabilities of doing cold gaussian
			//mutation, meaning the probability of replacing a link weight with
			//another, entirely random weight.  It is meant to bias such mutations
			//to the tail of a genome, because that is where less time-tested genes
			//reside.  The gausspoint and coldgausspoint represent values above
			//which a random float will signify that kind of mutation.  

			//Don't mutate weights of frozen links
			if (!((curgene).frozen)) {

				if (severe) {
					gausspoint=0.3;
					coldgausspoint=0.1;
				}
				else if ((gene_total>=10.0)&&(num>endpart)) {
					gausspoint=0.5;  //Mutate by modification % of connections
					coldgausspoint=0.3; //Mutate the rest by replacement % of the time
				}
				else {
					//Half the time don't do any cold mutations
					if (Neat.randfloat()>0.5) {
						gausspoint=1.0-rate;
						coldgausspoint=1.0-rate-0.1;
					}
					else {
						gausspoint=1.0-rate;
						coldgausspoint=1.0-rate;
					}
				}

				//Possible methods of setting the perturbation:
				//randnum=gaussrand()*powermod;
				//randnum=gaussrand();

				randnum=Neat.randposneg() * Neat.randfloat() * power * powermod;
	            //std::cout << "RANDOM: " << randnum << " " << randposneg() << " " << randfloat() << " " << power << " " << powermod << std::endl;
				if (mut_type==mutator.GAUSSIAN) {
					randchoice= Neat.randfloat();
					if (randchoice>gausspoint)
						((curgene).lnk).weight+=randnum;
					else if (randchoice>coldgausspoint)
						((curgene).lnk).weight=randnum;
				}
				else if (mut_type==mutator.COLDGAUSSIAN)
					((curgene).lnk).weight=randnum;

				//Cap the weights at 8.0 (experimental)
				if (((curgene).lnk).weight > 8.0) ((curgene).lnk).weight = 8.0;
				else if (((curgene).lnk).weight < -8.0) ((curgene).lnk).weight = -8.0;

				//Record the innovation
				//(*curgene)->mutation_num+=randnum;
				(curgene).mutation_num=((curgene).lnk).weight;

				num+=1.0;

			}

		} //end for loop


	}
//
//		// toggle genes on or off 
//		void mutate_toggle_enable(int times);
	public void mutate_toggle_enable(int times) {
		int genenum;
		int count;
		//std::vector<Gene*>::iterator thegene;  //Gene to toggle
		//std::vector<Gene*>::iterator checkgene;  //Gene to check
		int genecount;

		for (count=1;count<=times;count++) {

			//Choose a random genenum
			genenum=Neat.randint(0,genes.size()-1);

			//find the gene
//			thegene=genes.begin();
//			for(genecount=0;genecount<genenum;genecount++)
//				++thegene;
			Gene thegene = genes.get(genenum);

			//Toggle the enable on this gene
			if (((thegene).enable)==true) {
				//We need to make sure that another gene connects out of the in-node
				//Because if not a section of network will break off and become isolated
//				checkgene=genes.begin();
//				while((checkgene!=genes.end())&&
//					(((((*checkgene)->lnk)->in_node)!=(((*thegene)->lnk)->in_node))||
//					(((*checkgene)->enable)==false)||
//					((*checkgene)->innovation_num==(*thegene)->innovation_num)))
//					++checkgene;
				for (Gene checkgene : genes){
					if ((checkgene!=genes.lastElement())&&
							(((((checkgene).lnk).in_node)!=(((thegene).lnk).in_node))||
							(((checkgene).enable)==false)||
							((checkgene).innovation_num==(thegene).innovation_num)))
						continue;
					//Disable the gene if it's safe to do so
					if (checkgene!=genes.lastElement())
						(thegene).enable=false;
					
				}
				
			}
			else (thegene).enable=true;
			genes.set(genenum, thegene);
		}
	}
//
//		// Find first disabled gene and enable it 
//		void mutate_gene_reenable();
	void mutate_gene_reenable() {
		//std::vector<Gene*>::iterator thegene;  //Gene to enable

		//thegene=genes.begin();

		//Search for a disabled gene
//		while((thegene!=genes.end())&&((*thegene)->enable==true))
//			++thegene;
		for (Gene curgene : genes){
			if(curgene.enable) continue;
			curgene.enable = true;
			break;
		}

		//Reenable it
//		if (thegene!=genes.end())
//			if (((*thegene)->enable)==false) (*thegene)->enable=true;

	}
//
//		// These last kinds of mutations return false if they fail
//		//   They can fail under certain conditions,  being unable
//		//   to find a suitable place to make the mutation.
//		//   Generally, if they fail, they can be called again if desired. 
//
//		// Mutate genome by adding a node respresentation 
//		bool mutate_add_node(std::vector<Innovation*> &innovs,int &curnode_id,double &curinnov);
	public boolean mutate_add_node(Vector<Innovation> innovs,int curnode_id,double curinnov) {
		//std::vector<Gene*>::iterator thegene;  //random gene containing the original link
		Gene thegene = null;
		int genenum;  //The random gene number
		Nnode in_node; //Here are the nodes connected by the gene
		Nnode out_node; 
		Link thelink;  //The link inside the random gene

		//double randmult;  //using a gaussian to find the random gene

		//std::vector<Innovation*>::iterator theinnov; //For finding a historical match
		boolean done=false;

		Gene newgene1 = null;  //The new Genes
		Gene newgene2 = null;
		Nnode newnode = null;   //The new NNode
		Trait traitptr = null; //The original link's trait

		//double splitweight;  //If used, Set to sqrt(oldweight of oldlink)
		double oldweight;  //The weight of the original link

		int trycount;  //Take a few tries to find an open node
		boolean found;

		//First, find a random gene already in the genome  
		trycount=0;
		found=false;

		//Split next link with a bias towards older links
		//NOTE: 7/2/01 - for robots, went back to random split
		//        because of large # of inputs
		if (false) {
			//thegene=genes.begin();
			//while (((thegene!=genes.end())
				//&&(!((*thegene)->enable)))||
				//((thegene!=genes.end())
				//&&(((*thegene)->lnk->in_node)->gen_node_label==BIAS)))
			for (Gene agene : genes){
				if (agene.enable) {
					thegene = agene;
					break;
				}
				if ((agene.lnk.in_node.gen_node_label != Nnode.nodeplace.BIAS)){
					thegene = agene;
					break;
				}
				//if (thegene == genes.lastElement()) thegene = null;
				//++thegene;
			}

			//Now randomize which node is chosen at this point
			//We bias the search towards older genes because 
			//this encourages splitting to distribute evenly
			//while (((thegene!=genes.end())&&
			for (Gene agene : genes){
				//(randfloat()<0.3))||
				if (Neat.randfloat() >= 0.3) {
					thegene = agene;
					break;
				}
				//((thegene!=genes.end())
				//&&(((*thegene)->lnk->in_node)->gen_node_label==BIAS)))
				if (thegene.lnk.in_node.gen_node_label != Nnode.nodeplace.BIAS){
					thegene = agene;
					break;
				}
			
				//++thegene;
			

			//if ((!(thegene==genes.end()))&&
			//	((*thegene)->enable))
				if (thegene != null){
					if (thegene.enable){
							found=true;
					}
				}
			}//end for loop
		} //end if(false)
		//In this else:
		//Alternative random gaussian choice of genes NOT USED in this
		//version of NEAT
		//NOTE: 7/2/01 now we use this after all
		else {
			while ((trycount<20)&&(!found)) {

				//Choose a random genenum
				//randmult=gaussrand()/4;
				//if (randmult>1.0) randmult=1.0;

				//This tends to select older genes for splitting
				//genenum=(int) floor((randmult*(genes.size()-1.0))+0.5);

				//This old totally random selection is bad- splitting
				//inside something recently splitted adds little power
				//to the system (should use a gaussian if doing it this way)
				genenum= Neat.randint(0,genes.size()-1);

				//find the gene
				//thegene=genes.begin();
				for(int genecount=0;genecount<genenum;genecount++)
					thegene = genes.get(genecount);
				//	++thegene;
				

				//If either the gene is disabled, or it has a bias input, try again
				if (!(((thegene).enable==false)||
					(((((thegene).lnk).in_node).gen_node_label)==Nnode.nodeplace.BIAS)))
					found=true;

				++trycount;

			}
		}

		//If we couldn't find anything so say goodbye
		if (!found) 
			return false;

		//Disabled the gene
		(thegene).enable=false;

		//Extract the link
		thelink=(thegene).lnk;
		oldweight=(thegene).lnk.weight;

		//Extract the nodes
		in_node=thelink.in_node;
		out_node=thelink.out_node;

		//Check to see if this innovation has already been done   
		//in another genome
		//Innovations are used to make sure the same innovation in
		//two separate genomes in the same generation receives
		//the same innovation number.
		//theinnov=innovs.begin();

		//while(!done) {
		for (Innovation theinnov : innovs){

			if (theinnov==innovs.lastElement()) {

				//The innovation is totally novel

				//Get the old link's trait
				traitptr=thelink.linktrait;

				//Create the new NNode
				//By convention, it will point to the first trait
				newnode=new Nnode(Nnode.nodetype.NEURON,curnode_id++,Nnode.nodeplace.HIDDEN);
				newnode.nodetrait=((traits.firstElement()));

				//Create the new Genes
				if (thelink.is_recurrent) {
					newgene1=new Gene(traitptr,1.0,in_node,newnode,true,curinnov,0);
					newgene2=new Gene(traitptr,oldweight,newnode,out_node,false,curinnov+1,0);
					curinnov+=2.0;
				}
				else {
					newgene1=new Gene(traitptr,1.0,in_node,newnode,false,curinnov,0);
					newgene2=new Gene(traitptr,oldweight,newnode,out_node,false,curinnov+1,0);
					curinnov+=2.0;
				}

				//Add the innovations (remember what was done)
				innovs.add(new Innovation(in_node.node_id,out_node.node_id,curinnov-2.0,curinnov-1.0,newnode.node_id,(thegene).innovation_num));      

				done=true;
			}

			// We check to see if an innovation already occured that was:
			//   -A new node
			//   -Stuck between the same nodes as were chosen for this mutation
			//   -Splitting the same gene as chosen for this mutation 
			//   If so, we know this mutation is not a novel innovation
			//   in this generation
			//   so we make it match the original, identical mutation which occured
			//   elsewhere in the population by coincidence 
			else if (((theinnov).innovation_type== Innovation.innovtype.NEWNODE)&&
				((theinnov).node_in_id==(in_node.node_id))&&
				((theinnov).node_out_id==(out_node.node_id))&&
				((theinnov).old_innov_num==(thegene).innovation_num)) 
			{

				//Here, the innovation has been done before

				//Get the old link's trait
				traitptr=thelink.linktrait;

				//Create the new NNode
				newnode=new Nnode(Nnode.nodetype.NEURON,(theinnov).newnode_id,Nnode.nodeplace.HIDDEN);      
				//By convention, it will point to the first trait
				//Note: In future may want to change this
				newnode.nodetrait=((traits.firstElement()));

				//Create the new Genes
				if (thelink.is_recurrent) {
					newgene1=new Gene(traitptr,1.0,in_node,newnode,true,(theinnov).innovation_num1,0);
					newgene2=new Gene(traitptr,oldweight,newnode,out_node,false,(theinnov).innovation_num2,0);
				}
				else {
					newgene1=new Gene(traitptr,1.0,in_node,newnode,false,(theinnov).innovation_num1,0);
					newgene2=new Gene(traitptr,oldweight,newnode,out_node,false,(theinnov).innovation_num2,0);
				}

				done=true;
			}
			//else ++theinnov;
		} //end for loop

		//Now add the new NNode and new Genes to the Genome
		//genes.push_back(newgene1);   //Old way to add genes- may result in genes becoming out of order
		//genes.push_back(newgene2);
//		add_gene(genes,newgene1);  //Add genes in correct order
//		add_gene(genes,newgene2);
//		node_insert(nodes,newnode);
		genes.add(newgene1);
		genes.add(newgene2);
		nodes.add(newnode);

		return true;

	} 

//
//		// Mutate the genome by adding a new link between 2 random NNodes 
//		bool mutate_add_link(std::vector<Innovation*> &innovs,double &curinnov,int tries);
	boolean mutate_add_link(Vector<Innovation> innovs,double curinnov,int tries) {

				int nodenum1,nodenum2;  //Random node numbers
				//std::vector<NNode*>::iterator thenode1,thenode2;  //Random node iterators
				Nnode thenode1 = null, thenode2 = null;
				int nodecount;  //Counter for finding nodes
				int trycount; //Iterates over attempts to find an unconnected pair of nodes
				Nnode nodep1 = null; //Pointers to the nodes
				Nnode nodep2 = null; //Pointers to the nodes
				//Vector<Gene>::iterator thegene; //Searches for existing link
				boolean found=false;  //Tells whether an open pair was found
				//std::vector<Innovation*>::iterator theinnov; //For finding a historical match
				boolean recurflag=false; //Indicates whether proposed link is recurrent
				Gene newgene = null;  //The new Gene

				int traitnum;  //Random trait finder
				//std::vector<Trait*>::iterator thetrait;

				double newweight;  //The new weight for the new link

				boolean done;
				boolean do_recur;
				boolean loop_recur;
				int first_nonsensor;

				//These are used to avoid getting stuck in an infinite loop checking
				//for recursion
				//Note that we check for recursion to control the frequency of
				//adding recurrent links rather than to prevent any paricular
				//kind of error
				int thresh=(nodes.size())*(nodes.size());
				int count=0;

				//Make attempts to find an unconnected pair
				trycount=0;


				//Decide whether to make this recurrent
				if (Neat.randfloat()<Neat.recur_only_prob) 
					do_recur=true;
				else do_recur=false;

				//Find the first non-sensor so that the to-node won't look at sensors as
				//possible destinations
				first_nonsensor=0;
				//thenode1=nodes.begin();
				//while(((thenode1).get_type())==SENSOR) {
				for (Nnode anode1 : nodes){
					if (thenode1.get_type() != Nnode.nodetype.SENSOR) break;
					first_nonsensor++;
					//++thenode1;
				}

				//Here is the recurrent finder loop- it is done separately
				if (do_recur) {

					while(trycount<tries) {

						//Some of the time try to make a recur loop
						if (Neat.randfloat()>0.5) {
							loop_recur=true;
						}
						else loop_recur=false;

						if (loop_recur) {
							nodenum1=Neat.randint(first_nonsensor,nodes.size()-1);
							nodenum2=nodenum1;
						}
						else {
							//Choose random nodenums
							nodenum1=Neat.randint(0,nodes.size()-1);
							nodenum2=Neat.randint(first_nonsensor,nodes.size()-1);
						}

						//Find the first node
//						thenode1=nodes.begin();
//						for(nodecount=0;nodecount<nodenum1;nodecount++)
//							++thenode1;
						for (nodecount = 0; nodecount < nodenum1; nodecount++)
							thenode1 = nodes.get(nodecount);

						//Find the second node
//						thenode2=nodes.begin();
//						for(nodecount=0;nodecount<nodenum2;nodecount++)
//							++thenode2;
						for (nodecount = 0; nodecount < nodenum2; nodecount++)
							thenode2 = nodes.get(nodecount);

						nodep1=(thenode1);
						nodep2=(thenode2);

						//See if a recur link already exists  ALSO STOP AT END OF GENES!!!!
						Iterator<Gene> thegeneIter=genes.iterator();
						Gene thegene = thegeneIter.next();
						while ((thegene != null) && 
							((nodep2.type)!= Nnode.nodetype.SENSOR) &&   //Don't allow SENSORS to get input
							(!((((thegene).lnk).in_node==nodep1)&&
							(((thegene).lnk).out_node==nodep2)&&
							((thegene).lnk).is_recurrent))) {
								thegene = thegeneIter.next();
							}

							if (thegene==genes.lastElement())
								trycount++;
							else {
								count=0;
								recurflag= phenotype.is_recur(nodep1.analogue,nodep2.analogue,count,thresh);

								//ADDED: CONSIDER connections out of outputs recurrent
								//REMVED: Can't compare nodetype to nodeplace
//								if (((nodep1.type)==Nnode.nodeplace.OUTPUT)||
//									((nodep2.type)==Nnode.nodeplace.OUTPUT))
//									recurflag=true;

								//Exit if the network is faulty (contains an infinite loop)
								//NOTE: A loop doesn't really matter
								//if (count>thresh) {
								//  cout<<"LOOP DETECTED DURING A RECURRENCY CHECK"<<std::endl;
								//  return false;
								//}

								//Make sure it finds the right kind of link (recur)
								if (!(recurflag))
									trycount++;
								else {
									trycount=tries;
									found=true;
								}

							}

					}
				}
				else {
					//Loop to find a nonrecurrent link
					while(trycount<tries) {

						//cout<<"TRY "<<trycount<<std::endl;

						//Choose random nodenums
						nodenum1=Neat.randint(0,nodes.size()-1);
						nodenum2=Neat.randint(first_nonsensor,nodes.size()-1);

						//Find the first node
//						thenode1=nodes.begin();
//						for(nodecount=0;nodecount<nodenum1;nodecount++)
//							++thenode1;
						for (nodecount = 0; nodecount < nodenum1; nodecount++)
							thenode1 = nodes.get(nodecount);

						//cout<<"RETRIEVED NODE# "<<(*thenode1)->node_id<<std::endl;

						//Find the second node
//						thenode2=nodes.begin();
//						for(nodecount=0;nodecount<nodenum2;nodecount++)
//							++thenode2;
						for (nodecount = 0; nodecount < nodenum2; nodecount++)
							thenode2 = nodes.get(nodecount);

						nodep1=(thenode1);
						nodep2=(thenode2);

						//See if a link already exists  ALSO STOP AT END OF GENES!!!!
						Iterator<Gene> thegeneIter = genes.iterator();
						Gene thegene = thegeneIter.next();
						while ((thegene!=genes.lastElement()) && 
							((nodep2.type)!=Nnode.nodetype.SENSOR) &&   //Don't allow SENSORS to get input
							(!((((thegene).lnk).in_node==nodep1)&&
							(((thegene).lnk).out_node==nodep2)&&
							(!(((thegene).lnk).is_recurrent))))) {
								thegene = thegeneIter.next();
							}

							if (thegene!=genes.lastElement())
								trycount++;
							else {

								count=0;
								recurflag=phenotype.is_recur(nodep1.analogue,nodep2.analogue,count,thresh);

								//ADDED: CONSIDER connections out of outputs recurrent
								//REMOVED: Why is it comparing nodetype to nodeplace???
//								if (((nodep1->type)==OUTPUT)||
//									((nodep2->type)==OUTPUT))
//									recurflag=true;

								//Exit if the network is faulty (contains an infinite loop)
								if (count>thresh) {
									//cout<<"LOOP DETECTED DURING A RECURRENCY CHECK"<<std::endl;
									//return false;
								}

								//Make sure it finds the right kind of link (recur or not)
								if (recurflag)
									trycount++;
								else {
									trycount=tries;
									found=true;
								}

							}

					} //End of normal link finding loop
				}

				//Continue only if an open link was found
				if (found) {

					//Check to see if this innovation already occured in the population
					Iterator<Innovation>theinnovIter=innovs.iterator();
					Innovation theinnov = theinnovIter.next();

					//If it was supposed to be recurrent, make sure it gets labeled that way
					if (do_recur) recurflag=true;

					done=false;

					while(!done) {

						//The innovation is totally novel
						if (theinnov==innovs.lastElement()) {

							//If the phenotype does not exist, exit on false,print error
							//Note: This should never happen- if it does there is a bug
							if (phenotype==null) {
								//cout<<"ERROR: Attempt to add link to genome with no phenotype"<<std::endl;
								return false;
							}

							//Useful for debugging
							//cout<<"nodep1 id: "<<nodep1->node_id<<std::endl;
							//cout<<"nodep1: "<<nodep1<<std::endl;
							//cout<<"nodep1 analogue: "<<nodep1->analogue<<std::endl;
							//cout<<"nodep2 id: "<<nodep2->node_id<<std::endl;
							//cout<<"nodep2: "<<nodep2<<std::endl;
							//cout<<"nodep2 analogue: "<<nodep2->analogue<<std::endl;
							//cout<<"recurflag: "<<recurflag<<std::endl;

							//NOTE: Something like this could be used for time delays,
							//      which are not yet supported.  However, this does not
							//      have an application with recurrency.
							//If not recurrent, randomize recurrency
							//if (!recurflag) 
							//  if (randfloat()<recur_prob) recurflag=1;

							//Choose a random trait
							traitnum=Neat.randint(0,(traits.size())-1);
							Iterator<Trait>thetraitIter=traits.iterator();
							Trait thetrait = thetraitIter.next();

							//Choose the new weight
							//newweight=(gaussrand())/1.5;  //Could use a gaussian
							newweight=Neat.randposneg()*Neat.randfloat()*1.0; //used to be 10.0

							//Create the new gene
							newgene=new Gene(((traits.get(traitnum))),newweight,nodep1,nodep2,recurflag,curinnov,newweight);

							//Add the innovation
							innovs.add(new Innovation(nodep1.node_id,nodep2.node_id,curinnov,newweight,traitnum));

							curinnov=curinnov+1.0;

							done=true;
						}
						//OTHERWISE, match the innovation in the innovs list
						else if (((theinnov).innovation_type==Innovation.innovtype.NEWLINK)&&
							((theinnov).node_in_id==(nodep1.node_id))&&
							((theinnov).node_out_id==(nodep2.node_id))&&
							((theinnov).recur_flag==recurflag)) {

								Iterator<Trait>thetraitIter=traits.iterator();
								Trait thetrait = thetraitIter.next();

								//Create new gene
								newgene=new Gene(((traits.get((theinnov).new_traitnum))),(theinnov).new_weight,nodep1,nodep2,recurflag,(theinnov).innovation_num1,0);

								done=true;

							}
						else {
							//Keep looking for a matching innovation from this generation
							theinnov = theinnovIter.next();
						}
					}

					//Now add the new Genes to the Genome
					//genes.push_back(newgene);  //Old way - could result in out-of-order innovation numbers in rtNEAT
					//add_gene(genes,newgene);  //Adds the gene in correct order
					genes.add(newgene);


					return true;
				}
				else {
					return false;
				}

			}

//
//		void mutate_add_sensor(std::vector<Innovation*> &innovs, double &curinnov);
//
//		// ****** MATING METHODS ***** 
//
//		// This method mates this Genome with another Genome g.  
//		//   For every point in each Genome, where each Genome shares
//		//   the innovation number, the Gene is chosen randomly from 
//		//   either parent.  If one parent has an innovation absent in 
//		//   the other, the baby will inherit the innovation 
//		//   Interspecies mating leads to all genes being inherited.
//		//   Otherwise, excess genes come from most fit parent.
//		Genome *mate_multipoint(Genome *g,int genomeid,double fitness1, double fitness2, bool interspec_flag);
//
//		//This method mates like multipoint but instead of selecting one
//		//   or the other when the innovation numbers match, it averages their
//		//   weights 
//		Genome *mate_multipoint_avg(Genome *g,int genomeid,double fitness1,double fitness2, bool interspec_flag);
//
//		// This method is similar to a standard single point CROSSOVER
//		//   operator.  Traits are averaged as in the previous 2 mating
//		//   methods.  A point is chosen in the smaller Genome for crossing
//		//   with the bigger one.  
//		Genome *mate_singlepoint(Genome *g,int genomeid);
//
//
//		// ******** COMPATIBILITY CHECKING METHODS ********
//
//		// This function gives a measure of compatibility between
//		//   two Genomes by computing a linear combination of 3
//		//   characterizing variables of their compatibilty.
//		//   The 3 variables represent PERCENT DISJOINT GENES, 
//		//   PERCENT EXCESS GENES, MUTATIONAL DIFFERENCE WITHIN
//		//   MATCHING GENES.  So the formula for compatibility 
//		//   is:  disjoint_coeff*pdg+excess_coeff*peg+mutdiff_coeff*mdmg.
//		//   The 3 coefficients are global system parameters 
//		double compatibility(Genome *g);
//
//		double trait_compare(Trait *t1,Trait *t2);
//
//		// Return number of non-disabled genes 
//		int extrons();
//
//		// Randomize the trait pointers of all the node and connection genes 
//		void randomize_traits();
//
//	protected:
//		//Inserts a NNode into a given ordered list of NNodes in order
//		void node_insert(std::vector<NNode*> &nlist, NNode *n);
//
//		//Adds a new gene that has been created through a mutation in the
//		//*correct order* into the list of genes in the genome
//		void add_gene(std::vector<Gene*> &glist,Gene *g);
//
//	};
	
} // end of class genome

//
//	//Calls special constructor that creates a Genome of 3 possible types:
//	//0 - Fully linked, no hidden nodes
//	//1 - Fully linked, one hidden node splitting each link
//	//2 - Fully connected with a hidden layer 
//	//num_hidden is only used in type 2
//	//Saves to file "auto_genome"
//	Genome *new_Genome_auto(int num_in,int num_out,int num_hidden,int type, const char *filename);
//
//	void print_Genome_tofile(Genome *g,const char *filename);
//
//} // namespace NEAT
//
//#endif
//
