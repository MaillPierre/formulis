package com.irisa.formulis.control;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.UmbrellaException;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.i18n.client.Dictionary;
import java.util.MissingResourceException;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.xml.client.XMLParser;
import com.google.gwt.xml.client.impl.DOMParseException;
import com.irisa.formulis.control.access.LoginToken;
import com.irisa.formulis.control.access.NewUserToken;
import com.irisa.formulis.control.async.PlaceRequestCallback;
import com.irisa.formulis.control.profile.Profile;
import com.irisa.formulis.model.*;
import com.irisa.formulis.model.basic.*;
import com.irisa.formulis.model.exception.*;
import com.irisa.formulis.model.form.*;
import com.irisa.formulis.model.suggestions.Increment;
import com.irisa.formulis.model.suggestions.Increment.KIND;
import com.irisa.formulis.view.AbstractDataWidget;
import com.irisa.formulis.view.FooterWidget;
import com.irisa.formulis.view.MainNavigationBar;
import com.irisa.formulis.view.MainPage;
import com.irisa.formulis.view.ViewUtils;
import com.irisa.formulis.view.LoginWidget.LOGIN_STATE;
import com.irisa.formulis.view.create.CreationTypeOracle;
import com.irisa.formulis.view.create.fixed.ClassCreateWidget;
import com.irisa.formulis.view.create.fixed.RelationCreateWidget;
import com.irisa.formulis.view.event.*;
import com.irisa.formulis.view.event.interfaces.*;
import com.irisa.formulis.view.form.*;
import com.irisa.formulis.view.form.AbstractFormLineWidget.LINE_STATE;
import com.irisa.formulis.view.form.suggest.AbstractSuggestionWidget.SuggestionCallback;

/**
 * controller of all client-server interactions
 * Functions beginning with sewelis* are direct correspondance to the SEWELIS API
 * server adress is set by default to http://127.0.0.1:9999/ or set in a dictionnary on index.html
 * This class is the end of the event chain
 * Main method is onModuleLoad
 * @author pmaillot
 *
 */

public final class Controller implements EntryPoint, ClickHandler, FormEventChainHandler, StatementFocusChangeHandler/*, ValueChangeHandler<Integer>*/ {

	private HashMap<String, Store> storeMapByName = new HashMap<String, Store>();
	private HashMap<String, Store> storeMapByLabel = new HashMap<String, Store>();
	private MainPage mainPage;
	private MainNavigationBar navBar = new MainNavigationBar();

	private static String uriBaseAdress = "http://www.irisa.fr/LIS/sewelis/";
	
	private static String serverAdress = "http://127.0.0.1:9999/";
//	private static String serverAdress = "http://servolis.irisa.fr:3939/"; // TODO Rendre adresse serveur configurable
	private static String logServerAdress = "http://servolis.irisa.fr:3941";
	private String userLogin = "anonymous";
	private String userKey = "0";
	private Store currentStore = null;
	private Place place = null;
	private Form form = null;
	private String lastRequestPlace = "";

	private String cookiesProfilesIndex = "FormulisProfile";
	private String cookiesUserLogin = "FormulisUserLogin";
	private String cookiesUserkey = "FormulisUserKey";
	@SuppressWarnings("deprecation")
	private Date cookiesProfilesExpireDate = new Date(2500, 1, 1);
	private HashSet<Profile> profiles = new HashSet<Profile>();
	private HashMap<String, LinkedList<Profile>> storeProfileMap = new HashMap<String, LinkedList<Profile>>();
	
	private int numberOfActions = 0;
	private Date startEditDate = new Date();
	
	private static Controller _instance = null;

	/**
	 * Singleton design pattern
	 * @return the Controller instance
	 */
	public static Controller instance() {
		return _instance;
	}

	/**
	 * 
	 * @return the SEWELIS server adress, initialized by a dictionnary in index.html or set to http://127.0.0.1:9999/ by default
	 */
	public static String getServerAdress() {
		return serverAdress;
	}

	public static void setServerAdress(String adress) {
		serverAdress = adress;
	}

	/**
	 * SEWELIS work by "places", composition of a statement/query + differents suggestions + answers
	 * FORMULIS works by jumping from place to place according to user interaction with the forms
	 * @return
	 */
	public Place getPlace() {
		return this.place;
	}

	/**
	 * Select the given store in the known store list, refresh this list
	 * @param a store name
	 */
	private void setCurrentStore(String selectValue) {
		if(currentStore != null && ! selectValue.equals(currentStore.getName())) {
			this.form.clear();
			this.mainPage.formWidget.reload();
		}
		currentStore = storeMapByName.get(selectValue);
		sewelisStoreXmlns();
	}
	
	/**
	 * Function for a hidden variable counting the number of user interactions with the forms
	 */
	private void incrementNumberOfActions() {
		numberOfActions++;
		ControlUtils.debugMessage("numberOfActions: " + getNumberOfActions());
	}
	
	private int getNumberOfActions() {
		return numberOfActions;
	}

	// Profiles FIXME REMETTRE GESTION DES PROFILS ( HERE BE DRAGONS )
//
//	public void addProfile(Profile pro) {
//		ControlUtils.debugMessage("addProfile " + pro);
//		profiles.add(pro);
//		if(! storeProfileMap.containsKey(pro.getStoreName())) {
//			storeProfileMap.put(pro.getStoreName(), new LinkedList<Profile>());
//		}
//		storeProfileMap.get(pro.getStoreName()).add(pro);
//		addProfileToCookies(pro);
//	}
//
//	public void addProfileToCookies(Profile pro) {
//		try {
//			LinkedList<Profile> cookiesProfiles = Parser.parseProfiles(XMLParser.parse(Cookies.getCookie(this.cookiesProfilesIndex)).getDocumentElement());
//			cookiesProfiles.add(pro);
//			Cookies.setCookie(cookiesProfilesIndex, XMLSerializer.profilesToXml(cookiesProfiles).toString(), cookiesProfilesExpireDate);
//		} catch (XMLParsingException | SerializingException e) {
//			ControlUtils.exceptionMessage(e);
//		}
//		reloadNavbarProfileList();
//	}
//
//	public void addProfiles(Collection<Profile> l) {
//		Iterator<Profile> itPro = l.iterator();
//		while(itPro.hasNext()) {
//			Profile pro = itPro.next();
//			addProfile(pro);
//		}
//	}
//	
//	public void removeProfile(Profile pro) {
//		profiles.remove(pro);
//		storeProfileMap.get(pro.getStoreName()).remove(pro);
//	}
//
//	private Profile findProfile(Collection<Profile> list, String name) {
//		Iterator<Profile> itPro = list.iterator();
//		while(itPro.hasNext()) {
//			Profile pro = itPro.next();
//			if(pro.getName() == name) {
//				return pro;
//			}
//		}
//		return null;
//	}
//
//	public Profile getProfile(String name) {
//		return findProfile(this.profiles, name);
//	}
//
//	public LinkedList<Profile> getProfilesByStore(String store) {
//		return this.storeProfileMap.get(store);
//	}
//
//	public Profile getProfileForAStore(String name, String store) {
//		return findProfile(getProfilesByStore(store), name);
//	}
//
//	public void initializeProfilesFromCookie() {
//		if(Cookies.getCookie(cookiesProfilesIndex) == null) {
//			try {
//				Cookies.setCookie(cookiesProfilesIndex, XMLSerializer.profilesToXml(new LinkedList<Profile>()).toString(), cookiesProfilesExpireDate);
//			} catch (SerializingException e) {
//				ControlUtils.exceptionMessage(e);
//			}
//		} 
//		try {
//			this.profiles.addAll(Parser.parseProfiles(XMLParser.parse(Cookies.getCookie(cookiesProfilesIndex)).getDocumentElement()));
//		} catch (XMLParsingException e) {
//			ControlUtils.exceptionMessage(e);
//		}
//		this.reloadNavbarProfileList();
//	}
//	
//	/**
//	 * @return Le contenu parsé du cookie de profile
//	 */
//	public Document getProfileDocument() {
//		return XMLParser.parse(Cookies.getCookie(cookiesProfilesIndex));
//	}
//	
//	public void setProfileDocument(String doc) {
//		Cookies.setCookie(cookiesProfilesIndex, doc, cookiesProfilesExpireDate);
//	}
//
//	public LinkedList<Profile> getCookiesProfileList() {
//		try {
////			Utils.debugMessage("getCookiesProfileList cookie: " + Cookies.getCookie(cookiesProfilesIndex));
//			Document profilesXMLDoc = XMLParser.parse(Cookies.getCookie(cookiesProfilesIndex)); 
//			Node profilesXMLNode = profilesXMLDoc.getDocumentElement();
//			return Parser.parseProfiles(profilesXMLNode);
//		} catch (XMLParsingException | DOMParseException e) {
//			ControlUtils.exceptionMessage(e);
//			return null;
//		}
//	}
//
//	public void clearProfiles() {
//		this.profiles.clear();
//		this.storeProfileMap.clear();
//		try {
//			Cookies.setCookie(cookiesProfilesIndex, XMLSerializer.profilesToXml(new LinkedList<Profile>()).toString(), cookiesProfilesExpireDate);
//		} catch (SerializingException e) {
//			ControlUtils.exceptionMessage(e);
//		}
//	}
//
//	public void reloadNavbarProfileList() {
////		navBar.adminPanel.profileList.clear();
////		Iterator<Profile> itPro = this.profiles.iterator();
////		while(itPro.hasNext()) {
////			Profile pro = itPro.next();
////			navBar.adminPanel.profileList.addItem(pro.getName() + " : " + pro.getStoreName(), pro.getName());
////		}
////		
////		navBar.adminPanel.profileEditReload.click(); // SALE
//	}
	
	/**
	 * Refresh the  displayed namespaces associated to a store according to the values stocked in the Store object
	 */
	public void refreshNamespaceList() {
		this.mainPage.getSettingsWidget().getNsListBox().clear();
		Iterator<String> itNs = currentStore.getNamespaceIterator();
		while(itNs.hasNext()) {
			String ns = itNs.next();
			if(currentStore.getNamespacePrefix(ns) != null) {
				String line = currentStore.getNamespacePrefix(ns) + " " + ns;
				this.mainPage.getSettingsWidget().getNsListBox().addItem(line);
			}
		}
	}


	/**
	 * Ping request
	 * Refresh server state label on main page
	 * @throws RequestException
	 */
	public void sewelisPing() throws RequestException {
		String pingRequestString = serverAdress + "/ping";
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, URL.encode(pingRequestString));

