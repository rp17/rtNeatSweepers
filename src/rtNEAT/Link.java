package rtNEAT;

//#ifndef _LINK_H_
//#define _LINK_H_
//
//#include "neat.h"
//#include "trait.h"
//#include "nnode.h"
//
//namespace NEAT {
//
//	class NNode;
//
//	// ----------------------------------------------------------------------- 
//	// A LINK is a connection from one node to another with an associated weight 
//	// It can be marked as recurrent 
//	// Its parameters are made public for efficiency 
//	class Link {
class Link{
//	public: 
//		double weight; // Weight of connection
	public double weight;
//		NNode *in_node; // NNode inputting into the link
	public Nnode in_node;
//		NNode *out_node; // NNode that the link affects
	public Nnode out_node;
//		bool is_recurrent;
	public boolean is_recurrent;
//		bool time_delay;
	public boolean time_delay;
//
//		Trait *linktrait; // Points to a trait of parameters for genetic creation
	public Trait linktrait;
//
//		int trait_id;  // identify the trait derived by this link
	public int trait_id;
//
//		// ************ LEARNING PARAMETERS *********** 
//		// These are link-related parameters that change during Hebbian type learning
//
//		double added_weight;  // The amount of weight adjustment 
	public double added_weight;
//		double params[NEAT::num_trait_params];
	public double[] params = new double[Neat.NUM_TRAIT_PARAMS];
//
//		Link(double w,NNode *inode,NNode *onode,bool recur);
	public Link(double w, Nnode inode, Nnode onode, boolean recur) {
		weight=w;
		in_node=inode;
		out_node=onode;
		is_recurrent=recur;
		added_weight=0;
		linktrait=null;
		time_delay=false;
		trait_id=1;
	}
//
//		// Including a trait pointer in the Link creation
//		Link(Trait *lt,double w,NNode *inode,NNode *onode,bool recur);
	public Link(Trait lt, double w, Nnode inode, Nnode onode, boolean recur) {
		weight=w;
		in_node=inode;
		out_node=onode;
		is_recurrent=recur;
		added_weight=0;
		linktrait=lt;
		time_delay=false;
		if (lt!=null)
			trait_id=lt.trait_id;
		else trait_id=1;
	}
//
//		// For when you don't know the connections yet
//		Link(double w);
	public Link(double w) {
		weight=w;
		in_node=out_node=null;  
		is_recurrent=false;
		linktrait=null;
		time_delay=false;
		trait_id=1;
	}
//
//		// Copy Constructor
//		Link(const Link& link);
	Link(Link link)
	{
		weight = link.weight;
		in_node = link.in_node;
		out_node = link.out_node;
		is_recurrent = link.is_recurrent;
		added_weight = link.added_weight;
		linktrait = link.linktrait;
		time_delay = link.time_delay;
		trait_id = link.trait_id;
	}
//
//		// Derive a trait into link params
//		void derive_trait(Trait *curtrait);
	public void derive_trait(Trait curtrait) {

		if (curtrait!=null) {
			for (int count=0;count<Neat.NUM_TRAIT_PARAMS;count++)
				params[count]=(curtrait.params)[count];
		}
		else {
			for (int count=0;count<Neat.NUM_TRAIT_PARAMS;count++)
				params[count]=0;
		}

		if (curtrait!=null)
			trait_id=curtrait.trait_id;
		else trait_id=1;

	}
//
//	};
	
} // end of link class

//
//} // namespace NEAT
//
//#endif
//
