1. Introduction
This is the code of a paper entitled ``IKAROS: An Indoor Keyword-Aware Routing System''.

IKAEOS is a Dynamic Web Project and also it is a Maven Project which can be run on server.  IKAROS efficiently answers the indoor top-k keyword-aware routing query (IKRQ). 

2. Data and Code

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
3.b) Number of floors, can be changed in /Utilities/DataGenConstant.java, public static int nFloor
3.c) Reassign shop (change the mapping between Partitions and Shops) go to algorithm_new/Algo2.java, function Algo2(), change the line "assignShop.staticAssign();" to "assignShop.randomAssign();".

4. Contact

Tiantian Liu (liutt@cs.aau.dk)
Zijin Feng (zjfeng@se.cuhk.edu.hk)
Huan Li (lihuan@cs.aau.dk)
Hua Lu (luhua@ruc.dk)
Please feel free to contact us if any issues. You are also welcome to open an issue through GitHub. We will continue to maintain this project.

5. Citation
You are welcome to use our code and datasets for research use, but please do not forget to cite our paper :).

Zijin Feng, Tiantian Liu, Huan Li, Hua Lu, Lidan Shou and Jianliang Xu. Indoor Top-kKeyword-aware Routing Query. In ICDE, pages 1213-1224, 2020.
Tiantian Liu, Zijin Feng, Huan Li, Hua Lu, Lidan Shou and Jianliang Xu. IKAROS: An Indoor Keyword-Aware Routing System. 



	
	
