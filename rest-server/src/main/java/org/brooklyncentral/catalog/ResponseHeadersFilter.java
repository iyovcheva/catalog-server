/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.brooklyncentral.catalog;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

public class ResponseHeadersFilter implements Filter {

    private final Map<String, String> headers = new HashMap<>();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String headerKeys = filterConfig.getInitParameter("headersKeys");
        String headerValues = filterConfig.getInitParameter("headersValues");

        String[] listHeaderKeys = StringUtils.split(headerKeys, "|");
        String[] listHeaderValues = StringUtils.split(headerValues, "|");

        for (int i = 0; i < listHeaderKeys.length; i++) {
            headers.put(listHeaderKeys[i], listHeaderValues.length > i ? listHeaderValues[i] : null);
        }
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
        for (Map.Entry<String, String> header : this.headers.entrySet()) {
            httpResponse.setHeader(header.getKey(), header.getValue());
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }
}