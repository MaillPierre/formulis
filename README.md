# FORMULIS #

This project is the prototype implementation of Formulis, a purely data-driven interface to RDF based on forms and using intelligent suggestions. 
The Formulis Webapp is a war archive to be hosted in a web server. The Formulis application only works when interfaced with a [SEWELIS RDF server](https://bitbucket.org/sebferre/sewelis "Bitbucket for the SEWELIS implementation").

## USING FORMULIS ##
To deploy Formulis once you have the archive, you need to edit the index.html file to write the address of your Sewelis server. The value to edit are in the js code in index.html:

     var formulisSettings = {
          serverAdress: "URL_TO_REPLACE",
          uriBaseAdress: "http://www.irisa.fr/LIS/sewelis/",
     };

Once configured, the Formulis web site should retrieve automatically the available stores at least at anonymous level and display them in the list of stores on top of the page. At each selection of a store, Formulis display the list of classes given by Sewelis. Once a class is selected, it display a empty form with the most commonly filled properties for the selected class.

## IMPLEMENTATION DETAILS ##
Formulis is implemented as a JS application generated using GWT. It interface with a SEWELIS server using POST requests. It is implemented following a MVC designe pattern. It also use the GWT/Bootstrap library.
### CONTROL ###
The `control` package contains classes for the security and data management outside of the form of the application.
- The `Controller` class is the entry point of the GWT application (method `onModuleLoad()`). It contains methods for the interaction with the sewelis server using SEWELIS services, which names are all prefixed by "sewelis". It is also the main root of the chain of user events.
- The `Crypto` class contains call to native code of the SJCL class for the security aspect of loging into SEWELIS and is related to the `access` package.

### MODEL ###
The `model` package content is separated into three categories:
- The elements corresponding to the data-structure extracted from the SEWELIS server answers: `Place`, `Answers`, `Statement`, `Suggestions` and related sub-packages.
- The basic elements shared by all data related classes such as URIs, etc. that were initially created according to the Statement XML syntax of SEWELIS.
- The `Form` elements containing the data-structures used to store forms (and nested forms) in the application.

#### SEWELIS PLACE COMPONENTS ####
The `Place`, `Answers`, `Statement`, `Suggestions` and the content of the ̀̀`answers` and `suggestions` packages reflect the data structure of the responses of a SEWELIS server to a query. These elements are converted to `form` and `basic` elements using functions from the `DataUtils` class.

#### BASIC ELEMENTS ####
TBD

#### FORM ELEMENTS ####
TBD

### VIEW ###
TBD

#### CREATION ####
TBD

#### EVENTS ####
TBD

#### FORMS ####
TBD


## DEPLOYMENT ##

To obtain an usable .war, compile the GWT project and compress in a .zip all the elements of the `war/` folder. You must not compile the code using the debug mode of your IDE.

## ACKNOWLEGDMENT ##

FORMULIS was developped by Pierre Maillot from the SemLIS team, IRISA, University of Rennes 1, Rennes, France (https://www-semlis.irisa.fr/) in the effort to make data more accessible to users. This work was partially supported by ANR project IDFRAud (ANR-14-CE28-0012).

## LICENCE ##

This work is licensed under the GPL license (precisely, by its equivalent under french law CECILL v2.1)
