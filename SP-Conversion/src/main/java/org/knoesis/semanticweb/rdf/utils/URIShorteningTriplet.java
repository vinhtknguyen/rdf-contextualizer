package org.knoesis.semanticweb.rdf.utils;

public class URIShorteningTriplet {
	private final String shortenURI;
    private final String prefix;
    private final String namespace;

    public static URIShorteningTriplet createTriplet(String element0, String element1, String element2) {
        return new URIShorteningTriplet(element0, element1, element2);
    }

    public URIShorteningTriplet(String element0, String element1, String element2) {
        this.shortenURI = element0;
        this.prefix = element1;
        this.namespace = element2;
    }

    public String getShortenURI() {
        return this.shortenURI;
    }

    public String getPrefix() {
        return this.prefix;
    }
    
    public String getNamespace(){
    	return this.namespace;
    }
}