		navBar.setServerStatusMessage("Waiting...");
		builder.sendRequest(null, new RequestCallback() {	
			@Override		
			public void onError(Request request, Throwable exception) {
				ControlUtils.exceptionMessage(exception);
			}

			@Override
			public void onResponseReceived(Request request, Response response) {
				if (200 == response.getStatusCode()) {
					Document statusDoc = XMLParser.parse(response.getText());
					Element docElement = statusDoc.getDocumentElement();
					String status = docElement.getAttribute("status");
					navBar.setServerStatusMessage(status);
				} else {
					// TODO GESTION DES MESSAGE D'ERREUR
					ControlUtils.debugMessage(request.toString() + " " + response.getStatusCode() + " " + response.getStatusText());
				}
			}
		});
	}

	/**
	 * Register a new user
	 * @param nu a NewUserToken containing the necessary infos to create a new SEWELIS account
	 */
	public void sewelisRegister(final NewUserToken nu) {
		String registerRequestString = serverAdress + "/register";
		registerRequestString += "?userKey=" + userKey;
		registerRequestString += "&userLogin=" + nu.getUserName();
		registerRequestString += "&passwd=" + Crypto.getCryptedString(nu.getPassword(), Controller.serverAdress);
		registerRequestString += "&email=" + nu.getEmail();

		navBar.setServerStatusMessage("Waiting...");
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, URL.encode(registerRequestString));

		try {
			builder.sendRequest(null, new RequestCallback() {

				@Override
				public void onResponseReceived(Request request, Response response) {
					if (200 == response.getStatusCode()) {
						Document statusDoc = XMLParser.parse(response.getText());
						Element docElement = statusDoc.getDocumentElement();
						String status = docElement.getAttribute("status");
						navBar.setServerStatusMessage(status);
						if(status.equals("ok")) {
							sewelisLogin(new LoginToken(nu.getUserName(), nu.getPassword()));
						} else {
							ControlUtils.debugMessage(docElement.getNodeValue());
							navBar.setServerStatusHovertext(docElement.getNodeValue());
						}
					} else {
						// FIXME GESTION DES MESSAGE D'ERREUR
						ControlUtils.debugMessage(request.toString() + " " + response.getStatusCode() + " " + response.getStatusText());
					}					
				}

				@Override
				public void onError(Request request, Throwable exception) {
					ControlUtils.exceptionMessage(exception);
				}

			});
		} catch (RequestException e) {
			ControlUtils.exceptionMessage(e);
		}
	}

	/**
	 * Connect to an existing SEWELIS account on the server
	 * @param t Login Token containing login and password
	 */
	public void sewelisLogin(final LoginToken t) {
		String loginRequestString = serverAdress + "/login";
		loginRequestString += "?userKey=" + userKey;
		loginRequestString += "&userLogin=" + t.getLogin();
		loginRequestString += "&passwd=" + Crypto.getCryptedString(t.getPassword(), Controller.serverAdress);
		navBar.setServerStatusMessage("Waiting...");

		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, URL.encode(loginRequestString));

		try {
			builder.sendRequest(null, new RequestCallback() {

				@Override
				public void onResponseReceived(Request request, Response response) {
					if (200 == response.getStatusCode()) {
						Document statusDoc = XMLParser.parse(response.getText());
						Element docElement = statusDoc.getDocumentElement();
						String status = docElement.getAttribute("status");
						if(!status.equals("ok")) {
							ControlUtils.debugMessage(docElement.getNodeValue());
							navBar.setServerStatusHovertext(docElement.getNodeValue());
						} else {
							userLogin = t.getLogin();
							userKey = docElement.getAttribute("userKey");
							navBar.loginWid.loggedUsernameLabel.setText(t.getLogin());
							navBar.loginWid.setLogState(LOGIN_STATE.LOGGED);
							Cookies.setCookie(cookiesUserLogin, userLogin);
							Cookies.setCookie(cookiesUserkey, userKey);
							sewelisVisibleStores();
						}
					} else {
						// FIXME GESTION DES MESSAGE D'ERREUR
						ControlUtils.debugMessage(request.toString() + " " + response.getStatusCode() + " " + response.getStatusText());
					}					
				}

				@Override
				public void onError(Request request, Throwable exception) {
					ControlUtils.exceptionMessage(exception);
				}

			});
		} catch (RequestException e) {
			ControlUtils.exceptionMessage(e);
		}

	}

	/**
	 * Logout from the current connexion to a SEWELIS account
	 */
	public void sewelisLogout() {
		String logoutRequestString = serverAdress + "/logout";
		logoutRequestString += "?userKey=" + userKey;
		navBar.setServerStatusMessage("Waiting...");

		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, URL.encode(logoutRequestString));
		try {
			builder.sendRequest(null, new RequestCallback() {

				@Override
				public void onResponseReceived(Request request, Response response) {
					if (200 == response.getStatusCode()) {
						Document statusDoc = XMLParser.parse(response.getText());
						Element docElement = statusDoc.getDocumentElement();
						String status = docElement.getAttribute("status");
						if(!status.equals("ok")) {
							ControlUtils.debugMessage(docElement.getNodeValue());
							navBar.setServerStatusHovertext(docElement.getNodeValue());
						} else {
							Cookies.setCookie(cookiesUserLogin, "");
							Cookies.setCookie(cookiesUserkey, "");
						}
					} else {
						// FIXME GESTION DES MESSAGE D'ERREUR
						ControlUtils.debugMessage(request.toString() + " " + response.getStatusCode() + " " + response.getStatusText());
					}
				}

				@Override
				public void onError(Request request, Throwable exception) {
					ControlUtils.exceptionMessage(exception);
				}

			});
		} catch (RequestException e) {
			ControlUtils.exceptionMessage(e);
		}
	}
	
	public void sewelisResultsOfStatement(String statString ) {
		ControlUtils.debugMessage("resultsOfStatement (" + statString + ") ");
		String resultsOfStatementRequestString = serverAdress + "/resultsOfStatement?userKey=" + userKey ;
		resultsOfStatementRequestString += "&storeName=" + currentStore.getName(); 
		resultsOfStatementRequestString += "&statement=" + URL.encodeQueryString(statString);
		navBar.setServerStatusMessage("Waiting...");
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, resultsOfStatementRequestString);

		try {
			builder.sendRequest(null, new RequestCallback() {			
				@Override
				public void onError(Request request, Throwable exception) {
					ControlUtils.exceptionMessage(exception);
				}

				@Override
				public void onResponseReceived(Request request, Response response) {
					if (200 == response.getStatusCode()) {
						Document statusDoc = XMLParser.parse(response.getText());
						Element docElement = statusDoc.getDocumentElement();
						String status = docElement.getAttribute("status");
						navBar.setServerStatusMessage(status);
						if(status == "ok") {
							ControlUtils.debugMessage(docElement);
						} else {
							navBar.setServerStatusMessage(docElement.getAttribute("status"));
							if(docElement.getFirstChild().getNodeName() == "message") {
								ControlUtils.debugMessage( docElement.getFirstChild().toString());
								navBar.setServerStatusHovertext(docElement.getFirstChild().toString());
							}
						}
					} else {
						// TODO GESTION DES MESSAGE D'ERREUR
						ControlUtils.debugMessage(request.toString() + " " + response.getStatusCode() + " " + response.getStatusText());
					}
				}
			});
		} catch (RequestException e) {
			ControlUtils.exceptionMessage(e);
		}
	}


	// STORES LISTS
	/**
	 * Refresh the list of available stores
	 */
	public void sewelisVisibleStores() {
		try {
			String visibleStoresRequestString = serverAdress + "/visibleStores?";
			visibleStoresRequestString += "userKey=" + userKey;
			RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, URL.encode(visibleStoresRequestString));
			navBar.setServerStatusMessage("Retrieving stores...");
			builder.sendRequest(null, new RequestCallback() {			
				@Override
				public void onError(Request request, Throwable exception) {
				}

				@Override
				public void onResponseReceived(Request request, Response response) {
					if (200 == response.getStatusCode()) {
						Document storeListDoc = XMLParser.parse(response.getText());
						NodeList storeListNode = storeListDoc.getDocumentElement().getChildNodes();
						ArrayList<Store> storeList = new ArrayList<Store>();
						Element docElement = storeListDoc.getDocumentElement();
						String status = docElement.getAttribute("status");
						navBar.setServerStatusMessage(status);
						for(int i = 0; i< storeListNode.getLength(); i++) {
							Node storeNode = storeListNode.item(i);
							String storeName = storeNode.getAttributes().getNamedItem("storeName").getNodeValue();
							String storeRole = storeNode.getAttributes().getNamedItem("role").getNodeValue();
							String storeLabel = storeNode.getFirstChild().getNodeValue();
							Store store = new Store(storeName, storeLabel, storeRole);
							if(storeRole.equals("publisher") /*|| storeRole.equals("collaborator")*/ || storeRole.equals("admin")) {
								storeMapByName.put(storeName, store) ;
								storeMapByLabel.put(storeLabel, store) ;
								storeList.add(store);
							}
						}
						navBar.setStoreList(storeList);
						if(status != "ok") {
							navBar.setServerStatusMessage(docElement.getAttribute("status"));
							if(docElement.getFirstChild().getNodeName() == "message") {
								ControlUtils.debugMessage( docElement.getFirstChild().toString());
								navBar.setServerStatusHovertext(docElement.getFirstChild().toString());
							}
						}

					} else {
						ControlUtils.debugMessage(request.toString() + " " + response.getStatusCode() + " " + response.getStatusText());
					}
				}
			});
		}
		catch(Exception e)
		{
			ControlUtils.exceptionMessage(e);
		}
	}

	

//	// STORES ACCESSORS
//	public void exportRDF(String extension) {
//		String filename = currentStore.getName() + "." + extension;
//		String exportRDFString = serverAdress + "/exportRDF?";
//		exportRDFString += "userKey=" + userKey;
//		exportRDFString += "&storeName=" + currentStore.getName();
//		exportRDFString += "&extension=" + extension;
//		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, URL.encode(exportRDFString));
//		try {
//			builder.sendRequest(null, new RequestCallback() {
//				@Override
//				public void onResponseReceived(Request request, Response response) {
//					if (200 == response.getStatusCode()) {
//						Window.alert(response.getText());
//					}
//				}
//
//				@Override
//				public void onError(Request request, Throwable exception) {
//
//				}	
//			});
//		} catch (RequestException e) {
//			Utils.exceptionMessage(e);
//		}
//	}

		/**
		 * Refresh the namespace list associated to the current store from the server
		 */
		public void sewelisStoreXmlns() {
			if(this.currentStore != null) {
			try {
				String storeXmlnsRequestString = serverAdress + "/storeXmlns?";
				storeXmlnsRequestString += "userKey=" + userKey;
				storeXmlnsRequestString += "&storeName=" + currentStore.getName();
				RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, URL.encode(storeXmlnsRequestString));
				navBar.setServerStatusMessage("Retrieving namespaces...");
				builder.sendRequest(null, new RequestCallback() {			
					@Override
					public void onError(Request request, Throwable exception) {
					}

					@Override
					public void onResponseReceived(Request request, Response response) {
						if (200 == response.getStatusCode()) {
							Document storeListDoc = XMLParser.parse(response.getText());
							Element docElement = storeListDoc.getDocumentElement();
							String status = docElement.getAttribute("status");
							navBar.setServerStatusMessage(status);
							if(status != "ok") {
								navBar.setServerStatusMessage(docElement.getAttribute("status"));
								if(docElement.getFirstChild().getNodeName() == "message") {
									ControlUtils.debugMessage( docElement.getFirstChild().toString());
									navBar.setServerStatusHovertext(docElement.getFirstChild().toString());
								}
							} else {
								if(docElement.getNodeName().equals("storeXmlnsResponse")) {
									NodeList childs = docElement.getChildNodes();
									for(int i = 0; i < childs.getLength(); i++) {
										Node child = childs.item(i);
										if(child.getNodeName().equals("namespaceDefinition")) {
											String ns = child.getAttributes().getNamedItem("namespace").getNodeValue();
											String prefix = child.getAttributes().getNamedItem("prefix").getNodeValue();
											currentStore.addNamespace(prefix, ns);
										}
									}
									refreshNamespaceList();
								}
							}

						} else {
							ControlUtils.debugMessage(request.toString() + " " + response.getStatusCode() + " " + response.getStatusText());
						}
					}
				});
			}
			catch(Exception e)
			{
				ControlUtils.exceptionMessage(e);
			}
			}
		}
	//
	//	/**
	//	 * Utilité inconnue (Genre d'entité ? Class, property, etc ... )
	//	 */
	//	public void uriDescription() {
	//
	//	}
	//
	//
	//	/**
	//	 * Erreur 404 à l'accès manuel
	//	 */
	//	public void resultsOfStatements() {
	//
	//	}
	//
	//	public void defineBase() {
	//
	//	}
	//
	//	public void defineNamespace() {
	//
	//	}
	//
	//	public void addTriple() {
	//
	//	}
	//
	//	public void removeTriple() {
	//
	//	}
	//
	//	public void replaceObject() {
	//
	//	}

