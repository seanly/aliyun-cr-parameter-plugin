package xyz.opstack.jenkins.plugins.aliyun_cr_parameter;

import com.aliyuncs.transform.UnmarshallerContext;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class GetRepoTagsResponse extends com.aliyuncs.cr.model.v20160607.GetRepoTagsResponse {

    @Setter
    @Getter
    private String requestId;

    @Setter
    @Getter
    private Integer page;

    @Getter
    @Setter
    private Integer pageSize;

    @Getter
    @Setter
    private Integer totalCount;

    @Getter
    @Setter
    private List<TagInstance> tagInstances;

    public static class TagInstance {

        @Getter
        @Setter
        private Long imageUpdate;

        @Getter
        @Setter
        private Long imageCreate;

        @Getter
        @Setter
        private String imageId;

        @Getter
        @Setter
        private String digest;

        @Getter
        @Setter
        private Long imageSize;

        @Getter
        @Setter
        private String tag;

        @Getter
        @Setter
        private String status;
    }

    @Override
    public GetRepoTagsResponse getInstance(UnmarshallerContext context) {
        return	GetRepoTagsResponseUnmarshaller.unmarshall(this, context);
    }
}
