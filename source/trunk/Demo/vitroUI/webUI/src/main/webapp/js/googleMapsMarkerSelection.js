// global constants
var radiansPerDegree = Math.PI / 180.0;
var degreesPerRadian = 180.0 / Math.PI;
var earthRadiusMeters = 6367460.0;
var metersPerDegree = 2.0 * Math.PI * earthRadiusMeters / 360.0;
var metersPerKm = 1000.0;
var meters2PerHectare = 10000.0;
var feetPerMeter = 3.2808399;
var feetPerMile = 5280.0;
var acresPerMile2 = 640;

var MarkerSelection = {
    points: [],                     // points are the latlong points of the selection area
    allnodeMarkers: [],                 // replacing pointsrand are the markers representing the nodes/sensors/items to be selected (<-- should be merged with the markers of the AllMapItems object)
    selectedMarkers: [],              // is a table with the selected markers (in the defined polygon region)!
    singleMarkerSelected: null,     // is changed when a user right clicks or double clicks on a marker.
    routeMarkers: [],               // the markers of the selection borderline - not valid nodes/sensors etc
    lines: [],                       // unused?
    lineColor:'#6E6E98' ,//'#ff0000';
    fillColor:'#00FF00',
    lineWidth: 4,
    polygon: null,
    routePath: null,                    //????
    routePath2: null, //???             //????
    ShowHideONOFF: 0,
    selectedCapability: 'invalid',
    areaSpan: null,   // to show the m^2 of the selected surface
    areaSpanKm: null, // to show the km^2 of the selected surface
    selectedMarkersSpan: null
};

//
//var points = [];
//var pointsrand = [];
////var areaDiv = document.getElementById('area');
////var areaDivkm = document.getElementById('areakm');
//var randomMarkers = new Array(0);
//var routeMarkers = new Array(0);
//var lines = [];
//var lineColor = '#6E6E98' ;//'#ff0000';
//var fillColor = '#00FF00';
//var lineWidth = 4;
//var polygon;
//var routePath;
//var routePath2;
//var ShowHideONOFF = 0;

MarkerSelection.SwitchSelectAll = function(pMap) {
    if (MarkerSelection.selectedMarkers && MarkerSelection.selectedMarkers.length > 0) {
        MarkerSelection.Clean(pMap);
    }
    else {
        // TODO: code repetition (with searchPointsAdd()). Should be optimized
        MarkerSelection.selectedMarkers.length = 0;
        MarkerSelection.selectedMarkersSpan.innerHTML = '';
        for (var i = 0; i < MarkerSelection.allnodeMarkers.length; ++i) {
            var marker = MarkerSelection.allnodeMarkers[i];  // todo: maybe something more efficient than erasing the entire table and rewriting it?
            if (!(marker.getMap() == null)) // we are interested only on the displayed markers!
            {
                MarkerSelection.selectedMarkers.push(marker);
                drawSelectedIcon(marker, MarkerSelection.selectedCapability);
                if (MarkerSelection.selectedMarkers.length <= 5)
                    MarkerSelection.selectedMarkersSpan.innerHTML += marker.getTitle() + ':' + MarkerSelection.selectedCapability + ', ';
            }
        }
        var strTemp = MarkerSelection.selectedMarkersSpan.innerHTML;
        MarkerSelection.selectedMarkersSpan.innerHTML = strTemp.replace(/(,\s*$)/g, '');
        if (MarkerSelection.selectedMarkers.length > 5)
            MarkerSelection.selectedMarkersSpan.innerHTML += ' ...and&nbsp;' + (MarkerSelection.selectedMarkers.length - 5).toString() + '&nbsp;more.'

    }
}


