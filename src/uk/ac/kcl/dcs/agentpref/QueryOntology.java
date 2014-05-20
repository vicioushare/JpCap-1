package uk.ac.kcl.dcs.agentpref;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import org.coode.owlapi.manchesterowlsyntax.ManchesterOWLSyntaxEditorParser;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.expression.OWLEntityChecker;
import org.semanticweb.owlapi.expression.ParserException;
import org.semanticweb.owlapi.expression.ShortFormEntityChecker;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.util.BidirectionalShortFormProvider;
import org.semanticweb.owlapi.util.BidirectionalShortFormProviderAdapter;
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;

public class QueryOntology {

	private ArrayList<String> currentQueryResults = new ArrayList<String>();
	
	public QueryOntology(String ontologyIRI) { this.ontologyIRI = ontologyIRI; }
	
	//////////////////////////////////// Modified Library 
	
	OWLReasoner reasoner;
	SimpleShortFormProvider shortFormProvider = new SimpleShortFormProvider();
	String ontologyIRI;
	
	void loadOntology() {
			
		try {
         
            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
           
            File file = new File(ontologyIRI);
          
            OWLOntology ontology = manager.loadOntologyFromOntologyDocument(file);
            
       
          
            reasoner = createReasoner(ontology);
            
        } catch (OWLOntologyCreationException e) {
            System.out.println("Could not load ontology: " + e.getMessage());
        } 
			
	}
	
    public ArrayList<String> askQuery(String classExpression) {
    	
    	DLQueryEngine dlQueryEngine = new DLQueryEngine(reasoner, shortFormProvider);
    	currentQueryResults.clear();
    	
        if (classExpression.length() == 0) {
        	
            System.out.println("No class expression specified");
        
        } else {
            
        	try {
        		
        		//Set<OWLClass> superClasses = dlQueryEngine.getSuperClasses(
                //classExpression, false);
		        //formatReturn("SuperClasses", superClasses);
		        //Set<OWLClass> equivalentClasses = dlQueryEngine
		                //.getEquivalentClasses(classExpression);
		        //formatReturn("EquivalentClasses", equivalentClasses);
               
                Set<OWLClass> subClasses = dlQueryEngine.getSubClasses(classExpression,
                        false);
                formatReturn("SubClasses", subClasses);
                //Set<OWLNamedIndividual> individuals = dlQueryEngine.getInstances(
                        //classExpression, false);
                //formatReturn("Instances", individuals);
               
             
            } catch (ParserException e) {
            	
                System.out.println(e.getMessage());
            
            }
        
        }
        
        return currentQueryResults;
        
    }
    
    private void formatReturn(String name, Set<? extends OWLEntity> entities) {
    	
        if (!entities.isEmpty()) {
        	
            for (OWLEntity entity : entities) {
            
            	if (!(entity.toString().equals("owl:Nothing"))) {
            	
            		currentQueryResults.add(shortFormProvider.getShortForm(entity));
            	
            	}
            	
            }
            
        } 
        
    }
    
    
    //////////////////////////////////// Unmodified Library 
	
	/** This example shows how to perform a "dlquery". The DLQuery view/tab in
	 * Protege 4 works like this. */
	class DLQueryEngine {
	    private OWLReasoner reasoner;
	    private DLQueryParser parser;

	    /** Constructs a DLQueryEngine. This will answer "DL queries" using the
	     * specified reasoner. A short form provider specifies how entities are
	     * rendered.
	     * 
	     * @param reasoner
	     *            The reasoner to be used for answering the queries.
	     * @param shortFormProvider
	     *            A short form provider. */
	    public DLQueryEngine(OWLReasoner reasoner, ShortFormProvider shortFormProvider) {
	        this.reasoner = reasoner;
	        OWLOntology rootOntology = reasoner.getRootOntology();
	        parser = new DLQueryParser(rootOntology, shortFormProvider);
	    }

	    /** Gets the superclasses of a class expression parsed from a string.
	     * 
	     * @param classExpressionString
	     *            The string from which the class expression will be parsed.
	     * @param direct
	     *            Specifies whether direct superclasses should be returned or
	     *            not.
	     * @return The superclasses of the specified class expression
	     * @throws ParserException
	     *             If there was a problem parsing the class expression. */
	    public Set<OWLClass> getSuperClasses(String classExpressionString, boolean direct)
	            throws ParserException {
	        if (classExpressionString.trim().length() == 0) {
	            return Collections.emptySet();
	        }
	        OWLClassExpression classExpression = parser
	                .parseClassExpression(classExpressionString);
	        
	        NodeSet<OWLClass> superClasses = reasoner
	                .getSuperClasses(classExpression, direct);
	        
	        return superClasses.getFlattened();
	    }

