var LOADDATA = 1
var loaded = 0
var domain = "http://localhost:8084/crawler/start"

var active = "";
var firstTime = 0;
var campaigns = {};
var props = {};
var processIcons = [
	"glyphicon-leaf",
	"glyphicon-plane",
	"glyphicon-ok",
	"glyphicon-fire",
	"glyphicon-scissors",
	];
var colorPicker = [
{back: "#0000FF", front: "#FFFFFF"},
{back: "#FFFF00", front: "#000000"},
{back: "#00FF40", front: "#000000"},
{back: "#FF0000", front: "#FFFFFF"},
{back: "#FFFF00", front: "#000000"},
	];

function loadJsonFile(filename) {
	$.ajax(filename, {
		type:"get",
		success: function(res) {
			if (typeof res == "object") {
				props = res;
			} else {
				props = JSON.parse(res);
			}
			initialLoad(1);
		},
	});
}

function initialLoad(pollIt) {
	$("#overviewError").hide();
		$.ajax("php/handleDb.php", {
			type:"post",
			data: {
				loadPurpose: "overview",
				props: JSON.stringify(props["database"]),

			},
			success: function (res) {
				if (res.length > 0) {
					campaignJsonToObject(res);
					var lastLi = $("#jobOverview").find("li").last();
					var isFocus = false;
					if (lastLi.find("input").length > 0) {
						isFocus = lastLi.find("input").is(":focus");
					}
					$("#jobOverview").find("li").remove();
					var r = active.length;
					for (prop in campaigns) {
						if (r == 0) {
							active = prop;
						}
						r ++;
						$("#jobOverview").append(campaigns[prop].getListElement());
					}

					
				} else {
					console.log("No list elements found");
				}
				if (lastLi.length == 0)  {
					$("#jobOverview").append("<li><div class=\"form-group\"><div class=\"input-group\">"
						+ "<input id=\"addJob\" type=\"text\"  class=\"form-control\" placeholder=\"Neues Projekt\">"
						+ "<span class=\"input-group-btn\"><button class=\"btn btn-default btn-success\" onclick=\"addJob(this)\">"
						+ "<span class=\"glyphicon glyphicon-plus\" aria-hidden=\"true\"></span>"
						+ "</button></span></div></div></li>");
				} else {
					$("#jobOverview").append(lastLi);
					if (isFocus) {
						$(lastLi.find("input")[0]).get(0).focus();
					}
				}
				usableNavbar();
				readyLoaded();
				if (pollIt) {
					setTimeout(function() {initialLoad(1)}, 10000);
					//poll();
				}
			},
			error: function(e) {
				handleError(e);
				console.log(e);
			}
		});
}
function poll() {
	setTimeout(function() {
		$.ajax("php/handleDb.php", {
			type:"post",
			data: {
				props: JSON.stringify(props["database"]),
				loadPurpose: "overview",
			},
			success: function (res) {
				console.log("back");
				if (res.length > 0) {
					campaignJsonToObject(res);
					$("#jobOverview").find("li").remove();
					for (prop in campaigns) {
						$("#jobOverview").append(campaigns[prop].getListElement());
					}
				} else {
					console.log("No list elements found");
				}
				usableNavbar();
				poll();
			},
			error: function(e) {
				handleError(e);
				console.log(e);
				poll();
			}
		});

	}, 10000); 
}

