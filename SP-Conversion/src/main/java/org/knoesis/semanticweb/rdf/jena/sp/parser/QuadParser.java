package org.knoesis.semanticweb.rdf.jena.sp.parser;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
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
import org.knoesis.semanticweb.rdf.jena.sp.converter.ContextualRepresentationConverter;

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
	public void parse(ContextualRepresentationConverter con, String in, String fileOut,
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
        Node[] nodes;
        while (iter.hasNext()) {
            quad = iter.next();
            nodes = new Node[] {quad.getSubject(), quad.getPredicate(), quad.getObject(), quad.getGraph()};
			try {
				Files.write(Paths.get(fileOut), con.transform(nodes, ext).getBytes(), StandardOpenOption.APPEND);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.error(e);
			}
            // Do something with each triple
        }
        executor.shutdown();
        iter.close();
        inputStream.finish();
        logger.debug("Finished generating file " + fileOut);
	}

	
}