	    /** Gets the equivalent classes of a class expression parsed from a string.
	     * 
	     * @param classExpressionString
	     *            The string from which the class expression will be parsed.
	     * @return The equivalent classes of the specified class expression
	     * @throws ParserException
	     *             If there was a problem parsing the class expression. */
	    public Set<OWLClass> getEquivalentClasses(String classExpressionString)
	            throws ParserException {
	        if (classExpressionString.trim().length() == 0) {
	            return Collections.emptySet();
	        }
	        OWLClassExpression classExpression = parser
	                .parseClassExpression(classExpressionString);
	        
	        Node<OWLClass> equivalentClasses = reasoner.getEquivalentClasses(classExpression);
	        Set<OWLClass> result;
	        if (classExpression.isAnonymous()) {
	            result = equivalentClasses.getEntities();
	        } else {
	            result = equivalentClasses.getEntitiesMinus(classExpression.asOWLClass());
	        }
	        return result;
	    }

	    /** Gets the subclasses of a class expression parsed from a string.
	     * 
	     * @param classExpressionString
	     *            The string from which the class expression will be parsed.
	     * @param direct
	     *            Specifies whether direct subclasses should be returned or not.
	     * @return The subclasses of the specified class expression
	     * @throws ParserException
	     *             If there was a problem parsing the class expression. */
	    public Set<OWLClass> getSubClasses(String classExpressionString, boolean direct)
	            throws ParserException {
	        if (classExpressionString.trim().length() == 0) {
	            return Collections.emptySet();
	        }
	        OWLClassExpression classExpression = parser
	                .parseClassExpression(classExpressionString);
	        NodeSet<OWLClass> subClasses = reasoner.getSubClasses(classExpression, direct);
	        return subClasses.getFlattened();
	    }

	    /** Gets the instances of a class expression parsed from a string.
	     * 
	     * @param classExpressionString
	     *            The string from which the class expression will be parsed.
	     * @param direct
	     *            Specifies whether direct instances should be returned or not.
	     * @return The instances of the specified class expression
	     * @throws ParserException
	     *             If there was a problem parsing the class expression. */
	    public Set<OWLNamedIndividual> getInstances(String classExpressionString,
	            boolean direct) throws ParserException {
	        if (classExpressionString.trim().length() == 0) {
	            return Collections.emptySet();
	        }
	        OWLClassExpression classExpression = parser
	                .parseClassExpression(classExpressionString);
	        NodeSet<OWLNamedIndividual> individuals = reasoner.getInstances(classExpression,
	                direct);
	        return individuals.getFlattened();
	    }
	}
	
	private static OWLReasoner createReasoner(OWLOntology rootOntology) {
        // We need to create an instance of OWLReasoner. An OWLReasoner provides
        // the basic query functionality that we need, for example the ability
        // obtain the subclasses of a class etc. To do this we use a reasoner
        // factory.
        // Create a reasoner factory.
        OWLReasonerFactory reasonerFactory = new Reasoner.ReasonerFactory();
    	return reasonerFactory.createReasoner(rootOntology);
    }
	
	class DLQueryParser {
	    private OWLOntology rootOntology;
	    private BidirectionalShortFormProvider bidiShortFormProvider;

	    /** Constructs a DLQueryParser using the specified ontology and short form
	     * provider to map entity IRIs to short names.
	     * 
	     * @param rootOntology
	     *            The root ontology. This essentially provides the domain
	     *            vocabulary for the query.
	     * @param shortFormProvider
	     *            A short form provider to be used for mapping back and forth
	     *            between entities and their short names (renderings). */
	    public DLQueryParser(OWLOntology rootOntology, ShortFormProvider shortFormProvider) {
	        this.rootOntology = rootOntology;
	        OWLOntologyManager manager = rootOntology.getOWLOntologyManager();
	        Set<OWLOntology> importsClosure = rootOntology.getImportsClosure();
	        // Create a bidirectional short form provider to do the actual mapping.
	        // It will generate names using the input
	        // short form provider.
	        bidiShortFormProvider = new BidirectionalShortFormProviderAdapter(manager,
	                importsClosure, shortFormProvider);
	    }

	    /** Parses a class expression string to obtain a class expression.
	     * 
	     * @param classExpressionString
	     *            The class expression string
	     * @return The corresponding class expression
	     * @throws ParserException
	     *             if the class expression string is malformed or contains
	     *             unknown entity names. */
	    public OWLClassExpression parseClassExpression(String classExpressionString)
	            throws ParserException {
	        OWLDataFactory dataFactory = rootOntology.getOWLOntologyManager()
	                .getOWLDataFactory();
	        // Set up the real parser
	        ManchesterOWLSyntaxEditorParser parser = new ManchesterOWLSyntaxEditorParser(
	                dataFactory, classExpressionString);
	        parser.setDefaultOntology(rootOntology);
	        // Specify an entity checker that wil be used to check a class
	        // expression contains the correct names.
	        OWLEntityChecker entityChecker = new ShortFormEntityChecker(bidiShortFormProvider);
	        parser.setOWLEntityChecker(entityChecker);
	        // Do the actual parsing
	        return parser.parseClassExpression();
	    }
	    
	}
	
}