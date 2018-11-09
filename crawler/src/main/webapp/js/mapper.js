/**
 * Created by dehne on 04.03.2016.
 */


PROJECTION_4326 = new OpenLayers.Projection("EPSG:4326");
PROJECTION_MERC = new OpenLayers.Projection("EPSG:900913");
var layer;
function addPointToLayer(lat, lon, title, descr, iconPath, akwVectorLayer) {
    var brokdorfPoint = new OpenLayers.Geometry.Point(lat, lon).transform(PROJECTION_4326, PROJECTION_MERC);
    var brokdorfAttributes = {title: title, description: descr};
    var stylesAkwImage = {
        externalGraphic: iconPath,
        graphicHeight: 37,
        graphicWidth: 32,
        graphicYOffset: -35,
        graphicXOffset: -16
    };
    var brokdorfFeature = new OpenLayers.Feature.Vector(brokdorfPoint, brokdorfAttributes, stylesAkwImage);
    akwVectorLayer.addFeatures(brokdorfFeature);
}

function convertDataToLayer(obj, layer) {
    var dataElements = obj.categoryList;
		for (var i = 0; i < dataElements.length; i++) {
			dataElements[i].coordinates.forEach(function (coordinate) {
					addPointToLayer(coordinate.latlon[1], coordinate.latlon[0], "", "", "icons/category" + (i + 1) + ".png", layer);
			});
    $("#category" + i + "Label").text(dataElements[i].variableName);
		}
}

function init() {
    map = new OpenLayers.Map("map", {
        controls: [new OpenLayers.Control.PanZoomBar(),
            new OpenLayers.Control.Navigation(),
            new OpenLayers.Control.LayerSwitcher(),
            new OpenLayers.Control.MousePosition(),
            new OpenLayers.Control.Attribution(),
            new OpenLayers.Control.OverviewMap()],
        maxExtent: new OpenLayers.Bounds(-20037508.34, -20037508.34, 20037508.34, 20037508.34),
        numZoomLevels: 18,
        maxResolution: 156543,
        units: 'm',
        projection: PROJECTION_MERC,
        displayProjection: PROJECTION_4326
    });

    // OSM Layer
    var osmLayer = new OpenLayers.Layer.OSM("OpenStreetMap");
    map.addLayer(osmLayer);

    var center = new OpenLayers.LonLat(8.807357, 53.075813);
    var centerAsMerc = center.transform(PROJECTION_4326, PROJECTION_MERC);
    //map.setCenter(centerAsMerc, 8);

    // marker
    layer = new OpenLayers.Layer.Vector("UniDiskurs Layer");
    map.addLayer(layer);
    //var lat =  9.344722;
    //var lon =  53.850833;
    //var iconPath = 'http://bilder.bild.de/fotos/lachsack-36229038/Bild/1.bild.jpg';
    //var title = "Lernen";
    //var descr = "cariable1";
    //addPointToLayer(lat, lon, title, descr, iconPath, layer);
}


function displayData(isFile, path, camp) {
	layer.removeAllFeatures();
	if (isFile) {
    $.ajax({
        url: path,
        type: "GET",
				data:{
					props: JSON.stringify(props["database"]),
				},					
				success: function (data) {
					resObj = validateData(data);
					convertDataToLayer(resObj, layer);
					map.zoomToExtent(layer.getDataExtent());
				}
		});
	} else {
    $.ajax(path, {
        type: "post",
				data: {
					props: JSON.stringify(props["database"]),
					loadPurpose: "loadVarSolrSum",
					campaign: camp
				},
				success: function (data) {
					resObj = validateData(data);
					convertDataToLayer(resObj, layer);
					map.zoomToExtent(layer.getDataExtent());
					fillMyLegend(resObj);
				}
		});
	}
}

function fillMyLegend(data) {
	if ($("#mapLegend").find("tr").length > 0 ) {
		$("#mapLegend").find("tr").remove();
	}
	for (var i = 0; i < data.categoryList.length; i++) {
		var td = "<tr><td><img src=\"icons/category" + (i + 1) + ".png\" /></td><td>" + data.categoryList[i].variable + "</td></tr>";
		$("#mapLegend").append(td);

	}
}

function validateData(data) {
	var resObj;
	if (data.length == 0) {
		$.error("No data input");
	}
	try {
		resObj = JSON.parse(data);
	} catch (err) {
		if (typeof data == "object") {
			resObj = data;
		} else {
			console.log(data);
			$.error("Data is unparsable");
		}
	}
	if (! resObj.hasOwnProperty("categoryList")) {
		//TODO
		var newRes = {};
		for (var i = 0; i < resObj.length; i++) {
			var temp = resObj[i];
			var varName = temp["Variable"];
			if (! newRes.hasOwnProperty(varName) ) {
				newRes[varName]= [];
			}
			newRes[varName].push({"latlon": [temp["Lat"], temp["Lon"]], "solr": temp["SolrSumme"]});
		}
		resObj = {"categoryList": []};
		for (prop in newRes) {
			resObj["categoryList"].push({"variable": prop, "coordinates":newRes[prop]});
		}
		console.log("has to form this object");
	}
	return resObj;

}
