package rtNEAT;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Vector;

//#ifndef _NETWORK_H_
//#define _NETWORK_H_
//
//#include <algorithm>
//#include <vector>
//#include "neat.h"
//#include "nnode.h"
//
//namespace NEAT {
//
//    class Genome;
//
//	// ----------------------------------------------------------------------- 
//	// A NETWORK is a LIST of input NODEs and a LIST of output NODEs           
//	//   The point of the network is to define a single entity which can evolve
//	//   or learn on its own, even though it may be part of a larger framework 
//	class Network {
class Network{
//
//		friend class Genome;
//
//	//protected:
//	public:
//
//		int numnodes; // The number of nodes in the net (-1 means not yet counted)
	public int numnodes;
//		int numlinks; //The number of links in the net (-1 means not yet counted)
	public int numlinks;
//
//		std::vector<NNode*> all_nodes;  // A list of all the nodes
	public Vector<Nnode> all_nodes;
//
//		std::vector<NNode*>::iterator input_iter;  // For GUILE network inputting 
//
//		void destroy();  // Kills all nodes and links within
	void destroy_helper(Nnode curnode, Vector<Nnode> seenlist) {
		Vector<Link> innodes=curnode.incoming;
		//std::vector<NNode*>::iterator location;

		if (!((curnode.type)==Nnode.nodetype.SENSOR)) {
			//for(curlink=innodes.begin();curlink!=innodes.end();++curlink) {
			for (Link curlink : innodes){
	            //location = std::find(seenlist.begin(),seenlist.end(),((*curlink)->in_node));
				//if (location==seenlist.end()) {
				//	seenlist.push_back((*curlink)->in_node);
				//	destroy_helper((*curlink)->in_node,seenlist);
				int location = seenlist.indexOf(curlink.in_node);
					if (location==seenlist.capacity()) {
						seenlist.add((curlink).in_node);
						((curlink).in_node).flushback_check(seenlist);
					}
			}
		}

	}
//		void destroy_helper(NNode *curnode,std::vector<NNode*> &seenlist); // helper for above
//
//		void nodecounthelper(NNode *curnode,int &counter,std::vector<NNode*> &seenlist);
	private int nodecounthelper(Nnode curnode,int counter, Vector<Nnode> seenlist) {
		Vector<Link> innodes=curnode.incoming;
		//std::vector<Link*>::iterator curlink;
		//std::vector<NNode*>::iterator location;

		if (!((curnode.type)== Nnode.nodetype.SENSOR)) {
			//for(curlink=innodes.begin();curlink!=innodes.end();++curlink) {
			for (Link curlink : innodes){
	            //location= std::find(seenlist.begin(),seenlist.end(),((*curlink)->in_node));
				int location = seenlist.indexOf(curlink.in_node);
				if (location==seenlist.size()) {
					counter++;
					seenlist.add((curlink).in_node);
					counter = nodecounthelper((curlink).in_node,counter,seenlist);
				}
			}

		}
		return counter;

	}
//		void linkcounthelper(NNode *curnode,int &counter,std::vector<NNode*> &seenlist);
	
