package com.irisa.formulis.control;

import java.sql.Time;
import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.irisa.formulis.model.basic.Keyword;
import com.irisa.formulis.model.basic.URI;
import com.irisa.formulis.model.basic.URI.KIND;
import com.irisa.formulis.view.AbstractDataWidget;
import com.irisa.formulis.view.create.variable.DateCreateWidget;
import com.irisa.formulis.view.create.variable.DateTimeCreateWidget;
import com.irisa.formulis.view.create.variable.NumericCreateWidget;
import com.irisa.formulis.view.create.variable.TextCreateWidget;

public class ControlUtils {
	
	public enum LOG_LEVEL {
		TRACE,
		DEBUG,
		/**
		 * Aucun message ne sera affiché
		 */
		NOPE
	}
	public static LOG_LEVEL logLevel = LOG_LEVEL.DEBUG;

	public enum LITTERAL_URIS {
		xsdString("http://www.w3.org/2001/XMLSchema#string"),
		xsdInteger("http://www.w3.org/2001/XMLSchema#integer"),
		xsdDouble("http://www.w3.org/2001/XMLSchema#double"),
		xsdDate("http://www.w3.org/2001/XMLSchema#date"),
		xsdDatetime("http://www.w3.org/2001/XMLSchema#dateTime"),
		xsdTime("http://www.w3.org/2001/XMLSchema#time"),
		xsdBoolean("http://www.w3.org/2001/XMLSchema#boolean"),
		xsdYear("http://www.w3.org/2001/XMLSchema#gYear"),
		xsdMonth("http://www.w3.org/2001/XMLSchema#gMonth"),
		xsdMonthDay("http://www.w3.org/2001/XMLSchema#gMonthDay"),
		xsdYearMonth("http://www.w3.org/2001/XMLSchema#gYearMonth"),
		xsdDay("http://www.w3.org/2001/XMLSchema#gDay")
		;
	
		private String uri;
	
		private LITTERAL_URIS(String u) {
			uri = u;
		}
	
		public final String getUri() {
			return uri;
		}
	
		static public boolean isLitteralType(String u) {
			for(int i = 0; i < LITTERAL_URIS.values().length; i++) {
				if(LITTERAL_URIS.values()[i].getUri().equals(u)) {
					return true;
				}
			}
			return false;
		}
	
		static public AbstractDataWidget getCorrespondingCreateWidget(LITTERAL_URIS uri) {
			AbstractDataWidget result = null;
			switch(uri) {
			case xsdString:
				result = new TextCreateWidget(null);
				break;
			case xsdInteger:
				result = new NumericCreateWidget(null);
				break;
			case xsdBoolean:
				break;
			case xsdDate:
			case xsdYearMonth:
			case xsdYear:
			case xsdMonth:
			case xsdDay:
			case xsdMonthDay:
				result = new DateCreateWidget(null);
				break;
			case xsdDatetime:
				result = new DateTimeCreateWidget(null);
				break;
			case xsdTime:
				break;
			default:
				break;
			}
			return result;
		}
	}

