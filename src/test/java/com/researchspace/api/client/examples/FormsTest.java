package com.researchspace.api.client.examples;


import com.researchspace.api.client.ApiConnector;
import com.researchspace.api.clientmodel.FormInfo;
import com.researchspace.api.clientmodel.FormPost;
import org.apache.http.client.HttpResponseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FormsTest extends FixedIntervalTest {

    private static final Logger log = LoggerFactory
            .getLogger(FormsTest.class);

    @Test
    void createForm() throws IOException, URISyntaxException {
        ApiConnector apiConnector = createApiConnector();
        FormPost.Form formToCreate = createFormPostForm();
        FormInfo createdForm = apiConnector.createForm(formToCreate,configuredApiKey);
        assertNotNull(createdForm);
        assertEquals("formName",createdForm.getName());
    }

    @Test
    void updateForm() throws IOException {
        ApiConnector apiConnector = createApiConnector();
        FormPost.Form formToCreate = createFormPostForm();
        FormInfo createdForm = apiConnector.createForm(formToCreate,configuredApiKey);
        formToCreate.setId(createdForm.getId());
        assertEquals("formName",createdForm.getName());
        formToCreate.setName("updatedForm");
        FormInfo updatedForm =  apiConnector.updateForm(formToCreate, configuredApiKey);
        assertTrue(updatedForm.getId().equals(createdForm.getId()));
        assertTrue(updatedForm.getName().equals("updatedForm"));
    }

    @Test
    public void deleteForm() throws IOException {
        ApiConnector apiConnector = createApiConnector();
        FormPost.Form formToCreate = createFormPostForm();
        FormInfo createdForm = apiConnector.createForm(formToCreate,configuredApiKey);
        assertTrue(apiConnector.deleteForm(createdForm,configuredApiKey));
    }

    @Test
    public void setIconOnForm() throws IOException {
        ApiConnector apiConnector = createApiConnector();
        FormPost.Form formToCreate = createFormPostForm();
        FormInfo createdForm = apiConnector.createForm(formToCreate,configuredApiKey);
        File iconFile = new File("src/test/resources/camera.png");
        FormInfo response = apiConnector.setIconOnForm(createdForm,iconFile , configuredApiKey);
        assertEquals(response.getId(),createdForm.getId());
    }

    @Test
    public void publishForm() throws IOException {
        ApiConnector apiConnector = createApiConnector();
        FormPost.Form formToCreate = createFormPostForm();
        FormInfo createdForm = apiConnector.createForm(formToCreate,configuredApiKey);
        FormInfo publishedForm = apiConnector.publishForm(createdForm,configuredApiKey);
        assertEquals(publishedForm.getId(),createdForm.getId());
    }

    @Test
    public void globalShareForm() throws IOException {
        ApiConnector apiConnector = createApiConnector();
        FormPost.Form formToCreate = createFormPostForm();
        FormInfo createdForm = apiConnector.createForm(formToCreate,configuredApiKey);
        FormInfo shared = apiConnector.globalShareForm(createdForm,configuredApiKey);
        assertEquals(shared.getId(),createdForm.getId());
    }
    @Test
    public void globalShareFormNotAuthorisedForUserThatIsNotSysadmin() throws Exception {
        ApiConnector apiConnector = createApiConnector();
        FormPost.Form formToCreate = createFormPostForm();
        Map<String, String> userAndKey = apiConnector.getUserNamesAndApiKeys(configuredApiKey);
        userAndKey.remove("sysadmin1");
        String nonSyadminKey = userAndKey.entrySet().iterator().next().getValue();
        FormInfo createdForm = apiConnector.createForm(formToCreate,nonSyadminKey);
        FormInfo groupShared = apiConnector.groupShareForm(createdForm,nonSyadminKey);
        assertEquals(groupShared.getId(),createdForm.getId());
        HttpResponseException thrown =
                Assertions.assertThrows(HttpResponseException.class, () -> apiConnector.globalShareForm(createdForm,nonSyadminKey));
        assertEquals(401,thrown.getStatusCode());
    }

    @Test
    public void groupShareForm() throws IOException {
        ApiConnector apiConnector = createApiConnector();
        FormPost.Form formToCreate = createFormPostForm();
        FormInfo createdForm = apiConnector.createForm(formToCreate,configuredApiKey);
        FormInfo shared = apiConnector.groupShareForm(createdForm,configuredApiKey);
        assertEquals(shared.getId(),createdForm.getId());
    }

    private FormPost.Form createFormPostForm() {
        List<String> choices = Arrays.asList(new String [] {"a","b","c"});
        List<String> defaultChoices = Arrays.asList(new String [] {"a","b"});
        List<String> radios = Arrays.asList(new String [] {"x","y","z"});
        String defaultRadio= "z";
        return  FormPost.Form.builder().name("formName").tags("a,b,c")
                .field(FormPost.NumberFieldPost.builder().name("numberField").min(0d).max(10d).defaultValue(4d).build())
                .field(FormPost.DateFieldPost.builder().name("dateName").min(new Date()).build())
                .field(FormPost.StringFieldPost.builder().name("name").defaultValue("defaut string").build())
                .field(FormPost.TextFieldPost.builder().name("text-name").defaultValue("<em>html</em").build())
                .field(FormPost.ChoiceFieldPost.builder().name("choices")
                        .multipleChoice(true).options(choices)
                        .defaultOptions(defaultChoices).build())
                .field(FormPost.RadioFieldPost.builder()
                        .name("radios").options(radios)
                        .defaultOption(defaultRadio).build())
                .build();
//        ObjectMapper reader = new ObjectMapper();
//        System.err.println(reader.writeValueAsString(toSubmit));
//        return FormPost.Form.builder().name("name").tags("dd").field(
//                FormPost.TextFieldPost.builder().name("fieldName")
//                        .defaultValue("a test default value").build()).build();
    }

}

