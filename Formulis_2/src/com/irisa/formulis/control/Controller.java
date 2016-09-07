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
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Random;
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
import com.irisa.formulis.view.MainNavigationBar;
import com.irisa.formulis.view.MainPage;
import com.irisa.formulis.view.ViewUtils;
import com.irisa.formulis.view.LoginWidget.LOGIN_STATE;
import com.irisa.formulis.view.create.CreationTypeOracle;
import com.irisa.formulis.view.create.fixed.RelationCreateWidget;
import com.irisa.formulis.view.event.*;
import com.irisa.formulis.view.event.interfaces.*;
import com.irisa.formulis.view.form.*;
import com.irisa.formulis.view.form.FormLineWidget.LINE_STATE;
import com.irisa.formulis.view.form.FormWidget.FORM_CALLBACK_MODE;
import com.irisa.formulis.view.form.suggest.CustomSuggestionWidget;
import com.irisa.formulis.view.form.suggest.CustomSuggestionWidget.SuggestionCallback;

/**
 * controller of all client-server interactions
 * @author pmaillot
 *
 */

public final class Controller implements EntryPoint, ClickHandler, FormEventChainHandler, StatementFocusChangeHandler/*, TypeLineSetHandler*/, ValueChangeHandler<Integer> {

	private HashMap<String, Store> storeMapByName = new HashMap<String, Store>();
	private HashMap<String, Store> storeMapByLabel = new HashMap<String, Store>();
	private MainPage mainPage;
	private MainNavigationBar navBar = new MainNavigationBar();

	private static String serverAdress = "http://127.0.0.1:9999/";
	//	private String serverAdress = "http://lisfs2008.irisa.fr:9999/"; // TODO Rendre adresse serveur configurable
//	private static String serverAdress = "http://servolis.irisa.fr:3939/"; // TODO Rendre adresse serveur configurable
	private static String logServerAdress = "http://servolis.irisa.fr:3941";
	private String userLogin = "anonymous";
	private String userKey = "0";
	private Store currentStore = null;
	private Place place = null;
	private Form form = null;
	private HashMap<String, Place> historyPlaceStack = new HashMap<String, Place>();

	private String cookiesProfilesIndex = "FormulisProfile";
	private String cookiesUserLogin = "FormulisUserLogin";
	private String cookiesUserkey = "FormulisUserKey";
	@SuppressWarnings("deprecation")
	private Date cookiesProfilesExpireDate = new Date(2500, 1, 1);
	private HashSet<Profile> profiles = new HashSet<Profile>();
	private HashMap<String, LinkedList<Profile>> storeProfileMap = new HashMap<String, LinkedList<Profile>>();
	
	private int numberOfActions = 0;
	private Date startEditDate = new Date();

	public Controller instance() {
		return this;
	}

	public void setServer(String adress) {
		serverAdress = adress;
	}

	public Place getPlace() {
		return this.place;
	}

	private void setCurrentStore(String selectValue) {
		currentStore = storeMapByName.get(selectValue);
	}
	
	private void incrementNumberOfActions() {
		numberOfActions++;
		ControlUtils.debugMessage("numberOfActions: " + getNumberOfActions());
	}
	
	private int getNumberOfActions() {
		return numberOfActions;
	}

	// Profiles

	public void addProfile(Profile pro) {
		ControlUtils.debugMessage("addProfile " + pro);
		profiles.add(pro);
		if(! storeProfileMap.containsKey(pro.getStoreName())) {
			storeProfileMap.put(pro.getStoreName(), new LinkedList<Profile>());
		}
		storeProfileMap.get(pro.getStoreName()).add(pro);
		addProfileToCookies(pro);
	}

	public void addProfileToCookies(Profile pro) {
		try {
			LinkedList<Profile> cookiesProfiles = Parser.parseProfiles(XMLParser.parse(Cookies.getCookie(this.cookiesProfilesIndex)).getDocumentElement());
			cookiesProfiles.add(pro);
			Cookies.setCookie(cookiesProfilesIndex, XMLSerializer.profilesToXml(cookiesProfiles).toString(), cookiesProfilesExpireDate);
		} catch (XMLParsingException | SerializingException e) {
			ControlUtils.exceptionMessage(e);
		}
		reloadNavbarProfileList();
	}

	public void addProfiles(Collection<Profile> l) {
		Iterator<Profile> itPro = l.iterator();
		while(itPro.hasNext()) {
			Profile pro = itPro.next();
			addProfile(pro);
		}
	}
	
	public void removeProfile(Profile pro) {
		profiles.remove(pro);
		storeProfileMap.get(pro.getStoreName()).remove(pro);
	}

	private Profile findProfile(Collection<Profile> list, String name) {
		Iterator<Profile> itPro = list.iterator();
		while(itPro.hasNext()) {
			Profile pro = itPro.next();
			if(pro.getName() == name) {
				return pro;
			}
		}
		return null;
	}

	public Profile getProfile(String name) {
		return findProfile(this.profiles, name);
	}

	public LinkedList<Profile> getProfilesByStore(String store) {
		return this.storeProfileMap.get(store);
	}

	public Profile getProfileForAStore(String name, String store) {
		return findProfile(getProfilesByStore(store), name);
	}

	public void initializeProfilesFromCookie() {
		if(Cookies.getCookie(cookiesProfilesIndex) == null) {
			try {
				Cookies.setCookie(cookiesProfilesIndex, XMLSerializer.profilesToXml(new LinkedList<Profile>()).toString(), cookiesProfilesExpireDate);
			} catch (SerializingException e) {
				ControlUtils.exceptionMessage(e);
			}
		} 
		try {
			this.profiles.addAll(Parser.parseProfiles(XMLParser.parse(Cookies.getCookie(cookiesProfilesIndex)).getDocumentElement()));
		} catch (XMLParsingException e) {
			ControlUtils.exceptionMessage(e);
		}
		this.reloadNavbarProfileList();
	}
	
	/**
	 * @return Le contenu parsé du cookie de profile
	 */
	public Document getProfileDocument() {
		return XMLParser.parse(Cookies.getCookie(cookiesProfilesIndex));
	}
	
	public void setProfileDocument(String doc) {
		Cookies.setCookie(cookiesProfilesIndex, doc, cookiesProfilesExpireDate);
	}

	public LinkedList<Profile> getCookiesProfileList() {
		try {
//			Utils.debugMessage("getCookiesProfileList cookie: " + Cookies.getCookie(cookiesProfilesIndex));
			Document profilesXMLDoc = XMLParser.parse(Cookies.getCookie(cookiesProfilesIndex)); 
			Node profilesXMLNode = profilesXMLDoc.getDocumentElement();
			return Parser.parseProfiles(profilesXMLNode);
		} catch (XMLParsingException | DOMParseException e) {
			ControlUtils.exceptionMessage(e);
			return null;
		}
	}

	public void clearProfiles() {
		this.profiles.clear();
		this.storeProfileMap.clear();
		try {
			Cookies.setCookie(cookiesProfilesIndex, XMLSerializer.profilesToXml(new LinkedList<Profile>()).toString(), cookiesProfilesExpireDate);
		} catch (SerializingException e) {
			ControlUtils.exceptionMessage(e);
		}
	}

	public void reloadNavbarProfileList() {
		navBar.adminPanel.profileList.clear();
		Iterator<Profile> itPro = this.profiles.iterator();
		while(itPro.hasNext()) {
			Profile pro = itPro.next();
			navBar.adminPanel.profileList.addItem(pro.getName() + " : " + pro.getStoreName(), pro.getName());
		}
		
		navBar.adminPanel.profileEditReload.click(); // SALE
	}






