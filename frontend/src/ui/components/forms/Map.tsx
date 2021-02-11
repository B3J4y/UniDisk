import Grid from '@material-ui/core/Grid';

import { Feature, Map, Overlay, View } from 'ol';

// Start Openlayers imports
import { Point } from 'ol/geom';
import { Vector as VectorLayer, Tile as TileLayer } from 'ol/layer';
import { fromLonLat, transform, toLonLat } from 'ol/proj';
import { OSM as OSMSource, Vector as VectorSource } from 'ol/source';
import { Icon, Style } from 'ol/style';
import React from 'react';
import { GpsPosition } from './Location';
import './Map.css';

// End Openlayers imports

export type Marker = {
  id?: string;
  icon?: string;
} & GpsPosition;

export type MapProps = {
  onClick?: (value?: GpsPosition) => void;
  onHover?: (marker: Marker, position: { x: number; y: number }) => void;
  onHoverExit?: () => void;
  initialPosition: GpsPosition & { zoom?: number };
  markers?: Marker[];
  height?: number;
};

type State = {
  height?: number;
  markers: Marker[];
};
export class OLMap extends React.Component<MapProps, State> {
  private map: Map;
  private vectorLayer: VectorLayer;

  private mapId: string;
  constructor(props) {
    super(props);
    this.state = {
      markers: props.markers ?? [],
    };
    this.mapId = (new Date().getTime() * Math.random()).toString();
    this.updateDimensions = this.updateDimensions.bind(this);
  }
  updateDimensions() {
    const h = window.innerWidth >= 992 ? window.innerHeight : 400;
    this.setState({ height: this.props.height ?? h });
  }
  componentWillMount() {
    window.addEventListener('resize', this.updateDimensions);
    this.updateDimensions();
  }

  componentDidUpdate(oldProps: MapProps) {
    this.updateMarker(this.props.markers ?? []);
  }

  updateMarker(marker: Marker[]) {
    if (this.vectorLayer) {
      var features = this.vectorLayer.getSource().getFeatures();
      features.forEach((feature) => {
        this.vectorLayer.getSource().removeFeature(feature);
      });
    }
    const markers = marker.map((m) => {
      var newMarker = this.createMarker({ lat: m.lat, lng: m.lng }, m.id, m.icon);
      return newMarker;
    });
    var vectorSource = new VectorSource({
      features: markers,
    });
    this.vectorLayer = new VectorLayer({
      source: vectorSource,
    });
    this.map.addLayer(this.vectorLayer);
    this.map.updateSize();
  }

  createMarker(position, id, icon) {
    var newMarker = new Feature({
      geometry: new Point(fromLonLat([position.lng, position.lat])),
    });

    if (icon) {
      var iconStyle = new Style({
        image: new Icon({
          src: icon,

          scale: 0.8,
        }),
      });
      newMarker.setStyle(iconStyle);
    }
    newMarker.setId(id);
    return newMarker;
  }

  componentDidMount() {
    const that = this;
    const { initialPosition } = this.props;
    const { lng, lat } = initialPosition;

    try {
      const mapView = new View({
        center: fromLonLat([lng, lat]),
        zoom: initialPosition.zoom ?? 17.2,
      });
      const map = new Map({
        target: this.mapId,
        layers: [
          new TileLayer({
            source: new OSMSource(),
          }),
        ],
        view: mapView,
      });
      this.map = map;

      const markers = this.state.markers.map((m) => {
        var newMarker = this.createMarker({ lat: m.lat, lng: m.lng }, m.id, m.icon);
        return newMarker;
      });

      var vectorSource = new VectorSource({
        features: markers,
      });
      var markerVectorLayer = new VectorLayer({
        source: vectorSource,
      });

      this.vectorLayer = markerVectorLayer;

      var element = document.getElementById('popup');

      var popup = new Overlay({
        element: element,
        positioning: 'bottom-center',
        stopEvent: false,
        offset: [0, -10],
      });
      map.addOverlay(popup);
      map.addLayer(markerVectorLayer);

      if (that.props.onHover)
        map.on('pointermove', function (e) {
          if (e.dragging) {
            if (element) element.style.display = 'none';
            return;
          }
          var pixel = map.getEventPixel(e.originalEvent);
          var hit = map.hasFeatureAtPixel(pixel);
          var feature = map.forEachFeatureAtPixel(e.pixel, function (feature) {
            return feature;
          });
          if (feature) {
            const featureId = feature.getId();
            const hoveredMarker = that.state.markers.find((m) => m.id == featureId);
            const coordinate = feature.getGeometry().getCoordinates();
            const screenPosition = map.getPixelFromCoordinate(coordinate);

            const [x, y] = screenPosition;
            if (hoveredMarker) that.props.onHover!(hoveredMarker, { x, y });
          } else {
            if (that.props.onHoverExit) that.props.onHoverExit();
          }
          map.getTargetElement().style.cursor = hit ? 'pointer' : '';
        });
      map.updateSize();
      // map.on('moveend', function (e, x) {
      //   const [lon,lat] = toLonLat(map.getView().getCenter());
      //   const zoom = map.getView().getZoom();
      // });
      map.on('click', function (evt) {
        var feature = map.forEachFeatureAtPixel(evt.pixel, function (feature, layer) {
          //you can add a condition on layer to restrict the listener
          return feature;
        });

        const coord = transform(evt.coordinate, 'EPSG:3857', 'EPSG:4326');
        const lat = coord[1];
        const lng = coord[0];

        const marker = { lat: lat, lng: lng };
        if (feature) {
          // that.props.onMarkerSelected({ ...marker, id: feature.getId() });
        } else {
          if (that.props.onClick) that.props.onClick(marker);
        }
      });
    } catch (error) {
      console.error(error);
    }
  }
  componentWillUnmount() {
    window.removeEventListener('resize', this.updateDimensions);
  }
  render() {
    const style = {
      width: '100%',
      height: this.state.height,
      backgroundColor: '#cccccc',
    };
    return (
      <Grid container>
        <Grid item xs={12}>
          <div id={this.mapId} style={style}></div>
        </Grid>
      </Grid>
    );
  }
}
