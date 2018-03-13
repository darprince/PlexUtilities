if(document.title.indexOf("Pirate") != -1){
	console.log("Matched The Pirate Bay");
	var scriptElements = document.getElementsByTagName('script');
	console.log("Script elements "+ scriptElements.length);
	for(i = 0; i<scriptElements.length; i++){
		if(scriptElements[i].innerHTML.match("location")){
			scriptElements[i].parentNode.removeChild(scriptElements[i]);
		}
	}

	var linkElements = document.getElementsByTagName('link');
	console.log("Link elements "+ linkElements.length);
	for(i = 2; i<linkElements.length; i++){
		linkElements[i].removeAttribute('href');
		console.log("Link "+linkElements[i].getAttribute('href'));
		linkElements[i].remove();
	}

	var scriptElements = document.getElementsByTagName("script");
	for(i = 0; i<scriptElements.length; i++){
		if(scriptElements[i].innerHTML.match(/_wm_settings/)){
			console.log("Match Script");
			scriptElements[i].innerHTML="";
		}
		//console.log(scriptElements[i]);
	}

}

if (document.title.indexOf("RARBG") != -1) {
	console.log("Matched RARBG");
	// varShadowEls = document.getElementsByTagName('shadow');
	// console.log("Shadow count: "+varShadowEls.length);

	//Remove the "missing comments" dialog from the top of the page
	var divElements = document.getElementsByTagName('div');
	for(i = 0; i<divElements.length; i++){
		if(divElements[i].innerHTML.match('missing comments')){
			document.body.removeChild(divElements[i]);
		}
	}

	//remove the login and torrent header from the top of the page
	var tableElements = document.getElementsByTagName('table');
	document.body.removeChild(tableElements[0]);
	var tableElements = document.getElementsByTagName('table');
	document.body.removeChild(tableElements[0]);

	//remove the movie images
	var tableElements = document.getElementsByTagName('table');
	for(i=0;i<tableElements.length; i++){
		var inc = tableElements[i].getAttribute("width");
		if(inc != null && inc.includes("100%")){
			console.log(i);
			console.log(tableElements[i]);
		}
	}
	var page = window.location.href;
	if(page.includes("CANCELpage=")){

		var sheet = window.document.styleSheets[0]
		sheet.insertRule('table.lista-rounded { margin-top: -50px; }', 0);

		tableElements[3].remove();
		//remove search box and option selector
		var searchTor = document.getElementById("searchTorrent");
		searchTor.remove();

		var divs = document.getElementsByTagName('div');
		console.log("divs: "+divs.length);
		for(i=0; i<divs.length; i++){
			console.log(i);
			console.log(divs[i]);
			var attr = divs[i].getAttribute("align");
			if(attr != null && attr.includes("center")){
				if(i!=6 && i!=7 && i!=9){
					console.log("removing: "+i)
					divs[i].remove();
				}
			}
		}
			// divs[0].remove();
			divs[1].remove();
			divs[2].remove();
			// divs[3].remove();
			// divs[4].remove();
			// divs[5].remove();
			// divs[6].remove();
			// divs[7].remove();
			// divs[8].remove();
			// divs[9].remove();

			var brs = document.getElementsByTagName('br');
			console.log(brs.length);
			for(i=0; i<brs.length; i++){
				console.log(i);
				console.log(brs[i]);
			}

			brs[0].remove();
			brs[1].remove();
			brs[2].remove();
			brs[3].remove();
			brs[4].remove();
			brs[5].remove();
			brs[6].remove();
			brs[7].remove();
			brs[8].remove();
			brs[9].remove();
			brs[10].remove();
			brs[11].remove();
			brs[12].remove();
			brs[13].remove();
			brs[14].remove();
			brs[15].remove();
			brs[16].remove();
			brs[17].remove();
			brs[18].remove();
	
	}

	
	var tdElements = document.getElementsByTagName('td');
	// document.removeChild(tdElements[17]);
	for(i = 0; i<20; i++){
		if(tdElements[i].className == 'block'){
			//console.log('td: '+i);
			tdElements[i].remove();
		}
	}

	//set all links to download instead of opening up another page
	var allElements = document.getElementsByTagName('a');
	for(i = 0; i<allElements.length; i++){
		var href = allElements[i].getAttribute("href");
		if(!isEmpty(href)){
			if(href.includes("/torrent/")){
				console.log("Changing URL!!!!!!!!!!!!!!!!!!!!!!!!!");
				var id = getId(href);
				var title = allElements[i].getAttribute("title");
				var onmouseover = allElements[i].getAttribute("onmouseover");

				allElements[i].setAttribute("href", "/download.php?id="+id+"&f="+title+".torrent");
				allElements[i].setAttribute("onclick", "current_url = window.location.href; history.replaceState({},'',href); history.replaceState({},'',current_url);document.getElementById('snackbar').className = 'show'; setTimeout(function(){ document.getElementById('snackbar').className = document.getElementById('snackbar').className.replace('show', ''); }, 3000);");
			
				allElements[i].removeAttribute("onmouseover");
				allElements[i].removeAttribute("mouseover");
				allElements[i].setAttribute("onmouseover", "nd()");	
			}
		}
	}

	var request = new XMLHttpRequest();

	request.onreadystatechange = function() {
	jsontext = request.responseText;

		if(!isEmpty(jsontext)){
	  		//alert("HTML: "+jsontext);
			console.log(jsontext);
		}
	}

	//show confirmation of torrent download
	var divToast = document.createElement("DIV");
	divToast.setAttribute("id", "snackbar");
	divToast.innerHTML = "Button Clicked";
	document.body.appendChild(divToast);

	//remove script elements
	var scriptElements = document.getElementsByTagName("script");
	for(i = 0; i<scriptElements.length; i++){
		if(scriptElements[i].innerHTML.match(/popunder|window.top|lastpass|h_ab_m|pop=document/)){
			//console.log("Match popunder");
			scriptElements[i].innerHTML="";
		}
		//console.log(scriptElements[i]);
	}
} else {
	console.log('referrer '+encodeURIComponent(document.referrer));
	var url = document.location.href;
	if(url.match(/nordvpn|mulheresgostosas|ablogica|prxio|qc-sys|reviewyourbestoffer|beyourxfriend|chaturbate|canuck-method/)){
		//console.log("Matched URL");
		var html = document.getElementsByTagName("html");
		html[0].innerHTML = "";
		
		//console.log(html);
	}else{
		//console.log("No URL Match");
	}
}

function getId(href){
	var n = href.lastIndexOf("/") + 1;
	return href.substring(n);
}

function isEmpty(str){
	return(!str || 0 === str.length);
}

function balch(){
	console.log("Balch");
	return '';
}