package com.adyl.logistics.framework.auth.controller;

import com.adyl.logistics.framework.api.exceptions.ServiceException;
import com.adyl.logistics.framework.api.utils.BeanChangeUtils;
import com.adyl.logistics.framework.api.constant.CacheKey;
import com.adyl.logistics.framework.api.controller.BaseController;
import com.adyl.logistics.framework.api.controller.ResultData;
import com.adyl.logistics.framework.api.dto.TokenDto;
import com.adyl.logistics.framework.api.dto.UserDto;
import com.adyl.logistics.framework.api.jwt.JWTConstant;
import com.adyl.logistics.framework.api.jwt.JWTResult;
import com.adyl.logistics.framework.api.jwt.JWTUtils;
import com.adyl.logistics.framework.api.utils.CacheUtils;
import com.adyl.logistics.framework.api.utils.RequestUtils;
import com.adyl.logistics.framework.auth.dto.*;
import com.adyl.logistics.framework.core.utils.LogUtils;
import com.adyl.logistics.framework.core.utils.Tools;
import com.adyl.logistics.framework.message.api.constant.MessageTypeConstant;
import com.adyl.logistics.framework.message.api.constant.PushTypeConstant;
import com.adyl.logistics.framework.message.api.dto.PushMessage;
import com.adyl.logistics.framework.message.api.feign.MessageFeign;
import com.adyl.logistics.framework.message.client.sms.SmsCommonClient;
import com.adyl.logistics.framework.message.client.utils.SmsUtils;
import com.adyl.logistics.permission.api.dto.AccountInfoDto;
import com.adyl.logistics.permission.api.dto.QueryAccountInfoDto;
import com.adyl.logistics.permission.api.dto.QueryUserLogin;
import com.adyl.logistics.permission.api.service.feign.PermissionMFeignClient;
import com.adyl.logistics.platform.api.dto.EnterpriseDto;
import com.adyl.logistics.platform.api.dto.PlatformAppDto;
import com.adyl.logistics.platform.api.dto.QueryEnterpriseDto;
import com.adyl.logistics.platform.api.dto.QueryPlatformAppDto;
import com.adyl.logistics.platform.service.PlatformAppService;
import com.adyl.logistics.platform.service.PlatformEnterpriseService;
import com.adyl.logistics.user.api.user.usermanage.dto.UserSuperviseInfoDto;
import com.adyl.logistics.user.api.user.usermanage.service.UserManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 授权，验证操作入口
 *
 * @author Dengb
 * @date 20180822
 */
@RestController
@RequestMapping(value = "/auth", produces = "application/json;charset=utf-8")
public class AuthController extends BaseController {
    @Autowired
    private PermissionMFeignClient permissionFeign;
    @Autowired
    private MessageFeign messageFeign;
    @Autowired
    private SmsCommonClient smsCommonClient;
    @Autowired
    private PlatformAppService platformAppService;
    @Autowired
    private UserManageService userManageService;
    @Autowired
    private PlatformEnterpriseService platformEnterpriseService;

    /**
     * 用户注册
     * @param registerRequest
     * @return
     */
    @RequestMapping("/register")
    public ResultData register(@RequestBody RegisterRequest registerRequest) {
        // 参数验证
        validatorDto(registerRequest, ErrorConstant.Error_Code_02);
        // 验证短信验证码是否正确
        try {
            SmsUtils.verifyValidCode(registerRequest.getMobilePhone(), registerRequest.getValidCode());
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException("81004", e.getMessage());
        }
        // 查询手机号是否已注册
        QueryAccountInfoDto queryAccountInfoDto = new QueryAccountInfoDto();
        queryAccountInfoDto.setAccountName(registerRequest.getMobilePhone());
        ResultData<List<AccountInfoDto>> resultData = permissionFeign.getAccountInfo(queryAccountInfoDto);
        if (!ResultData.isSuccess(resultData)) {
            return ResultData.newFailure(resultData.getCode(), resultData.getMsg());
        }
        if (resultData.getData() != null && resultData.getData().size() > 0) {
            return ResultData.newFailure("80304", "账号已存在，请直接登录");
        }
        // 构造跟登录一样的结构响应
        LoginResponse loginResponse = new LoginResponse();
        // 未注册的情况下，注册账号
        UserSuperviseInfoDto userSuperviseInfoDto = new UserSuperviseInfoDto();
        userSuperviseInfoDto.setUserCellPhone(registerRequest.getMobilePhone());
        userSuperviseInfoDto.setUserName(registerRequest.getMobilePhone());
        userSuperviseInfoDto.setUserStatus((byte) 1);
        userSuperviseInfoDto.setAccountType((byte) 0);
        // 这个时候没有appid，先模拟一个
        userSuperviseInfoDto.setOwnerAppid(UserSuperviseInfoDto.TEMP_APPID);
        ResultData<Map> addResultData = userManageService.addUserSuperviseInfo(userSuperviseInfoDto);
        if (!ResultData.isSuccess(addResultData)) {
            return ResultData.newFailure(addResultData.getCode(), addResultData.getMsg());
        }
        userSuperviseInfoDto.setAccountId(Tools.toString(addResultData.getData().get("accountId")));
        userSuperviseInfoDto.setUserId(Tools.toLong(addResultData.getData().get("userid")));
        // 注册成功获取用户信息
        UserDto userInfo = BeanChangeUtils.toBean(userSuperviseInfoDto, UserDto.class);
        loginResponse.setUser(userInfo);
        // 创建Token
        TokenDto tokenDto = createToken(userInfo);
        loginResponse.setToken(tokenDto);
        // 放入缓存
        CacheUtils.cacheLogin(tokenDto, userInfo, null);
        // 成功
        return ResultData.newSuccess(loginResponse);
    }

