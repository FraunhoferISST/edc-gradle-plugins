package org.eclipse.edc.plugins.autodoc.merge;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.gradle.api.GradleException;
import org.gradle.api.Project;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * A class that reads a JSON file and creates a list of objects
 */
public class JsonReader {

    private static final String MERGED_MANIFEST_FILENAME = "manifest.json";
    private final ObjectMapper mapper;
    private File jsonFile;

    public JsonReader(Project project) {
        mapper = new ObjectMapper();
        jsonFile = new File(project.getRootProject().getBuildDir(), MERGED_MANIFEST_FILENAME);
    }

    public List<Map<String, Object>> readJson() {
        if (!jsonFile.exists()) {
            throw new GradleException("The JSON file does not exist!");
        }

        try {
            return mapper.readValue(jsonFile, new TypeReference<List<Map<String, Object>>>() {});
        } catch (IOException e) {
            throw new GradleException("Error reading JSON file", e);
        }
    }
}