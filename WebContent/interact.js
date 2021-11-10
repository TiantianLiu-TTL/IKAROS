
var curSizePar = 141;  // num of partitions per floor
var curSizeDoor = 216;  // num of doors per floor

var floorPlan_x = 0;  // x-axis of floor plan
var floorPlan_y = 0;  // y-axis of floor plan
var floorPlan_weight = 1368;  // width of floor plan
var original_floorPlan_weight = floorPlan_weight;

var currentFloor = 0;  // current floor
var currentFloor_s = 0;  //current floor of starting point
var currentFloor_e = 0;  //current floor of ending point

var records = [];

var currentPath = -1;  // current path to be shown

var shopInShown_id = -1;  // the id of the partition that its information is currently shown 
var shopInShown_color = "";	// the color of the partition that its information is currently shown 

var canPut_s = false;
var canPut_e = false;

function loadData(){
	var xhr = new XMLHttpRequest();
	
	xhr.onreadystatechange = function() {
		if (xhr.readyState == 4) {
			changeFloor(0);
		}
	}
	
	xhr.open('POST', 'DemoServlet?q_type=0', true);
	var start = new Date().getTime();
	xhr.send();
}

function TOE	() {
	refreshTopK();
  var xhr = new XMLHttpRequest();
  
  var q_sloc_temp = document.getElementById("sloc").value;
  var q_eloc_temp = document.getElementById("eloc").value;
  var q_distance = document.getElementById("dist").value;
  var q_keyword = document.getElementById("keywords").value;
  var q_k = document.getElementById("numofroute").value;
  
  var s_temp1 = q_sloc_temp.split(",");
  var e_temp1 = q_eloc_temp.split(",");
  
  var s_floor = parseInt(s_temp1[2].split("r ")[1]) - 1;
  var s_x = s_temp1[0].replace("(", "");
  var s_y = s_temp1[1].replace(")", "");
  s_y = s_y.replace(" ", "");
  var q_sloc = s_floor + "," + s_x + "," + s_y;
  
  var e_floor = parseInt(e_temp1[2].split("r ")[1]) - 1;
  var e_x = e_temp1[0].replace("(", "");
  var e_y = e_temp1[1].replace(")", "");
  e_y = e_y.replace(" ", "");
  var q_eloc = e_floor + "," + e_x + "," + e_y;
  
  xhr.onreadystatechange = function() {
	  if (xhr.readyState == 4) {
		  var sol = xhr.responseText;
		  
		  if (sol == "no such a route") addTopkReuslt("no such a route", -1);
		  else {
			// empty the records
			  records = [];

			  var tmps = sol.split(";");
			  records = tmps;
			  
			  for (var i = 0; i < records.length; i ++){
				  addTopkReuslt(records[i], i); 
			  }
		  }
		  
	  }
  }
  
  xhr.open('POST', 'DemoServlet?q_sloc=' + q_sloc + '&q_eloc=' + q_eloc + '&q_distance=' + q_distance + '&q_keyowrd=' + q_keyword  + '&q_k=' + q_k + '&q_type=2', true)
  xhr.send();
}

