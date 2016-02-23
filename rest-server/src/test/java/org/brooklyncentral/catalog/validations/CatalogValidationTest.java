package org.brooklyncentral.catalog.validations;
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

import org.apache.brooklyn.test.Asserts;
import org.testng.annotations.Test;

public class CatalogValidationTest {
    private String CATALOG_URL = "https://github.com/brooklyncentral/brooklyn-community-catalog";
    private String NOT_VALID_CATALOG_URL = "https://github.com/iyovcheva/brooklyn-community-catalog";

    private String CATALOG_ITEM_URL = "https://github.com/cloudsoft/jmeter-entity";
    private String NOT_VALID_CATALOG_ITEM_URL = "https://github.com/cloudsoft/bower-cloudsoft-ui-common";

    @Test
    public void testCatalogValidation() {
        Asserts.assertTrue(CatalogValidator.validate(CATALOG_URL));
        Asserts.assertFalse(CatalogValidator.validate(NOT_VALID_CATALOG_URL), "Not valid catalog was successfully validated");
    }

    @Test
    public void testSingleCatalogValidation() {
        Asserts.assertTrue(CatalogValidator.validateCatalogItem(CATALOG_ITEM_URL));
        Asserts.assertFalse(CatalogValidator.validateCatalogItem(NOT_VALID_CATALOG_ITEM_URL), "Not valid catalog item was successfully validated");
    }
}