function campaignJsonToObject(json) {
	overviewObj = JSON.parse(json.split("#")[0]); 
	countObj = JSON.parse(json.split("#")[1]);
	if ( (countObj == null) || (overviewObj == null) ) {
		$("#overviewError").show();
		return;	
	} else {
		$("#overviewError").hide();
	}
	var myCount = {}
	for (var t = 0; t < countObj.length; t++) {
		nameSplit = countObj[t].TABLE_NAME.split("_");
		if (nameSplit.length == 2) {
			if (typeof myCount[nameSplit[0]] != "object") {
				myCount[nameSplit[0]] = {}
			}
			myCount[nameSplit[0]][nameSplit[1]] = countObj[t].TABLE_ROWS;

		} else if (nameSplit.length > 2) {
			var newName = "";
			for (var j = 0; j < nameSplit.length - 1; j++) {
				newName += nameSplit[j];
				if (j < nameSplit.length - 2) {
					newName += "_";
				}
				if (typeof myCount[newName] != "object") {
					myCount[newName] = {}
				}
				myCount[newName][nameSplit[nameSplit.length - 1]] = countObj[t].TABLE_ROWS;
			}
		}
	}
	console.log(myCount);
	for (var i = 0; i < overviewObj.length; i++) {
		var stichCount = "0";
		scoreStich = "0";
		varMeta = "0";
		myCamp = new Campaign(overviewObj[i].Name);
		if (myCount[overviewObj[i].Name] != null) {
			myCamp.stich = myCount[overviewObj[i].Name].Stichwort;
			if (myCount[overviewObj[i].Name].ScoreStich != null) {
				myCamp.scoreStich = myCount[overviewObj[i].Name].ScoreStich;
				myCamp.scoreMeta = myCount[overviewObj[i].Name].varMeta;
			}
		}
		myCamp.status = overviewObj[i].Status;
		if ( (listElementCamp = campaigns[overviewObj[i].Name]) == null) {
			campaigns[myCamp.name] = myCamp;
		} else {
			if (! myCamp.compare(listElementCamp)) {
				listElementCamp.assimilate(myCamp);
			}
		}
	}
}

function readyLoaded() {
	loaded++;
	if (loaded == LOADDATA) {
		console.log("Loaded");
		elementsToTab();
		prepareAndLoadCamp("#" + active);
		//usableNavbar();
		$("#pluswrap").hide();
	}
}

function usableNavbar() {
	

	$(".nav a").on("click", function(){
		if (this.parentNode != $(".nav").find(".active")[0]) {
			$(".nav").find(".active").removeClass("active");
			prepareAndLoadCamp(this);
			$(this).parent().addClass("active");
			active = this.id;
		} 
	});	
	if (active != "" ) {
		$("#" + active).parent().addClass("active");
	}
}

function prepareAndLoadCamp(element) {
			$("#pluswrap").show();
			$("#addr0").attr("name", $(element).attr("id"));
			$("#accordion").css("display", "");
			loadCampaign(element);
}

function loadAndLock(element) {
		$("#pluswrap").show();
		loadCampaign(element);
		$("#pluswrap").hide();
}

