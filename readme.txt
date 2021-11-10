1. How to setup:

"Demo" is a Dynamic Web Project and also it is a Maven Project which can be run on server. We developed this project on Eclipse Java EE IDE for Web Developers, iOS system v10.15.6.

Server: Apache Tomcat v9.0 
	download link: https://tomcat.apache.org/download-90.cgi
	Setup and Install Apache Tomcat Server in Eclipse IDE: https://crunchify.com/step-by-step-guide-to-setup-and-install-apache-tomcat-server-in-eclipse-development-environment-ide/

Java: JavaSE-9

Maven Dependencies: Provided by Eclipse Java EE IDE for Web Developers
	download link: https://www.eclipse.org/downloads/packages/

After setting up the environment on Eclipse, can : File -> Open Project File from System, and import the project (if it does not work, can consider following steps):
	a) New -> Dynamic Web Project
	b) Right click the new project -> Configure -> Convert to Maven Prject -> (then a new file pom.xml will be created)
	c) copy the content between (include) <dependencies> </dependencies> in Demo/pom.xml to this newly created pom.xml, to the place bewtween   </build> and </project>
	d) copy the *.java files under /src
	e) copy the Data files, *.png, *.html, *.js, *.css under main folder.

2. What does it include

2.a) Data. All *.txt files in root folder, including 
	identityKeywordIndex.txt. Identity keywords and its corresponding index
	regularKeywordIndex.txt. Regular keywords and its corresponding index
	shopsWithKey-new.txt. Shops with shop information, all shop keywords are not indexed
	shopsWithIndex.txt. Shops with shop information, all shop keywords are indexed
	shopsWithPartition.txt. Map each partition with a shop (Format: partition ID \t shop ID \t shop original ID in shopsWithKey-new.txt). If you want to construct a new partition-shop mapping, go to algorithm_new/Algo2.java, function Algo2(), change the line "assignShop.staticAssign();" to "assignShop.randomAssign();".
	wordRelationship_identity.txt. Map each identity keyword to related regular keyword(s).
	wordRelationship_regular.txt. Map each regular keyword to related identity keyword(s).

2.b) Back-End. *.java in /src folder
	After receving the query, Back-End will calculate the result(s). MAIN ENTRY POINT is algorithm_new/Algo2.java

2.c) Bridge between Front-End and Back-End. Bridge is implemented by com.whynot.demo.DemoServlet.java ONLY, under /src/com/whynot/demo folder.
	2.c.1) init() will be called automically once the server is started, we new the Algo2() object here and load in some necessary data
	2.c.2) doGet() is used to get the request sent from Front-End, the requests are generated in interact.js, See Section 2.d)
	2.c.3) TOE() calls ptpWordDist1() in algorithm_new/Algo2.java
	2.c.4) KOE() calls ptpWordDist2() in algorithm_new/Algo2.java

2.d) Front-End. Mainly on web design. It mainly CONSISTS OF *.html, *.js, *.css under /WebContent folder
	index.html. Is the main page of the website, if you want to add some buttons, words, you can do it in this file.
	mystyle.css. For website style design, if you want to change the style of the buttons, words you can do it in this file
	interact.js. Provides some functions for index.html, and have functions that send requests to Bridge, receive response from Bridge after. If you want to add/delete/adjust some functions, you can do it in this file

3. Parameter Setting.
3.a) Prefix address for input files, can be changed in /Utilities/FilePaths.java
3.b) Number of floors, can be changed in /Utilities/DataGenConstant.java, public static int nFloor = ???
3.c) Reassign shop (change the mapping between Partitions and Shops) go to algorithm_new/Algo2.java, function Algo2(), change the line "assignShop.staticAssign();" to "assignShop.randomAssign();".

	
	
