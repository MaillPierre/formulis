package com.irisa.formulis.control;

import java.util.LinkedList;

import com.google.gwt.xml.client.NamedNodeMap;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.irisa.formulis.control.profile.Profile;
import com.irisa.formulis.control.profile.ProfileClassLine;
import com.irisa.formulis.control.profile.ProfileForm;
import com.irisa.formulis.control.profile.ProfileLeafElement;
import com.irisa.formulis.control.profile.ProfileLine;
import com.irisa.formulis.control.profile.ProfileRelationLine;
import com.irisa.formulis.model.*;
import com.irisa.formulis.model.answers.*;
import com.irisa.formulis.model.answers.AnswersHeader.AGGREGATION;
import com.irisa.formulis.model.answers.AnswersHeader.ORDER;
import com.irisa.formulis.model.basic.*;
import com.irisa.formulis.model.exception.XMLParsingException;
import com.irisa.formulis.model.suggestions.*;

public class Parser {

	private static Controller control;

	public static void setControl(Controller c) {
		control = c;
	}

	public static Place parsePlace(Node node) throws XMLParsingException {
		Place result = new Place();
		String placeId = node.getAttributes().getNamedItem("placeId").getNodeValue();
		result.setId(placeId);
		NodeList placeChildList = node.getChildNodes();
		Statement stat = null;
		Suggestions sug = null;
		Answers ans = null;
		for(int i = 0; i < placeChildList.getLength(); i++) {
			Node childNode = placeChildList.item(i);
			String childNodeName = childNode.getNodeName();
			switch(childNodeName) {
			case "string" :
				break;
			case "statement":
				stat = parseStatement(childNode);
				break;
			case "suggestions":
				sug = parseSuggestions(childNode);
				break;
			case "answers":
				ans = parseAnswers(childNode);
				break;
			case "relaxation":
				String rank = childNode.getAttributes().getNamedItem("rank").getNodeValue();
				String hasMore = childNode.getAttributes().getNamedItem("hasMore").getNodeValue();
				String hasLess = childNode.getAttributes().getNamedItem("hasLess").getNodeValue();
				result.setRelaxationRank(Integer.valueOf(rank));
				result.setHasMore(Boolean.valueOf(hasMore));
				result.setHasLess(Boolean.valueOf(hasLess));
				break;
			default:
				break;
			}
		}
		if(stat != null) {
			result.setStatement(stat);
		}
		if(sug != null) {
			result.setSuggestions(sug);
		}
		if(ans != null) {
			result.setAnswers(ans);
		}
		return result;
	}

	public static Suggestions parseSuggestions(Node node) throws XMLParsingException {
		Suggestions result = new Suggestions(control);

		if(node.getNodeName().equals("suggestions")) {
			NamedNodeMap attrList = node.getAttributes();
			boolean canInsertEntity = Boolean.parseBoolean(attrList.getNamedItem("canInsertEntity").getNodeValue());
			boolean canInsertRelation = Boolean.parseBoolean(attrList.getNamedItem("canInsertRelation").getNodeValue());
			result.setCanInsertEntity(canInsertEntity);
			result.setCanInsertRelation(canInsertRelation);

			// Entity suggestions
			Node entitySuggNode = node.getFirstChild();
			NodeList entitySuggList = entitySuggNode.getChildNodes();
			for(int i = 0; i < entitySuggList.getLength(); i++) {
				Node currSugg = entitySuggList.item(i);
				if(currSugg.getNodeName().equals("increment")) {
					result.setCurrentEntityIncrement(parseIncrement(currSugg));
				} else if(currSugg.getNodeName().equals("node")) {
					result.addEntitySuggestions(parseIncrement(currSugg.getFirstChild()));
				} else {
					throw new XMLParsingException("Expected entity increment or node node, got " + currSugg);
				}
			}

			// Relation suggestions
			Node relationSuggNode = entitySuggNode.getNextSibling();
			NodeList relationSuggList = relationSuggNode.getChildNodes();
			for(int i = 0; i < relationSuggList.getLength(); i++) {
				Node currSugg = relationSuggList.item(i);
				if(currSugg.getNodeName().equals("increment")) {
					result.setCurrentRelationIncrement(parseIncrement(currSugg));
				} else if(currSugg.getNodeName().equals("node")) {
					result.addRelationSuggestions(parseIncrement(currSugg.getFirstChild()));
				} else {
					throw new XMLParsingException("Expected relation increment or node node, got " + currSugg);
				}
			}

			// Transformation suggestions
			Node transfoSuggNode = relationSuggNode.getNextSibling();
			NodeList transfoSuggList = transfoSuggNode.getChildNodes();
			for(int i = 0; i < transfoSuggList.getLength(); i++) {
				Node currTrans = transfoSuggList.item(i);
				String transName = currTrans.getNodeName();
				result.addTransformationSuggestions(new Transformation(transName));
			}
		}

		return result;
	}

