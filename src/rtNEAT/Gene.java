package rtNEAT;

import java.io.BufferedWriter;
import java.util.Vector;

//#ifndef _GENE_H_
//#define _GENE_H_
//
//#include "neat.h"
//#include "trait.h"
//#include "link.h"
//#include "network.h"
//
//namespace NEAT {
//
//	class Gene {
class Gene{
//	public:
//
//		Link *lnk;
	public Link lnk;
//		double innovation_num;
	public double innovation_num;
//		double mutation_num;  //Used to see how much mutation has changed the link
	public double mutation_num;
//		bool enable;  //When this is off the Gene is disabled
	public boolean enable;
//		bool frozen;  //When frozen, the linkweight cannot be mutated
	public boolean frozen;
//
//		//Construct a gene with no trait
//		Gene(double w,NNode *inode,NNode *onode,bool recur,double innov,double mnum);
	public Gene(double w, Nnode inode, Nnode onode, boolean recur, double innov, double mnum) {
		lnk = new Link(w, inode, onode, recur);
		innovation_num = innov;
		mutation_num = mnum;

		enable = true;

		frozen = false;
	}
//
//		//Construct a gene with a trait
//		Gene(Trait *tp,double w,NNode *inode,NNode *onode,bool recur,double innov,double mnum);
	public Gene(Trait tp,double w,Nnode inode,Nnode onode,boolean recur,double innov,double mnum) {
		lnk=new Link(tp,w,inode,onode,recur);
		innovation_num=innov;
		mutation_num=mnum;

		enable=true;

		frozen=false;
	}
//
//		//Construct a gene off of another gene as a duplicate
//		Gene(Gene *g,Trait *tp,NNode *inode,NNode *onode);
	public Gene(Gene g,Trait tp,Nnode inode,Nnode onode) {
		//cout<<"Trying to attach nodes: "<<inode<<" "<<onode<<endl;
		lnk=new Link(tp,(g.lnk).weight,inode,onode,(g.lnk).is_recurrent);
		innovation_num=g.innovation_num;
		mutation_num=g.mutation_num;
		enable=g.enable;

		frozen=g.frozen;
	}
//
//		//Construct a gene from a file spec given traits and nodes
//		Gene(const char *argline, std::vector<Trait*> &traits, std::vector<NNode*> &nodes);
	public Gene(String argline, Vector<Trait> traits, Vector<Nnode> nodes) {
		//Gene parameter holders
		int traitnum;
		int inodenum;
		int onodenum;
		Nnode inode = null;
		Nnode onode = null;
		double weight;
		boolean recur;
		Trait traitptr = null;

		//std::vector<Trait*>::iterator curtrait;
		//std::vector<NNode*>::iterator curnode;

		//Get the gene parameters

	    //std::stringstream ss(argline);

		//char curword[128];
		//char delimiters[] = " \n";
		//int curwordnum = 0;

		//strcpy(curword, NEAT::getUnit(argline, curwordnum++, delimiters));
		//traitnum = atoi(curword);
		//strcpy(curword, NEAT::getUnit(argline, curwordnum++, delimiters));
		//inodenum = atoi(curword);
		//strcpy(curword, NEAT::getUnit(argline, curwordnum++, delimiters));
		//onodenum = atoi(curword);
		//strcpy(curword, NEAT::getUnit(argline, curwordnum++, delimiters));
		//weight = atof(curword);
		//strcpy(curword, NEAT::getUnit(argline, curwordnum++, delimiters));
		//recur = atoi(curword);
		//strcpy(curword, NEAT::getUnit(argline, curwordnum++, delimiters));
		//innovation_num = atof(curword);
		//strcpy(curword, NEAT::getUnit(argline, curwordnum++, delimiters));
		//mutation_num = atof(curword);
		//strcpy(curword, NEAT::getUnit(argline, curwordnum++, delimiters));
		//enable = (bool)(atoi(curword));

	    //ss >> traitnum >> inodenum >> onodenum >> weight >> recur >> innovation_num >> mutation_num >> enable;
	    //std::cout << traitnum << " " << inodenum << " " << onodenum << " ";
	    //std::cout << weight << " " << recur << " " << innovation_num << " ";
	    //std::cout << mutation_num << " " << enable << std::endl;
		
		String[] ss = new String[8];
		ss = argline.split( " ", 8);
		traitnum = Integer.parseInt(ss[0]);
		inodenum = Integer.parseInt(ss[1]);
		onodenum = Integer.parseInt(ss[2]);
		weight = Double.parseDouble(ss[3]);
		recur = Boolean.parseBoolean(ss[4]);
		innovation_num = Double.parseDouble(ss[5]);
		mutation_num = Double.parseDouble(ss[6]);
		enable = Boolean.parseBoolean(ss[7]);

		frozen=false; //TODO: MAYBE CHANGE

		//Get a pointer to the linktrait
		if (traitnum==0) traitptr=null;
		else {
//			curtrait=traits.begin();
//			while(((*curtrait)->trait_id)!=traitnum)
//				++curtrait;
//			traitptr=(*curtrait);
			for (Trait curtrait : traits){
				if(curtrait.trait_id != traitnum) continue;
				traitptr = new Trait(curtrait);
				break;
			}
		}

		//Get a pointer to the input node
//		curnode=nodes.begin();
//		while(((*curnode)->node_id)!=inodenum)
//			++curnode;
//		inode=(*curnode);
		for (Nnode curnode : nodes){
			if(curnode.node_id != inodenum) continue;
			inode = new Nnode(curnode);
			break;
		}

		//Get a pointer to the output node
//		curnode=nodes.begin();
//		while(((*curnode)->node_id)!=onodenum)
//			++curnode;
//		onode=(*curnode);
		for (Nnode curnode : nodes){
			if (curnode.node_id != onodenum) continue;
			onode = new Nnode(curnode);
		}

		lnk=new Link(traitptr,weight,inode,onode,recur);
	}
//
//		// Copy Constructor
//		Gene(const Gene& gene);
	public Gene(Gene gene)
	{
		innovation_num = gene.innovation_num;
		mutation_num = gene.mutation_num;
		enable = gene.enable;
		frozen = gene.frozen;

		lnk = new Link(gene.lnk);
	}
//
//		~Gene();
//
//		//Print gene to a file- called from Genome
//        void print_to_file(std::ostream &outFile);
//	void print_to_file(std::ofstream &outFile);
	public void print_to_file(BufferedWriter outFile) {
		  //outFile<<"gene ";
		try{
			outFile.write("gene");
			  //Start off with the trait number for this gene
			  if ((lnk.linktrait)==null) outFile.write("0 ");
			  else outFile.write(((lnk.linktrait).trait_id) + " ");
			  outFile.write((lnk.in_node).node_id + " ");
			  outFile.write((lnk.out_node).node_id + " ");
			  outFile.write((lnk.weight) + " ");
			  outFile.write(lnk.is_recurrent + " ");
			  outFile.write(innovation_num + " ");
			  outFile.write(mutation_num + " ");
			  outFile.write(enable + "\n");
		} catch(Exception e){
			System.out.println("Gene has had trouble writing to file");
			System.out.println(e.getMessage());
		}
	}
//	};
	
} //end of class Gene

//
//} // namespace NEAT
//
//
//#endif
