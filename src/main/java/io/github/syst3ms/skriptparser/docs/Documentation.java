package io.github.syst3ms.skriptparser.docs;

/**
 * Represents the documentation for a syntax element
 */
public class Documentation {

    private boolean noDoc = false;
    private String name;
    private String[] description = new String[0];
    private String experimental = null;
    private String usage;
    private String[] examples = new String[0];
    private String since;

    public Documentation() {
    }

    public String getName() {
        return this.name;
    }

    public String[] getDescription() {
        return this.description;
    }

    public String getUsage() {
        return this.usage;
    }

    public String[] getExamples() {
        return this.examples;
    }

    public String getSince() {
        return this.since;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void noDoc() {
        this.noDoc = true;
    }

    public boolean isNoDoc() {
        return this.noDoc;
    }

    public void experimental() {
        this.experimental = "This syntax element is experimental and may change in the future, use with caution.";
    }

    public void experimental(String message) {
        this.experimental = message;
    }

    public boolean isExperimental() {
        return this.experimental != null;
    }

    public String getExperimentalMessage() {
        return this.experimental;
    }

    public void setDescription(String[] description) {
        this.description = description;
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }

    public void setExamples(String[] examples) {
        this.examples = examples;
    }

    public void setSince(String since) {
        this.since = since;
    }

}
