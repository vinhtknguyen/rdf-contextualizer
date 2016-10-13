package org.knoesis.rdf.sp.supplier;
import org.apache.jena.graph.Triple;
import org.apache.jena.riot.RiotException;
import org.apache.jena.sparql.core.Quad;
import org.knoesis.rdf.sp.exception.SPException;
import org.knoesis.rdf.sp.parser.ParserElement;
import org.knoesis.rdf.sp.parser.Reporter;
import org.knoesis.rdf.sp.parser.SPAnalyzer;
import org.knoesis.rdf.sp.parser.SPDataStatElement;
import org.knoesis.rdf.sp.pipeline.PipedQuadTripleIterator;
import org.knoesis.rdf.sp.pipeline.PipedSPTripleStream;
import org.knoesis.rdf.sp.utils.Constants;

import com.romix.scala.collection.concurrent.TrieMap;

import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Supplier;

public class SupplierAnalyzer implements Supplier<ParserElement>{

    PipedQuadTripleIterator processorIter;
	PipedSPTripleStream converterInputStream;
	
	ParserElement element;
	Reporter reporter;
	long countItem;
	long countSingletonProp;
	TrieMap<String, Long> genericPropMap;
	String dsType;

	public SupplierAnalyzer(PipedQuadTripleIterator processorIter , ParserElement element, Reporter reporter) {
		super();
		this.processorIter = processorIter;
		this.element = element;
		this.reporter = reporter;
		countItem = 0;
		countSingletonProp = 0;
		genericPropMap = new TrieMap<String,Long>();
		dsType = reporter.isInfer()?Constants.DS_TYPE_SPR:Constants.DS_TYPE_SP;
	}

	@Override
	public ParserElement get() {
		long start = System.currentTimeMillis();
    	reporter.reportStartStatus(element, Constants.PROCESSING_TASK_ANALYZE);
		Triple triple = null;
		
		try{
			System.out.println(element.getFilein());
			while (processorIter.hasNext()){
				// Put the output to the writerInputStream
				Object obj = processorIter.next();
		
				if (obj instanceof Quad){
					countItem++;
					
				} else if (obj instanceof Triple){
					triple = (Triple) obj;
					if (triple != null){
						countItem++;
						// If predicate is singletonPropertyOf, increase countSingletonProperty
						if (triple.getPredicate().toString().equals(Constants.SINGLETON_PROPERTY_OF)){
							countSingletonProp++;
						}
						// if predicate is rdf:type and object is rdf:GenericProperty, increase countGenericProperty
						if (triple.getPredicate().toString().equals(Constants.RDF_TYPE) && triple.getObject().toString().equals(Constants.RDF_GENERIC_PROPERTY_CLASS)){
							Long num = genericPropMap.get(triple.getSubject().toString());
							if (num != null){
								num += 1L;
							} else {
								num = Long.valueOf(1L);
							}
							genericPropMap.put(triple.getSubject().toString(), num);
						}
						
					}
				}
			}
		} catch (RiotException e){
			throw new SPException("File " + element.getFilein() + ": encounter streaming exception in analyzer", e);
		} finally{
			processorIter.close();
		}
		// Count distinct generic prop and their total singleton props
		
		long countGenericProp = 0;
		long totalSingInstantiation = 0;
		
		Iterator<Entry<String, Long>> it = genericPropMap.entrySet().iterator();
		while (it.hasNext()) {
		    Map.Entry<String,Long> pair = (Map.Entry<String,Long>)it.next();
		    countGenericProp++;
		    totalSingInstantiation += (long) pair.getValue();
//		    System.out.println("generic " + pair.getKey() + ": " + countGenericProp + " vs " + totalSingProp);
		}
		SPAnalyzer.mergeGenericProp(genericPropMap);
		
		double average = (countGenericProp > 0)?(double)totalSingInstantiation/countGenericProp:0;
		SPDataStatElement statElement = new SPDataStatElement(reporter.getDsName(), dsType, element.getFilein());
		statElement.setCountItem(countItem);
		statElement.setTotalSingletonInstantiation(totalSingInstantiation);
		statElement.setCountSingletonProp(countSingletonProp);
		statElement.setCountGenericProp(countGenericProp);
		statElement.setAverageSingsPerGeneric(average);
		statElement.setFilename(element.getFilein());
		statElement.setDiskspace(Paths.get(element.getFilein()).toFile().length());
		
		// Put data to the accumulator
		SPAnalyzer.putStatElement(element.getFilein(), statElement);
		
		reporter.reportSystem(start, element, Constants.PROCESSING_TASK_ANALYZE);
		reporter.reportData(start, reporter.getDsName(), dsType, element.getFilein(), countItem, countSingletonProp, totalSingInstantiation, countGenericProp, average);
		
		return element;
	}

}