function KOE	() {
	refreshTopK();
  var xhr = new XMLHttpRequest();
  
  var q_sloc_temp = document.getElementById("sloc").value;
  var q_eloc_temp = document.getElementById("eloc").value;
  var q_distance = document.getElementById("dist").value;
  var q_keyword = document.getElementById("keywords").value;
  var q_k = document.getElementById("numofroute").value;
  
  var s_temp1 = q_sloc_temp.split(",");
  var e_temp1 = q_eloc_temp.split(",");
  
  var s_floor = parseInt(s_temp1[2].split("r ")[1]) - 1;
  var s_x = s_temp1[0].replace("(", "");
  var s_y = s_temp1[1].replace(")", "");
  s_y = s_y.replace(" ", "");
  var q_sloc = s_floor + "," + s_x + "," + s_y;
  
  var e_floor = parseInt(e_temp1[2].split("r ")[1]) - 1;
  var e_x = e_temp1[0].replace("(", "");
  var e_y = e_temp1[1].replace(")", "");
  e_y = e_y.replace(" ", "");
  var q_eloc = e_floor + "," + e_x + "," + e_y;
  
  xhr.onreadystatechange = function() {
	  if (xhr.readyState == 4) {
		  var sol = xhr.responseText;
		  
		  if (sol == "no such a route") addTopkReuslt("no such a route", -1);
		  else {
			// empty the records
			  records = [];

			  var tmps = sol.split(";");
			  records = tmps;
			  
			  for (var i = 0; i < records.length; i ++){
				  addTopkReuslt(records[i], i); 
			  }
		  }
	  }
  }
  
  xhr.open('POST', 'DemoServlet?q_sloc=' + q_sloc + '&q_eloc=' + q_eloc + '&q_distance=' + q_distance + '&q_keyowrd=' + q_keyword  + '&q_k=' + q_k + '&q_type=3', true)
  xhr.send();
}

function refreshPath(){
	for (var j = 0; j < 31; j ++){
		document.getElementById("p" + j).style.display = "none";
	}
}

function left(){
	floorPlan_x = floorPlan_x + 100;
    document.getElementById("floorPlan").setAttribute("viewBox", floorPlan_x + " " + floorPlan_y + " " + floorPlan_weight + " " + floorPlan_weight);
}

function right(){
	floorPlan_x = floorPlan_x - 100;
    document.getElementById("floorPlan").setAttribute("viewBox", floorPlan_x + " " + floorPlan_y + " " + floorPlan_weight + " " + floorPlan_weight);
}

function up(){
	floorPlan_y = floorPlan_y - 100;
    document.getElementById("floorPlan").setAttribute("viewBox", floorPlan_x + " " + floorPlan_y + " " + floorPlan_weight + " " + floorPlan_weight);
}

function down(){
	floorPlan_y = floorPlan_y + 100;
    document.getElementById("floorPlan").setAttribute("viewBox", floorPlan_x + " " + floorPlan_y + " " + floorPlan_weight + " " + floorPlan_weight);
}

function enlargeFromRange(){
	floorPlan_weight = original_floorPlan_weight - parseInt(document.getElementById("mapSize").value);
	document.getElementById("floorPlan").setAttribute("viewBox", floorPlan_x + " " + floorPlan_y + " " + floorPlan_weight + " " + floorPlan_weight);
}

function put(evt){
	var type = evt.value;
	if (type == "sloc"){
		canPut_s = true;
	} else if (type == "eloc"){
		canPut_e = true;
	}
}

function update(evt){
	var type = evt.value;
	var dom = document.getElementById(type);
	var value = dom.value;
	
	var landMark = document.getElementById("v0");
	var lm_left = landMark.getBoundingClientRect().left;
	var lm_right = landMark.getBoundingClientRect().right;
	var lm_top = landMark.getBoundingClientRect().top;
	var lm_bottom = landMark.getBoundingClientRect().bottom;
	
	var xconvert = (lm_right - lm_left) / 288;
	var yconvert = (lm_bottom - lm_top) / 288;
	
	var clientX = x * xconvert + lm_left;
	var clientY = y * yconvert + lm_top;
	
	if (type == "sloc"){
		var s_temp1 = value.split(",");
		  
		var z = s_temp1[2].split("r ")[1];
		var x = s_temp1[0].replace("(", "");
		var y = s_temp1[1].replace(")", "");
		y = y.replace(" ", "");
		
		s_loc = [];
	    s_loc.push(x);
	    s_loc.push(y);
	    
	    var marker = document.getElementById("marker_s");
	    marker.setAttribute("transform", "translate(" + (x - 35) + " " + (y - 80) + ") scale(0.15 0.15)");
	    
	    currentFloor_s = parseInt(z) - 1;
	    
	    attachShopName(type, x, y, (parseInt(z) - 1))
		
	} else if (type == "eloc"){
		var s_temp1 = value.split(",");
		  
		var z = s_temp1[2].split("r ")[1];
		var x = s_temp1[0].replace("(", "");
		var y = s_temp1[1].replace(")", "");
		y = y.replace(" ", "");
		
		e_loc = [];
		e_loc.push(x);
		e_loc.push(y);
		 
	    var marker = document.getElementById("marker_e");
	    marker.setAttribute("transform", "translate(" + (x - 35) + " " + (y - 80) + ") scale(0.15 0.15)");
	     
	    currentFloor_e = parseInt(z) - 1;
	     
	    attachShopName(type, x, y, (parseInt(z) - 1))
	}
}