	private int linkcounthelper(Nnode curnode,int counter, Vector<Nnode> seenlist) {
		Vector<Link> inlinks=curnode.incoming;
		//std::vector<Link*>::iterator curlink;
		//std::vector<NNode*>::iterator location;

	    //location = std::find(seenlist.begin(),seenlist.end(),curnode);
		int location = seenlist.indexOf(curnode);
		if ((!((curnode.type)== Nnode.nodetype.SENSOR))&&(location==seenlist.size())) {
			seenlist.add(curnode);

			//for(curlink=inlinks.begin();curlink!=inlinks.end();++curlink) {
			for (Link curlink : inlinks){
				counter++;
				linkcounthelper((curlink).in_node,counter,seenlist);
			}

		}
		return counter;

	}
//
//	public:
//
//		Genome *genotype;  // Allows Network to be matched with its Genome
	public Genome genotype;
//
//		char *name; // Every Network or subNetwork can have a name
	public String name;
//		std::vector<NNode*> inputs;  // NNodes that input into the network
	public Vector<Nnode> inputs;
//		std::vector<NNode*> outputs; // Values output by the network
	public Vector<Nnode> outputs;
//
//		int net_id; // Allow for a network id
	public int net_id;
//
//		double maxweight; // Maximum weight in network for adaptation purposes
	public double maxweight;
//
//		bool adaptable; // Tells whether network can adapt or not
	public boolean adaptable;
//
//		// This constructor allows the input and output lists to be supplied
//		// Defaults to not using adaptation
//		Network(std::vector<NNode*> in,std::vector<NNode*> out,std::vector<NNode*> all,int netid);
	public Network(Vector<Nnode> in, Vector<Nnode> out, Vector<Nnode> all,int netid) {
		  inputs=in;
		  outputs=out;
		  all_nodes=all;
		  name=null;   //Defaults to no name  ..NOTE: TRYING TO PRINT AN EMPTY NAME CAN CAUSE A CRASH
		  numnodes=-1;
		  numlinks=-1;
		  net_id=netid;
		  adaptable=false;
		}
//
//		//Same as previous constructor except the adaptibility can be set true or false with adaptval
//		Network(std::vector<NNode*> in,std::vector<NNode*> out,std::vector<NNode*> all,int netid, bool adaptval);
	public Network(Vector<Nnode> in, Vector<Nnode> out, Vector<Nnode> all,int netid, boolean adaptval) {
		  inputs=in;
		  outputs=out;
		  all_nodes=all;
		  name=null;   //Defaults to no name  ..NOTE: TRYING TO PRINT AN EMPTY NAME CAN CAUSE A CRASH                                    
		  numnodes=-1;
		  this.numlinks=-1;
		  net_id=netid;
		  adaptable=adaptval;
		}
//
//		// This constructs a net with empty input and output lists
//		Network(int netid);
	public Network(int netid) {
		name=null; //Defaults to no name
		numnodes=-1;
		numlinks=-1;
		net_id=netid;
		adaptable=false;
	}
//
//		//Same as previous constructor except the adaptibility can be set true or false with adaptval
//		Network(int netid, bool adaptval);
	public Network(int netid, boolean adaptval) {
		  name=null; //Defaults to no name                                                                                               
		  numnodes=-1;
		  numlinks=-1;
		  net_id=netid;
		  adaptable=adaptval;
		}
//
//		// Copy Constructor
//		Network(const Network& network);
	public Network(Network network)
	{
		//std::vector<NNode*>::const_iterator curnode;

		// Copy all the inputs
		//for(curnode = network.inputs.begin(); curnode != network.inputs.end(); ++curnode) {
		for (Nnode curnode : network.inputs){
			//NNode* n = new NNode(**curnode);
			//inputs.push_back(n);
			inputs.add(new Nnode(curnode));
			//all_nodes.push_back(n);
			all_nodes.add(new Nnode(curnode));
		}

		// Copy all the outputs
		//for(curnode = network.outputs.begin(); curnode != network.outputs.end(); ++curnode) {
		for (Nnode curnode : network.outputs){
			//NNode* n = new NNode(**curnode);
			//outputs.push_back(n);
			outputs.add(new Nnode(curnode));
			//all_nodes.push_back(n);
			all_nodes.add(new Nnode(curnode));
		}

		if(network.name != null)
			//name = strdup(network.name);
			name = new String(network.name);
		else
			name = null;

		numnodes = network.numnodes;
		numlinks = network.numlinks;
		net_id = network.net_id;
		adaptable = network.adaptable;
	}
//
//		~Network();
//
//		// Puts the network back into an inactive state
//		void flush();
	public void flush() {
		//std::vector<NNode*>::iterator curnode;

		//for(curnode=outputs.begin();curnode!=outputs.end();++curnode) {
		for (Nnode curnode : outputs){
			(curnode).flushback();
		}
	}
//		
//		// Verify flushedness for debugging
//		void flush_check();
	public void flush_check() {
		//std::vector<NNode*>::iterator curnode;
		//std::vector<NNode*>::iterator location;
		Vector<Nnode> seenlist = new Vector<Nnode>();  //List of nodes not to doublecount

		//for(curnode=outputs.begin();curnode!=outputs.end();++curnode) {
		for (Nnode curnode : outputs){
	        //location= std::find(seenlist.begin(),seenlist.end(),(*curnode));
			int location = outputs.indexOf(curnode);
			if (location==seenlist.capacity()) {
				seenlist.add(curnode);
				(curnode).flushback_check(seenlist);
			}
		}
	}
//
//		// Activates the net such that all outputs are active
//		bool activate();
	public boolean activate() {
		//std::vector<NNode*>::iterator curnode;
		//std::vector<Link*>::iterator curlink;
		double add_amount;  //For adding to the activesum
		boolean onetime; //Make sure we at least activate once
		int abortcount=0;  //Used in case the output is somehow truncated from the network

		//cout<<"Activating network: "<<this->genotype<<endl;

		//Keep activating until all the outputs have become active 
		//(This only happens on the first activation, because after that they
		// are always active)

		onetime=false;

		while(outputsoff()||!onetime) {

			++abortcount;

			if (abortcount==20) {
				return false;
				//cout<<"Inputs disconnected from output!"<<endl;
			}
			//std::cout<<"Outputs are off"<<std::endl;

			// For each node, compute the sum of its incoming activation 
			//for(curnode=all_nodes.begin();curnode!=all_nodes.end();++curnode) {
			for (Nnode curnode : all_nodes){
				//Ignore SENSORS

				//cout<<"On node "<<(*curnode)->node_id<<endl;

				if (((curnode).type)!= Nnode.nodetype.SENSOR) {
					(curnode).activesum=0;
					(curnode).active_flag=false;  //This will tell us if it has any active inputs

					// For each incoming connection, add the activity from the connection to the activesum 
					//for(curlink=((*curnode)->incoming).begin();curlink!=((*curnode)->incoming).end();++curlink) {
					for (Link curlink : curnode.incoming){
						//Handle possible time delays
						if (!((curlink).time_delay)) {
							add_amount=((curlink).weight)*(((curlink).in_node).get_active_out());
							if ((((curlink).in_node).active_flag)||(((curlink).in_node).type==Nnode.nodetype.SENSOR)) 
								(curnode).active_flag=true;
							(curnode).activesum+=add_amount;
							//std::cout<<"Node "<<(*curnode)->node_id<<" adding "<<add_amount<<" from node "<<((*curlink)->in_node)->node_id<<std::endl;
						}
						else {
							//Input over a time delayed connection
							add_amount=((curlink).weight)*(((curlink).in_node).get_active_out_td());
							(curnode).activesum+=add_amount;
						}

					} //End for over incoming links

				} //End if (((*curnode)->type)!=SENSOR) 

			} //End for over all nodes

			// Now activate all the non-sensor nodes off their incoming activation 
			//for(curnode=all_nodes.begin();curnode!=all_nodes.end();++curnode) {
			for (Nnode curnode : all_nodes){

				if (((curnode).type)!= Nnode.nodetype.SENSOR) {
					//Only activate if some active input came in
					if ((curnode).active_flag) {
						//cout<<"Activating "<<(*curnode)->node_id<<" with "<<(*curnode)->activesum<<": ";

						//Keep a memory of activations for potential time delayed connections
						(curnode).last_activation2=(curnode).last_activation;
						(curnode).last_activation=(curnode).activation;

						//If the node is being overrided from outside,
						//stick in the override value
						if ((curnode).overridden()) {
							//Set activation to the override value and turn off override
							(curnode).activate_override();
						}
						else {
							//Now run the net activation through an activation function
							if ((curnode).ftype== Nnode.functype.SIGMOID)
								(curnode).activation=Neat.fsigmoid((curnode).activesum,4.924273,2.4621365);  //Sigmoidal activation- see comments under fsigmoid
						}
						//cout<<(*curnode)->activation<<endl;

						//Increment the activation_count
						//First activation cannot be from nothing!!
						(curnode).activation_count++;
					}
				}
			}

			onetime=true;
		}

		if (adaptable) {

		  //std::cout << "ADAPTING" << std:endl;

		  // ADAPTATION:  Adapt weights based on activations 
		  //for(curnode=all_nodes.begin();curnode!=all_nodes.end();++curnode) {
			for(Nnode curnode : all_nodes){
		    //Ignore SENSORS
		    
		    //cout<<"On node "<<(*curnode)->node_id<<endl;
		    
		    if (((curnode).type)!= Nnode.nodetype.SENSOR) {
		      
		      // For each incoming connection, perform adaptation based on the trait of the connection 
		      //for(curlink=((*curnode)->incoming).begin();curlink!=((*curnode)->incoming).end();++curlink) {
		    	for (Link curlink : curnode.incoming){
			
					if (((curlink).trait_id==2)||
					    ((curlink).trait_id==3)||
					    ((curlink).trait_id==4)) {
				  
				  //In the recurrent case we must take the last activation of the input for calculating hebbian changes
						  if ((curlink).is_recurrent) {
						    (curlink).weight=
						      Neat.hebbian((curlink).weight,maxweight,
							      (curlink).in_node.last_activation, 
							      (curlink).out_node.get_active_out(),
							      (curlink).params[0],(curlink).params[1],
							      (curlink).params[2]);  
						  }
						  else { //non-recurrent case
						    (curlink).weight=
						      Neat.hebbian((curlink).weight,maxweight,
							      (curlink).in_node.get_active_out(), 
							      (curlink).out_node.get_active_out(),
							      (curlink).params[0],(curlink).params[1],
							      (curlink).params[2]);
						  }
					}
			
		      }
		      
		    }
		    
		  }
		  
		} //end if (adaptable)

		return true;  
	}
	
