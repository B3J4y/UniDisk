var varMetaElements = [];
function addJob(element) {
	if ($("#addJob").val().length == 0) {
		return;
	}
	//$("#jobOverview").append("<li><a href='#' >" + $('#jobName').val() + " <span class='badge'>0</span></a></li>");

		$.ajax("php/handleDb.php", {
			type:"post",
			data: {
				loadPurpose: "saveCampaign",
				props: JSON.stringify(props["database"]),
				Name: $('#addJob').val()
			},
			success: function (res) {
				console.log(res);
				initialLoad(0);
				//usableNavbar();
				
			},
			error: function(e) {
				console.log(e);
			}

		});
	$("#jobName").val("");
}

function addStichVar(element) {
	console.log(element);
	if (element.id == "addr0") {
		var stich = $("input[name='stich0']").val();
		var vari = $("input[name='var0']").val();

		varMetaElements.unshift({Stich: stich, Var: vari});
		console.log(varMetaElements);

		//save this element
		$.ajax("php/handleDb.php", {
			type:"post",
			data: {
				loadPurpose: "saveStichVarMeta",
				stich: stich,
				props: JSON.stringify(props["database"]),
				vari: vari,
				campaign: $("#addr0").attr("name")
			},
			success: function (res) {
				console.log(res);
				loadAndLock("#" + active);
				
			},
			error: function(e) {
				console.log(e);
			}

		});
		
		//write list to table
		elementsToTab();
		$("input[name='stich0']").val("");
		$("input[name='var0']").val("");
	}
}

function buttonGroupInteract(element) {
	if ($("#stichBtn").find(".active")[0] != element[0]) {
		$("#stichBtn").find(".active").removeClass("active");
		element.addClass("active");
		var g = $("#stichBtn").find(".active")[0].innerHTML;
		if (g == "Url" ) {
			g = "id"
		}
		$.ajax("php/handleDb.php", {
			type:"post",
			data: {
				loadPurpose: "loadScoreStich",
				props: JSON.stringify(props["database"]),
				campaign: $(".nav .active").children()[0].id,
				group: g
			},
			invokedata: {
				group: g
			},
			success: function (res) {
				scoreStichToTab(res, this.invokedata.group);
				console.log(res);
			},
			error: function(e) {
				console.log(e);
			}
		});

	}
	console.log(element);
}

function elementsToTab() {
	$("#varMetaTable").find("tr:gt(1)").remove();
	for (var i = 0; i < varMetaElements.length; i++) {
		$("#varMetaTable").append(
				"<tr><td>" + varMetaElements[i].Id + "</td><td>" + varMetaElements[i].Stich + "</td><td>" + varMetaElements[i].Var + "</td>"
				+ "<td><button class=\"btn btn-danger\" onclick=\"removeElement($(this).parent().parent())\" >"
				+ "<span class=\"glyphicon glyphicon-remove\" aria-hidden=\"true\" /> </button></td></tr>");
	}
	
}
