package xyz.opstack.jenkins.plugins.aliyun_cr_parameter;

import com.aliyuncs.RoaAcsRequest;
import com.aliyuncs.http.MethodType;

public class GetRepoTagsRequest extends RoaAcsRequest<GetRepoTagsResponse> {

    public GetRepoTagsRequest() {
        super("cr", "2016-06-07", "GetRepoTags", "cr");
        setUriPattern("/repos/[RepoNamespace]/[RepoName]/tags");
        setMethod(MethodType.GET);
    }

    private String repoNamespace;

    private String repoName;

    private Integer pageSize;

    private Integer page;

    public String getRepoNamespace() {
        return this.repoNamespace;
    }

    public void setRepoNamespace(String repoNamespace) {
        this.repoNamespace = repoNamespace;
        if(repoNamespace != null){
            putPathParameter("RepoNamespace", repoNamespace);
        }
    }

    public String getRepoName() {
        return this.repoName;
    }

    public void setRepoName(String repoName) {
        this.repoName = repoName;
        if(repoName != null){
            putPathParameter("RepoName", repoName);
        }
    }

    public Integer getPageSize() {
        return this.pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
        if(pageSize != null){
            putQueryParameter("PageSize", pageSize.toString());
        }
    }

    public Integer getPage() {
        return this.page;
    }

    public void setPage(Integer page) {
        this.page = page;
        if(page != null){
            putQueryParameter("Page", page.toString());
        }
    }

    @Override
    public Class<GetRepoTagsResponse> getResponseClass() {
        return GetRepoTagsResponse.class;
    }
}
