package xyz.opstack.jenkins.plugins.aliyun_cr_parameter;

import com.aliyuncs.transform.UnmarshallerContext;
import xyz.opstack.jenkins.plugins.aliyun_cr_parameter.GetRepoTagsResponse.TagInstance;

import java.util.ArrayList;
import java.util.List;

public class GetRepoTagsResponseUnmarshaller {

    public static GetRepoTagsResponse unmarshall(GetRepoTagsResponse getRepoTagsResponse, UnmarshallerContext context) {

        getRepoTagsResponse.setRequestId(context.stringValue("GetRepoTagsResponse.requestId"));
        getRepoTagsResponse.setTotalCount(context.integerValue("GetRepoTagsResponse.data.total"));
        getRepoTagsResponse.setPage(context.integerValue("GetRepoTagsResponse.data.page"));
        getRepoTagsResponse.setPageSize(context.integerValue("GetRepoTagsResponse.data.pageSize"));

        List<TagInstance> instances = new ArrayList<>();

        for (int i = 0; i < context.lengthValue("GetRepoTagsResponse.data.Length"); i++) {

            TagInstance instance = new TagInstance();

            instance.setTag(context.stringValue("GetRepoTagsResponse.data[" + i + "].tag"));
            instance.setDigest(context.stringValue("GetRepoTagsResponse.data[" + i + "].digest"));
            instance.setImageCreate(context.longValue("GetRepoTagsResponse.data[" + i + "].imageCreate"));
            instance.setImageUpdate(context.longValue("GetRepoTagsResponse.data[" + i + "].imageUpdate"));
            instance.setStatus(context.stringValue("GetRepoTagsResponse.data[" + i + "].status"));
            instance.setImageId(context.stringValue("GetRepoTagsResponse.data[" + i + "].imageId"));
            instance.setImageSize(context.longValue("GetRepoTagsResponse.data[" + i + "].imageSize"));

            instances.add(instance);

        }

        getRepoTagsResponse.setTagInstances(instances);

        return getRepoTagsResponse;
    }
}