	public enum FORBIDDEN_URIS {
		rdfType("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"),
		rdfProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#Property"),
		rdfList("http://www.w3.org/1999/02/22-rdf-syntax-ns#List"),
		rdfStatement("http://www.w3.org/1999/02/22-rdf-syntax-ns#Statement"),
		rdfXMLLiteral("http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral"),
		rdfValue("http://www.w3.org/1999/02/22-rdf-syntax-ns#value"),
		rdfsLabel("http://www.w3.org/2000/01/rdf-schema#label"),
		rdfsClass("http://www.w3.org/2000/01/rdf-schema#Class"),
		rdfsSubClass("http://www.w3.org/2000/01/rdf-schema#subClassOf"),
		rdfsDomain("http://www.w3.org/2000/01/rdf-schema#domain"),
		rdfsRange("http://www.w3.org/2000/01/rdf-schema#range"),
		rdfsSubpropertyof("http://www.w3.org/2000/01/rdf-schema#subPropertyOf"),
		rdfsSeeAlso("http://www.w3.org/2000/01/rdf-schema#seeAlso"),
		rdfsIsDefinedBy("http://www.w3.org/2000/01/rdf-schema#isDefinedBy"),
		rdfsContainer("http://www.w3.org/2000/01/rdf-schema#Container"),
		rdfsLiteral("http://www.w3.org/2000/01/rdf-schema#Literal"),
		lispqlInverselabel("http://www.irisa.fr/LIS/ferre/RDFS/lisql#inverseLabel"),
		lispqlAssertion("http://www.irisa.fr/LIS/ferre/RDFS/lisql#Assertion"),
		termArity("http://www.irisa.fr/LIS/ferre/RDFS/term#arity"),
		termFunctor("http://www.irisa.fr/LIS/ferre/RDFS/term#Functor"),
		termFunctorarg1("http://www.irisa.fr/LIS/ferre/RDFS/term#functorArg1"),
		termFunctorarg2("http://www.irisa.fr/LIS/ferre/RDFS/term#functorArg2"),
		termFunctorarg3("http://www.irisa.fr/LIS/ferre/RDFS/term#functorArg3"),
		termTerm("http://www.irisa.fr/LIS/ferre/RDFS/term#Term"),
		owlClass("http://www.w3.org/2002/07/owl#Class"),
		owlDatatypeProperty("http://www.w3.org/2002/07/owl#DatatypeProperty"),
		owlObjectProperty("http://www.w3.org/2002/07/owl#ObjectProperty"),
		owlNamedIndividual("http://www.w3.org/2002/07/owl#NamedIndividual"),
		owlOntology("http://www.w3.org/2002/07/owl#Ontology"),
		owlEquivalentClass("http://www.w3.org/2002/07/owl#equivalentClass"),
		owlDisjointWith("http://www.w3.org/2002/07/owl#disjointWith"),
		owlEquivalentProperty("http://www.w3.org/2002/07/owl#equivalentProperty"),
		owlInverseOf("http://www.w3.org/2002/07/owl#inverseOf"),
		owlFunctionalProperty("http://www.w3.org/2002/07/owl#FunctionalProperty"),
		owlInverseFunctionalProperty("http://www.w3.org/2002/07/owl#InverseFunctionalProperty"),
		owlTransitiveProperty("http://www.w3.org/2002/07/owl#TransitiveProperty"),
		owlSymmetricProperty("http://www.w3.org/2002/07/owl#SymmetricProperty"),
		owlDameAs("http://www.w3.org/2002/07/owl#sameAs"),
		owlDifferentFrom("http://www.w3.org/2002/07/owl#differentFrom"),
		owlAllDifferent("http://www.w3.org/2002/07/owl#AllDifferent"),
		owlIntersectionOf("http://www.w3.org/2002/07/owl#intersectionOf"),
		owlUnionOf("http://www.w3.org/2002/07/owl#unionOf"),
		owlComplementOf("http://www.w3.org/2002/07/owl#complementOf"),
		owlAnnotationProperty("http://www.w3.org/2002/07/owl#AnnotationProperty"),
		owlRestriction("http://www.w3.org/2002/07/owl#Restriction"),
		owlVersionInfo("http://www.w3.org/2002/07/owl#versionInfo"),
		xsdInteger("http://www.w3.org/2001/XMLSchema#integer"),
		xsdString("http://www.w3.org/2001/XMLSchema#string"),
		xsdFloat("http://www.w3.org/2001/XMLSchema#float"),
		xsdNonNegativeInteger("http://www.w3.org/2001/XMLSchema#nonNegativeInteger"),
		xsdPositiveInteger("http://www.w3.org/2001/XMLSchema#positiveInteger"),
		xsdUnsignedInt("http://www.w3.org/2001/XMLSchema#unsignedInt"),
		xsdUnsignedShort("http://www.w3.org/2001/XMLSchema#unsignedShort"),
		
		;
	
		private String uri;
	
		private FORBIDDEN_URIS(String u) {
			uri = u;
		}
	
		public String getUri() {
			return uri;
		}
	
		static public boolean isForbidden(String u) {
			for(int i = 0; i < FORBIDDEN_URIS.values().length; i++) {
				if(FORBIDDEN_URIS.values()[i].getUri().equals(u)) {
					return true;
				}
			}
			return false;
		}
	}
	
	public static int queryTimeout = 10000; // in milliseconds

	public static URI thingUri = new URI("thing", KIND.CLASS, "");
	public static Keyword thingKeyword = new Keyword("thing");

	private ControlUtils() {
	}

	@SuppressWarnings("deprecation")
	public static Date getProfileExpireDate() {
		Time current = new Time((new Date()).getTime());
		current.setYear(2050);
		return current;
	}

	public static void debugMessage(Object o) {
		if(o != null) {
			GWT.log(o.toString());
		} else {
			GWT.log("null");
		}
	}

	public static void setLogLevel(LOG_LEVEL level) {
		logLevel = level;
	}

	public static LOG_LEVEL getLogLevel() {
		return logLevel;
	}

	public static void debugMessage(String mess) {
		ControlUtils.logMessage(mess, LOG_LEVEL.DEBUG);
	}

	public static void traceMessage(String mess) {
		ControlUtils.logMessage(mess, LOG_LEVEL.TRACE);
	}

	public static void debugMessage(String mess, Throwable except) {
		ControlUtils.logMessage(mess, except, LOG_LEVEL.DEBUG);
	}

	public static void traceMessage(String mess, Throwable except) {
		ControlUtils.logMessage(mess, except, LOG_LEVEL.TRACE);
	}
	
	public static void logMessage(String message, LOG_LEVEL level) {
		logMessage(message, null, level);
	}

	public static void logMessage(String message, Throwable except, LOG_LEVEL level) {
		String finalMessage = message;
		if(except != null) {
			finalMessage += " " + expandExceptionMessage(except);
		}
		if(level == logLevel) {
			GWT.log(finalMessage);
		} else {
			if(logLevel == LOG_LEVEL.TRACE) {
				GWT.log(finalMessage);
			} else if(logLevel == LOG_LEVEL.NOPE) {
			}
		}
	}

	public static void exceptionMessage(Throwable e) {
		debugMessage(ControlUtils.expandExceptionMessage(e));
		GWT.log("EXCEPTION: ", e);
	}

	public static String expandExceptionMessage(Throwable exceptToDisplay) {
		StackTraceElement[] stTab = exceptToDisplay.getStackTrace();
		String message = "";
		for(int i = 0; i < stTab.length ; i++) {
			message = stTab[i].toString() + "\n";
		}
		String exceptionMess = "EXCEPTION: " + exceptToDisplay + " " + exceptToDisplay.getCause() + " " + exceptToDisplay.getMessage() + " " + message;
		return exceptionMess;
	}

}
