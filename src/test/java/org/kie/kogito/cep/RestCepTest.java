/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.cep;

import java.util.Arrays;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;

@QuarkusTest
public class RestCepTest {

    @Test
    public void testInsert() {
        given()
                .body(new StockTick( "IBM", 2000, 100 ).toJson())
                .contentType(ContentType.JSON)
                .when()
                .post("/stocks/1")
                .then()
                .statusCode(200)
                .body(equalTo("0"));

        // insert on a different rule unit instance
        given()
                .body(new StockTick( "IBM", 1700, 150 ).toJson())
                .contentType(ContentType.JSON)
                .when()
                .post("/stocks/2")
                .then()
                .statusCode(200)
                .body(equalTo("0"));

        // temporal constraint not satisfied
        given()
                .body(new StockTick( "IBM", 1700, 250 ).toJson())
                .contentType(ContentType.JSON)
                .when()
                .post("/stocks/1")
                .then()
                .statusCode(200)
                .body(equalTo("0"));

        // this fires
        given()
                .body(new StockTick( "IBM", 1500, 300 ).toJson())
                .contentType(ContentType.JSON)
                .when()
                .post("/stocks/1")
                .then()
                .statusCode(200)
                .body(equalTo("2"));
    }

    @Test
    public void testBatchInsert() {
        String json = null;
        try {
            json = new ObjectMapper().writeValueAsString(Arrays.asList(
                    new StockTick( "IBM", 2000, 100 ),
                    new StockTick( "IBM", 1700, 170 ),
                    new StockTick( "IBM", 1500, 240 )));
        } catch (JsonProcessingException e) {
            new RuntimeException(e);
        }

        given()
                .body(json)
                .contentType(ContentType.JSON)
                .when()
                .post("/stocks/3/batch")
                .then()
                .statusCode(200)
                .body(equalTo("3"));

        given()
                .when()
                .get("/stocks/3/highestDrop/IBM")
                .then()
                .statusCode(200)
                .body("dropAmount", equalTo(300));
    }
}