function loadCampaign(element) {
	var elemStatus = processIcons.indexOf($(element).find(".glyphicon").attr("class").split(" ")[1])
		if (elemStatus == 1) {
			$("#fireButton").hide();
			$("#cancelButton").show();
		} else if (elemStatus == 4) {
			$("#fireButton").hide();
			$("#cancelButton").hide();
		} else {
			$("#cancelButton").hide();
			$("#fireButton").show();
		}
		$.ajax("php/handleDb.php", {
			type:"post",
			data: {
				props: JSON.stringify(props["database"]),
				loadPurpose: "loadStichVarMeta",
				campaign: $(element).attr("id"),
				groupStich: "Stichwort",
				groupVar: "Variable"
			},
			invokedata: {
				groupStich: "Stichwort",
				groupVar: "Variable",
			},
			success: function (res) {
				varMetaElements = [];
				var stichworte = res.split("#")[0];
				var scoreStich = res.split("#")[1];
				var scoreVar = res.split("#")[2];
				if ((stichworte.length > 0) && (stichworte != "\n")) {
					var loadedCamp = JSON.parse(stichworte); 
					for (var i = 0; i < loadedCamp.length; i++) {
						varMetaElements.push({Id: loadedCamp[i].Id, Stich: loadedCamp[i].Stichwort, Var: loadedCamp[i].Variable});
					}
				}
				scoreStichToTab(scoreStich, this.invokedata.groupStich);
				scoreVarToTab(scoreVar, this.invokedata.groupVar);
				elementsToTab();
				$("#pluswrap").hide();
			},
			error: function(e) {
				handleError(e);
				console.log(e);
				$("#pluswrap").hide();
			}
		});
	
	if ($(element).find("#scoreStich")[0].innerHTML == "0" ) {
		$("#resTables").hide();
		$("#mapTab").hide();
	} else {
		$("#resTables").show();
		$("#mapTab").show();
		displayData(0, "php/handleDb.php", $(element).attr("id"));
	}
		
	console.log(element);
}
function scoreVarToTab(scoreVar, group) {
	if ($("#scoreVarTab").find("tr:gt(0)").length) {
		$("#scoreVarTab").find("tr:gt(0)").remove();
	}

	if (scoreVar.length > 0) {
		var loadedScoreVar = JSON.parse(scoreVar);
		for (var t = 0; t < loadedScoreVar.length; t++) {
			$("#scoreVarTab").append("<tr><td>" + (t + 1 ) + "</td><td>" + loadedScoreVar[t][group] + "</td><td>" + loadedScoreVar[t]["Score"] + "</td><td>" + loadedScoreVar[t]["Count"] + "</tr>");

		}
	}
}

function scoreStichToTab(scoreStich, group) {
	$("#scoreStichTab").find("tr:gt(0)").remove();

	if (scoreStich.length > 0) {
		var loadedScoreStich = JSON.parse(scoreStich);
		for (var t = 0; t < loadedScoreStich.length; t++) {
			$("#scoreStichTab").append("<tr><td>" + (t + 1 ) + "</td><td>" + loadedScoreStich[t][group] + "</td><td>" + loadedScoreStich[t]["Score"] + "</td><td>" + loadedScoreStich[t]["Count"] + "</tr>");

		}
	}
}

function fireInTheHole() {
	$("#fireButton").hide();
	$("#cancelButton").show();
	$.ajax(props["endpoints"]["crawler"] + "crawler/start?campaign=" + active, {
		type:"post",
		contentType: 'application/json',
		success: function (res) {
			console.log(res);
		},
		error: function (res, status, error) {
			console.log(res);
			if (res.status != 200) {
				handleError(res);
				$("#fireButton").show();
				$("#cancelButton").hide();
			}
		},
	});
}

function handleError(res) {
		switch (res.status) {
			case 0:
				alert("Kein Server kann angesprochen werden. Fehlercode:" + res.status);
				break;
			case 404:
				alert("Der Server ist gerade nicht erreichbar oder es liegt keine Internetverbindung an. Hoffentlich ändert sich das bald. Fehlercode:" + res.status);
				break;
			case 503:
				alert("Der Service ist auf dem Server nicht verfügbar. Da wurde also irgendwas im Backend verändert. Fehlercode:" + res.status);
				break;
			default:
				alert("Unerklärlicher Fehler. Sicher, dass der Server der Schuldige ist? Fehlercode:" + res.status);
				break;
		}

}
function removeElement(element) {
	$.ajax("php/handleDb.php", {
			type:"post",
			data: {
				props: JSON.stringify(props["database"]),
				loadPurpose: "deleteElement",
				element: element.children()[0].innerHTML,
				campaign: active
			},
			success:function (res) {
				console.log(res);
				loadAndLock("#" + active);
			}
	});
}

function cancelTheThing() {
	$("#cancelButton").hide();
	$.ajax(props["endpoints"]["crawler"] + "crawler/stop?campaign=" + active, {
		type:"post",
		contentType: 'application/json',
		success: function (res) {
			console.log(res);
		},
		error: function (res) {
			handleError(res);
			console.log(res);
			$("#cancelButton").show();
		},
	});
}
