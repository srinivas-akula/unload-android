/*
 * Copyright 2015 Anton Tananaev (anton.tananaev@gmail.com)
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
package com.sringa.unload.service;

import org.json.JSONObject;

public class DummyRequestManager implements IRequestManager {

    @Override
    public void sendAsyncRequest(Method method, String urlStr, JSONObject jsonObject, IRequestHandler handler) {

        Response response = new Response();
        response.markSuccess();
        response.setResponse("Success.");
        handler.onComplete(response);
    }
}