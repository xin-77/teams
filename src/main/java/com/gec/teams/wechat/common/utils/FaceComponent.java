package com.gec.teams.wechat.common.utils;

import cn.hutool.core.util.StrUtil;
import com.gec.teams.wechat.exception.TeamsException;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.iai.v20180301.IaiClient;
import com.tencentcloudapi.iai.v20180301.models.*;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Encoder;

import java.io.IOException;

@Data
@Component
public class FaceComponent {
    @Value("${teams.tencentcloud.secretId}")
    private String secretId;
    @Value("${teams.tencentcloud.secretKey}")
    private String secretKey;
    @Value("${teams.tencentcloud.groupId}")
    private String groupId;

    //创建人员
    public Boolean createUser( String username, String userId, MultipartFile file){
        try{
            // 实例化一个认证对象，入参需要传入腾讯云账户secretId，secretKey,此处还需注意密钥对的保密
            // 密钥可前往https://console.cloud.tencent.com/cam/capi网站进行获取
            Credential cred = new Credential(secretId, secretKey);
            // 实例化一个http选项，可选的，没有特殊需求可以跳过
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("iai.tencentcloudapi.com");
            // 实例化一个client选项，可选的，没有特殊需求可以跳过
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            // 实例化要请求产品的client对象,clientProfile是可选的
            IaiClient client = new IaiClient(cred, "ap-guangzhou", clientProfile);
            // 实例化一个请求对象,每个接口都会对应一个request对象
            CreatePersonRequest req = new CreatePersonRequest();
            req.setGroupId(groupId);
            req.setPersonName(username);
            req.setPersonId(userId);

            //设置图片base64编码
            BASE64Encoder encode = new BASE64Encoder();
            String imgStr = encode.encode(file.getBytes());
            req.setImage(imgStr);
            // 返回的resp是一个CreatePersonResponse的实例，与请求对象对应
            CreatePersonResponse resp = client.CreatePerson(req);
            String faceId = resp.getFaceId();
            if(StrUtil.isBlank(faceId)){
                return false;
            }else {
                return true;
            }
        } catch (TencentCloudSDKException e) {
            throw new TeamsException(e.getMessage());
        } catch (IOException e) {
            throw new TeamsException("图片IO错误");
        }
    }

    //人员查询信息
    public Boolean getUserInfo(String userId){
        try{
            // 实例化一个认证对象，入参需要传入腾讯云账户secretId，secretKey,此处还需注意密钥对的保密
            // 密钥可前往https://console.cloud.tencent.com/cam/capi网站进行获取
            Credential cred = new Credential(secretId, secretKey);
            // 实例化一个http选项，可选的，没有特殊需求可以跳过
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("iai.tencentcloudapi.com");
            // 实例化一个client选项，可选的，没有特殊需求可以跳过
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            // 实例化要请求产品的client对象,clientProfile是可选的
            IaiClient client = new IaiClient(cred, "ap-guangzhou", clientProfile);
            // 实例化一个请求对象,每个接口都会对应一个request对象
            GetPersonBaseInfoRequest req = new GetPersonBaseInfoRequest();
            req.setPersonId(userId);
            // 返回的resp是一个GetPers
            // onBaseInfoResponse的实例，与请求对象对应
            GetPersonBaseInfoResponse resp = client.GetPersonBaseInfo(req);
            // 输出json格式的字符串回包
            String personName = resp.getPersonName();
            if(StrUtil.isBlank(personName)){
                return false;
            }else {
                return true;
            }
        } catch (TencentCloudSDKException e) {
            throw new TeamsException(e.getMessage());
        }
    }

    //人员验证
    public VerifyPersonResponse verifyUser(String userId, MultipartFile file) {
        try{
            // 实例化一个认证对象，入参需要传入腾讯云账户secretId，secretKey,此处还需注意密钥对的保密
            // 密钥可前往https://console.cloud.tencent.com/cam/capi网站进行获取
            Credential cred = new Credential(secretId, secretKey);
            // 实例化一个http选项，可选的，没有特殊需求可以跳过
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("iai.tencentcloudapi.com");
            // 实例化一个client选项，可选的，没有特殊需求可以跳过
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            // 实例化要请求产品的client对象,clientProfile是可选的
            IaiClient client = new IaiClient(cred, "ap-guangzhou", clientProfile);
            // 实例化一个请求对象,每个接口都会对应一个request对象
            VerifyPersonRequest req = new VerifyPersonRequest();
            //设置图片base64编码
            BASE64Encoder encode = new BASE64Encoder();
            String imgStr = encode.encode(file.getBytes());
            req.setImage(imgStr);
            req.setPersonId(userId);
            // 返回的resp是一个VerifyPersonResponse的实例，与请求对象对应
            // 输出json格式的字符串回包
            return  client.VerifyPerson(req);
        } catch (TencentCloudSDKException e) {
            throw new TeamsException(e.getMessage());
        } catch (IOException e) {
            throw new TeamsException("图片IO错误");
        }
    }
}