package org.knoesis.rdf.sp.parser;


import org.apache.jena.riot.lang.PipedQuadsStream;
import org.apache.jena.riot.lang.PipedRDFIterator;
import org.apache.jena.riot.lang.PipedRDFStream;
import org.apache.jena.sparql.core.Quad;
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
import org.knoesis.rdf.sp.utils.RDFWriteUtils;
import org.knoesis.rdf.sp.utils.Reporter;

public class QuadParser extends SPParser{
	
	final static Logger logger = Logger.getLogger(QuadParser.class);

	public QuadParser() {
		super();
	}

	public QuadParser(long _uuidInitNum, String _uuidInitStr) {
		super(_uuidInitNum, _uuidInitStr);
	}

	
	@Override
	public void parseFile(String filein, String ext, String rep, String fileout) {
		// Check the condition before submitting new tasks
        while (!canSubmitTask(futureParserList)) {
        	// Wait until the parser and writer of the same task finish, then start a new task
        	checkFutures(futureConverterList);
        }

        PipedRDFIterator<Quad> processorIter = new PipedRDFIterator<Quad>(bufferSizeStream, true);
		final PipedRDFStream<Quad> processorInputStream = new PipedQuadsStream(processorIter);

		final String filename = RDFWriteUtils.getPrettyName(filein);

		Reporter reporter = new Reporter(rep, infer, ext, filename, filein, fileout, dsName, this.shortenURI, zip, bufferSizeWriter);
		
		// Start the parser
		CallableParser<Quad> parser = new CallableParser<Quad>(processorInputStream, reporter);
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

    		CallableConverter<Quad> converter = new CallableConverter<Quad>(processor, processorIter, converterInputStream, reporter);
            CallableTransformer transformer = new CallableTransformer(transformerInputStream, converterIter, reporter);
            CallableWriter writer = new CallableWriter(transformerIter, reporter);
            
    		futureConverterList.add(converterExecutor.submit(converter));
    		futureConverterList.add(converterExecutor.submit(transformer));
    		futureConverterList.add(converterExecutor.submit(writer));
    	}

	}
	
}
