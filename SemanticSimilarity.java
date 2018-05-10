
import static org.semanticweb.owlapi.vocab.OWLFacet.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import static java.lang.reflect.Array.set;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;
import java.lang.Math;
import java.util.Collections;
import org.apache.commons.collections4.keyvalue.MultiKey;
import org.apache.commons.collections4.map.MultiKeyMap;
import org.apache.commons.collections4.multimap.AbstractListValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;

import org.semanticweb.elk.owlapi.ElkReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;

import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import org.semanticweb.owlapi.util.InferredAxiomGenerator;
import org.semanticweb.owlapi.util.InferredEquivalentClassAxiomGenerator;
import org.semanticweb.owlapi.util.InferredOntologyGenerator;
import org.semanticweb.owlapi.util.InferredSubClassAxiomGenerator;

/**
 * John Kaiserlik CSC526 - Assignment 1 This is my own work. JRK. 2.8.2018
 *
 * For self reference:
 *
 * @author John Kaiserlik CSC526: Assignment 1 W3C sets web standards:
 * https://www.w3.org/TR/#tr_OWL_Web_Ontology_Language OWL reference, *see
 * Syntax: https://www.w3.org/TR/owl-ref/ GO(Gene Ontology), search GO data:
 * http://www.geneontology.org/ Elk Reasoner
 * http://owlcs.github.io/owlapi/apidocs_4/org/semanticweb/owlapi/reasoner/OWLReasoner.html
 *
 * Ontologies have entities(classes), expressions, and axioms Entity : "named
 * objects used to build class expressions and axioms." They include classes,
 * data-types, (object, data, and annotation properties), named individuals.
 * Named individual : id'ed by their IRI Axiom: set of statements that are true
 * for a domain. Class expression: The individuals that satisfy
 * conditions/assertions.
 */
public class Assignment1 {

    protected HashMap<String, ArrayList<String>> xySuperClassesMap = null;

    protected OWLOntology geneOntology = null;
    protected OWLOntology newOntology = null;
    protected OWLOntologyManager man = null;
    protected HashMap<String, Double> ICmap = null;
    protected HashMap<String, ArrayList<String>> trimmedMap = null;
    protected HashMap<String, ArrayList<String>> superMapX = null;
    protected HashMap<String, ArrayList<String>> superMapY = null;
    protected HashMap<String, ArrayList<String>> superClassesMapAll = null;
    protected ArrayList goTerms = null;
    protected ArrayList goTermsNODUPS = null;
    protected int numberOfGenes = 0;
    protected Double m1 = 0.00;
    protected Double m2 = 0.00;

    public Assignment1() {
        this.xySuperClassesMap = new HashMap();
        this.geneOntology = null;
        this.newOntology = null;
        this.man = OWLManager.createOWLOntologyManager();
        this.ICmap = new HashMap();
        this.trimmedMap = null;
        this.superClassesMapAll = new HashMap();
        this.superMapX = new HashMap();
        this.superMapY = new HashMap();
        this.numberOfGenes = 0;
        this.goTerms = new ArrayList();
        this.goTermsNODUPS = new ArrayList();
        this.m1 = 0.00;
        this.m2 = 0.00;
    }

