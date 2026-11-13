package com.example.ui.components

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.viewinterop.AndroidView
import com.example.BuildConfig

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun MapboxWebViewScreen(
    latitude: Double = 37.7749,
    longitude: Double = -122.4194,
    zoom: Double = 15.0,
    modifier: Modifier = Modifier
) {
    val mapboxToken = try {
        BuildConfig.MAPBOX_ACCESS_TOKEN
    } catch (_: Exception) {
        ""
    }

    val htmlContent = """
        <!DOCTYPE html>
        <html>
        <head>
        <meta charset="utf-8" />
        <title>Flick Map - Mapbox 3D Satellite</title>
        <meta name="viewport" content="initial-scale=1,maximum-scale=1,user-scalable=no" />
        <link href="https://api.mapbox.com/mapbox-gl-js/v3.3.0/mapbox-gl.css" rel="stylesheet" />
        <script src="https://api.mapbox.com/mapbox-gl-js/v3.3.0/mapbox-gl.js"></script>
        <style>
            body { margin: 0; padding: 0; background: #0B0E14; }
            #map { position: absolute; top: 0; bottom: 0; width: 100%; }
        </style>
        </head>
        <body>
        <div id="map"></div>
        <script>
            mapboxgl.accessToken = '$mapboxToken';
            
            const map = new mapboxgl.Map({
                container: 'map',
                style: 'mapbox://styles/mapbox/satellite-streets-v12',
                center: [$longitude, $latitude],
                zoom: $zoom,
                pitch: 65,
                bearing: -20,
                antialias: true
            });

            // Add navigation controls (zoom, rotation, compass)
            map.addControl(new mapboxgl.NavigationControl(), 'top-right');
            map.addControl(new mapboxgl.FullscreenControl(), 'top-right');

            map.on('load', () => {
                // Add 3D terrain
                map.addSource('mapbox-dem', {
                    'type': 'raster-dem',
                    'url': 'mapbox://mapbox.mapbox-terrain-dem-v1',
                    'tileSize': 513,
                    'maxzoom': 14
                });
                map.setTerrain({ 'source': 'mapbox-dem', 'exaggeration': 1.5 });

                // Add 3D buildings layer
                const layers = map.getStyle().layers;
                let labelLayerId;
                for (let i = 0; i < layers.length; i++) {
                    if (layers[i].type === 'symbol' && layers[i].layout['text-field']) {
                        labelLayerId = layers[i].id;
                        break;
                    }
                }

                if (!map.getLayer('add-3d-buildings')) {
                    map.addLayer(
                        {
                            'id': 'add-3d-buildings',
                            'source': 'composite',
                            'source-layer': 'building',
                            'filter': ['==', 'extrude', 'true'],
                            'type': 'fill-extrusion',
                            'minzoom': 13,
                            'paint': {
                                'fill-extrusion-color': '#aaa',
                                'fill-extrusion-height': [
                                    'interpolate',
                                    ['linear'],
                                    ['zoom'],
                                    13, 0,
                                    13.05, ['get', 'height']
                                ],
                                'fill-extrusion-base': [
                                    'interpolate',
                                    ['linear'],
                                    ['zoom'],
                                    13, 0,
                                    13.05, ['get', 'min_height']
                                ],
                                'fill-extrusion-opacity': 0.85
                            }
                        },
                        labelLayerId
                    );
                }

                // Add marker for user location
                new mapboxgl.Marker({ color: '#00E5FF' })
                    .setLngLat([$longitude, $latitude])
                    .addTo(map);
            });
        </script>
        </body>
        </html>
    """.trimIndent()

    AndroidView(
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.loadWithOverviewMode = true
                settings.useWideViewPort = true
                webViewClient = WebViewClient()
                loadDataWithBaseURL("https://api.mapbox.com", htmlContent, "text/html", "UTF-8", null)
            }
        },
        modifier = modifier
            .fillMaxSize()
            .testTag("mapbox_webview")
    )
}