	public static Increment parseIncrement(Node node) throws XMLParsingException {
		Increment result = null;

		if(node.getNodeName().equals("increment")) {
			String id;
			Increment.KIND kind;
			int ratioLeft;
			int ratioRight;
			boolean isNew;
			BasicElement element;
			NamedNodeMap attrList = node.getAttributes();
			id = attrList.getNamedItem("incrementId").getNodeValue();
			kind = Increment.kindFromString(attrList.getNamedItem("kind").getNodeValue());
			ratioLeft = Integer.parseInt(attrList.getNamedItem("ratioLeft").getNodeValue());
			ratioRight = Integer.parseInt(attrList.getNamedItem("ratioRight").getNodeValue());
			isNew = Boolean.parseBoolean(attrList.getNamedItem("isNew").getNodeValue());
			element = parseDisplayNode(node.getFirstChild(), null);
			// Il y a un second noeud après le DisplayNode pris en element, il ne respecte pas la syntaxe des display node 
			// et semble répéter le contenu de element, donc je l'ignore

			result = new Increment(id);
			result.setKind(kind);
			result.setRatioLeft(ratioLeft);
			result.setRatioRight(ratioRight);
			result.setDisplayElement(element);
			result.setIsNew(isNew);
		} else {
			throw new XMLParsingException("Incorrect node name " + node.getNodeName() + " expected increment");
		}

		return result;
	}

	public static Answers parseAnswers(Node node) throws XMLParsingException {
		//		Utils.debugMessage("parseAnswers " + node);
		Answers result = new Answers();
		int count = 0;
		int start = 0;
		int end = 0;
		int size = 0;

		if(node.getNodeName().equals("answers")) {
			// Paging
			Node pagingNode = node.getFirstChild();
			if(pagingNode.getNodeName().equals("paging")) {
				NamedNodeMap attrList = pagingNode.getAttributes();
				count = Integer.parseInt(attrList.getNamedItem("count").getNodeValue());
				start = Integer.parseInt(attrList.getNamedItem("start").getNodeValue());
				end = Integer.parseInt(attrList.getNamedItem("end").getNodeValue());
				size = Integer.parseInt(attrList.getNamedItem("size").getNodeValue());
			} else {
				throw new XMLParsingException("Expected paging node, got " + pagingNode );
			}

			// Header
			Node columnsNode = pagingNode.getNextSibling();
			if(columnsNode.getNodeName().equals("columns")) {
				NodeList columns = columnsNode.getChildNodes();
				for(int i = 0; i < columns.getLength(); i++) {
					Node columnNode = columns.item(i);
					if(columnNode.getNodeName().equals("column")) {
						NamedNodeMap attrList = columnNode.getAttributes();
						String name = attrList.getNamedItem("name").getNodeValue();
						ControlUtils.debugMessage("Column Name " + attrList.getNamedItem("name").getNodeValue() + " ==> " + name);
						ORDER order = AnswersHeader.orderFromString(attrList.getNamedItem("order").getNodeValue());
						String pattern = attrList.getNamedItem("pattern").getNodeValue();
						AGGREGATION aggreg = AnswersHeader.aggregationFromString(attrList.getNamedItem("aggreg").getNodeValue());
						boolean hidden = Boolean.parseBoolean(attrList.getNamedItem("hidden").getNodeValue());

						AnswersHeader header = new AnswersHeader(name);
						header.setAggreg(aggreg);
						header.setHidden(hidden);
						header.setOrder(order);
						header.setPattern(pattern);

						result.addHeaderColumn(header);
					} else {
						throw new XMLParsingException("Expected column node, got " + columnNode );
					}
				}
			} else {
				throw new XMLParsingException("Expected columns node, got " + columnsNode );
			}

			// Rows
			Node rowsNode = columnsNode.getNextSibling();
			if(rowsNode.getNodeName().equals("rows")) {
				NodeList rows = rowsNode.getChildNodes();
				for(int i = 0; i < rows.getLength(); i++) {
					Node rowNode = rows.item(i);
					if(rowNode.getNodeName().equals("row")) {
						AnswersRow aRow = parseAnswerRow(rowNode);

						result.addContentRow(aRow);
					} else {
						throw new XMLParsingException("Expected row node, got " + rowNode );
					}
				}
			} else {
				throw new XMLParsingException("Expected rows node, got " + rowsNode );
			}

		} else {
			throw new XMLParsingException("Incorrect node name " + node.getNodeName() + " expected answers");
		}

		result.setCount(count);
		result.setEnd(end);
		result.setSize(size);
		result.setStart(start);
		return result;
	}

