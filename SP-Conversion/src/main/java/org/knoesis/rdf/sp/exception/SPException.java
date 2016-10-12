package org.knoesis.rdf.sp.exception;

import org.apache.jena.shared.JenaException ;

public class SPException extends JenaException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public SPException()                          { super() ; }
    public SPException(String msg)                { super(msg) ; }
    public SPException(Throwable th)              { super(th) ; }
    public SPException(String msg, Throwable th)  { super(msg, th) ; }
}