    public HashMap initMapFromFile() throws IOException {
        System.out.println("-----------------Reading file into a map (Gene : GoTerms)--------------------");
        File file = new File("Assignment3-Corpus.txt");
        Scanner scan = null;
        StringTokenizer strTok = null;
        this.trimmedMap = new HashMap();

        int termCount = 0;

        try {
            FileReader read = new FileReader(file);
            scan = new Scanner(read);
            String token = "";
            String term = null;
            String gene = null;
            ArrayList<String> termList = null;
            while (scan.hasNextLine()) {
                strTok = new StringTokenizer(scan.next(), ",");
                termList = new ArrayList();
                while (strTok.hasMoreTokens()) {
                    token = strTok.nextToken();
                    if (!token.substring(0, 2).equals("GO")) {
                        this.numberOfGenes++;
                        gene = token;
                        System.out.println("Gene : " + gene.toString());
                    } else {

                        term = token;
                        termList.add(term);
                        this.goTerms.add(term.toString());
                        System.out.print(term.toString() + ", ");
                        termCount++;
                    }
                }
                System.out.println("");
                this.trimmedMap.put(gene, termList);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            scan.close();
        }
        System.out.println("-------------------------------------------------------------");
        System.out.println("\n#Genes = " + this.numberOfGenes + ", #GoTerms = "
                + termCount + ", Map(Gene:goTerm) size = " + this.trimmedMap.size() + ", goTerms list size = "
                + this.goTerms.size() + "\n");
        return trimmedMap;
    }

    public void initializeNewOntology() throws OWLOntologyCreationException, OWLOntologyStorageException {
        this.newOntology = this.newOntology();
    }

    public void loadOntologyManager() throws OWLOntologyCreationException {
        File file = new File("go.owl");
        this.geneOntology = this.man.loadOntologyFromOntologyDocument(file);
        System.out.println(file.toPath() + " loaded sucessfully.");
    }

    public IRI getOntologyIRI() throws OWLOntologyCreationException {
        IRI iri = this.man.getOntologyDocumentIRI(this.geneOntology);
        return iri;
    }

    public OWLOntology newOntology() throws OWLOntologyCreationException, OWLOntologyStorageException {
        OWLDataFactory factory = this.man.getOWLDataFactory();
        OWLReasonerFactory reasonerFactory = new ElkReasonerFactory();
        OWLReasoner reasoner = reasonerFactory.createReasoner(this.geneOntology);
        reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);
        List<InferredAxiomGenerator<? extends OWLAxiom>> axioms = new ArrayList<InferredAxiomGenerator<? extends OWLAxiom>>();
        axioms.add(new InferredSubClassAxiomGenerator());
        axioms.add(new InferredEquivalentClassAxiomGenerator());
        OWLOntology ontology = this.man.createOntology();
        InferredOntologyGenerator generator = new InferredOntologyGenerator(reasoner, axioms);
        generator.fillOntology(factory, ontology);
        this.man.saveOntology(ontology, IRI.create((new File("ontology.text"))));
        reasoner.dispose();
        return ontology;
    }

    public ArrayList getGoTerms(String key) {
        ArrayList temp = null;
        if (this.trimmedMap.containsKey(key)) {
            temp = new ArrayList(this.trimmedMap.get(key));
        }
        return temp;
    }