	// THIS WAS NOT USED IN THE FINAL VERSION, AND NOT FULLY IMPLEMENTED,   
	// BUT IT SHOWS HOW SOMETHING LIKE THIS COULD BE INITIATED
	// Note that checking networks for loops in general in not necessary
	// and therefore I stopped writing this function
	// Check Network for loops.  Return true if its ok, false if there is a loop.
	//bool Network::integrity() {
	//  std::vector<NNode*>::iterator curnode;
	//  std::vector<std::vector<NNode*>*> paths;
	//  int count;
	//  std::vector<NNode*> *newpath;
	//  std::vector<std::vector<NNode*>*>::iterator curpath;

	//  for(curnode=outputs.begin();curnode!=outputs.end();++curnode) {
//	    newpath=new std::vector<NNode*>();
//	    paths.push_back(newpath);
//	    if (!((*curnode)->integrity(newpath))) return false;
	//  }

	//Delete the paths now that we are done
	//  curpath=paths.begin();
	//  for(count=0;count<paths.size();count++) {
//	    delete (*curpath);
//	    curpath++;
	//  }

	//  return true;
	//}
//
//		// Prints the values of its outputs
//		void show_activation();
	public void show_activation() {
		//std::vector<NNode*>::iterator curnode;
		int count;

		//if (name!=0)
		//  cout<<"Network "<<name<<" with id "<<net_id<<" outputs: (";
		//else cout<<"Network id "<<net_id<<" outputs: (";

		count=1;
		//for(curnode=outputs.begin();curnode!=outputs.end();++curnode) {
		for (Nnode curnode : outputs){
			//cout<<"[Output #"<<count<<": "<<(*curnode)<<"] ";
			count++;
		}

		//cout<<")"<<endl;
	}
//
//		void show_input();
	
