/**
 * Created by dehne on 04.03.2016.
 */


PROJECTION_4326 = new OpenLayers.Projection("EPSG:4326");
PROJECTION_MERC = new OpenLayers.Projection("EPSG:900913");

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
    var dataElements = obj.categoryList
    dataElements[0].coordinates.forEach(function (coordinate) {
        addPointToLayer(coordinate.latlon[1], coordinate.latlon[0], "", "", "icons/category1.png", layer)
    });
    $("#category1Label").text(dataElements[0].variableName);

    dataElements[1].coordinates.forEach(function (coordinate) {
        addPointToLayer(coordinate.latlon[1], coordinate.latlon[0], "", "", "icons/category2.png", layer)
    });
    $("#category2Label").text(dataElements[1].variableName);

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
    var layer = new OpenLayers.Layer.Vector("UniDiskurs Layer");
    map.addLayer(layer);


    $.ajax({
        url: "data.json",
        type: "GET",
        success: function (data) {
            convertDataToLayer(data, layer)
            map.zoomToExtent(layer.getDataExtent())
        }
    });


    //var lat =  9.344722;
    //var lon =  53.850833;
    //var iconPath = 'http://bilder.bild.de/fotos/lachsack-36229038/Bild/1.bild.jpg';
    //var title = "Lernen";
    //var descr = "cariable1";
    //addPointToLayer(lat, lon, title, descr, iconPath, layer);
}

