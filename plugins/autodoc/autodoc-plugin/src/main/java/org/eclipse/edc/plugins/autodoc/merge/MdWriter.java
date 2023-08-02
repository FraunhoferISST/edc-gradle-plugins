package org.eclipse.edc.plugins.autodoc.merge;

import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

/**
 * A class that converts a list of objects to a Markdown table
 */
public class MdWriter extends DefaultTask {

    private static final String DEFAULT_MD_FILENAME = "manifest.md";
    private File destinationFile;
    private List<Map<String, Object>> objects;

    public MdWriter() {
        setDestinationFile(new File(getProject().getRootProject().getBuildDir(), DEFAULT_MD_FILENAME));
    }

    @TaskAction
    public void convertObjectsToMd() {
        if (objects == null || objects.isEmpty()) {
            throw new GradleException("The list of objects is empty!");
        }

        StringBuilder mdContent = new StringBuilder();

        // add table headers
        mdContent.append("| Name | Type | Overview | Class Name | Categories | Provides | References | Configuration | Version | Module Path |\n");
        mdContent.append("|------|------|----------|------------|------------|----------|------------|---------------| ---------------|---------------|\n");

        // iterate over each object, each object contains list of extensions
        for (Map<String, Object> object : objects) {
            List<Map<String, Object>> extensions = (List<Map<String, Object>>) object.get("extensions");
            String version = (String) object.get("version");
            String modulePath = (String) object.get("modulePath");

            for (Map<String, Object> extension : extensions) {
                mdContent.append("| ")
                        .append(sanitize(extension.get("name")))
                        .append(" | ")
                        .append(sanitize(extension.get("type")))
                        .append(" | ")
                        .append(sanitize(extension.get("overview")))
                        .append(" | ")
                        .append(sanitize(extension.get("className")))
                        .append(" | ")
                        .append(sanitize(extension.get("categories")))
                        .append(" | ")
                        .append(sanitize(extension.get("provides")))
                        .append(" | ")
                        .append(sanitize(extension.get("references")))
                        .append(" | ")
                        .append(sanitize(extension.get("configuration")))
                        .append(" | ")
                        .append(sanitize(version))
                        .append(" | ")
                        .append(sanitize(modulePath))
                        .append(" |\n");
            }
        }

        try {
            Files.writeString(destinationFile.toPath(), mdContent.toString());
            getProject().getLogger().lifecycle("Generated the .md file at {}", destinationFile.getAbsolutePath());
        } catch (IOException e) {
            throw new GradleException("Error writing MD file", e);
        }
    }

    /**
     * The destination file. By default, it is set to {@code <rootProject>/build/manifest.md}
     */
    @OutputFile
    public File getDestinationFile() {
        return destinationFile;
    }

    public void setDestinationFile(File destinationFile) {
        this.destinationFile = destinationFile;
    }

    /**
     * The list of objects to convert to markdown. This should be set by the JsonReader class.
     */
    @Input
    public List<Map<String, Object>> getObjects() {
        return objects;
    }

    public void setObjects(List<Map<String, Object>> objects) {
        this.objects = objects;
    }

    /**
     * A Method that converts the input object into a string representation
     * that does not contain any newline characters (\n) or pipe characters (|).
     */
    private String sanitize(Object input) {
        if (input == null) return "";
        String output = input.toString();
        output = output.replace("\n", " ");
        output = output.replace("|", "-");
        return output;
    }
}
