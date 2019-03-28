package xyz.opstack.jenkins.plugins.aliyun_cr_parameter;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;
import xyz.opstack.jenkins.plugins.aliyun_cr_parameter.GetRepoTagsResponse.TagInstance;

import java.util.ArrayList;
import java.util.List;

public class AliyunCrClient {

    @Getter
    private String accessKey;

    @Getter
    private String secretKey;

    @Getter
    private String regionId;

    @Getter
    IAcsClient client;

    public AliyunCrClient(String regionId, String accessKey, String secretKey) {

        this.regionId = regionId;

        if (StringUtils.isEmpty(StringUtils.trim(regionId))) {
            this.regionId = "cn-beijing";
        }

        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    public void Init() {
        DefaultProfile.addEndpoint(this.regionId, "cr", "cr."+ this.regionId +".aliyuncs.com");

        IClientProfile profile = DefaultProfile.getProfile(this.regionId, this.accessKey, this.secretKey);
        this.client = new DefaultAcsClient(profile);
    }

    public List<String> getTagList(String namespace, String repoName, String tagNameFilter, int listSize) {

        List<String> tagsList = new ArrayList<>();
        GetRepoTagsRequest request = new GetRepoTagsRequest();
        request.setRepoNamespace(namespace);
        request.setRepoName(repoName);
        request.setPageSize(50);

        try {
            GetRepoTagsResponse response = client.getAcsResponse(request);

            List<TagInstance> instances = response.getTagInstances();

            for (TagInstance instance: instances ) {

                if (instance.getStatus().equalsIgnoreCase("NORMAL") && instance.getTag().matches(tagNameFilter)) {
                    tagsList.add(instance.getTag());
                }
            }


        } catch (ClientException e) {
            e.printStackTrace();
        }

        if (tagsList.size() > listSize) {
            return tagsList.subList(0, listSize);
        }

        return tagsList;
    }

    public static void main(String[] args) {
    }
}
