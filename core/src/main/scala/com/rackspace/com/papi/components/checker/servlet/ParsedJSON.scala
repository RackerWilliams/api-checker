/***
 *   Copyright 2017 Rackspace US, Inc.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package com.rackspace.com.papi.components.checker.servlet

import javax.servlet.http.HttpServletRequest

import com.rackspace.com.papi.components.checker.servlet.RequestAttributes._

import com.fasterxml.jackson.databind.JsonNode

class ParsedJSON(val representation : JsonNode) extends ParsedRepresentation {
  type R = JsonNode
  override val parameters = List(PARSED_JSON)
  override val sideEffectParams = List(PARSED_JSON_SEQUENCE, PARSED_JSON_XDM_VALUE)
}
