package com.irisa.formulis.control;

import java.util.Collection;
import java.util.Iterator;

import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.XMLParser;
import com.irisa.formulis.control.profile.Profile;
import com.irisa.formulis.control.profile.ProfileClassLine;
import com.irisa.formulis.control.profile.ProfileForm;
import com.irisa.formulis.control.profile.ProfileLine;
import com.irisa.formulis.control.profile.ProfileRelationLine;
import com.irisa.formulis.model.basic.BasicLeafElement;
import com.irisa.formulis.model.basic.Keyword;
import com.irisa.formulis.model.basic.Plain;
import com.irisa.formulis.model.basic.Prim;
import com.irisa.formulis.model.basic.Typed;
import com.irisa.formulis.model.basic.URI;
import com.irisa.formulis.model.exception.SerializingException;

public class XMLSerializer {

	private XMLSerializer() {
	}
	
	private static Document serialRootDoc = XMLParser.createDocument();
	
	public static Element profilesToXml(Collection<Profile> l) throws SerializingException {
		Element result = serialRootDoc.createElement("profiles");
		
		Iterator<Profile> itPro = l.iterator();
		while(itPro.hasNext()) {
			Profile pro = itPro.next();
			result.appendChild(profileToXml(pro));
		}
		
		return result;
	}
	
	public static Element profileToXml( Profile pro) throws SerializingException {
		Element result = serialRootDoc.createElement("profile");
		
		result.setAttribute("storeName", pro.getStoreName());
		result.setAttribute("name", pro.getName());
		result.appendChild(formToXml(pro.getForm()));
		
		return result;
	}
	
	public static Element uriToXml(URI u) {
		Element result = serialRootDoc.createElement("URI");
		
		result.setAttribute("kind", u.kindToString());
		result.setAttribute("uri", u.getUri());
		result.appendChild(serialRootDoc.createTextNode(u.getLabel()));
//		result.setNodeValue(u.getLabel());
		
		return result;
	}
	
	public static Element keywordToXml(Keyword k) {
		Element result = serialRootDoc.createElement("Kwd");
		
//		result.setNodeValue(k.getKeyword());
		result.appendChild(serialRootDoc.createTextNode(k.getKeyword()));
		
		return result;
	}
	
	public static Element plainToXml(Plain p) {
		Element result = serialRootDoc.createElement("Plain");
		
		result.setAttribute("lang", p.getLang());
//		result.setNodeValue(p.getPlain());
		result.appendChild(serialRootDoc.createTextNode(p.getPlain()));
		
		return result;
	}
	
	public static Element primToXml(Prim p) {
		Element result = serialRootDoc.createElement("Prim");
		
//		result.setNodeValue(p.getPrim());
		result.appendChild(serialRootDoc.createTextNode(p.getPrim()));
		
		return result;
	}
	
	public static Element typedToXml(Typed t) {
		Element result = serialRootDoc.createElement("Typed");
		
		result.setAttribute("uri", t.getUri());
//		result.setNodeValue(t.getValue());
		result.appendChild(serialRootDoc.createTextNode(t.getValue()));
		
		return result;
	}
	
	public static Element basicLeafElementToXml(BasicLeafElement e) throws SerializingException {
		if(e instanceof Keyword) {
			return keywordToXml((Keyword) e);
		} else if(e instanceof Plain) {
			return plainToXml((Plain) e);
		} else if(e instanceof Prim) {
			return primToXml((Prim) e);
		} else if(e instanceof Typed) {
			return typedToXml((Typed) e);
		} else if(e instanceof URI) {
			return uriToXml((URI) e);
		} else {
			throw new SerializingException("basicLeafElementToXml missing serialization for " + e.getClass());
		}
	}
	
	public static Element classlineToXml(ProfileClassLine profileClassLine) throws SerializingException {
		Element result = serialRootDoc.createElement("ClassLine");
		
		if(profileClassLine.isAnonymous()) {
			result.appendChild(serialRootDoc.createElement("anonymous"));
		} else {
			result.appendChild(uriToXml(profileClassLine.getClassUri()));
		}
		
		return result;
	}
	
	public static Element formlineToXml(ProfileLine l) throws SerializingException {
		if(l instanceof ProfileClassLine) {
			return classlineToXml((ProfileClassLine) l);
		} else if(l instanceof ProfileRelationLine) {
			return relationlineToXml((ProfileRelationLine) l);
		} else {
			throw new SerializingException("formlineToXml missing formline serialization");
		}
	}
	
	public static Element relationlineToXml(ProfileRelationLine l) throws SerializingException {
		Element result = serialRootDoc.createElement("RelationLine");
		
		result.setAttribute("index", String.valueOf(l.getIndex()));
		result.setAttribute("info", l.getInfo());

		Element fixed = serialRootDoc.createElement("fixed");
		fixed.appendChild(uriToXml(l.getRelation()));	
		result.appendChild(fixed);
		
		if(l.getVariable() != null) {
			Element variable = serialRootDoc.createElement("variable");
			if(l.getVariable() instanceof BasicLeafElement) {
				variable.appendChild(basicLeafElementToXml((BasicLeafElement) l.getVariable()));
			} else if(l.getVariable() instanceof ProfileForm) {
				variable.appendChild(formToXml((ProfileForm) l.getVariable()));
			} else {
				throw new SerializingException("classlineToXml expect variable element to be BasicLeafElement or ProfileForm, got: " + l.getVariable());
			}
			result.appendChild(variable);
		}
		
		return result;
	}
	
	public static Element formToXml(ProfileForm f) throws SerializingException {
		ControlUtils.debugMessage("formToXml( " + f + " )");
		Element result = serialRootDoc.createElement("form");
		
		if(! f.getTypeLines().isEmpty()) {
			Iterator<ProfileClassLine> itType = f.typeIterator();
			while(itType.hasNext()) {
				ProfileClassLine type = itType.next();
				Element classCat = serialRootDoc.createElement("types");
				classCat.appendChild(classlineToXml(type));
				result.appendChild(classCat);
			}
		}
		
		if(! f.getLines().isEmpty()) {
			Element linesCat = serialRootDoc.createElement("lines");
			Iterator<ProfileRelationLine> itLines = f.relationIterator();
			while(itLines.hasNext()) {
				ProfileLine li = itLines.next();
				linesCat.appendChild(formlineToXml(li));
			}
			result.appendChild(linesCat);
		}
		
		return result;
	}

}
