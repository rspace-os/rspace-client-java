package com.researchspace.api.client.examples;

import com.researchspace.api.client.ApiConnector;
import com.researchspace.api.clientmodel.User;
import org.apache.http.client.HttpResponseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Example code that finds documents using various criteria and search types,
 * also demonstrates how to navigate paginated results returned by the API.
 */
class GetUserInfoTest extends FixedIntervalTest {


    /**
     * General search for a particular phrase. Corresponds to Workspace 'All' search.
     */
    @Test
    void getsAllUserInfoWhenRunAsSysadmin() throws Exception {

        ApiConnector apiConnector = createApiConnector();

        Map<String, String> userAndKey = apiConnector.getUserNamesAndApiKeys(configuredApiKey);

        assertTrue(userAndKey.size() > 0);
    }

    @Test
    void getsUnauthorisedErrorWhenNotRunAsSysadmin() throws Exception {

        ApiConnector apiConnector = createApiConnector();

        HttpResponseException thrown =
                Assertions.assertThrows(HttpResponseException.class, () -> apiConnector.getUserNamesAndApiKeys("abcdefghijklmnop3"));
        assertEquals(401,thrown.getStatusCode());

    }

    /**
     * Get user object by username using getUserNamesAndApiKeys method
     */
    @Test
    void getsUserByUsername() throws Exception {

        ApiConnector apiConnector = createApiConnector();

        User user = apiConnector.getUserByUsername("sysadmin1", configuredApiKey);

        assertTrue(user.getUsername().equals("sysadmin1"));

    }

    /**
     * Get user object by username using getUserNamesAndApiKeys method username does not exist
     */
    @Test
    void getsUserByUsernameWhichDoesNotExistThrowsException() throws Exception {

        ApiConnector apiConnector = createApiConnector();

        assertThrows(HttpResponseException.class, () -> apiConnector.getUserByUsername("blahblah",configuredApiKey));

    }


}

