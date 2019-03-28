package xyz.opstack.jenkins.plugins.aliyun_cr_parameter;

import com.cloudbees.plugins.credentials.CredentialsMatchers;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.common.StandardCertificateCredentials;
import com.cloudbees.plugins.credentials.common.StandardCredentials;
import com.cloudbees.plugins.credentials.common.StandardListBoxModel;
import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;
import com.cloudbees.plugins.credentials.domains.DomainRequirement;
import com.cloudbees.plugins.credentials.domains.URIRequirementBuilder;
import hudson.Extension;
import hudson.Util;
import hudson.model.*;
import hudson.security.ACL;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import jenkins.model.Jenkins;
import lombok.Getter;
import lombok.Setter;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.*;
import org.kohsuke.stapler.export.Exported;

import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class AliyunCrParameterDefinition extends SimpleParameterDefinition {

    private static final Logger LOG = Logger.getLogger(AliyunCrParameterDefinition.class.getName());

    private static final int DEFAULT_LIST_SIZE = 5;
    private static final String DEFAULT_REGION_ID = "cn-beijing";
    private static final String DEFAULT_TAGNAME_FILTER = "\\d..*";

    @Getter
    private String regionId = DEFAULT_REGION_ID;

    @Getter
    private String credentialsId;

    @Getter
    private String namespace;

    @Getter
    private String repoName;

    @Getter
    private String tagNameFilter = DEFAULT_TAGNAME_FILTER;

    @Getter
    private int listSize = DEFAULT_LIST_SIZE;

    private final String defaultValue;

    @DataBoundConstructor
    public AliyunCrParameterDefinition(String name, String regionId, String credentialsId, String defaultValue,
                                       String namespace, String repoName, String tagNameFilter, int listSize, String description) {
        super(name, description);
        this.defaultValue = defaultValue;
        this.credentialsId = credentialsId;
        this.namespace = namespace;
        this.repoName = repoName;
        this.listSize = listSize;

        setTagNameFilter(tagNameFilter);
        setRegionId(regionId);
    }

    public void setTagNameFilter(String tagNameFilter) {
        if (StringUtils.isEmpty(StringUtils.trim(tagNameFilter))) {
            tagNameFilter = DEFAULT_TAGNAME_FILTER;
        }
        this.tagNameFilter = tagNameFilter;
    }

    public void setRegionId(String regionId) {
        if (StringUtils.isEmpty(StringUtils.trim(regionId))) {
            regionId = DEFAULT_REGION_ID;
        }
        this.regionId = regionId;
    }

    @Exported
    public List<String> getChoices() {
        AliyunCrClient client = createCrClient();
        return client.getTagList(this.namespace, this.repoName, this.tagNameFilter, this.listSize);
    }

    private AliyunCrClient createCrClient() {

        StandardCredentials credentials = getCredentials();

        AliyunCrClient client = new AliyunCrClient(this.regionId,
                ((StandardUsernamePasswordCredentials)credentials).getUsername(),
                ((StandardUsernamePasswordCredentials)credentials).getPassword().getPlainText());

        client.Init();

        return client;
    }
    private StandardCredentials getCredentials() {
        return CredentialsMatchers.firstOrNull(
                CredentialsProvider.lookupCredentials(StandardCredentials.class,
                        Jenkins.getInstance(), ACL.SYSTEM, Collections.emptyList()),
                CredentialsMatchers.withId(credentialsId)
        );
    }

    @Override
    public StringParameterValue getDefaultParameterValue() {
        List<String> tags = getChoices();
        if (tags.size() == 0) {
            throw new IllegalArgumentException("Illegal default must not empty" );
        }

        boolean isNull = StringUtils.isEmpty(StringUtils.trim(defaultValue));
        return new StringParameterValue(getName(), isNull? tags.get(0): defaultValue, getDescription());
    }

    private StringParameterValue checkValue(StringParameterValue value) {
        List<String> choices = getChoices();
        if (!choices.contains(value.getValue())) {
            throw new IllegalArgumentException("Illegal choice for parameter " + getName() + ": " + value.getValue());
        }
        return value;
    }

    @Override
    public ParameterValue createValue(StaplerRequest req, JSONObject jo) {
        StringParameterValue value = req.bindJSON(StringParameterValue.class, jo);
        value.setDescription(getDescription());
        return checkValue(value);
    }

    public StringParameterValue createValue(String value) {
        return checkValue(new StringParameterValue(getName(), value, getDescription()));
    }

    @Extension
    @Symbol({"aliyunCrParam"})
    public static class DescriptorImpl extends ParameterDescriptor {
        @Override
        public String getDisplayName() {
            return Messages.AliyunCrParameterDefinition_DisplayName();
        }

        public ListBoxModel doFillCredentialsIdItems(@AncestorInPath Item context, @QueryParameter String remote) {
            if (context == null || !context.hasPermission(Item.EXTENDED_READ)) {
                return new StandardListBoxModel();
            }
            return fillCredentialsIdItems(context, remote);
        }


        public ListBoxModel fillCredentialsIdItems(Item context, String remote) {
            List<DomainRequirement> domainRequirements;
            if (remote == null) {
                domainRequirements = Collections.emptyList();
            } else {
                domainRequirements = URIRequirementBuilder.fromUri(remote.trim()).build();
            }

            return new StandardListBoxModel()
                    .includeEmptyValue()
                    .withMatching(
                            CredentialsMatchers.anyOf(
                                    CredentialsMatchers.instanceOf(StandardUsernamePasswordCredentials.class),
                                    CredentialsMatchers.instanceOf(StandardCertificateCredentials.class)
                            ),
                            CredentialsProvider.lookupCredentials(StandardCredentials.class,
                                    context,
                                    ACL.SYSTEM,
                                    domainRequirements)
                    );
        }

        public FormValidation doCheckRegionId(StaplerRequest req, @AncestorInPath Item context, @QueryParameter String value) {
            String regionId = Util.fixEmptyAndTrim(value);

            if (regionId == null) {
                return FormValidation.error("Region Id is required");
            }

            return FormValidation.ok();
        }

        public FormValidation doCheckListSize(@QueryParameter String listSize) {
            if(isInteger(listSize)) {
                return FormValidation.ok();
            } else {
                return FormValidation.error("Current value is not integer");
            }
        }

        boolean isInteger(String s) {
            try {
                Integer.parseInt(s);
            } catch(NumberFormatException | NullPointerException e) {
                return false;
            }
            return true;
        }

        public FormValidation doCheckTagNameFilter(@QueryParameter String value) {
            String errorMessage = "Invalid Branch Filter Pattern";
            return validateRegularExpression(value, errorMessage);
        }

        private FormValidation validateRegularExpression(String value, String errorMessage) {
            try {
                Pattern.compile(value);
            } catch (PatternSyntaxException e) {
                LOG.log(Level.WARNING, errorMessage, e);
                return FormValidation.error(errorMessage);
            }
            return FormValidation.ok();
        }
    }
}