function doubleclick(el, evt) {
    if (el.getAttribute("data-dblclick") == null) {
        el.setAttribute("data-dblclick", 1);
        setTimeout(function () {
            if (el.getAttribute("data-dblclick") == 1) {
            		oneClick(el, evt);
            }
            el.removeAttribute("data-dblclick");
        }, 300);
    } else {
        el.removeAttribute("data-dblclick");
        oneClick(el, evt);
    }
}

function oneClick(el, evt){
	var landMark = document.getElementById("v0");
	var lm_left = landMark.getBoundingClientRect().left;
	var lm_right = landMark.getBoundingClientRect().right;
	var lm_top = landMark.getBoundingClientRect().top;
	var lm_bottom = landMark.getBoundingClientRect().bottom;
	
	var xconvert = (lm_right - lm_left) / 288;
	var yconvert = (lm_bottom - lm_top) / 288;
	
    var x = Math.floor((evt.clientX - lm_left) / xconvert);
    var y = Math.floor((evt.clientY - lm_top) / yconvert);
    
    if (canPut_s){
    		s_loc = [];
        s_loc.push(x);
        s_loc.push(y);
        
        document.getElementById("sloc").value = "(" + x + ", " + y + ")" + ", Floor " + (currentFloor + 1);
        
        attachShopName("sloc", x, y, currentFloor);
        
        var marker = document.getElementById("marker_s");
        marker.setAttribute("transform", "translate(" + (x - 35) + " " + (y - 80) + ") scale(0.15 0.15)");
        
        currentFloor_s = currentFloor;
        
        canPut_s = false;
        
    } else if (canPut_e){
    		e_loc = [];
        e_loc.push(x);
        e_loc.push(y);
        
        document.getElementById("eloc").value = "(" + x + ", " + y + ")" + ", Floor " + (currentFloor + 1);
        
        attachShopName("eloc", x, y, currentFloor);
        
        var marker = document.getElementById("marker_e");
        marker.setAttribute("transform", "translate(" + (x - 35) + " " + (y - 80) + ") scale(0.15 0.15)");
        
        currentFloor_e = currentFloor;
        
        canPut_e = false;
    }
    
    showInfo(el);
    
    displayMarkers();
}

function attachShopName(str, x, y, z){
	var xhr = new XMLHttpRequest();
	
	xhr.onreadystatechange = function() {
		if (xhr.readyState == 4) {
			var sol = xhr.responseText;
			
			var dom = document.getElementById(str);
			
			var temp = document.getElementById(str).value.split(",");
			
			var old = temp[0] + "," + temp[1] + "," + temp[2];
			
			document.getElementById(str).value = old + ", " + sol;
		}
	}
	
	xhr.open('POST', 'DemoServlet?q_x=' + x + '&q_y=' + y + '&q_floor=' + z + '&q_type=5', true);
	var start = new Date().getTime();
	xhr.send();
	
}