//	public void importRdf() {
//
//	}
//
//	public void importUri() {
//
//	}

	// NAVIGATION
		/**
		 * Place the current Place object to the root place of the current Store
		 */
	public void sewelisGetPlaceRoot() {
//		ControlUtils.debugMessage("getPlaceRoot");
		if(currentStore != null) {
			String placeHomeRequestString = serverAdress + "/getPlaceRoot?";
			placeHomeRequestString += "userKey=" + userKey;
			placeHomeRequestString += "&storeName=" + currentStore.getName();
			navBar.setServerStatusMessage("Waiting...");
			RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, URL.encode(placeHomeRequestString));
			try {
				builder.sendRequest(null, new RequestCallback() {			
					@Override
					public void onError(Request request, Throwable exception) {
						ControlUtils.debugMessage("getPlaceRoot() ERROR " + exception.getMessage());
					}

					@Override
					public void onResponseReceived(Request request, Response response) {
						if (200 == response.getStatusCode()) {
							Document homePlaceDoc = XMLParser.parse(response.getText());
							Element homePlaceElem = homePlaceDoc.getDocumentElement();
							String status = homePlaceElem.getAttribute("status");
							navBar.setServerStatusMessage(status);
							if(homePlaceElem.getNodeName() == "getPlaceRootResponse" && homePlaceElem.getAttribute("status") == "ok") {
								Node placeNode = homePlaceElem.getFirstChild();
								if(placeNode.getNodeName() == "place") {
									loadPlace(placeNode);
									loadFormContent();
								} else {
									// FIXME GESTION DES MESSAGES D'ERREUR
									ControlUtils.debugMessage("EXPECTED <place> node = " + placeNode);
								}
							} else {
								navBar.setServerStatusMessage(homePlaceElem.getAttribute("status"));
								if(homePlaceElem.getFirstChild().getNodeName() == "message") {
									String message = homePlaceElem.getFirstChild().getFirstChild().getNodeValue();
									ControlUtils.debugMessage(message );
									navBar.setServerStatusHovertext(message);
								}
							}
						} else {
							// FIXME GESTION DES MESSAGES D'ERREUR
							ControlUtils.debugMessage(request.toString() + " " + response.getStatusCode() + " " + response.getStatusText());
						}
					}
				});
			} catch (RequestException e) {
				ControlUtils.debugMessage("SewelisGetPlaceRoot ERROR " + e);
				ControlUtils.exceptionMessage(e);
			}
		}
