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
		this.initMap = function(mapId) {
			while (vietek.isGoogleMapLibraryLoaded) {
				break;
			}
			map = new google.maps.Map(zk.Widget.$(jq('$' + mapId + '')).$n(), {
		          zoom: 15,
		          center: {lat: 20.9694534, lng: 105.835416}
		        });
			self.map = map;
			self.id = mapId;
			google.maps.event.addListener(this.map, 'click', function() {
				zAu.send(new zk.Event(zk.Widget.$('$' + mapId + ''), 'onVMapClick',
						mapId, {
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
				zAu.send(new zk.Event(zk.Widget.$('$' + mapId + ''), 'onMouseMove',
						latLng, {
							toServer : true
						}), 0);
			});
			// right click on map, send point of cursor to server
			google.maps.event.addListener(this.map, 'rightclick', function(e) {
				var lat = e.latLng.lat();
				var lng = e.latLng.lng();
				var point = new google.maps.LatLng(lat, lng);
				var address = '';
				var geocoder = new google.maps.Geocoder(); // create a geocoder object
				geocoder.geocode({
					'latLng' : point
				}, function(results, status) {
					if (status == google.maps.GeocoderStatus.OK) { // if geocode success
						address = results[0].formatted_address;
						
					}
				});

				var jsonData = {
					'data' : {
						'latitude' : lat,
						'longtitude' : lng,
						'address' : address
					}
				}
				zAu.send(new zk.Event(zk.Widget.$('$' + mapId + ''), 'onMapRightClick',
						jsonData, {
							toServer : true
						}), 0);
			});
			// Event zoom changed
			self.map.addListener('zoom_changed', function() {
				var zoom = map.getZoom();
				zAu.send(new zk.Event(zk.Widget.$('$' + mapId + ''), 'onZoomChanged',
						zoom, {
							toServer : true
						}), 0);
			});
			zAu.send(new zk.Event(zk.Widget.$('$' + mapId + ''),
					'onDidLoadMap', null, {
						toServer : true
					}), 0);
			
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
		
		this.hideAllMarker = function(){
			for(var x in self.markers){
				self.markers[x].setVisible(false);
			}
		};
		this.removeChild = function(childId) {
			var marker = vietek.mapController.markers[childId];
			if(""+marker !== "null" && ""+marker !== "undefined"){
				self.markers[childId] = null;
				marker.setMap(null);
			}
			if("" + self.polygon !== 'null' && "" + self.polygon !== 'undefined'){
				self.polygon = null;
			}
			var polyline = self.polylines[childId];
			if("" + polyline !== 'null' && "" + polyline !== 'undefined'){
				self.polylines[childId] = null;
				polyline = null;
			}
		};
		this.removeAllChild = function(){
			for(var x in self.markers){
				var marker = self.markers[x];
				if(""+marker !== "null" && ""+marker !== "undefined"){
					self.markers[x] = null;
					marker.setMap(null);
				}
			}
			self.polygon = null;
			for(var y in self.polylines){
				var polyline = self.polylines[y];
				if("" + polyline !== "null" && "" + polyline !== "undefined"){
					self.polylines[y] = null;
					polyline = null;
				}
			}
		};
		this.removeAllMarker = function(){
			for(var x in vietek.mapController.markers){
				var vmarker = vietek.mapController.markers[x];
				if(vmarker.marker.map === this.map){
					
					delete vietek.mapController.markers[x]
					vmarker.marker.setMap(null);
				}
			}
		}
	},
	
	VMarker : function() {
		var map = null;
		var marker = null;
		var id = "";
		var imgSrc = '';
		var self = this;
		var rotate = 0;
		this.setLabel = function(lable){
			this.marker.set('labelContent', lable);
		}
		this.setContent = function(strContent){
			if(""+ this.map !== 'undefined' && ""+ this.map !== 'null'){
				if(""+ this.map.infoWindow !== 'null' && ""+ this.map.infoWindow !== 'undefined'){
					this.map.infoWindow.close();
					this.map.infoWindow = null;
				}
				var gmap = this.map.map;
				this.map.infoWindow = new google.maps.InfoWindow({
				    content: strContent
				});
				this.marker.addListener('click', function() {
					self.map.infoWindow.open(gmap, this);
				});
			}
		},
		this.setDraggable = function(draggable) {
			this.marker.setDraggable(draggable);
		},
		this.setPosition = function(position) {
			this.marker.setPosition(position);
		},
		this.setRotate = function(angle){
			this.rotate = angle;
			var str = RotateIcon.makeIcon(this.imgSrc).setRotation({ deg : angle }).getUrl();
			this.marker.setIcon(str);
		},
		this.setIcon = function(image) {
			this.imgSrc = image;
			var str = RotateIcon.makeIcon(image).setRotation({ deg : this.rotate }).getUrl();
			this.marker.setIcon(str);
		};
		this.setMap = function(mapId) {
			var vmap = vietek.mapController.maps[mapId];
			if(""+ vmap !== 'undefined' && ""+vmap !== 'null'){
				map = vmap.map;
			}
			else {
			}
			this.map = vmap;
			this.marker.setMap(map);
			this.marker.addListener('click', function() {
				zAu.send(new zk.Event(zk.Widget.$('$' + vmap.id + ''),
						'onMarkerClick', self.id, {
							toServer : true
						}), 0);
			});
			this.marker.addListener('drag', function() {
				zAu.send(new zk.Event(zk.Widget.$('$' + vmap.id + ''),
						'onMarkerDrag', self.id, {
							toServer : true
						}), 0);
			});
			this.marker.addListener('dragstart', function() {
				zAu.send(new zk.Event(zk.Widget.$('$' + vmap.id + ''),
						'onMarkerDragStart', self.id, {
							toServer : true
						}), 0);
			});
			google.maps.event.addListener(this.marker,'dragend',function(e) {
		        var lat = e.latLng.lat();
		        var lng = e.latLng.lng();
		        var data = {
		    			'data' : {
		    				'markerId': self.id,
		    				'lat' : lat,
		    				'lng' : lng
		    			}
		    		};
		        zAu.send(new zk.Event(zk.Widget.$('$' + vmap.id + ''),
						'onMarkerDragEnd', data, {
							toServer : true
						}), 0);
		    });
		}
		this.initMarker = function(markerId, options){
			this.id = markerId;
			this.marker = new google.maps.Marker({
	            position: options["position"],
	        });
		}
	},
	
	Polyline : function(){
		var id = "";
		var map = null;
		var polyline = null;
		var path = [];
		this.initPolyline = function(pId, option){
			this.id = pId;
			this.polyline = new google.maps.Polyline({
				
			});
		}
	},
	
	mapController : {
		maps : {},
		markers : {},
		createMap : function(mapId) {
			var map = new vietek.VMap();
			map.initMap(mapId);
			vietek.mapController.maps[mapId] = map;
		},
		deleteMap : function(mapId) {
			var vmap = vietek.mapController.maps[mapId];
			if ("" + vmap !== 'undefined' && "" + vmap !== 'null') {
				delete vietek.mapController.maps[mapId];
				vmap = null;
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
			//TODO: Sao lai = null?
			if (typeof vmap !== 'undefined') {
				vmap.removeAllMarker();
			}
		},
		panBy : function(mapId, x, y){
			var vmap = vietek.mapController.maps[mapId];
			if ("" + vmap !== 'undefined' && "" + vmap !== 'null') {
				vmap.panBy(x, y);
			}
		},
		fitBounds : function(mapId, points, sum) {
			var vmap = vietek.mapController.maps[mapId];
			if ("" + vmap !== 'undefined' && "" + vmap !== 'null') {
				var parsedData = JSON.parse(points.toString());
				var bounds = new google.maps.LatLngBounds();
				for (var i = 0; i < sum; i++) {
					var pointsData = parsedData[i];
					var point = new google.maps.LatLng(pointsData['lat'],
							pointsData['lng']);
					bounds.extend(point);
				}
				vmap.fitBounds(bounds);
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
				map.panToBounds(bounds);
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
		hideAllMarker : function(mapId){
			var vmap = vietek.mapController.maps[mapId];
			if ("" + vmap !== 'undefined' && "" + vmap !== 'null') {
				vmap.hideAllMarker();
			}
		},
		
		addMarker : function(markerId, options) {
			var vmarker = new vietek.VMarker();
			vmarker.initMarker(markerId, options);
			vietek.mapController.markers[markerId] = vmarker;
			zAu.send(new zk.Event(zk.Widget.$('$' + markerId + ''),
					'onDidLoadMarker', null, {
					toServer : true
			}), 0);
		},
		
		setMap : function(mapId, markerId) {
			var vmarker = vietek.mapController.markers[markerId];
			vmarker.setMap(mapId);
		},
		setLabel : function(markerId, label){
			var vmarker = vietek.mapController.markers[markerId];
			vmarker.setLabel(label);
		},
		setIcon : function(markerId, imgSrc){
			var vmarker = vietek.mapController.markers[markerId];
			if (""+ vmarker !== 'undefined' && ""+ vmarker !== 'null'){
				vmarker.setIcon(imgSrc);
			}
		},
		setRotate : function(markerId, angle){
			var vmarker = vietek.mapController.markers[markerId];
			vmarker.setRotate(angle);
		},
		
		setPosition : function(markerId, lat, lng) {
			var vmarker = vietek.mapController.markers[markerId];
			if ("" + vmarker !== 'undefined' && "" + vmarker !== 'null') {
				var position = {"lat" : lat, "lng" : lng};
				vmarker.setPosition(position);
			}
		},
		setContent : function(markerId, strContent){
			var vmarker = vietek.mapController.markers[markerId];
			if("" + vmarker !== 'undefined' && "" + vmarker !== 'null'){
				vmarker.setContent(strContent);
			}
		},
		openContent : function(mapId, markerId){
			var vmap = vietek.mapController.maps[mapId];
			if("" + vmap !== 'undefined' && "" + vmap !== 'null'){
				var marker = vmap.markers[markerId];
				if(""+ marker !== 'undefined' && ""+ marker !== 'null'){
					if(vmap.infoWindow!= null){
						vmap.infoWindow = null;
					}
					vmap.infoWindow = new google.maps.InfoWindow({
					    content: marker.content
					});
					marker.addListener('click', function() {
						vmap.infowindow.open(vmap.map, marker);
					});
				}
			}
		},
		setDraggable : function(markerId, flag){
			var vmarker = vietek.mapController.markers[markerId];
			//TODO: Sao lai = null?
			if (typeof vmarker !== 'undefined')
				vmarker.setDraggable(flag);
		},
		setClickable : function(mapId, markerId, flag){
			var vmap = vietek.mapController.maps[mapId];
			if("" + vmap !== 'undefined' && "" + vmap !== 'null'){
				var marker = vmap.markers[markerId];
				if(""+ marker !== 'undefined' && ""+ marker !== 'null'){
					marker.setClickable(flag);
				}
			}
		},
		setVisible : function(mapId, markerId, flag) {
			var vmap = vietek.mapController.maps[mapId];
			if("" + vmap !== 'undefined' && "" + vmap !== 'null'){
				var marker = vmap.markers[markerId];
				if(""+ marker !== 'undefined' && ""+ marker !== 'null'){
					marker.setVisible(flag);
				}
			}
		},
		removeMarker : function(markerId) {
			var vmarker = vietek.mapController.markers[markerId];
			if (typeof vmarker != 'undefined') {
				vmarker.marker.setMap(null);
				delete vietek.mapController.markers[markerId];
			}
		}
	},
}