function showInfo(x) {
	var xhr = new XMLHttpRequest();
	
	if (shopInShown_id != -1){
		var shopInShown_id_temp = shopInShown_id;
		var shopInShown_color_temp = shopInShown_color;
		document.getElementById(shopInShown_id_temp).style.fill = shopInShown_color_temp;
	}
	
	var q_id = x.id;
	shopInShown_id = q_id;
	shopInShown_color = x.style.fill;
	x.style.fill = "#ffffcc";
  
    xhr.onreadystatechange = function() {
	    if (xhr.readyState == 4) {
		    var sol = xhr.responseText;
		    
		    if (sol != ""){
		    		var temps = sol.split(",");
			    
			    var mID = temps[0];
			    var name = temps[1];
			    var open_hour = temps[2];
			    var img = temps[3];
			    var url = temps[4];
			    var phone = temps[5];
			    var website = temps[6];
			    var keywords = temps[7];
			    var keys = keywords.split("\t");
			    
			    var dom = document.getElementById("shopInfo");
			    dom.innerHTML = '';
			    
			    var box = document.createElement("div");
			    var tmp=document.createElement("div");
			    
			    var keyword_html = "";
			    for (var i = 0; i < keys.length; i ++){
			    		keyword_html += '<button id="' + keys[i] + '" class="button_css" onclick="addKeyword(this)">' + keys[i] + '</button>';
			    }
			    
				var html_0 = '<b>' + "Name: " + name + '</b>' + '<br>' + "Opening_hour: <br>" + open_hour + '<br>' + "Phone: <br>" + phone + '<br>' + "Keywords: <br>" + keyword_html + "<br"
				+ '<img src='+ img + ' alt="Smiley face" height="30%" width="30%">';
				tmp.innerHTML = html_0;
				
				box.appendChild(tmp);
				dom.appendChild(box);
		    }
	    }
    }
  
    xhr.open('POST', 'DemoServlet?q_id=' + q_id + '&q_currentFloor=' + currentFloor + '&q_type=1', true)
    xhr.send();
}

function addKeyword(evt){
	var currentValur = document.getElementById("keywords").value;
	
	if (currentValur == "undefined") {
		
		document.getElementById("keywords").value = evt.id;
		var element = document.createElement("button");
		element.style.value = evt.id + "&nbsp x";
		element.innerHTML = evt.id;
		element.classList.add('button_css');
		element.onclick = function(){
			element.parentNode.removeChild(element);
			var children = document.getElementById("keywords").children
			var str = "";
			
//			alert("no of children: " + children.length);
			
			for (var i = 0; i < children.length; i ++) {
				var tableChild = children[i];
				str += tableChild.style.value + ",";
			}
			
			document.getElementById("keywords").value = str;
			return false;
		}
		
		document.getElementById("keywords").appendChild(element);
		
	} else {
		
		document.getElementById("keywords").value = currentValur + "," + evt.id;
		var element = document.createElement("button");	
		element.style.value = evt.id;
		element.innerHTML = evt.id + "&nbsp x";
		element.classList.add('button_css');
		element.onclick = function(){
			element.parentNode.removeChild(element);
			var children = document.getElementById("keywords").children
			var str = "";
			
//			alert("no of children: " + children.length);
			
			for (var i = 0; i < children.length; i ++) {
				var tableChild = children[i];
				str += tableChild.style.value + ",";
			}
			
			document.getElementById("keywords").value = str;
			return false;
		}
		document.getElementById("keywords").appendChild(element);
	}
}