//		ControlUtils.debugMessage("FIN getPlaceRoot");
	}

	/**
	 * Place the current palce to the home place ( or root in undefined) of the current store
	 */
	public void sewelisGetPlaceHome() {
		ControlUtils.debugMessage("getPlaceHome");
		if(currentStore != null) {
			String placeHomeRequestString = serverAdress + "/getPlaceHome?";
			placeHomeRequestString += "userKey=" + userKey;
			placeHomeRequestString += "&storeName=" + currentStore.getName();
			navBar.setServerStatusMessage("Waiting...");
			RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, URL.encode(placeHomeRequestString));
			try {
				builder.sendRequest(null, new RequestCallback() {			
					@Override
					public void onError(Request request, Throwable exception) {
						Window.alert("getPlaceHome() ERROR " + exception.getMessage());
					}

					@Override
					public void onResponseReceived(Request request, Response response) {
						if (200 == response.getStatusCode()) {
							try {
								Document homePlaceDoc = XMLParser.parse(response.getText());
								Element homePlaceElem = homePlaceDoc.getDocumentElement();
								String status = homePlaceElem.getAttribute("status");
								navBar.setServerStatusMessage(status);
								if(homePlaceElem.getNodeName() == "getPlaceHomeResponse" && homePlaceElem.getAttribute("status") == "ok") {
									Node placeNode = homePlaceElem.getFirstChild();
									if(placeNode.getNodeName() == "place") {
										loadPlace(placeNode);
//										getRootForm();
										setCurrentForm( newForm());
										loadFormContent();
									} else {
										if(placeNode.getNodeName() == "message") {
											ControlUtils.debugMessage( placeNode.getFirstChild().getNodeValue());
										}
									}
								} else {
									navBar.setServerStatusMessage(homePlaceElem.getAttribute("status"));
									if(homePlaceElem.getFirstChild().getNodeName() == "message") {
										String message = homePlaceElem.getFirstChild().getFirstChild().getNodeValue();
										ControlUtils.debugMessage(message );
										navBar.setServerStatusHovertext(message);
									}
								}
							} catch(DOMParseException e) {
								ControlUtils.exceptionMessage(e);
							}
						} else {
							// FIXME GESTION DES MESSAGES D'ERREUR
							ControlUtils.debugMessage(request.toString() + " " + response.getStatusCode() + " " + response.getStatusText());
						}
					}
				});
			} catch (RequestException e) {
				ControlUtils.exceptionMessage(e);
			}
		}
		ControlUtils.debugMessage("FIN getPlaceHome");
	}

	public void sewelisGetPlaceStatement(String statString) {
		sewelisGetPlaceStatement(statString, null);
	}

	/**
	 * Set the current place to the place corresponding to the LispQL statement passed in parameter (for some reason, sewelisChangeFocus has to be called right after)
	 * @param statString statement LispQL
	 * @param event transmitted to changeFocus
	 */
	public void sewelisGetPlaceStatement(final String statString, final FormEvent event) {
		ControlUtils.debugMessage("getPlaceStatement( " + statString + " ) " );
		if(currentStore != null) {
			String placeStatementRequestString = serverAdress + "/getPlaceStatement?";
			placeStatementRequestString += "userKey=" + userKey;
			placeStatementRequestString += "&storeName=" + currentStore.getName();
			placeStatementRequestString += "&statement=" + URL.encodeQueryString(statString);
			navBar.setServerStatusMessage("Waiting...");
			RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, placeStatementRequestString);
			try {
				builder.sendRequest(null, new RequestCallback() {			
					@Override
					public void onError(Request request, Throwable exception) {
						ControlUtils.exceptionMessage(exception);
					}

					@Override
					public void onResponseReceived(Request request, Response response) {
						//						displayDebugMessage("onResponseReceived");
						if (200 == response.getStatusCode()) {
							Document homePlaceDoc = XMLParser.parse(response.getText());
							Element homePlaceElem = homePlaceDoc.getDocumentElement();
							String status = homePlaceElem.getAttribute("status");
							navBar.setServerStatusMessage(status);
							if(homePlaceElem.getNodeName() == "getPlaceStatementResponse" && status == "ok") {
								Node placeNode = homePlaceElem.getFirstChild();
								if(placeNode.getNodeName() == "place") {
									loadPlace(placeNode);
									lastRequestPlace = statString;

									// TODO Rustine pour gérer le focus renvoyé par getPlaceStatement = a étudier
									// Le focused est placé à la racine du statement, ce qui ne permet pas d'avoir de suggestions pour l'objet qui nous interesse
									// La rustine déplace le focused au premier focus de la formule (2 numéro après, premier élément da la première Pair)
									// SALE
									try {
										String focusedId = place.getStatement().getFocusedDisplay();
										String targetFocusId = String.valueOf(Integer.valueOf(focusedId) - 1);
										sewelisChangeFocus(targetFocusId, event);
									} catch(Exception e) {
										ControlUtils.exceptionMessage( e);
									}
								} else {
									// FIXME GESTION DES MESSAGES D'ERREUR
									ControlUtils.debugMessage("EXPECTED <place> node = " + placeNode);
								}
							} else {
								String message =  homePlaceElem.getFirstChild().getFirstChild().getNodeValue();
								ControlUtils.debugMessage(homePlaceElem.getAttribute("status") + ": " + message);
								navBar.setServerStatusHovertext(message);
							}
						} else {
							// FIXME GESTION DES MESSAGES D'ERREUR
							ControlUtils.debugMessage(request.toString() + " " + response.getStatusCode() + " " + response.getStatusText());
						}
					}
				});
			} catch (Exception e) {
				ControlUtils.debugMessage("sewelisGetPlaceStatement EXCEPTION");
				ControlUtils.exceptionMessage(e);
			}
		}
		ControlUtils.debugMessage("FIN getPlaceStatement " );
	}


	/**
	 * Run the given statement, eg. used to create entities
	 * @param statString statement LispQL
	 */
	private void sewelisRunStatement(String statString) {
		ControlUtils.debugMessage("runStatement (" + statString + ") ");
		String runStatementRequestString = serverAdress + "/runStatement?userKey=" + userKey ;
		runStatementRequestString += "&storeName=" + currentStore.getName(); 
		runStatementRequestString += "&statement=" + URL.encodeQueryString(statString);
		navBar.setServerStatusMessage("Waiting...");
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, runStatementRequestString);

		try {
			builder.sendRequest(null, new RequestCallback() {			
				@Override
				public void onError(Request request, Throwable exception) {
					ControlUtils.exceptionMessage(exception);
				}

				@Override
				public void onResponseReceived(Request request, Response response) {
					if (200 == response.getStatusCode()) {
						Document statusDoc = XMLParser.parse(response.getText());
						Element docElement = statusDoc.getDocumentElement();
						String status = docElement.getAttribute("status");
						navBar.setServerStatusMessage(status);
						if(status == "ok") {
//							getRootForm();
//							sewelisGetPlaceHome();
						} else {
							navBar.setServerStatusMessage(docElement.getAttribute("status"));
							if(docElement.getFirstChild().getNodeName() == "message") {
								ControlUtils.debugMessage( docElement.getFirstChild().toString());
								navBar.setServerStatusHovertext(docElement.getFirstChild().toString());
							}
						}
					} else {
						// TODO GESTION DES MESSAGE D'ERREUR
						ControlUtils.debugMessage(request.toString() + " " + response.getStatusCode() + " " + response.getStatusText());
					}
				}
			});
		} catch (RequestException e) {
			ControlUtils.exceptionMessage(e);
		}
	}

	/**
	 * Retrieve completions computed by the SEWELIS server from the given string. Potentially computationally heavy in case of property with no previous values
	 * @param match partial string to be matched
	 * @param event Event whose callback will be used in case of success
	 */
	public void sewelisGetCompletions(final String match, final FormEvent event) {
		String getCompletionsRequestString = serverAdress + "/getCompletions?userKey=" + userKey ;
		getCompletionsRequestString += "&storeName=" + currentStore.getName(); 
		getCompletionsRequestString += "&placeId=" + place.getId(); 
		getCompletionsRequestString += "&matchingKey=" + match;
		navBar.setServerStatusMessage("Waiting...");
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, URL.encode(getCompletionsRequestString));

		try {
			builder.sendRequest(null, new RequestCallback() {			
				@Override
				public void onError(Request request, Throwable exception) {
					ControlUtils.exceptionMessage(exception);
				}

				@Override
				public void onResponseReceived(Request request, Response response) {
					if (200 == response.getStatusCode()) {
						Document statusDoc = XMLParser.parse(response.getText());
						Element docElement = statusDoc.getDocumentElement();
						String status = docElement.getAttribute("status");
						navBar.setServerStatusMessage(status);
						if(status == "ok") {
							Node responseNode = docElement.getFirstChild();
							if(responseNode.getNodeName().equals("completions")) {
								LinkedList<Increment> result = new LinkedList<Increment>();
								if(responseNode.hasChildNodes()) {
									Node currNode = responseNode.getFirstChild();
									do {
										try {
											Increment inc = Parser.parseIncrement(currNode);
											ControlUtils.debugMessage("Controller getCompletions " + inc.getDisplayElement());
											if(inc.getKind() != KIND.CLASS 
													&& inc.getKind() != KIND.INVERSEPROPERTY 
													&& inc.getKind() != KIND.OPERATOR
													&& inc.getKind() != KIND.PROPERTY
													&& inc.getKind() != KIND.RELATION) {
												result.addFirst(inc);
												ControlUtils.debugMessage("Controller getCompletions AJOUT " + inc.getDisplayElement());
											}
										} catch (XMLParsingException e) {
											ControlUtils.exceptionMessage(e);
										}
										currNode = currNode.getNextSibling();
									}
									while(currNode != null);
									Iterator<Increment> itInc = place.getSuggestions().getEntitySuggestions().iterator();
									while(itInc.hasNext()){
										Increment inc = itInc.next();
										if(inc.getKind() != KIND.CLASS 
										&& inc.getKind() != KIND.INVERSEPROPERTY 
										&& inc.getKind() != KIND.OPERATOR
										&& inc.getKind() != KIND.PROPERTY
										&& inc.getKind() != KIND.RELATION
										&& ! result.contains(inc)) {
											result.addLast(inc);
//											ControlUtils.debugMessage("Controller getCompletions AJOUT " + inc.getDisplayElement());
										}
									}
									if(!result.isEmpty()) {
										place.setCurrentCompletions(result);
										event.getCallback().call(instance());
									}
								}
							}
						}
					} else {
						// TODO GESTION DES MESSAGE D'ERREUR
						ControlUtils.debugMessage(request.toString() + " " + response.getStatusCode() + " " + response.getStatusText());
					}
				}
			});
		} catch (RequestException e) {
			ControlUtils.exceptionMessage(e);
		}
	}

	/**
	 * Corresponding function to sur showMost SEWLIS API function, return the most relaxed suggestions reachable
	 * @param event whose callback will be run in case of success
	 */
	public void sewelisShowMost(final FormEvent event) {
		String showMostRequestString = serverAdress + "/showMost?userKey=" + userKey ;
		showMostRequestString += "&storeName=" + currentStore.getName(); 
		showMostRequestString += "&placeId=" + place.getId(); 
		navBar.setServerStatusMessage("Waiting...");
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, URL.encode(showMostRequestString));

		try {
			builder.sendRequest(null, new RequestCallback() {			
				@Override
				public void onError(Request request, Throwable exception) {
					ControlUtils.exceptionMessage(exception);
				}

				@Override
				public void onResponseReceived(Request request, Response response) {
					if (200 == response.getStatusCode()) {
						Document statusDoc = XMLParser.parse(response.getText());
						Element docElement = statusDoc.getDocumentElement();
						String status = docElement.getAttribute("status");
						navBar.setServerStatusMessage(status);
						if(status == "ok") {
							Node placeNode = docElement.getFirstChild();
							if(placeNode.getNodeName().equals("place")) {
								loadPlace(placeNode);
								if(event != null) {
									event.getCallback().call(instance());
								}
							}
						} else {
							navBar.setServerStatusMessage(docElement.getAttribute("status"));
							if(docElement.getFirstChild().getNodeName() == "message") {
								ControlUtils.debugMessage( docElement.getFirstChild().toString());
								navBar.setServerStatusHovertext(docElement.getFirstChild().toString());
							}
						}
					} else {
						// TODO GESTION DES MESSAGE D'ERREUR
						ControlUtils.debugMessage(request.toString() + " " + response.getStatusCode() + " " + response.getStatusText());
					}
				}
			});
		} catch (RequestException e) {
			ControlUtils.exceptionMessage(e);
		}
	}

	public void sewelisShowMore() {
		sewelisShowMore(null);
	}


	/**
	 * Corresponding function to sur showMore SEWLIS API function, return the next reachable relaxed suggestions
	 * @param event whose callback will be run in case of success
	 */
	public void sewelisShowMore(final FormEvent event) {
		String showMoreRequestString = serverAdress + "/showMore?userKey=" + userKey ;
		showMoreRequestString += "&storeName=" + currentStore.getName(); 
		showMoreRequestString += "&placeId=" + place.getId(); 
		navBar.setServerStatusMessage("Waiting...");
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, URL.encode(showMoreRequestString));

		try {
			builder.sendRequest(null, new RequestCallback() {			
				@Override
				public void onError(Request request, Throwable exception) {
					ControlUtils.exceptionMessage(exception);
				}

				@Override
				public void onResponseReceived(Request request, Response response) {
					if (200 == response.getStatusCode()) {
						Document statusDoc = XMLParser.parse(response.getText());
						Element docElement = statusDoc.getDocumentElement();
						String status = docElement.getAttribute("status");
						navBar.setServerStatusMessage(status);
						if(status == "ok") {
							Node placeNode = docElement.getFirstChild();
							if(placeNode.getNodeName().equals("place")) {
								loadPlace(placeNode);
								
								if(event != null) {
									event.getCallback().call(instance());
								}
							}
						} else {
							navBar.setServerStatusMessage(docElement.getAttribute("status"));
							if(docElement.getFirstChild().getNodeName() == "message") {
								ControlUtils.debugMessage( docElement.getFirstChild().toString());
								navBar.setServerStatusHovertext(docElement.getFirstChild().toString());
							}
						}
					} else {
						// TODO GESTION DES MESSAGE D'ERREUR
						ControlUtils.debugMessage(request.toString() + " " + response.getStatusCode() + " " + response.getStatusText());
					}
				}
			});
		} catch (RequestException e) {
			ControlUtils.exceptionMessage(e);
		}
	}
	
	public void sewelisShowLess() {
		sewelisShowLess(null);
	}

	/**
	 * Corresponding function to sur showLess SEWLIS API function, reduce the relaxation of suggestions by one rank
	 * @param event whose callback will be run in case of success
	 */
	public void sewelisShowLess(final FormEvent event) {
		String showLessRequestString = serverAdress + "/showLess?userKey=" + userKey ;
		showLessRequestString += "&storeName=" + currentStore.getName(); 
		showLessRequestString += "&placeId=" + place.getId(); 
		navBar.setServerStatusMessage("Waiting...");
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, URL.encode(showLessRequestString));

		try {
			builder.sendRequest(null, new RequestCallback() {			
				@Override
				public void onError(Request request, Throwable exception) {
					ControlUtils.exceptionMessage(exception);
				}

				@Override
				public void onResponseReceived(Request request, Response response) {
					if (200 == response.getStatusCode()) {
						Document statusDoc = XMLParser.parse(response.getText());
						Element docElement = statusDoc.getDocumentElement();
						String status = docElement.getAttribute("status");
						navBar.setServerStatusMessage(status);
						if(status == "ok") {
							Node placeNode = docElement.getFirstChild();
							if(placeNode.getNodeName().equals("place")) {
								loadPlace(placeNode);
								if(event != null && event.getCallback() != null /*&& event instanceof LessCompletionsEvent*/) {
									event.getCallback().call(instance());
								}
							}
						} else {
							navBar.setServerStatusMessage(docElement.getAttribute("status"));
							if(docElement.getFirstChild().getNodeName() == "message") {
								ControlUtils.debugMessage( docElement.getFirstChild().toString());
								navBar.setServerStatusHovertext(docElement.getFirstChild().toString());
							}
						}
					} else {
						// TODO GESTION DES MESSAGE D'ERREUR
						ControlUtils.debugMessage(request.toString() + " " + response.getStatusCode() + " " + response.getStatusText());
					}
				}
			});
		} catch (RequestException e) {
			ControlUtils.exceptionMessage(e);
		}
	}


	/**
	 * Corresponding function to showLeast SEWELIS API function, reduce the relaxation of suggestions to 0
	 * @param event whose callback will be run in case of success
	 */
	public void sewelisShowLeast(final FormEvent event) {
		String showLeastRequestString = serverAdress + "/showLeast?userKey=" + userKey ;
		showLeastRequestString += "&storeName=" + currentStore.getName(); 
		showLeastRequestString += "&placeId=" + place.getId(); 
		navBar.setServerStatusMessage("Waiting...");
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, URL.encode(showLeastRequestString));

		try {
			builder.sendRequest(null, new RequestCallback() {			
				@Override
				public void onError(Request request, Throwable exception) {
					ControlUtils.exceptionMessage(exception);
				}

				@Override
				public void onResponseReceived(Request request, Response response) {
					if (200 == response.getStatusCode()) {
						Document statusDoc = XMLParser.parse(response.getText());
						Element docElement = statusDoc.getDocumentElement();
						String status = docElement.getAttribute("status");
						navBar.setServerStatusMessage(status);
						if(status == "ok") {
							Node placeNode = docElement.getFirstChild();
							if(placeNode.getNodeName().equals("place")) {
								loadPlace(placeNode);
								if(event != null) {
									event.getCallback().call(instance());
								}
							}
						} else {
							navBar.setServerStatusMessage(docElement.getAttribute("status"));
							if(docElement.getFirstChild().getNodeName() == "message") {
								ControlUtils.debugMessage( docElement.getFirstChild().toString());
								navBar.setServerStatusHovertext(docElement.getFirstChild().toString());
							}
						}
					} else {
						// TODO GESTION DES MESSAGE D'ERREUR
						ControlUtils.debugMessage(request.toString() + " " + response.getStatusCode() + " " + response.getStatusText());
					}
				}
			});
		} catch (RequestException e) {
			ControlUtils.exceptionMessage(e);
		}
	}
	
	/**
	 * Corresponding function to uriDescription SEWELIS API function, return a place with a statement containing all statement known starting from an uri contained in event.getSource.getData
	 * @param event event with a URIWidget at its source
	 */
	public void sewelisUriDescription(final DescribeUriEvent event) {
		String showMoreRequestString = serverAdress + "/uriDescription?userKey=" + userKey ;
		showMoreRequestString += "&storeName=" + currentStore.getName(); 
		showMoreRequestString += "&userkey=" + this.userKey;
		showMoreRequestString += "&uri=" + event.getSource().getData().getUri();

		navBar.setServerStatusMessage("Waiting...");
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, URL.encode(showMoreRequestString));

		try {
			builder.sendRequest(null, new RequestCallback() {			
				@Override
				public void onError(Request request, Throwable exception) {
					ControlUtils.exceptionMessage(exception);
				}

				@Override
				public void onResponseReceived(Request request, Response response) {
					if (200 == response.getStatusCode()) {
						Document statusDoc = XMLParser.parse(response.getText());
						Element docElement = statusDoc.getDocumentElement();
						String status = docElement.getAttribute("status");
						navBar.setServerStatusMessage(status);
						if(status != "ok") {
							ControlUtils.debugMessage("sewelisUriDescription ERROR " + status);
							if(docElement.getFirstChild().getNodeName() == "message") {
								String message = docElement.getFirstChild().getFirstChild().getNodeValue();
								ControlUtils.debugMessage( message);
								navBar.setServerStatusHovertext(message);
							}
						} else {
							Node root = docElement.getFirstChild();
							try {

								ControlUtils.debugMessage("uridescription statement statement: " + root.toString());
								SafeHtml stat = ViewUtils.toSimpleHtml(Parser.parseDisplayNode(root));
								
								if(stat != null) {
									event.getCallback().call(stat.asString());
								}
							} catch (XMLParsingException e) {
								ControlUtils.exceptionMessage(e);
								ControlUtils.debugMessage("uridescription statement parse failed");
							}
						}
					} else {
						// TODO GESTION DES MESSAGE D'ERREUR
						ControlUtils.debugMessage(request.toString() + " " + response.getStatusCode() + " " + response.getStatusText());
					}
				}
			});
		} catch (RequestException e) {
			ControlUtils.exceptionMessage(e);
		}
	}
	/**
	 * Define a new prefixed namespace for the current store 
	 * @param prefix 
	 * @param adress
	 */
	public void sewelisDefineNamespace(String prefix, String adress) {
		String showMoreRequestString = serverAdress + "/defineNamespace?userKey=" + userKey ;
		showMoreRequestString += "&storeName=" + currentStore.getName(); 
		showMoreRequestString += "&userkey=" + this.userKey;
		showMoreRequestString += "&prefix=" + prefix;
		showMoreRequestString += "&uri=" + adress;

		navBar.setServerStatusMessage("Waiting...");
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, URL.encode(showMoreRequestString));

		try {
			builder.sendRequest(null, new RequestCallback() {			
				@Override
				public void onError(Request request, Throwable exception) {
					ControlUtils.exceptionMessage(exception);
				}

				@Override
				public void onResponseReceived(Request request, Response response) {
					if (200 == response.getStatusCode()) {
						Document statusDoc = XMLParser.parse(response.getText());
						Element docElement = statusDoc.getDocumentElement();
						String status = docElement.getAttribute("status");
						navBar.setServerStatusMessage(status);
						if(status != "ok") {
							ControlUtils.debugMessage("sewelisDefineNamespace ERROR " + status);
							if(docElement.getFirstChild().getNodeName() == "message") {
								String message = docElement.getFirstChild().getFirstChild().getNodeValue();
								ControlUtils.debugMessage( message);
								navBar.setServerStatusHovertext(message);
							}
						}
					} else {
						// TODO GESTION DES MESSAGE D'ERREUR
						ControlUtils.debugMessage(request.toString() + " " + response.getStatusCode() + " " + response.getStatusText());
					}
				}
			});
		} catch (RequestException e) {
			ControlUtils.exceptionMessage(e);
		}
	}


	// DATA LOAD FUNCTIONS 
	/**
	 * Parse and load the XML Place node passed in arguments, call refreshAnswers
	 * @param placeNode a XML Place node
	 */
	private void loadPlace(Node placeNode) {
//		ControlUtils.debugMessage("loadPlace " + placeNode.toString());
		try {
			place = Parser.parsePlace(placeNode);
			refreshAnswers();
//			ControlUtils.debugMessage("Place " + place.getSuggestions().getEntitySuggestions().size() + " answers " + place.getAnswers().getContentRows().size() + " entity suggestions " + place.getSuggestions().getRelationSuggestions().size() + " relations suggestions");
		} catch (XMLParsingException e) {
			ControlUtils.exceptionMessage(e);
		}
//		ControlUtils.debugMessage("FIN loadPlace");
	}
	
	/**
	 * Call for run to create the data contained in the transmitted form, go back to placeRoot
	 * Supposed to be used to also send log to an external serveur
	 * @param f FormWidget containing data to be created
	 */
