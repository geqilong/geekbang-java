/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.geektimes.configuration.microprofile.config.source.servlet;

import org.eclipse.microprofile.config.spi.ConfigSource;
import org.geektimes.configuration.microprofile.config.source.MapBasedConfigSource;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;

/**
 * ServletRequest ConfigSource
 */
public class ServletRequestConfigSource extends MapBasedConfigSource {
    private final HttpServletRequest request;

    protected ServletRequestConfigSource(HttpServletRequest request) {
        super("HttpServletRequest Parameters", 50);
        this.request = request;
    }

    @Override
    protected void prepareConfigData(Map configData) throws Throwable {
        List<String> paramList = Collections.list(request.getParameterNames());
        for (String param : paramList) {
            configData.put(param, request.getParameter(param));
        }
    }
}
