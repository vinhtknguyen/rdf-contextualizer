package org.knoesis.rdf.sp.parser;

import org.apache.jena.graph.Triple;
import org.apache.jena.riot.lang.PipedRDFIterator;
import org.apache.jena.riot.lang.PipedRDFStream;
import org.apache.jena.riot.lang.PipedTriplesStream;
import org.apache.log4j.Logger;
import org.knoesis.rdf.sp.concurrent.PipedNodesIterator;
import org.knoesis.rdf.sp.concurrent.PipedNodesStream;
import org.knoesis.rdf.sp.concurrent.PipedSPTripleIterator;
import org.knoesis.rdf.sp.concurrent.PipedSPTripleStream;
import org.knoesis.rdf.sp.model.SPTriple;
import org.knoesis.rdf.sp.runnable.CallableConverter;
import org.knoesis.rdf.sp.runnable.CallableParser;
import org.knoesis.rdf.sp.runnable.CallableTransformer;
import org.knoesis.rdf.sp.runnable.CallableWriter;
import org.knoesis.rdf.sp.runnable.SPProcessor;
import org.knoesis.rdf.sp.utils.Constants;
import org.knoesis.rdf.sp.utils.RDFWriteUtils;
import org.knoesis.rdf.sp.utils.Reporter;

public class TripleParser extends SPParser {
	
	final static Logger logger = Logger.getLogger(TripleParser.class);

	public TripleParser() {
	}

	public TripleParser(long uuidInitNum, String _uuidInitStr) {
		super(uuidInitNum, _uuidInitStr);
	}

	@Override
	public void parseFile(String filein, String ext, String rep, String fileout) {
		// Check the condition before submitting new tasks
        while (!canSubmitTask(futureParserList)) {
        	// Wait until the parser and writer of the same task finish, then start a new task
        	checkFutures(futureConverterList);
       }
		// PipedRDFStream and PipedRDFIterator need to be on different threads
		PipedRDFIterator<Triple> processorIter = new PipedRDFIterator<Triple>(Constants.BUFFER_SIZE_STREAM, true);
		final PipedTriplesStream processorInputStream = new PipedTriplesStream(processorIter);

		final String filename = RDFWriteUtils.getPrettyName(filein);

		Reporter reporter = new Reporter(rep, infer, ext, filename, filein, fileout, dsName, shortenURI, zip, bufferSizeWriter);
		
		// Start the parser
		CallableParser<Triple> parser = new CallableParser<Triple>(processorInputStream, reporter);
    	futureParserList.add(parserExecutor.submit(parser));

    	int round = findConverterNum(filein);
    	
    	for (int i = 0; i < round; i++){
    		
            PipedRDFIterator<SPTriple> converterIter = new PipedSPTripleIterator(bufferSizeStream, true);
    		PipedRDFStream<SPTriple> converterInputStream = new PipedSPTripleStream(converterIter);
    		PipedRDFIterator<String> transformerIter = new PipedNodesIterator(bufferSizeStream, true);
    		PipedRDFStream<String> transformerInputStream = new PipedNodesStream(transformerIter);

    		SPProcessor processor = new SPProcessor(rep, uuidInitNum, uuidInitStr);
    		processor.setExt(ext);
    		processor.setIsinfer(infer);
    		processor.setOntoDir(ontoDir);
    		processor.setDsName(dsName);
    		processor.setShortenURI(shortenURI);
    		processor.start();

    		CallableConverter<Triple> converter = new CallableConverter<Triple>(processor, processorIter, converterInputStream, reporter);
            CallableTransformer transformer = new CallableTransformer(transformerInputStream, converterIter, reporter);
            CallableWriter writer = new CallableWriter(transformerIter, reporter);
            
    		futureConverterList.add(converterExecutor.submit(converter));
    		futureConverterList.add(converterExecutor.submit(transformer));
    		futureConverterList.add(converterExecutor.submit(writer));
    	}

	}
}
