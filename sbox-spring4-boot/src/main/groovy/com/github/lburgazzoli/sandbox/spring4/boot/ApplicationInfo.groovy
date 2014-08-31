/*
 * Copyright 2014 lb
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.lburgazzoli.sandbox.spring4.boot

import org.apache.commons.lang3.builder.ReflectionToStringBuilder
import org.apache.commons.lang3.builder.ToStringStyle

class ApplicationInfo {
    String name
    String description
    File path
    File pathProperties
    File pathGroovy

    public void setName(String name) {
        this.name = name
    }

    public void setDescription(String description) {
        this.description = description
    }

    public void setPath(File path) {
        this.path = path
    }

    public void setPathProperties(File pathProperties) {
        this.pathProperties = pathProperties
    }

    public void setPathGroovy(File pathGroovy) {
        this.pathGroovy = pathGroovy
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE)
    }
}