MarkerSelection.Clean = function(pMap) {
    MarkerSelection.areaSpan.innerHTML = '0 m&sup2;';
    MarkerSelection.areaSpanKm.innerHTML = '0 km&sup2;';
    MarkerSelection.selectedMarkersSpan.innerHTML = '';

    if (MarkerSelection.selectedMarkers) {
        for (var i = 0; i < MarkerSelection.selectedMarkers.length; ++i) {
            //MarkerSelection.selectedMarkers[i].setMap(null)
            drawDefaultUnselectedIcon(MarkerSelection.selectedMarkers[i], MarkerSelection.selectedCapability)
        }
    }
    if (MarkerSelection.routeMarkers) {
        for (var i = 0; i < MarkerSelection.routeMarkers.length; ++i) {
            MarkerSelection.routeMarkers[i].setMap(null)
        }
    }
    if (! (MarkerSelection.routePath == undefined)) {
        MarkerSelection.routePath.setMap(null)
    }
    if (! (MarkerSelection.routePath2 == undefined)) {
        MarkerSelection.routePath2.setMap(null)
    }
    if (! (MarkerSelection.polygon == undefined)) {
        MarkerSelection.polygon.setMap(null)
    }

    if (! (MarkerSelection.singleMarkerSelected == undefined)) {
        drawDefaultUnselectedIcon(MarkerSelection.singleMarkerSelected, MarkerSelection.selectedCapability);
        MarkerSelection.singleMarkerSelected = null;
    }

    MarkerSelection.routeMarkers.length = 0;
    MarkerSelection.routeMarkers = [];
    MarkerSelection.routePath = null;
    MarkerSelection.routePath2 = null;
    MarkerSelection.polygon = null;
    MarkerSelection.points.length = 0;
    MarkerSelection.points = []
    MarkerSelection.selectedMarkers.length = 0;
    MarkerSelection.selectedMarkers = [];
}
//function initialize() {
//	var latlng = new google.maps.LatLng(34.12016327332972, -118.0456525824976);
//	var myOptions = {
//		zoom: 16,
//		center: latlng,
//		mapTypeId: google.maps.MapTypeId.HYBRID,
//		draggableCursor: 'crosshair',
//		mapTypeControlOptions: {
//			style: google.maps.MapTypeControlStyle.DROPDOWN_MENU
//		}
//	};
//
//	map = new google.maps.Map(document.getElementById("map_canvas"), myOptions);
//	google.maps.event.addListener(map, 'click', mapclick);
//	areaDiv.innerHTML = '0 m&sup2;';
//	areaDivkm.innerHTML = '0 km&sup2;';
//	Display();
//	setTimeout('Regen()', 200)
//}


//function Regen() {
//	var bounds = map.getBounds();
//	var southWest = bounds.getSouthWest();
//	var northEast = bounds.getNorthEast();
//	var lngSpan = northEast.lng() - southWest.lng();
//	var latSpan = northEast.lat() - southWest.lat();
//	pointsrand = [];
//	for (var i = 0; i < 100; i++) {
//		var point = new google.maps.LatLng(southWest.lat() + latSpan * Math.random(), southWest.lng() + lngSpan * Math.random());
//		pointsrand.push(point)
//	}
//}

//function mapclick(event) {
//	points.push(event.latLng);
//	ShowHideONOFF = 0;
//	Display()
//}

/**
 * if there is a selection polygon defined this function will push in the MarkerSelection.selectedMarkers table, the markers with pointsrand[] belonging to the polygon.
 * @param pMap
 * @constructor
 */
function SearchPointsAdd(pMap) {
    if (! (MarkerSelection.polygon == undefined)) {
        if (MarkerSelection.selectedMarkers) {
            for (var i = 0; i < MarkerSelection.selectedMarkers.length; ++i) {
                drawDefaultUnselectedIcon(MarkerSelection.selectedMarkers[i], MarkerSelection.selectedCapability);
                //MarkerSelection.selectedMarkers[i].setMap(null)            // we hide the existing, but won't delete them (?)
            }
        }
        //MarkerSelection.selectedMarkers = new Array();
        MarkerSelection.selectedMarkers.length = 0;
        MarkerSelection.selectedMarkersSpan.innerHTML = '';
        for (var i = 0; i < MarkerSelection.allnodeMarkers.length; ++i) {
            if (MarkerSelection.points.length > 2 && MarkerSelection.polygon.containsLatLng(MarkerSelection.allnodeMarkers[i].getPosition())) {
                //var marker = placeMarkerred(MarkerSelection.pointsrand[i], i, pMap);      // this creates a NEW marker. We don't want that!
                //var marker =
                //if(doesNotContain(MarkerSelection.selectedMarkers, MarkerSelection.allnodeMarkers[i]))
                var marker = MarkerSelection.allnodeMarkers[i];  // todo: maybe something more efficient than erasing the entire table and rewriting it?
                if (!(marker.getMap() == null)) // we are interested only on the displayed markers!
                {
                    MarkerSelection.selectedMarkers.push(marker);
                    drawSelectedIcon(marker, MarkerSelection.selectedCapability);
                    if (MarkerSelection.selectedMarkers.length <= 5)
                        MarkerSelection.selectedMarkersSpan.innerHTML += marker.getTitle() + ':' + MarkerSelection.selectedCapability + ', ';
                }

                //marker.setMap(pMap)
            }
        }
        var strTemp = MarkerSelection.selectedMarkersSpan.innerHTML;
        MarkerSelection.selectedMarkersSpan.innerHTML = strTemp.replace(/(,\s*$)/g, '');
        if (MarkerSelection.selectedMarkers.length > 5)
            MarkerSelection.selectedMarkersSpan.innerHTML += ' ...and&nbsp;' + (MarkerSelection.selectedMarkers.length - 5).toString() + '&nbsp;more.'

    }
    else {
        MarkerSelection.selectedMarkers.length = 0;
        MarkerSelection.selectedMarkersSpan.innerHTML = '';
    }
}

