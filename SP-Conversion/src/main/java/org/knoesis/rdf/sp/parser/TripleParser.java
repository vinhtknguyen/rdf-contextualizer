package org.knoesis.rdf.sp.parser;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.lang.PipedRDFIterator;
import org.apache.jena.riot.lang.PipedRDFStream;
import org.apache.jena.riot.lang.PipedTriplesStream;
import org.apache.log4j.Logger;
import org.knoesis.rdf.sp.converter.ContextualRepresentationConverter;
import org.knoesis.rdf.sp.model.SPTriple;
import org.knoesis.rdf.sp.utils.RDFWriteUtils;

public class TripleParser implements Parser {
	
	final static Logger logger = Logger.getLogger(TripleParser.class);

    PipedRDFIterator<org.apache.jena.graph.Triple> iter = null;
    PipedRDFStream<org.apache.jena.graph.Triple> inputStream = null;

	public TripleParser(){
        iter = new PipedRDFIterator<org.apache.jena.graph.Triple>();
        inputStream = new PipedTriplesStream(iter);
	}
	
	public boolean hasNext(){
		
		return this.iter.hasNext();
		
	}

	@Override
	public void parse(ContextualRepresentationConverter con, String in,
			BufferedWriter writer, String ext) {
        // PipedRDFStream and PipedRDFIterator need to be on different threads
        ExecutorService executor = Executors.newSingleThreadExecutor();

        // Create a runnable for our parser thread
        Runnable parser = new Runnable() {

            @Override
            public void run() {
                // Call the parsing process.
                RDFDataMgr.parse(inputStream, in, null);
            }
        };

        // Start the parser on another thread
        executor.submit(parser);
        
        List<SPTriple> triples = new LinkedList<SPTriple>();
        org.apache.jena.graph.Triple triple;
        while (iter.hasNext()) {
        	triple = iter.next();
			try {
				triples.addAll(con.transformTriple(triple, ext));
				if (con.isInfer()){
					// infer new triples and add them to the list
					
				}
				writer.write(RDFWriteUtils.printTriples(triples,ext));
				triples.clear();
			} catch (IOException e) {
				logger.error(e);
			}
            // Do something with each triple
        }
        iter.close();
        inputStream.finish();
        executor.shutdown();
		
	}

}