	private static AnswersRow parseAnswerRow(Node node) throws XMLParsingException {
		//		Utils.debugMessage("parseAnswerRow " + node);
		AnswersRow result = new AnswersRow();
		if(node.getNodeName().equals("row")) {
			NodeList cellList = node.getChildNodes();
			for(int i = 0; i < cellList.getLength(); i++) {
				Node cellNode = cellList.item(i);
				if(cellNode.getNodeName().equals("cell")) {
					result.addContent(parseDisplayNode(cellNode.getFirstChild(), null));
				} else {
					throw new XMLParsingException("Expected cell node, got " + cellNode );
				}
			}
		} else {
			throw new XMLParsingException("Incorrect node name " + node.getNodeName() + " expected row");
		}
		return result;
	}

	public static Statement parseStatement(Node node) throws XMLParsingException {
		Statement result = new Statement(control);
		String displayedFocus = null;
		BasicElement content = null;
		if(node.getNodeName().equals("statement")) {
			Node stringNode = node.getFirstChild();
			String statString = stringNode.getFirstChild().getNodeValue();
			result.setString(statString);
			Node displayedFocusNode = stringNode.getNextSibling();
			displayedFocus = displayedFocusNode.getAttributes().getNamedItem("focusId").getNodeValue();
			BasicElement parsingResult =  parseDisplayNode(displayedFocusNode, null);
			content = parsingResult;
			result.setContent(content);
			result.setFocusedDiplay(displayedFocus);
		} else {
			throw new XMLParsingException("Incorrect node name " + node.getNodeName() + " expected statement");
		}
		return result;
	}
	
	public static BasicElement parseDisplayNode(Node node) throws XMLParsingException {
		return parseDisplayNode(node, null);
	}