//	@SuppressWarnings("deprecation")
	private void finish(FormWidget f) {
		ControlUtils.debugMessage("Controller finish");
		sewelisRunStatement(/*"get " +*/ f.getData().toLispql(true) + "");
		if(f.getData() == this.rootForm()) {
			// Retour au formulaire de départ
			sewelisGetPlaceRoot();
//			this.toRootForm();
			
			// Logging des actions
			ControlUtils.debugMessage("Nombre d'actions: " + getNumberOfActions());
//			Date nowDate = new Date();
//			ControlUtils.debugMessage(userLogin + " " + currentStore.getName() + " " + this.form.getType().getEntityUri() + " " + this.startEditDate.getHours()+":"+this.startEditDate.getMinutes()+":"+this.startEditDate.getSeconds() + " " + nowDate.getHours()+":"+nowDate.getMinutes()+":"+nowDate.getSeconds() + " " + this.getNumberOfActions());
//			this.sendExperimentLog(userLogin, currentStore.getName(), this.form.getType().getElementUri(), this.startEditDate.getHours()+":"+this.startEditDate.getMinutes()+":"+this.startEditDate.getSeconds(), nowDate.getHours()+":"+nowDate.getMinutes()+":"+nowDate.getSeconds(), this.getNumberOfActions());
			numberOfActions = 0;
		}
		ControlUtils.debugMessage("Controller finish END");
	}


	// NAVIGATION 
//	public void sewelisDoRun() {
//		String insertIncrementRequestString = serverAdress + "/doRun?userKey=" + userKey ;
//		insertIncrementRequestString += "&storeName=" + currentStore.getName(); 
//		insertIncrementRequestString += "&placeId=" + place.getId();
//		navBar.setServerStatusMessage("Waiting...");
//		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, URL.encode(insertIncrementRequestString));
//		try {
//			builder.sendRequest(null, new RequestCallback() {			
//				@Override
//				public void onError(Request request, Throwable exception) {
//					ControlUtils.exceptionMessage(exception);
//				}
//
//				@Override
//				public void onResponseReceived(Request request, Response response) {
//					if (200 == response.getStatusCode()) {
//						Document statusDoc = XMLParser.parse(response.getText());
//						Element docElement = statusDoc.getDocumentElement();
//						String status = docElement.getAttribute("status");
//						navBar.setServerStatusMessage(status);
//						if(status == "ok") {
//							loadPlace(docElement.getFirstChild());
//						}
//					} else {
//						// TODO GESTION DES MESSAGE D'ERREUR
//						ControlUtils.debugMessage(request.toString() + " " + response.getStatusCode() + " " + response.getStatusText());
//					}
//				}
//			});
//		} catch (RequestException e) {
//			ControlUtils.exceptionMessage(e);
//		}
//	}


	// TRANSFORMATION
	public void sewelisChangeFocus(String focusId) {
		sewelisChangeFocus(focusId, null);
	}

	/**
	 * In SEWELIS Places, elements in statement can be set as focus, identified by numbers
	 * With this function, the statement stay the same, but the place change
	 * @param focusId number of the element in the statement
	 * @param followUp event whose callback will be called in case of success
	 */
	public void sewelisChangeFocus(String focusId, final FormEvent followUp) {
		String insertIncrementRequestString = serverAdress + "/changeFocus?userKey=" + userKey ;
		insertIncrementRequestString += "&storeName=" + currentStore.getName(); 
		insertIncrementRequestString += "&placeId=" + place.getId(); 
		insertIncrementRequestString += "&focusId=" + focusId;
		navBar.setServerStatusMessage("Waiting...");
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, URL.encode(insertIncrementRequestString));
		try {
			builder.sendRequest(null, new RequestCallback() {			
				@Override
				public void onError(Request request, Throwable exception) {
					ControlUtils.exceptionMessage(exception);
				}

				@Override
				public void onResponseReceived(Request request, Response response) {
					ControlUtils.debugMessage("Controller sewelisChangeFocus");
					if (200 == response.getStatusCode()) {
						Document statusDoc = XMLParser.parse(response.getText());
						Element docElement = statusDoc.getDocumentElement();
						String status = docElement.getAttribute("status");
						navBar.setServerStatusMessage(status);
						if(status == "ok") {
							Node placeNode = docElement.getFirstChild();
							if(placeNode.getNodeName().equals("place")) {
								loadPlace(placeNode);
								if(followUp != null) {
									ControlUtils.debugMessage("changeFocus followUp: " + followUp.getClass().getSimpleName());
									if(followUp instanceof StatementChangeEvent) {
										onStatementChange((StatementChangeEvent) followUp);
									} else if(followUp.getCallback() != null) {
										followUp.getCallback().call(instance());
									}
								}
							}
						} else {
							ControlUtils.debugMessage("sewelisChangeFocus ERROR " + status);
							if(docElement.getFirstChild().getNodeName() == "message") {
								String message = docElement.getFirstChild().getFirstChild().getNodeValue();
								ControlUtils.debugMessage( message);
								navBar.setServerStatusHovertext(message);
							}
						}
					} else {
						// TODO GESTION DES MESSAGE D'ERREUR
						ControlUtils.debugMessage(request.toString() + " " + response.getStatusCode() + " " + response.getStatusText());
					}

					ControlUtils.debugMessage("Controller sewelisChangeFocus END");
				}
			});
		} catch (Exception e) {
			ControlUtils.debugMessage("SewelisChangeFocus EXCEPTION ");
			ControlUtils.exceptionMessage(e);
		}
	}

//	public Place sewelisChangeFocusAlone(Place here, String focusId) {
//		//		mainPage.addDebugMessage("changeFocus " + f.toString());
//		Place result = null;
//		String insertIncrementRequestString = serverAdress + "/changeFocus?userKey=" + userKey ;
//		insertIncrementRequestString += "&storeName=" + currentStore.getName(); 
//		insertIncrementRequestString += "&placeId=" + here.getId(); 
//		insertIncrementRequestString += "&focusId=" + focusId;
//		navBar.setServerStatusMessage("Waiting...");
//		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, URL.encode(insertIncrementRequestString));
//		try {
//			PlaceRequestCallback placeCallback = new PlaceRequestCallback() {
//				
//				private Place resultPlace = null;
//				
//				@Override
//				public void onError(Request request, Throwable exception) {
//					ControlUtils.exceptionMessage(exception);
//				}
//
//				@Override
//				public void onResponseReceived(Request request, Response response) {
//					if (200 == response.getStatusCode()) {
//						Document statusDoc = XMLParser.parse(response.getText());
//						Element docElement = statusDoc.getDocumentElement();
//						String status = docElement.getAttribute("status");
//						navBar.setServerStatusMessage(status);
//						if(status == "ok") {
//							Node placeNode = docElement.getFirstChild();
//							if(placeNode.getNodeName().equals("place")) {
//								try {
//									resultPlace = Parser.parsePlace(placeNode);
//								} catch (XMLParsingException e) {
//									ControlUtils.exceptionMessage(e);
//								}
//							}
//						}else {
//							ControlUtils.debugMessage("sewelisChangeFocusAlone ERROR " + status);
//							if(docElement.getFirstChild().getNodeName() == "message") {
//								String message = docElement.getFirstChild().getFirstChild().getNodeValue();
//								ControlUtils.debugMessage( message);
//								navBar.setServerStatusHovertext(message);
//							}
//						}
//					} else {
//						// TODO GESTION DES MESSAGE D'ERREUR
//						ControlUtils.debugMessage(request.toString() + " " + response.getStatusCode() + " " + response.getStatusText());
//					}
//				}
//
//				@Override
//				public Place getPlace() {
//					return resultPlace;
//				}
//			};
//			builder.sendRequest(null, placeCallback );
//			result = placeCallback.getPlace();
//		} catch (RequestException e) {
//			ControlUtils.exceptionMessage(e);
//		}
//		return result;
//	}


	// VIEW REFRESH

	/**
	 * Refresh the displayed answers from the place answers
	 */
	private void refreshAnswers() {
		//		Utils.debugMessage("refreshAnswers " + place.getAnswers());
		mainPage.ansWidget.setAnswers(place.getAnswers());
		//		if(place.getAnswers().getCount() == 0 && place.hasMore()) {
		//			showMore();
		//		}
		//		Utils.debugMessage("FIN refreshAnswers");
	}

	/**
	 * 
	 * @return a Form without root
	 */
	private Form newForm() {
		return newForm(null);
	}

	/**
	 * 
	 * @param l Form line parent of the form (the form is nested)
	 * @return a (nested) form 
	 */
	private Form newForm(FormLine l) {
		Form result = new Form(l);

		if(l != null) {
			sewelisGetPlaceStatement(this.lispqlStatementQuery(l));
		}

		return result;
	}


	/**
	 * Initializations and handlers attribution
	 * Main function of the GWT module
	 */
	@Override
	public void onModuleLoad() {
		// Init de singleton
		_instance = this;
		
		// Récupération de l'adresse du serveur
		try {
			Dictionary appSettings = Dictionary.getDictionary("formulisSettings");
			String serverAdressString = appSettings.get("serverAdress");
			String uriBaseString = appSettings.get("uriBaseAdress");
			ControlUtils.debugMessage("Controller onModuleLoad retrieve server adress: " + serverAdressString);
			serverAdress = serverAdressString;
			uriBaseAdress = uriBaseString;
		} catch(MissingResourceException e) {
			ControlUtils.debugMessage("Controller onModuleLoad couldn't load server adress from main page");
			ControlUtils.exceptionMessage(e);
		}
		
		Parser.setControl(this);
//		initializeProfilesFromCookie(); // FIXME  remettre la gestion des profiles
		form = new Form(null);
		mainPage = new MainPage(this);

		RootPanel.get().add(navBar);
		RootPanel.get().add(mainPage);
		RootPanel.get().add(new FooterWidget());
//		RootPanel.get().setStyleName("root");
		
		if((Cookies.getCookie(cookiesUserLogin) != null && Cookies.getCookie(cookiesUserLogin) != "") 
				&& (Cookies.getCookie(cookiesUserkey) != null && Cookies.getCookie(cookiesUserkey) != "")) {
			userKey = Cookies.getCookie(cookiesUserkey);
			userLogin = Cookies.getCookie(cookiesUserLogin);
			navBar.loginWid.loggedUsernameLabel.setText(userLogin);
			navBar.loginWid.setLogState(LOGIN_STATE.LOGGED);
		}

		// History

		History.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				String historyToken;
				try {
					historyToken = HistoryUtils.getProfileFromHistoryToken(event.getValue());
					mainPage.getSettingsWidget().setStatePermalink(historyToken);
					Profile newForm = Parser.parseProfile(XMLParser.parse(historyToken).getDocumentElement());
					setProfile(newForm);
				} catch (XMLParsingException|InvalidHistoryState|NumberFormatException e) {
					ControlUtils.exceptionMessage(e);
				} 
			}
		});
		