function selectKeyword(){
	var currentValur = document.getElementById("keywords").value;
	var temp = document.getElementById("selectKeyword").value;
	document.getElementById("selectKeyword").value = "";
	var evts = temp.split(",");
	
	if (currentValur == "undefined") {
		
		document.getElementById("keywords").value = temp;
		
		for (var j = 0; j < evts.length; j ++){
			var evt = evts[j];
			
			var element = document.createElement("button");
			element.style.value = evt;
			element.innerHTML = evt + "&nbsp x";
			element.classList.add('button_css');
			element.onclick = function(){
				this.remove();
				var children = document.getElementById("keywords").children
				var str = "";
				
				for (var i = 0; i < children.length; i ++) {
					var tableChild = children[i];
					str += tableChild.style.value + ",";
				}
				
				document.getElementById("keywords").value = str;
				return false;
			}
			
			document.getElementById("keywords").appendChild(element);
		}
		
	} else {
		
		document.getElementById("keywords").value = currentValur + "," + temp;
		
		for (var j = 0; j < evts.length; j ++){
			var evt = evts[j];
			
			var element = document.createElement("button");	
			element.style.value = evt;
			element.innerHTML = evt + "&nbsp x";
			element.classList.add('button_css');
			element.onclick = function(){
				this.remove();
				var children = document.getElementById("keywords").children
				var str = "";
				
				for (var i = 0; i < children.length; i ++) {
					var tableChild = children[i];
					str += tableChild.style.value + ",";
				}
				
				document.getElementById("keywords").value = str;
				return false;
			}
			
			document.getElementById("keywords").appendChild(element);
		}
	}
}

function changeFloor(new_floor){
	var xhr = new XMLHttpRequest();
	
	xhr.onreadystatechange = function() {
		if (xhr.readyState == 4) {
			var sol = xhr.responseText;
			
			var shop_names = sol.split(";");
			
			// change color
			document.getElementById(currentFloor).setAttribute("style", "background-color: white;");
			document.getElementById(currentFloor).style.color = "black";
			document.getElementById(new_floor).setAttribute("style", "background-color: #f44336;");
			document.getElementById(new_floor).style.color = "white";
			
			currentFloor = new_floor;
			
			// change floor plan
			var i = -1;
			for (i = 0; i < curSizePar; i ++) {
				var id = i + (currentFloor * 141);
				document.getElementById("vt" + i).innerHTML = shop_names[i];
			}
			
			// change markers
			displayMarkers();
			
			// change path
			drawPath(currentPath);
		}
	}
	
	xhr.open('POST', 'DemoServlet?q_floor=' + new_floor + '&q_type=4', true);
	var start = new Date().getTime();
	xhr.send();
}

function displayMarkers(){
	if (currentFloor == currentFloor_s){
		document.getElementById("marker_s").style.display = "";
	} else {
		document.getElementById("marker_s").style.display = "none";
	}
	
	if (currentFloor == currentFloor_e){
		document.getElementById("marker_e").style.display = "";
	} else {
		document.getElementById("marker_e").style.display = "none";
	}
}

function displayquerylog(){
	var dom = document.getElementById("details");
	if(dom.style.display=="none") {
		dom.style.display="inline";
	}
	else {
		dom.style.display="none";
	}
}


function addTopkReuslt(record, number){
	
	if (record == "no such a route"){
		var dom = document.getElementById("topkresult");
	    var box = document.createElement("div");
	    var gap = document.createElement("br");
		var tmp=document.createElement("div");
		var tmp1=document.createElement("div");
		
		var html_0 = '<b>' + "No Such A Route";
		var html_1 = '<img src='+ 'ban.png ' + ' alt="Smiley face" height="20%" width="20%">';
		tmp.innerHTML = html_0;
		tmp1.innerHTML = html_1;
		tmp.style.position= 'relative';
		tmp1.style.position= 'relative';
		tmp.style.left = '27%';
		tmp1.style.left = '2%';
		tmp.style.fontSize = '16px';
		
		if(dom.hasChildNodes()==false) {
			tmp1.style.top = '3%';
			tmp.style.top = '-13%';
		} else 	tmp.style.top = '-15%';
		
		box.appendChild(tmp);
		box.appendChild(tmp1);
		box.appendChild(gap);
		dom.appendChild(box);
		
	} else {
		elements = record.split("\t");
		  
	    // route
	    points = elements[0].split(",");
	    // Pc
	    pcs = elements[1].split(",");
	    // Wc
	    wcs = elements[2].split(",");
	    // distance 
	    distance = Math.round(elements[3] * 100) / 100;
	    // relScore 
	    relScore = elements[4];
	    // rankScore 
	    rankScore = elements[5];
	    // floorNum
	    floors = elements[6].split(",");
	    
	    var dom = document.getElementById("topkresult");
	    var box = document.createElement("div");
	    var gap = document.createElement("br");
		var tmp=document.createElement("div");
		var tmp1=document.createElement("div");
		
		var html_0 = '<b>' + "Distance: " + distance + '</b>' + '<br>' + "Relevant Score: " + relScore + '<br>' + "Path: <br>" + elements[0] + '<br>';
		var html_1 = '<img src='+ 'go.png ' + ' alt="Smiley face" height="20%" width="20%" onclick="drawPath(' + number + ')">';
		tmp.innerHTML = html_0;
		tmp1.innerHTML = html_1;
		tmp.style.position= 'relative';
		tmp1.style.position= 'relative';
		tmp.style.left = '27%';
		tmp1.style.left = '2%';
		tmp.style.fontSize = '16px';
		tmp.style.height = '16px';
		
		if(dom.hasChildNodes()==false) {
			tmp1.style.top = '3%';
			tmp.style.top = '-13%';
		} else 	tmp.style.top = '-15%';
		
		box.appendChild(tmp);
		box.appendChild(tmp1);
		box.appendChild(gap);
		dom.appendChild(box);
	}
}