	public static BasicElement parseDisplayNode(Node node, BasicElementContener parent) throws XMLParsingException {
		try {
			String nodeName = node.getNodeName();
			switch(nodeName) {
			case "focusedDisplay": 
			case "display":
				Display dis = new Display(parent);
				NodeList contenerChildList = node.getChildNodes();
				for(int i = 0; i < contenerChildList.getLength(); i++) {
					Node displayChildNode = contenerChildList.item(i);
					dis.addContent(parseDisplayNode(displayChildNode, parent));
				}
				return dis;
			case "Focus":
				String focusId = node.getAttributes().getNamedItem("id").getNodeValue();
				Focus focus = new Focus(focusId, parent);
				NodeList focusChildList = node.getChildNodes();
				for(int i = 0; i < focusChildList.getLength(); i++) {
					BasicElement childDisplay = parseDisplayNode(focusChildList.item(i), focus);
					focus.addContent(childDisplay);
				}
				return focus;
			case "Kwd":
				String kwdValue = node.getFirstChild().getNodeValue();
				Keyword kwd = new Keyword(kwdValue);
				return kwd;
			case "Prim":
				String primValue = node.getFirstChild().getNodeValue();
				Prim prim = new Prim(primValue);
				return prim;
			case "Var":
				String varValue = node.getFirstChild().getNodeValue();
				Variable var = new Variable(varValue);
				return var;
			case "Space":
				break;
			case "Pair":
				String forceIndentString = node.getAttributes().getNamedItem("forceIndent").getNodeValue();
				boolean forceIndent = forceIndentString.equals("true");
				Pair pair = new Pair(forceIndent, parent);
				NodeList pairChildList = node.getChildNodes();
				if(pairChildList.getLength() == 2) {
					BasicElement pairFirstLine = parseDisplayNode(pairChildList.item(0), pair);
					pair.addOnFirstLine(pairFirstLine);
					BasicElement pairSecondLine = parseDisplayNode(pairChildList.item(1), pair);
					pair.addOnSecondLine(pairSecondLine);
				} else {
					throw new XMLParsingException("parseStatementNode( " + node + " ) unexpected number of element in pair" );
				}
				return pair;
			case "URI":
				String uriUri = node.getAttributes().getNamedItem("uri").getNodeValue();
				URI.KIND urikind = URI.getKindFromString(node.getAttributes().getNamedItem("kind").getNodeValue());
				String uriValue = uriUri; 
				if(node.getFirstChild() != null) {
					uriValue = node.getFirstChild().getNodeValue();
				}
				URI uri = new URI(uriUri, urikind, uriValue);
				return uri;
			case "Typed":
				String typedUri = node.getAttributes().getNamedItem("uri").getNodeValue();
				String typedValue = node.getFirstChild().getNodeValue();
				Typed typed = new Typed(typedUri, typedValue);
				return typed;
			case "Plain":
				String plainValue = node.getFirstChild().getNodeValue();
				Plain plain;
				if (node.getAttributes().getNamedItem("lang") != null) {
					String plainLang = node.getAttributes().getNamedItem("lang").getNodeValue();
					plain = new Plain(plainValue, plainLang);
				} else {
					plain = new Plain(plainValue);
				}
				return plain;
			case "List":
				List list = new List(parent);
				NodeList listChildList = node.getChildNodes();
				for(int i = 0; i < listChildList.getLength(); i++) {
					BasicElement childDisplay = parseDisplayNode(listChildList.item(i), list);
					if(! (childDisplay instanceof Space)) {
						list.addContent(childDisplay);
					}
				}
				return list;
			case "And":
				And and = new And(parent);
				NodeList andChildList = node.getChildNodes();
				for(int i = 0; i < andChildList.getLength(); i++) {
					BasicElement childDisplay = parseDisplayNode(andChildList.item(i), and);
					and.addContent(childDisplay);
				}
				return and;
			case "Or":
				Or orE = new Or(parent);
				NodeList orChildList = node.getChildNodes();
				for(int i = 0; i < orChildList.getLength(); i++) {
					BasicElement childDisplay = parseDisplayNode(orChildList.item(i), orE);
					orE.addContent(childDisplay);
				}
				return orE;
			case "Brackets":
				Brackets brack = new Brackets(parent);
				NodeList brackChildList = node.getChildNodes();
				for(int i = 0; i < brackChildList.getLength(); i++) {
					BasicElement childDisplay = parseDisplayNode(brackChildList.item(i), brack);
					brack.addContent(childDisplay);
				}
				return brack;
			case "Quote":
				Quote quo = new Quote(parent);
				NodeList quoChildList = node.getChildNodes();
				for(int i = 0; i < quoChildList.getLength(); i++) {
					BasicElement childDisplay = parseDisplayNode(quoChildList.item(i), quo);
					quo.addContent(childDisplay);
				}
				return quo;
			case "Tuple":
				Tuple tup = new Tuple(parent);
				NodeList tupleChildList = node.getChildNodes();
				for(int i = 0; i < tupleChildList.getLength(); i++) {
					BasicElement childDisplay = parseDisplayNode(tupleChildList.item(i), tup);
					if(! (childDisplay instanceof Space)) {
						tup.addContent(childDisplay);
					}
				}
				return tup;
			default:
				throw new XMLParsingException("Unknown node type " + node);
			}
		} catch(NullPointerException e) {
			ControlUtils.exceptionMessage(e);
		}
		return null;
	}

	public static LinkedList<Profile> parseProfiles(Node node) throws XMLParsingException {
		LinkedList<Profile> result = new LinkedList<Profile>();
		if(node.getNodeName() == "profiles") {
			NodeList profilesChildList = node.getChildNodes();
			for(int i = 0; i < profilesChildList.getLength(); i++) {
				if(profilesChildList.item(i).getNodeName() == "profile") {
					result.add(parseProfile(profilesChildList.item(i)));
				}
			}
		} else {
			throw new XMLParsingException("parseProfiles expect <profiles> root node, got : " + node.getNodeName() + " node = " + node);
		}
		return result;
	}

	public static Profile parseProfile(Node node) throws XMLParsingException {
		//		Utils.debugMessage("parseProfile( " + node + " )");
		if(node.getNodeName() == "profile") {
			String name = "";
			if(node.getAttributes().getNamedItem("name") != null) {
				name = node.getAttributes().getNamedItem("name").getNodeValue();
			} else {
				throw new XMLParsingException("parseProfile expect <profile> root node to have name attribut, got : " + node);
			}
			String storeName = "";
			if(node.getAttributes().getNamedItem("storeName") != null) {
				storeName = node.getAttributes().getNamedItem("storeName").getNodeValue();
			} else {
				throw new XMLParsingException("parseProfile expect <profile> root node to have storeName attribut, got : " + node);
			}
			Profile result = new Profile(name, storeName);

			Node proCompoNode = node.getFirstChild(); 
			if(proCompoNode.getNodeName() == "form") {
				result.setForm((ProfileForm) parseProfileForm(proCompoNode));
			}

			return result;
		} else {
			throw new XMLParsingException("parseProfile expect <profile> root node, got : " + node);
		}
	}