MarkerSelection.Display = function(pMap) {
    if (MarkerSelection.routeMarkers) {
        //for (i in MarkerSelection.routeMarkers) {
        for (var i = 0; i < MarkerSelection.routeMarkers.length; ++i) {
            MarkerSelection.routeMarkers[i].setMap(null)
        }
    }
    if (! (MarkerSelection.routePath == undefined)) {
        MarkerSelection.routePath.setMap(null)
    }
    if (! (MarkerSelection.routePath2 == undefined)) {
        MarkerSelection.routePath2.setMap(null)
    }
    if (! (MarkerSelection.polygon == undefined)) {
        MarkerSelection.polygon.setMap(null)
    }

    MarkerSelection.routePath = new google.maps.Polyline({
        path: MarkerSelection.points,
        strokeColor: MarkerSelection.lineColor,
        strokeOpacity: 1.0,
        strokeWeight: MarkerSelection.lineWidth,
        geodesic: true
    });
    MarkerSelection.routePath.setMap(pMap);
    if (MarkerSelection.points.length > 2) {
        var points2 = [MarkerSelection.points[0], MarkerSelection.points[MarkerSelection.points.length - 1]];
        MarkerSelection.routePath2 = new google.maps.Polyline({
            path: points2,
            strokeColor: MarkerSelection.lineColor,
            strokeOpacity: 1.0,
            strokeWeight: MarkerSelection.lineWidth,
            geodesic: true
        });
        MarkerSelection.routePath2.setMap(pMap);
        MarkerSelection.polygon = new google.maps.Polygon({
            paths: MarkerSelection.points,
            strokeColor: "#FF0000",
            strokeOpacity: 1,
            strokeWeight: 1,
            fillColor: MarkerSelection.fillColor,
            fillOpacity: 0.5
        });
        MarkerSelection.polygon.setMap(pMap);
        MarkerSelection.areaSpan.innerHTML = '&nbsp;';
        MarkerSelection.areaSpanKm.innerHTML = '&nbsp;';
        var areaMeters2 = SphericalPolygonAreaMeters2(MarkerSelection.points);
        if (areaMeters2 < 1000000.0) areaMeters2 = PlanarPolygonAreaMeters2(MarkerSelection.points);
        MarkerSelection.areaSpan.innerHTML = Areas(areaMeters2);
        MarkerSelection.areaSpanKm.innerHTML = Areaskm(areaMeters2)
    }
    MarkerSelection.lines = [];
    MarkerSelection.routeMarkers = new Array(0);
    for (var i = 0; i < MarkerSelection.points.length; ++i) {
        var marker = placeMarker(MarkerSelection.points[i], i, pMap);
        MarkerSelection.routeMarkers.push(marker);
        marker.setMap(pMap)
    }
    SearchPointsAdd(pMap)
}


MarkerSelection.DeleteLastPoint = function(pMap) {
    if (MarkerSelection.points.length > 0) MarkerSelection.points.length--;
    MarkerSelection.Display(pMap)
}


function placeMarker(location, number, pMap) {
    var image = new google.maps.MarkerImage('img/marker004c-icon32.png', new google.maps.Size(20, 34), new google.maps.Point(0, 0), new google.maps.Point(9, 33));
    var shadow = new google.maps.MarkerImage('img/markershadow004-icon32.png', new google.maps.Size(28, 22), new google.maps.Point(0, 0), new google.maps.Point(1, 22));
    var marker = new google.maps.Marker({
        position: location,
        map: pMap,
        shadow: shadow,
        icon: image,
        draggable: true
    });
    google.maps.event.addListener(marker, 'dragend', function (event) {
        MarkerSelection.points[number] = event.latLng;
        MarkerSelection.Display(pMap)
    });
    return marker
}


