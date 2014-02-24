package com.codenvy.factory.storage.mongo;

import com.codenvy.api.factory.dto.Replacement;
import com.codenvy.api.factory.dto.Variable;
import com.codenvy.dto.server.DtoFactory;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;

import org.everrest.core.impl.provider.json.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.util.*;

/** Tests for {@link com.codenvy.factory.storage.mongo.VariableHelper} */
public class VariableHelperTest {
    @Test
    public void testFromBasicDBFormat() throws Exception {
        List<Variable> variables = new ArrayList<>();
        Replacement replacement = DtoFactory.getInstance().createDto(Replacement.class);
        replacement.setFind("findText");
        replacement.setReplace("replaceText");
        replacement.setReplacemode("text_multipass");

        Variable variable = DtoFactory.getInstance().createDto(Variable.class);
        variable.setFiles(Collections.singletonList("glob_pattern"));
        variable.setEntries(Collections.singletonList(replacement));

        variables.add(variable);

        BasicDBList basicDBVariables = VariableHelper.toBasicDBFormat(variables);
        BasicDBObject factoryURLbuilder = new BasicDBObject();
        factoryURLbuilder.put("variables", basicDBVariables);

        List<Variable> result = VariableHelper.fromBasicDBFormat(factoryURLbuilder);

        Assert.assertEquals(variables, result);
    }

    @Test
    public void testToBasicDBFormat() throws Exception {
        List<Variable> variables = new ArrayList<>();

        Replacement replacement = DtoFactory.getInstance().createDto(Replacement.class);
        replacement.setFind("findText");
        replacement.setReplace("replaceText");
        replacement.setReplacemode("text_multipass");

        Variable variable = DtoFactory.getInstance().createDto(Variable.class);
        variable.setFiles(Collections.singletonList("glob_pattern"));
        variable.setEntries(Collections.singletonList(replacement));

        variables.add(variable);

        BasicDBList basicDBVariables = VariableHelper.toBasicDBFormat(variables);
        BasicDBObjectBuilder factoryURLbuilder = new BasicDBObjectBuilder();
        factoryURLbuilder.add("variables", basicDBVariables);

        JsonParser jsonParser = new JsonParser();
        jsonParser.parse(new ByteArrayInputStream(factoryURLbuilder.get().toString().getBytes("UTF-8")));
        JsonValue jsonValue = jsonParser.getJsonObject().getElement("variables");
        Assert.assertTrue(jsonValue.isArray());
        ArrayValue variablesArr = (ArrayValue)jsonValue;

        List<Variable> result = new ArrayList<>();
        List<String> resultFiles = new ArrayList<>();
        List<Replacement> resultReplacements = new ArrayList<>();

        Iterator<JsonValue> iterator = variablesArr.getElements();
        while (iterator.hasNext()) {
            resultFiles.clear();
            resultReplacements.clear();
            ObjectValue o = (ObjectValue)iterator.next();

            ArrayValue files = (ArrayValue)o.getElement("files");
            Iterator<JsonValue> filesIterator = files.getElements();
            while (filesIterator.hasNext()) {
                JsonValue fileValue = filesIterator.next();
                if (fileValue.isString()) {
                    resultFiles.add(fileValue.getStringValue());
                }
            }

            ArrayValue entries = (ArrayValue)o.getElement("entries");
            Iterator<JsonValue> entriesIterator = entries.getElements();
            while (entriesIterator.hasNext()) {
                JsonValue entryValue = entriesIterator.next();
                if (entryValue.isObject()) {
                    Replacement tempReplacement = DtoFactory.getInstance().createDto(Replacement.class);
                    tempReplacement.setFind(entryValue.getElement("find").getStringValue());
                    tempReplacement.setReplace(entryValue.getElement("replace").getStringValue());
                    tempReplacement.setReplacemode(entryValue.getElement("replacemode").getStringValue());
                    resultReplacements.add(tempReplacement);
                }
            }

            Variable iteratorVariable = DtoFactory.getInstance().createDto(Variable.class);
            iteratorVariable.setFiles(resultFiles);
            iteratorVariable.setEntries(resultReplacements);
            result.add(iteratorVariable);
        }

        Assert.assertEquals(variables, result);
    }
}