//		// handlers attribution
//		mainPage.getSettingsWidget().profileModeButton.addClickHandler(this);
//		mainPage.getSettingsWidget().profileCreateButton.addClickHandler(this);
//		mainPage.getSettingsWidget().profileClearButton.addClickHandler(this);
//		mainPage.getSettingsWidget().profileGoButton.addClickHandler(this);
//		mainPage.getSettingsWidget().profileDeleteButton.addClickHandler(this);
//		mainPage.getSettingsWidget().profileEditSave.addClickHandler(this);
//		mainPage.getSettingsWidget().profileEditClear.addClickHandler(this);
//		mainPage.getSettingsWidget().profileEditReload.addClickHandler(this);
		mainPage.getSettingsWidget().getNamespaceDefineButton().addClickHandler(this);
		
		
		navBar.storeListBox.addChangeHandler(new ChangeHandler(){
			@Override
			public void onChange(ChangeEvent event) {
				if(event.getSource() == navBar.storeListBox) {
					int selectIndex= navBar.storeListBox.getSelectedIndex();
					String selectValue = navBar.storeListBox.getValue(selectIndex);
					setCurrentStore(selectValue);
//					sewelisGetPlaceHome();
					sewelisGetPlaceRoot();
				}
			}
		});

//		navBar.adminPanel.limitBox.setValue(CustomSuggestionWidget.getLimit());
		// LOGIN

		this.navBar.loginWid.notLoggedLoginButton.addClickHandler(this);
		this.navBar.loginWid.logoutButton.addClickHandler(this);
		this.navBar.loginWid.newUserButton.addClickHandler(this);
		this.navBar.loginWid.notLoggedNewuserButton.addClickHandler(this);

		// Uncaught Exceptions
		GWT.setUncaughtExceptionHandler(new GWT.UncaughtExceptionHandler() {
			@Override
			public void onUncaughtException(Throwable e) {
				ControlUtils.exceptionMessage(new Exception(unwrap(e)));
			}
			public Throwable unwrap(Throwable e) {
				if(e instanceof UmbrellaException) {
					UmbrellaException ue = (UmbrellaException) e;
					if(ue.getCauses().size() == 1) {
						return unwrap(ue.getCauses().iterator().next());
					}
				}
				return e;
			}
		});

		// Page preparation
		navBar.loginWid.loggedUsernameLabel.setText(userLogin);

		try {
			sewelisPing();
		} catch (RequestException e1) {
			ControlUtils.exceptionMessage(e1);
		}
		sewelisVisibleStores();
		
		// Ping toutes les 30 secondes
		Timer pingTimer = new Timer() {
			@Override
			public void run() {
				try {
					if(navBar.getServerStatusMessage() != "error") {
						sewelisPing();
					}
				} catch (RequestException e) {
					ControlUtils.exceptionMessage(e);
				}
			}
		};
		pingTimer.scheduleRepeating(30000);

		
		String winState = Window.Location.getParameter("state");
		if(winState != null) {
			String winProfileString = Crypto.deobfuscate(winState);
			ControlUtils.debugMessage("State d'adresse de page : " + winProfileString);
			try {
				this.setProfile(Parser.parseProfile(XMLParser.parse(winProfileString+">").getDocumentElement()));
			} catch (XMLParsingException e1) {
				ControlUtils.exceptionMessage(e1);
			}
		}

	}
	
	// PROFILES

	/**
	 * Interroge la base, crée un profile de base pour la classe, hors rdfs/owl, qui a le plus d'élements
	 */
	public Profile formToProfile() {
//		ControlUtils.debugMessage("Controller formToProfile " + this.mainPage.formWidget.getData());
		if(currentStore != null) {
			Profile pro = new Profile(this.currentStore.getName(), this.currentStore.getName());
			pro.setForm(this.mainPage.formWidget.getData().toProfileForm());

//			ControlUtils.debugMessage("Controller formToProfile FIN " + pro);
			return pro;
		}
//		ControlUtils.debugMessage("Controller formToProfile FIN null");
		return null;
	}

	// FORMULAIRE

	public Form rootForm() {
		return this.form;
	}
	
	public Form finishedForm() {
		return this.extractFinishedForm(form, null);
	}
	
	private LinkedList<? extends FormLine> extractFinishedLines(Form f) {
		LinkedList<FormLine> result = new LinkedList<FormLine>();
		if(! f.isAnonymous()) {
			result.add(f.getType());
		}
		Iterator<FormRelationLine> itRelL = f.relationLinesIterator();
		while(itRelL.hasNext()) {
			FormLine line = itRelL.next();
			
			if(line.isFinishable()) {
				result.add(line);
			} else if(line.getVariableElement() != null && line instanceof FormRelationLine && line.getVariableElement() instanceof Form) {
				FormRelationLine nLine = new FormRelationLine(null, line.getFixedElement());
				nLine.setVariableElement(this.extractFinishedForm((Form) line.getVariableElement(), nLine));
				result.add(nLine);
			}
		}
		
		return result;
	}
	
	/**
	 * Extrait les élément remplis dans le formulaire courant (f)
	 * @param f Formulaire courant
	 * @param parent pour recursivité
	 * @return
	 */
	private Form extractFinishedForm(Form f, FormComponent parent) {
		Form result = new Form(parent);
		
		if(! f.isEmpty()) {
			if(f.isFinishable()) {
				result = f;
			} else {
				result.setTypeLine(f.getType());
				result.addAllLines(this.extractFinishedLines(f));
			}
		}
		
		return result;
	}
	
	
	
	// EVENTS

	/**
	 * Handling click event on the mainpage.
	 * Mostly used for settings and login events
	 */
	@Override
	public void onClick(ClickEvent event) {
//		if(event.getSource() == mainPage.getSettingsWidget().profileModeButton) {
//			this.mainPage.formWidget.toggleProfileMode();
//
//		} else if(event.getSource() == mainPage.getSettingsWidget().profileCreateButton) {
//			if(this.mainPage.formWidget.isInProfileMode()) {
//				Profile pro = formToProfile();
//				pro.setName(mainPage.getSettingsWidget().profileNameBox.getValue());
//				addProfile(pro);
//				reloadNavbarProfileList();
//			}
//
//		} else if(event.getSource() == mainPage.getSettingsWidget().profileClearButton) {
//			this.clearProfiles();
//			reloadNavbarProfileList();
//
//		} else if(event.getSource() == mainPage.getSettingsWidget().profileGoButton) {
//			String select = mainPage.getSettingsWidget().profileList.getSelectedValue();
//			setProfile(findProfile(profiles, select));
//
//		} else if(event.getSource() == mainPage.getSettingsWidget().profileDeleteButton) {
//			String select = mainPage.getSettingsWidget().profileList.getSelectedValue();
//			Profile pro = findProfile(profiles, select);
//			if(pro != null) {
//				removeProfile(pro);
//			}
//			
//			reloadNavbarProfileList();
//		} else 
		if(event.getSource() == mainPage.getSettingsWidget().getNamespaceDefineButton()) {
			if(mainPage.getSettingsWidget().getNamespacePrefixBox().getValue() != "" && mainPage.getSettingsWidget().getNamespaceUriBox().getValue() != "") {
				this.sewelisDefineNamespace(mainPage.getSettingsWidget().getNamespacePrefixBox().getValue(), mainPage.getSettingsWidget().getNamespaceUriBox().getValue());
			}

		} else 
			if(event.getSource() == navBar.loginWid.notLoggedLoginButton) {
			String login = this.navBar.loginWid.notLoggedLoginTextbox.getText();
			String password = this.navBar.loginWid.notLoggedPasswdTextBox.getText();
			this.sewelisLogin(new LoginToken( login, password));

		} else if(event.getSource() == this.navBar.loginWid.logoutButton) {
			sewelisLogout();
			navBar.loginWid.setLogState(LOGIN_STATE.NOT_LOGGED);

			userLogin = "anonymous";
			userKey = "0";
			sewelisVisibleStores();
			
		} else if(event.getSource() == this.navBar.loginWid.notLoggedNewuserButton) {
			navBar.loginWid.setLogState(LOGIN_STATE.NEW_USER);
			
		} else if(event.getSource() == this.navBar.loginWid.newUserButton) {
			String login = this.navBar.loginWid.newUserTextbox.getText();
			String password = this.navBar.loginWid.newUserpasswdTextBox.getText();
			String email = this.navBar.loginWid.newUserEmailTextbox.getText();
			sewelisRegister(new NewUserToken( login, password, email));
			
		} 
//			else if(event.getSource() == this.navBar.adminPanel.profileEditSave) {
//			this.setProfileDocument(navBar.adminPanel.profileEditArea.getText());
//			
//		} else if(event.getSource() == this.navBar.adminPanel.profileEditClear) {
//			navBar.adminPanel.profileEditArea.setText("");
//			
//		} else if(event.getSource() == this.navBar.adminPanel.profileEditReload) {
//			navBar.adminPanel.profileEditArea.setText(getProfileDocument().toString());
//		}

	}

	/**
	 * call sewelisChangeFocus
	 */
	@Override
	public void onFocusChange(Focus f) {
		sewelisChangeFocus(f.getId());
	}

	/**
	 * Clear the current completions, according to the source of the event it will:
	 * - call sewelisGetPlaceStatement if it's a change of selected relation line
	 * - set the form typeline and call sewelisGetPlaceStatement if the source is a form type list ou anonymous
	 * - unset the form type line if the source is a typed form (and call sewelisGetPlaceStatement to logically retract to a type list form)
	 * @param event whose callback will be transmitted in the mentioned cases or directly called otherwise
	 */
	@Override
	public void onLineSelection(LineSelectionEvent event) {
		// SELECTION DE LIGNE
		// la selection d'une ligne entraine un changement de statement

		ControlUtils.debugMessage("Controller onLineSelection ( " + event.getSource() + " )");
		this.place.clearCurrentCompletions();
		// La source est forcément une ligne
		AbstractFormLineWidget widSource = (AbstractFormLineWidget)event.getSource();
		FormWidget widSourceParent = widSource.getParentWidget();
		FormLine dataSource = widSource.getFormLine();
		Form dataSourceParent = dataSource.getParent();
		String queryLineLispql = lispqlStatementQuery(dataSource);

		// Si c'est une relatio ou un form typé et qu'on a un callback pour renvoyer des données, c'est une demande de suggestions
		if(dataSource instanceof FormRelationLine || (dataSource instanceof FormClassLine && ! dataSourceParent.isAnonymous() && event.getCallback() != null)) {
			ControlUtils.debugMessage("Controller onLineSelection ASKING COMPLETIONS");
			if(! queryLineLispql.equals(lastRequestPlace)) { // Si on a pas changé de ligne, pas besoin de recharger les suggestions
				sewelisGetPlaceStatement(queryLineLispql, new StatementChangeEvent(widSource, event.getCallback()));
			} else {
				event.getCallback().call(this);
			}
		}
		else if(dataSource instanceof FormClassLine) { // Selection d'une classe
			ControlUtils.debugMessage("Controller onLineSelection BY A CLASS");
			// Si c'est une classe de litteral
			if( ControlUtils.LITTERAL_URIS.isLitteralType(((URI) dataSource.getFixedElement()).getUri())) {

				// si le form n'a pas encore de type
			} else if(dataSourceParent.isAnonymous() || dataSourceParent.isTypeList()) {
				dataSourceParent.addTypeLine((FormClassLine) dataSource, true);
//				ControlUtils.debugMessage("Controller onLineSelection BY A CLASS SETTING TYPE LINE");
				sewelisGetPlaceStatement(queryLineLispql, new StatementChangeEvent(widSourceParent, widSourceParent.getLoadCallback()));
				
				// Si la ligne avait déjà un type (retractation) et qu'on a pas fourni de callback
			} else {
				ControlUtils.debugMessage("Controller onLineSelection BY A CLASS RESETING TYPE LINE");
				dataSourceParent.clear();
				String queryFormLispql = lispqlStatementQuery(dataSourceParent);
				sewelisGetPlaceStatement(queryFormLispql, new StatementChangeEvent(widSourceParent, widSourceParent.getLoadCallback()));
			}
		}
		incrementNumberOfActions();
	}

	/**
	 * call sewelisGetCompletions(event.getSearch(), event)
	 * @param event
	 */
	@Override
	public void onCompletionAsked(CompletionAskedEvent event) {
		ControlUtils.debugMessage("Controller onCompletionAsked");
		// COMPLETIONS DEMANDEES
		// Les completions doivent être rechargée pour correspondre au statement
		// FIXME Comented for testing
//		if(event.getCallback() != null) {
//			event.getCallback().call(this); 
//		}
		// FIXME Comented for testing
		sewelisGetCompletions(event.getSearch(), event);
		
//		incrementNumberOfActions();
	}

	/**
	 * According to the event source:
	 * - if it is a FormWidget, it will call the event callback
	 * - if it is a finishable RelationLineWidget, it will call sewelisGetPlaceStatement
	 * - if it is an empty line, it will call onCompletionsAsked (with the same event callback)
	 */
	@Override
	public void onStatementChange(StatementChangeEvent event) {
		// CHANGEMENT DE STATEMENT
		// Le contenu du statement a été changé est pret a être chargé dans le formulaire source
		if(event.getSource() instanceof FormWidget) {
			event.getCallback().call(this);
		} else if(event.getSource() instanceof FormRelationLineWidget) {
			FormRelationLineWidget widSource = (FormRelationLineWidget) event.getSource();
			if(widSource.getData().isFinishable()) {
//				ControlUtils.debugMessage("onStatementChange CHANGE BY A FINISHED LINE");
				sewelisGetPlaceStatement(this.lispqlStatementQuery(widSource.getData().getParent()));
			} else {
//				ControlUtils.debugMessage("onStatementChange CHANGE BY A LINE");
				if(event.getCallback() instanceof SuggestionCallback) {
					SuggestionCallback callback = (SuggestionCallback) event.getCallback();
					onCompletionAsked(new CompletionAskedEvent(event.getSource(), callback));
				}
			}
		} else if(event.getSource() instanceof FormRelationLineWidget) {
			event.getCallback().call(this);
		}
		refreshAnswers();
		
//		incrementNumberOfActions();
	}

	/**
	 * Intended use: a FormRelationLineWidget call for the creation of a new element
	 * If it is possible to create a new nested form (there are properties and/or classes to be used), the line is set into guide creation mode, (sewelisGetPlaceStatement is called to load the new form content)
	 * Otherwise, the literal creation widgets (widget + type oracle) are set as variable element of the line
	 */
	@Override
	public void onElementCreation(ElementCreationEvent event) {
		// CREATION D'UN NOUVEL ELEMENT
//		ControlUtils.debugMessage("Controller onElementCreation");
		AbstractFormLineWidget widSource = event.getSource();
		if(widSource instanceof FormRelationLineWidget) {
			FormLine dataSource = widSource.getFormLine();
			Form newDataForm = new Form(dataSource);
			FormWidget newFormWid = new FormWidget(newDataForm, widSource);
			//	boolean newFormFilled = loadFormContent(newFormWid);
			boolean newFormFilled = this.isFormContentLoadable(newFormWid);
//			ControlUtils.debugMessage("Controller onElementCreation " + newFormFilled + " " + newDataForm .isEmpty());
			if(newFormFilled ) {	
				widSource.setLineState(LINE_STATE.GUIDED_CREATION);
				dataSource.setVariableElement(newDataForm);
				widSource.setVariableElement(newFormWid);
				newFormWid.addClickWidgetEventHandler(widSource);
				newDataForm.setTempValue(event.getValue());
	//			ViewUtils.connectFormEventChain(newFormWid, widSource);
	
				String queryLineLispql = lispqlStatementQuery(dataSource);
				sewelisGetPlaceStatement(queryLineLispql, new StatementChangeEvent(newFormWid, newFormWid.getLoadCallback()));
			} else {
				widSource.setLineState(LINE_STATE.CREATION, new CreationTypeOracle(this.getPlaceLiteralLines(widSource.getParentWidget()), event.getValue()));
			}
	
			incrementNumberOfActions();
		}
	}

	/**
	 * Call Form.removeRelationLine and FormWidget.reload
	 */
	@Override
	public void onRemoveLine(RemoveLineEvent event) {
		AbstractFormLineWidget widSource = event.getSource();
		FormLine dataSource = widSource.getData();
		FormWidget widSourceParent = widSource.getParentWidget();
		Form dataSourceParent = widSourceParent.getData();

		dataSourceParent.removeRelationLine(dataSource);
		widSourceParent.reload();

		incrementNumberOfActions();
	}

	/**
	 * Call sewelisUriDescription
	 */
	@Override
	public void onDescribeUri(DescribeUriEvent event) {
//		URIWidget widSource = event.getSource();
//		URI dataSource = widSource.getData();
		sewelisUriDescription(event);
	}

	/**
	 * 
	 */
	@Override
	public void onMoreCompletions(MoreCompletionsEvent event) {
//		ControlUtils.debugMessage("onMoreCompletions");
		if(this.getPlace().hasMore()) {
			sewelisShowMost(event);
		} else {
			String queryString = lispqlStatementQuery(event.getSource().getParentWidget().getData(), true);
			sewelisGetPlaceStatement(queryString, event);		
		}

		incrementNumberOfActions();
	}

	@Override
	public void onLessCompletions(LessCompletionsEvent event) {
//		ControlUtils.debugMessage("onLessCompletions");
		if(this.getPlace().hasLess()) {
//			ControlUtils.debugMessage("onLessCompletions showLeast");
			sewelisShowLeast(event);
		} else {
			String queryString = lispqlStatementQuery(event.getSource().getParentWidget().getData());
//			ControlUtils.debugMessage("onLessCompletions " + queryString);
			sewelisGetPlaceStatement(queryString, event);	
		}
	}
