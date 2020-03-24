
const mapIconScale = 0.2;
const initialLatitude = 51.4553834;
const initialLongitude = 10.0933013;
const initialZoom = 6.1;
const toggleMapViewThreshhold = 6;

const minScore = 0;
var maxScore = 0;
var defaultLayer;
var heatmapLayer;

var currentZoom = initialZoom;

function max(values) {
    var currentMax = values[0];
    for (value in values) {
        if (value > currentMax) currentMax = value;
    }
    return currentMax;
}

function getRndInteger(min, max) {
    return Math.floor(Math.random() * (max - min)) + min;
}

function generateRandom(count, maxDist, layers) {
    const testCount = 30;

    const features = [];

    for (var i = 0; i < count; i++) {
        //            [0,1] -> [-1,1]
        const dLat = (1 - 2 * Math.random()) * maxDist;
        const dLong = (1 - 2 * Math.random()) * maxDist;
        const lon = initialLongitude + dLong;
        const lat = initialLatitude + dLat;
        const score = getRndInteger(0, 100);
        const feature = createFeature(lon, lat, score);

        layers.forEach(layer => layer.getSource().addFeature(feature));
        features.push(feature);
    }
    const scores = features.map(f => parseInt(f.get("score")));
    maxScore = max(scores);
}

function createFeature(lon, lat, score) {
    const feature = new ol.Feature({
        geometry: new ol.geom.Point(ol.proj.fromLonLat([lon, lat]))
    });
    feature.setProperties({ score: score.toString() });
    return feature;
}

function displayHeatMap() {
    defaultLayer.setVisible(false);
    heatmapLayer.setVisible(true);
}

function displayDefaultMap() {
    defaultLayer.setVisible(true);
    heatmapLayer.setVisible(false);
}

let styleFunction = function(feature, resolution) {
    return new ol.style.Style({
        text: new ol.style.Text({
            text: feature.get("score")
        })
    });
};


function refreshMap(jsonMarker) {
    console.log(jsonMarker);
    const marker = jsonMarker;

    const features = marker.map(m => {
        const lat = m.university.lat;
        const lng = m.university.lng;

        const score = m.score;

        const feature = createFeature(lng, lat, score);
        return feature;
    });

    const scores = marker.map(m => m.score);
    maxScore = max(scores);
    console.log(features);

    var vectorSource = new ol.source.Vector({
        features: features
    });
    vectorLayer = new ol.layer.Vector({
        source: vectorSource
    });

    [heatmapLayer, defaultLayer].forEach(layer => {
        const layerSource = layer.getSource();

    const oldFeatures = layerSource.getFeatures();
    oldFeatures.forEach(feature => layerSource.removeFeature(feature));
    features.forEach(f => layerSource.addFeature(f));
});
}

var PROJECTION_4326 = new ol.proj.Projection("EPSG:4326");
var PROJECTION_MERC = new ol.proj.Projection("EPSG:900913");

var map = new ol.Map({
    target: "map",
    numZoomLevels: 18,
    maxResolution: 156543,
    units: "m",
    projection: PROJECTION_MERC,
    displayProjection: PROJECTION_4326,
    controls: [
        new ol.control.Zoom(),
        new ol.control.MousePosition(),
        new ol.control.Attribution(),
        new ol.control.OverviewMap()
    ],
    layers: [
        new ol.layer.Tile({
            source: new ol.source.OSM()
        })
    ],
    view: new ol.View({
        center: ol.proj.fromLonLat([initialLongitude, initialLatitude]),
        zoom: initialZoom
    })
});
var vectorSource = new ol.source.Vector({
    features: []
});

defaultLayer = new ol.layer.Vector({
    source: vectorSource,
    style: styleFunction
});

heatmapLayer = new ol.layer.Heatmap({
    source: new ol.source.Vector({
        features: []
    }),
    blur: 12,
    radius: 15,
    weight: function(feature) {
        //value in range [0,1]
        return parseInt(feature.get("score")) / maxScore;
    }
});

map.addLayer(defaultLayer);
map.addLayer(heatmapLayer);
heatmapLayer.setVisible(false);

//generateRandom(100, 2.5 * 1.3, [defaultLayer, heatmapLayer]);

map.on("moveend", function(e) {
    var newZoom = map.getView().getZoom();

    if (newZoom <= toggleMapViewThreshhold) {
        displayDefaultMap();
    } else {
        displayHeatMap();
    }
    console.log("zoom end, new zoom: " + newZoom);
    currentZoom = newZoom;
});

