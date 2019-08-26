/*
 *  Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.ballerinalang.test.service.http.sample;

import org.ballerinalang.test.service.http.HttpBaseTest;
import org.ballerinalang.test.util.HttpClientRequest;
import org.ballerinalang.test.util.HttpResponse;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.assertEquals;

/**
 * Test cases for HTTP caching.
 *
 * @since 1.0
 */
@Test(groups = "http-test")
public class HTTPCachingTestCase extends HttpBaseTest {

    @Test(description = "Test basic caching behaviour")
    public void testPassthroughServiceByBasePath() throws IOException, InterruptedException {
        final String serviceHitCount = "x-service-hit-count";
        final String proxyHitCount = "x-proxy-hit-count";
        final String payload = "{\"message\":\"Hello, World!\"}";
        HttpResponse response = HttpClientRequest.doGet(serverInstance.getServiceURLHttp(9239, "cache"));
        assertEquals(response.getResponseCode(), 200);
        assertEquals(response.getHeaders().get(serviceHitCount), "1");
        assertEquals(response.getHeaders().get(proxyHitCount), "1");
        assertEquals(response.getData(), payload);

        response = HttpClientRequest.doGet(serverInstance.getServiceURLHttp(9239, "cache"));
        assertEquals(response.getResponseCode(), 200);
        assertEquals(response.getHeaders().get(serviceHitCount), "1");
        assertEquals(response.getHeaders().get(proxyHitCount), "2");
        assertEquals(response.getData(), payload);

        // Wait for a while before sending the next request
        Thread.sleep(1000);

        response = HttpClientRequest.doGet(serverInstance.getServiceURLHttp(9239, "cache"));
        assertEquals(response.getResponseCode(), 200);
        assertEquals(response.getHeaders().get(serviceHitCount), "1");
        assertEquals(response.getHeaders().get(proxyHitCount), "3");
        assertEquals(response.getData(), payload);
    }

    @Test(description = "Test no-cache cache control")
    public void testNoCacheCacheControl() throws IOException, InterruptedException {
        HttpResponse response = HttpClientRequest.doGet(serverInstance.getServiceURLHttp(9242, "nocache"));
        assertEquals(response.getData(), "{\"message\":\"1st request\"}");

        response = HttpClientRequest.doGet(serverInstance.getServiceURLHttp(9242, "nocache"));
        assertEquals(response.getData(), "{\"message\":\"2nd request\"}");
    }

    @Test(description = "Test max-age cache control")
    public void testMaxAgeCacheControl() throws IOException, InterruptedException {
        HttpResponse response = HttpClientRequest.doGet(serverInstance.getServiceURLHttp(9243, "maxAge"));
        assertEquals(response.getData(), "{\"message\":\"before cache expiration\"}");

        response = HttpClientRequest.doGet(serverInstance.getServiceURLHttp(9243, "maxAge"));
        assertEquals(response.getData(), "{\"message\":\"before cache expiration\"}");

        Thread.sleep(5000);

        response = HttpClientRequest.doGet(serverInstance.getServiceURLHttp(9243, "maxAge"));
        assertEquals(response.getData(), "{\"message\":\"after cache expiration\"}");
    }
}
