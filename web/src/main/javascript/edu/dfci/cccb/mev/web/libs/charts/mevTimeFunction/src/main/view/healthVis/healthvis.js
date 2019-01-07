define(["jquery"], function($) {

			function HVSocket() {
				this._socket = null;
				this._requestId = 0;
				this._callbacks = {};
				this._requestsStack = [];
			}

			HVSocket.instance = new HVSocket();
			HVSocket.prototype.initialize = function(host) {
				if (host == 'None') {
					return;
				}

				try {
					var socket = new WebSocket(host);
					var self = this;

					socket.onopen = function() {
						self._onSocketOpen();
					};
					socket.onmessage = function(msg) {
						self._onSocketMsg(msg);
					};
					socket.onclose = function() {
						self._onSocketClose();
					};

					this._socket = socket;
				} catch (ex) {
					console.log(ex);
				}
			};

			HVSocket.prototype._onSocketOpen = function() {
				console.log("Socket connected\n");
				for (var i = 0; i < this._requestsStack.length; ++i) {
					this._socket.send(this._requestsStack[i]);
				}

				this._requestsStack = [];
			};

			HVSocket.prototype._onSocketClose = function() {
				this._socket = null;
			};

			HVSocket.prototype._onSocketMsg = function(msg) {
				console.log('Socket received: ' + msg.data);

				var message = JSON.parse(msg.data);
				if (message.type == 'response') {
					var callback = this._callbacks[message.id];
					delete this._callbacks[message.id];
					callback(JSON.parse(message.data));
				} else if (message.type == 'request') {

				}
			};

			HVSocket.prototype.connected = function() {
				return (this._socket != null);
			};

			HVSocket.prototype._nextId = function() {
				return this._requestId++;
			};

			HVSocket.prototype.sendRequest = function(data, callback) {
				var id = this._nextId();
				this._callbacks[id] = callback;
				var message = {
					type : 'request',
					id : id,
					data : data
				};

				var msg = JSON.stringify(message);
				console.log("Socket sending");
				console.dir(message.data);
				if (this._socket.readyState) {
					this._socket.send(msg);
				} else {
					console.log("Pushed to stack");
					this._requestsStack.push(msg);
				}
			};

			function Healthvis() {
				this._renderer = null;
				this._paramsURL = null;
				this._socket = null;
				this._saved = null;
				this._elementId = null;
				this._embedIds = null;
			}

			Healthvis.instance = new Healthvis();

			Healthvis.prototype.initialize = function(saved, url, elementId,
					emebedIds) {
				this._saved = saved;
				this._elementId = elementId;
				this._embedIds = emebedIds;

				if (!saved) {
					this._socket = HVSocket.instance;
					this._socket.initialize(url);
				} else {
					this._paramsURL = url;
				}
			};

			Healthvis.prototype.register = function(fn) {
				this._renderer = fn;
			};

			Healthvis.prototype.visualize = function() {
				var renderer = this._renderer, self = this, elementId = this._elementId;

				var callback = function(json) {
					renderer.init(elementId, json);
					renderer.visualize();

					if (document.location.search.length) {
						var newcov = $('#covariate-form').serializeArray();
						self.update(newcov);
					}
				};

				if (this._saved) {
					d3.json(this._paramsURL, callback);
				} else {
					this._getParams(callback);
				}
			};

			Healthvis.prototype.getDimensions = function(inputWidth,
					inputHeight) {
				var height = inputHeight, width = inputWidth, embedIds = this._embedIds, elementId = this._elementId;

				if (embedIds) {
					var totalHeight = $(window).height();

					height = totalHeight - $(embedIds.header).height() - $(embedIds.footer).height();
					width = $(elementId).width();

					height = (height > 0 && height < inputHeight) ? height : inputHeight;
					width = (width > 0 && width < inputWidth) ? width : inputWidth;
				}

				return {
					width : width,
					height : height
				};
			};

			Healthvis.prototype.update = function(formInput) {
				this._renderer.update(formInput);
			};

			Healthvis.prototype._getParams = function(callback) {
				var message = {
					action : 'getParams'
				}
				this._socket.sendRequest(message, callback);
			};

			Healthvis.prototype._savePlot = function(url, uploadURL, query) {
				var self = this, query = query;

				var callback = function(json) {
					self._stopServer();
					var url = '/display/' + json.id + '?' + query;
					console.log(url);
					console.log(encodeURI(url));
					window.location.replace(url);
				};

				this.initialize(false, url);
				var message = {
					action : 'savePlot',
					data : uploadURL
				}
				this._socket.sendRequest(message, callback);
			};

			Healthvis.prototype._stopServer = function() {
				var message = {
					action : 'stopServer',
					data : ''
				}
				this._socket.sendRequest(message, function() {
				})
			};

			var healthvis = Healthvis.instance;

			$(document).ready(function() {
				$('#covariate-form :input').change(function() {
					if ($('#covariate-form').valid()) {
						var newcov = $('#covariate-form').serializeArray();
						healthvis.update(newcov);
					} else {
						console.log('Not valid')
					}
				});
			});

		});