package org.knoesis.rdf.sp.parser;


import java.io.BufferedWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.jena.graph.Node;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.lang.PipedQuadsStream;
import org.apache.jena.riot.lang.PipedRDFIterator;
import org.apache.jena.riot.lang.PipedRDFStream;
import org.apache.jena.sparql.core.Quad;
import org.apache.log4j.Logger;
import org.knoesis.rdf.sp.converter.ContextualRepresentationConverter;
import org.knoesis.rdf.sp.inference.ContextualInference;
import org.knoesis.rdf.sp.model.SPTriple;
import org.knoesis.rdf.sp.utils.RDFWriteUtils;

public class QuadParser implements Parser{
	
	final static Logger logger = Logger.getLogger(QuadParser.class);


	protected PipedRDFIterator<Quad> iter;
	protected PipedRDFStream<Quad> inputStream;
	
	public QuadParser(){
		
	}
	
	
	public Node[] next(){
		Quad quad = (Quad) this.iter.next();
		return new Node[] {quad.getSubject(), quad.getPredicate(), quad.getObject(), quad.getGraph()};
	}
	
	public boolean hasNext(){
		return this.iter.hasNext();
	}


	public void parse(String in) {
		// TODO Auto-generated method stub

	}


	@Override
	public void parse(ContextualRepresentationConverter con, String in, BufferedWriter writer,
			String ext) {
        iter = new PipedRDFIterator<Quad>();
        inputStream = new PipedQuadsStream(iter);
        // PipedRDFStream and PipedRDFIterator need to be on different threads
        ExecutorService executor = Executors.newSingleThreadExecutor();

        // Create a runnable for our parser thread
        Runnable parser = new Runnable() {

            @Override
            public void run() {
                // Call the parsing process.
                RDFDataMgr.parse(inputStream, in, Lang.NQUADS);
            }
        };

        // Start the parser on another thread
        executor.submit(parser);
        Quad quad;
        List<SPTriple> triples = new LinkedList<SPTriple>();
		try {
	        while (iter.hasNext()) {
	            quad = iter.next();
				triples.addAll(con.transformQuad(quad, ext));
				if (con.isInfer()){
					// infer new triples and add them to the list
					ContextualInference inference = new ContextualInference();
					inference.loadModel(con.getOntoDir());
					triples.addAll(inference.infer(triples));
				}
				writer.write(RDFWriteUtils.printTriples(triples,ext));
				triples.clear();
	        }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error(e);
		}
    
        executor.shutdown();
        iter.close();
        inputStream.finish();
	}

	
}
