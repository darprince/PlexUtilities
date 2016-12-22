if (document.title.indexOf("RARBG") != -1) {
	var allElements = document.getElementsByTagName('a');

	for(i = 0; i<allElements.length; i++){
		var href = allElements[i].getAttribute("href");
		if(!isEmpty(href)){
			if(href.includes("/torrent/")){
				console.log(href);
				allElements[i].style.color="#000000";
				var id = getId(href);
				var title = allElements[i].getAttribute("title");
				console.log(title);
				allElements[i].setAttribute("href", "/download.php?id="+id+"&f="+title+".torrent");
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
}
function getId(href){
	var n = href.lastIndexOf("/") + 1;
	return href.substring(n);
}

function isEmpty(str){
	return(!str || 0 === str.length);
}