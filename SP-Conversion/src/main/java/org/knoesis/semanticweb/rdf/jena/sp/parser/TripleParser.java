package org.knoesis.semanticweb.rdf.jena.sp.parser;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.lang.PipedRDFIterator;
import org.apache.jena.riot.lang.PipedRDFStream;
import org.apache.jena.riot.lang.PipedTriplesStream;
import org.apache.log4j.Logger;
import org.knoesis.semanticweb.rdf.jena.sp.converter.ContextualRepresentationConverter;

public class TripleParser implements Parser {
	
	final static Logger logger = Logger.getLogger(TripleParser.class);


    PipedRDFIterator<Triple> iter;
    PipedRDFStream<Triple> inputStream;

	public TripleParser(){
        iter = new PipedRDFIterator<Triple>();
        inputStream = new PipedTriplesStream(iter);
	}
	
	public Node[] next(){
		Triple triple = (Triple) this.iter.next();
		return new Node[] {triple.getSubject(), triple.getPredicate(), triple.getObject()};
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
        
        Triple triple;
        Node[] nodes;
        while (iter.hasNext()) {
        	triple = iter.next();
            nodes = new Node[] {triple.getSubject(), triple.getPredicate(), triple.getObject()};
			try {
				writer.write(con.transform(nodes, ext));
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
