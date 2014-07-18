package rtNEAT;

//#ifndef _INNOVATION_H_
//#define _INNOVATION_H_
//
//namespace NEAT {
//
//	enum innovtype {
//		NEWNODE = 0,
//		NEWLINK = 1
//	};
//
//	// ------------------------------------------------------------
//	// This Innovation class serves as a way to record innovations
//	//   specifically, so that an innovation in one genome can be 
//	//   compared with other innovations in the same epoch, and if they
//	//   are the same innovation, they can both be assigned the same
//	//   innovation number.
//    //
//	//  This class can encode innovations that represent a new link
//	//  forming, or a new node being added.  In each case, two 
//	//  nodes fully specify the innovation and where it must have
//	//  occured.  (Between them)                                     
//	// ------------------------------------------------------------ 
//	class Innovation {
class Innovation {
//	private:
//		enum innovtype {
//			NEWNODE = 0,
//			NEWLINK = 1
//		};
	public enum innovtype{ 
		NEWNODE, 
		NEWLINK};
//
//		//typedef int innovtype;
//		//const int NEWNODE = 0;
		//private static final int NEWNODE = 0;
//		//const int NEWLINK = 1;
		//private static final int NEWLINK = 1; 
//
//	public:
//		innovtype innovation_type;  //Either NEWNODE or NEWLINK
		public innovtype innovation_type;
//
//		int node_in_id;     //Two nodes specify where the innovation took place
		public int node_in_id;
//		int node_out_id;
		public int node_out_id;
//
//		double innovation_num1;  //The number assigned to the innovation
		public double innovation_num1;
//		double innovation_num2;  // If this is a new node innovation, then there are 2 innovations (links) added for the new node 
		public double innovation_num2;
		
//		double new_weight;   //  If a link is added, this is its weight 
		public double new_weight;
//		int new_traitnum; // If a link is added, this is its connected trait 
		public int new_traitnum;
//
//		int newnode_id;  // If a new node was created, this is its node_id
		public int newnode_id;
//
//		double old_innov_num;  // If a new node was created, this is the innovnum of the gene's link it is being stuck inside 
		public double old_innov_num;
//
//		bool recur_flag;
		public boolean recur_flag;
//
//		//Constructor for the new node case
//		Innovation(int nin,int nout,double num1,double num2,int newid,double oldinnov);
		public Innovation(int nin,int nout,double num1,double num2,int newid,double oldinnov) {
			innovation_type= innovtype.NEWNODE;
			node_in_id=nin;
			node_out_id=nout;
			innovation_num1=num1;
			innovation_num2=num2;
			newnode_id=newid;
			old_innov_num=oldinnov;

			//Unused parameters set to zero
			new_weight=0;
			new_traitnum=0;
			recur_flag=false;
		}
//
//		//Constructor for new link case
//		Innovation(int nin,int nout,double num1,double w,int t);
		public Innovation(int nin,int nout,double num1,double w,int t) {
			innovation_type=innovtype.NEWLINK;
			node_in_id=nin;
			node_out_id=nout;
			innovation_num1=num1;
			new_weight=w;
			new_traitnum=t;

			//Unused parameters set to zero
			innovation_num2=0;
			newnode_id=0;
			recur_flag=false;
		}
//
//		//Constructor for a recur link
//		Innovation(int nin,int nout,double num1,double w,int t,bool recur);
		public Innovation(int nin,int nout,double num1,double w,int t,boolean recur) {
			innovation_type=innovtype.NEWLINK;
			node_in_id=nin;
			node_out_id=nout;
			innovation_num1=num1;
			new_weight=w;
			new_traitnum=t;

			//Unused parameters set to zero
			innovation_num2=0;
			newnode_id=0;
			recur_flag=recur;
		}
//
//	};
	
} // end of Innovation class
	
//
//} // namespace NEAT
//
//#endif
//