function placeMarkerred(location, number, pMap) {
    var image = new google.maps.MarkerImage('img/marker004c-icon32.png', new google.maps.Size(20, 34), new google.maps.Point(0, 0), new google.maps.Point(9, 33));
    var shadow = new google.maps.MarkerImage('img/markershadow-icon32.png', new google.maps.Size(28, 22), new google.maps.Point(0, 0), new google.maps.Point(1, 22));
    var marker = new google.maps.Marker({
        position: location,
        map: pMap,
        shadow: shadow,
        icon: image,
        draggable: false
    });
//	google.maps.event.addListener(marker, 'dragend', function (event) {
//        MarkerSelection.points[number] = event.latLng;
//		Display(pMap)
//	});
    return marker
}


function GreatCirclePoints(p1, p2) {
    var maxDistanceMeters = 200000.0;
    var ps = [];
    if (p1.distanceFrom(p2) <= maxDistanceMeters) {
        ps.push(p1);
        ps.push(p2)
    } else {
        var theta1 = p1.lng() * radiansPerDegree;
        var phi1 = (90.0 - p1.lat()) * radiansPerDegree;
        var x1 = earthRadiusMeters * Math.cos(theta1) * Math.sin(phi1);
        var y1 = earthRadiusMeters * Math.sin(theta1) * Math.sin(phi1);
        var z1 = earthRadiusMeters * Math.cos(phi1);
        var theta2 = p2.lng() * radiansPerDegree;
        var phi2 = (90.0 - p2.lat()) * radiansPerDegree;
        var x2 = earthRadiusMeters * Math.cos(theta2) * Math.sin(phi2);
        var y2 = earthRadiusMeters * Math.sin(theta2) * Math.sin(phi2);
        var z2 = earthRadiusMeters * Math.cos(phi2);
        var x3 = (x1 + x2) / 2.0;
        var y3 = (y1 + y2) / 2.0;
        var z3 = (z1 + z2) / 2.0;
        var r3 = Math.sqrt(x3 * x3 + y3 * y3 + z3 * z3);
        var theta3 = Math.atan2(y3, x3);
        var phi3 = Math.acos(z3 / r3);
        var p3 = new GLatLng(90.0 - phi3 * degreesPerRadian, theta3 * degreesPerRadian);
        var s1 = GreatCirclePoints(p1, p3);
        var s2 = GreatCirclePoints(p3, p2);
        for (var i = 0; i < s1.length; ++i) ps.push(s1[i]);
        for (var i = 1; i < s2.length; ++i) ps.push(s2[i])
    }
    return ps
}


function ShowHide(pMap) {
    if (MarkerSelection.ShowHideONOFF == 0) {
        MarkerSelection.ShowHideONOFF = 1;
        for (var i = 0; i < MarkerSelection.allnodeMarkers.length; ++i) {
            //var marker = placeMarkerred(MarkerSelection.pointsrand[i], i, pMap); // TODO: replace this inefficient code if we use this method to clear the map.
            MarkerSelection.selectedMarkers.push(marker);
            marker.setMap(pMap)
        }
    } else {
        MarkerSelection.ShowHideONOFF = 0;
        for (var i = 0; i < MarkerSelection.allnodeMarkers.length; ++i) {
            MarkerSelection.selectedMarkers[i].setMap(null)
        }
        MarkerSelection.Display(pMap)
    }
}


function Areas(areaMeters2) {
    var areaHectares = areaMeters2 / meters2PerHectare;
    var areaKm2 = areaMeters2 / metersPerKm / metersPerKm;
    var areaFeet2 = areaMeters2 * feetPerMeter * feetPerMeter;
    var areaMiles2 = areaFeet2 / feetPerMile / feetPerMile;
    var areaAcres = areaMiles2 * acresPerMile2;
    return areaMeters2 + ' m&sup2; '
}


function Areaskm(areaMeters2) {
    var areaHectares = areaMeters2 / meters2PerHectare;
    var areaKm2 = areaMeters2 / metersPerKm / metersPerKm;
    var areaFeet2 = areaMeters2 * feetPerMeter * feetPerMeter;
    var areaMiles2 = areaFeet2 / feetPerMile / feetPerMile;
    var areaAcres = areaMiles2 * acresPerMile2;
    return areaKm2 + ' km&sup2; '
}


function SphericalPolygonAreaMeters2(points) {
    var totalAngle = 0.0;
    for (i = 0; i < points.length; ++i) {
        var j = (i + 1) % points.length;
        var k = (i + 2) % points.length;
        totalAngle += Angle(points[i], points[j], points[k])
    }
    var planarTotalAngle = (points.length - 2) * 180.0;
    var sphericalExcess = totalAngle - planarTotalAngle;
    if (sphericalExcess > 420.0) {
        totalAngle = points.length * 360.0 - totalAngle;
        sphericalExcess = totalAngle - planarTotalAngle
    } else if (sphericalExcess > 300.0 && sphericalExcess < 420.0) {
        sphericalExcess = Math.abs(360.0 - sphericalExcess)
    }
    return sphericalExcess * radiansPerDegree * earthRadiusMeters * earthRadiusMeters
}


