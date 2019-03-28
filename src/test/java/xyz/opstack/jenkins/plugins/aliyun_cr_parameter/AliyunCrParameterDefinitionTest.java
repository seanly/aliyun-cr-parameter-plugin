package xyz.opstack.jenkins.plugins.aliyun_cr_parameter;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class AliyunCrParameterDefinitionTest {

    @Test
    public void testSimpleConfiguration() throws Exception{


        System.out.println("--//DEBUG----");

        AliyunCrClient client = new AliyunCrClient("registry.cn-beijing.aliyuncs.com", "test", "test");
        client.Init();

    }
}