	public void backHistory() {
		ControlUtils.debugMessage("backHistory");
		History.back();
		String backId = History.getToken();
		if(this.historyPlaceStack.containsKey(backId)) {
			if(this.historyPlaceStack.get(backId) != null) {
				place = this.historyPlaceStack.get(backId);
				ControlUtils.debugMessage("place = " + backId);
				//				refreshPlace();
			} else {
				ControlUtils.debugMessage("newPlace: " + this.historyPlaceStack.get(backId));
			}
		} else {
			ControlUtils.debugMessage("backId: " + backId);
		}
	}

	public void forwardHistory() {
		ControlUtils.debugMessage("forwardHistory");
		History.forward();
		String forwardId = History.getToken();
		if(this.historyPlaceStack.containsKey(forwardId)) {
			if(this.historyPlaceStack.get(forwardId) != null) {
				place = this.historyPlaceStack.get(forwardId);
				ControlUtils.debugMessage("place = " + forwardId);
				//				refreshPlace();
			} else {
				ControlUtils.debugMessage("newPlace: " + this.historyPlaceStack.get(forwardId));
			}
		} else {
			ControlUtils.debugMessage("forwardId: " + forwardId);
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

		navBar.setServerStatusMessage("attente...");
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

	public void sewelisRegister(final NewUserToken nu) {
		String registerRequestString = serverAdress + "/register";
		registerRequestString += "?userKey=" + userKey;
		registerRequestString += "&userLogin=" + nu.getUserName();
		registerRequestString += "&passwd=" + Crypto.getCryptedString(nu.getPassword(), Controller.serverAdress);
		registerRequestString += "&email=" + nu.getEmail();

		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, URL.encode(registerRequestString));

		try {
			builder.sendRequest(null, new RequestCallback() {

				@Override
				public void onResponseReceived(Request request, Response response) {
					if (200 == response.getStatusCode()) {
						Document statusDoc = XMLParser.parse(response.getText());
						Element docElement = statusDoc.getDocumentElement();
						String status = docElement.getAttribute("status");
						if(status.equals("ok")) {
							sewelisLogin(new LoginToken(nu.getUserName(), nu.getPassword()));
						} else {
							ControlUtils.debugMessage(docElement.getNodeValue());
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

	public void sewelisLogin(final LoginToken t) {
		String loginRequestString = serverAdress + "/login";
		loginRequestString += "?userKey=" + userKey;
		loginRequestString += "&userLogin=" + t.getLogin();
		loginRequestString += "&passwd=" + Crypto.getCryptedString(t.getPassword(), Controller.serverAdress);

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

	public void sewelisLogout() {
		String logoutRequestString = serverAdress + "/logout";
		logoutRequestString += "?userKey=" + userKey;

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


	// STORES LISTS
	/**
	 * Rafraichit la liste des stores accessibles
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
						for(int i = 0; i< storeListNode.getLength(); i++) {
							Node storeNode = storeListNode.item(i);
							String storeName = storeNode.getAttributes().getNamedItem("storeName").getNodeValue();
							String storeRole = storeNode.getAttributes().getNamedItem("role").getNodeValue();
							String storeLabel = storeNode.getFirstChild().getNodeValue();
							Store store = new Store(storeName, storeLabel, storeRole);
							if(storeRole.equals("publisher") || storeRole.equals("collaborator") || storeRole.equals("admin")) {
								storeMapByName.put(storeName, store) ;
								storeMapByLabel.put(storeLabel, store) ;
								storeList.add(store);
							}
						}
						navBar.setStoreList(storeList);
						navBar.setServerStatusMessage("ok");

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

	/**
	 * Aucune utilité apparente ! (CTRL-C CTRL-V de ping() )
	 */
	public void sewelisRemovedStores() {
		String removedStoresRequestString = serverAdress + "/removedStores";
		removedStoresRequestString += "?userKey=" + userKey;
		removedStoresRequestString += "&storeId=" + currentStore.getName();
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, URL.encode(removedStoresRequestString));

		try {
			builder.sendRequest(null, new RequestCallback() {	
				@Override		
				public void onError(Request request, Throwable exception) {
				}

				@Override
				public void onResponseReceived(Request request, Response response) {
					if (200 == response.getStatusCode()) {
						Document statusDoc = XMLParser.parse(response.getText());
						Element docElement = statusDoc.getDocumentElement();
						String status = docElement.getAttribute("status");
						navBar.setServerStatusMessage(status);
					} else {
						// FIXME GESTION DES MESSAGE D'ERREUR
						ControlUtils.debugMessage(request.toString() + " " + response.getStatusCode() + " " + response.getStatusText());
					}
				}
			});
		} catch (RequestException e) {
			e.printStackTrace();
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

	//	/**
	//	 * Utilité inconnue
	//	 */
	//	public void storeBase() {
	//
	//	}
	//
	//	/**
	//	 * Utilité inconnue
	//	 */
	//	public void storeXmlns() {
	//
	//	}
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
	public void sewelisGetPlaceRoot() {
		ControlUtils.debugMessage("getPlaceRoot");
		if(currentStore != null) {
			String placeHomeRequestString = serverAdress + "/getPlaceRoot?";
			placeHomeRequestString += "userKey=" + userKey;
			placeHomeRequestString += "&storeName=" + currentStore.getName();
			navBar.setServerStatusMessage("Loading...");
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
							if(homePlaceElem.getNodeName() == "getPlaceRootResponse" && homePlaceElem.getAttribute("status") == "ok") {
								Node placeNode = homePlaceElem.getFirstChild();
								if(placeNode.getNodeName() == "place") {
									loadPlace(placeNode);
								} else {
									// FIXME GESTION DES MESSAGES D'ERREUR
									ControlUtils.debugMessage("EXPECTED <place> node = " + placeNode);
								}
							} else {
								navBar.setServerStatusMessage(homePlaceElem.getAttribute("status"));
								if(homePlaceElem.getFirstChild().getNodeName() == "message") {
									ControlUtils.debugMessage( homePlaceElem.getFirstChild().getFirstChild().getNodeValue());
								}
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
		ControlUtils.debugMessage("FIN getPlaceRoot");
	}

	public void sewelisGetPlaceHome() {
		ControlUtils.debugMessage("getPlaceHome");
		if(currentStore != null) {
			String placeHomeRequestString = serverAdress + "/getPlaceHome?";
			placeHomeRequestString += "userKey=" + userKey;
			placeHomeRequestString += "&storeName=" + currentStore.getName();
			navBar.setServerStatusMessage("Loading...");
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
								if(homePlaceElem.getNodeName() == "getPlaceHomeResponse" && homePlaceElem.getAttribute("status") == "ok") {
									Node placeNode = homePlaceElem.getFirstChild();
									if(placeNode.getNodeName() == "place") {
										loadPlace(placeNode);
//										getRootForm();
										setCurrentForm( newForm());
										loadFormContent(mainPage.formWidget);
									} else {
										if(placeNode.getNodeName() == "message") {
											ControlUtils.debugMessage( placeNode.getFirstChild().getNodeValue());
										}
									}
								} else {
									navBar.setServerStatusMessage(homePlaceElem.getAttribute("status"));
									if(homePlaceElem.getFirstChild().getNodeName() == "message") {
										ControlUtils.debugMessage( homePlaceElem.getFirstChild().getFirstChild().getNodeValue());
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
	 * 
	 * @param statString statement LispQL
	 */
	public Place sewelisGetPlaceStatementAlone(String statString) {
		ControlUtils.debugMessage("getPlaceStatementAlone( " + statString + " ) " );
		Place result = null;
		if(currentStore != null) {
			String placeStatementRequestString = serverAdress + "/getPlaceStatement?";
			placeStatementRequestString += "userKey=" + userKey;
			placeStatementRequestString += "&storeName=" + currentStore.getName();
			placeStatementRequestString += "&statement=" + URL.encodeQueryString(statString);
			navBar.setServerStatusMessage("Loading...");
			PlaceRequestCallback placeCallback = new PlaceRequestCallback() {
				
				private Place resultPlace = null;
				
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
								try {
									Place here = Parser.parsePlace(placeNode);

									// TODO Rustine pour gérer le focus renvoyé par getPlaceStatement = a étudier
									// Le focused est placé à la racine du statement, ce qui ne permet pas d'avoir de suggestions pour l'objet qui nous interesse
									// La rustine déplace le focused au premier focus de la formule (2 numéro après, premier élément da la première Pair)
									// SALE
									try {
										String focusedId = place.getStatement().getFocusedDisplay();
										String targetFocusId = String.valueOf(Integer.valueOf(focusedId) - 1);
										resultPlace = sewelisChangeFocusAlone(here, targetFocusId);
									} catch(Exception e) {
										ControlUtils.exceptionMessage( e);
									}
								} catch (XMLParsingException e1) {
									ControlUtils.exceptionMessage(e1);
								}
							} else {
								// FIXME GESTION DES MESSAGES D'ERREUR
								ControlUtils.debugMessage("EXPECTED <place> node = " + placeNode);
							}
						} else {
							navBar.setServerStatusMessage(homePlaceElem.getAttribute("status"));
							if(homePlaceElem.getFirstChild().getNodeName() == "message") {
								ControlUtils.debugMessage( homePlaceElem.getFirstChild().getFirstChild().getNodeValue());
							}
						}
					} else {
						// FIXME GESTION DES MESSAGES D'ERREUR
						ControlUtils.debugMessage(request.toString() + " " + response.getStatusCode() + " " + response.getStatusText());
					}
				}

				@Override
				public Place getPlace() {
					return resultPlace;
				}
			};
			RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, placeStatementRequestString);
			try {
				builder.sendRequest(null, placeCallback);
				result = placeCallback.getPlace();
			} catch (RequestException e) {
				ControlUtils.exceptionMessage(e);
			}
		}
		ControlUtils.debugMessage("FIN getPlaceStatement " );
		return result;
	}

	/**
	 * 
	 * @param statString statement LispQL
	 * @param followUp Event a emettre en cas de succès
	 */
	public void sewelisGetPlaceStatement(String statString, final FormEvent followUp) {
		ControlUtils.debugMessage("getPlaceStatement( " + statString + " ) " );
		if(currentStore != null) {
			String placeStatementRequestString = serverAdress + "/getPlaceStatement?";
			placeStatementRequestString += "userKey=" + userKey;
			placeStatementRequestString += "&storeName=" + currentStore.getName();
			placeStatementRequestString += "&statement=" + URL.encodeQueryString(statString);
			navBar.setServerStatusMessage("Loading...");
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

									// TODO Rustine pour gérer le focus renvoyé par getPlaceStatement = a étudier
									// Le focused est placé à la racine du statement, ce qui ne permet pas d'avoir de suggestions pour l'objet qui nous interesse
									// La rustine déplace le focused au premier focus de la formule (2 numéro après, premier élément da la première Pair)
									// SALE
									try {
										String focusedId = place.getStatement().getFocusedDisplay();
										String targetFocusId = String.valueOf(Integer.valueOf(focusedId) - 1);
										sewelisChangeFocus(targetFocusId, followUp);
									} catch(Exception e) {
										ControlUtils.exceptionMessage( e);
									}
								} else {
									// FIXME GESTION DES MESSAGES D'ERREUR
									ControlUtils.debugMessage("EXPECTED <place> node = " + placeNode);
								}
							} else {
								ControlUtils.debugMessage(homePlaceElem.getAttribute("status") + ": " + homePlaceElem.getFirstChild().getFirstChild().getNodeValue());
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
		ControlUtils.debugMessage("FIN getPlaceStatement " );
	}


	/**
	 * @param statString statement LispQL
	 */
	private void sewelisRunStatement(String statString) {
		ControlUtils.debugMessage("runStatement (" + statString + ") ");
		String insertIncrementRequestString = serverAdress + "/runStatement?userKey=" + userKey ;
		insertIncrementRequestString += "&storeName=" + currentStore.getName(); 
		insertIncrementRequestString += "&statement=" + URL.encodeQueryString(statString);
		navBar.setServerStatusMessage("Creation...");
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, insertIncrementRequestString);

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
							sewelisGetPlaceHome();
						} else {
							navBar.setServerStatusMessage(docElement.getAttribute("status"));
							if(docElement.getFirstChild().getNodeName() == "message") {
								ControlUtils.debugMessage( docElement.getFirstChild().toString());
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
	 * 
	 * @param match Chaine partielle demandée par la source
	 * @param widSource source à qui renvoyer les completions
	 */
	public void sewelisGetCompletions(final String match, final CustomSuggestionWidget widSource) {
		//		displayDebugMessage("getCompletions " + match + " " + event);
		String insertIncrementRequestString = serverAdress + "/getCompletions?userKey=" + userKey ;
		insertIncrementRequestString += "&storeName=" + currentStore.getName(); 
		insertIncrementRequestString += "&placeId=" + place.getId(); 
		insertIncrementRequestString += "&matchingKey=" + match;
		navBar.setServerStatusMessage("Loading...");
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, URL.encode(insertIncrementRequestString));

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
									while(currNode.getNextSibling() != null) {
										try {
											result.add(Parser.parseIncrement(currNode));
											currNode = currNode.getNextSibling();
										} catch (XMLParsingException e) {
											ControlUtils.exceptionMessage(e);
										}
									}
									if(!result.isEmpty()) {
										place.setCurrentCompletions(result);
										onCompletionReady(widSource);
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

//	public void insertElement(BasicElement elem, FormEvent callback) {
//		try {
//			if(elem instanceof Plain) {
//				insertPlain((Plain) elem, callback);
//			} else if(elem instanceof Typed) {
//				insertTyped((Typed) elem, callback);
//			} else if(elem instanceof URI) {
//				insertUri((URI) elem, callback);
//			}
//		} catch (RequestException e) {
//			Utils.exceptionMessage(e);
//		}
//	}
//
//	public void insertPlain(Plain plain,final  FormEvent callback) throws RequestException {
//		String insertIncrementRequestString = serverAdress + "/insertPlainLiteral?userKey=" + userKey ;
//		insertIncrementRequestString += "&storeName=" + currentStore.getName(); 
//		insertIncrementRequestString += "&placeId=" + place.getId(); 
//		insertIncrementRequestString += "&text=" + URL.encodeQueryString(plain.getPlain());
//		insertIncrementRequestString += "&lang=" + URL.encodeQueryString(plain.getLang());
//		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, URL.encode(insertIncrementRequestString));
//
//		builder.sendRequest(null, new RequestCallback() {			
//			@Override
//			public void onError(Request request, Throwable exception) {
//				Utils.exceptionMessage(exception);
//			}
//
//			@Override
//			public void onResponseReceived(Request request, Response response) {
//				if (200 == response.getStatusCode()) {
//					Document statusDoc = XMLParser.parse(response.getText());
//					Element docElement = statusDoc.getDocumentElement();
//					String status = docElement.getAttribute("status");
//					navBar.setServerStatusMessage(status);
//					if(status == "ok") {
//						Node placeNode = docElement.getFirstChild();
//						if(placeNode.getNodeName().equals("place")) {
//							loadPlace(placeNode);
//							//							onFormEvent(callback);
//						}
//					}
//				} else {
//					// TODO GESTION DES MESSAGE D'ERREUR
//					Utils.debugMessage(request.toString() + " " + response.getStatusCode() + " " + response.getStatusText());
//				}
//			}
//		});
//	}
//
//	public void insertTyped(Typed typed,final  FormEvent callback) throws RequestException {
//		String insertIncrementRequestString = serverAdress + "/insertTypedLiteral?userKey=" + userKey ;
//		insertIncrementRequestString += "&storeName=" + currentStore.getName(); 
//		insertIncrementRequestString += "&placeId=" + place.getId(); 
//		insertIncrementRequestString += "&text=" + URL.encodeQueryString(typed.getValue());
//		insertIncrementRequestString += "&datatype=" + URL.encodeQueryString(typed.getUri());
//		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, URL.encode(insertIncrementRequestString));
//
//		builder.sendRequest(null, new RequestCallback() {			
//			@Override
//			public void onError(Request request, Throwable exception) {
//				Utils.exceptionMessage(exception);
//			}
//
//			@Override
//			public void onResponseReceived(Request request, Response response) {
//				if (200 == response.getStatusCode()) {
//					Document statusDoc = XMLParser.parse(response.getText());
//					Element docElement = statusDoc.getDocumentElement();
//					String status = docElement.getAttribute("status");
//					navBar.setServerStatusMessage(status);
//					if(status == "ok") {
//						Node placeNode = docElement.getFirstChild();
//						if(placeNode.getNodeName().equals("place")) {
//							loadPlace(placeNode);
//							//							onFormEvent(callback);
//						}
//					}
//				} else {
//					// TODO GESTION DES MESSAGE D'ERREUR
//					Utils.debugMessage(request.toString() + " " + response.getStatusCode() + " " + response.getStatusText());
//				}
//			}
//		});
//	}
//
//	public void insertUri(URI uri,final FormEvent callback) throws RequestException {
//		String insertIncrementRequestString = serverAdress + "/insertUri?userKey=" + userKey ;
//		insertIncrementRequestString += "&storeName=" + currentStore.getName(); 
//		insertIncrementRequestString += "&placeId=" + place.getId(); 
//		insertIncrementRequestString += "&uri=" + URL.encodeQueryString(uri.getUri());
//		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, URL.encode(insertIncrementRequestString));
//
//		builder.sendRequest(null, new RequestCallback() {			
//			@Override
//			public void onError(Request request, Throwable exception) {
//				Utils.exceptionMessage(exception);
//			}
//
//			@Override
//			public void onResponseReceived(Request request, Response response) {
//				if (200 == response.getStatusCode()) {
//					Document statusDoc = XMLParser.parse(response.getText());
//					Element docElement = statusDoc.getDocumentElement();
//					String status = docElement.getAttribute("status");
//					navBar.setServerStatusMessage(status);
//					if(status == "ok") {
//						Node placeNode = docElement.getFirstChild();
//						if(placeNode.getNodeName().equals("place")) {
//							loadPlace(placeNode);
//							//							onFormEvent(callback);
//						}
//					}
//				} else {
//					// TODO GESTION DES MESSAGE D'ERREUR
//					Utils.debugMessage(request.toString() + " " + response.getStatusCode() + " " + response.getStatusText());
//				}
//			}
//		});
//	}
//
//	public void insertIncrement(Increment select) throws RequestException {
//		String insertIncrementRequestString = serverAdress + "/insertIncrement?userKey=" + userKey ;
//		insertIncrementRequestString += "&storeName=" + currentStore.getName(); 
//		insertIncrementRequestString += "&placeId=" + place.getId(); 
//		insertIncrementRequestString += "&incrementId=" + select.getId();
//		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, URL.encode(insertIncrementRequestString));
//
//		builder.sendRequest(null, new RequestCallback() {			
//			@Override
//			public void onError(Request request, Throwable exception) {
//				Utils.exceptionMessage(exception);
//			}
//
//			@Override
//			public void onResponseReceived(Request request, Response response) {
//				if (200 == response.getStatusCode()) {
//					Document statusDoc = XMLParser.parse(response.getText());
//					Element docElement = statusDoc.getDocumentElement();
//					String status = docElement.getAttribute("status");
//					navBar.setServerStatusMessage(status);
//					if(status == "ok") {
//						Node placeNode = docElement.getFirstChild();
//						if(placeNode.getNodeName().equals("place")) {
//							loadPlace(placeNode);
//						}
//					}
//				} else {
//					// TODO GESTION DES MESSAGE D'ERREUR
//					Utils.debugMessage(request.toString() + " " + response.getStatusCode() + " " + response.getStatusText());
//				}
//			}
//		});
//	}
//
//	public void applyTransformation(Transformation t) throws RequestException {
//		String applyTransformationRequestString = serverAdress + "/applyTransformation?userKey=" + userKey ;
//		applyTransformationRequestString += "&storeName=" + currentStore.getName(); 
//		applyTransformationRequestString += "&placeId=" + place.getId(); 
//		applyTransformationRequestString += "&transformation=" + t.getName();
//
//		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, URL.encode(applyTransformationRequestString));
//		builder.sendRequest(null, new RequestCallback() {
//			@Override
//			public void onError(Request request, Throwable exception) {
//			}
//
//			@Override
//			public void onResponseReceived(Request request, Response response) {
//				if (200 == response.getStatusCode()) {
//					Document statusDoc = XMLParser.parse(response.getText());
//					Element docElement = statusDoc.getDocumentElement();
//					String status = docElement.getAttribute("status");
//					navBar.setServerStatusMessage(status);
//					if(status == "ok") {
//						Node placeNode = docElement.getFirstChild();
//						if(placeNode.getNodeName().equals("place")) {
//							loadPlace(placeNode);
//						}
//					}
//				} else {
//					// TODO GESTION DES MESSAGE D'ERREUR
//					Utils.debugMessage(request.toString() + " " + response.getStatusCode() + " " + response.getStatusText());
//				}
//			}
//		});
//	}

	public void sewelisShowMost() {
		String showMostRequestString = serverAdress + "/showMost?userKey=" + userKey ;
		showMostRequestString += "&storeName=" + currentStore.getName(); 
		showMostRequestString += "&placeId=" + place.getId(); 
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
		String showMoreRequestString = serverAdress + "/showMore?userKey=" + userKey ;
		showMoreRequestString += "&storeName=" + currentStore.getName(); 
		showMoreRequestString += "&placeId=" + place.getId(); 
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
		String showLessRequestString = serverAdress + "/showLess?userKey=" + userKey ;
		showLessRequestString += "&storeName=" + currentStore.getName(); 
		showLessRequestString += "&placeId=" + place.getId(); 
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

	public void sewelisShowLeast() {
		String showLeastRequestString = serverAdress + "/showLeast?userKey=" + userKey ;
		showLeastRequestString += "&storeName=" + currentStore.getName(); 
		showLeastRequestString += "&placeId=" + place.getId(); 
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

	public void sewelisDefineNamespace(String prefix, String adress) {
		String showMoreRequestString = serverAdress + "/defineNamespace?userKey=" + userKey ;
		showMoreRequestString += "&storeName=" + currentStore.getName(); 
		showMoreRequestString += "&userkey=" + this.userKey;
		showMoreRequestString += "&prefix=" + prefix;
		showMoreRequestString += "&uri=" + adress;
		
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
								ControlUtils.debugMessage( docElement.getFirstChild().getFirstChild().getNodeValue());
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
	private void loadPlace(Node placeNode) {
		//		displayDebugMessage("loadPlace " + placeNode.toString());
		try {
			place = Parser.parsePlace(placeNode);
			refreshAnswers();
			ControlUtils.debugMessage("Place " + place.getSuggestions().getEntitySuggestions().size() + " answers " + place.getAnswers().getContentRows().size() + " entity suggestions " + place.getSuggestions().getRelationSuggestions().size() + " relations suggestions");
			//this.historyPlaceStack.put(place.getId(), place);
		} catch (XMLParsingException e) {
			ControlUtils.exceptionMessage(e);
		}
		//		displayDebugMessage("FIN loadPlace");
	}
	
	@SuppressWarnings("deprecation")
	private void finish() {
//		ControlUtils.debugMessage("Nombre d'actions: " + getNumberOfActions());
//		Date nowDate = new Date();
//		ControlUtils.debugMessage(userLogin + " " + currentStore.getName() + " " + this.form.getTypeLines().getFirst().getElementUri() + " " + this.startEditDate.getHours()+":"+this.startEditDate.getMinutes()+":"+this.startEditDate.getSeconds() + " " + nowDate.getHours()+":"+nowDate.getMinutes()+":"+nowDate.getSeconds() + " " + this.getNumberOfActions());
//		this.sendExperimentLog(userLogin, currentStore.getName(), this.form.getTypeLines().getFirst().getElementUri(), this.startEditDate.getHours()+":"+this.startEditDate.getMinutes()+":"+this.startEditDate.getSeconds(), nowDate.getHours()+":"+nowDate.getMinutes()+":"+nowDate.getSeconds(), this.getNumberOfActions());
//		numberOfActions = 0;
//		
//		sewelisRunStatement("get " + this.form.toLispql(true) + "");
		ControlUtils.debugMessage("Nombre d'actions: " + getNumberOfActions());
		Date nowDate = new Date();
		ControlUtils.debugMessage(userLogin + " " + currentStore.getName() + " " + this.form.getType().getElementUri() + " " + this.startEditDate.getHours()+":"+this.startEditDate.getMinutes()+":"+this.startEditDate.getSeconds() + " " + nowDate.getHours()+":"+nowDate.getMinutes()+":"+nowDate.getSeconds() + " " + this.getNumberOfActions());
		this.sendExperimentLog(userLogin, currentStore.getName(), this.form.getType().getElementUri(), this.startEditDate.getHours()+":"+this.startEditDate.getMinutes()+":"+this.startEditDate.getSeconds(), nowDate.getHours()+":"+nowDate.getMinutes()+":"+nowDate.getSeconds(), this.getNumberOfActions());
		numberOfActions = 0;
		finish(this.form);
	}
	
	private void finish(Form f) {
		sewelisRunStatement("get " + f.toLispql(true) + "");
	}


	// NAVIGATION 
	public void sewelisDoRun() {
		String insertIncrementRequestString = serverAdress + "/doRun?userKey=" + userKey ;
		insertIncrementRequestString += "&storeName=" + currentStore.getName(); 
		insertIncrementRequestString += "&placeId=" + place.getId();
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, URL.encode(insertIncrementRequestString));
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
							loadPlace(docElement.getFirstChild());
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


	// TRANSFORMATION
	public void sewelisChangeFocus(String focusId) {
		sewelisChangeFocus(focusId, null);
	}

	public void sewelisChangeFocus(String focusId, final FormEvent followUp) {
		ControlUtils.debugMessage("changeFocus " + focusId + " " + followUp.getClass());
		String insertIncrementRequestString = serverAdress + "/changeFocus?userKey=" + userKey ;
		insertIncrementRequestString += "&storeName=" + currentStore.getName(); 
		insertIncrementRequestString += "&placeId=" + place.getId(); 
		insertIncrementRequestString += "&focusId=" + focusId;
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, URL.encode(insertIncrementRequestString));
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
								if(followUp != null && followUp instanceof StatementChangeEvent) {
									onStatementChange((StatementChangeEvent) followUp);
								} else if(followUp != null && followUp instanceof MoreCompletionsEvent) {
									followUp.getCallback().call(instance());
								}
							}
						} else {
							navBar.setServerStatusMessage(docElement.getAttribute("status"));
							if(docElement.getFirstChild().getNodeName() == "message") {
								ControlUtils.debugMessage( docElement.getFirstChild().getFirstChild().getNodeValue());
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

	public Place sewelisChangeFocusAlone(Place here, String focusId) {
		//		mainPage.addDebugMessage("changeFocus " + f.toString());
		Place result = null;
		String insertIncrementRequestString = serverAdress + "/changeFocus?userKey=" + userKey ;
		insertIncrementRequestString += "&storeName=" + currentStore.getName(); 
		insertIncrementRequestString += "&placeId=" + here.getId(); 
		insertIncrementRequestString += "&focusId=" + focusId;
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, URL.encode(insertIncrementRequestString));
		try {
			PlaceRequestCallback placeCallback = new PlaceRequestCallback() {
				
				private Place resultPlace = null;
				
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
								try {
									resultPlace = Parser.parsePlace(placeNode);
								} catch (XMLParsingException e) {
									ControlUtils.exceptionMessage(e);
								}
							}
						}
					} else {
						// TODO GESTION DES MESSAGE D'ERREUR
						ControlUtils.debugMessage(request.toString() + " " + response.getStatusCode() + " " + response.getStatusText());
					}
				}

				@Override
				public Place getPlace() {
					return resultPlace;
				}
			};
			builder.sendRequest(null, placeCallback );
			result = placeCallback.getPlace();
		} catch (RequestException e) {
			ControlUtils.exceptionMessage(e);
		}
		return result;
	}


	// VIEW REFRESH

	private void refreshAnswers() {
		//		Utils.debugMessage("refreshAnswers " + place.getAnswers());
		mainPage.ansWidget.setAnswers(place.getAnswers());
		//		if(place.getAnswers().getCount() == 0 && place.hasMore()) {
		//			showMore();
		//		}
		//		Utils.debugMessage("FIN refreshAnswers");
	}

	private Form newForm() {
		return newForm(null);
	}

	private Form newForm(FormLine l) {
		Form result = new Form(l);

		if(l != null) {
			sewelisGetPlaceStatement(this.lispqlStatementQuery(l));
		}

		return result;
	}


	/**
	 * Initializations and handlers attribution
	 */
	@Override
	public void onModuleLoad() {
		Parser.setControl(this);
		initializeProfilesFromCookie();
		form = new Form(null);
		mainPage = new MainPage(this);

		RootPanel.get().add(mainPage);
		RootPanel.get().add(navBar);
		RootPanel.get().setStyleName("root");
		
		if((Cookies.getCookie(cookiesUserLogin) != null && Cookies.getCookie(cookiesUserLogin) != "") 
				&& (Cookies.getCookie(cookiesUserkey) != null && Cookies.getCookie(cookiesUserkey) != "")) {
			userKey = Cookies.getCookie(cookiesUserkey);
			userLogin = Cookies.getCookie(cookiesUserLogin);
			navBar.loginWid.loggedUsernameLabel.setText(userLogin);
			navBar.loginWid.setLogState(LOGIN_STATE.LOGGED);
		}

		// PROFILES
		navBar.adminPanel.profileEditArea.setText(this.getProfileDocument().toString());

		// handlers attribution

		navBar.adminPanel.profileModeButton.addClickHandler(this);
		navBar.adminPanel.profileCreateButton.addClickHandler(this);
		navBar.adminPanel.profileClearButton.addClickHandler(this);
		navBar.adminPanel.profileGoButton.addClickHandler(this);
		navBar.adminPanel.profileDeleteButton.addClickHandler(this);
		navBar.adminPanel.profileEditSave.addClickHandler(this);
		navBar.adminPanel.profileEditClear.addClickHandler(this);
		navBar.adminPanel.profileEditReload.addClickHandler(this);
		navBar.adminPanel.namespaceDefineButton.addClickHandler(this);
		navBar.finishButton.addClickHandler(this);
		
		navBar.storeListBox.addChangeHandler(new ChangeHandler(){
			@Override
			public void onChange(ChangeEvent event) {
				if(event.getSource() == navBar.storeListBox) {
					int selectIndex= navBar.storeListBox.getSelectedIndex();
					String selectValue = navBar.storeListBox.getValue(selectIndex);
					setCurrentStore(selectValue);
					sewelisGetPlaceHome();
				}
			}
		});

		navBar.adminPanel.limitBox.setValue(CustomSuggestionWidget.getLimit());
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
		
		Timer pingTimer = new Timer() {
			@Override
			public void run() {
				try {
					sewelisPing();
				} catch (RequestException e) {
					ControlUtils.exceptionMessage(e);
				}
			}
		};
		pingTimer.scheduleRepeating(30000);

	}
	
	// PROFILES

	/**
	 * Interroge la base, crée un profile de base pour la classe, hors rdfs/owl, qui a le plus d'élements
	 */
	public Profile formToProfile() {
		if(currentStore != null) {
			Profile pro = new Profile(this.currentStore.getName(), this.currentStore.getName());
			pro.setForm(this.mainPage.formWidget.toProfileForm());

			return pro;
		}
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
			
			if(line.isFinished()) {
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
			if(f.isFinished()) {
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
	 * Handling click event on the mainpage
	 */
	@Override
	public void onClick(ClickEvent event) {
		if(event.getSource() == navBar.adminPanel.profileModeButton) {
			this.mainPage.formWidget.toggleProfileMode();

		} else if(event.getSource() == navBar.adminPanel.profileCreateButton) {
			if(this.mainPage.formWidget.isInProfileMode()) {
				Profile pro = formToProfile();
				pro.setName(navBar.adminPanel.profileNameBox.getValue());
				addProfile(pro);
				reloadNavbarProfileList();
			}

		} else if(event.getSource() == navBar.adminPanel.profileClearButton) {
			this.clearProfiles();
			reloadNavbarProfileList();

		}else if(event.getSource() == navBar.adminPanel.profileGoButton) {
			String select = navBar.adminPanel.profileList.getSelectedValue();
			setProfile(findProfile(profiles, select));

		}else if(event.getSource() == navBar.adminPanel.profileDeleteButton) {
			String select = navBar.adminPanel.profileList.getSelectedValue();
			Profile pro = findProfile(profiles, select);
			if(pro != null) {
				removeProfile(pro);
			}
			
			reloadNavbarProfileList();
		}else if(event.getSource() == navBar.adminPanel.namespaceDefineButton) {
			if(navBar.adminPanel.namespacePrefixBox.getValue() != "" && navBar.adminPanel.namespaceUriBox.getValue() != "") {
				this.sewelisDefineNamespace(navBar.adminPanel.namespacePrefixBox.getValue(), navBar.adminPanel.namespaceUriBox.getValue());
			}

		} else if(event.getSource() == navBar.loginWid.notLoggedLoginButton) {
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
			
		} else if(event.getSource() == this.navBar.adminPanel.profileEditSave) {
			this.setProfileDocument(navBar.adminPanel.profileEditArea.getText());
			
		} else if(event.getSource() == this.navBar.adminPanel.profileEditClear) {
			navBar.adminPanel.profileEditArea.setText("");
			
		} else if(event.getSource() == this.navBar.adminPanel.profileEditReload) {
			navBar.adminPanel.profileEditArea.setText(getProfileDocument().toString());
		}

	}

	@Override
	public void onFocusChange(Focus f) {
		sewelisChangeFocus(f.getId());
	}


	

	@Override
	public void onLineSelection(LineSelectionEvent event) {
		// SELECTION DE LIGNE
		// la selection d'une ligne entraine un changement de statement
		ControlUtils.debugMessage("Controller onLineSelection ( " + event.getSource() + " )");
		this.place.clearCurrentCompletions();
		// La source est forcément une ligne
		FormLineWidget widSource = (FormLineWidget)event.getSource();
		FormWidget widSourceParent = widSource.getParentWidget();
		FormLine dataSource = widSource.getFormLine();
		Form dataSourceParent = dataSource.getParent();
		String queryLineLispql = lispqlStatementQuery(dataSource);

		if(dataSource instanceof FormClassLine) { // Selection d'une classe
			ControlUtils.debugMessage("Controller onLineSelection BY A CLASS");
//			if(dataSource.getFixedElement() instanceof BasicElement) {
//				this.place.getAnswers().setPivotElement((BasicElement) dataSource.getFixedElement());
//			}
			// Si c'est une classe de litteral
			if( ControlUtils.LITTERAL_URIS.isLitteralType(((URI) dataSource.getFixedElement()).getUri())) {

				// si le form n'a pas encore de type
			} else if(dataSourceParent.isAnonymous() || dataSourceParent.isTypeList()) {
				dataSourceParent.addTypeLine((FormClassLine) dataSource, true);
				ControlUtils.debugMessage("Controller onLineSelection BY A CLASS SETTING TYPE LINE");
				sewelisGetPlaceStatement(queryLineLispql, new StatementChangeEvent(widSourceParent, widSourceParent.getCallback()));
				// Si la ligne avait déjà un type (retractation)
			} else {
				ControlUtils.debugMessage("Controller onLineSelection BY A CLASS RESETING TYPE LINE");
//				dataSourceParent.addTypeLine(null, true);
//				String queryFormLispql = lispqlStatementQuery(dataSourceParent);
//				sewelisGetPlaceStatement(queryFormLispql, new StatementChangeEvent(widSourceParent, widSourceParent.getCallback()));
				dataSourceParent.clear();
				String queryFormLispql = lispqlStatementQuery(dataSourceParent);
				sewelisGetPlaceStatement(queryFormLispql, new StatementChangeEvent(widSourceParent, widSourceParent.getCallback()));
			}
		} else if(dataSource instanceof FormRelationLine) { // selection d'une relation
			ControlUtils.debugMessage("Controller onLineSelection BY A RELATION");
//			if(dataSource.getFixedElement() instanceof BasicElement) {
//				this.place.getAnswers().setPivotElement((BasicElement) dataSource.getFixedElement());
//			}
			sewelisGetPlaceStatement(queryLineLispql, new StatementChangeEvent(widSource, event.getCallback()));
		}

		incrementNumberOfActions();
	}

	@Override
	public void onCompletionAsked(CompletionAskedEvent event) {
		// COMPLETIONS DEMANDEES
		// Les completions doivent être rechargée pour correspondre au statement
		if(event.getCallback() != null) {
			event.getCallback().call(this);
		}
		
//		incrementNumberOfActions();
	}

	@Override
	public void onStatementChange(StatementChangeEvent event) {
		// CHANGEMENT DE STATEMENT
		// Le contenu du statement a été changé est pret a être chargé dans le formulaire source
		ControlUtils.debugMessage("onStatementChange " + event.getSource().getClass());
		if(event.getSource() instanceof FormWidget) {
			FormWidget widSource = (FormWidget)event.getSource();
			ControlUtils.debugMessage("onStatementChange BY A FORM " + widSource.getData().toLispql());
			event.getCallback().call(this);
		} else if(event.getSource() instanceof FormRelationLineWidget) {
			FormRelationLineWidget widSource = (FormRelationLineWidget) event.getSource();
			SuggestionCallback callback = (SuggestionCallback) event.getCallback();
			if(widSource.getData().isFinished()) {
				ControlUtils.debugMessage("onStatementChange CHANGE BY A FINISHED LINE");
				sewelisGetPlaceStatement(this.lispqlStatementQuery(widSource.getData().getParent()));
			} else {
				ControlUtils.debugMessage("onStatementChange CHANGE BY A LINE");
				onCompletionAsked(new CompletionAskedEvent(event.getSource(), callback));
			}
		}
		refreshAnswers();

//		incrementNumberOfActions();
	}

//	@Override
//	public void onNestedForm(NestedFormEvent event) {
//		// NOUVEAU FORMULAIRE IMBRIQUE
//		// Crée un nouveau formulaire, demande un changement de statement puis un chargement du formulaire
//		ControlUtils.debugMessage("onNestedForm");
//		FormLineWidget widSource = ((FormLineWidget)event.getSource());
//		FormLine dataSource = widSource.getFormLine();
//		Form newDataForm = new Form(dataSource);
//		FormWidget newFormWid = new FormWidget(newDataForm, widSource);
//		widSource.setVariableElement(newFormWid);
//		String queryLineLispql = lispqlStatementQuery(dataSource);
//		sewelisGetPlaceStatement(queryLineLispql, new StatementChangeEvent(newFormWid, newFormWid.getCallback()));
//	}

	/**
	 * Evenement de demande de création d'élément provenant d'un widget de ligne de relation (FormRelationLineWidget)
	 * Si les éléments pour un nouveau formulaire sont présent, un nouveau formulaire est crée et le statement est changé pour charger son contenu via un statementchangeevent
	 * Sinon la ligne est en mode création de littéral
	 */
	@Override
	public void onElementCreation(ElementCreationEvent event) {
		// CREATION D'UN NOUVEL ELEMENT
		ControlUtils.debugMessage("onElementCreation");
		FormLineWidget widSource = event.getSource();
		FormLine dataSource = widSource.getFormLine();
		Form newDataForm = new Form(dataSource);
		FormWidget newFormWid = new FormWidget(newDataForm, widSource);
		//		boolean newFormFilled = loadFormContent(newFormWid);
		boolean newFormFilled = this.isFormContentLoadable(newFormWid);
		if(newFormFilled) {			
			widSource.setLineState(LINE_STATE.GUIDED_CREATION);
			dataSource.setVariableElement(newDataForm);
			widSource.setVariableElement(newFormWid);
			newFormWid.addClickWidgetEventHandler(widSource);
//			newFormWid.addCompletionAskedHandler(widSource);
//			newFormWid.addElementCreationHandler(widSource);
//			newFormWid.addLineSelectionHandler(widSource);
//			newFormWid.addMoreCompletionsHandler(widSource);
//			newFormWid.addRelationCreationHandler(widSource);
//			newFormWid.addRemoveLineHandler(widSource);
//			newFormWid.addStatementChangeHandler(widSource);
			ViewUtils.connectFormEventChain(newFormWid, widSource);

			String queryLineLispql = lispqlStatementQuery(dataSource);
			sewelisGetPlaceStatement(queryLineLispql, new StatementChangeEvent(newFormWid, newFormWid.getCallback()));
		} else {
			widSource.setLineState(LINE_STATE.CREATION, new CreationTypeOracle(this.getPlaceLiteralLines(widSource.getParentWidget())));
		}

		incrementNumberOfActions();
	}

	@Override
	public void onRemoveLine(RemoveLineEvent event) {
		FormLineWidget widSource = event.getSource();
		FormLine dataSource = widSource.getData();
		FormWidget widSourceParent = widSource.getParentWidget();
		Form dataSourceParent = widSourceParent.getData();

		dataSourceParent.removeRelationLine(dataSource);
		widSourceParent.reload();

		incrementNumberOfActions();
	}

//	@Override
//	public void onTypeLineSet(TypeLineSetEvent event) {
//		String queryString = lispqlStatementQuery(event.getSource().getData());
//		this.sewelisGetPlaceStatement(queryString, new StatementChangeEvent(event.getSource(), event.getSource().getCallback()));
//
//		incrementNumberOfActions();
//	}

	//	public void onCompletionReady(ElementSuggestionWidget source) {
	//		Utils.debugMessage("onCompletionReady " + place.getCurrentCompletions().size());
	//		if(place.getCurrentCompletions().size() < 2 && place.hasMore()) {
	//			showMore();
	//		}
	//		if(place.getCurrentCompletions() != null) {
	//			source.setOracleSuggestion(place.getCurrentCompletions());
	//			source.getSuggestBox().showSuggestionList();
	//		}
	//	}

	public void onCompletionReady(CustomSuggestionWidget source) {
//		Utils.debugMessage("onCompletionReady " + place.getCurrentCompletions().size());
		if(place.getCurrentCompletions().size() < 2 && place.hasMore()) {
			sewelisShowMore();
		}
		if(place.getCurrentCompletions() != null) {
			source.setOracleSuggestions(place.getCurrentCompletions());
			source.showSuggestions();
		}
	}

	@Override
	public void onMoreCompletions(MoreCompletionsEvent event) {
//		Utils.debugMessage("onMoreCompletions");
		String queryString = lispqlStatementQuery(event.getSource().getParentWidget().getData(), true);
		sewelisGetPlaceStatement(queryString, event);

		incrementNumberOfActions();
	}

	@Override
	public void onValueChange(ValueChangeEvent<Integer> event) {
		if(event.getSource() == this.navBar.adminPanel.limitBox) {
			CustomSuggestionWidget.setLimit(this.navBar.adminPanel.limitBox.getValue());
		}
	}

	@Override
	public void onRelationCreation(RelationCreationEvent event) {
		if(event.getSource() instanceof RelationCreateWidget) {
			RelationCreateWidget widSource = (RelationCreateWidget) event.getSource();
			FormWidget parentWidSource = widSource.getParentWidget();
			
			String uri = serverAdress + widSource.getTextValue();
			String label = widSource.getTextValue();
			
			URI uriObj = new URI(uri, URI.KIND.PROPERTY, label);
			
			FormRelationLine newLine = new FormRelationLine(parentWidSource.getData(), uriObj);
			newLine.setAsNew(true);
			parentWidSource.getData().addLine(newLine);
			parentWidSource.reload();
 		}

		incrementNumberOfActions();
	}
	
	
	
	// GESTION FORMULAIRE

	/**
	 * Créé un formulaire vide et déplace le stateement à la racine "get []"
	 */
	protected void getRootForm() {
		setCurrentForm( newForm());
		sewelisGetPlaceStatement("get [ ]", new StatementChangeEvent(mainPage.formWidget, mainPage.formWidget.getCallback()));
	}
	
	public void setCurrentForm(Form f) {
		ControlUtils.debugMessage("setCurrentForm( " + f + " )");
		this.form = f;
		mainPage.formWidget.setData(this.form);
		mainPage.formWidget.reload();
	}

	/**
	 * Vide et charge les lignes du formulaire en argument en fonction du contenu de la place courante.
	 * Si il n'y a pas de ligne de type, il charge les lignes de Class, sinon les lignes de relation
	 * @param widSource
	 * @return
	 */
	public void loadFormContent(FormWidget widSource) {
		ControlUtils.debugMessage("loadFormContent " + widSource);

		if(this.isFormContentLoadable(widSource)) {

			widSource.getData().clear();

			LinkedList<FormClassLine> classLines = getPlaceClassLines(widSource);
			LinkedList<FormRelationLine> relationLines = getPlaceRelationLines(widSource);
			Collections.sort(relationLines, new FormLineComparator());
			Collections.sort(classLines, new FormLineComparator());
			//		LinkedList<FormLine> literalLines = this.getPlaceliteralLines(widSource);
			widSource.getData().addAllTypeLines(classLines);


			if(widSource.getData().isAnonymous() || widSource.getData().isTypeList()) {
				ControlUtils.debugMessage("loadFormContent anonyme" );
				if(classLines.size() == 1 ) {
					ControlUtils.debugMessage("loadFormContent anonyme un seul type" );
					String queryString = lispqlStatementQuery(widSource.getData());
					// Ca ne devrai pas modifier le statement, on chargement le contenu de Place ici
					this.sewelisGetPlaceStatement(queryString, new StatementChangeEvent(widSource, widSource.getCallback()));

					incrementNumberOfActions();
				} else {
//					ControlUtils.debugMessage("loadFormContent anonyme 0 ou n types : " + classLines );
//					int nbLines = classLines.size();
//					Iterator<FormClassLine> itClassLines = classLines.iterator();
//					while(itClassLines.hasNext()) {
//						FormClassLine classLine = itClassLines.next();
//						classLine.setWeight(nbLines);
//						nbLines--;
//					}
//					
//					widSource.getData().addAllLines(classLines);
				}
			} else {
				ControlUtils.debugMessage("loadFormContent typé" );
				int nbLines = relationLines.size();
				Iterator<FormRelationLine> itRelLines = relationLines.iterator();
				while(itRelLines.hasNext()) {
					FormRelationLine relLine = itRelLines.next();
					relLine.setWeight(nbLines);
					nbLines--;
				}
				
				widSource.getData().addAllLines(relationLines);
			}

			widSource.reload();
		}
	}

	public void appendFormContent(FormWidget widSource) {
		ControlUtils.debugMessage("appendFormContent " + widSource);

		if(this.isFormContentLoadable(widSource)) {
			LinkedList<FormClassLine> classLines = getPlaceClassLines(widSource);
			LinkedList<FormRelationLine> relationLines = getPlaceRelationLines(widSource);
			ControlUtils.debugMessage("appendFormContent content: " + classLines + relationLines);
			if(widSource.getData().getTypeLines().isEmpty()) {
				widSource.getData().addAllLines(classLines);
			} else {
				widSource.getData().addAllLines(relationLines);
			}
			widSource.reload();
		}
	}
	
	public void addNewRelation(FormWidget widSource, URI relation) {
		
	}

	/**
	 * Vérifie si il y a les lignes nécessaires pour qu'un nouveau formulaire soir chargé:
	 * Soit un ensemble de lignes de classe, 
	 * soit une seule ligne de classe, 
	 * soit un ensemble de lignes de relations
	 * @param widSource
	 * @return
	 */
	public boolean isFormContentLoadable(FormWidget widSource) {
		LinkedList<FormClassLine> classLines = getPlaceClassLines(widSource);
		LinkedList<FormRelationLine> relationLines = getPlaceRelationLines(widSource);

		return (widSource.getData().isAnonymous()
				&& ! classLines.isEmpty()
				&& (classLines.size() == 1 
				&& classLines.getFirst() instanceof FormClassLine)
				|| (! classLines.isEmpty())) 
				|| ! relationLines.isEmpty();
	}

	private void setProfile(Profile pro) {
		ControlUtils.debugMessage("setProfile " + pro);
		if(this.currentStore == null || this.currentStore.getName() != pro.getStoreName()) {
			this.setCurrentStore(pro.getStoreName());
		}
		try {
			Form fo = pro.toForm();
			ControlUtils.debugMessage("setProfile candidate Form: " + fo);
			ControlUtils.debugMessage("setProfile currentForm (BEFORE): " + this.form);
			this.setCurrentForm(fo);
			ControlUtils.debugMessage("setProfile currentForm (AFTER): " + this.form);
			ControlUtils.debugMessage(pro + " = " + fo);
			//		this.mainPage.setFormWidget(new FormWidget(fo, null));
			StatementChangeEvent event = new StatementChangeEvent(mainPage.formWidget, mainPage.formWidget.getCallback(FORM_CALLBACK_MODE.APPEND));
			sewelisGetPlaceStatement(lispqlStatementQuery(form), event);
		} catch (FormElementConversionException e) {
			ControlUtils.exceptionMessage(e);
		}
	}

	public LinkedList<FormClassLine> getPlaceClassLines(FormWidget widSource) {
		LinkedList<FormClassLine> result = new LinkedList<FormClassLine>();

		Iterator<Increment> itIncre = place.getSuggestions().relationIterator();
		while(itIncre.hasNext()) {
			Increment incre = itIncre.next();
			if(incre.getKind() == KIND.CLASS) {
				FormLine newLine = DataUtils.formLineFromIncrement(incre, widSource.getData());
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
	 * Récupération des lignes avec relation après filtrage des valeurs interdites
	 * @param widSource Widget sont les données seront la racine des lignes retournées
	 * @return lignes relations après filtrage des valeurs interdites
	 */
	public LinkedList<FormRelationLine> getPlaceRelationLines(FormWidget widSource) {
		LinkedList<FormRelationLine> result = new LinkedList<FormRelationLine>();

		Iterator<Increment> itIncre = place.getSuggestions().relationIterator();
		while(itIncre.hasNext()) {
			Increment incre = itIncre.next();
			if(incre.getKind() == KIND.PROPERTY) {
				FormLine newLine = DataUtils.formLineFromIncrement(incre, widSource.getData());
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

	public LinkedList<BasicLeafElement> getPlaceLiteralLines(FormWidget widSource) {
		LinkedList<BasicLeafElement> result = new LinkedList<BasicLeafElement>();

		Iterator<Increment> itIncre = place.getSuggestions().entityIterator();
		while(itIncre.hasNext()) {
			Increment incre = itIncre.next();
			ControlUtils.debugMessage(incre.toString());
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

	public static String newElementUri(String label) {
		String result = serverAdress + /*"/" + currentStore.getName() +*/ "#";
		int uriMinSize = 8;
		if(label != null && label.length() > uriMinSize) {
			result += label;
		} else {
			if(label != null && label.length() < uriMinSize) {
				result += label + "_";
			}
			// CTRL-C CTRL-V de http://stackoverflow.com/questions/20536566/creating-a-random-string-with-a-z-and-0-9-in-java
			String RANDCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
			StringBuilder randBuilder = new StringBuilder();
			int randSize = uriMinSize - label.length();
			while (randBuilder.length() < randSize) {
				int index = (Random.nextInt(RANDCHARS.length()));
				randBuilder.append(RANDCHARS.charAt(index));
			}
			String randString = randBuilder.toString();
			result += randString;
		}
		return result;
	}

	public String lispqlStatementQuery(FormElement e) {
		return lispqlStatementQuery(e, false);
	}

	public String lispqlStatementQuery(FormElement e, boolean root) {
//		Utils.debugMessage("lispqlStatementQuery( " + e + " )");
		String result = "";
		if(e instanceof FormLine) {
			FormLine line = (FormLine) e;
			if(e instanceof FormRelationLine) {
				if(root) {
					FormRelationLine relLine = (FormRelationLine) e;
					result = "get [ " + relLine.toRootLispql() + " ]";
				} else {
					result = "get [ " + line.toLispql(true, false) + " ]";
				}
			} else if(e instanceof FormClassLine) {
				result = "get [ " + line.toLispql() + " ]";
			}
		} else {
			result = "get " + e.toLispql() + "";
		}
//		Utils.debugMessage("lispqlStatementQuery( " + e + " ) " + result);
		return result;
	}
	
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

	@Override
	public void onFinishForm(FinishFormEvent event) {
		finish( ( (FormWidget)event.getSource()).getData());
	}

	/**
	 * Le controller est handler parce qu'il est en bout de FormEventCHain, mais il n'est pas sensé en faire quoi que ce soit.
	 * @param event
	 */
	@Override
	public void onFinishLine(FinishLineEvent event) {
		ControlUtils.debugMessage("Controller onFinishLine " + event.getSource().toString());
	}

}