//
//	@Override
//	public void onValueChange(ValueChangeEvent<Integer> event) {
////		if(event.getSource() == this.navBar.adminPanel.limitBox) {
////			CustomSuggestionWidget.setLimit(this.navBar.adminPanel.limitBox.getValue());
////		}
//	}

	/**
	 * Create a new line, add it to the data form source of event, call FormWidget.reload
	 */
	@Override
	public void onRelationCreation(RelationCreationEvent event) {
		if(event.getSource() instanceof RelationCreateWidget) {
			RelationCreateWidget widSource = (RelationCreateWidget) event.getSource();
			FormWidget parentWidSource = widSource.getParentWidget();
			
			String uri = newElementUri(widSource.getTextValue());
			String label = widSource.getTextValue();
			
			URI uriObj = new URI(uri, URI.KIND.PROPERTY, label);
			
			FormRelationLine newLine = new FormRelationLine(parentWidSource.getData(), uriObj);
			newLine.setAsNew(true);
			parentWidSource.getData().addLine(newLine);
			parentWidSource.reload();
 		}

		incrementNumberOfActions();
	}

	/**
	 * Create a new ClassLine, add it to the form source of event, call FormWidget.reload
	 */
	@Override
	public void onClassCreation(ClassCreationEvent event) {
		ControlUtils.debugMessage("Controller onClassCreation");
		if(event.getSource() instanceof ClassCreateWidget) {
			ClassCreateWidget widSource = (ClassCreateWidget) event.getSource();
			FormWidget parentWidSource = widSource.getParentWidget();
			
			String uri = newElementUri(widSource.getTextValue());
			String label = widSource.getTextValue();
			
			URI uriObj = new URI(uri, URI.KIND.CLASS, label);
			
			FormClassLine newLine = new FormClassLine(parentWidSource.getData(), uriObj);
			newLine.setAsNew(true);
			parentWidSource.getData().addTypeLine(newLine);
			parentWidSource.reload();
 		}

		incrementNumberOfActions();
	}

	/**
	 * Call the event callback and call Controller.finish
	 */
	@Override
	public void onFinishForm(FinishFormEvent event) {
//		ControlUtils.debugMessage("Controller onFinishForm");
		event.getCallback().call(this);
		finish( ( (FormWidget)event.getSource()));
//		ControlUtils.debugMessage("Controller onFinishForm END");
	}

	/**
	 * Not supposed to do anything in the controller
	 * @param event
	 */
	@Override
	public void onFinishableLine(FinishableLineEvent event) {
	}

	/**
	 * call sewelisShowMore
	 */
	@Override
	public void onMoreFormLines(MoreFormLinesEvent event) {
		ControlUtils.debugMessage("Controller onMoreFormLines " + event.getSource().toString());
		if(this.getPlace().hasMore()) {
			this.sewelisShowMore(event);
		}
	}

	/**
	 * Reload the event source content from the base 
	 */
	@Override
	public void onReload(ReloadEvent event) {
		String queryString = this.lispqlStatementQuery(((AbstractDataWidget) event.getSource()).getData());
		sewelisGetPlaceStatement(queryString, event);
	}

	/**
	 * 
	 */
	@Override
	public void onHistory(HistoryEvent event) {
		try {
			String currentProfile = XMLSerializer.profileToXml(this.formToProfile()).toString();

			if(event.getSource() instanceof FormWidget) {
				FormWidget widSource = (FormWidget)event.getSource();
				if(! widSource.getData().isEmpty()) {
					HistoryUtils.addHistoryToken(currentProfile);
					mainPage.getSettingsWidget().setStatePermalink(HistoryUtils.getPermalink(currentProfile));
				}
			} else if(event.getSource() instanceof FormRelationLineWidget) {
				HistoryUtils.addHistoryToken(currentProfile);
				mainPage.getSettingsWidget().setStatePermalink(HistoryUtils.getPermalink(currentProfile));
			}
		} catch (SerializingException | InvalidHistoryState e) {
			// TODO Auto-generated catch block
			ControlUtils.exceptionMessage(e);
//			e.printStackTrace();
		}
		
	}
	
	
	
	// GESTION FORMULAIRE

	/**
	 * Create an empty form and place the current Place to the statement equivalent to "get []"
	 */
	protected void toRootForm() {
		setCurrentForm( newForm());
		sewelisGetPlaceStatement("get [ ]", new StatementChangeEvent(mainPage.formWidget, mainPage.formWidget.getLoadCallback()));
	}
	
	/**
	 * Set the main form data and call FormWidget.reload
	 * @param f
	 */
	public void setCurrentForm(Form f) {
		ControlUtils.debugMessage("setCurrentForm( " + f + " )");
		this.form = f;
		mainPage.formWidget.setData(this.form);
		mainPage.formWidget.reload();
	}
	
	/**
	 * load the content of the main form from Place data
	 */
	public void loadFormContent() {
		loadFormContent(mainPage.formWidget);
	}

	/**
	 * Clear then load the form content from the current Place data.
	 * If there is no class line, it will load only relation line, otherwise it will load all class lines
	 * @param widSource
	 */
	public void loadFormContent(FormWidget widSource) {
//		ControlUtils.debugMessage("loadFormContent " + widSource + " : " + widSource.getData());
		if(this.currentStore != null) {
			widSource.setStoreIsSet(true);
		}

		if( widSource.getData() != null) {
			if(this.isFormContentLoadable(widSource)) {
	
				LinkedList<FormClassLine> classLines = getPlaceClassLines(widSource.getData());
				LinkedList<FormRelationLine> relationLines = getPlaceRelationLines(widSource.getData());
				Collections.sort(relationLines, new FormLineComparator());
				Collections.sort(classLines, new FormLineComparator());
	
				// Si il n'y a qu'un seul type proposé, alors il faut qu'il soit selectionné et placé dans le statement 
				// pour que les relations proposées soient les bonnes
	//			if(! widSource.getData().getTypeLines().equals(classLines)) { // Ne fonctionne pas, pas d'appel à equals
				if(! (widSource.getData().getTypeLines().size() == classLines.size() 
						&& widSource.getData().getTypeLines().containsAll(classLines) ) ) {
					
					
	//				ControlUtils.debugMessage("Controller loadFormContent current:" + widSource.getData().getTypeLines() + " new:"+ classLines);
					widSource.getData().clear();

					widSource.getData().setHasMore(this.place.hasMore());
					
					widSource.getData().addAllTypeLines(classLines);
					
					// Si il n'y a qu'un type proposé, on change le statement vers ce type
					if(widSource.getData().isTyped()) {
	//						ControlUtils.debugMessage("Controller loadFormContent typé" );
							String queryString = lispqlStatementQuery(widSource.getData());
							relationLines.clear();
							this.sewelisGetPlaceStatement(queryString, new StatementChangeEvent(widSource, widSource.getLoadCallback()));
					} else if (widSource.getData().isTypeList()){ // C'est une liste de type
						widSource.fireHistoryEvent();
					}
				} 
					
				if(widSource.getData().isTyped() || widSource.getData().isAnonymous()) {
	//				ControlUtils.debugMessage("Controller loadFormContent relations " + relationLines.size() + " relations" );
					int nbLines = relationLines.size();
					Iterator<FormRelationLine> itRelLines = relationLines.iterator();
					while(itRelLines.hasNext()) {
						FormRelationLine relLine = itRelLines.next();
						relLine.setWeight(nbLines);
						nbLines--;
					}	
					widSource.getData().addAllLines(relationLines);
					widSource.fireHistoryEvent();
				}
			}
			widSource.clear();
			widSource.getData().setFinished(false);
			widSource.reload();
		}

	}


	/**
	 * add form content from the current Place data.
	 * If there is no class line, it will load only relation line, otherwise it will load all class lines
	 * @param widSource
	 */
	public void appendFormContent(FormWidget widSource) {
//		ControlUtils.debugMessage("appendFormContent " + widSource);

		if(this.isFormContentLoadable(widSource)) {
			LinkedList<FormClassLine> classLines = getPlaceClassLines(widSource.getData());
			LinkedList<FormRelationLine> relationLines = getPlaceRelationLines(widSource.getData());
//			ControlUtils.debugMessage("appendFormContent content: " + classLines + relationLines);
			if(widSource.getData().getTypeLines().isEmpty()) {
				widSource.getData().appendAllLines(classLines);
			} else {
				widSource.getData().appendAllLines(relationLines);
			}

			widSource.getData().setHasMore(this.place.hasMore());
			widSource.reload();
			widSource.fireHistoryEvent();
		}
	}

	/**
	 * Check the current Place if there is a set of class lines, one class line or a set of relation line
	 * @param widSource whose corresponding lispql statement il to be checked
	 * @return true if the mentioned necessary component are present
	 */
	public boolean isFormContentLoadable(FormWidget widSource) {
//		ControlUtils.debugMessage("isFormContentLoadable " + widSource.getData());
		LinkedList<FormClassLine> classLines = getPlaceClassLines(widSource.getData());
		LinkedList<FormRelationLine> relationLines = getPlaceRelationLines(widSource.getData());

//		return (widSource.getData().isAnonymous()
//				&& ! classLines.isEmpty()
//				&& (classLines.size() == 1 
//				&& classLines.getFirst() instanceof FormClassLine)
//				|| (! classLines.isEmpty())) 
//				|| ! relationLines.isEmpty();
//		return (widSource.getData().isAnonymous()
//				|| widSource.getData().isTyped()
//				|| widSource.getData().isTypeList() )
//				&& ! widSource.getData().isEmpty();
		return ! classLines.isEmpty() || ! relationLines.isEmpty();
	}

	private void setProfile(Profile pro) {
		if(this.currentStore == null || this.currentStore.getName() != pro.getStoreName()) {
			this.setCurrentStore(pro.getStoreName());
		}
		try {
			Form fo = pro.toForm();
			this.setCurrentForm(fo);
		} catch (FormElementConversionException e) {
			ControlUtils.exceptionMessage(e);
		}
	}

	/**
	 * 
	 * @param source
	 * @return the liste of available class lines in the current Place suggestions (with the passed form as their parent)
	 */
	public LinkedList<FormClassLine> getPlaceClassLines(Form source) {
		LinkedList<FormClassLine> result = new LinkedList<FormClassLine>();

		Iterator<Increment> itIncre = place.getSuggestions().relationIterator();
		while(itIncre.hasNext()) {
			Increment incre = itIncre.next();
			if(incre.getKind() == KIND.CLASS) {
				FormLine newLine = DataUtils.formLineFromIncrement(incre, source);
				if(newLine instanceof FormClassLine && newLine.getFixedElement() instanceof URI) {
					URI uriClass = (URI)newLine.getFixedElement();
					if(! ControlUtils.FORBIDDEN_URIS.isForbidden(uriClass.getUri()) && ! ControlUtils.LITTERAL_URIS.isLitteralType(uriClass.getUri())) {
						result.add((FormClassLine) newLine);
					}
				}
			}
		}

		return result;
	}

	/**
	 * @param widSource
	 * @return the liste of available relation lines in the current Place suggestions (after filtering the RDFS/OWL ones and with the passed form as their parent)
	 */
	public LinkedList<FormRelationLine> getPlaceRelationLines(Form source) {
		LinkedList<FormRelationLine> result = new LinkedList<FormRelationLine>();

		Iterator<Increment> itIncre = place.getSuggestions().relationIterator();
		while(itIncre.hasNext()) {
			Increment incre = itIncre.next();
			if(incre.getKind() == KIND.PROPERTY) {
				FormLine newLine = DataUtils.formLineFromIncrement(incre, source);
				if (newLine instanceof FormRelationLine) {
					if(newLine.getFixedElement() instanceof URI) {
						URI uriProperty = (URI)newLine.getFixedElement();
						if(! ControlUtils.FORBIDDEN_URIS.isForbidden(uriProperty.getUri()) ) {
							result.add((FormRelationLine) newLine);		
						}
					}
				}
			}
		}

		return result;
	}

	/**
	 * 
	 * @param widSource
	 * @return List of literal suggestions in the current place
	 */
	public LinkedList<BasicLeafElement> getPlaceLiteralLines(FormWidget widSource) {
		LinkedList<BasicLeafElement> result = new LinkedList<BasicLeafElement>();

		Iterator<Increment> itIncre = place.getSuggestions().entityIterator();
		while(itIncre.hasNext()) {
			Increment incre = itIncre.next();
//			ControlUtils.debugMessage(incre.toString());
			if(incre.getKind() == KIND.SOMETHING) {
				LinkedList<BasicElement> newElemList = DataUtils.getFirstDisplayableElements(incre.getDisplayElement());
				if(newElemList.size() == 1) {
					if( newElemList.getFirst() instanceof Typed) {
						Typed newElem = (Typed) newElemList.getFirst();
						//					URI uriClass = ((Typed) newElem).getUri();
						if( ControlUtils.LITTERAL_URIS.isLitteralType(newElem.getUri())) {
							result.add(newElem);
						}
					} else if(newElemList.getFirst() instanceof Plain) {
						Plain newElem = (Plain)newElemList.getFirst();
						result.add(newElem);
					}
				}
			}
		}

		return result;
	}

	/**
	 * Utility function to create new URIs as per SEWELIS standards
	 * @param label end part of the uri (most of the type, the label of the entity)
	 * @return
	 */
	public static String newElementUri(String label) {
		String result = uriBaseAdress + instance().currentStore.getName() + "/#";
		String sanitizedLabel = label.replace(" ", "_").replace("<","_").replace(">","_").replace("#","_").replace("%","_").replace("\"","_").replace(",","_").replace(")","_").replace("{","_").replace("}","_").replace("|","_").replace("\\","_").replace("^","_").replace("'","_").replace(";","_").replace("/","_").replace("?","_").replace(":","_").replace("@","_").replace("&","_").replace("=","_").replace("+","_").replace("$","_").replace(",", "_");
		result += sanitizedLabel;
		return result;
	}

	/**
	 * 
	 * @param e
	 * @return the corresponding statement to interrogate the base from e
	 */
	public String lispqlStatementQuery(FormElement e) {
		return lispqlStatementQuery(e, false);
	}

	/**
	 * 
	 * @param eleme element à the start of the query
	 * @param root element  is the root of the form
	 * @return
	 */
	public String lispqlStatementQuery(FormElement eleme, boolean root) {
//		ControlUtils.debugMessage("lispqlStatementQuery( " + eleme + " )");
		String result = "";
		if(eleme instanceof FormLine) {
//			ControlUtils.debugMessage("lispqlStatementQuery FormLine");
			FormLine line = (FormLine) eleme;
			if(eleme instanceof FormRelationLine) {
//				ControlUtils.debugMessage("lispqlStatementQuery FormRelationLine");
				if(root) {
//					ControlUtils.debugMessage("lispqlStatementQuery FormRelationLine Root");
					FormRelationLine relLine = (FormRelationLine) eleme;
					result = "get [ " + relLine.toRootLispql() + " ]";
				} else {
//					ControlUtils.debugMessage("lispqlStatementQuery FormRelationLine not Root");
					try{
					result = "get [ " + line.toLispql(true, false) + " ]";
					}catch(Exception e) {
						ControlUtils.debugMessage("lispqlStatemetQuery EXCEPTION " + eleme );
						throw e;
					}
				}
			} else if(eleme instanceof FormClassLine) {
//				ControlUtils.debugMessage("lispqlStatementQuery FormClassLine");
				result = "get [ " + line.toLispql() + " ]";
			}
		} else {
//			ControlUtils.debugMessage("lispqlStatementQuery not FormLine");
			result = "get " + eleme.toLispql() + "";
		}
//		ControlUtils.debugMessage("lispqlStatementQuery( " + eleme + " ) result:" + result);
		return result;
	}
	
	/**
	 * EXPERIMENTAL
	 * @param user
	 * @param store
	 * @param uri
	 * @param start
	 * @param end
	 * @param nbActions
	 */
	public void sendExperimentLog(String user, String store, String uri, String start, String end, int nbActions) {
//		http://servolis.irisa.fr:3941/message?user=testUser2&store=testStore2&uri=testUri&start=11:50&end=11:52
		String logRequestString = logServerAdress + "/message?";
			logRequestString += "&user=" + user; 
			logRequestString += "&store=" + currentStore.getLabel(); 
			logRequestString += "&uri=" + URL.encodeQueryString(uri);
			logRequestString += "&start=" + start;
			logRequestString += "&end=" + end;
			logRequestString += "&nbActions=" + nbActions;
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, URL.encode(logRequestString));
		try {
			RequestCallback placeCallback = new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					if(response.getStatusCode() != 0) {
						ControlUtils.debugMessage("EXPERIMENT LOGGING FAILED" + response.getText());
					} else {
						
					}
				}
				@Override
				public void onError(Request request, Throwable exception) {
					ControlUtils.debugMessage("EXPERIMENT LOGGING FAILED ");
					ControlUtils.exceptionMessage(exception);
				}
			};
			builder.sendRequest(null, placeCallback );
		} catch (RequestException e) {
			ControlUtils.exceptionMessage(e);
		}
	}

}
