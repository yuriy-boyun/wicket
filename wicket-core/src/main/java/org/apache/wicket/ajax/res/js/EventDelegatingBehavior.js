/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

;(function (undefined) {
	'use strict';

	jQuery.extend(true, Wicket.Event, {

		delegate: function(element, eventName, attributesMap) {

			Wicket.Event.add(element, eventName, function(event) {
				var cursor = event.target;

				while (cursor && cursor.tagName.toLowerCase() !== "html") {
					var id = cursor.id;

console.log(1, id);

					if (attributesMap[id]) {

						var attrs = attributesMap[id];
console.log(3, attrs);
						var call = new Wicket.Ajax.Call();
						call.ajax(attrs);
						break;
					}

					cursor = cursor.parentNode;
				}
			});
		}
	})
})();