function PlanarPolygonAreaMeters2(points) {
    var a = 0.0;
    for (var i = 0; i < points.length; ++i) {
        var j = (i + 1) % points.length;
        var xi = points[i].lng() * metersPerDegree * Math.cos(points[i].lat() * radiansPerDegree);
        var yi = points[i].lat() * metersPerDegree;
        var xj = points[j].lng() * metersPerDegree * Math.cos(points[j].lat() * radiansPerDegree);
        var yj = points[j].lat() * metersPerDegree;
        a += xi * yj - xj * yi
    }
    return Math.abs(a / 2.0)
}


function Angle(p1, p2, p3) {
    var bearing21 = Bearing(p2, p1);
    var bearing23 = Bearing(p2, p3);
    var angle = bearing21 - bearing23;
    if (angle < 0.0) angle += 360.0;
    return angle
}


function Bearing(from, to) {
    var lat1 = from.lat() * radiansPerDegree;
    var lon1 = from.lng() * radiansPerDegree;
    var lat2 = to.lat() * radiansPerDegree;
    var lon2 = to.lng() * radiansPerDegree;
    var angle = -Math.atan2(Math.sin(lon1 - lon2) * Math.cos(lat2), Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));
    if (angle < 0.0) {
        angle += Math.PI * 2.0;
        angle = angle * degreesPerRadian
    }
    return angle
}

//  ???????????????????????????????????????????????
//  ???????????????????????????????????????????????
//  ???????????????????????????????????????????????

if (!google.maps.Polygon.prototype.getBounds) {
    google.maps.Polygon.prototype.getBounds = function (latLng) {
        var bounds = new google.maps.LatLngBounds();
        var paths = this.getPaths();
        var path;
        for (var p = 0; p < paths.getLength(); p++) {
            path = paths.getAt(p);
            for (var i = 0; i < path.getLength(); i++) {
                bounds.extend(path.getAt(i))
            }
        }
        return bounds
    }
}
;

// TODO: could be replaced by google.maps.geometry.poly   containsLocation(point:LatLng, polygon:Polygon)	boolean	Computes whether the given point lies inside the specified polygon.
google.maps.Polygon.prototype.containsLatLng = function (latLng) {
    var bounds = this.getBounds();
    if (bounds != null && !bounds.contains(latLng)) {
        return false
    }
    var inPoly = false;
    var numPaths = this.getPaths().getLength();
    for (var p = 0; p < numPaths; p++) {
        var path = this.getPaths().getAt(p);
        var numPoints = path.getLength();
        var j = numPoints - 1;
        for (var i = 0; i < numPoints; i++) {
            var vertex1 = path.getAt(i);
            var vertex2 = path.getAt(j);
            if (vertex1.lng() < latLng.lng() && vertex2.lng() >= latLng.lng() || vertex2.lng() < latLng.lng() && vertex1.lng() >= latLng.lng()) {
                if (vertex1.lat() + (latLng.lng() - vertex1.lng()) / (vertex2.lng() - vertex1.lng()) * (vertex2.lat() - vertex1.lat()) < latLng.lat()) {
                    inPoly = !inPoly
                }
            }
            j = i
        }
    }
    return inPoly
};

google.maps.LatLng.prototype.distanceFrom = function (newLatLng) {
    var lat1 = this.lat();
    var radianLat1 = lat1 * (Math.PI / 180);
    var lng1 = this.lng();
    var radianLng1 = lng1 * (Math.PI / 180);
    var lat2 = newLatLng.lat();
    var radianLat2 = lat2 * (Math.PI / 180);
    var lng2 = newLatLng.lng();
    var radianLng2 = lng2 * (Math.PI / 180);
    var earth_radius = 3959;
    var diffLat = (radianLat1 - radianLat2);
    var diffLng = (radianLng1 - radianLng2);
    var sinLat = Math.sin(diffLat / 2);
    var sinLng = Math.sin(diffLng / 2);
    var a = Math.pow(sinLat, 2.0) + Math.cos(radianLat1) * Math.cos(radianLat2) * Math.pow(sinLng, 2.0);
    var distance = earth_radius * 2 * Math.asin(Math.min(1, Math.sqrt(a)));
    return distance
};