    /**
     * 登录授权，获取Token
     *
     * @param request
     * @param loginRequest
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/login")
    public ResultData login(HttpServletRequest request, @RequestBody LoginRequest loginRequest) throws Exception {
        // 参数验证
        validatorDto(loginRequest, ErrorConstant.Error_Code_02);
        // 登录响应对象
        LoginResponse loginResponse = new LoginResponse();
        // 登录校验
        UserDto userInfo = userLogin(loginRequest);
        // 获取企业信用代码和名称
        if (0 == userInfo.getAccountType()) {
            // 还是未知账号的情况，通过账号获取企业相关信息
            QueryEnterpriseDto queryEnterpriseDto = new QueryEnterpriseDto();
            queryEnterpriseDto.setCreateAccountId(userInfo.getAccountId());
            ResultData<EnterpriseDto> resultData = platformEnterpriseService.getPlatformEnterprise(queryEnterpriseDto);
            if (ResultData.isSuccess(resultData) && resultData.getData() != null) {
                userInfo.setEnterpriseLicense(resultData.getData().getEnterpriseLicense());
                userInfo.setEnterpriseName(resultData.getData().getEnterpriseName());
            }
        } else {
            QueryPlatformAppDto queryPlatformAppDto = new QueryPlatformAppDto();
            queryPlatformAppDto.setAppAppId(userInfo.getOwnerAppid());
            ResultData<PlatformAppDto> resultData = platformAppService.getPlatformApp(queryPlatformAppDto);
            if (ResultData.isSuccess(resultData) && resultData.getData() != null) {
                // 检查是否被停用
                if (2 == resultData.getData().getAppStatus()) {
                    return ResultData.newFailure("81405", "企业已被停用，请联系平台管理员。");
                }
                userInfo.setEnterpriseLicense(resultData.getData().getEnterpriseLicense());
                userInfo.setEnterpriseName(resultData.getData().getEnterpriseName());
            }
        }
        loginResponse.setUser(userInfo);
        // 登录成功后，调用权限服务，获取该登录用户的角色和权限
        Map<String, String> permissions = getPermissions(request, userInfo, loginResponse);
        // 生成Token，返回给客户端
        TokenDto tokenDto = createToken(userInfo);
        loginResponse.setToken(tokenDto);
        // 检查是否有同个账号登录过，如果已经登录就提示之前登录的账号下线
        String ipAddresss = RequestUtils.getIPAddress(request);
        checkSyncAccountLogin(ipAddresss, userInfo.getAccountId());
        // 放入缓存，后续待用
        CacheUtils.cacheLogin(tokenDto, userInfo, permissions);
        // 存入登录IP
        CacheUtils.cacheLoginIP(userInfo.getAccountId(), ipAddresss);
        // 把缓存用户APP的设备唯一号（推送的）
        if (Tools.isNotEmpty(loginRequest.getRegistrationId())) {
            CacheUtils.cacheRegistrationId(userInfo.getAccountId(), loginRequest.getRegistrationId());
            CacheUtils.cacheRegistrationId(userInfo.getIdNumber(), loginRequest.getRegistrationId());
        }
        // 登录成功
        return ResultData.newSuccess(loginResponse);
    }

    /**
     * 刷新Token
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/refresh")
    public ResultData refresh(HttpServletRequest request) throws Exception {
        String token = request.getHeader(JWTConstant.JWT_Auth_RefreshToken);
        if (Tools.isEmpty(token)) {
            return ResultData.newFailure(JWTConstant.JWT_Verify_Token_Valid, JWTConstant.JWT_Verify_Token_Valid_Message);
        }
        // 通过刷新Token，从Redis获取信息，用于验证Token的有效性
        Map<String, String> mapClaim = CacheUtils.getTokenClaim(token);
        // 检查账号在其他地方是否登录了
        if (mapClaim == null) {
            return ResultData.newFailure(JWTConstant.JWT_Verify_Token_Valid, "账号在其他地方登录");
        }
        // 先验证Refresh Token是否有效
        JWTResult jwtResult = JWTUtils.verifyToken(token, mapClaim);
        if (jwtResult.isSuccess()) {
            // 验证成功，通过刷新Token从缓存中获取用户信息
            UserDto userInfo = CacheUtils.getUserInfoByToken(token);
            TokenDto tokenDto = createToken(userInfo);
            Map<String, String> permissions = CacheUtils.getStringMap(token + CacheKey.Cache_Key_Permission_Suffix);
            // 这里需要把相关的用户信息放入新的Token下
            CacheUtils.cacheLogin(tokenDto, userInfo, permissions);
            // 刷新成功
            return ResultData.newSuccess(tokenDto);
        } else {
            return ResultData.newFailure(jwtResult.getCode(), jwtResult.getMessage());
        }
    }

    /**
     * 验证Token有效性
     *
     * @param token
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/verify")
    public ResultData verify(String token) throws Exception {
        // 通过Token从缓存获取载荷
        Map<String, String> mapClaim = CacheUtils.getTokenClaim(token);
        // 检查账号在其他地方是否登录了
        if (mapClaim == null) {
            return ResultData.newFailure(JWTConstant.JWT_Verify_Token_Valid, "账号在其他地方登录");
        }
        // 验证Token是否有效
        JWTResult jwtResult = JWTUtils.verifyToken(token, mapClaim);
        if (jwtResult.isSuccess()) {
            return ResultData.newSuccess();
        }
        return ResultData.newFailure(jwtResult.getCode(), jwtResult.getMessage());
    }

    /**
     * 验证用户是否具有某个权限点
     *
     * @param apiUrl
     * @param token
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/verifyPerm")
    public ResultData verifyPerm(String apiUrl, String token) throws Exception {
        // 解析当前Token，得到用户名
        Map<String, String> mapClaim = CacheUtils.getTokenClaim(token);
        JWTResult jwtResult = JWTUtils.verifyToken(token, mapClaim);
        if (!jwtResult.isSuccess()) {
            // Token无效
            return ResultData.newFailure(jwtResult.getCode(), jwtResult.getMessage());
        }
        // 通过用户名获取缓存中的权限
        Map<String, String> permissions = CacheUtils.getStringMap(token + CacheKey.Cache_Key_Permission_Suffix);
        if (permissions != null && !permissions.containsKey(apiUrl)) {
            // 没有包含API权限
            return ResultData.newFailure(ErrorConstant.Error_Code_01, ErrorConstant.Error_Code_01_Reason);
        }
        // 校验是否具有API权限
        return ResultData.newSuccess();
    }

    /**
     * 获取验证码
     *
     * @param validCodeDto
     * @return
     */
    @RequestMapping(value = "/getValidCode")
    public ResultData getValidCode(@RequestBody ValidCodeDto validCodeDto) {
        validatorDto(validCodeDto, "81002", GroupDefault.class);
        // 发送短信验证码
        try {
            switch (validCodeDto.getScene()) {
                case 1:
                    // 企业注册校验手机号是否已经注册
                    QueryAccountInfoDto queryAccountInfoDto = new QueryAccountInfoDto();
                    queryAccountInfoDto.setAccountName(validCodeDto.getPhone());
                    ResultData<List<AccountInfoDto>> resultData = permissionFeign.getAccountInfo(queryAccountInfoDto);
                    if (!ResultData.isSuccess(resultData)) {
                        return ResultData.newFailure(resultData.getCode(), resultData.getMsg());
                    }
                    if (resultData.getData() != null && resultData.getData().size() > 0) {
                        return ResultData.newFailure("80304", "账号已存在，请直接登录");
                    }
                    smsCommonClient.sendRegisterSms(validCodeDto.getPhone());
                    break;
                case 2:
                    smsCommonClient.sendLoginSms(validCodeDto.getPhone());
                    break;
                default :
                    return ResultData.newFailure("81003", "无效的使用场景");
            }
            return ResultData.newSuccess("", "发送成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ResultData.newFailure("81003", e.getMessage());
        }
    }

    /**
     * 校验验证码
     *
     * @param validCodeDto
     * @return
     */
    @RequestMapping(value = "/verifyValidCode")
    public ResultData verifyValidCode(@RequestBody ValidCodeDto validCodeDto) {
        validatorDto(validCodeDto, "81002", GroupDefault.class, GroupUpdate.class);
        // 发送短信验证码
        try {
            SmsUtils.verifyValidCode(validCodeDto.getPhone(), validCodeDto.getValidCode());
        } catch (Exception e) {
            e.printStackTrace();
            return ResultData.newFailure("81004", e.getMessage());
        }
        return ResultData.newSuccess();
    }

    /**
     * 获取权限信息
     *
     * @param request
     * @param userDto
     * @param loginResponse
     * @return
     */
    private Map<String, String> getPermissions(HttpServletRequest request, UserDto userDto, LoginResponse loginResponse) {
        Map<String, String> permissions = new HashMap<String, String>();
        if (!RequestUtils.isAppRequest(request)) {
            // APP的不去获取权限，直接扔回去
            QueryAccountInfoDto accountInfoDto = new QueryAccountInfoDto();
            accountInfoDto.setAccountId(userDto.getAccountId());
            accountInfoDto.setOwnerAppid(userDto.getOwnerAppid());
            ResultData resultData = permissionFeign.getAccountPermission(accountInfoDto);
            if (!ResultData.isSuccess(resultData)) {
                throw new ServiceException(resultData.getCode(), resultData.getMsg());
            }
            List<PermissionInfo> permissionList = BeanChangeUtils.toBeanList(resultData.getData(), PermissionInfo.class);
            // 处理权限
            permissions = makePermissions(permissionList, loginResponse);
        }
        return permissions;
    }

    /**
     * 用户登录
     *
     * @param loginRequest
     * @return
     */
    private UserDto userLogin(LoginRequest loginRequest) {
        // 调用用户服务，验证用户名密码是否正确
        ResultData resultData = permissionFeign.getUserLogin(toQueryUserLogin(loginRequest));
        if (!ResultData.isSuccess(resultData)) {
            throw new ServiceException(resultData.getCode(), resultData.getMsg());
        }
        UserDto userInfo = BeanChangeUtils.toBean(resultData.getData(), UserDto.class);
        return userInfo;
    }

    /**
     * 检查是否有同个账号登录
     *
     * @param ipAddresss
     * @param accountId
     */
    private void checkSyncAccountLogin(String ipAddresss, String accountId) {
        UserDto existsUser = CacheUtils.getUserInfo(accountId);
        if (existsUser != null) {
            // 获取已登录的Session
            String cacheIpAddress = CacheUtils.getLoginIP(accountId);
            if (Tools.isNotEmpty(cacheIpAddress) && !cacheIpAddress.equalsIgnoreCase(ipAddresss)) {
                LogUtils.warn("账号登录IP不同，缓存IP：" + cacheIpAddress + "，当前IP：" + ipAddresss);
                pushLoginMessage(existsUser.getOwnerAppid(), accountId, ipAddresss);
            }
        }
    }

    /**
     * 推送登录消息
     *
     * @param ownerAppid
     * @param accountId
     * @param ipAddresss
     */
    private void pushLoginMessage(String ownerAppid, String accountId, String ipAddresss) {
        PushMessage pushMessage = new PushMessage();
        List<String> list = new ArrayList<>();
        list.add(accountId);
        pushMessage.setAccountIds(list);
        pushMessage.setMessage("您的账号在别处登录（IP：" + ipAddresss + "），请检查账号安全。");
        pushMessage.setPushType(PushTypeConstant.PUSH_PC);
        pushMessage.setMessageType(MessageTypeConstant.MT_USER);
        pushMessage.setOwnerAppid(ownerAppid);
        messageFeign.sendPush(pushMessage);
    }

    /**
     * 转换为QueryUserLogin对象
     *
     * @param loginRequest
     * @return
     */
    private QueryUserLogin toQueryUserLogin(LoginRequest loginRequest) {
        QueryUserLogin queryUserLogin = new QueryUserLogin();
        queryUserLogin.setType(loginRequest.getType());
        queryUserLogin.setUsername(loginRequest.getUsername());
        queryUserLogin.setPassword(loginRequest.getPassword());
        return queryUserLogin;
    }

    /**
     * 创建访问Token和刷新Token
     *
     * @param user
     * @return
     */
    private TokenDto createToken(UserDto user) {
        // 访问Token，因子用户ID和用户名
        Map<String, String> mapClaim = new HashMap<String, String>();
        mapClaim.put(JWTConstant.JWT_Token_UserId_Key, String.valueOf(user.getUserId()));
        mapClaim.put(JWTConstant.JWT_Token_AccountId_Key, user.getAccountId());
        mapClaim.put(JWTConstant.JWT_Token_IDNumber_Key, user.getIdNumber());
        String accessToken = JWTUtils.generateAccessToken(mapClaim);
        // 把Access_Token的因子放入缓存
        CacheUtils.cacheTokenClaim(accessToken, mapClaim);
        // 增加刷新的因子
        mapClaim.put(JWTConstant.JWT_Refresh_Token_UUID, UUID.randomUUID().toString());
        String refreshToken = JWTUtils.generateRefreshToken(mapClaim);
        // 把刷新缓存的因子放入缓存
        CacheUtils.cacheTokenClaim(refreshToken, mapClaim);
        // 返回Token对象
        return TokenDto.create(accessToken, refreshToken);
    }

    /**
     * 处理权限
     *
     * @param permissionList
     * @param loginResponse
     * @return
     */
    private Map<String, String> makePermissions(List<PermissionInfo> permissionList, LoginResponse loginResponse) {
        // 存储Code权限码集合的
        List<String> codeList = new ArrayList<String>();
        // API权限对应关系的
        Map<String, String> mapPermission = new HashMap<String, String>();
        // 菜单的
        List<MenuInfo> menuList = new ArrayList<MenuInfo>();
        // 存放临时的菜单集合（一级节点的）
        Map<Long, MenuInfo> mapMenus = new HashMap<Long, MenuInfo>();
        // 循环处理权限集合
        MenuInfo menuInfo = null;
        for (PermissionInfo permissionInfo : permissionList) {
            codeList.add(permissionInfo.getResourceCode());
            if (permissionInfo.getResourceType() == 1) {
                // 菜单
                menuInfo = new MenuInfo();
                menuInfo.setTitle(permissionInfo.getResourceName());
                menuInfo.setAuthCode(permissionInfo.getResourceCode());
                menuInfo.setIcon(permissionInfo.getResourceIcon());
                menuInfo.setUrl(permissionInfo.getResourceUrl());
                menuInfo.setSort(permissionInfo.getResourceSort());
                if (permissionInfo.getResourcePid() != null && permissionInfo.getResourcePid() != 0) {
                    // 是子集菜单，直接放到上级菜单下
                    if (mapMenus.containsKey(permissionInfo.getResourcePid())) {
                        mapMenus.get(permissionInfo.getResourcePid()).getSubNavList().add(menuInfo);
                    } else {
                        MenuInfo parentMenu = new MenuInfo();
                        parentMenu.getSubNavList().add(menuInfo);
                        mapMenus.put(permissionInfo.getResourcePid(), parentMenu);
                    }
                } else {
                    if (mapMenus.containsKey(permissionInfo.getResourceId())) {
                        menuInfo.setSubNavList(mapMenus.get(permissionInfo.getResourceId()).getSubNavList());
                    }
                    mapMenus.put(permissionInfo.getResourceId(), menuInfo);
                }
            }
            // API
            String resourceApi = permissionInfo.getResourceApi();
            if (Tools.isNotEmpty(resourceApi)) {
                String[] apis = resourceApi.split(",");
                for (String api : apis) {
                    mapPermission.put(api, permissionInfo.getResourceCode());
                }
            }
        }
        // 处理好后，放入登录响应里面，并返回API对应权限放入缓存后续待用
        loginResponse.setCode(codeList);
        menuList.addAll(mapMenus.values());
        Collections.sort(menuList, new Comparator<MenuInfo>() {
            @Override
            public int compare(MenuInfo o1, MenuInfo o2) {
                if (o1.getSort() == null || o2.getSort() == null) {
                    return 1;
                }
                return o1.getSort().compareTo(o2.getSort());
            }
        });
        loginResponse.setMenuList(menuList);
        return mapPermission;
    }
}