function refreshTopK(){
	var dom = document.getElementById("topkresult");
	dom.innerHTML = '';
}

function drawPath(number){
	refreshPath();
	
	// update current path
	currentPath = number;
	
	elements = records[number].split("\t");
	// route
    var points = elements[0].split(",");
    // floorNum
    var floors = elements[6].split(",");
	
	// starting point to first door
	if (floors[1] == currentFloor && currentFloor_s == currentFloor){
		var id_2 = (points[1] - (curSizeDoor * currentFloor));
		var x_1 = s_loc[0];
		var y_1 = s_loc[1];
		var x_2 = document.getElementById("d" + id_2).getAttribute("x");
		var y_2 = document.getElementById("d" + id_2).getAttribute("y");
		document.getElementById("p0").setAttribute("d", "M " + x_1 + " " + y_1 + " l " + (x_2 - x_1) + " " + (y_2 - y_1));
		document.getElementById("p0").style.display = "";
	}
	
	// between other doors
	for (var j = 1; j < points.length - 2; j ++){
		if (floors[j] == currentFloor && floors[j + 1] == currentFloor){
			var id_1 = "d" + (points[j] - (curSizeDoor * currentFloor));
			var id_2 = "d" + (points[j + 1] - (curSizeDoor * currentFloor));
			
			var x_1 = document.getElementById(id_1).getAttribute("x");
			var y_1 = document.getElementById(id_1).getAttribute("y");
			var x_2 = document.getElementById(id_2).getAttribute("x");
			var y_2 = document.getElementById(id_2).getAttribute("y");
			
			document.getElementById("p" + j).setAttribute("d", "M " + x_1 + " " + y_1 + " l " + (x_2 - x_1) + " " + (y_2 - y_1));
			document.getElementById("p" + j).style.display = "";
		}
	}
	
	// last door to ending point
	if (floors[points.length - 2] == currentFloor && currentFloor_e == currentFloor){
		var id_1 = (points[points.length - 2] - (curSizeDoor * currentFloor));
		x_1 = document.getElementById("d" + id_1).getAttribute("x");
		y_1 = document.getElementById("d" + id_1).getAttribute("y");
		x_2 = e_loc[0];
		y_2 = e_loc[1];
		document.getElementById("p" + (points.length - 2)).setAttribute("d", "M " + x_1 + " " + y_1 + " l " + (x_2 - x_1) + " " + (y_2 - y_1));
		document.getElementById("p" + (points.length - 2)).style.display = "";
	}
	
}

function refreshTopKResult(){
	var dom = document.getElementById("topkresult");
    while(dom.hasChildNodes())
    {
        dom.removeChild(dom.firstChild);
    }
}