	public void show_input() {
		//std::vector<NNode*>::iterator curnode;
		int count;

		//if (name!=0)
		//  cout<<"Network "<<name<<" with id "<<net_id<<" inputs: (";
		//else cout<<"Network id "<<net_id<<" outputs: (";

		count=1;
		//for(curnode=inputs.begin();curnode!=inputs.end();++curnode) {
		for (Nnode curnode : inputs){
			//cout<<"[Input #"<<count<<": "<<(*curnode)<<"] ";
			count++;
		}

		//cout<<")"<<endl;
	}
//
//		// Add a new input node
//		void add_input(NNode*);
	
	public void add_input(Nnode in_node) {
		inputs.add(new Nnode(in_node));
	}
//
//		// Add a new output node
//		void add_output(NNode*);
	public void add_output(Nnode out_node) {
		outputs.add(new Nnode(out_node));
	}
//
//		// Takes an array of sensor values and loads it into SENSOR inputs ONLY
//		void load_sensors(double*);
	
	public void load_sensors(double sensvals) {
		//int counter=0;  //counter to move through array
		//std::vector<NNode*>::iterator sensPtr;

		//for(sensPtr=inputs.begin();sensPtr!=inputs.end();++sensPtr) {
		for (Nnode sensPtr : inputs){
			//only load values into SENSORS (not BIASes)
			if (((sensPtr).type)== Nnode.nodetype.SENSOR) {
				(sensPtr).sensor_load(sensvals);
				sensvals++;
			}
		}
	}
//		void load_sensors(const std::vector<float> &sensvals);
	
