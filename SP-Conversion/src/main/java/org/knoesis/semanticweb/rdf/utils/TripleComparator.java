package org.knoesis.semanticweb.rdf.utils;

import java.util.Comparator;
import org.knoesis.semanticweb.rdf.sp.model.SPTriple;

public class TripleComparator implements Comparator<SPTriple> {

	@Override
	public int compare(SPTriple triple1, SPTriple triple2) {
		// TODO Auto-generated method stub
		return triple1.toString().compareTo(triple2.toString());
	}
}