    /*
    Get superclasses of Gene x and Gene y separately. Need to include the 
    duplicates in the map, so a multimap was used to store multiple non-unique
    keys.
     */
    public NodeSet<OWLClass> getSuperClassesOf(String x) throws OWLOntologyCreationException, OWLOntologyStorageException {
        //USE ORIGINAL ONTOLOGY
        //DEBUG HERE
        OWLOntology o = this.geneOntology;
        OWLReasonerFactory reasonerFactory = new ElkReasonerFactory();
        OWLReasoner reasoner = reasonerFactory.createReasoner(o);
        reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);
        String trim = "";
        NodeSet<OWLClass> set = null;
        for (OWLClassExpression e : o.getClassesInSignature()) {
            trim = e.toString().trim().replaceAll("Node", "")
                    .replaceAll("[()]", "")
                    .replaceAll("<http://purl.obolibrary.org/obo/", "")
                    .replaceAll(">", "");

            //set to "false" to get alllll the way up the true. True gives nearest parent.
            if (x.equals(trim)) {
                System.out.println("MATCH! of " + x + " and " + trim);
                System.out.println("Super classes of " + "'" + e.toString() + "' = "
                        + reasoner.getSuperClasses(e, false).toString());
                set = reasoner.getSuperClasses(e, false);
                System.out.println("-------------DONE------------------------");
                return set;
            }
        }
        return null;
    }

    /*
     * Use this for the calculation for IC because all the go terms and their
     * superclasses all the way up the tree to owl:Thing (root) are considered.
     */
    public void initializeSuperClasses(String x, String y) throws OWLOntologyCreationException, OWLOntologyStorageException {

        ArrayList<String> list = new ArrayList();
        String gene1 = x;
        String gene2 = y;
        String term = "";
        MultiKey mk = null;

        System.out.println("----------------------------------------------");
        System.out.println("# of go terms : " + this.goTerms.size());

        for (Object s : this.getGoTerms(gene1)) {
            term = s.toString().trim().replaceAll("Node", "")
                    .replaceAll("[()]", "")
                    .replaceAll("<http://purl.obolibrary.org/obo/", "")
                    .replaceAll(">", "");
            String superclass = "";
            for (Node<OWLClass> e : this.getSuperClassesOf(term)) {
                superclass = e.toString().trim().replaceAll("Node", "")
                        .replaceAll("[()]", "")
                        .replaceAll("<http://purl.obolibrary.org/obo/", "")
                        .replaceAll(">", "");
                list.add(superclass);
            }
            this.superMapX.put(term, list);
            list = new ArrayList();
        }
        String term2 = "";
        ArrayList<String> list2 = new ArrayList();
        for (Object s2 : this.getGoTerms(gene2)) {
            list2 = new ArrayList();
            term2 = s2.toString().trim().replaceAll("Node", "")
                    .replaceAll("[()]", "")
                    .replaceAll("<http://purl.obolibrary.org/obo/", "")
                    .replaceAll(">", "");
            String superclass2 = "";
            for (Node<OWLClass> e2 : this.getSuperClassesOf(term2)) {
                superclass2 = e2.toString().trim().replaceAll("Node", "")
                        .replaceAll("[()]", "")
                        .replaceAll("<http://purl.obolibrary.org/obo/", "")
                        .replaceAll(">", "");

                System.out.println(e2.toString().trim().replaceAll("Node", "")
                        .replaceAll("[()]", "")
                        .replaceAll("<http://purl.obolibrary.org/obo/", "")
                        .replaceAll(">", "") + "ADDED TO LIST");
                list2.add(superclass2);
            }
            this.superMapY.put(term2, list2);
        }
        System.out.println("superMapX + superMapY total size should be size = 20 to verify that duplicates were included.");
    }

    /*
    Compute IC values for all unique goTerms. Information Content (IC) value
    based on the inverse of the Logit function. Log base 10 or "natural" log base e
    will be taken, since dealing with Biology.
     */
    public void initializeInfoContentMap(String x, String y) throws OWLOntologyCreationException, OWLOntologyStorageException {
        System.out.println("----------------------------------------------------------------------");
        System.out.println("superClassesMapAll in initializeInfoContentMap size : " + this.superClassesMapAll.size());
        String gene1 = x;
        String gene2 = y;
        int k = 0;
        ArrayList<String> xList = this.getGoTerms(gene1);
        System.out.println("xList size = " + xList.size());
        ArrayList<String> yList = this.getGoTerms(gene2);
        System.out.println("yList size = " + yList.size());
        ArrayList<String> tree = new ArrayList();
        for (int i = 0; i < xList.size(); i++) {
            tree.add(xList.get(i).trim());
            String super1 = "";
            for (Node<OWLClass> o1 : this.getSuperClassesOf(xList.get(i))) {
                super1 = o1.toString().trim().replaceAll("Node", "")
                        .replaceAll("[()]", "")
                        .replaceAll("<http://purl.obolibrary.org/obo/", "")
                        .replaceAll(">", "");
                if (!super1.trim().equals("owl:Thing")) {
                    tree.add(super1);
                } else {
                    continue;
                }
            }
        }
        for (int i = 0; i < yList.size(); i++) {
            tree.add(yList.get(i).trim());
            String super2 = "";
            for (Node<OWLClass> o2 : this.getSuperClassesOf(yList.get(i))) {
                super2 = o2.toString().trim().replaceAll("Node", "")
                        .replaceAll("[()]", "")
                        .replaceAll("<http://purl.obolibrary.org/obo/", "")
                        .replaceAll(">", "");
                if (!super2.trim().equals("owl:Thing")) {
                    tree.add(super2);
                } else {
                    continue;
                }
            }
        }
        System.out.println("----------------------------------------------");
        System.out.println("Tree size : " + tree.size());
        String goTerm = "";
        for (int i = 0; i < tree.size(); i++) {
            goTerm = tree.get(i).trim();
            for (int j = 0; j < tree.size(); j++) {
                if (tree.get(i).equals(tree.get(j))) {
                    System.out.println(tree.get(i) + " equals " + tree.get(j));
                    k++;
                    System.out.println("frequency : " + k);
                }
            }
            this.ICmap.put(goTerm, -1 * (Math.log10((k / (double) this.numberOfGenes))));
            k = 0;
        }
        System.out.println("ICmap size : " + this.ICmap.size());
        System.out.println("-------------------------------------------");

        for (Entry<String, Double> e : this.ICmap.entrySet()) {
            System.out.println("Key : " + e.getKey() + ", IC : " + e.getValue());
        }
        System.out.println("ICmap size : " + this.ICmap.size());
    }

    public void printIcMapToFile() throws FileNotFoundException {
        File f = new File("icMap.txt");
        PrintWriter p = new PrintWriter(f);
        for (Entry<String, Double> e : this.ICmap.entrySet()) {
            p.append(e.getKey() + " : " + e.getValue()).println();
        }
        p.close();
    }
        public void printM1ToFile() throws FileNotFoundException {
        File f = new File("Resnik.txt");
        PrintWriter p = new PrintWriter(f);
        p.append("Semantic Similarity Score (Resnik) : " + this.m1);
        p.close();
    }
            public void printM2ToFile() throws FileNotFoundException {
        File f = new File("Jaccard.txt");
        PrintWriter p = new PrintWriter(f);
            p.append("Semantic Similarity Score (Jaccard) : " + this.m2);
        p.close();
    }

    public void allPairsResnik() {
        System.out.println("-------All Pairs : RESNIK-------------"
                + "");

        ArrayList<String> superClassesX = new ArrayList();
        ArrayList<String> superClassesY = new ArrayList();

        //S1, S2 for set operations, intersection
        Set<String> S1 = null;
        Set<String> S2 = null;

        Set<String> intersection = null;
        ArrayList<Double> aggregateIC = new ArrayList();

        //tempListX to store an "owl;Thing"-less copy of superClassesX list
        ArrayList<String> tempListX = null;
        String keyX = "";
        ArrayList<Double> ic = null;

//------------------------------------------------------
        for (Entry<String, ArrayList<String>> e : this.superMapX.entrySet()) {
            //get key of x in X
            keyX = e.getKey();
            //get superclasses of x in X
            superClassesX = e.getValue();
            String strX = "";
            tempListX = new ArrayList();

            for (int i = 0; i < superClassesX.size(); i++) {
                //cleanup s1 in S1
                strX = superClassesX.get(i).trim().replaceAll("Node", "")
                        .replaceAll("[()]", "")
                        .replaceAll("<http://purl.obolibrary.org/obo/", "")
                        .replaceAll(">", "");
//                System.out.println("strX[] = " + strX);
                //remove the root from S1 'cause IC=0
                if (strX.equals("owl:Thing")) {
                    System.out.println("owl:Thing detected and ignored!");
                    continue;
                } else {
                    tempListX.add(strX);
                    System.out.println("X : " + strX + " added");
                }
            }
            //S1 as HashSet
            S1 = new HashSet(tempListX);

            //Repeat for y in Y
            String keyY = "";
            ArrayList tempListY = new ArrayList();
            ic = new ArrayList();
//            System.out.println("----------------------superMapY-----------------------------------------------------------------");
            for (Entry<String, ArrayList<String>> e2 : this.superMapY.entrySet()) {
                keyY = e2.getKey();
                superClassesY = e2.getValue();
                String strY = "";
                for (int i = 0; i < superClassesY.size(); i++) {
                    strY = superClassesY.get(i).trim().replaceAll("Node", "")
                            .replaceAll("[()]", "")
                            .replaceAll("<http://purl.obolibrary.org/obo/", "")
                            .replaceAll(">", "");
//                    System.out.println("strY[] = " + strY);
                    //remove the root 'cause IC=0
                    if (strY.equals("owl:Thing")) {
                        System.out.println("owl:Thing detected and ignored!");
                        continue;
                    } else {
                        System.out.println("Y : " + strY + " added");
                        tempListY.add(strY);
                    }
                }

                Iterator itr1 = S1.iterator();
                while (itr1.hasNext()) {
                    System.out.println("S1 " + itr1.next().toString());
                }
                String element1 = "";
                String trimmer1 = "";
                S2 = new HashSet(tempListY);
                tempListY.clear();
                Iterator itr2 = S2.iterator();
                while (itr2.hasNext()) {
                    element1 = itr2.next().toString();
                    trimmer1 = element1.trim();
                    trimmer1 = trimmer1.replaceAll("[()]", "");
                    trimmer1 = trimmer1.replaceAll("<http://purl.obolibrary.org/obo/", "");
                    trimmer1 = trimmer1.replaceAll(">", "");
//                    System.out.println("S2 " + trimmer1);
                }

                //S1 retains all from S2, intersection operation
                intersection = new HashSet(S1);
                intersection.retainAll(S2);
                Iterator itr3 = intersection.iterator();
                String element = "";
                String trimmer = "";
                while (itr3.hasNext()) {
                    element = itr3.next().toString();
                    trimmer = element.trim();
                    trimmer = trimmer.replaceAll("[()]", "");
                    trimmer = trimmer.replaceAll("<http://purl.obolibrary.org/obo/", "");
                    trimmer = trimmer.replaceAll(">", "");
                    System.out.println("Intersection : " + trimmer);

                    if (this.ICmap.containsKey(trimmer.trim())) {
                        System.out.println("SuperClasses element : " + element
                                + " has IC val = " + this.ICmap.get(trimmer));
                        ic.add(this.ICmap.get(trimmer));
                    }
                }
                S2.clear();
                intersection.clear();
                System.out.println("ic size = " + ic.size());
            }

            if (ic.size() != 0) {
                /*Take max IC score to get the LCS because higher up the tree 
                 *the less the IC value, where the IC(root) = 0.
                 */
                Collections.sort(ic);
                for (int i = 0; i < ic.size(); i++) {
                    System.out.println("sorted IC list : " + ic.get(i));
                }
                int m = ic.size() - 1;
                aggregateIC.add(ic.get(m));
                System.out.println(ic.get(m) + " added max to aggregateIC");

                S1.clear();
                continue;
            }
            S1.clear();
            ic.clear();
        }

        //Presort before finding median.
        Collections.sort(aggregateIC);

        Iterator itr = aggregateIC.iterator();
        Object element = 0.00;
        int k = 0;
        while (itr.hasNext()) {
            element = itr.next();
            System.out.println("aggregateIC[" + k + "] = " + aggregateIC.get(k));
            k++;
        }
        System.out.print("\n");

        int size = aggregateIC.size();
        System.out.println("agregateIC size = " + size);
        int mod = size % 2;
        System.out.println("mod = " + mod);
        int e1 = 0;
        int e2 = 0;
        double median = 0.00;
        int index = 0;
        //odd index of 2k+1, where 2k like %2 and +1 like remainder 1
        if (mod == 1) {
            index = ((size + 1) / 2) - 1;
            median = aggregateIC.get(index);
            //even index of 2k, where 2k like %2 and +0 like remainder 0
        } else if (mod == 0) {
            e1 = (int) (Math.floor((size + 1) / 2) - 1);
            System.out.println("e1 = " + e1);
            e2 = (int) (Math.ceil((size + 1) / 2) - 1);
            System.out.println("e2 = " + e2);
            median = (aggregateIC.get(e1) + (aggregateIC.get(e2))) / 2;
        }
        System.out.println("--------------------------------------------------------");
        this.m1 = median;
        System.out.println("Median RESULT => " + median);
    }

    public void allPairsJaccard() {
        System.out.println("-------All Pairs : JACCARD-------------"
                + "");

        ArrayList<String> superClassesX = new ArrayList();
        ArrayList<String> superClassesY = new ArrayList();

        //S1, S2 for set operations, intersection
        Set<String> S1 = null;
        Set<String> S2 = null;

        Set<String> intersection = null;
        Set<String> union = null;

        //tempListX to store an "owl;Thing"-less copy of superClassesX list
        ArrayList<String> tempListX = null;
        String keyX = "";
        ArrayList<Double> semanticSimilarityList = new ArrayList();
        Double  semanticSimilarityScore = 0.00;
        ArrayList<Double> semanticSimilarityListFinal = null;

//------------------------------------------------------
        for (Entry<String, ArrayList<String>> e : this.superMapX.entrySet()) {
            //get key of x in X
            keyX = e.getKey();
            //get superclasses of x in X
            superClassesX = e.getValue();
            String strX = "";
            tempListX = new ArrayList();

            for (int i = 0; i < superClassesX.size(); i++) {
                //cleanup s1 in S1
                strX = superClassesX.get(i).trim().replaceAll("Node", "")
                        .replaceAll("[()]", "")
                        .replaceAll("<http://purl.obolibrary.org/obo/", "")
                        .replaceAll(">", "");
//                System.out.println("strX[] = " + strX);
                //remove the root from S1 'cause IC=0
                if (strX.equals("owl:Thing")) {
                    System.out.println("owl:Thing detected and ignored!");
                    continue;
                } else {
                    tempListX.add(strX);
                    System.out.println("X : " + strX + " added");
                }
            }
            //S1 as HashSet
            S1 = new HashSet(tempListX);

            //Repeat for y in Y
            String keyY = "";
            ArrayList tempListY = new ArrayList();
//            System.out.println("----------------------superMapY-----------------------------------------------------------------");
            for (Entry<String, ArrayList<String>> e2 : this.superMapY.entrySet()) {
                keyY = e2.getKey();
                superClassesY = e2.getValue();
                String strY = "";
                semanticSimilarityScore = 0.00;
                for (int i = 0; i < superClassesY.size(); i++) {
                    strY = superClassesY.get(i).trim().replaceAll("Node", "")
                            .replaceAll("[()]", "")
                            .replaceAll("<http://purl.obolibrary.org/obo/", "")
                            .replaceAll(">", "");
//                    System.out.println("strY[] = " + strY);
                    //remove the root 'cause IC=0
                    if (strY.equals("owl:Thing")) {
                        System.out.println("owl:Thing detected and ignored!");
                        continue;
                    } else {
                        System.out.println("Y : " + strY + " added");
                        tempListY.add(strY);
                    }
                }

                Iterator itr1 = S1.iterator();
                while (itr1.hasNext()) {
                    System.out.println("S1 " + itr1.next().toString());
                }
                String element1 = "";
                String trimmer1 = "";
                S2 = new HashSet(tempListY);
                tempListY.clear();
                Iterator itr2 = S2.iterator();
                while (itr2.hasNext()) {
                    element1 = itr2.next().toString();
                    trimmer1 = element1.trim();
                    trimmer1 = trimmer1.replaceAll("[()]", "");
                    trimmer1 = trimmer1.replaceAll("<http://purl.obolibrary.org/obo/", "");
                    trimmer1 = trimmer1.replaceAll(">", "");
//                    System.out.println("S2 " + trimmer1);
                }

                //S1 retains all from S2, intersection operation
                intersection = new HashSet(S1);
                intersection.retainAll(S2);
                //S1 add all from S2, union operation
                union = new HashSet(S1);
                union.addAll(S2);

                Iterator itr3 = intersection.iterator();
                Iterator itr4 = union.iterator();
                String element2 = "";
                String element = "";    
                String trimmer = "";
                while (itr3.hasNext()) {
                    element = itr3.next().toString();
                    trimmer = element.trim();
                    trimmer = trimmer.replaceAll("[()]", "");
                    trimmer = trimmer.replaceAll("<http://purl.obolibrary.org/obo/", "");
                    trimmer = trimmer.replaceAll(">", "");
                    System.out.println("Intersection : " + trimmer);
                }
                while(itr4.hasNext()){
                    element2 = itr4.next().toString().trim().replaceAll("[()]","")
                            .replaceAll("<http://purl.obolibrary.org/obo/", "")
                            .replaceAll(">","");
                    System.out.println("Union : " + element2);
                }
                System.out.println("size of intersection : " + intersection.size() + ", union size : " + union.size());
                double iSize = intersection.size();
                double uSize = union.size();
                System.out.println("i size is " + iSize + ", uSize is " + uSize);
                semanticSimilarityScore = iSize/uSize;
                
                System.out.println("Semantic similarity score : " +  semanticSimilarityScore);
                semanticSimilarityList.add(semanticSimilarityScore);
                S2.clear();
                intersection.clear();
                union.clear();
            }
            S1.clear();
        }
        

        //Presort before finding median.
        Collections.sort(semanticSimilarityList);
        for (int i = 0; i < semanticSimilarityList.size(); i++) {
            if(semanticSimilarityList.get(i).equals(0.0)){
                semanticSimilarityList.remove(i);
            }
        }
        Collections.sort(semanticSimilarityList);
        Iterator itr = semanticSimilarityList.iterator();
        Object element = 0.00;
        int k = 0;
        while (itr.hasNext()) {
            element = itr.next();
            System.out.println("semantic similarity score[" + k + "] = " + semanticSimilarityList.get(k));
            k++;
        }
        System.out.print("\n");

        int size = semanticSimilarityList.size();
        System.out.println("semanticSimilarityList size = " + size);
        int mod = size % 2;
        System.out.println("mod = " + mod);
        int e1 = 0;
        int e2 = 0;
        double median = 0.00;
        int index = 0;
        //odd index of 2k+1, where 2k like %2 and +1 like remainder 1
        if (mod == 1) {
            index = ((size + 1) / 2) - 1;
            median = semanticSimilarityList.get(index);
            //even index of 2k, where 2k like %2 and +0 like remainder 0
        } else if (mod == 0) {
            e1 = (int) (Math.floor((size-1.0)/2.0));
            System.out.println("e1 = " + e1);
            e2 = (int) (Math.ceil((size-1.0)/2.0));
//            System.out.println("e2 = " + e2);
//            System.out.println("at e1 : " + semanticSimilarityList.get(e1) + ", at e2 : " + semanticSimilarityList.get(e2));
            median = (semanticSimilarityList.get(e1) + (semanticSimilarityList.get(e2))) / 2;
        }
        System.out.println("--------------------------------------------------------");
        System.out.println("Median RESULT => " + median);
        this.m2 = median;
    }

    public static void main(String[] args) throws OWLOntologyCreationException, IOException, OWLOntologyStorageException {
        String gene1 = "CREB3L1";
        String gene2 = "RPS11";

        Assignment1 assignment = new Assignment1();
        assignment.loadOntologyManager();

        System.out.println("IRI = " + assignment.getOntologyIRI().toString());

        assignment.initMapFromFile();
        assignment.initializeNewOntology();
        assignment.initializeSuperClasses(gene1, gene2);
        assignment.initializeInfoContentMap(gene1, gene2);
        assignment.allPairsResnik();
//        assignment.printIcMapToFile();
        assignment.allPairsJaccard();
        assignment.printM1ToFile();
        assignment.printM2ToFile();
    }
}
