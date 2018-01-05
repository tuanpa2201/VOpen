/**
 * 
 */
String.prototype.replaceAll = function (find, replace) {
	var str = this;
	return str.replace(new RegExp(find.replace(/[-\/\\^$*+?.()|[\]{}]/g, '\\$&'), 'g'), replace);
};
function merge_options(obj1,obj2){
    var obj3 = {};
    for (var attrname in obj1) { obj3[attrname] = obj1[attrname]; }
    for (var attrname in obj2) { obj3[attrname] = obj2[attrname]; }
    return obj3;
}
vietek = {
	isGoogleMapLibraryLoaded : false,
	didLoadGoogleMapLibrary : function() {
		vietek.isGoogleMapLibraryLoaded = true;
	},
	VMap : function() {
		var polygon = null;
		var polylines = {};
		var map = null;
		var id = "";
		var infoWindow = null;
		var drawingManager = null;
		var self = this;
		var markerCluster = null;
		this.initMap = function(mapId) {
			while (vietek.isGoogleMapLibraryLoaded) {
				break;
			}
			var isCreated = false;
			if(""+ zk.Widget.$('$' + mapId + '') !== 'null'){
				var widget = zk.Widget.$('$' + mapId + '').$n();
				if (widget != null && widget != undefined) {
					isCreated = true;
				}
			}
			if (!isCreated)
				return;
			map = new google.maps.Map(zk.Widget.$('$' + mapId + '').$n(), {
		          zoom: 5,
		          center: {lat: 16.3776538, lng: 105.0040875},
		          mapTypeControl: true,
		          mapTypeControlOptions: {
			        style: google.maps.MapTypeControlStyle.DROPDOWN_MENU,
			        position: google.maps.ControlPosition.TOP_RIGHT
		          },
		          streetViewControl: false,
		          zoomControlOptions: {
		        	  position: google.maps.ControlPosition.RIGHT_TOP
		          }
		        });
			self.map = map;
			self.id = mapId;
//			google.maps.event.trigger(map, 'resize');
			google.maps.event.addListener(this.map, 'idle', function(e) {
				zAu.send(new zk.Event(zk.Widget.$('$' + mapId + ''), 'onIdle',
						location, {
							toServer : true
						}), 0);
			});
			google.maps.event.addListener(this.map, 'click', function(e) {
				var lat = e.latLng.lat();
				var lng = e.latLng.lng();
				var location  = {
						'data' : {
							'latitude' : lat,
							'longtitude' : lng
						}
				};   
				zAu.send(new zk.Event(zk.Widget.$('$' + mapId + ''), 'onClickVMap',
						location, {
							toServer : true
						}), 0);
				
			});
			// Event mousemove of map, send latlng of cursor to server
			google.maps.event.addListener(this.map, 'mousemove', function(e) {
				var lat = e.latLng.lat();
				var lng = e.latLng.lng();
				var latLng = {
					'data' : {
						'latitude' : lat,
						'longtitude' : lng
					}
				}
				zAu.send(new zk.Event(zk.Widget.$('$' + mapId + ''), 'onMouseMoveVMap',
						latLng, {
							toServer : true
						}), 0);
			});
			// right click on map, send point of cursor to server
			google.maps.event.addListener(this.map, 'rightclick', function(e) {
				var lat = e.latLng.lat();
				var lng = e.latLng.lng();
				var jsonData = {
					'data' : {
						'latitude' : lat,
						'longtitude' : lng
					}
				}
				zAu.send(new zk.Event(zk.Widget.$('$' + mapId + ''), 'onRightClickVMap',
						jsonData, {
							toServer : true
						}), 0);
			});
			// Event zoom changed
			self.map.addListener('zoom_changed', function() {
				var zoom = map.getZoom();
				zAu.send(new zk.Event(zk.Widget.$('$' + mapId + ''), 'onZoomChangedVmap',
						zoom, {
							toServer : true
						}), 0);
			});
			// Event drag
			google.maps.event.addListener(map, 'drag', function(){
				zAu.send(new zk.Event(zk.Widget.$('$' + mapId + ''), 'onDragVmap',
						null, {
							toServer : true
						}), 0);
			});
			//TODO Evvent dragstart
			google.maps.event.addListener(map, 'dragstart', function(){
				zAu.send(new zk.Event(zk.Widget.$('$' + mapId + ''), 'onDragStartVmap',
						null, {
							toServer : true
						}), 0);
			});
			//TODO Event dragend
			google.maps.event.addListener(map, 'dragend', function(){
				zAu.send(new zk.Event(zk.Widget.$('$' + mapId + ''), 'onDragEndVmap',
						null, {
							toServer : true
						}), 0);
			});
			//TODO Event bounds_changed
			google.maps.event.addListener(map, 'bounds_changed', function() {
				if ("" + this.getBounds() !== 'undefined' ) {
					var bounds = map.getBounds();
					var lat0 = map.getBounds().getNorthEast().lat();
					var lng0 = map.getBounds().getNorthEast().lng();
					var lat1 = map.getBounds().getSouthWest().lat();
					var lng1 = map.getBounds().getSouthWest().lng();
					var jsonData = {
						'bounds' : {
							'NorthEastLat' : lat0,
							'NorthEastLng' : lng0,
							'SouthWestLat' : lat1,
							'SouthWestLng' : lng1
						}
					};
					console.log("Bounds - " + lat0 + "|" + lng0 + " - " + lat1 + "|" + lng1);
					zAu.send(new zk.Event(zk.Widget.$('$' + mapId + ''),
							'onBoundsChangedVMap', jsonData, {
								toServer : true
							}), 0);
				}
			});
			//TODO
			google.maps.event.addListener(map, 'center_changed', function(){
//					if (""+ this.getCenter() !== 'undefined') {
					var centerData = {
						'center' : {
							'latitude' : map.getCenter().lat(),
							'longtitude' : map.getCenter().lng()
						}
					};
					zAu.send(new zk.Event(zk.Widget.$('$' + mapId + ''),
							'onCenterChangeVMap', centerData, {
								toServer : true
							}), 0);
//					}
//					google.maps.event.clearListeners(this, 'center_changed');
			});
			
			google.maps.event.addListener(map, 'maptypeid_changed', function(e) {
				var mapType = map.getMapTypeId();
				zAu.send(new zk.Event(zk.Widget.$('$' + mapId + ''),
						'onTypeChangedVMap', mapType, {
							toServer : true
						}), 0);
			});
			
		};
		this.panTo = function(latLng) {
			this.map.panTo(latLng);
		};
		this.panBy = function(x, y){
			this.map.panBy(x,y);
		};
		this.fitBounds = function(bounds){
			this.map.fitBounds(bounds);
		};
		this.panToBounds = function(bounds){
			this.map.panToBounds(bounds);
		};
		this.setCenter = function(possion){
			this.map.setCenter(possion);
		};
		this.setClickableIcons = function(flag){
			this.map.setClickableIcons(flag);
		};
		this.setMapTypeId = function(mapType){
			this.map.setMapTypeId(google.maps.MapTypeId[mapType]);
		};
		this.setZoom = function(zoom){
			this.map.setZoom(zoom);
		};
		this.drawPolygon = function() {
			self.polygon.setMap(null);
			if (self.drawingManager !== null) {
				self.drawingManager.setDrawingMode(google.maps.drawing.OverlayType.POLYLINE);
			}
		};
		this.removeChild = function(childId) {
			var marker = vietek.mapController.markers[childId];
			if(""+marker !== "null" && ""+marker !== "undefined"){
				marker.setMap(null);
				delete vietek.mapController.markers[childId];
			}
			if("" + self.polygon !== 'null' && "" + self.polygon !== 'undefined'){
				self.polygon = null;
			}
			var polyline = vietek.mapController.polylines[childId];
			if("" + polyline !== 'null' && "" + polyline !== 'undefined'){
				polyline.setMap(null);
				delete vietek.mapController.polylines[childId];
			}
		};
		this.removeAllChild = function(){
			if (self.markerCluster != null) {
				self.markerCluster.clearMarkers();
			}
			for(var x in vietek.mapController.markers){
				var marker = vietek.mapController.markers[x];
				if(marker.mapObj !== null){
					if(marker.mapObj === this){
						marker.setMap(null);
						marker.mapID = null;
						delete vietek.mapController.markers[x];
					}
				}
			}
			self.polygon = null;
			for(var y in vietek.mapController.polylines){
				var polyline = vietek.mapController.polylines[y];
				if(polyline.mapObj != null){
					if(polyline.mapObj === this){
						polyline.setMap(null);
						delete vietek.mapController.polylines[y];
					}
				}
			}
		};
		this.removeAllMarker = function(){
			if (self.markerCluster != null) {
				self.markerCluster.clearMarkers();
			}
			for(var x in vietek.mapController.markers){
				var vmarker = vietek.mapController.markers[x];
				if(vmarker.marker.map === this.map || (vmarker.mapId == this.id)){
					vmarker.marker.setMap(null);
					vmarker.mapID = null;
					delete vietek.mapController.markers[x];
				}
			}
		};
	},
	
	VMarker : function() {
		var marker = null;
		var mapObj = null;
		var id = "";
		var self = this;
		var rotate = 0.0;
		var imgSrc = "";
		var content = "";
		var infowindow = null;
		var mapId = null;
		var options = null;
		var isRotateSmooth = true;
		this.setLabelClass = function(Sclass){
			this.marker.set('labelClass', Sclass);
		},
		this.setLabelAnchor = function(x, y){
			var anchor = new google.maps.Point(x, y);
			this.marker.set('labelAnchor', anchor);
		},
		this.setVisible = function(flag){
			this.marker.setVisible(flag);
			if(flag===false){
				if(this.infowindow != null){
					this.infowindow.close();
				}
			}
		},
		this.setClickable = function(flag){
			this.marker.setClickable(flag);
		},
		this.setLabel = function(label){
			if (this.marker instanceof MarkerWithLabel) {
				this.marker.set('labelContent', label);
			}
			else {
				this.marker.setLabel(label);
			}
		},
		this.setContent = function(strContent){
			this.content = strContent;
			if(""+ this.mapObj !== 'undefined' && ""+ this.mapObj !== 'null'){
				var gmap = this.mapObj.map;
				if(this.infowindow == null){
					this.infowindow = new google.maps.InfoWindow({
					    content: strContent
					});
				} 
				this.infowindow.setContent(strContent);
				this.infowindow.setOptions({disableAutoPan : false});
				this.marker.addListener('click', function() {
					self.infowindow.open(gmap, this);
				});
			}
		},
		this.setDraggable = function(draggable) {
			this.marker.setDraggable(draggable);
		},
		this.setPosition = function(position) {
			this.marker.setPosition(position);
			if(this.infowindow != null)
				this.infowindow.setOptions({disableAutoPan : false});
		},
		this.setRotate = function(angle){
			this.isRotateSmooth = false;
			this.rotate = angle;
			var str = RotateIcon.makeIcon(this.imgSrc).setRotation({ deg : angle }).getUrl();
			var image = {
					url : str
			}
			image = merge_options(image, this.options);
			this.marker.setIcon(image);
		},
		this.setRotateSmooth = function(angle) {
			this.setIcon(this.imgSrc, this.options);
			this.isRotateSmooth = true;
			$("img[src='" + this.imgSrc + "#" + this.marker.id + "']").rotate(angle);
			this.rotate = angle;
		}
		this.setIcon = function(imageUrl, options) {
			if (this.imgSrc == imageUrl && this.options == options) {
//				return;
			}
			this.imgSrc = imageUrl;
			this.options = options;
			var self = this;
			var image = {
				url: imageUrl + "#" + this.marker.id,
			};
			if (imageUrl == './vmap/img/m1.png') {
				image = {
					url: imageUrl,
					anchor: new google.maps.Point(35, 35),
				};
			}
			else if (imageUrl == './vmap/img/m2.png') {
				image = {
					url: imageUrl,
					anchor: new google.maps.Point(37, 37),
				};
			}
			else if (imageUrl == './vmap/img/m3.png') {
				image = {
					url: imageUrl,
					anchor: new google.maps.Point(40, 40),
				};
			}
			else if (imageUrl == './vmap/img/m4.png') {
				image = {
					url: imageUrl,
					anchor: new google.maps.Point(45, 45),
				};
			}
			else if (imageUrl == './vmap/img/m5.png') {
				image = {
					url: imageUrl,
					anchor: new google.maps.Point(50, 50),
				};
			}
			image = merge_options(image, options);
			this.marker.setIcon(image);
			if (!this.isRotateSmooth) {
				this.setRotate(this.rotate);
			}
		};
		this.setId = function(newId){
			this.id = newId;
			this.marker.set("id", newId);
			this.marker.id = newId;
		}
		this.setMap = function(mapId) {
			if (this.mapId == mapId) {
				return;
			}
			var vmap = null;
			var map = null;
			if(mapId !== null){
				console.log("getMap:" + mapId);
				vmap = vietek.mapController.maps[mapId];
				map = vmap.map;
			}
			
			if (vmap == null && this.mapObj != null) {
				if (this.mapObj.markerCluster != null) {
					this.mapObj.markerCluster.removeMarker(this.marker);
				}
			}
			
			this.mapId = mapId;
			this.mapObj = vmap;
			this.marker.setMap(map);
			if(vmap !== null){
				if (vmap.markerCluster != null) {
					vmap.markerCluster.addMarker(this.marker);
				}
				this.marker.addListener('click', function(e) {
					var lat = e.latLng.lat();
					var lng = e.latLng.lng();
					var latLng = {
							'data' : {
								'markerId' : self.id,
								'latitude' : lat,
								'longtitude' : lng
							}
						};
					zAu.send(new zk.Event(zk.Widget.$('$' + vmap.id + ''),
							'onVMarkerClick', latLng, {
								toServer : true
							}), 0);
					
				});
				this.marker.addListener('drag', function(e) {
					var lat = e.latLng.lat();
					var lng = e.latLng.lng();
					var data = {
							'data' : {
								'markerId' : self.id,
								'latitude' : lat,
								'longtitude' : lng
							}
					}
					zAu.send(new zk.Event(zk.Widget.$('$' + vmap.id + ''),
							'onVMarkerDrag', data, {
								toServer : true
							}), 0);
				});
				this.marker.addListener('dragstart', function(e) {
					var lat = e.latLng.lat();
			        var lng = e.latLng.lng();
			        var data = {
			    			'data' : {
			    				'markerId': self.id,
			    				'latitude' : lat,
			    				'longtitude' : lng
			    			}
			    	};
					zAu.send(new zk.Event(zk.Widget.$('$' + vmap.id + ''),
							'onVMarkerDragStart', data, {
								toServer : true
							}), 0);
				});
				google.maps.event.addListener(this.marker,'dragend',function(e) {
			        var lat = e.latLng.lat();
			        var lng = e.latLng.lng();
			        var data = {
			    			'data' : {
			    				'markerId': self.id,
			    				'latitude' : lat,
			    				'longtitude' : lng
			    			}
			    		};
			        zAu.send(new zk.Event(zk.Widget.$('$' + vmap.id + ''),
							'onVMarkerDragEnd', data, {
								toServer : true
							}), 0);
			    });
			}
		}
		this.initMarkerWithLabel = function(markerId, options){
			this.id = markerId;
			this.marker = new MarkerWithLabel({
				position: options["position"],
				draggable: false,
				labelContent: "",
//				anchor:new google.maps.Point(32, 32),
				labelAnchor : new google.maps.Point(-10, 10),
				labelInBackground : false,
				labelStyle: {opacity: 0.75}
			});
			this.marker.set("id", markerId);
			this.marker.id = markerId;
		}
		
		this.initMarker = function(markerId, options, size){
			if (size == 1) {
				this.marker = new MarkerWithLabel({
					position: options["position"],
					draggable: false,
					labelContent: "",
				/*	labelAnchor : new google.maps.Point(10, 15),*/
					labelAnchor : new google.maps.Point(3, 32),
					labelInBackground : false,
					labelClass: 'vmarker_label2'
				});
			}
			else if (size == 2) {
				this.marker = new MarkerWithLabel({
					position: options["position"],
					draggable: false,
					labelContent: "",
//					labelAnchor : new google.maps.Point(15, 15),
					labelAnchor : new google.maps.Point(7, 33),
					labelInBackground : false,
					labelClass: 'vmarker_label2'
				});
			}
			else if (size == 3) {
				this.marker = new MarkerWithLabel({
					position: options["position"],
					draggable: false,
					labelContent: "",
					labelAnchor : new google.maps.Point(12, 12),
					labelInBackground : false,
					labelClass: 'vmarker_label2'
				});
			}
			else if (size == 4) {
				this.marker = new MarkerWithLabel({
					position: options["position"],
					draggable: false,
					labelContent: "",
					labelAnchor : new google.maps.Point(13, 11),
					labelInBackground : false,
					labelClass: 'vmarker_label2'
				});
			}
			else if (size == 5) {
				this.marker = new MarkerWithLabel({
					position: options["position"],
					draggable: false,
					labelContent: "",
					labelAnchor : new google.maps.Point(15, 10),
					labelInBackground : false,
					labelClass: 'vmarker_label2'
				});
			}
			this.marker.set("id", markerId);
			this.marker.id = markerId;
		}
	},
	
	VPolyline : function(){
		var id = "";
		var path = [];
		var option = [];
		var polyline;
		var mapObj = null;
		this.setVisible = function(visible){
			this.polyline.setVisible(visible);
		}
		this.setWeight = function(val){
			this.polyline.setOptions({strokeWeight : val});
		};
		this.setOpacity = function(opacity){
			this.polyline.setOptions({strokeOpacity: opacity});
		};
		this.setColor = function(color){
			this.polyline.setOptions({strokeColor: color});
		};
		this.setEditable = function(flag){
			this.polyline.setEditable(flag);
		};
		this.setDraggable = function(flag){
			this.polyline.setDraggable(flag);
		};
		this.setOptions = function(options){
			this.polyline.setOptions(options);
		};
		this.setPath = function(path){
			this.polyline.setPath(path);
		};
		this.setMap = function(mapId) {
			var vmap = null;
			var map = null;
			if(mapId !== null){
				vmap = vietek.mapController.maps[mapId];
				map = vmap.map;
			}
			this.mapObj = vmap;
			this.polyline.setMap(map);
		};
		this.initPolyline = function(pId, option){
			this.id = pId;
			this.polyline = new google.maps.Polyline(option);
			this.polyline.set("id", pId);
		}
	},
	
	mapController : {
		maps : {},
		markers : {},
		polylines : {},
		createMap : function(mapId, isMarkerCluster) {
			var map = new vietek.VMap();
			map.initMap(mapId);
			if (isMarkerCluster) {
				var options = {
						imagePath: './vmap/img/m'
				    };
				var markers = [];
				map.markerCluster = new MarkerClusterer(map.map, markers, options);
			}
			vietek.mapController.maps[mapId] = map;
			console.log("created Map:" + mapId);
		},
		deleteMap : function(mapId) {
			var vmap = vietek.mapController.maps[mapId];
			if ("" + vmap !== 'undefined' && "" + vmap !== 'null') {
				delete vietek.mapController.maps[mapId];
				vmap = null;
			}
		},
		checkChild : function(mapId){
			var vmap = vietek.mapController.maps[mapId];
			if ("" + vmap !== 'undefined' && "" + vmap !== 'null') {
				var size = 0;
				for (var key in vmap.markers) {
			        if (vmap.markers.hasOwnProperty(key)) size++;
			    }
			}
		},
		panTo : function(mapId, lat, lng){
			var vmap = vietek.mapController.maps[mapId];
			if ("" + vmap !== 'undefined' && "" + vmap !== 'null') {
				vmap.panTo({"lat" :lat, "lng": lng});
			}
		},
		removeChild : function(mapId, childId) {
			var vmap = vietek.mapController.maps[mapId];
			if ("" + vmap !== 'undefined' && "" + vmap !== 'null') {
				vmap.removeChild(childId);
			}
		},
		removeAllChild : function(mapId){
			var vmap = vietek.mapController.maps[mapId];
			if ("" + vmap !== 'undefined' && "" + vmap !== 'null') {
				vmap.removeAllChild();
			}
		},
		removeAllMarker : function(mapId){
			var vmap = vietek.mapController.maps[mapId];
			if (typeof vmap !== 'undefined') {
				vmap.removeAllMarker();
			}
		},
		hideAllMarker : function(mapId, flag){
			var vmap = vietek.mapController.maps[mapId];
			for(var x in vietek.mapController.markers){
				vietek.mapController.markers[x].setVisible(flag);
			}
		},
		closeAllInfo : function(mapId){
			var vmap = vietek.mapController.maps[mapId];
			for(var x in vietek.mapController.markers){
				var markerObj = vietek.mapController.markers[x];
				if(typeof markerObj.mapObj !== 'undefined'  && markerObj.mapObj != null){
					if(markerObj.mapObj.id === mapId){
						if(markerObj.infowindow !== null && typeof markerObj.infowindow !== 'undefined'){
							markerObj.infowindow.setOptions({disableAutoPan: true});
							markerObj.infowindow.close();
							markerObj.infowindow=null;
						}
					}
				}
			}
		},
		panBy : function(mapId, x, y){
			var vmap = vietek.mapController.maps[mapId];
			if ("" + vmap !== 'undefined' && "" + vmap !== 'null') {
				vmap.panBy(x, y);
			}
		},
		fitBounds : function(mapId, data) {
			var vmap = vietek.mapController.maps[mapId];
			if ("" + vmap !== 'undefined' && "" + vmap !== 'null') {
				var parsedData = JSON.parse(data.toString());
				var bounds = new google.maps.LatLngBounds();
				var pointsData = parsedData['bounds'];
				var northEast = new google.maps.LatLng(pointsData['NorthEastLat'],
						pointsData['NorthEastLng']);
				var southWest = new google.maps.LatLng(pointsData['SouthWestLat'],
						pointsData['SouthWestLng']);
				bounds.extend(northEast);
				bounds.extend(southWest);
				vmap.fitBounds(bounds);
			} else {
				var str = 'Cannot use panToBounds() map before map load complete!'
				zAu.send(new zk.Event(zk.Widget.$('$' + mapId + ''), 'onErrorMap', str,
						{
							toServer : true
						}), 0);
			}
		},
		panToBounds : function(mapId, data) {
			var vmap = vietek.mapController.maps[mapId];
			if ("" + vmap !== 'undefined' && "" + vmap !== 'null') {
				var parsedData = JSON.parse(data.toString());
				var bounds = new google.maps.LatLngBounds();
				var pointsData = parsedData['bounds'];
				var northEast = new google.maps.LatLng(pointsData['NorthEastLat'],
						pointsData['NorthEastLng']);
				var southWest = new google.maps.LatLng(pointsData['SouthWestLat'],
						pointsData['SouthWestLng']);
				bounds.extend(northEast);
				bounds.extend(southWest);
				vmap.panToBounds(bounds);
			} else {
				var str = 'Cannot use panToBounds() map before map load complete!'
				zAu.send(new zk.Event(zk.Widget.$('$' + mapId + ''), 'onErrorMap', str,
						{
							toServer : true
						}), 0);
			}
		},
		setCenter : function(mapId, lat, lng) {
			var vmap = vietek.mapController.maps[mapId];
			if ("" + vmap !== 'undefined' && "" + vmap !== 'null') {
				var possion = new google.maps.LatLng(lat, lng);
				vmap.setCenter(possion);
			}
		},
		setClickableIcons : function(mapId, flag) {
			var vmap = vietek.mapController.maps[mapId];
			if ("" + vmap !== 'undefined' && "" + vmap !== 'null') {
				vmap.setClickableIcons(flag);
			}
		},
		setMapTypeId : function(mapId, mapType){
			var vmap = vietek.mapController.maps[mapId];
			if ("" + vmap !== 'undefined' && "" + vmap !== 'null') {
				vmap.setMapTypeId(mapType);
			}
		},
		setZoom : function(mapId, zoom){
			var vmap = vietek.mapController.maps[mapId];
			if ("" + vmap !== 'undefined' && "" + vmap !== 'null') {
				vmap.setZoom(zoom);
			}
		},
		drawPolygon : function(mapId){
			var vmap = vietek.mapController.maps[mapId];
			if ("" + vmap !== 'undefined' && "" + vmap !== 'null') {
				vmap.drawPolygon();
			}
		},
		
		addMarkerWithLabel : function(markerId, options) {
			var vmarker = new vietek.VMarker();
			vmarker.initMarkerWithLabel(markerId, options);
			vietek.mapController.markers[markerId] = vmarker;
		},
		addMarker : function(markerId, options, size) {
			var vmarker = new vietek.VMarker();
			vmarker.initMarker(markerId, options, size);
			vietek.mapController.markers[markerId] = vmarker;
		},
		setIdMarker : function(oldId, newId){
			var vmarker = vietek.mapController.markers[oldId];
			if(""+ vmarker !== 'undefined' && "" + vmarker !== 'null'){
				vmarker.setId(newId);
				delete vietek.mapController.markers[oldId];
				vietek.mapController.markers[newId] = vmarker;
			}
		},
		setMap : function(mapId, markerId) {
			var vmarker = vietek.mapController.markers[markerId];
			vmarker.setMap(mapId);
		},
		setLabel : function(markerId, label){
			var vmarker = vietek.mapController.markers[markerId];
			vmarker.setLabel(label);
		},
		setLabelAnchor : function(markerId, x, y){
			var vmarker = vietek.mapController.markers[markerId];
			vmarker.setLabelAnchor(x, y);
		},
		setLabelClass : function(markerId, sclass){
			var vmarker = vietek.mapController.markers[markerId];
			vmarker.setLabelClass(sclass);
		},
		setIcon : function(markerId, imgSrc, options){
			var vmarker = vietek.mapController.markers[markerId];
			vmarker.setIcon(imgSrc, options);
		},
		setRotate : function(markerId, angle){
			var vmarker = vietek.mapController.markers[markerId];
			vmarker.setRotate(angle);
		},
		
		setRotateSmooth : function(markerId, angle){
			var vmarker = vietek.mapController.markers[markerId];
			vmarker.setRotateSmooth(angle);
		},
		
		setPosition : function(markerId, lat, lng) {
			var vmarker = vietek.mapController.markers[markerId];
			if("" + vmarker === 'undefined' || "" + vmarker === 'null'){
				vietek.mapController.addMarker(markerId, {image:'./themes/images/gmaps_marker.png', position:{'lat' : lat, 'lng' : lng}}, 5);
				vmarker = vietek.mapController.markers[markerId];
			}
//			if ("" + vmarker !== 'undefined' && "" + vmarker !== 'null') {
				var position = {"lat" : lat, "lng" : lng};
				vmarker.setPosition(position);
//			}
		},
		setContent : function(markerId, strContent){
			var vmarker = vietek.mapController.markers[markerId];
			if(strContent.indexOf("&#39;") > -1){
				strContent = strContent.replaceAll("&#39;", "'");
			}
			if(strContent.indexOf('&quot;') > -1){
				strContent = strContent.replaceAll("&quot;", '"');
			}
			if(strContent.indexOf('&#92;') > -1){
				strContent = strContent.replaceAll("&#92;", "\\");
			}
			vmarker.setContent(strContent);
		},
		setOpenContent : function(mapId, markerId, flag){
			var vmap = vietek.mapController.maps[mapId];
			if("" + vmap !== 'undefined' && "" + vmap !== 'null'){
				var markerObj = vietek.mapController.markers[markerId];
				if(""+ markerObj !== 'undefined' && ""+ markerObj !== 'null'){
					if(flag === true){
						if(markerObj.infowindow == null){
							markerObj.infowindow = new google.maps.InfoWindow({
								disableAutoPan: false,
							    content: markerObj.content
							});
							markerObj.infowindow.setOptions({disableAutoPan : false});
						}
						markerObj.marker.addListener('click', function() {
							markerObj.infowindow.open(vmap.map, this);
					    });
						markerObj.infowindow.open(vmap.map, markerObj.marker);
					} else {
						if(markerObj.infowindow!= null){
							markerObj.infowindow.close();
						}
					}
				}
			}
		},
		setDraggable : function(markerId, flag){
			var vmarker = vietek.mapController.markers[markerId];
			//TODO: Sao lai = null?
//			if (typeof vmarker !== 'undefined')
				vmarker.setDraggable(flag);
		},
		setClickable : function(markerId, flag) {
			var marker = vietek.mapController.markers[markerId];
			marker.setClickable(flag);
		},
		setVisible : function(markerId, flag) {
			var marker = vietek.mapController.markers[markerId];
			if(marker !== undefined && marker !== null){
				marker.setVisible(flag);
			}
		},
		removeMarker : function(markerId) {
			var vmarker = vietek.mapController.markers[markerId];
			if (typeof vmarker != 'undefined' && vmarker != null) {
				vmarker.marker.setMap(null);
				delete vietek.mapController.markers[markerId];
			}
		},
		autoPanVMarker : function(markerId, flag){
			var vmarker = vietek.mapController.markers[markerId];
			if(typeof vmarker.infowindow !== 'undefined' && vmarker.infowindow !== null){
				vmarker.infowindow.setOptions({disableAutoPan : flag});
			}
		},
		
		/**
		 * VPolyline
		 */
		addVPolyline : function(polylineId, options){
			while(options.indexOf("&quot;")!= -1 || options.indexOf("&#39;")!= -1){
				options = options.replace('&quot;', '"');
				options = options.replace("&#39;", "'");
			}
			var option = JSON.parse(options);
			var polyline = new vietek.VPolyline();
			polyline.initPolyline(polylineId, option);
			vietek.mapController.polylines[polylineId] = polyline;
		},
		setMapVPolyline : function(mapId, pId){
			var polyline = vietek.mapController.polylines[pId];
			polyline.setMap(mapId);
		},
		setPathVPolyline : function(pId, options){
			var polyline = vietek.mapController.polylines[pId];
			while(options.indexOf("&quot;")!= -1 || options.indexOf("&#39;")!= -1){
				options = options.replace('&quot;', '"');
				options = options.replace("&#39;", "'");
			}
			var option = JSON.parse(options);
			polyline.setPath(option);
		},
		setOptionsVPolyline : function(pId, strOptions){
			var polyline = vietek.mapController.polylines[pId];
			while(strOptions.indexOf("&quot;")!= -1 || strOptions.indexOf("&#39;")!= -1){
				strOptions = strOptions.replace('&quot;', '"');
				strOptions = strOptions.replace("&#39;", "'");
			}
			var options = JSON.parse(strOptions);
			polyline.setOptions(options);
		},
		setDraggableVPolyline : function(pId, flag){
			var polyline = vietek.mapController.polylines[pId];
			polyline.setDraggable(flag);
		},
		setEditableVpolyline : function(pId, flag){
			var polyline = vietek.mapController.polylines[pId];
			polyline.setDraggable(flag);
		},
		setColorVPolyline : function(pId, color){
			var polyline = vietek.mapController.polylines[pId];
			polyline.setColor(color);
		},
		setOpacityVPolyline : function(pId, opacity){
			var polyline = vietek.mapController.polylines[pId];
			polyline.setOpacity(opacity);
		},
		setWeightVPolyline : function(pId, val){
			var polyline = vietek.mapController.polylines[pId];
			polyline.setWeight(val);
		},
		setVisibleVPolyline : function(pId, visible){
			var polyline = vietek.mapController.polylines[pId];
			polyline.setVisible(visible);
		},
		
		/**
		 * VInfoWindow
		 */
		addInfo : function(wId){
			
		},
		doCluster : function(mapId) {
//			var vmap = vietek.mapController.maps[mapId];
//			if ("" + vmap !== 'undefined' && "" + vmap !== 'null') {
//				vmap.doCluster();
//			}
		}
	}
}