	public static ProfileLine parseProfileLine(Node node) throws XMLParsingException {
		if(node.getNodeName() == "RelationLine") {
			return parseProfileRelationLine(node);
		} else if(node.getNodeName() == "ClassLine") {
			return parseProfileClassLine(node);
		} else {
			throw new XMLParsingException("parseProfileLine expect relationline or classline node, got: " + node);
		}
	}

	public static ProfileClassLine parseProfileClassLine(Node node) throws XMLParsingException, XMLParsingException {
		//		Utils.debugMessage("parseProfileClassLine( " + node + " )");
		if(node.getNodeName() == "ClassLine") {
			String info = "";
			if(node.getAttributes().getNamedItem("info") != null) {
				info = node.getAttributes().getNamedItem("info").getNodeValue();
			}
			if( node.getFirstChild() != null && node.getFirstChild().getNodeName() == "URI") {
				URI u = (URI) parseDisplayNode(node.getFirstChild());
				return new ProfileClassLine(u, info);
			} else if(node.getFirstChild() != null && node.getFirstChild().getNodeName() == "anonymous") {
				return new ProfileClassLine();
			} else {
				throw new XMLParsingException("parseProfileClassLine expect ClassLine content to be URI node or anonymous node, got: " + node.getFirstChild());
			}
		} else {
			throw new XMLParsingException("parseProfileClassLine expect ClassLine node, got: " + node);
		}
	}

	public static ProfileRelationLine parseProfileRelationLine(Node node) throws XMLParsingException {
		//		Utils.debugMessage("parseProfileRelationLine( " + node + " )");
		if(node.getNodeName() == "RelationLine") {
			URI fixed = null;
			ProfileLeafElement variable = null;
			String info = "";
			String index = "-1";
			NodeList cList = node.getChildNodes();
			for(int i = 0; i < cList.getLength(); i++) {
				Node child = cList.item(i);
				if(child.getNodeName() == "fixed") {
					fixed = (URI) parseDisplayNode(child.getFirstChild());
				} else if(child.getNodeName() == "variable") {
					Node varContent = child.getFirstChild();
					if(varContent.getNodeName() == "URI" || varContent.getNodeName() == "Typed" || varContent.getNodeName() == "Plain" ) {
						variable = (ProfileLeafElement) parseDisplayNode(varContent);
					} else if(varContent.getNodeName() == "form") {
						variable = parseProfileForm(varContent);
					} else {
						throw new XMLParsingException("parseProfileRelationLine expect basicLeafElement or form as variable, got: " + varContent );
					}
				}
			}
			if(node.getAttributes().getNamedItem("info") != null) {
				info = node.getAttributes().getNamedItem("info").getNodeValue();
			}
			if(node.getAttributes().getNamedItem("index") != null) {
				index = node.getAttributes().getNamedItem("index").getNodeValue();
			}

			if(fixed != null) {
				ProfileRelationLine newLine = new ProfileRelationLine(fixed, variable, info);
				newLine.setIndex(Integer.parseInt(index));
				return newLine;
			} else {
				throw new XMLParsingException("parseProfileRelationLine no fixed element found");
			}

		} else {
			throw new XMLParsingException("parseProfileRelationLine expect RelationLine root node, got: " + node);
		}
	}

	public static ProfileLeafElement parseProfileForm(Node node) throws XMLParsingException {
		if(node.getNodeName() == "form") {
			ProfileForm result = new ProfileForm();

			NodeList nodeChilds = node.getChildNodes();
			for(int chi = 0; chi < nodeChilds.getLength(); chi++) {
				Node child = nodeChilds.item(chi);
				if(child.getNodeName() == "type") {
					ProfileClassLine typeLine = parseProfileClassLine(child.getFirstChild());
					result.setTypeLine(typeLine);
				} else if(child.getNodeName() == "lines") {
					NodeList lineList = child.getChildNodes();
					for(int li = 0; li < lineList.getLength(); li++) {
						Node lineNode = lineList.item(li);
						ProfileLine line = parseProfileLine(lineNode);
						result.addLine(line);
					}
				}
			}

			return result;
		} else {
			throw new XMLParsingException("parseProfileForm expect form root node, got: " + node);
		}
	}
}