	public void load_sensors(Vector<Float> sensvals) {
		//int counter=0;  //counter to move through array
		//std::vector<NNode*>::iterator sensPtr;
		//std::vector<float>::const_iterator valPtr;

		//for(valPtr = sensvals.begin(), sensPtr = inputs.begin(); sensPtr != inputs.end() && valPtr != sensvals.end(); ++sensPtr, ++valPtr) {
			//only load values into SENSORS (not BIASes)
		for (int i = 0; i < inputs.size() && i < sensvals.size(); i++){
			if (((inputs.get(i)).type)== Nnode.nodetype.SENSOR) {
				(inputs.get(i)).sensor_load(sensvals.get(i));
				//sensvals++;
			}
		}
	}
//
//		// Takes and array of output activations and OVERRIDES the outputs' actual 
//		// activations with these values (for adaptation)
//		void override_outputs(double*);
	public void override_outputs(Vector<Double> outvals) {

		//std::vector<NNode*>::iterator outPtr;
		int valIndex = 0;

		//for(outPtr=outputs.begin();outPtr!=outputs.end();++outPtr) {
		for (Nnode outPtr : outputs){
			(outPtr).override_output(outvals.get(valIndex));
			valIndex++;
		}

	}
//
//		// Name the network
//		void give_name(char*);
	public void give_name(String newname) {
		//char *temp;
		//char *temp2;
		//temp=new char[strlen(newname)+1];
		//strcpy(temp,newname);
		//if (name==null) name=temp;
		//else {
		//	temp2=name;
		//	delete temp2;
		//	name=temp;
		//}
		name = new String(newname);
	}
//
//		// Counts the number of nodes in the net if not yet counted
//		int nodecount();
	public int nodecount() {
		int counter=0;
		//std::vector<NNode*>::iterator curnode;
		//std::vector<NNode*>::iterator location;
		Vector<Nnode> seenlist = new Vector<Nnode>();  //List of nodes not to doublecount

		//for(curnode=outputs.begin();curnode!=outputs.end();++curnode) {
		for (Nnode curnode : outputs){

	        //location = std::find(seenlist.begin(),seenlist.end(),(*curnode));
			int location = seenlist.indexOf(curnode);
			if (location==seenlist.size()) {
				counter++;
				seenlist.add(new Nnode(curnode));
				counter = nodecounthelper((curnode),counter,seenlist);
			}
		}

		numnodes=counter;

		return counter;

	}
//
//		// Counts the number of links in the net if not yet counted
//		int linkcount();
	public int linkcount() {
		int counter=0;
		//std::vector<NNode*>::iterator curnode;
		Vector<Nnode> seenlist = new Vector<Nnode>();  //List of nodes not to doublecount

		//for(curnode=outputs.begin();curnode!=outputs.end();++curnode) {
		for (Nnode curnode : outputs){
			counter = linkcounthelper((curnode),counter,seenlist);
		}

		numlinks=counter;

		return counter;

	}
//
//		// This checks a POTENTIAL link between a potential in_node
//		// and potential out_node to see if it must be recurrent 
//		// Use count and thresh to jump out in the case of an infinite loop 
//		bool is_recur(NNode *potin_node,NNode *potout_node,int &count,int thresh);
	public boolean is_recur(Nnode potin_node,Nnode potout_node,int count,int thresh) {
		//std::vector<Link*>::iterator curlink;


		++count;  //Count the node as visited

		if (count>thresh) {
			//cout<<"returning false"<<endl;
			return false;  //Short out the whole thing- loop detected
		}

		if (potin_node==potout_node) return true;
		else {
			//Check back on all links...
			//for(curlink=(potin_node->incoming).begin();curlink!=(potin_node->incoming).end();curlink++) {
			for(Link curlink : (potin_node.incoming))
				//But skip links that are already recurrent
				//(We want to check back through the forward flow of signals only
				if (!((curlink).is_recurrent)) {
					if (is_recur((curlink).in_node,potout_node,count,thresh)) return true;
				}
		}
		return false;
	}
//
//		// Some functions to help GUILE input into Networks   
//		int input_start();
//		int load_in(double d);
//
//		// If all output are not active then return true
//		bool outputsoff();
	public boolean outputsoff() {
		//std::vector<NNode*>::iterator curnode;

		//for(curnode=outputs.begin();curnode!=outputs.end();++curnode) {
		for (Nnode curnode : outputs){
			if (((curnode).activation_count)==0) return true;
		}

		return false;
	}
//
//		// Just print connections weights with carriage returns
//		void print_links_tofile(char *filename);
	public void print_links_tofile(String filename) {
		//std::vector<NNode*>::iterator curnode;
		//std::vector<Link*>::iterator curlink;

	    //std::ofstream oFile(filename);

		//Make sure it worked
		//if (!oFile) {
		//	cerr<<"Can't open "<<filename<<" for output"<<endl;
			//return 0;
		//}
		
		try{
			OutputStream os = new FileOutputStream("filename");
			BufferedWriter oFile = new BufferedWriter(new OutputStreamWriter(os));

			//for(curnode=all_nodes.begin();curnode!=all_nodes.end();++curnode) {
			for(Nnode curnode : all_nodes){
				if (((curnode).type)!=Nnode.nodetype.SENSOR) {
					//for(curlink=((*curnode)->incoming).begin(); curlink!=((*curnode)->incoming).end(); ++curlink) {
					for (Link curlink : curnode.incoming){
		                //oFile << (*curlink)->in_node->node_id << " -> " <<( *curlink)->out_node->node_id << " : " << (*curlink)->weight << std::endl;
						oFile.write(curlink.in_node.node_id + " -> " + curlink.out_node.node_id + " : " + curlink.weight);
					} // end for loop on links
				} //end if
			} //end for loop on nodes
	
			oFile.close();
		}catch(Exception e){
			System.out.println("Network had trouble writing to file " + filename);
			System.out.println(e.getMessage());
		}

	} //print_links_tofile
	
	
//
//		int max_depth();
	public int max_depth() {
		  //Vector<Nnode>::iterator curoutput; //The current output we are looking at
		  int cur_depth; //The depth of the current node
		  int max=0; //The max depth
		  
		  //for(curoutput=outputs.begin();curoutput!=outputs.end();curoutput++) {
		  for (Nnode curoutput: outputs){
		    cur_depth=(curoutput).depth(0,this);
		    if (cur_depth>max) max=cur_depth;
		  }

		  return max;

		}
//
//	};
	
} // end class Network
	
//
//} // namespace NEAT
//
//#endif
//
