import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;
import java.util.logging.Logger;

import static java.lang.String.format;
import static java.lang.String.valueOf;
import static java.lang.System.getProperty;
import static java.util.logging.Logger.getLogger;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

public class SearchEndpointTest {
    private static final Logger logger = getLogger(SearchEndpointTest.class.getName());
    private GiphyApi giphyApi;
    private String query;
    private String responseMessage;
    private String actualMessage;

    @BeforeMethod
    public void initApi() {
        giphyApi = new GiphyApi();
        query = getProperty("query", "cat");
    }

    @Test
    public void testPositiveRequiredParameters() {
        giphyApi.setQuery(query)
                .sendRequest();
        logger.info("Successful request");
    }

    @Test
    public void testIncorrectApiKey() {
        responseMessage = "Invalid authentication credentials";
        actualMessage = giphyApi.setQuery(query)
                .setApiKey("11111")
                .sendInvalidApiRequest();
        checkResponseMessage();
    }

    @Test
    public void testMissingApiKey() {
        responseMessage = "No API key found in request";
        actualMessage = giphyApi.sendNoApiKeyRequest();
        checkResponseMessage();
    }

    @Test
    public void testEmptySearchQuery() {
        List response = giphyApi.setQuery("")
                .sendRequest();
        logger.info("Check response data json is empty");
        assertEquals(response.size(), 0);
    }

    @Test
    public void testDefaultResponseSize() {
        List response = giphyApi.setQuery(query)
                .sendRequest();
        logger.info("Check response default size = 25");
        assertEquals(response.size(), 25);
    }

    @Test
    public void testLimitAndOffset() {
        List response = giphyApi.setQuery(query)
                .sendRequestAndGetResponseIds();

        int limit = 5;
        int offset = 3;
        List responseWithOffset = giphyApi.setLimit(valueOf(limit))
                .setOffset(valueOf(offset))
                .sendRequestAndGetResponseIds();
        assertEquals(responseWithOffset.size(), limit);

        response = response.subList(offset, offset + limit);
        logger.info("Compare received responses");
        assertEquals(response, responseWithOffset);
    }

    @Test(dataProvider = "query-parameters", dataProviderClass = SearchProvider.class)
    public void testQueryParameter(String description, String parameter) {
        logger.info(format("Search parameter: %s", description));
        List response = giphyApi.setQuery(parameter)
                .sendRequest();
        logger.info("Check response data is not empty");
        assertFalse(response.isEmpty());
    }

    @Test(dataProvider = "language-parameters", dataProviderClass = SearchProvider.class)
    public void testLanguageParameter(String word, String language, boolean emptyList) {
        logger.info(format("Search parameter: %s; language: %s", word, language));
        List response = giphyApi.setQuery(word)
                .setLanguage(language)
                .sendRequest();
        logger.info(format("Check response data is %sempty", emptyList ? "" : "not "));
        assertEquals(response.isEmpty(), emptyList);
    }

    private void checkResponseMessage() {
        logger.info(format("Check response message is [%s]", responseMessage));
        assertEquals(actualMessage, responseMessage);
    }
}
