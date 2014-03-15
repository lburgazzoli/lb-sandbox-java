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
package com.github.lburgazzoli.sandbox;

/**
 */
public class MiscMain {
    public static void main(String[] args) throws Exception {
        CharSequence cs1 = new StringBuffer("test").toString();
        CharSequence cs2 = new StringBuffer("test");

        System.out.println("length1   : " + cs1.length());
        System.out.println("toString1 : " + cs1.toString());
        System.out.println("length2   : " + cs2.length());
        System.out.println("toString2 : " + cs2.toString());
    }
}
